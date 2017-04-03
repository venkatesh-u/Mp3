package roy.tablayoutwithviewpager;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class AlbumFragment extends Fragment {

    AdapterAlbums adapterAllSongs;
    RecyclerView recyclerView_album;
    ArrayList<DataObjects> songsData;
    ArrayList<AlbumObjects> album_list;


    public AlbumFragment() {
        // Required empty public constructor
        this.songsData = songsData;
        this.album_list = album_list;
    }


    public static AlbumFragment init(ArrayList<DataObjects> songsData, ArrayList<AlbumObjects> albumObjects) {

        AlbumFragment frag = new AlbumFragment();
        Bundle b = new Bundle();
        b.putSerializable("songsData", songsData);
        b.putSerializable("albumObjects", albumObjects);
        frag.setArguments(b);
        return frag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_albums, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView_album = (RecyclerView) getActivity().findViewById(R.id.recycler_view_albums);



        songsData = (ArrayList<DataObjects>) getArguments().getSerializable("songsData");
        album_list = (ArrayList<AlbumObjects>) getArguments().getSerializable("albumObjects");

        Log.d("Albums data size: ", songsData.size() + "");

        adapterAllSongs = new AdapterAlbums(getActivity(), songsData, album_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView_album.setLayoutManager(mLayoutManager);
        recyclerView_album.setItemAnimator(new DefaultItemAnimator());
        recyclerView_album.setAdapter(adapterAllSongs);
    }



}
