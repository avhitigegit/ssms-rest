package com.dev.ssms.component.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Status {

    PENDING(1, "PENDING"),
    APPROVED(2, "APPROVED"),
    DELETED(3, "DELETED"),
    BLOCKED(4, "BLOCKED");

    private final Integer statusSeq;
    private final String status;

    Status(Integer statusSeq, String status) {
        this.statusSeq = statusSeq;
        this.status = status;
    }

    public Integer getStatusSeq() {
        return statusSeq;
    }

    public String getStatus() {
        return status;
    }
}
