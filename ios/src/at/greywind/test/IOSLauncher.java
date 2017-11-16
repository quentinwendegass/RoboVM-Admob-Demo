package at.greywind.test;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.iosrobovm.objectal.OALSimpleAudio;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIScreen;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication.Delegate;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.utils.Logger;
import org.robovm.pods.google.mobileads.*;

public class IOSLauncher extends Delegate implements IActivityRequestHandler {
    private static final Logger log = new Logger(IOSLauncher.class.getName(), Application.LOG_DEBUG);
    private static final boolean USE_TEST_DEVICES = true;
    private GADBannerView adview;
    private GADInterstitial ad;
    private boolean adsInitialized = false;
    private IOSApplication iosApplication;

    @Override
    protected IOSApplication createApplication() {
        final IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = false;
        config.orientationPortrait = true;
        config.allowIpod = true;
        OALSimpleAudio.sharedInstance().setUseHardwareIfAvailable(true);

        iosApplication = new IOSApplication(new MyGdxGame(this), config);
        return iosApplication;
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();

    }


    @Override
    public void hideBanner() {
        initializeAds();

        final CGSize screenSize = UIScreen.getMainScreen().getBounds().getSize();
        double screenWidth = screenSize.getWidth();

        final CGSize adSize = adview.getBounds().getSize();
        double adWidth = adSize.getWidth();
        double adHeight = adSize.getHeight();

        log.debug(String.format("Hidding ad. size[%s, %s]", adWidth, adHeight));

        float bannerWidth = (float) screenWidth;
        float bannerHeight = (float) (bannerWidth / adWidth * adHeight);

        adview.setFrame(new CGRect(0, -bannerHeight, bannerWidth, bannerHeight));
    }

    @Override
    public void showBanner() {
        initializeAds();

        final CGSize screenSize = UIScreen.getMainScreen().getBounds().getSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();

        final CGSize adSize = adview.getBounds().getSize();
        double adWidth = adSize.getWidth();
        double adHeight = adSize.getHeight();

        log.debug(String.format("Showing ad. size[%s, %s]", adWidth, adHeight));

        float bannerWidth = (float) screenWidth;
        float bannerHeight = (float) (bannerWidth / adWidth * adHeight);

        adview.setFrame(new CGRect((screenWidth / 2) - adWidth / 2, screenHeight - adHeight, bannerWidth, bannerHeight));
    }

    @Override
    public void showInterstitial() {
        if(ad.hasBeenUsed()) {
            ad = new GADInterstitial("ca-app-pub-3940256099942544/4411468910");

            GADRequest request = new GADRequest();
            final NSArray<?> testDevices = new NSArray<NSObject>(
                    new NSString(GADRequest.getSimulatorID()));
            request.setTestDevices(testDevices.asStringList());

            ad.loadRequest(request);
            log.debug("Request loaded..");
        }

        if(ad.isReady()){
               ad.present(iosApplication.getUIViewController());
               log.debug("Interstitial Ad shown");
           }else{
               log.debug("Interstitial Ad not ready");
        }
    }

    @Override
    public void initAds(){
        ad = new GADInterstitial("ca-app-pub-3940256099942544/4411468910");

        GADRequest request = new GADRequest();
        final NSArray<?> testDevices = new NSArray<NSObject>(
                new NSString(GADRequest.getSimulatorID()));
        request.setTestDevices(testDevices.asStringList());

        ad.loadRequest(request);
        log.debug("Request loaded..");
    }

    public void initializeAds() {
        if (!adsInitialized) {
            log.debug("Initalizing ads...");

            adsInitialized = true;

            adview = new GADBannerView(GADAdSize.SmartBannerPortrait());
            adview.setAdUnitID("ca-app-pub-3940256099942544/6300978111"); //put your secret key here
            adview.setRootViewController(iosApplication.getUIViewController());
            iosApplication.getUIViewController().getView().addSubview(adview);

            final GADRequest request = new GADRequest();
            if (USE_TEST_DEVICES) {
                final NSArray<?> testDevices = new NSArray<NSObject>(
                        new NSString(GADRequest.getSimulatorID()));
                request.setTestDevices(testDevices.asStringList());
                log.debug("Test devices: " + request.getTestDevices());
            }

            adview.setDelegate(new GADBannerViewDelegateAdapter() {
                @Override
                public void didReceiveAd(GADBannerView view) {
                    super.didReceiveAd(view);
                    log.debug("didReceiveAd");
                }

                @Override
                public void didFailToReceiveAd(GADBannerView view,
                                               GADRequestError error) {
                    super.didFailToReceiveAd(view, error);
                    log.debug("didFailToReceiveAd:" + error);
                }
            });

            adview.loadRequest(request);

            log.debug("Initalizing ads complete.");
        }
    }

    @Override
    public void showAds(boolean show) {
        initializeAds();

        final CGSize screenSize = UIScreen.getMainScreen().getBounds().getSize();
        double screenWidth = screenSize.getWidth();

        final CGSize adSize = adview.getBounds().getSize();
        double adWidth = adSize.getWidth();
        double adHeight = adSize.getHeight();

        log.debug(String.format("Hidding ad. size[%s, %s]", adWidth, adHeight));

        float bannerWidth = (float) screenWidth;
        float bannerHeight = (float) (bannerWidth / adWidth * adHeight);

        if(show) {
            adview.setFrame(new CGRect((screenWidth / 2) - adWidth / 2, 0, bannerWidth, bannerHeight));
        } else {
            adview.setFrame(new CGRect(0, -bannerHeight, bannerWidth, bannerHeight));
        }
    }
}