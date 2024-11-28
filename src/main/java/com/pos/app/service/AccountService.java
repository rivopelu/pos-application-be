package com.pos.app.service;

import com.pos.app.entities.Account;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.ResponseGetMe;
import com.pos.app.model.response.ResponsePasswordCreateAccount;

public interface AccountService {
    String createNewAccount(RequestCreateAccount req);
    String createAvatar(String name);

    Account getCurrentAccount();
    Account getCurrentAccount(String id);
    String getCurrentAccountId();
    String getCurrentClientId();
    String getCurrentClientIdOrNull();
    ResponseGetMe getMeData();

    ResponsePasswordCreateAccount newAccountByAdmin(RequestCreateAccount req);
}
