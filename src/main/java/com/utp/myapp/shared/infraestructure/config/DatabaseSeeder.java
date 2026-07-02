package com.utp.myapp.shared.infraestructure.config;

import com.utp.myapp.auth.domain.model.aggregates.User;
import com.utp.myapp.auth.domain.model.repository.IUserRepository;
import com.utp.myapp.auth.domain.model.valueobjects.Role;
import com.utp.myapp.catalog.domain.model.aggregates.PartCategory;
import com.utp.myapp.catalog.domain.model.repository.IPartCategoryRepository;
import com.utp.myapp.reminder.domain.model.aggregates.Reminder;
import com.utp.myapp.reminder.domain.model.repository.IReminderRepository;
import com.utp.myapp.reminder.domain.model.valueobjects.NotificationChannel;
import com.utp.myapp.reminder.domain.model.valueobjects.ReminderType;
import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.sales.domain.model.valueobjets.Address;
import com.utp.myapp.shared.domain.model.valueobjects.Email;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import com.utp.myapp.vehicle.domain.model.aggregates.Vehicle;
import com.utp.myapp.vehicle.domain.model.repository.IVehicleRepository;
import com.utp.myapp.vehicle.domain.model.valueobjects.FuelType;
import com.utp.myapp.vehicle.domain.model.valueobjects.LicensePlate;
import com.utp.myapp.vehicle.domain.model.valueobjects.Mileage;
import com.utp.myapp.workorder.domain.model.aggregates.WorkOrder;
import com.utp.myapp.workorder.domain.model.entities.WorkOrderItem;
import com.utp.myapp.workorder.domain.model.repository.IWorkOrderRepository;
import com.utp.myapp.workorder.domain.model.valueobjects.Priority;
import com.utp.myapp.workorder.domain.model.valueobjects.RepairAction;
import com.utp.myapp.workorder.domain.model.valueobjects.WorkOrderStatus;
import com.utp.myapp.shared.domain.model.valueobjects.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Seeds the database with sample data for development and testing.
 * Only runs when the 'dev' or 'mysql' profile is active and the database is empty.
 */
@Component
@Profile({"dev", "mysql"})
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private static final String TEST_TENANT_ID = "test-tenant-001";
    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_PASSWORD = "password";

    private final IUserRepository userRepository;
    private final ITenantRepository tenantRepository;
    private final ICustomerRepository customerRepository;
    private final IVehicleRepository vehicleRepository;
    private final IWorkOrderRepository workOrderRepository;
    private final IReminderRepository reminderRepository;
    private final IPartCategoryRepository partCategoryRepository;

    public DatabaseSeeder(IUserRepository userRepository, ITenantRepository tenantRepository,
                          ICustomerRepository customerRepository, IVehicleRepository vehicleRepository,
                          IWorkOrderRepository workOrderRepository, IReminderRepository reminderRepository,
                          IPartCategoryRepository partCategoryRepository) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.workOrderRepository = workOrderRepository;
        this.reminderRepository = reminderRepository;
        this.partCategoryRepository = partCategoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail(Email.of(TEST_EMAIL))) {
            log.info("Test data already exists — skipping seed.");
            return;
        }

        log.info("Seeding test data...");

        TenantId tid = TenantId.of(TEST_TENANT_ID);
        Tenant tenant = Tenant.create(tid, "AutoTaller Demo", "+51999888777", SubscriptionPlan.FREE);
        tenantRepository.save(tenant);

        User user = User.register(Email.of(TEST_EMAIL), TEST_PASSWORD, Role.OWNER, tid);
        userRepository.save(user);
        log.info("Created test user: {} / {}", TEST_EMAIL, TEST_PASSWORD);

        // Customers
        Customer c1 = saveCustomer("Carlos", "García", "12345678", "carlos@email.com", "+51999111222", "Cliente frecuente");
        Customer c2 = saveCustomer("María", "López", "87654321", "maria@email.com", "+51999222333", "Vehículo Toyota");
        Customer c3 = saveCustomer("Juan", "Pérez", "11223344", "juan@email.com", "+51999333444", null);

        // Vehicles
        Vehicle v1 = saveVehicle(c1.getId(), "Toyota", "Corolla", 2020, "ABC-123", "Blanco", FuelType.GASOLINE, 45000);
        Vehicle v2 = saveVehicle(c1.getId(), "Honda", "Civic", 2021, "XYZ-789", "Negro", FuelType.GASOLINE, 32000);
        Vehicle v3 = saveVehicle(c2.getId(), "Toyota", "Hilux", 2019, "DEF-456", "Rojo", FuelType.DIESEL, 78000);

        // Work Orders (with costs)
        WorkOrder wo1 = saveWorkOrder(v1.getId(), c1.getId(), WorkOrderStatus.IN_PROGRESS, "Cambio de frenos delanteros", Priority.HIGH, 350.00);
        WorkOrder wo2 = saveWorkOrder(v2.getId(), c1.getId(), WorkOrderStatus.RECEIVED, "Revisión general + cambio de aceite", Priority.NORMAL, 180.00);
        WorkOrder wo3 = saveWorkOrder(v3.getId(), c2.getId(), WorkOrderStatus.COMPLETED, "Reparación de suspensión trasera", Priority.NORMAL, 520.00);

        // Reminders
        saveReminder(c1.getId(), v1.getId(), ReminderType.MAINTENANCE_DUE, "Mantenimiento programado Corolla",
                LocalDateTime.now().plusDays(7), NotificationChannel.WHATSAPP);
        saveReminder(c2.getId(), v3.getId(), ReminderType.OIL_CHANGE, "Cambio de aceite Hilux",
                LocalDateTime.now().plusDays(14), NotificationChannel.EMAIL);

        // Part Catalog
        savePartCategories();

        log.info("Seed complete: 1 tenant, 1 user, 3 customers, 3 vehicles, 3 work orders, 2 reminders, part catalog");
    }

    private Customer saveCustomer(String name, String lastName, String dni, String email, String phone, String notes) {
        Customer c = new Customer.Builder()
                .name(name).lastName(lastName).dni(dni).email(email).phone(phone)
                .notes(notes).tenantId(TEST_TENANT_ID)
                .address(new Address("Av. Principal", "123", "Lima", "Perú"))
                .build();
        return customerRepository.insert(c);
    }

    private Vehicle saveVehicle(Integer customerId, String make, String model, int year,
                                 String plate, String color, FuelType fuelType, int mileage) {
        Vehicle v = new Vehicle.Builder()
                .customerId(customerId).make(make).model(model).year(year)
                .plate(LicensePlate.of(plate)).color(color).fuelType(fuelType)
                .mileage(Mileage.ofKm(mileage)).tenantId(TEST_TENANT_ID)
                .build();
        return vehicleRepository.save(v);
    }

    private WorkOrder saveWorkOrder(Long vehicleId, Integer customerId, WorkOrderStatus status,
                                     String description, Priority priority, double estimatedCost) {
        WorkOrder wo = new WorkOrder.Builder()
                .vehicleId(vehicleId).customerId(customerId).tenantId(TEST_TENANT_ID)
                .status(status).description(description).priority(priority)
                .startDate(LocalDateTime.now())
                .estimatedCost(Money.of(estimatedCost))
                .build();
        return workOrderRepository.save(wo);
    }

    private void saveReminder(Integer customerId, Long vehicleId, ReminderType type,
                               String title, LocalDateTime scheduledDate, NotificationChannel channel) {
        Reminder r = new Reminder.Builder()
                .customerId(customerId).vehicleId(vehicleId).tenantId(TEST_TENANT_ID)
                .type(type).title(title).message(title).scheduledDate(scheduledDate).channel(channel)
                .build();
        reminderRepository.save(r);
    }

    private void savePartCategories() {
        PartCategory motor = savePartCat(null, "Motor", "Componentes del motor");
        savePartCat(motor.getId(), "Bujías", "Bujías de encendido");
        savePartCat(motor.getId(), "Filtro de aceite", "Filtro de aceite del motor");
        savePartCat(motor.getId(), "Correa de distribución", "Correa de distribución / timing belt");

        PartCategory frenos = savePartCat(null, "Frenos", "Sistema de frenos");
        savePartCat(frenos.getId(), "Pastillas de freno", "Pastillas delanteras y traseras");
        savePartCat(frenos.getId(), "Discos de freno", "Discos de freno ventilados");

        PartCategory suspension = savePartCat(null, "Suspensión", "Sistema de suspensión");
        savePartCat(suspension.getId(), "Amortiguadores", "Amortiguadores delanteros/traseros");

        PartCategory electrico = savePartCat(null, "Eléctrico", "Sistema eléctrico");
        savePartCat(electrico.getId(), "Batería", "Batería 12V");
        savePartCat(electrico.getId(), "Alternador", "Alternador del vehículo");
    }

    private PartCategory savePartCat(Long parentId, String name, String description) {
        PartCategory pc = new PartCategory.Builder()
                .name(name).description(description).parentCategoryId(parentId)
                .tenantId(TEST_TENANT_ID)
                .build();
        return partCategoryRepository.save(pc);
    }
}
