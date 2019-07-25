package id.codecapital.cashlez.module.linkaja;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.cashlez.android.sdk.CLErrorResponse;
import com.cashlez.android.sdk.CLPayment;
import com.cashlez.android.sdk.bean.ApprovalStatus;
import com.cashlez.android.sdk.bean.TransactionType;
import com.cashlez.android.sdk.companion.printer.CLPrinterCompanion;
import com.cashlez.android.sdk.payment.CLPaymentResponse;
import com.cashlez.android.sdk.payment.CLTCashQRResponse;
import com.cashlez.android.sdk.payment.tcashqr.CLTCashQRHandler;
import com.cashlez.android.sdk.payment.tcashqr.ICLTCashQRHandler;
import com.cashlez.android.sdk.payment.tcashqr.ICLTCashQRService;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.google.zxing.WriterException;

import id.codecapital.cashlez.module.Const;
import id.codecapital.cashlez.module.util.QRGenerator;

public class LinkAjaModule extends ReactContextBaseJavaModule implements iLinkAjaModule, ICLTCashQRService {
    private final ReactApplicationContext reactContext;
    private ICLTCashQRHandler mCLLinkAjaHandler;
    private CLTCashQRResponse mCLLinkAjaResponse;
    private Promise mPromise;

    public LinkAjaModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNCashlezLinkAja";
    }



    @ReactMethod
    @Override
    public void init() {
        if (mCLLinkAjaHandler == null) {
            this.mCLLinkAjaHandler = new CLTCashQRHandler(getCurrentActivity(), null, this);
        }
    }



    @ReactMethod
    @Override
    public void doStartLinkAjaHandler() { mCLLinkAjaHandler.doStartTCashHandler(); }

    @ReactMethod
    @Override
    public void doResumeLinkAjaHandler() {
        mCLLinkAjaHandler.doResumeTCashHandler();
    }

    @ReactMethod
    @Override
    public void doStopLinkAjaHandler() {
        mCLLinkAjaHandler.doStopTCashHandler();
    }



    @ReactMethod
    @Override
    public void doPayLinkAja(String amount, String description, Promise promise) {
        this.mPromise = promise;

        // Create Transaction & Generate QR Code
        CLPayment linkAjaCLPayment = new CLPayment();
        linkAjaCLPayment.setAmount(amount);
        linkAjaCLPayment.setDescription(description);
        linkAjaCLPayment.setTransactionType(TransactionType.QR);

        mCLLinkAjaHandler.doProceedTCashQRPayment(linkAjaCLPayment);
    }

    @Override
    public void onTCashQRSuccess(CLTCashQRResponse cltCashQRResponse) {
        mCLLinkAjaResponse = cltCashQRResponse;

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
        map.putString(Const.RETURN_CODE_KEY, null);
        map.putString(Const.RETURN_MESSAGE_KEY, "Transaction has been created successfully");

        mPromise.resolve(map);
    }

    @Override
    public void onTCashQRError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }



    @ReactMethod
    @Override
    public void doPrintQR(Promise promise) {
        try {
            Bitmap bitmap = QRGenerator.encodeAsBitmap(getCurrentActivity(), mCLLinkAjaResponse.getQrCodeContent());
            mCLLinkAjaHandler.doPrintQR(bitmap);

            WritableMap map = new WritableNativeMap();
            map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
            map.putString(Const.RETURN_CODE_KEY, null);
            map.putString(Const.RETURN_MESSAGE_KEY, "Print QR Code success");

            mPromise.resolve(map);
        } catch (WriterException e) {
            e.printStackTrace();

            WritableMap map = new WritableNativeMap();
            map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
            map.putString(Const.RETURN_CODE_KEY, null);
            map.putString(Const.RETURN_MESSAGE_KEY, e.getMessage());

            mPromise.resolve(map);
        }
    }



    @ReactMethod
    @Override
    public void doCheckLinkAjaStatus(Promise promise) {
        this.mPromise = promise;

        if (mCLLinkAjaResponse != null) {
            mCLLinkAjaHandler.doCheckTCashQRStatus(mCLLinkAjaResponse);
        } else {
            Toast.makeText(reactContext, "Invalid Payment Response", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onCheckTCashQRStatusSuccess(CLTCashQRResponse cltCashQRResponse) {
        mCLLinkAjaResponse = cltCashQRResponse;

        int transactionStatus = cltCashQRResponse.getTransactionStatus();
        String approvalMessage = ApprovalStatus.getStatus(transactionStatus).getMessage();
        WritableMap map = new WritableNativeMap();

        if (transactionStatus == ApprovalStatus.APPROVED.getCode()) {
            map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
        } else {
            map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        }

        map.putString(Const.RETURN_CODE_KEY, null);
        map.putString(Const.RETURN_MESSAGE_KEY, approvalMessage);

        mPromise.resolve(map);
    }

    @Override
    public void onCheckTCashQRStatusError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }



    @Override
    public void onPrintingSuccess(CLPrinterCompanion clPrinterCompanion) {
        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
        map.putString(Const.RETURN_CODE_KEY, null);
        map.putString(Const.RETURN_MESSAGE_KEY, clPrinterCompanion.getMessage());

        mPromise.resolve(map);
    }

    @Override
    public void onPrintingError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }
}
