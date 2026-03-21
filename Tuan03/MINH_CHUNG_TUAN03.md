# MINH CHUNG BAI TAP TUAN 03

## 1) Thong tin chung

- Ho va ten: Nguyen Vo Hiep
- MSSV: 22707710
- Mon hoc: KTTKPM
- Tuan: 03
- Ngay cap nhat: 21/03/2026

---

## 2) FileSystem_CompositePattern

### UML

![UML FileSystem Composite Pattern](evidence/FileSystem_CompositePattern/uml-class.png)

### Ket qua chay chuong trinh

![Run Main](evidence/FileSystem_CompositePattern/run-main.png)

---

## 3) JsonXmlConverter

### UML

![UML JsonXmlConverter](evidence/JsonXmlConverter/uml-class.png)

### Ket qua chuyen doi

![JSON to XML](evidence/JsonXmlConverter/run-convert-json-to-xml.png)

![XML to JSON](evidence/JsonXmlConverter/run-convert-xml-to-json.png)

![Invalid XML](evidence/JsonXmlConverter/invalid-xml.png)

---

## 4) LibrarySystem_DesignPatterns

### Ket qua chay chuong trinh

![Library Main 1](evidence/LibrarySystem_DesignPatterns/run-main-1.png)

![Library Main 2](evidence/LibrarySystem_DesignPatterns/run-main-2.png)

![Library Main 3](evidence/LibrarySystem_DesignPatterns/run-main-3.png)

---

## 5) ObserverPattern

### UML

![Observer Core UML](evidence/ObserverPattern/observer_pattern_core_uml.png)

![Observer Two Scenarios UML](evidence/ObserverPattern/observer_two_scenarios_uml.png)

### Ket qua chay chuong trinh

![Run StockMain](evidence/ObserverPattern/run-stock-main.png)

![Run TaskMain](evidence/ObserverPattern/run-task-main.png)

---

## 6) my-cms (Frontend/Backend)

### Mo ta kien truc Microkernel

Du an `my-cms/backend` duoc to chuc theo huong **Microkernel Architecture**:

- **Core (Microkernel):**
  - `src/kernel.js` dong vai tro nhan trung tam, khoi tao he thong va dieu phoi cac thanh phan.
  - `src/server.js` boot ung dung, nap kernel va khoi dong HTTP server.
- **Internal Services (Core Services):**
  - Cac module trong `src/core/` (vi du: `authService.js`) cung cap nghiep vu cot loi, duoc kernel quan ly.
- **Plug-in/Extension Layer:**
  - `src/plugins/` chua cac plugin mo rong tinh nang CMS.
  - `src/extension/` chua cac extension bo sung hanh vi ma khong can sua code kernel.
- **Diem mo rong (Extension Points):**
  - `src/api/registerRoutes.js` la diem dang ky route/module de plugin co the "cam" vao he thong.
  - Cac controller trong `src/controllers/` xu ly request theo luong da duoc kernel + plugin dang ky.

=> Nhu vay, phan cot loi (kernel + core services) duoc giu gon, on dinh; tinh nang moi duoc them qua plugin/extension, dung tinh chat "mo rong ma khong sua nhan" cua Microkernel.

### Giao dien

![UI Dashboard](evidence/my-cms/ui-dashboard.png)

![UI Content List](evidence/my-cms/ui-content-list.png)

![UI Create Content](evidence/my-cms/ui-create-content.png)

![UI Plugin](evidence/my-cms/ui-plugin.png)

![UI Role](evidence/my-cms/ui-role.png)

![UI User](evidence/my-cms/ui-user.png)
