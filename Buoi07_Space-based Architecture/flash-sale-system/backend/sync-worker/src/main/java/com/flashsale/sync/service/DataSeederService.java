package com.flashsale.sync.service;

import com.flashsale.sync.model.*;
import com.flashsale.sync.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Seeds MongoDB Atlas with sample data on startup if collections are empty.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class DataSeederService implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FlashSaleRepository flashSaleRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final InventorySnapshotRepository inventorySnapshotRepository;

    @Override
    public void run(String... args) {
        seedUsers();
        seedProducts();
        seedFlashSales();
        seedFlashSaleItems();
        seedInventory();
        log.info("✅ Data seeding completed!");
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already seeded, skipping...");
            return;
        }
        List<User> users = Arrays.asList(
            User.builder().id("user_1").fullName("Nguyen Van A").email("a@gmail.com")
                .phone("0901111111").role("CUSTOMER").status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            User.builder().id("user_2").fullName("Tran Thi B").email("b@gmail.com")
                .phone("0902222222").role("CUSTOMER").status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            User.builder().id("user_3").fullName("Le Van C").email("c@gmail.com")
                .phone("0903333333").role("CUSTOMER").status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build()
        );
        userRepository.saveAll(users);
        log.info("✅ Seeded {} users", users.size());
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            log.info("Products already seeded, skipping...");
            return;
        }
        List<Product> products = Arrays.asList(
            Product.builder()
                .id("product_1").sku("CAM-IP-4MP-001").name("Camera IP Wifi 4MP").slug("camera-ip-wifi-4mp")
                .description("Camera an ninh độ phân giải cao, hỗ trợ quay đêm, kết nối Wifi")
                .category(Map.of("name", "Camera an ninh"))
                .brand(Map.of("name", "SecureTech"))
                .originalPrice(1200000).salePrice(799000)
                .thumbnailUrl("https://picsum.photos/seed/camera/400/400")
                .images(List.of("https://picsum.photos/seed/camera1/400/400", "https://picsum.photos/seed/camera2/400/400"))
                .attributes(Map.of("resolution", "4MP", "warranty", "12 tháng", "origin", "Việt Nam"))
                .status("ACTIVE").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            Product.builder()
                .id("product_2").sku("LOCK-FINGER-001").name("Khóa cửa vân tay thông minh").slug("khoa-cua-van-tay")
                .description("Khóa thông minh cho nhà ở, mở bằng vân tay, mã PIN, thẻ từ")
                .category(Map.of("name", "Khóa thông minh"))
                .brand(Map.of("name", "SecureHome"))
                .originalPrice(2500000).salePrice(1890000)
                .thumbnailUrl("https://picsum.photos/seed/lock/400/400")
                .images(List.of("https://picsum.photos/seed/lock1/400/400"))
                .attributes(Map.of("type", "Vân tay + PIN", "warranty", "24 tháng", "origin", "Hàn Quốc"))
                .status("ACTIVE").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            Product.builder()
                .id("product_3").sku("HEADPHONE-SONY-XM5").name("Tai nghe Bluetooth Sony WH-1000XM5").slug("tai-nghe-sony-xm5")
                .description("Tai nghe chống ồn cao cấp, âm thanh Hi-Res, pin 30 giờ")
                .category(Map.of("name", "Tai nghe"))
                .brand(Map.of("name", "Sony"))
                .originalPrice(8500000).salePrice(4990000)
                .thumbnailUrl("https://picsum.photos/seed/headphone/400/400")
                .images(List.of("https://picsum.photos/seed/headphone1/400/400"))
                .attributes(Map.of("type", "Over-ear Bluetooth", "warranty", "12 tháng", "origin", "Nhật Bản"))
                .status("ACTIVE").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            Product.builder()
                .id("product_4").sku("KB-LOGI-PROX").name("Bàn phím cơ Logitech G Pro X").slug("ban-phim-logitech-g-pro-x")
                .description("Switch GX hot-swappable, đèn RGB LIGHTSYNC, thiết kế compact TKL")
                .category(Map.of("name", "Bàn phím"))
                .brand(Map.of("name", "Logitech"))
                .originalPrice(2800000).salePrice(1490000)
                .thumbnailUrl("https://picsum.photos/seed/keyboard/400/400")
                .images(List.of("https://picsum.photos/seed/keyboard1/400/400"))
                .attributes(Map.of("type", "Mechanical TKL", "warranty", "24 tháng", "origin", "Trung Quốc"))
                .status("ACTIVE").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            Product.builder()
                .id("product_5").sku("MOUSE-RAZER-DAV3").name("Chuột gaming Razer DeathAdder V3").slug("chuot-razer-deathadder-v3")
                .description("Cảm biến 30K DPI, siêu nhẹ 59g, kết nối HyperSpeed wireless")
                .category(Map.of("name", "Chuột"))
                .brand(Map.of("name", "Razer"))
                .originalPrice(1990000).salePrice(990000)
                .thumbnailUrl("https://picsum.photos/seed/mouse/400/400")
                .images(List.of("https://picsum.photos/seed/mouse1/400/400"))
                .attributes(Map.of("dpi", "30000", "warranty", "24 tháng", "origin", "Trung Quốc"))
                .status("ACTIVE").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build()
        );
        productRepository.saveAll(products);
        log.info("✅ Seeded {} products", products.size());
    }

    private void seedFlashSales() {
        if (flashSaleRepository.count() > 0) {
            log.info("Flash sales already seeded, skipping...");
            return;
        }
        FlashSale flashSale = FlashSale.builder()
            .id("flash_sale_1")
            .name("Flash Sale 12.12")
            .description("Chương trình bán hàng sốc cho thiết bị công nghệ và an ninh")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(16))
            .status("ACTIVE")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        flashSaleRepository.save(flashSale);
        log.info("✅ Seeded flash sale event");
    }

    private void seedFlashSaleItems() {
        if (flashSaleItemRepository.count() > 0) {
            log.info("Flash sale items already seeded, skipping...");
            return;
        }
        List<FlashSaleItem> items = Arrays.asList(
            FlashSaleItem.builder().id("fsi_1").flashSaleId("flash_sale_1").productId("product_1")
                .productSnapshot(Map.of("name", "Camera IP Wifi 4MP", "sku", "CAM-IP-4MP-001", "thumbnailUrl", "https://picsum.photos/seed/camera/400/400"))
                .flashPrice(799000).saleStock(100).soldCount(0).limitPerUser(2).status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            FlashSaleItem.builder().id("fsi_2").flashSaleId("flash_sale_1").productId("product_2")
                .productSnapshot(Map.of("name", "Khóa cửa vân tay thông minh", "sku", "LOCK-FINGER-001", "thumbnailUrl", "https://picsum.photos/seed/lock/400/400"))
                .flashPrice(1890000).saleStock(60).soldCount(0).limitPerUser(1).status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            FlashSaleItem.builder().id("fsi_3").flashSaleId("flash_sale_1").productId("product_3")
                .productSnapshot(Map.of("name", "Tai nghe Bluetooth Sony WH-1000XM5", "sku", "HEADPHONE-SONY-XM5", "thumbnailUrl", "https://picsum.photos/seed/headphone/400/400"))
                .flashPrice(4990000).saleStock(50).soldCount(0).limitPerUser(1).status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            FlashSaleItem.builder().id("fsi_4").flashSaleId("flash_sale_1").productId("product_4")
                .productSnapshot(Map.of("name", "Bàn phím cơ Logitech G Pro X", "sku", "KB-LOGI-PROX", "thumbnailUrl", "https://picsum.photos/seed/keyboard/400/400"))
                .flashPrice(1490000).saleStock(120).soldCount(0).limitPerUser(3).status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            FlashSaleItem.builder().id("fsi_5").flashSaleId("flash_sale_1").productId("product_5")
                .productSnapshot(Map.of("name", "Chuột gaming Razer DeathAdder V3", "sku", "MOUSE-RAZER-DAV3", "thumbnailUrl", "https://picsum.photos/seed/mouse/400/400"))
                .flashPrice(990000).saleStock(200).soldCount(0).limitPerUser(2).status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build()
        );
        flashSaleItemRepository.saveAll(items);
        log.info("✅ Seeded {} flash sale items", items.size());
    }

    private void seedInventory() {
        if (inventorySnapshotRepository.count() > 0) {
            log.info("Inventory already seeded, skipping...");
            return;
        }
        List<InventorySnapshot> snapshots = Arrays.asList(
            InventorySnapshot.builder().id("inv_1").productId("product_1").flashSaleId("flash_sale_1").totalStock(100).availableStock(100).reservedStock(0).soldStock(0).lastSyncAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            InventorySnapshot.builder().id("inv_2").productId("product_2").flashSaleId("flash_sale_1").totalStock(60).availableStock(60).reservedStock(0).soldStock(0).lastSyncAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            InventorySnapshot.builder().id("inv_3").productId("product_3").flashSaleId("flash_sale_1").totalStock(50).availableStock(50).reservedStock(0).soldStock(0).lastSyncAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            InventorySnapshot.builder().id("inv_4").productId("product_4").flashSaleId("flash_sale_1").totalStock(120).availableStock(120).reservedStock(0).soldStock(0).lastSyncAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
            InventorySnapshot.builder().id("inv_5").productId("product_5").flashSaleId("flash_sale_1").totalStock(200).availableStock(200).reservedStock(0).soldStock(0).lastSyncAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build()
        );
        inventorySnapshotRepository.saveAll(snapshots);
        log.info("✅ Seeded {} inventory snapshots", snapshots.size());
    }
}
