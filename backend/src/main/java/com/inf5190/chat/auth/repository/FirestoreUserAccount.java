package com.inf5190.chat.auth.repository;

import java.util.Objects;

/**
 * Représente un message stocké dans firestore.
 */
public class FirestoreUserAccount {
    private String username;
    private String encodedPassword;
    public FirestoreUserAccount() {
    }
    public FirestoreUserAccount(String username, String encodedPassword) {
        this.username = username;
        this.encodedPassword = encodedPassword;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEncodedPassword() {
        return encodedPassword;
    }
    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirestoreUserAccount that = (FirestoreUserAccount) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(encodedPassword, that.encodedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, encodedPassword);
    }

    @Override
    public String toString() {
        return "FirestoreUserAccount{" +
                "username='" + username + '\'' +
                ", encodedPassword='" + encodedPassword + '\'' +
                '}';
    }

}
