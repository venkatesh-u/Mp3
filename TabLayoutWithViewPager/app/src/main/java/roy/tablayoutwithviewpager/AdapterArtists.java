package roy.tablayoutwithviewpager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by venkatesh on 17/3/17.
 */

class AdapterArtists extends RecyclerView.Adapter<AdapterArtists.SimpleHolder> {

    private final ArrayList<ArtistsObjects> artists;
    ArrayList<DataObjects> songsData;
    Activity act;
//    public AdapterAllSongs(Activity context, ArrayList<String> songNames) {
//        this.songNames = songNames;
//        act =  context;
//
//    }

    public AdapterArtists(FragmentActivity activity, ArrayList<DataObjects> songsData, ArrayList<ArtistsObjects> artists) {

        act = activity;
        this.songsData = songsData;
        this.artists = artists;

    }

    @Override
    public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_list_row, parent, false);
        return new SimpleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleHolder holder, final int position) {
//        holder.tvSongName.setText("Artists- "+songsData.get(position).artistname +", "+songsData.get(position).artist_id);
        holder.tvSongName.setText("Artists- "+String.valueOf(artists.get(position).artist_id) + " - "+ artists.get(position).artist);

        if (artists.get(position).album_art != null)
            holder.imageView.setImageURI(Uri.parse( artists.get(position).album_art ));
        else
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.audio1));

        holder.tvSongName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               ArrayList<SubActivityModel> songs_list = addSongsForEachArtist(artists.get(position).artist_id);

                Intent intent = new Intent(act, ArtistsSubActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("songs_list", songs_list);
                intent.putExtra("bundle", bundle);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("artists_object", artists.get(position));
//                intent.putExtra("artists_bundle", bundle);
                act.startActivity(intent);

            }
        });

    }

    private ArrayList<SubActivityModel> addSongsForEachArtist(int artist_id) {

        ArrayList<SubActivityModel> songs_list = new ArrayList<>();

        for (DataObjects songData : songsData) {
            if (artist_id == songData.artist_id) {
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
        }

        return songs_list;
    }


    @Override
    public int getItemCount() {
        Log.d("Count: ", songsData.size() + "");

        return artists.size();
    }

    public class SimpleHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        ImageView imageView;
        public SimpleHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);


        }
    }
}