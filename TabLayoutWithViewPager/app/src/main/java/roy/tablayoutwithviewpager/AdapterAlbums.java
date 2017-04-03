package roy.tablayoutwithviewpager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by venkatesh on 20/3/17.
 */

class AdapterAlbums extends RecyclerView.Adapter<AdapterAlbums.SimpleHolder>{

    Activity activity;
    ArrayList<DataObjects> songsData;
    ArrayList<AlbumObjects> album_list;
    public AdapterAlbums(Activity activity, ArrayList<DataObjects> songsData, ArrayList<AlbumObjects> album_list) {

        this.activity = activity;
        this.songsData = songsData;
        this.album_list = album_list;
    }

    @Override
    public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_list_row, parent, false);
        return new AdapterAlbums.SimpleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleHolder holder, final int position) {

        holder.tvSongName.setText("Albums- "+ album_list.get(position).album_id+" - "+album_list.get(position).album_name);
        if (album_list.get(position).album_art != null)
             holder.album_iv.setImageURI(Uri.parse( album_list.get(position).album_art ));
        else
            holder.album_iv.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.audio1));

        holder.tvSongName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<SubActivityModel> songs_list =  addSongsForEachAlbum(album_list.get(position).album_id);
                Intent intent = new Intent(activity, ArtistsSubActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("songs_list", songs_list);
                intent.putExtra("bundle", bundle);

//                intent.putStringArrayListExtra("songs_list", songs_list);
                activity.startActivity(intent);

            }
        });

    }



    private ArrayList<SubActivityModel> addSongsForEachAlbum(int album_id) {

        ArrayList<SubActivityModel> songs_list = new ArrayList<>();

        for (DataObjects songData : songsData) {
            if (album_id == songData.album_id) {
                SubActivityModel obj = new SubActivityModel();
                obj.song_id = songData.song_id;
                obj.song_name = songData.songname;
                obj.song_uri = songData.fullpath;
                obj.album_art = songData.albumArt;
                obj.fromNotification = false;
                obj.is_play = false;
                songs_list.add(obj);

            }
        }

        return songs_list;
    }

    @Override
    public int getItemCount() {
        if (album_list!=null)
            return album_list.size();
        else
            return 0;
    }

    public class SimpleHolder extends RecyclerView.ViewHolder {
        TextView tvSongName;
        ImageView album_iv;
        public SimpleHolder(View itemView) {
            super(itemView);

            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            album_iv = (ImageView) itemView.findViewById(R.id.imageView);

        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
