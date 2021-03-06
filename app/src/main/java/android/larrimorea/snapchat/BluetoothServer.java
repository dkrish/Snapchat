package android.larrimorea.snapchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Alex on 6/15/2015.
 */
public class BluetoothServer extends AsyncTask<BluetoothAdapter, Void, Void> {
    private Activity activity;
    public UUID myUUID;
    public BluetoothAdapter btAdapter;

    private int timerCount = 0;
    private ConnectedThread cdt;

    @Override
    protected Void doInBackground(BluetoothAdapter... bluetoothAdapters){
        btAdapter = bluetoothAdapters[0];

        AcceptThread thread = new AcceptThread();
        thread.run();
        return null;
    }

    public class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket temp = null;

            //Got this uuid from using a random UUID generator online. Students should generate their own.
            myUUID = UUID.fromString("10d28dc0-105f-11e5-b939-0800200c9a66");
            try{
                //had errors using secure rfcomm
                temp = btAdapter.listenUsingInsecureRfcommWithServiceRecord("name", myUUID);
                //temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("name", myUUID);
               //Toast.makeText(, "AcceptThread", Toast.LENGTH_LONG).show();
                Log.i("AcceptThread", "AcceptingThreads");
            }catch (IOException e){
                Log.e("SendPicture Constructor", "IOException: " + e);
            }
            mmServerSocket = temp;

//            //Timer to check our connection to see if it needs to be reconnected
//            MyTimerTask yourTask = new MyTimerTask();
//            Timer t = new Timer();
//            //t.scheduleAtFixedRate(yourTask, 0, 5000);
//            t.scheduleAtFixedRate(yourTask, 0, 30000);
        }

        public void run(){
            BluetoothSocket socket = null;
            while(true){
                try{
                     socket = mmServerSocket.accept();
                }catch(IOException e){
                    Log.e("SendPicture While Loop", "IOException: " + e);
                }

                if(socket != null){
                    cdt = new ConnectedThread(socket);
                    cdt.run();
                    try {
                        Log.i("AcceptThreadRun", "Closing Socket");
                        mmServerSocket.close();
                        new BluetoothServer().execute(MainActivity.mBluetoothAdapter);
                    }
                    catch(IOException e) {
                        Log.e("SendPicture Socket", "IOException: " + e);
                    }
                    break;
                }else{
                    Log.i("AcceptThreadRun", "Null Socket");
                }
            }
        }



        public void cancel(){
            try{
                mmServerSocket.close();
            }catch(IOException e){

            }
        }
    }

//    public class MyTimerTask extends TimerTask {
//        public void run(){
////            if(!mmSocket.isConnected()){
////                //ConnectedThread.cancel();
////                Log.i("ConnectedThread", "Needs a Break");
////            }else{
////                Log.i("ConnectedThread", "Still going");
////            }
//            if(timerCount == 0){
//                timerCount++;
//            }else {
//                Log.i("MyTimerAccept", "Canceling Connection");
//                cdt = null;
//            }
//        }
//    }

}
