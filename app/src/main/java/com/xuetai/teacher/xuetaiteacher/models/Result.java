package com.xuetai.teacher.xuetaiteacher.models;

public class Result {

    /**
     * jsonrpc : 2.0
     * error : {"code":16,"message":"用户名或密码错误。"}
     * id : 1
     * code : 16
     */

    private String jsonrpc;
    /**
     * code : 16
     * message : 用户名或密码错误。
     */

    private ErrorBean error;
    private int id;
    private int code;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ErrorBean {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
