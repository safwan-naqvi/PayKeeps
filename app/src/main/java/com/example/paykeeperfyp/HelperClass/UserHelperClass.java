package com.example.paykeeperfyp.HelperClass;

public class UserHelperClass {
    String fullName,email,password,phoneNo,age,gender,image,address,choosenBusiness;

    public UserHelperClass() {
    }

    public UserHelperClass(String fullName, String email, String password, String phoneNo, String age, String gender, String choosenBusiness) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.age = age;
        this.gender = gender;
        this.choosenBusiness = choosenBusiness;
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

    public String getChoosenBusiness() {
        return choosenBusiness;
    }
}
