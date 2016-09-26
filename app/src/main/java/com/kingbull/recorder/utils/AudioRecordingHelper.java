package com.kingbull.recorder.utils;

import android.media.AudioFormat;
import android.media.MediaRecorder;

import java.io.File;
import java.lang.ref.WeakReference;

import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

import static com.kingbull.recorder.widget.MicrophoneVolumeView.ASR_SAMPLE_RATE;

/**
 * Created by seanzhou on 9/21/16.
 * <p>
 * An util class for handling recording audio via mic.
 * <p>
 */

public class AudioRecordingHelper {
    public static final int FREQUENCY = 44100;

    private final File outputFile;
    private final WeakReference<RecordListener> listenerRef;

    /***
     * Callback for audio recording.
     */
    public interface RecordListener {
        void onAudioVolumeChange(float value);

        File getOutputFile();
    }

    private Recorder recorder;

    /***
     * Constructor
     *
     * @param listener {@link RecordListener}
     */
    public AudioRecordingHelper(RecordListener listener) {
        //Assert.checkNotNull(listener);

        listenerRef = new WeakReference<RecordListener>(listener);
        outputFile = listener.getOutputFile();

        recorder = OmRecorder.wav(
                new PullTransport.Default(
                        mic(),
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                float maxPeak = (float) audioChunk.maxAmplitude();

                                // take the sample rate as baseline
                                maxPeak = maxPeak * ASR_SAMPLE_RATE / 100;

                                if (listenerRef.get() != null)
                                    listenerRef.get().onAudioVolumeChange(maxPeak);
                            }
                        }
                ),
                outputFile
        );
    }

    private AudioSource mic() {
        return new AudioSource.Smart(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, FREQUENCY);
    }

    /***
     * Starts recording
     */
    public void startRecording() {
        recorder.startRecording();
    }

    /***
     * Stops recording
     */
    public void stopRecording() {
        recorder.stopRecording();
    }
}
