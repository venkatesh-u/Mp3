package roy.tablayoutwithviewpager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by venkatesh on 28/3/17.
 */

public class ActivityPlaying extends AppCompatActivity {
    ArrayList<SubActivityModel> list;
    int index;
    @Bind(R.id.image_albumArt)
    ImageView imageAlbumArt;
    @Bind(R.id.iv_skip_previous)
    ImageButton ivSkipPrevious;
    @Bind(R.id.iv_play)
    ImageButton ivPlay;
    @Bind(R.id.iv_skip_next)
    ImageButton ivSkipNext;
    @Bind(R.id.controls)
    LinearLayout controls;
    @Bind(R.id.tvSongName)
    TextView tvSongName;


    private MediaPlayerService player;
    boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "audioplayer.PlayNewAudio";
    private BroadcastReceiver receiver, receiver2;

    boolean play_resume = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        ButterKnife.bind(this);


        Bundle bundle = getIntent().getBundleExtra("bundle");
        list = (ArrayList<SubActivityModel>) bundle.getSerializable("songs_list");
        index = bundle.getInt("pos");

        getSupportActionBar().setTitle("Title");


        setAlbumArt(list.get(index).album_art, list.get(index).song_name);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio(list, index);
                play_resume = true;
            }
        });

        ivSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (index > -1 &&  index < list.size()){
                    int i = ++index;
                    playAudio(list,  i);
                    setAlbumArt(list.get(i).album_art, list.get(i).song_name);


                }
            }
        });

        ivSkipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (index <= list.size() && index >0){
                    int i = --index;
                    playAudio(list,  i);
                    setAlbumArt(list.get(i).album_art, list.get(i).song_name);

                }

            }
        });
    }

    private void setAlbumArt(String album_art, String song_name) {

        if (album_art != null)
            imageAlbumArt.setImageURI(Uri.parse(album_art));
        else
            imageAlbumArt.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio1));

        tvSongName.setText(song_name);

    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(ActivityPlaying.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };



    public void playAudio(ArrayList<SubActivityModel> audioList, int audioIndex) {

        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);

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

//    @Override
//    public void pauseAudio() {
////        player.pauseMedia();
//        player.setPauseToNotification();
//    }
//
//    @Override
//    public void resumeAudio() {
////        player.resumeMedia();
//        player.setResumeToNotification();
//    }


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
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
//            player.stopSelf();

            Toast.makeText(ActivityPlaying.this, "Service unBound", Toast.LENGTH_SHORT).show();

//            serviceBound = false;
        }
    }
}
