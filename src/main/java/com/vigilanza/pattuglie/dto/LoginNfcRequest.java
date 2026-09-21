package com.vigilanza.pattuglie.dto;

public class LoginNfcRequest {
    private String nfcTagId;
    /** Vero se il codice è stato digitato a mano invece che letto dal lettore NFC. */
    private boolean manuale;

    public LoginNfcRequest() {
    }

    public boolean isManuale() {
        return manuale;
    }

    public void setManuale(boolean manuale) {
        this.manuale = manuale;
    }

    public String getNfcTagId() {
        return nfcTagId;
    }

    public void setNfcTagId(String nfcTagId) {
        this.nfcTagId = nfcTagId;
    }
}
