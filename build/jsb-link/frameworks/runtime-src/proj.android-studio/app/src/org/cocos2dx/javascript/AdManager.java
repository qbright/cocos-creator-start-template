package org.cocos2dx.javascript;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdManager {
    private static final String APP_ID = "ca-app-pub-2178561732863118~5126041893";

    private Context mainActive = null;
    private static AdManager mInstace = null;

    private InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd;

    private LinearLayout bannerLayout;
    private AdView mAdView;


    public static AdManager getInstance() {
        if (null == mInstace) {
            mInstace = new AdManager();
        }
        return mInstace;
    }

    public void init(Context context) {
        this.mainActive = context;
        // 初始化广告 SDK.

        MobileAds.initialize(context, APP_ID);

        mInterstitialAd = new InterstitialAd(mainActive);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        rewardedAd = new RewardedAd(mainActive, "ca-app-pub-3940256099942544/5224354917");

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        this.loadBannerAd();

    }

    private void loadBannerAd() {
        bannerLayout = new LinearLayout(this.mainActive);
        bannerLayout.setOrientation(LinearLayout.VERTICAL);

        mAdView = new AdView(this.mainActive);
//        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdSize(this.getAdSize());
        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        AdRequest.Builder adRequesetBuilder = new AdRequest.Builder();

        adRequesetBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        bannerLayout.addView(mAdView);

        mAdView.loadAd(adRequesetBuilder.build());

        AppActivity activity = (AppActivity) this.mainActive;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        activity.addContentView(bannerLayout, params);
        bannerLayout.setVisibility(View.VISIBLE);


    }

    private AdSize getAdSize() {

        Display display = ((Activity) this.mainActive).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this.mainActive, adWidth);

    }

    public static void showRewardAd() {
        AppActivity mActivity = (AppActivity) AdManager.getInstance().mainActive;
        // 一定要确保在UI线程操作
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppActivity mActivity = (AppActivity) AdManager.getInstance().mainActive;
                if (AdManager.getInstance().rewardedAd.isLoaded()) {
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            // Ad failed to display.
                        }
                    };
                    AdManager.getInstance().rewardedAd.show(mActivity, adCallback);
                } else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                }
            }
        });

    }

    public static void showInterstitialAd() {

        AppActivity mActivity = (AppActivity) AdManager.getInstance().mainActive;
        // 一定要确保在UI线程操作
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AdManager.getInstance().mInterstitialAd.isLoaded()) {
                    AdManager.getInstance().mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

    }
}
