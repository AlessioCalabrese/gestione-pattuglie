package com.vigilanza.pattuglie.dto;

public class LoginNfcRequest {
    private String nfcTagId;

    public LoginNfcRequest() {
    }

    public String getNfcTagId() {
        return nfcTagId;
    }

    public void setNfcTagId(String nfcTagId) {
        this.nfcTagId = nfcTagId;
    }
}
