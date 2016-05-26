package com.example.ebrahim_elgaml.ffmpeg_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


public class MainActivity extends AppCompatActivity {
    private TextView myTextView;
    private static final String VIDEO_PATH = Environment.getExternalStorageDirectory() + "/zoobetest/videos/";
    private static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/zoobetest/images/";
    private static final String[] engineVideoParams = new String[]{"-c:v",  "libx264", "-profile:v", "baseline", "-level", "3.0", "-b:v", "800k"
    , "-g", "10", "-qmin", "10", "-qmax", "51", "-i_qfactor", "0.71", "-qcomp", "0.6", "-me_method", "hex"
    , "-subq", "5", "-pix_fmt", "yuv420p"};
    private AlphaVideoView myVideoView;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;
    private  long startTime = System.currentTimeMillis();
    private int myCounter = 0;
    private  Uri videoURI;
    private  boolean isFinshied = false;
    private int position = 0;
    private boolean seek = false;
    private FFmpeg ffmpeg;
    private Button button1;
    private Button button2;
    private Button button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = (TextView)(findViewById(R.id.mTextView));
        myVideoView = (AlphaVideoView)(findViewById(R.id.videoView));
//        button1 = (Button)(findViewById(R.id.button));
//        button2 = (Button)(findViewById(R.id.button2));
//        button3 = (Button)(findViewById(R.id.button3));
//        button1.setBackgroundColor(Color.parseColor("#ccff99"));
//        button1.setTag(Color.parseColor("#ccff99"));
//        button2.setBackgroundColor(Color.parseColor("#6666ff"));
//        button2.setTag(Color.parseColor("#6666ff"));
//        button3.setBackgroundColor(Color.parseColor("#cc0066"));
//        button3.setTag(Color.parseColor("#cc0066"));
        myVideoView.getRootView().setBackgroundColor(Color.rgb(0xAA, 0xFF, 0xEE));
        myVideoView.setVideoViewListener(mVideoViewListener);
        myVideoView.setOnCompletionListener(mVideoViewCompleteListener);
        mediaControls = new MediaController(this);
        videoURI = Uri.parse(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts");
        startTime = System.currentTimeMillis();
        ffmpeg = FFmpeg.getInstance(this);
        progressDialog = ProgressDialog.show(this, "", "Buffering video...", true);
        progressDialog.setCancelable(true);
//        myVideoView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                String[] command = formFFMPEGCommand(15, 29.27, "bg.jpg", "ojob4_Full1_pre_full.mp4", "ojob4_Full1_pre_full.mp4_alpha.mp4", "0:1", true, myVideoView.getWidth(), myVideoView.getHeight());
//                progressDialog.setCancelable(true);
//    //          myVideoView.change
//
//        //      loadFFMPEGBinary();
////                executeFFMPEGCommandPlayVideo(command);
//            }
//        });
        String[] command = formFFMPEGCommand(15, 29.27, "bg.jpg", "ojob4_Full1_pre_full.mp4", "ojob4_Full1_pre_full.mp4_alpha.mp4", "0:1", true, 320, 240);
        executeFFMPEGCommandPlayVideo(command);
//        new FFMPEGThread(command).run();
    }
    public void loadFFMPEGBinary(){
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }
    public void executeFFMPEGCommandPlayVideo(String[] command){
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {

                    startTime = System.currentTimeMillis();
                    Log.i("FFMPEG_TRAC", "STARETED");
                }
                @Override
                public void onProgress(String message) {
                    int p = myVideoView.getCurrentPosition();
                    if(p <= 0){
                        if(System.currentTimeMillis() - startTime > 5000 ) {
                            playVideoOriginal();
                            seek = true;
                            startTime = System.currentTimeMillis();
                        }
                    }
                    Log.i("FFMPEG_TRAC", message);
                }
                @Override
                public void onFailure(String message) {
                    Log.i("FFMPEG_TRAC", message);
                }

                @Override
                public void onSuccess(String message) {
                    isFinshied = true;
                    myTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            myTextView.setText("Time elapsed in milliseconds : " + (System.currentTimeMillis() - startTime) + ", Counter : " + myCounter);
                        }
                    });
                    playVideoOriginal();
                    Log.i("FFMPEG_TRAC", message);
                }

                @Override
                public void onFinish() {
                    Log.i("FFMPEG_TRAC", "FINISH");
                    isFinshied = true;
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }
    // duration in seconds example : 22.1
    // SAR is string as "0:1"
    public static String[] formFFMPEGCommand(double frameRate, double duration, String bgPath, String videoColoredPath, String videoAlphaPath, String SAR, boolean isBGImage, int width, int height){
        String orgVideo = videoColoredPath;
        videoColoredPath = VIDEO_PATH + videoColoredPath;
        videoAlphaPath = VIDEO_PATH + videoAlphaPath;
        String[] filters = new String[]{"-filter_complex", "[0:v]setsar=sar=" + SAR + ", format=pix_fmts=yuv420p[cimg];[1:v]scale=320x180[corg];[2:v]scale=320x180[cmask];[cimg][corg][cmask]maskedmerge[out]", "-map", "[out]", "-map", "1:a", "-t", ""+duration};
        String[] inputs;
        if(isBGImage) {
            bgPath = IMAGE_PATH + bgPath;
            inputs = new String[]{"-r", "" + frameRate, "-loop", "1", "-i", bgPath, "-i", videoColoredPath, "-i", videoAlphaPath, "-y", "-t", "1"};
        }else{
            bgPath = "-f lavfi -i color=c="+ bgPath +":size=" + width + "x" + height;
            inputs = new String[]{"-r", "" + frameRate, bgPath, "-i", videoColoredPath, "-i", videoAlphaPath, "-y", "-t", "1"};
        }
        String[] output = new String[]{VIDEO_PATH + orgVideo + "_transparent.ts"};
        String[] command = new String[inputs.length + engineVideoParams.length + filters.length + output.length];
        int start = 0;
        for(String s : inputs){
            command[start] = s ;
            start++;
        }
        for(String s : engineVideoParams){
            command[start] = s ;
            start++;
        }
        for(String s : filters){
            command[start] = s ;
            start++;
        }
        for(String s : output){
            command[start] = s ;
            start++;
        }
        return command;
    }
    private AlphaVideoView.IVideoViewActionListener mVideoViewListener = new AlphaVideoView.IVideoViewActionListener()
    {
        @Override
        public void onTimeBarSeekChanged(int currentTime)
        {
            //TODO what you want
            myVideoView.start();
        }

        @Override
        public void onResume()
        {
            //TODO what you want
        }

        @Override
        public void onPause()
        {
            //TODO what you want
        }
    };

    private MediaPlayer.OnCompletionListener mVideoViewCompleteListener = new MediaPlayer.OnCompletionListener()
    {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(!isFinshied) {

                position = myVideoView.getDuration() - 100;
                playVideoOriginal();
                myCounter++;
            }
        }

    };
    public  void playVideoOriginal() {
        try {
            Log.i("THREAD_TRAC", "SEEK : " + seek + " And postion is : " + position);
            if(!seek) {
                progressDialog.dismiss();
            }
            Log.i("PLAY_TRAC","Position : " +  position + ", Loaded : " + (myVideoView.getDuration() - 100)) ;
            myVideoView.setVideoURI(videoURI);
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    //if (myVideoView.getDuration() - position >= 2000) {

                        myVideoView.seekTo(position);
                    //}
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }

    public void backhroundColorChanged(int newColor){
        if(ffmpeg.isFFmpegCommandRunning()){
            ffmpeg.killRunningProcesses();
        }
        myVideoView.stopPlayback();
        myVideoView.setAlpha(0f);
        ProgressDialog.show(this, "", "Buffering video...", true);
        startTime = System.currentTimeMillis();
        executeFFMPEGCommandPlayVideo(formFFMPEGCommand(15, 29.27, String.format("#%06X", (0xFFFFFF & newColor)), "ojob4_Full1_pre_full.mp4", "ojob4_Full1_pre_full.mp4_alpha.mp4", "0:1", false,  myVideoView.getWidth(), myVideoView.getHeight()));
    }
    public void changeBackground(View view) {
        backhroundColorChanged((int) ((Button) view).getTag());
    }

    public class FFMPEGThread extends Thread{
        private String[] command;
        public FFMPEGThread(String[] command){
            super("FFMPEG THREAD");
            this.command = command;
        }
        public void run(){
            executeFFMPEGCommandPlayVideo(command);
        }
    }

}
