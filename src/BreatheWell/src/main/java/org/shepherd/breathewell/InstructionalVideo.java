package org.shepherd.breathewell;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by scott on 7/18/2015.
 */
public class InstructionalVideo extends Activity {


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);


        setContentView(R.layout.instructionalvideo);

//        Intent i = new Intent();
//        i.setAction("com.google.glass.action.VIDEOPLAYER");
//        i.putExtra("video_url", "https://m.youtube.com/watch?v=yCmsZUN4r_s");
//        startActivity(i);
//        finish();

        //Somewhere set the video name variable
        String video_name="instructions.mp4";
//setup up and play video

        final VideoView videoView=(VideoView)findViewById(R.id.video);
        videoView.setVisibility(View.VISIBLE);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/"  + R.raw.instructions);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
               finish();
            }
        });
        videoView.setBackgroundColor(0);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                int position = mediaPlayer.getCurrentPosition();
                if (position == 0) {
                    try {
                        videoView.requestFocus();
                        videoView.start();
                    } catch (Exception e) {
                        System.out.printf("Error playing video %s\n", e);
                    }
                } else {
                    videoView.pause();
                }

            }
        });
    }
}
