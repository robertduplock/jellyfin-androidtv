package org.jellyfin.androidtv.playback.overlay;

import android.os.Bundle;

import androidx.leanback.app.PlaybackSupportFragment;

import org.jellyfin.androidtv.TvApp;
import org.jellyfin.androidtv.playback.CustomPlaybackOverlayFragment;
import org.jellyfin.androidtv.playback.PlaybackController;
import org.jellyfin.apiclient.model.dto.BaseItemDto;

public class LeanbackOverlayFragment extends PlaybackSupportFragment {

    private PlaybackController playbackController;
    private CustomPlaybackTransportControlGlue playerGlue;
    private VideoPlayerAdapter playerAdapter;
    private boolean shouldShowOverlay = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TvApp application = TvApp.getApplication();
        PlaybackController playbackController = application.getPlaybackController();

        playerAdapter = new VideoPlayerAdapter(playbackController);
        playerGlue = new CustomPlaybackTransportControlGlue(getContext(), playerAdapter, playbackController, this);
        playerGlue.setHost(new CustomPlaybackFragmentGlueHost(this));
    }

    public void initFromView(PlaybackController playbackController, CustomPlaybackOverlayFragment customPlaybackOverlayFragment) {
        this.playbackController = playbackController;
        playerGlue.setSeekProvider(new CustomSeekProvider(playerAdapter.getDuration()));
        playerGlue.setInitialPlaybackDrawable();
        playerGlue.setSeekEnabled(playerAdapter.canSeek());
        playerAdapter.setMasterOverlayFragment(customPlaybackOverlayFragment);
    }

    @Override
    public void showControlsOverlay(boolean runAnimation) {
        if (shouldShowOverlay) {
            super.showControlsOverlay(runAnimation);
        }
    }

    public void updateCurrentPosition() {
        playerAdapter.updateCurrentPosition();
    }

    public void updatePlayState() {
        playerAdapter.updatePlayState();
    }

    public void setShouldShowOverlay(boolean shouldShowOverlay) {
        this.shouldShowOverlay = shouldShowOverlay;
    }

    public void hideOverlay() {
        hideControlsOverlay(true);
    }

    void setFading(boolean fadingEnabled) {
        playerAdapter.getMasterOverlayFragment().setFadingEnabled(fadingEnabled);
    }

    public void mediaInfoChanged() {
        BaseItemDto currentlyPlayingItem = playbackController.getCurrentlyPlayingItem();
        playerGlue.setTitle(currentlyPlayingItem.getName());
        playerGlue.invalidatePlaybackControls();
        recordingStateChanged();
    }

    public void recordingStateChanged() {
        playerGlue.recordingStateChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        playerAdapter.getMasterOverlayFragment().onPause();
    }
}
