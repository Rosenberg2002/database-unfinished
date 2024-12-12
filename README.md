# Library Management System

Hệ thống quản lý thư viện được xây dựng bằng Java Web với JSP/Servlet.

## Yêu cầu hệ thống

1. JDK (Java Development Kit):
   - Phiên bản: JDK 17
   - [Tải JDK tại đây](https://www.oracle.com/java/technologies/downloads/)
   - Cài đặt và cấu hình biến môi trường JAVA_HOME

2. Apache TomEE:
   - Phiên bản: Apache TomEE Microprofile 10.0.0-M3
   - [Tải TomEE tại đây](https://tomee.apache.org/download.html)
   - Giải nén vào thư mục không có dấu cách trong đường dẫn

3. SQL Server:
   - Phiên bản: SQL Server 2019 trở lên (Developer Edition)
   - [Tải SQL Server tại đây](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
   - Chọn "Download for free" ở phiên bản Developer
   - Trong quá trình cài đặt:
     + Chọn "Custom" installation
     + Chọn instance name (mặc định là MSSQLSERVER)
     + Chọn "Mixed Mode Authentication"
     + Đặt mật khẩu cho tài khoản sa (ghi nhớ mật khẩu này)

4. SQL Server Management Studio (SSMS):
   - [Tải SSMS tại đây](https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms)
   - Cài đặt với các tùy chọn mặc định
   - Sau khi cài đặt:
     + Mở SSMS
     + Server type: Database Engine
     + Server name: localhost hoặc tên instance (VD: DESKTOP-ABC\\SQLEXPRESS)
     + Authentication: SQL Server Authentication
     + Login: sa
     + Password: mật khẩu đã đặt cho sa

5. IDE (khuyến nghị):
   - IntelliJ IDEA Ultimate (có thể dùng bản Community)
   - [Tải IntelliJ IDEA tại đây](https://www.jetbrains.com/idea/download/)
   - Hoặc Eclipse/NetBeans

## Cấu trúc Database

### 1. Bảng BOOK (Quản lý sách)
```sql
CREATE TABLE BOOK (
    book_id VARCHAR(10) PRIMARY KEY,  -- Khóa chính, không được trùng
    title NVARCHAR(200) NOT NULL,     -- Tên sách, bắt buộc
    author NVARCHAR(100) NOT NULL,    -- Tác giả, bắt buộc
    category NVARCHAR(50) NOT NULL,   -- Thể loại, bắt buộc
    quantity INT NOT NULL CHECK (quantity >= 0),  -- Số lượng, phải >= 0
    status NVARCHAR(20) NOT NULL,     -- Trạng thái sách
    CONSTRAINT CHK_BookID CHECK (book_id LIKE 'EMP[0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_Status CHECK (status IN ('Available', 'Borrowed', 'Maintenance'))
);
```
- `book_id`: Định dạng EMPxxxxx (EMP + 5 số)
- `status`: Chỉ nhận 3 giá trị: Available (có sẵn), Borrowed (đã mượn), Maintenance (bảo trì)

### 2. Bảng MEMBER (Quản lý thành viên)
```sql
CREATE TABLE MEMBER (
    member_id VARCHAR(10) PRIMARY KEY,  -- Khóa chính
    name NVARCHAR(100) NOT NULL,        -- Tên thành viên
    email VARCHAR(100) NOT NULL UNIQUE,  -- Email, không được trùng
    phone VARCHAR(15) NOT NULL,          -- Số điện thoại
    address NVARCHAR(200) NOT NULL,      -- Địa chỉ
    CONSTRAINT CHK_MemberID CHECK (member_id LIKE 'MEM[0-9][0-9][0-9][0-9][0-9]')
);
```
- `member_id`: Định dạng MEMxxxxx (MEM + 5 số)
- `email`: Phải là duy nhất trong hệ thống

### 3. Bảng BORROW (Quản lý mượn trả)
```sql
CREATE TABLE BORROW (
    borrow_id VARCHAR(10) PRIMARY KEY,
    member_id VARCHAR(10) NOT NULL,
    book_id VARCHAR(10) NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    CONSTRAINT FK_Borrow_Member FOREIGN KEY (member_id) REFERENCES MEMBER(member_id),
    CONSTRAINT FK_Borrow_Book FOREIGN KEY (book_id) REFERENCES BOOK(book_id),
    CONSTRAINT CHK_BorrowID CHECK (borrow_id LIKE 'BOR[0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_BorrowDates CHECK (
        borrow_date <= due_date
        AND (return_date IS NULL OR return_date >= borrow_date)
        AND DATEDIFF(day, borrow_date, due_date) BETWEEN 0 AND 20
    )
);
```
- `borrow_id`: Định dạng BORxxxxx (BOR + 5 số)
- `member_id`: Khóa ngoại liên kết với bảng MEMBER
- `book_id`: Khóa ngoại liên kết với bảng BOOK
- `return_date`: Có thể NULL (khi sách chưa được trả)
- Ràng buộc thời gian:
  + Ngày mượn phải <= ngày phải trả
  + Ngày trả thực tế phải >= ngày mượn
  + Thời gian mượn tối đa 20 ngày

### Giải thích các kiểu dữ liệu
- `VARCHAR`: Chuỗi ký tự ASCII, độ dài thay đổi
- `NVARCHAR`: Chuỗi Unicode (hỗ trợ tiếng Việt), độ dài thay đổi
- `DATE`: Kiểu ngày tháng
- `INT`: Số nguyên

### Giải thích các ràng buộc
- `PRIMARY KEY`: Khóa chính, không được trùng và không được NULL
- `FOREIGN KEY`: Khóa ngoại, liên kết với khóa chính của bảng khác
- `NOT NULL`: Bắt buộc phải có giá trị
- `UNIQUE`: Giá trị không được trùng lặp
- `CHECK`: Ràng buộc điều kiện (VD: quantity >= 0)

### Mối quan hệ giữa các bảng
- Một MEMBER có thể mượn nhiều BOOK (quan hệ 1-n)
- Một BOOK có thể được mượn bởi nhiều MEMBER (thông qua bảng BORROW)
- Bảng BORROW là bảng trung gian lưu thông tin mượn trả

## Cài đặt

### 1. Cài đặt và cấu hình SQL Server

1. Sau khi cài SQL Server, mở SSMS và kết nối với tài khoản sa

2. Kích hoạt tài khoản sa (nếu chưa kích hoạt):
   ```sql
   -- Kích hoạt tài khoản sa
   ALTER LOGIN sa ENABLE;
   GO
   
   -- Đặt lại mật khẩu cho sa
   ALTER LOGIN sa WITH PASSWORD = '12345678';
   GO
   ```

3. Tạo database và bảng:
   ```sql
   -- Tạo database
   CREATE DATABASE Library_Manager;
   GO
   
   USE Library_Manager;
   GO
   
   -- Tạo bảng USER
   CREATE TABLE [USER] (
       username VARCHAR(50) PRIMARY KEY,
       password VARCHAR(50) NOT NULL,
       role BIT NOT NULL,
       name NVARCHAR(100),
       avt VARCHAR(200),
       sex BIT,
       datebirth VARCHAR(20),
       phone VARCHAR(20),
       gmail VARCHAR(100)
   );
   GO
   
   -- Thêm tài khoản admin mặc định
   INSERT INTO [USER] (username, password, role, name, avt)
   VALUES ('admin', 'admin', 1, 'Administrator', 'img/avt/avt.jpg');
   GO
   ```

### 2. Cấu hình TomEE

1. Tạo tài khoản admin cho TomEE Manager:
   - Mở file `conf/tomcat-users.xml` trong thư mục TomEE
   - Thêm đoạn sau vào trong thẻ `<tomcat-users>`:
   ```xml
   <role rolename="manager-gui"/>
   <role rolename="admin-gui"/>
   <user username="admin" password="admin" roles="manager-gui,admin-gui"/>
   ```

2. Cấu hình port (nếu cần):
   - Mặc định server chạy trên port 8080
   - Nếu port 8080 đã được sử dụng, mở file `conf/server.xml`
   - Tìm và thay đổi port trong thẻ `<Connector port="8080" ...>`

### 3. Cấu hình Database Connection

1. Kiểm tra file `src/java/dbConnect/DBContext.java`:
   ```java
   private final String serverName = "localhost";
   private final String dbName = "Library_Manager";
   private final String portNumber = "1433";
   private final String userID = "sa";
   private final String password = "12345678"; // Thay đổi theo mật khẩu sa của bạn
   ```

2. Nếu sử dụng instance cụ thể:
   - Thay `localhost` bằng `localhost\\INSTANCE_NAME`
   - Ví dụ: `localhost\\SQLEXPRESS`

### 4. Cấu hình IDE (IntelliJ IDEA)

1. Mở project:
   - File -> Open -> Chọn thư mục project

2. Cấu hình TomEE:
   - File -> Settings -> Build, Execution, Deployment -> Application Servers
   - Click + -> TomEE Server
   - Chọn thư mục TomEE đã giải nén

3. Cấu hình Artifacts:
   - File -> Project Structure -> Artifacts
   - Click + -> Web Application: Exploded -> From Modules
   - Chọn module của project

4. Cấu hình Run Configuration:
   - Run -> Edit Configurations
   - Click + -> TomEE Server -> Local
   - Chọn TomEE server đã cấu hình
   - Trong tab Deployment, thêm artifact đã tạo

## Chạy ứng dụng

1. Khởi động SQL Server (thường tự động chạy khi khởi động Windows)

2. Khởi động TomEE:
   - Trong IDE: Nhấn nút Run (Shift + F10)
   - Hoặc thủ công:
     + Windows: Chạy `bin/startup.bat`
     + Linux/Mac: Chạy `bin/startup.sh`

3. Truy cập ứng dụng:
   - URL: `http://localhost:8080/LibrariManagement_war_exploded`
   - Đăng nhập admin mặc định:
     + Username: admin
     + Password: admin

## Xử lý lỗi thường gặp

1. Lỗi kết nối database:
   - Kiểm tra SQL Server đã chạy chưa (SQL Server Configuration Manager)
   - Kiểm tra tài khoản sa đã được kích hoạt
   - Kiểm tra mật khẩu sa trong DBContext.java
   - Kiểm tra tên database và port

2. Lỗi TomEE:
   - Kiểm tra port 8080 có bị chiếm không
   - Kiểm tra log trong thư mục logs của TomEE
   - Đảm bảo đúng phiên bản JDK

3. Lỗi đăng nhập:
   - Kiểm tra bảng USER trong database
   - Kiểm tra tài khoản admin đã được thêm
   - Xóa cache trình duyệt và cookie

## Hỗ trợ và tài liệu tham khảo

1. SQL Server:
   - [Tài liệu SQL Server](https://learn.microsoft.com/en-us/sql/sql-server)
   - [Hướng dẫn sử dụng SSMS](https://learn.microsoft.com/en-us/sql/ssms/sql-server-management-studio-ssms)

2. TomEE:
   - [Tài liệu TomEE](https://tomee.apache.org/documentation.html)
   - [TomEE Configuration](https://tomee.apache.org/configuration.html)

3. Java Web:
   - [Java Servlet Tutorial](https://www.javatpoint.com/servlet-tutorial)
   - [JSP Tutorial](https://www.javatpoint.com/jsp-tutorial)

## Lưu ý bảo mật

1. Trong môi trường production:
   - Thay đổi tất cả mật khẩu mặc định
   - Sử dụng HTTPS
   - Mã hóa mật khẩu người dùng (bcrypt)
   - Cấu hình tường lửa
   - Giới hạn quyền truy cập SQL Server

2. Backup dữ liệu:
   - Backup database thường xuyên
   - Lưu trữ backup ở nơi an toàn
   - Có kế hoạch phục hồi dữ liệu
