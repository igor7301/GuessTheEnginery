package com.guessenginery.guesstheenginery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.odnoklassniki.OkSocialNetwork;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.vk.sdk.VKScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.ok.android.sdk.util.OkScope;

/**
 * Created by Igor on 16.08.15.
 */
public class SocialNetworkFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener {
    /**
     * SocialNetwork Ids in ASNE:
     * 1 - Twitter
     * 2 - LinkedIn
     * 3 - Google Plus
     * 4 - Facebook
     * 5 - Vkontakte
     * 6 - Odnoklassniki
     * 7 - Instagram
     */
    public static SocialNetworkManager mSocialNetworkManager;
    private ImageView vk;
    private ImageView ok;
    private ImageView fb;
    private int networkId = 0;

    public SocialNetworkFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.social_network_buttons_fragment, container, false);
//        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        // init buttons and set Listener
        vk = (ImageView) rootView.findViewById(R.id.buttonVK);
        vk.setOnClickListener(loginClick);

        ok = (ImageView) rootView.findViewById(R.id.buttonOK);
        ok.setOnClickListener(loginClick);

        fb = (ImageView) rootView.findViewById(R.id.buttonFacebook);
        fb.setOnClickListener(loginClick);

        //Get Keys for initiate SocialNetworks
        String VK_KEY = getActivity().getString(R.string.vk_app_id);
        String OK_APP_ID = getActivity().getString(R.string.ok_app_id);
        String OK_PUBLIC_KEY = getActivity().getString(R.string.ok_public_key);
        String OK_SECRET_KEY = getActivity().getString(R.string.ok_secret_key);


        String[] okScope = new String[] {
                OkScope.VALUABLE_ACCESS
        };



        String[] vkScope = new String[]{
                VKScope.FRIENDS,
                VKScope.WALL,
                VKScope.PHOTOS,
                VKScope.NOHTTPS,
                VKScope.STATUS,
        };

        //Chose permissions
        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends"));

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(MainActivity.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager VkSocialNetwork
            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            OkSocialNetwork okNetwork = new OkSocialNetwork(this, OK_APP_ID, OK_PUBLIC_KEY, OK_SECRET_KEY, okScope);
            mSocialNetworkManager.addSocialNetwork(okNetwork);

            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);


            //Initiate every network from mSocialNetworkManager
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, MainActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if (!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
//                    initSocialNetwork(socialNetwork);
                }
            }
        }
        return rootView;
    }

//    private void initSocialNetwork(SocialNetwork socialNetwork) {
//        if (socialNetwork.isConnected()) {
//            switch (socialNetwork.getID()) {
//                case VkSocialNetwork.ID:
//                    vk.setText("Show VK profile");
//                    break;
//
//            }
//        }
//    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
//            initSocialNetwork(socialNetwork);
        }
    }

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.buttonVK:
                    networkId = VkSocialNetwork.ID;
                    break;
                case R.id.buttonOK:
                    networkId = OkSocialNetwork.ID;
                    break;
                case R.id.buttonFacebook:
                    networkId = FacebookSocialNetwork.ID;
                    break;

            }
            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if (!socialNetwork.isConnected()) {
                if (networkId != 0) {
                    socialNetwork.requestLogin();
//                    StartActivity.showProgress("Loading social person");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
//                startProfile(socialNetwork.getID());
                shareLink(mSocialNetworkManager.getSocialNetwork(networkId));
            }
        }
    };

    @Override
    public void onLoginSuccess(int i) {
        shareLink(mSocialNetworkManager.getSocialNetwork(networkId));

    }
    @Override
    public void onError(int i, String s, String s1, Object o) {
        Toast.makeText(getActivity(), R.string.login_error, Toast.LENGTH_LONG).show();
    }

    private void shareLink(SocialNetwork socialNetwork) {
        Bundle postParams = new Bundle();
        TextView textView = (TextView) getActivity().findViewById(R.id.textViewEndGameProgressMessage);
        String shareText = textView.getText().toString() + "\n"  + getString(R.string.app_link);
        postParams.putString(SocialNetwork.BUNDLE_LINK, shareText);
        socialNetwork.requestPostLink(postParams, shareText, postingComplete);
    }


    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            Toast.makeText(getActivity(), R.string.wall_message_success, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Toast.makeText(getActivity(), R.string.wall_message_error, Toast.LENGTH_LONG).show();
        }
    };
}