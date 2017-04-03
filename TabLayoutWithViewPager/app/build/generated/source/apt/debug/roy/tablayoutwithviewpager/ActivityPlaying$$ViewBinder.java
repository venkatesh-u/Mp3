// Generated code from Butter Knife. Do not modify!
package roy.tablayoutwithviewpager;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ActivityPlaying$$ViewBinder<T extends roy.tablayoutwithviewpager.ActivityPlaying> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558524, "field 'imageAlbumArt'");
    target.imageAlbumArt = finder.castView(view, 2131558524, "field 'imageAlbumArt'");
    view = finder.findRequiredView(source, 2131558526, "field 'ivSkipPrevious'");
    target.ivSkipPrevious = finder.castView(view, 2131558526, "field 'ivSkipPrevious'");
    view = finder.findRequiredView(source, 2131558527, "field 'ivPlay'");
    target.ivPlay = finder.castView(view, 2131558527, "field 'ivPlay'");
    view = finder.findRequiredView(source, 2131558528, "field 'ivSkipNext'");
    target.ivSkipNext = finder.castView(view, 2131558528, "field 'ivSkipNext'");
    view = finder.findRequiredView(source, 2131558525, "field 'controls'");
    target.controls = finder.castView(view, 2131558525, "field 'controls'");
    view = finder.findRequiredView(source, 2131558523, "field 'tvSongName'");
    target.tvSongName = finder.castView(view, 2131558523, "field 'tvSongName'");
  }

  @Override public void unbind(T target) {
    target.imageAlbumArt = null;
    target.ivSkipPrevious = null;
    target.ivPlay = null;
    target.ivSkipNext = null;
    target.controls = null;
    target.tvSongName = null;
  }
}
