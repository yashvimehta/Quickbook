package com.example.quickbook.ApiHelper;

public class BFResult {
    final private boolean general_success;
    final private boolean face_success;
    final private boolean book_success;
    final private String name;
    final private String isbn;
    final private String title;
    final private String face_error;
    final private String book_error;
    final private String general_error;

    public BFResult(boolean general_success, boolean face_success, boolean book_success, String name, String isbn, String title, String face_error, String book_error,String general_error) {
        this.general_success=general_success;
        this.face_success=face_success;
        this.book_success=book_success;
        this.name=name;
        this.isbn=isbn;
        this.title=title;
        this.face_error=face_error;
        this.book_error=book_error;
        this.general_error=general_error;
    }

    public boolean getGeneralSuccess() {
        return general_success;
    }
    public boolean getFaceSuccess() {
        return face_success;
    }
    public boolean getBookSuccess() {
        return book_success;
    }
    public String getName() {
        return name;
    }
    public String getIsbn() {
        return isbn;
    }
    public String getTitle() {
        return title;
    }
    public String getFaceError() {
        return face_error;
    }
    public String getBookError() {
        return book_error;
    }
    public String getGeneralError() {
        return general_error;
    }
}
