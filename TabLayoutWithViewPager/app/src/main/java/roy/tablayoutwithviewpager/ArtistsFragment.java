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
public class ArtistsFragment extends Fragment {

    AdapterArtists adapterAllSongs;
    RecyclerView recyclerView_artists;
    ArrayList<DataObjects> songsData;
    ArrayList<ArtistsObjects> artists;


    public ArtistsFragment() {
        // Required empty public constructor
        this.songsData = songsData;
        this.artists = artists;
    }


    public static ArtistsFragment init(ArrayList<DataObjects> songsData, ArrayList<ArtistsObjects> artistsObjects) {
        ArtistsFragment frag = new ArtistsFragment();
        Bundle b = new Bundle();
        b.putSerializable("songsData", songsData);
        b.putSerializable("artistsObjects", artistsObjects);
        frag.setArguments(b);
        return frag;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView_artists = (RecyclerView) getActivity().findViewById(R.id.recycler_view_artists);

        songsData = (ArrayList<DataObjects>) getArguments().getSerializable("songsData");
        Log.d("Artists data size: ", songsData.size() + "");


        artists = (ArrayList<ArtistsObjects>) getArguments().getSerializable("artistsObjects");
        adapterAllSongs = new AdapterArtists(getActivity(), songsData, artists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView_artists.setLayoutManager(mLayoutManager);
        recyclerView_artists.setItemAnimator(new DefaultItemAnimator());
        recyclerView_artists.setAdapter(adapterAllSongs);
    }


}
