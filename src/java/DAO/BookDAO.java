/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import dbConnect.DBContext;
import entity.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author MSII
 */
public class BookDAO {

    Connection con = null;
    PreparedStatement pt = null;
    ResultSet rs = null;

    public ArrayList<Book> getListBookByPage(ArrayList<Book> list, int start, int end) {
        ArrayList<Book> arr = new ArrayList<>();
        for (int i = start; i < end; ++i) {
            arr.add(list.get(i));
        }
        return arr;
    }
    
    public Map<Integer, Book> getMapBook() {
        Map<Integer, Book> list = new HashMap<>();
        String sql = "select * from book";
        DBContext db = new DBContext();
        try {
            con = db.getConnection();
            pt = con.prepareStatement(sql);
            rs = pt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt(1),           // bookid
                        rs.getString(2),        // name
                        rs.getString(4),        // author
                        rs.getString(3),        // category
                        rs.getString(6),        // image
                        rs.getString(7),        // description
                        rs.getInt(8),          // quantity
                        rs.getInt(5),          // status
                        rs.getInt(9)           // current
                );
                list.put(book.getBookid(), book);
            }
            con.close();
            pt.close();
            rs.close();
            return list;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public ArrayList<Book> getAllBook() {
        ArrayList<Book> list = new ArrayList<>();
        String sql = "select * from book";
        DBContext db = new DBContext();
        try {
            con = db.getConnection();
            pt = con.prepareStatement(sql);
            rs = pt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt(1),           // bookid
                        rs.getString(2),        // name
                        rs.getString(4),        // author
                        rs.getString(3),        // category
                        rs.getString(6),        // image
                        rs.getString(7),        // description
                        rs.getInt(8),          // quantity
                        rs.getInt(5),          // status
                        rs.getInt(9)           // current
                );
                list.add(book);
            }
            con.close();
            pt.close();
            rs.close();
            return list;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    public ArrayList<Book> getBookByName(String name) {
        ArrayList<Book> list = new ArrayList<>();
        String sql = "select * from book where [name] like '%" + name + "%'";
        DBContext db = new DBContext();
        try {
            con = db.getConnection();
            pt = con.prepareStatement(sql);
            rs = pt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt(1),           // bookid
                        rs.getString(2),        // name
                        rs.getString(4),        // author
                        rs.getString(3),        // category
                        rs.getString(6),        // image
                        rs.getString(7),        // description
                        rs.getInt(8),          // quantity
                        rs.getInt(5),          // status
                        rs.getInt(9)           // current
                );
                list.add(book);
            }
            con.close();
            pt.close();
            rs.close();
            return list;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    public ArrayList<Book> getBookByCategory(int cid) {
        ArrayList<Book> list = new ArrayList<>();
        String sql = "select * from book where category =" + cid;
        DBContext db = new DBContext();
        try {
            con = db.getConnection();
            pt = con.prepareStatement(sql);
            rs = pt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt(1),           // bookid
                        rs.getString(2),        // name
                        rs.getString(4),        // author
                        rs.getString(3),        // category
                        rs.getString(6),        // image
                        rs.getString(7),        // description
                        rs.getInt(8),          // quantity
                        rs.getInt(5),          // status
                        rs.getInt(9)           // current
                );
                list.add(book);
            }
            con.close();
            pt.close();
            rs.close();
            return list;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public Book getBookById(int id) {
        String sql = "SELECT * FROM book WHERE id = ?";
        DBContext db = new DBContext();
        try {
            con = db.getConnection();
            pt = con.prepareStatement(sql);
            pt.setInt(1, id);
            Book book = null;
            rs = pt.executeQuery();
            if (rs.next()) {
                book = new Book(
                        rs.getInt("id"),        // bookid
                        rs.getString("name"),    // name
                        rs.getString("author"),  // author
                        rs.getString("category"),// category
                        rs.getString("img"),     // image
                        rs.getString("description"), // description
                        rs.getInt("total"),     // quantity
                        rs.getInt("status"),    // status
                        rs.getInt("current")    // current
                );
            }
            rs.close();
            pt.close();
            return book;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public void updateBook(Book book) {
        DBContext db = new DBContext();
        try {
            con = db.getConnection();
            pt = con.prepareStatement("UPDATE book "
                    + "SET [name]=?, image=?, author=?, category=?, description=?, quantity=?, status=?, [current]=? "
                    + "WHERE id=?");

            // Set parameters
            pt.setString(1, book.getName());
            pt.setString(2, book.getImage());
            pt.setString(3, book.getAuthor());
            pt.setString(4, book.getCategory());
            pt.setString(5, book.getDescription());
            pt.setInt(6, book.getQuantity());
            pt.setInt(7, book.getStatus());
            pt.setInt(8, book.getCurrent());
            pt.setInt(9, book.getBookid());

            pt.executeUpdate();
            con.close();
            pt.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertbook(Book book) {
        DBContext db = new DBContext();
        try {
            con = db.getConnection();
            String sql = "INSERT INTO book (id, [name], image, author, category, description, quantity, status, [current]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pt = con.prepareStatement(sql);
            pt.setInt(1, book.getBookid());
            pt.setString(2, book.getName());
            pt.setString(3, book.getImage());
            pt.setString(4, book.getAuthor());
            pt.setString(5, book.getCategory());
            pt.setString(6, book.getDescription());
            pt.setInt(7, book.getQuantity());
            pt.setInt(8, book.getStatus());
            pt.setInt(9, book.getCurrent());
            pt.executeUpdate();
            con.close();
            pt.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean deleteBook(String id) {
        DBContext db = new DBContext();
        try {
            con = db.getConnection();
            pt = con.prepareStatement("DELETE FROM book WHERE id=?");
            pt.setString(1, id);
            pt.executeUpdate();
            con.close();
            pt.close();
            return true;
        } catch (Exception e) {
            return false;

        }
    }
}
