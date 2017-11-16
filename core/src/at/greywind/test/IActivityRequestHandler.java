package at.greywind.test;

public interface IActivityRequestHandler {

    void hideBanner();
    void showBanner();
    void showInterstitial();
    void showAds(boolean show);
    void initAds();
}
