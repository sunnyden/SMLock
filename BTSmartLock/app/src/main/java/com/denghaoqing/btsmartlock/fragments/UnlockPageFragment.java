package com.denghaoqing.btsmartlock.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denghaoqing.btsmartlock.CryptoObjectHelper;
import com.denghaoqing.btsmartlock.MainActivity;
import com.denghaoqing.btsmartlock.utilities.BTCommSrv;
import com.denghaoqing.btsmartlock.utilities.security;
import com.denghaoqing.btsmartlock.FingerprintAuthCallBack;
import com.denghaoqing.btsmartlock.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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


    public static final int REQUEST_ENABLE_BT = 3;


    private ImageView imgFinger;
    private ImageView imgSecureAuthStat;
    private ImageView imgCommStat;
    private ImageView imgAuthStat;
    private TextView txtTips;
    private EditText codeEnter;
    private Button mButton;
    private ProgressBar procCommStat;
    private ProgressBar procAuthStat;
    /*BT Init*/
    private BluetoothAdapter mBluetoothAdapter = null;
    private BTCommSrv mBTCommSrv = null;
    private getMac mgetMac=null;
    private GetAuthCode mGetAuthCode = null;
    private int BTRetryCount=0;
    private String codeLock="";


    private FingerprintManagerCompat fingerprintManager = null;
    private FingerprintAuthCallBack myAuthCallback = null;
    private CancellationSignal cancellationSignal = null;

    private boolean isAuthSuccess=false;
    private boolean isFingerPrint=true;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public String mac="";
    public String code="";

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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//Get default bt adapter
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }


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
            isFingerPrint = false;
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
    public void onStart(){
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            try{
                //We don't need user's approval!
                mBluetoothAdapter.enable();
            }catch(Exception e){
                Log.e("Error",e.toString());
            }

        }
        if(mBTCommSrv==null){
            mBTCommSrv=new BTCommSrv(getActivity(),mHandler);
        }
    }



    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case BTCommSrv.Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BTCommSrv.STATE_CONNECTED:
                            //Connected
                            SharedPreferences userinfo = getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                            mGetAuthCode=new GetAuthCode(mac,userinfo.getString("token","null"),userinfo.getInt("uid",-1));
                            mGetAuthCode.execute((Void)null);
                            break;
                        case BTCommSrv.STATE_CONNECTING:
                            //Connecting
                            break;
                        case BTCommSrv.STATE_LISTEN:
                        case BTCommSrv.STATE_NONE:
                            //not connected
                            break;
                    }
                    break;
                case BTCommSrv.Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    //Message sent
                    break;
                case BTCommSrv.Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.e("Tag",readMessage);
                    try {
                        JSONObject mResult = new JSONObject(readMessage);
                        if(mResult.getInt("result")==0){
                            Thread mUploadResult = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        SharedPreferences userinfo = getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);

                                        URL url= new URL("http://58.63.232.138:62078/lock/api.php");
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.setDoOutput(true);
                                        conn.setDoInput(true);
                                        conn.setRequestMethod("POST");
                                        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                                        conn.connect();
                                        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                                        String content = "action=2&token="+userinfo.getString("token","null")+"&uid="+userinfo.getInt("uid",-1)+"&stat=1&lkcode="+codeLock;
                                        out.writeBytes(content);
                                        out.flush();
                                        out.close();
                                        InputStreamReader input=new InputStreamReader(conn.getInputStream());
                                        BufferedReader buffer = new BufferedReader(input);
                                        String resultData="",inputLine=null;
                                        while(((inputLine=buffer.readLine())!=null)) {
                                            resultData += inputLine + "\n";
                                        }
                                        Log.e("Http",resultData);
                                    } catch (Exception e){

                                    }
                                }
                            });
                            mUploadResult.run();
                            procAuthStat.setVisibility(View.GONE);
                            imgAuthStat.setImageResource(R.drawable.ic_check_circle_black_24dp);
                            imgAuthStat.setVisibility(View.VISIBLE);
                            mBTCommSrv.stop();
                            mButton.setEnabled(true);
                            codeEnter.setEnabled(true);
                            codeEnter.setText("");
                            BTRetryCount = 0;
                            Toast.makeText(getContext(),"Unlocked!",Toast.LENGTH_SHORT).show();
                        }else{
                            procAuthStat.setVisibility(View.GONE);
                            imgAuthStat.setImageResource(R.drawable.ic_info_outline_black_24dp);
                            imgAuthStat.setVisibility(View.VISIBLE);
                            mButton.setEnabled(true);
                            codeEnter.setEnabled(true);
                            Log.e("Post","Wrong json result");
                            mBTCommSrv.stop();
                            BTRetryCount = 0;
                        }
                    }catch(Exception e){
                        BTRetryCount++;
                        if(BTRetryCount==1){
                            SimpleDateFormat df = new SimpleDateFormat("yy,MM,dd,HH,mm,ss");
                            String message="code<"+code+">time<"+df.format(new Date())+">";
                            byte[] byteToSend = message.getBytes();//transform it into byte[]
                            mBTCommSrv.write(byteToSend);
                        }else{
                            procAuthStat.setVisibility(View.GONE);
                            imgAuthStat.setImageResource(R.drawable.ic_info_outline_black_24dp);
                            imgAuthStat.setVisibility(View.VISIBLE);
                            mButton.setEnabled(true);
                            codeEnter.setEnabled(true);
                            BTRetryCount = 0;
                        }

                       // mBTCommSrv.stop();
                       Log.e("Post",e.toString());
                    }
                    //Message received
                    break;
                case BTCommSrv.Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //msg.getData().getString(BTCommSrv.Constants.DEVICE_NAME);

                    break;
                case BTCommSrv.Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(BTCommSrv.Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    /**
     * Establish connection with other divice
     */
    private void connectDevice(String mac, boolean secure) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
        // Attempt to connect to the device
        Log.d("BTComm","Attemp to connect");
        mBTCommSrv.connect(device, secure);
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
        procCommStat=(ProgressBar)view.findViewById(R.id.onhttpConnProc);
        procAuthStat=(ProgressBar)view.findViewById(R.id.onBtCommProc);
        txtTips = (TextView)view.findViewById(R.id.txtTips);
        codeEnter=(EditText)view.findViewById(R.id.et_code);
        mButton=(Button)view.findViewById(R.id.submit);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFingerPrint &&  !isAuthSuccess){
                    SharedPreferences pref= getContext().getSharedPreferences("userinfo",Context.MODE_PRIVATE);
                    if(security.md5(codeEnter.getText().toString()).equals(pref.getString("pin","N/A"))){
                        cancellationSignal.cancel();
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
                    if(mBluetoothAdapter.isEnabled()){
                        //bluetooth communication

                        mButton.setEnabled(false);
                        codeEnter.setEnabled(false);
                        //Get MAC Address from remote server
                        SharedPreferences userinfo = getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                        codeLock=codeEnter.getText().toString();
                        mgetMac = new getMac(codeLock,userinfo.getString("token","null"),userinfo.getInt("uid",-1));
                        mgetMac.execute((Void)null);
                    }else{
                        Toast.makeText(getContext(), "Enabling Bluetooth", Toast.LENGTH_SHORT).show();
                        try{
                            mBluetoothAdapter.enable();
                        }catch(Exception e){
                            Log.e("Error",e.toString());
                        }
                    }

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


    public class getMac extends AsyncTask<Void, Void, Boolean> {

        private final String mUID;
        private final String mCode;
        private final String mToken;

        getMac(String code, String token,int uid) {
            mUID = String.valueOf(uid);
            mCode = code;
            mToken = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject result;
            try {


                URL url= new URL("http://58.63.232.138:62078/lock/api.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.connect();
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                String content = "action=2&token="+mToken+"&uid="+mUID+"&locknum="+mCode;
                out.writeBytes(content);
                out.flush();
                out.close();
                InputStreamReader input=new InputStreamReader(conn.getInputStream());
                BufferedReader buffer = new BufferedReader(input);
                String resultData="",inputLine=null;
                while(((inputLine=buffer.readLine())!=null)) {
                    resultData += inputLine + "\n";
                }
                Log.e("Http",resultData);
                result=new JSONObject(resultData);

            } catch (Exception e){

                return false;
            }
            boolean isValid=false;
            try {

                if(result.getInt("error")==0) {
                    mac=result.getString("mac");
                    isValid=true;
                }
            }catch (Exception e){
                isValid = false;
            }

            return isValid;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            if (success)
            {
                procCommStat.setVisibility(View.GONE);
                imgCommStat.setImageResource(R.drawable.ic_check_circle_black_24dp);
                imgCommStat.setVisibility(View.VISIBLE);
                imgAuthStat.setVisibility(View.GONE);
                procAuthStat.setVisibility(View.VISIBLE);
                connectDevice(mac,false);
            }else{
                procCommStat.setVisibility(View.GONE);
                imgCommStat.setImageResource(R.drawable.ic_info_outline_black_24dp);
                imgCommStat.setVisibility(View.VISIBLE);
                mButton.setEnabled(true);
                codeEnter.setEnabled(true);
            }
        }

        @Override
        protected void onCancelled() {

        }
    }


    public class GetAuthCode extends AsyncTask<Void, Void, Boolean> {

        private final String mUID;
        private final String mMac;
        private final String mToken;

        GetAuthCode(String mac, String token,int uid) {
            mUID = String.valueOf(uid);
            mMac = mac;
            mToken = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject result;
            try {


                URL url= new URL("http://58.63.232.138:62078/lock/api.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.connect();
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                String content = "action=4&token="+mToken+"&uid="+mUID+"&mac="+mMac;
                out.writeBytes(content);
                out.flush();
                out.close();
                InputStreamReader input=new InputStreamReader(conn.getInputStream());
                BufferedReader buffer = new BufferedReader(input);
                String resultData="",inputLine=null;
                while(((inputLine=buffer.readLine())!=null)) {
                    resultData += inputLine + "\n";
                }
                Log.e("Http",resultData);
                result=new JSONObject(resultData);

            } catch (Exception e){

                return false;
            }
            boolean isValid=false;
            try {

                if(result.getInt("error")==0) {
                    code=String.valueOf(result.getInt("code"));
                    isValid=true;
                }
            }catch (Exception e){
                isValid = false;
            }

            return isValid;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            if (success)
            {
                SimpleDateFormat df = new SimpleDateFormat("yy,MM,dd,HH,mm,ss");
                String message="code<"+code+">time<"+df.format(new Date())+">";
                Log.d("command",message);
                byte[] byteToSend = message.getBytes();//transform it into byte[]
                mBTCommSrv.write(byteToSend);
            }else{
                procAuthStat.setVisibility(View.GONE);
                imgAuthStat.setImageResource(R.drawable.ic_info_outline_black_24dp);
                imgAuthStat.setVisibility(View.VISIBLE);
                mButton.setEnabled(true);
                codeEnter.setEnabled(true);
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

}
