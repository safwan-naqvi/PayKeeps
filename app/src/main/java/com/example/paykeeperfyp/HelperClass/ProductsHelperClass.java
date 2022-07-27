package com.example.paykeeperfyp.HelperClass;

public class ProductsHelperClass {
    String category, date, time, description, featured, image, pid, pname, price, qty,rating;

    public ProductsHelperClass(){
        this.rating = "0";
    }

    public ProductsHelperClass(String category, String date, String time, String description, String featured, String image, String pid, String pname, String price, String qty) {
        this.category = category;
        this.date = date;
        this.time = time;
        this.description = description;
        this.featured = featured;
        this.image = image;
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.qty = qty;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getFeatured() {
        return featured;
    }

    public String getImage() {
        return image;
    }

    public String getPid() {
        return pid;
    }

    public String getPname() {
        return pname;
    }

    public String getPrice() {
        return price;
    }

    public String getQty() {
        return qty;
    }

    public String getRating() {
        return rating;
    }
}
