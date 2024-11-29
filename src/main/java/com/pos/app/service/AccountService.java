package com.pos.app.service;

import com.pos.app.entities.Account;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqChangePassword;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.ResponseGetMe;
import com.pos.app.model.response.ResponseListAccount;
import com.pos.app.model.response.ResponsePasswordCreateAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    Page<ResponseListAccount> listAccount(Pageable pageable);

    ResponsePasswordCreateAccount resetPassword(String id);

    ResponseEnum changePassword(ReqChangePassword req);
}
