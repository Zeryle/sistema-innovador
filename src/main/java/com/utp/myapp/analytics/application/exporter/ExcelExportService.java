package com.utp.myapp.analytics.application.exporter;

import com.utp.myapp.analytics.application.analytics.AdvancedAnalyticsService;
import com.utp.myapp.shared.infraestructure.config.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Exports tenant data to .xlsx (Excel) files using Apache POI.
 *
 * The "Comprehensive" report bundles four sheets:
 *   1) Summary        : this month vs last month, KPIs + deltas
 *   2) Monthly trend  : 6-month series of orders and revenue
 *   3) Work orders    : every work order, with customer/vehicle info
 *   4) Top customers  : top 10 customers by spend
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcelExportService {

    @PersistenceContext
    private EntityManager em;

    private final AdvancedAnalyticsService analyticsService;

    public byte[] exportComprehensiveReport() {
        String tenantId = TenantContext.getTenantId();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            buildSummarySheet(wb, tenantId);
            buildMonthlyTrendSheet(wb, tenantId);
            buildWorkOrdersSheet(wb, tenantId);
            buildTopCustomersSheet(wb, tenantId);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Excel report", e);
        }
    }

    private void buildSummarySheet(Workbook wb, String tenantId) {
        Sheet sheet = wb.createSheet("Resumen");
        Map<String, Object> comparison = analyticsService.getPeriodComparison(tenantId);
        @SuppressWarnings("unchecked")
        Map<String, Object> thisMonth = (Map<String, Object>) comparison.get("thisMonth");
        @SuppressWarnings("unchecked")
        Map<String, Object> lastMonth = (Map<String, Object>) comparison.get("lastMonth");

        CellStyle titleStyle = wb.createCellStyle();
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("Reporte de Negocio");
        titleRow.getCell(0).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(2);
        String[] headers = {"Metrica", "Este mes", "Mes pasado", "Variacion"};
        for (int i = 0; i < headers.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }

        int row = 3;
        row = addSummaryRow(sheet, row, "Ordenes",
                ((Number) thisMonth.get("orders")).intValue(),
                ((Number) lastMonth.get("orders")).intValue(),
                ((Number) comparison.get("ordersDelta")).intValue());
        row = addSummaryRow(sheet, row, "Ingresos (S/)",
                ((Number) thisMonth.get("revenue")).doubleValue(),
                ((Number) lastMonth.get("revenue")).doubleValue(),
                ((Number) comparison.get("revenueDelta")).intValue());
        row = addSummaryRow(sheet, row, "Nuevos clientes",
                ((Number) thisMonth.get("newCustomers")).intValue(),
                ((Number) lastMonth.get("newCustomers")).intValue(),
                ((Number) comparison.get("customersDelta")).intValue());

        for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);
    }

    private int addSummaryRow(Sheet sheet, int row, String metric, double current, double previous, int deltaPct) {
        Row r = sheet.createRow(row);
        r.createCell(0).setCellValue(metric);
        r.createCell(1).setCellValue(current);
        r.createCell(2).setCellValue(previous);
        String sign = deltaPct > 0 ? "+" : "";
        r.createCell(3).setCellValue(sign + deltaPct + "%");
        return row + 1;
    }

    private void buildMonthlyTrendSheet(Workbook wb, String tenantId) {
        Sheet sheet = wb.createSheet("Tendencia Mensual");
        Map<String, Object> trend = analyticsService.getMonthlyTrend(tenantId, 6);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> series = (List<Map<String, Object>>) trend.get("series");

        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        String[] h = {"Mes", "Ordenes", "Ingresos (S/)"};
        for (int i = 0; i < h.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(h[i]);
            c.setCellStyle(headerStyle);
        }

        int row = 1;
        for (Map<String, Object> point : series) {
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue((String) point.get("month"));
            r.createCell(1).setCellValue(((Number) point.get("orders")).doubleValue());
            r.createCell(2).setCellValue(((Number) point.get("revenue")).doubleValue());
        }
        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
    }

    private void buildWorkOrdersSheet(Workbook wb, String tenantId) {
        Sheet sheet = wb.createSheet("Ordenes de Trabajo");

        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        String[] h = {"#", "Estado", "Cliente", "Vehiculo (placa)", "Descripcion", "Costo est. (S/)", "Fecha creacion"};
        for (int i = 0; i < h.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(h[i]);
            c.setCellStyle(headerStyle);
        }

        try {
            Query q = em.createNativeQuery(
                    "SELECT wo.id, wo.status, (c.name || ' ' || c.last_name), v.plate, wo.description, wo.estimated_cost, wo.created_at " +
                    "FROM work_order wo LEFT JOIN customer c ON c.id = wo.customer_id " +
                    "LEFT JOIN vehicle v ON v.id = wo.vehicle_id " +
                    "WHERE wo.tenant_id = :tid ORDER BY wo.created_at DESC");
            q.setParameter("tid", tenantId);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            int row = 1;
            for (Object[] r : rows) {
                Row xr = sheet.createRow(row++);
                xr.createCell(0).setCellValue(((Number) r[0]).doubleValue());
                xr.createCell(1).setCellValue((String) r[1]);
                xr.createCell(2).setCellValue((String) r[2]);
                xr.createCell(3).setCellValue((String) r[3]);
                xr.createCell(4).setCellValue((String) r[4]);
                xr.createCell(5).setCellValue(r[5] == null ? 0.0 : ((Number) r[5]).doubleValue());
                xr.createCell(6).setCellValue(r[6] == null ? "" : r[6].toString());
            }
        } catch (Exception e) {
            // empty sheet
        }
        for (int i = 0; i < h.length; i++) sheet.autoSizeColumn(i);
    }

    private void buildTopCustomersSheet(Workbook wb, String tenantId) {
        Sheet sheet = wb.createSheet("Top Clientes");
        List<Map<String, Object>> top = analyticsService.getTopCustomers(tenantId, 10);

        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        String[] h = {"#", "Cliente", "Ordenes totales", "Gasto total (S/)"};
        for (int i = 0; i < h.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(h[i]);
            c.setCellStyle(headerStyle);
        }

        int row = 1;
        int rank = 1;
        for (Map<String, Object> t : top) {
            Row xr = sheet.createRow(row++);
            xr.createCell(0).setCellValue(rank++);
            xr.createCell(1).setCellValue((String) t.get("fullName"));
            xr.createCell(2).setCellValue(((Number) t.get("totalOrders")).doubleValue());
            xr.createCell(3).setCellValue(((Number) t.get("totalSpend")).doubleValue());
        }
        for (int i = 0; i < h.length; i++) sheet.autoSizeColumn(i);
    }
}
