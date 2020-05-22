package com.seckill.response;

public class CommonResponse {

    /**
     * 处理结果，fail \ success
     */
    private String status;

    private Object data;

    public CommonResponse(Object result){
        this.status = "success";
        this.data = result;
    }

    public CommonResponse(String status, Object result){
        this.status = status;
        this.data = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
