package com.back.global.rsData;

public record RsData<T>(
        String msg,
        String resultCode,
        T data
) {
    public RsData(String msg, String resultCode) {
        this(msg, resultCode, null);
    }

    public int getStatusCode() {
        return Integer.parseInt(resultCode.split("-")[0]);
    }
}
