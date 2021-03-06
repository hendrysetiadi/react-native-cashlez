
package id.codecapital.cashlez;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

import id.codecapital.cashlez.module.authentication.AuthenticationModule;
import id.codecapital.cashlez.module.linkaja.LinkAjaModule;
import id.codecapital.cashlez.module.mandiripay.MandiriPayModule;
import id.codecapital.cashlez.module.ovo.OvoModule;
import id.codecapital.cashlez.module.payment.PaymentModule;

public class RNCashlezPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
      return Arrays.<NativeModule>asList(
              new RNCashlezModule(reactContext),
              new AuthenticationModule(reactContext),
              new PaymentModule(reactContext),
              new OvoModule(reactContext),
              new LinkAjaModule(reactContext),
              new MandiriPayModule(reactContext)
      );
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
      return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      return Collections.emptyList();
    }
}
