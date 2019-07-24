package id.codecapital.cashlez.module.authentication;

import com.facebook.react.bridge.Promise;

public interface iAuthenticationModule {
    void init(Promise promise);
    void doLogin(String username, String pin, Promise promise);
    void doLoginAggregator(String serverPublicKey, String clientPrivateKey, String mobileUserId, String aggregatorId, Promise promise);
}
