USE Library_Manager;
GO

-- Xóa tất cả các ràng buộc khóa ngoại trước
IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_book_category_3A81B327')
    ALTER TABLE book DROP CONSTRAINT FK_book_category_3A81B327;
IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_borrower_userid_403A8C7D')
    ALTER TABLE borrower DROP CONSTRAINT FK_borrower_userid_403A8C7D;
IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_feedback_userid_4E88ABD1')
    ALTER TABLE feedback DROP CONSTRAINT FK_feedback_userid_4E88ABD1;

-- Xóa các bảng theo thứ tự (bảng con trước, bảng cha sau)
IF OBJECT_ID('feedback', 'U') IS NOT NULL
    DROP TABLE feedback;
IF OBJECT_ID('borrower', 'U') IS NOT NULL
    DROP TABLE borrower;
IF OBJECT_ID('BORROW', 'U') IS NOT NULL
    DROP TABLE BORROW;
IF OBJECT_ID('BOOK', 'U') IS NOT NULL
    DROP TABLE BOOK;
IF OBJECT_ID('category', 'U') IS NOT NULL
    DROP TABLE category;
IF OBJECT_ID('MEMBER', 'U') IS NOT NULL
    DROP TABLE MEMBER;
IF OBJECT_ID('USER', 'U') IS NOT NULL
    DROP TABLE [USER];
GO

-- Tạo bảng BOOK
CREATE TABLE BOOK (
    book_id VARCHAR(10) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    author NVARCHAR(100) NOT NULL,
    category NVARCHAR(50) NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0),
    status NVARCHAR(20) NOT NULL,
    CONSTRAINT CHK_BookID CHECK (book_id LIKE 'EMP[0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_Status CHECK (status IN ('Available', 'Borrowed', 'Maintenance'))
);
GO

-- Tạo bảng MEMBER
CREATE TABLE MEMBER (
    member_id VARCHAR(10) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    address NVARCHAR(200) NOT NULL,
    CONSTRAINT CHK_MemberID CHECK (member_id LIKE 'MEM[0-9][0-9][0-9][0-9][0-9]')
);
GO

-- Tạo bảng BORROW
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
GO 