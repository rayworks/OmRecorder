package com.kingbull.recorder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kingbull.recorder.utils.AudioRecordingHelper;
import com.kingbull.recorder.widget.MicrophoneVolumeView;

import java.io.File;
import java.io.IOException;

import omrecorder.Recorder;

/**
 * Created by seanzhou on 9/26/16.
 */

public class MicrophoneRecordActivity extends AppCompatActivity implements AudioRecordingHelper.RecordListener {

    private Button recordBtn;
    private MicrophoneVolumeView microphoneView;
    private Recorder recorder;
    private AudioRecordingHelper recordingHelper;
    private File destFile;
    private ImageView playButton;

    private MediaPlayer mediaPlayer;
    private boolean playerInited;
    private boolean actionDownOccurred;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);

        recordBtn = (Button) findViewById(R.id.recorder_button);
        microphoneView = (MicrophoneVolumeView) findViewById(R.id.microphone_volume);

        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!recordBtn.isEnabled())
                    return false;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    actionDownOccurred = true;
                    recordBtn.setPressed(true);
                    onRecordStart();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // dump the action up if there is not a valid down event.
                    if (actionDownOccurred) {
                        recordBtn.setPressed(false);
                        onRecordComplete();
                        return true;
                    }
                    return false;
                } else {
                    recordBtn.setPressed(false);
                    onRecordCancel();
                    return false;
                }
            }
        });

        playButton = (ImageView) findViewById(R.id.record_play);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay();
            }
        });
        setupRecorder();
    }

    private void onPlay() {
        if (destFile != null && destFile.exists()) {

            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            // disable the audio record for accessing the file concurrently when playing audio.
            recordBtn.setEnabled(false);

            if (!playerInited) {
                try {
                    mediaPlayer.setDataSource(this, Uri.fromFile(destFile));
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(
                            new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mediaPlayer.reset();
                                    playerInited = false;

                                    recordBtn.setEnabled(true);
                                }
                            }
                    );

                    playerInited = true;

                } catch (IOException e) {
                    e.printStackTrace();

                    recordBtn.setEnabled(true);
                }
            } else {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mediaPlayer != null) {
            if (playerInited)
                mediaPlayer.pause();
            mediaPlayer.release();
        }
    }

    private void onRecordCancel() {

    }

    private void onRecordComplete() {
        if (recordingHelper != null)
            recordingHelper.stopRecording();

        microphoneView.setVisibility(View.INVISIBLE);
        microphoneView.setProportion(0);
    }

    private void onRecordStart() {
        microphoneView.setVisibility(View.VISIBLE);

        if (recordingHelper != null)
            recordingHelper.startRecording();
    }

    private void setupRecorder() {
        if (recordingHelper == null) {
            recordingHelper = new AudioRecordingHelper(this);
        }
    }

    private File getFile() {
        if (destFile == null) {
            destFile = new File(Environment.getExternalStorageDirectory(), "demo2.wav");
        }
        return destFile;
    }

    @Override
    public void onAudioVolumeChange(float value) {
        microphoneView.setProportion(value);
    }

    @Override
    public File getOutputFile() {
        return getFile();
    }
}
