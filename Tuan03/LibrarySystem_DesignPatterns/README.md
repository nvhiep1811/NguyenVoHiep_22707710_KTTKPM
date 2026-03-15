# Hệ thống Quản lý Thư Viện – Design Patterns Java

## Cấu trúc package

```
src/
├── Main.java                          ← Điểm chạy chương trình
├── model/
│   ├── Book.java                      ← Abstract base class
│   ├── PaperBook.java                 ← Sách giấy
│   ├── EBook.java                     ← Sách điện tử
│   └── AudioBook.java                 ← Sách nói
├── factory/
│   └── BookFactory.java               ← [FACTORY] Tạo các loại sách
├── observer/
│   ├── LibrarySubject.java            ← [OBSERVER] Interface Subject
│   ├── LibraryObserver.java           ← [OBSERVER] Interface Observer
│   ├── LibraryStaff.java              ← Nhân viên thư viện
│   └── RegisteredUser.java            ← Người dùng đăng ký
├── strategy/
│   ├── SearchStrategy.java            ← [STRATEGY] Interface
│   ├── SearchByTitle.java             ← Tìm theo tên
│   ├── SearchByAuthor.java            ← Tìm theo tác giả
│   └── SearchByGenre.java             ← Tìm theo thể loại
├── decorator/
│   ├── BorrowingService.java          ← [DECORATOR] Interface gốc
│   ├── BasicBorrowingService.java     ← Mượn cơ bản 14 ngày
│   ├── BorrowingDecorator.java        ← Abstract + 3 concrete decorators
│   └── BorrowingServiceBuilder.java   ← Builder helper
└── library/
    └── Library.java                   ← [SINGLETON] Core class
```


```

## Design Patterns sử dụng

| Pattern    | Class chính         | Mô tả |
|------------|---------------------|-------|
| Singleton  | `Library`           | Chỉ 1 instance thư viện trong hệ thống |
| Factory    | `BookFactory`       | Tạo PaperBook / EBook / AudioBook |
| Strategy   | `SearchBy*`         | Đổi chiến lược tìm kiếm lúc runtime |
| Observer   | `LibraryStaff/User` | Thông báo sự kiện tự động |
| Decorator  | `*BorrowingDecorator`| Thêm tính năng mượn không sửa class gốc |
