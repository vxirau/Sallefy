package com.prpr.androidpprog2.entregable.model;

import com.google.gson.annotations.SerializedName;

public class UserRegister {

    @SerializedName("activated")
    private Boolean activated;

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("id")
    private Integer id;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("langKey")
    private String langKey;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("login")
    private String login;

    @SerializedName("password")
    private String password;




    public UserRegister(String email, String login, String password) {
        this.activated = true;
        this.email = email;
        this.login = login;
        this.password = password;
        this.id = null;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
