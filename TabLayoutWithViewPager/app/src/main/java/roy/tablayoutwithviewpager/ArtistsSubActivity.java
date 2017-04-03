package roy.tablayoutwithviewpager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by venkatesh on 20/3/17.
 */

public class ArtistsSubActivity extends AppCompatActivity implements ArtistsSubAdapter.InterfacePlaySongs {

    RecyclerView recyclerView;
    ArtistsSubAdapter artistsSubAdapter;
//    ArrayList<String> songs_names;


//    private MusicService musicSrv;
//    private Intent playIntent;
//    private boolean musicBound = false;


    private MediaPlayerService player;
    boolean serviceBound = true;
    public static final String Broadcast_PLAY_NEW_AUDIO = "audioplayer.PlayNewAudio";
    public static final String Broadcast_PLAY_NEW_AUDIO_TAB = "from_tab";
    private BroadcastReceiver receiver, receiver2, receiver3;

    boolean flag = true;

    // Change to your package name

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_layout);


        Bundle bundle = getIntent().getBundleExtra("bundle");
        ArrayList<SubActivityModel> songs_list = (ArrayList<SubActivityModel>) bundle.getSerializable("songs_list");
//        getSongsOfEachArtist(artist_obj);

//           ArrayList<String> songs_list = getIntent().getStringArrayListExtra("songs_list");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int s = intent.getIntExtra("data", -1);
                // do something here.
                artistsSubAdapter.updateItem(s);
            }
        };

        // update adapter's view(controllers)from notification controllers.
        receiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int s = intent.getIntExtra("data", -1);
                String action = intent.getStringExtra("action");
                artistsSubAdapter.updateControls(s, action);

            }
        };

        // process is foreground but clear notification then stop song.
        receiver3 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int s = intent.getIntExtra("data", -1);
                String action = intent.getStringExtra("action");
                artistsSubAdapter.refreshList(s, action);

            }
        };

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        artistsSubAdapter = new ArtistsSubAdapter(songs_list, this, ArtistsSubActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(artistsSubAdapter);


        Intent playerIntent = new Intent(this, MediaPlayerService.class);
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!serviceBound){
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            this.startService(playerIntent);
            this.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
//        serviceBound = false;


        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("intent_filter_update"));
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver2), new IntentFilter("intent_filter_receiver2"));
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver3), new IntentFilter("intent_filter_receiver3"));


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = false;

            Toast.makeText(ArtistsSubActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = true;
            Toast.makeText(ArtistsSubActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();

        }
    };


// Original
//    @Override
//    public void playAudio(ArrayList<SubActivityModel> audioList, int audioIndex) {
//
//        //Check is service is active
//        if (!serviceBound) {
//            //Store Serializable audioList to SharedPreferences
//            StorageUtil storage = new StorageUtil(getApplicationContext());
//            storage.storeAudio(audioList);
//            storage.storeAudioIndex(audioIndex);
//            if (storage.loadFlag()){
////                Intent playerIntent = new Intent(this, MediaPlayerService.class);
////                startService(playerIntent);
//                storage.storeFlag(false);
//                bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//
//            }
//            else {
//                Intent broadcastIntent1 = new Intent(Broadcast_PLAY_NEW_AUDIO_TAB);
//                sendBroadcast(broadcastIntent1);
//            }
//
//
//        } else {
//            //Store the new audioIndex to SharedPreferences
//            StorageUtil storage = new StorageUtil(getApplicationContext());
//            storage.storeAudioIndex(audioIndex);
//
//            //Service is active
//            //Send a broadcast to the service -> PLAY_NEW_AUDIO
//            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            sendBroadcast(broadcastIntent);
//        }
//
//
//    }





    @Override
    public void playAudio(ArrayList<SubActivityModel> audioList, int audioIndex) {

        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);
//            if (storage.loadFlag()){
//                Intent playerIntent = new Intent(this, MediaPlayerService.class);
//                startService(playerIntent);
//                storage.storeFlag(false);
//                bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                player.loadDataFirstTime();

//            }
//            else {
//                Intent broadcastIntent1 = new Intent(Broadcast_PLAY_NEW_AUDIO_TAB);
//                sendBroadcast(broadcastIntent1);
//            }

            serviceBound = true;

        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }


    }

    @Override
    public void pauseAudio() {
//        player.pauseMedia();
        player.setPauseToNotification();
    }

    @Override
    public void resumeAudio() {
//        player.resumeMedia();
        player.setResumeToNotification();


    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }


    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver2);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver3);


        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
//            player.stopSelf();

            serviceBound = true;
            Toast.makeText(this, "Service unBound", Toast.LENGTH_SHORT).show();


        }

        }


}



