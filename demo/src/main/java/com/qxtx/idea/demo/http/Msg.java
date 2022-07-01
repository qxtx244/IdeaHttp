package com.qxtx.idea.demo.http;

/**
 * @author QXTX-WIN
 * <p>
 * <b>Create Date</b><p> 2022/5/17 16:15
 * <p>
 * <b>Description</b>
 * <pre>
 *
 * </pre>
 */
public class Msg {

    private String msg;

    public Msg() {}

    public Msg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
