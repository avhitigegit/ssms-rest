package com.dev.ssms.component.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ImageType {
    IMAGE(1, "IMAGE"),
    VIDEO(2, "VIDEO");

    private final Integer imgTypeSeq;
    private final String imgType;

    ImageType(Integer imgTypeSeq, String imgType) {
        this.imgTypeSeq = imgTypeSeq;
        this.imgType = imgType;
    }

    public Integer getImgTypeSeq() {
        return imgTypeSeq;
    }

    public String getImgType() {
        return imgType;
    }
}
