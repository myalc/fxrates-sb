package com.myalc.fxrates.dto.external.fixerio;

import java.io.Serializable;

public class FixerioError implements Serializable {
    
    private Integer code;
    private String type;
    private String info;

    public FixerioError() {
    }

    public FixerioError(Integer code, String type, String info) {
        this.code = code;
        this.type = type;
        this.info = info;
    }
   
    public Integer getCode() {
        return code;
    }
   
    public String getType() {
        return type;
    }
   
    public String getInfo() {
        return info;
    }
   
    public void setCode(Integer code) {
        this.code = code;
    }
   
    public void setType(String type) {
        this.type = type;
    }
   
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "{" +
            " code='" + getCode() + "'" +
            ", type='" + getType() + "'" +
            ", info='" + getInfo() + "'" +
            "}";
    }

}