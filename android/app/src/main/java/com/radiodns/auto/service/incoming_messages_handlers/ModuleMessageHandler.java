package com.radiodns.auto.service.incoming_messages_handlers;

import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.radiodns.R;
import com.radiodns.auto.messages.AutoServiceMessages;
import com.radiodns.auto.service.MediaService;

/**
 * Incoming message handler class to handle communication from the React Native module and this service.
 */
public class ModuleMessageHandler extends Handler {
    private MediaService service;

    public ModuleMessageHandler(MediaService service) {
        this.service = service;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case AutoServiceMessages.REGISTER_CLIENT:
                service.getClients().add(msg.replyTo);
                break;
            case AutoServiceMessages.UNREGISTER_CLIENT:
                service.getClients().remove(msg.replyTo);
                break;
            case AutoServiceMessages.RESET_DB:
                service.getDb().autoNodeDAO().nukeTable();
                break;
            case AutoServiceMessages.REFRESH_FROM_DB:
                Log.i("[" + this.getClass().getName() + "]", "ON refresh from db. ");
                break;
            case AutoServiceMessages.UPDATE_MEDIA_STATE_TO_PLAYING:
                service.setMediaSessionState(PlaybackState.STATE_PLAYING);
                break;
            case AutoServiceMessages.UPDATE_MEDIA_STATE_TO_BUFFERING:
                service.setMediaSessionState(PlaybackState.STATE_BUFFERING);
                break;
            case AutoServiceMessages.UPDATE_MEDIA_STATE_TO_ERROR:
                PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
                stateBuilder.setState(PlaybackState.STATE_ERROR, 0, 0);
                stateBuilder.setErrorMessage(1, service.getApplicationContext().getResources().getString(R.string.error_media_format_unsuported));
                service.getSession().setPlaybackState(stateBuilder.build());
                break;
            case AutoServiceMessages.UPDATE_CURRENT_CHANNEL_ID:
                service.setCurrentMediaID(msg.getData().getString("channelId"));
                service.setNodeData();
                break;
            default:
                super.handleMessage(msg);
        }
    }
}