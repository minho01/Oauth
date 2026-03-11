package com.back.global.rsData;

public record RsData<T>(
        String msg,
        String resultCode,
        T data
) {
}
