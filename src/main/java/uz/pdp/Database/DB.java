package uz.pdp.Database;

import uz.pdp.Entity.Book;
import uz.pdp.Entity.User;

import java.util.ArrayList;
import java.util.List;

public interface DB {
    List<Book> books = new ArrayList<>();
    List<User> users = new ArrayList<>();
}
