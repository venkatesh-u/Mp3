package roy.tablayoutwithviewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by kundan on 10/16/2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<DataObjects> songsData;

    public PagerAdapter(FragmentManager fm, ArrayList<DataObjects> songsData) {
        super(fm);
        this.songsData = songsData;

    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;

        switch (position){
            case 0:
//frag= ArtistsFragment.init(songsData, setArtists(songsData));
                ArtistsFragment afrag = new ArtistsFragment();
                Bundle b = new Bundle();
                b.putSerializable("songsData", songsData);
                b.putSerializable("artistsObjects", setArtists(songsData));
                afrag.setArguments(b);
                return afrag;
            case 1:
//frag= AlbumFragment.init(songsData, setAlbums(songsData));
                AlbumFragment alfrag = new AlbumFragment();
                Bundle b2 = new Bundle();
                b2.putSerializable("songsData", songsData);
                b2.putSerializable("albumObjects", setAlbums(songsData));
                alfrag.setArguments(b2);
                return alfrag;
            case 2:
//frag= AllSongsFragment.init(songsData);
                AllSongsFragment allfrag = new AllSongsFragment();
                Bundle b3 = new Bundle();
                b3.putSerializable("songsData", songsData);
                allfrag.setArguments(b3);
                return allfrag;
            default:
                return null;
        }




//        switch (position){
//            case 0:
//                frag= ArtistsFragment.init(songsData,  setArtists(songsData));
//                break;
//            case 1:
//                frag= AlbumFragment.init(songsData, setAlbums(songsData));
//                break;
//            case 2:
//                frag= AllSongsFragment.init(songsData);
//                break;
//        }
//        return frag;
    }

    //    @Override
//    public Object instantiateItem(ViewGroup view, int position) {
//        EventFragment fragment = fragments.get(position);
//        if (fragment != null) {
//            return fragment;
//        }
//        else {
//            return super.instantiateItem(view, position);
//        }
//    }


    private ArrayList<AlbumObjects> setAlbums(ArrayList<DataObjects> songsData) {

        ArrayList<AlbumObjects> albums = new ArrayList<>();
        for (DataObjects list2 : songsData) {
            // Loop arrayList1 items
            boolean found = false;
            for (AlbumObjects list1 : albums) {
                if (list2.album_id == list1.album_id) {
                    found = true;
                }
            }
            if (!found) {
                AlbumObjects albumObjects = new AlbumObjects();
                albumObjects .album_id = list2.album_id;
                albumObjects .album_name = list2.albumname;
                albumObjects.album_art = list2.albumArt;
                albums.add(albumObjects );
            }
        }

        Log.d("Artists Size: ", albums.size() + "");
        return albums;
    }


    // Separate Artists object for artist ids and artist names, removed redundancy.
    private ArrayList<ArtistsObjects> setArtists(ArrayList<DataObjects> songsData) {

        ArrayList<ArtistsObjects> artists = new ArrayList<>();
        for (DataObjects list2 : songsData) {
            // Loop arrayList1 items
            boolean found = false;
            for (ArtistsObjects list1 : artists) {
                if (list2.artist_id == list1.artist_id) {
                    found = true;
                }
            }
            if (!found) {
                ArtistsObjects artistsObjects = new ArtistsObjects();
                artistsObjects.artist_id = list2.artist_id;
                artistsObjects.artist = list2.artistname;
                artistsObjects.album_art = list2.albumArt;
                artists.add(artistsObjects);
            }
        }

        Log.d("Artists Size: ", artists.size() + "");
        return artists;

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="ARTISTS";
                break;
            case 1:
                title="ALBUMS";
                break;
            case 2:
                title="ALL SONGS";
                break;
        }
        return title;
    }
}
