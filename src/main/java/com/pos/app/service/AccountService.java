package com.pos.app.service;

import com.pos.app.model.request.RequestCreateAccount;

public interface AccountService {
    String createNewAccount(RequestCreateAccount req);
}
