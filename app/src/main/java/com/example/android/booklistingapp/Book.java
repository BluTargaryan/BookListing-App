package com.example.android.booklistingapp;

public class Book {
    //Image URL
    private String imgUrl;

    //Name of the book
    private String Name;

    //Author of the book
    private String Author;

    //Preview url of the book
    private String Url;

    /**
     * Create a new Book object
     */
    public Book( String mname, String mauthor, String murl,String mimgUrl)
    {
        Name = mname;
        Author = mauthor;
        Url = murl;
        imgUrl = mimgUrl;
    }


    //Get name of book
    public String getName(){
        return Name;
    }

    //Get author of the book

    public String getAuthor() {
        return Author;
    }

    //Get preview url

    public String getUrl() {
        return Url;
    }

    //Get image Url

    public String getImgUrl() {
        return imgUrl;
    }
}
