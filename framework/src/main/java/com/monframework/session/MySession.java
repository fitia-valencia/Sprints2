package com.monframework.session;

import javax.servlet.http.HttpSession;

public class MySession {

    private HttpSession session;

    public MySession(HttpSession session) {
        this.session = session;
    }

    public void set(String key, Object value) {
        session.setAttribute(key, value);
    }

    public Object get(String key) {
        return session.getAttribute(key);
    }

    public void remove(String key) {
        session.removeAttribute(key);
    }

    public boolean exists(String key) {
        return session.getAttribute(key) != null;
    }

    public void invalidate() {
        session.invalidate();
    }
}
