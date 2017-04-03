package roy.tablayoutwithviewpager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

/**
 * Created by venkatesh on 20/3/17.
 */

class ArtistsSubAdapter extends RecyclerView.Adapter<ArtistsSubAdapter.SampleViewHolder>{

    ArrayList<SubActivityModel> songs_list;
    Activity act;
    InterfacePlaySongs interfacePlaySongs;
    int flag=-1;
    SampleViewHolder holder;
    public ArtistsSubAdapter(ArrayList<SubActivityModel> songs_list, InterfacePlaySongs artistsSubActivity, Activity activity) {
        this.songs_list = songs_list;
        interfacePlaySongs =  artistsSubActivity;
        act = activity;
    }



    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_list_row, parent, false);
        return new ArtistsSubAdapter.SampleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SampleViewHolder holder, final int position) {
        this.holder = holder;
        holder.tvSongName.setText(songs_list.get(position).song_name);


//        if (songs_list.get(position).isVisible){
//            holder.iv_play.setVisibility(View.GONE);
//            holder.iv_pause.setVisibility(View.GONE);
//            holder.itemView.setEnabled(true);
//        }
//        else if (songs_list.get(position).isPauseVisible)
//        {
//            holder.iv_pause.setVisibility(View.VISIBLE);
//            holder.iv_play.setVisibility(View.GONE);
////            holder.itemView.setEnabled(false);
//
//
//        }
//        else if (songs_list.get(position).isResumeVisible){
//            holder.iv_pause.setVisibility(View.GONE);
//            holder.iv_play.setVisibility(View.VISIBLE);
//        }
//
//        if (songs_list.get(position).isNextPrevious){
//            holder.iv_pause.setVisibility(View.VISIBLE);
//            holder.iv_play.setVisibility(View.GONE);
//            holder.itemView.setEnabled(true);
//
//        }

        if (songs_list.get(position).album_art != null)
            holder.imageView.setImageURI(Uri.parse( songs_list.get(position).album_art ));
        else
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.audio1));



        if(flag==position){
            if(!songs_list.get(position).is_play){
                holder.iv_pause.setVisibility(View.GONE);
                holder.iv_play.setVisibility(View.VISIBLE);
            }
            else{
                holder.iv_play.setVisibility(View.GONE);
                holder.iv_pause.setVisibility(View.VISIBLE);
            }


        }else{
            holder.iv_play.setVisibility(View.GONE);
            holder.iv_pause.setVisibility(View.GONE);
            holder.itemView.setEnabled(true);

        }

        if (songs_list.get(position).fromNotification){
            holder.iv_play.setVisibility(View.GONE);
            holder.iv_pause.setVisibility(View.GONE);
            holder.itemView.setEnabled(true);
        }

        holder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interfacePlaySongs!=null){

                    interfacePlaySongs.resumeAudio();
                    SubActivityModel model = songs_list.get(position);
                    model.is_play = true;
                    songs_list.set(position, model);
                    notifyItemChanged(position);
                }

            }
        });


        holder.iv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interfacePlaySongs!=null){

                    interfacePlaySongs.pauseAudio();

                    SubActivityModel model = songs_list.get(position);
                    model.is_play = false;
                    songs_list.set(position, model);
                    notifyItemChanged(position);


                }
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag>-1){
                    if (flag != position)
                          updateItem(flag);
                }

                if (interfacePlaySongs!=null){
                    interfacePlaySongs.playAudio(songs_list, position);
//                    holder.iv_pause.setVisibility(View.VISIBLE);
//                    holder.iv_play.setVisibility(View.GONE);
                    flag = position;
                    holder.itemView.setEnabled(false);



                    SubActivityModel model = songs_list.get(position);
                    model.is_play = true;
                    model.fromNotification = false;
                    songs_list.set(position, model);
                    notifyItemChanged(position);


                }


            }
        });


//            musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
//            musicSrv.playSong();


    }

    @Override
    public int getItemCount() {
        return songs_list.size();
    }

    public void updateItem(int s) {

        SubActivityModel model = songs_list.get(s);
        model.isVisible = true;
        model.isNextPrevious = false;
        model.is_play = false;
        songs_list.set(s, model);
        notifyItemChanged(s);

    }

    /***
     * update controls for adapter item from notification.
     * @param s index
     * @param action play or pause
     */
    public void updateControls(int s, String action) {

        if (action.equals("ACTION_NEXT")){

            SubActivityModel model1 = songs_list.get(s);
            model1.isNextPrevious = true;
            model1.is_play = true;
            songs_list.set(s, model1);
            notifyItemChanged(s);
            flag = s;

        }else {
            SubActivityModel model = songs_list.get(s);
            if (action.equals("ACTION_PLAY")){
                model.isPauseVisible = true;
                model.isResumeVisible = false;
                model.isVisible = false;
                model.is_play = true;


            }else if (action.equals("ACTION_PAUSE")){
                model.isResumeVisible = true;
                model.isPauseVisible = false;
                model.isVisible = false;
//                model.fromNotification= true;
                model.is_play = false;

            }
            songs_list.set(s, model);
            notifyItemChanged(s);

        }




    }

    public void refreshList(int s, String action) {

        SubActivityModel model = songs_list.get(s);
        model.isVisible = true;
        model.isNextPrevious = false;
        model.is_play = false;
        model.fromNotification = true;
        songs_list.set(s, model);
        notifyItemChanged(s);

    }

    public class SampleViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        ImageView imageView, iv_pause, iv_play;
        public SampleViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            iv_pause = (ImageView) itemView.findViewById(R.id.iv_control_pause);
            iv_play = (ImageView) itemView.findViewById(R.id.iv_control_play);

        }
    }


    public interface InterfacePlaySongs{

        void playAudio(ArrayList<SubActivityModel> mediaFile, int index);
        void pauseAudio();
        void resumeAudio();
    }
}
