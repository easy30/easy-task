package com.cehome.task.console;

import javax.servlet.http.HttpServletRequest;

public interface GlobalService {
    long getLoginUserId(HttpServletRequest request);

    String getLoginUsername(HttpServletRequest request);

    boolean isAdminUser(HttpServletRequest request);
}
