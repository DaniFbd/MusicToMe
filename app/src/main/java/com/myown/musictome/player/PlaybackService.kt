package com.myown.musictome.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.myown.musictome.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    @Inject lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @UnstableApi
    override fun onCreate() {
        super.onCreate()

        //Silenciamos logs de auditoria
        val attributionContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            createAttributionContext("music_playback")
        } else {
            this
        }

        val channelId = "music_playback_channel"
        val channel = android.app.NotificationChannel(
            channelId,
            "Reproducción de Música",
            android.app.NotificationManager.IMPORTANCE_LOW // Cambiado a LOW para evitar ruidos al arrancar
        ).apply {
            description = "Controles de música"
            setShowBadge(false)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(attributionContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(attributionContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationProvider = DefaultMediaNotificationProvider.Builder(attributionContext)
            .setChannelId(channelId)
            .build()
        setMediaNotificationProvider(notificationProvider)

        mediaSession = MediaSession.Builder(attributionContext, player)
            .setCallback(MediaSessionCallback())
            .setSessionActivity(pendingIntent)
            .build()

        player.addListener(object : androidx.media3.common.Player.Listener {
             override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                mediaSession?.setCustomLayout(emptyList())
            }
        })
    }

    @UnstableApi
    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        super.onUpdateNotification(session, startInForegroundRequired)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        player.stop()
        player.release()
        mediaSession?.run {
            release()
            mediaSession = null
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        super.onTaskRemoved(rootIntent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    private class MediaSessionCallback : MediaSession.Callback {
        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val availableSessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .build()
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(availableSessionCommands)
                .setAvailablePlayerCommands(MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS)
                .build()
        }
    }

}

