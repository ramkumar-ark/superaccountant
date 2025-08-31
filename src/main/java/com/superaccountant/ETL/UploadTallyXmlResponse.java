package com.superaccountant.ETL;

public class UploadTallyXmlResponse {
    private String message;

    public UploadTallyXmlResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
