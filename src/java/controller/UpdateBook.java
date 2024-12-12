/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import DAO.BookDAO;
import entity.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author MSII
 */
public class UpdateBook extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null || session.getAttribute("role") == null || !session.getAttribute("role").equals("admin")) {
            resp.sendRedirect("Login");
            return;
        }
        try {
            int bookid = Integer.parseInt(request.getParameter("bookid"));
            String name = request.getParameter("name");
            String author = request.getParameter("author");
            String category = request.getParameter("category");
            String image = request.getParameter("avt");
            if(image.length() == 0){
                image = "img/book/book.jpg";
            }
            String description = request.getParameter("description");
            int quantity = Integer.parseInt(request.getParameter("total"));
            int status = 1;
            int current = Integer.parseInt(request.getParameter("current"));

            Book newBook = new Book(bookid, name, author, category, image, description, quantity, status, current);
            BookDAO bDAO = new BookDAO();
            bDAO.updateBook(newBook);
            resp.sendRedirect("ViewBook?id="+bookid);
        } catch (Exception e) {
            System.out.println("Error in UpdateBook: " + e.getMessage());
        }
    }

}
