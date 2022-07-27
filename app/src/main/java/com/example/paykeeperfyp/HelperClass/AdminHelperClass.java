package com.example.paykeeperfyp.HelperClass;

public class AdminHelperClass {
    String fullName,email,password,phoneNo,age,gender,image,address,business;

    public AdminHelperClass(String fullName, String email, String password, String phoneNo, String age, String gender, String business) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.age = age;
        this.gender = gender;
        this.business = business;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getImage() {
        return image;
    }

    public String getAddress() {
        return address;
    }

    public String getBusiness() {
        return business;
    }
}
