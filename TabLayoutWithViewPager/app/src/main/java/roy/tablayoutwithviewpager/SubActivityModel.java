package roy.tablayoutwithviewpager;

import java.io.Serializable;

/**
 * Created by venkatesh on 22/3/17.
 */

public class SubActivityModel implements Serializable {

    public String song_name, album_art, song_uri;
    public int song_id;
    public boolean isVisible;
    public boolean isPauseVisible;
    public boolean isResumeVisible;
    public boolean isNextPrevious, fromNotification, is_play, is_resume ;
}
