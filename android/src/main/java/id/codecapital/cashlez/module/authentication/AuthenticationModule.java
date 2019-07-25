package id.codecapital.cashlez.module.authentication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.cashlez.android.sdk.CLErrorResponse;
import com.cashlez.android.sdk.CLPaymentCapability;
import com.cashlez.android.sdk.login.CLLoginHandler;
import com.cashlez.android.sdk.login.CLLoginResponse;
import com.cashlez.android.sdk.login.ICLLoginHandler;
import com.cashlez.android.sdk.login.ICLLoginService;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import id.codecapital.cashlez.module.ApplicationState;
import id.codecapital.cashlez.module.Const;


public class AuthenticationModule extends ReactContextBaseJavaModule implements ICLLoginService, iAuthenticationModule {
    private final ReactApplicationContext reactContext;
    private static final int MY_PERMISSIONS_REQUEST = 8;
    private ICLLoginHandler mCLLoginHandler;
    private Promise mPromise;

    public AuthenticationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNCashlezAuthentication";
    }



    @ReactMethod
    @Override
    public void init() {
        if (mCLLoginHandler == null) {
            this.mCLLoginHandler = new CLLoginHandler(getCurrentActivity(), this);
        }

        // Check for Location Permission
        if ((ContextCompat.checkSelfPermission(this.reactContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this.reactContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getCurrentActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
        }
    }



    @ReactMethod
    @Override
    public void doLogin(String username, String pin, Promise promise) {
        this.mPromise = promise;
        mCLLoginHandler.doLogin(username, pin);
    }

    @ReactMethod
    @Override
    public void doLoginAggregator(String serverPublicKey, String clientPrivateKey, String mobileUserId, String aggregatorId, Promise promise) {
        this.mPromise = promise;
        mCLLoginHandler.doLogin(serverPublicKey, clientPrivateKey, mobileUserId, aggregatorId);
    }



    @Override
    public void onStartActivation(String s) {

    }

    @Override
    public void onLoginSuccess(CLLoginResponse clLoginResponse) {
        CLPaymentCapability paymentCapability = clLoginResponse.getPaymentCapability();
        ApplicationState.getInstance().setPaymentCapability(paymentCapability);


        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
        map.putString(Const.RETURN_CODE_KEY, null);
        map.putString(Const.RETURN_MESSAGE_KEY, "Cashlez Account has been Authenticated successfuly");

        mPromise.resolve(map);
    }

    @Override
    public void onLoginError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }

    @Override
    public void onNewVersionAvailable(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }

    @Override
    public void onApplicationExpired(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }
}
