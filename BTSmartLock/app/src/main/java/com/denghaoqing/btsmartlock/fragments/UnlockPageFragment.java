package com.denghaoqing.btsmartlock.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.os.CancellationSignal;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denghaoqing.btsmartlock.CryptoObjectHelper;
import com.denghaoqing.btsmartlock.MainActivity;
import com.denghaoqing.btsmartlock.utilities.security;
import com.denghaoqing.btsmartlock.FingerprintAuthCallBack;
import com.denghaoqing.btsmartlock.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UnlockPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UnlockPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnlockPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    private ImageView imgFinger;
    private ImageView imgSecureAuthStat;
    private ImageView imgCommStat;
    private ImageView imgAuthStat;
    private TextView txtTips;
    private EditText codeEnter;
    private Button mButton;

    private FingerprintManagerCompat fingerprintManager = null;
    private FingerprintAuthCallBack myAuthCallback = null;
    private CancellationSignal cancellationSignal = null;

    private boolean isAuthSuccess=false;
    private boolean isFingerPrint=true;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Handler handler = null;
    private OnFragmentInteractionListener mListener;


    public static UnlockPageFragment newInstances() {
        UnlockPageFragment fragment = new UnlockPageFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
    public UnlockPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UnlockPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UnlockPageFragment newInstance(String param1, String param2) {
        UnlockPageFragment fragment = new UnlockPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //imgFinger = (ImageView)View.



        //handle the fingerprint verification result
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Log.d("FingerPrint", "msg: " + msg.what + " ,arg1: " + msg.arg1);
                switch (msg.what) {
                    case MainActivity.MSG_AUTH_SUCCESS:
                        isAuthSuccess = true;
                        imgFinger.setImageResource( R.drawable.ic_verified_user_black_24dp) ;
                        imgSecureAuthStat.setVisibility(View.VISIBLE);
                        txtTips.setText(R.string.finger_auth_success_tips);
                        codeEnter.setVisibility(View.VISIBLE);
                        codeEnter.setInputType(InputType.TYPE_CLASS_TEXT);
                        mButton.setVisibility(View.VISIBLE);
                        codeEnter.setText("");
                        mButton.setText("UNLOCK");
                        Toast.makeText(getContext(), "Auth Success!", Toast.LENGTH_SHORT).show();

                        break;
                    case MainActivity.MSG_AUTH_FAILED:
                        txtTips.setText(R.string.finger_auth_failure_tips);
                        imgFinger.setImageResource(R.drawable.ic_info_outline_black_24dp);
                        Toast.makeText(getContext(), "Auth Failed!", Toast.LENGTH_SHORT).show();
                        break;
                    case MainActivity.MSG_AUTH_ERROR:

                        break;
                    case MainActivity.MSG_AUTH_HELP:

                        break;
                }
            }
        };


        // init fingerprint.
        fingerprintManager = FingerprintManagerCompat.from(getContext());
        if (!fingerprintManager.isHardwareDetected()) {
            isFingerPrint = false;
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            // no fingerprint image has been enrolled.

        } else {
            try {
                myAuthCallback = new FingerprintAuthCallBack(handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        try {
            CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
            if (cancellationSignal == null) {
                cancellationSignal = new CancellationSignal();
            }
            fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                    cancellationSignal, myAuthCallback, null);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Fingerprint init failed! Try again!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_unlock_page, container, false);
        imgFinger =(ImageView) view.findViewById(R.id.fingerprintimg);
        imgSecureAuthStat = (ImageView)view.findViewById(R.id.img_stat_security);
        imgCommStat=(ImageView)view.findViewById(R.id.img_stat_remote);
        imgAuthStat=(ImageView)view.findViewById(R.id.img_stat_auth);
        txtTips = (TextView)view.findViewById(R.id.txtTips);
        codeEnter=(EditText)view.findViewById(R.id.et_code);
        mButton=(Button)view.findViewById(R.id.submit);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFingerPrint &&  !isAuthSuccess){
                    SharedPreferences pref= getContext().getSharedPreferences("userinfo",Context.MODE_PRIVATE);
                    if(security.md5(codeEnter.getText().toString()).equals(pref.getString("pin","N/A"))){
                        isAuthSuccess = true;
                        imgFinger.setImageResource( R.drawable.ic_verified_user_black_24dp) ;
                        imgSecureAuthStat.setVisibility(View.VISIBLE);
                        txtTips.setText(R.string.pin_auth_success_tips);
                        codeEnter.setVisibility(View.VISIBLE);
                        codeEnter.setInputType(InputType.TYPE_CLASS_TEXT);
                        mButton.setVisibility(View.VISIBLE);
                        mButton.setText("UNLOCK");
                        codeEnter.setText("");
                        Toast.makeText(getContext(), "Auth Success!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "Auth failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //bluetooth communication
                    Toast.makeText(getContext(), "BT Comm", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(isFingerPrint){
            codeEnter.setVisibility(View.INVISIBLE);
            codeEnter.setText("");
            mButton.setVisibility(View.INVISIBLE);
            mButton.setText("UNLOCK");
        }else{
            codeEnter.setVisibility(View.VISIBLE);
            codeEnter.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mButton.setVisibility(View.VISIBLE);
            codeEnter.setText("");
            mButton.setText("Authorize");
        }

        imgFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAuthSuccess){
                    if(isFingerPrint) {
                        imgFinger.setImageResource(R.drawable.ic_lock_outline_black_24dp);
                        txtTips.setText(R.string.pin_auth_tips);
                        isFingerPrint = false;
                        codeEnter.setVisibility(View.VISIBLE);
                        codeEnter.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        codeEnter.setText("");
                        mButton.setVisibility(View.VISIBLE);
                        mButton.setText("Authorize");

                    }else{
                        imgFinger.setImageResource(R.drawable.ic_fingerprint_black_24dp);
                        txtTips.setText(R.string.finger_auth_tips);
                        codeEnter.setVisibility(View.INVISIBLE);
                        codeEnter.setText("");
                        isFingerPrint = true;
                        mButton.setVisibility(View.INVISIBLE);
                        mButton.setText("UNLOCK");
                    }
                }

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
