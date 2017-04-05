package roy.tablayoutwithviewpager;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import static roy.tablayoutwithviewpager.ArtistsSubActivity.Broadcast_PLAY_NEW_AUDIO;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class AllSongsFragment extends Fragment implements AdapterAllSongs.InterfacePlaySongs {


    RecyclerView recyclerView_allsongs;
    ArrayList<DataObjects> songsData;
    AdapterAllSongs adapterAllSongs;

    AdapterAllSongs adapter;

    ArrayList<SubActivityModel> songsList;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    String action_refresh = null;

    private MediaPlayerService player;
    boolean fragServiceBound = true;
    boolean isPlaying = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "audioplayer.PlayNewAudio";
    private BroadcastReceiver receiver, receiver2, receiver3, receiver_updateFragment;



    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            fragServiceBound = false;

            Toast.makeText(getActivity(), "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            fragServiceBound = true;
            Toast.makeText(getActivity(), "Service UnBound", Toast.LENGTH_SHORT).show();

        }
    };


//    public AllSongsFragment() {
//        // Required empty public constructor
//        this.songsData = songsData;
////        Log.d("songss..........", songsData.get(0).songname);
////        Log.d("songss..........", songsData.get(1).songname);
//    }
//
//    public static AllSongsFragment init(ArrayList<DataObjects> songsData) {
//        AllSongsFragment frag = new AllSongsFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("songsData", songsData);
//        frag.setArguments(b);
//        return frag;
//
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_all_songs, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        // Inflate the layout for this fragment
//        Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
//        getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        recyclerView_allsongs = (RecyclerView) getActivity().findViewById(R.id.recycler_view_allsongs);
        songsData = (ArrayList<DataObjects>) getArguments().getSerializable("songsData");



        Log.d("AllSongs data size: ", songsData.size() + "");

        songsList =  getAllSongs(songsData);



        adapter = new AdapterAllSongs(songsList, this, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView_allsongs.setLayoutManager(mLayoutManager);
        recyclerView_allsongs.setItemAnimator(new DefaultItemAnimator());
        recyclerView_allsongs.setAdapter(adapter);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int s = intent.getIntExtra("data", -1);
                // do something here.
                adapter.updateItem(s);
            }
        };

        // update adapter's view(controllers)from notification controllers.
        receiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int s = intent.getIntExtra("data", -1);
                String action = intent.getStringExtra("action");
                adapter.updateControls(s, action);

            }
        };

        // process is foreground but clear notification then stop song.
        receiver3 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int s = intent.getIntExtra("data", -1);
                action_refresh = intent.getStringExtra("action");
                adapter.refreshList(s, action_refresh);

            }
        };


        receiver_updateFragment =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //refresh Fragment3
                setAdapter();
            }
        };


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver), new IntentFilter("intent_filter_update"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver2), new IntentFilter("intent_filter_receiver2"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver3), new IntentFilter("intent_filter_receiver3"));


    }

    private void setAdapter() {
        adapter = new AdapterAllSongs(songsList, this, getActivity());
        recyclerView_allsongs.setAdapter(adapter);
    }


    private ArrayList<SubActivityModel> getAllSongs(ArrayList<DataObjects> songsData) {

        ArrayList<SubActivityModel> songs_list = new ArrayList<>();
        for (DataObjects songData : songsData) {
                SubActivityModel obj = new SubActivityModel();
                obj.song_id = songData.song_id;
                obj.song_name = songData.songname;
                obj.song_uri = songData.fullpath;
                obj.album_art = songData.albumArt;
                obj.isVisible = false;
                obj.isPauseVisible = false;
                obj.isResumeVisible = false;
                obj.isNextPrevious = false;
            obj.fromNotification = false;
            obj.is_play = false;
                songs_list.add(obj);
        }
        return songs_list;
    }


    @Override
    public void playAudio(ArrayList<SubActivityModel> audioList, int audioIndex) {
        //Check is service is active
        if (!fragServiceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getActivity());
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

            fragServiceBound = true;
            isPlaying = true;

        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getActivity());
//            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            getActivity().sendBroadcast(broadcastIntent);
        }


    }

    @Override
    public void pauseAudio() {

//        if (player==null){
//            Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
//            getActivity().startService(playerIntent);
//            getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        }
        player.setPauseToNotification();

    }

    @Override
    public void resumeAudio() {

//        if (player==null){
//            Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
//            getActivity().startService(playerIntent);
//            getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        }
        player.setResumeToNotification();
        opeNEqualizerDialogue();

    }
    private void opeNEqualizerDialogue() {
        AudioFxDemo audioFxDemo=new AudioFxDemo(getActivity(),Utils.mMediaPlayer);
        audioFxDemo.show();
        audioFxDemo.setCancelable(true);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", fragServiceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        fragServiceBound = savedInstanceState.getBoolean("ServiceState");
//    }


//    @Override
//    protected void onStop() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver2);
//
//        super.onStop();
//    }


    @Override
    public void onResume() {
        super.onResume();
//        fragServiceBound = false;
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver( (receiver_updateFragment), new IntentFilter("receiver_updateFragment") );

        if (fragServiceBound){
            Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
//            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            fragServiceBound = false;
            isPlaying = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isPlaying){

            getActivity().unbindService(serviceConnection);
            fragServiceBound = true;


        }else if (!fragServiceBound) {
            getActivity().unbindService(serviceConnection);
            //service is active
//            player.stopSelf();

            fragServiceBound = true;

        }
//        getActivity().unregisterReceiver(receiver_updateFragment);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
