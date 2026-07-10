package com.utp.myapp.analytics.interfaces.rest;

import com.utp.myapp.analytics.application.exporter.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Premium-tier endpoint: download a comprehensive .xlsx report of the
 * tenant's business activity (summary, monthly trend, all work orders,
 * top customers).
 */
@RestController
@RequestMapping("/api/analytics/export")
@RequiredArgsConstructor
public class ExcelExportController {

    private final ExcelExportService exportService;

    @GetMapping("/xlsx")
    public ResponseEntity<byte[]> downloadXlsx() {
        byte[] bytes = exportService.exportComprehensiveReport();
        String filename = "reporte-autotaller-" + LocalDate.now() + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(bytes.length);
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
