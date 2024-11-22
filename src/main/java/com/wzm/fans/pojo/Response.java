package com.wzm.fans.pojo;

import lombok.Data;

@Data
public class Response<T> {

    private boolean success;

    private String error;

    private T body;

    public static <T> Response<T> ok(T body){
        Response<T> result = new Response<>();
        result.setBody(body);
        result.setSuccess(true);
        return result;
    }

    public static<T>  Response<T> ok(){
        Response<T> result = new Response<>();
        result.setSuccess(true);
        return result;
    }

    public static<T> Response<T> error(String error){
        Response<T> result = new Response<>();
        result.setError(error);
        result.setSuccess(false);
        return result;
    }
}
