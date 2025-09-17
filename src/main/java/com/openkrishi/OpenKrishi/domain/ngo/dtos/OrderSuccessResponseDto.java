package com.openkrishi.OpenKrishi.domain.ngo.dtos;

public class OrderSuccessResponseDto {

    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String message;


    public OrderSuccessResponseDto (String status, String message)
    {
        this.status = status;
        this.message = message;
    }


}
