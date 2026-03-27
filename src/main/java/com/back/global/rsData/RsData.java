package com.back.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;

// 모든 API 응답을 같은 형태로 내려주기 위한 공통 래퍼
public record RsData<T>(
        String msg,
        String resultCode,
        T data
) {
    public RsData(String msg, String resultCode) {
        this(msg, resultCode, null);
    }

    @JsonIgnore
    public int getStatusCode() {
        return Integer.parseInt(resultCode.split("-")[0]);
    }
}
