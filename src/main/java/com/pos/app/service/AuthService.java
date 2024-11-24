package com.pos.app.service;

import com.pos.app.model.request.RequestSignIn;
import com.pos.app.model.response.ResponseSignIn;

public interface AuthService {

    ResponseSignIn signInStaff(RequestSignIn req);

    ResponseSignIn signInSuperAdmin(RequestSignIn req);
}
