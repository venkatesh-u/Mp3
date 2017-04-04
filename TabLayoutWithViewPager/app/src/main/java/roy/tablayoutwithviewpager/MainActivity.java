package roy.tablayoutwithviewpager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    ViewPager  pager;
    TabLayout tabLayout;
    String[] STAR = { "*" };
    int totalSongs;
    ArrayList<DataObjects> SongsData;
    PagerAdapter pagerAdapter;
    FragmentManager manager;
   public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1019;
   public Intent playerIntent;
    boolean doubleBackToExitPressedOnce = false;

    private LocalBroadcastManager receiver_updateFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        SongsData = new ArrayList<>();




        checkPermissions();



        manager = getSupportFragmentManager();
        //pagerAdapter = new PagerAdapter(manager, SongsData);
     /*   pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0);
//        pager.setOffscreenPageLimit(3);
//        pager.setCurrentItem(0);
        tabLayout.setupWithViewPager(pager);*/
        // mTabLayout.setupWithViewPager(mPager1);
//        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//        tabLayout.setTabsFromPagerAdapter(adapter);


//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//
//        new DownloadFilesTask().execute(path+"/Download/", null, null);


//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ListAllSongs();
//            }
//        });
//        thread.start();



        Intent playerIntent = new Intent(this, MediaPlayerService.class);
        startService(playerIntent);
    }


    public Intent startServiceMain(){
        return playerIntent;
    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<HashMap<String,String>> getPlayList(String rootPath) {

        ArrayList<HashMap<String,String>> fileList = new ArrayList<>();
        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }

            Log.d("File Size: ", fileList.size() + "");

            return fileList;
        } catch (Exception e) {
            return null;
        }
    }


//    private class DownloadFilesTask extends AsyncTask<String, String, ArrayList<String>> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected ArrayList<String> doInBackground(String... path) {
//            Log.e("doInBackground"," ok" );
//            ArrayList<String> songsPaths = new ArrayList<>();
//            ArrayList<HashMap<String,String>> songList = getPlayList(path[0]);
//
//            if(songList!=null){
//                for(int i=0;i<songList.size();i++){
//                    String fileName=songList.get(i).get("file_name");
//                    String filePath=songList.get(i).get("file_path");
//                    //here you will get list of file name and file path that present in your device
//
//                    songsPaths.add(fileName);
//                    onProgressUpdate(fileName +", "+filePath);
//                }
//            }
//            return songsPaths;
//        }
//
//        protected void onProgressUpdate(String names) {
//            Log.e("onProgressUpdate"," ok" );
//            Log.e("file details "," name, path ="+names );
//        }
//
//        protected void onPostExecute(ArrayList<String> list) {
//            Log.e("onPostExecute"," ok" +list);
//
//            Toast.makeText(MainActivity.this, "list size: "+list.size(), Toast.LENGTH_SHORT).show();
////            setAdapter(list);
//        }
//
//    }


//    private void setAdapter(ArrayList<String> list) {
//        adapter = new RecyclerViewAdapter(context, list);
//        recyclerView.setAdapter(adapter);
//
//    }
//


    private class GetFiles extends AsyncTask<String, String, ArrayList<DataObjects>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<DataObjects> doInBackground(String... path) {
            return  ListAllSongs();

        }

        protected void onProgressUpdate(String names) {
            Log.e("onProgressUpdate"," ok" );
            Log.e("file details "," name, path ="+names );
        }

        protected void onPostExecute(ArrayList<DataObjects> list) {
            Log.e("onPostExecute"," ok" +list);


            Toast.makeText(MainActivity.this, "list size: "+list.size(), Toast.LENGTH_SHORT).show();
//            setAdapter(list);
            pagerAdapter = new PagerAdapter(manager, SongsData);
            pager.setAdapter(pagerAdapter);
            //pager.setCurrentItem(0);
            //pager.setOffscreenPageLimit(1);
//        pager.setCurrentItem(0);
            tabLayout.setupWithViewPager(pager);
            //tabLayout.setupWithViewPager(pager);

        }

    }


public void checkPermissions(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts

                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique

                return;
            }else {
                new GetFiles().execute("", null, null);

            }
        } else {

        new GetFiles().execute("", null, null);
    }

}


    public ArrayList<DataObjects> ListAllSongs()
    {

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        ArrayList<String> albums = new ArrayList<>();
//        if (MusicUtils.isSdPresent()) {
        Cursor cursor = getContentResolver().query(allsongsuri, STAR, selection, null, null);

        if(cursor!=null)
            totalSongs = cursor.getCount();

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        DataObjects dataObjects = new DataObjects();
                        String songname = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        int song_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        Log.d("songName: ",songname +", id: "+song_id);
                        dataObjects.song_id = song_id;
                        dataObjects.songname = songname;

                        String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        Log.d("fullPath: ",fullpath  );
                        dataObjects.fullpath = fullpath;

                        String albumname = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        int album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        albums.add(albumname);
                        Log.d("albumName: ",albumname +", id: "+album_id);
                        dataObjects.album_id = album_id;
                        dataObjects.albumname = albumname;

                        String artistname = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        int artist_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                        Log.d("artistname: ",artistname +", id: "+artist_id);
                        dataObjects.artist_id = artist_id;
                        dataObjects.artistname =artistname;

                        //AlbumArt URI
                        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, album_id);
                        Log.d("album " + album_id + ": " , albumArtUri.toString() );


                       if (getCoverArtPath(album_id,  this) != null){
                           dataObjects.albumArt =  albumArtUri.toString();
                       }else {
                           dataObjects.albumArt =  null;
                       }

                        SongsData.add(dataObjects);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
//        }
        return SongsData;
    }

    /***
     *It returns null if Content URI is not found from sdcard.
     * @param albumId
     * @param context
     * @return
     */
    private static String getCoverArtPath(long albumId, Context context) {

        Cursor albumCursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    new GetFiles().execute("", null, null);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public Bitmap getAlbumart(int album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = this.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        StorageUtil storage = new StorageUtil(getApplicationContext());
        storage.storeFlag(true);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }



    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        Handler handler =  new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }


}
