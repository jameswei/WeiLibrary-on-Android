package com.weibo.net;

import java.util.List;

import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.RestAdapter;
/**
 * Created by weijia on 6/8/13.
 */
public class WeiLibraryClient {
    private static final String API_URL = "https://10.209.130.249:9080";

    public static class User {
        String id;
        String username;
        String password;
        String email;
        String bio;
        String tags;
        String books;
    }

    public static class Book{
        String id;
        String bookname;
        String description;
        String tags;
        String owner;
        String occupant;
    }

    public static class Result{
        int suc;
    }

    protected interface UserInterface {
        @GET("/user/all")
        List<User> getAllUsers();

        @POST("/user/login")
        Result login(String username,String password);
    }

    protected interface BookInterface{
        @GET("/book/all")
        List<Book> getAllBooks();

        @GET("/book/info/{bookid}")
        Book getBook(@Path("bookid")String bookId);

        @PUT("/book/info/{bookid}")
        boolean updateBook(@Path("bookid")String bookId);

        @DELETE("/book/info/{bookid}")
        boolean deleteBook(@Path("bookid")String bookId);

        @PUT("/book/status/{bookid}")
        boolean updateBookStatus(@Path("bookid")String bookId);
    }

    public class UserClient implements UserInterface{
        RestAdapter restAdapter = new RestAdapter.Builder().setServer(API_URL).build();
        UserInterface client =restAdapter.create(UserInterface.class);

        @Override
        public List<User> getAllUsers() {
            return client.getAllUsers();
        }

        @Override
        public Result login(String username, String password) {
//            return client.login();
            return null;
        }
    }

    public class BookClient implements BookInterface{
        RestAdapter restAdapter = new RestAdapter.Builder().setServer(API_URL).build();
        BookInterface client = restAdapter.create(BookInterface.class);

        @Override
        public List<Book> getAllBooks() {
            return client.getAllBooks();
        }

        @Override
        public Book getBook(@Path("bookid") String bookId) {
            return client.getBook(bookId);
        }

        @Override
        public boolean updateBook(@Path("bookid") String bookId) {
            return false;
        }

        @Override
        public boolean deleteBook(@Path("bookid") String bookId) {
            return false;
        }

        @Override
        public boolean updateBookStatus(@Path("bookid") String bookId) {
            return false;
        }
    }

}
