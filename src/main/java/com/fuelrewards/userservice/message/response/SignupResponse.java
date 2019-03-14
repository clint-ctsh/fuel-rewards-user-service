package com.fuelrewards.userservice.message.response;

public class SignupResponse {
    private String token;
    private String type;
    private String cardNumber;

    public SignupResponse(String token, String type, String cardNumber) {
        this.token = token;
        this.type = type;
        this.cardNumber = cardNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
