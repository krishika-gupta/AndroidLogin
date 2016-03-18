package com.clicklabs.androidlogin.Models;

/**
 * Created by hp- on 11-03-2016.
 */
public class UserInformation {

    String firstName;
    String lastName;
    String email;
    String gender;
    String pic;
    String address;
    String mobileNo;


    public UserInformation(String firstName, String lastName, String email,String pic, String gender) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email = email;
        this.pic=pic;
        this.gender = gender;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
