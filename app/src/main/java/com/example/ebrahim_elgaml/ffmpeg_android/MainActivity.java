package com.example.ebrahim_elgaml.ffmpeg_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private VideoView myVideoView;
    private ProgressDialog progressDialog;
    private boolean isPlayed = false;
    private MediaController mediaControls;
    private  long startTime = System.currentTimeMillis();
    private int myCounter = 0;
//    private PlayThred th;
    private  boolean isFinshied = false;
    private boolean isStarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = (TextView)(findViewById(R.id.mTextView));
        myVideoView = (VideoView)(findViewById(R.id.videoView));
        Drawable d = Drawable.createFromPath(IMAGE_PATH + "bg.jpg");
        mediaControls = new MediaController(this);
//        myVideoView.setBackground(d);
//        playVideoOriginal(VIDEO_PATH +"ojob4_Full1_pre_full.mp4_alpha.mp4_transparent.ts" );
        final String[] command = formFFMPEGCommand(15, 29.27, "bg.jpg", "ojob4_Full1_pre_full.mp4", "ojob4_Full1_pre_full.mp4_alpha.mp4", "0:1");
        final Context myContext = this;
        final FFmpeg ffmpeg = FFmpeg.getInstance(myContext);
//        myVideoView.post(new Runnable() {
//            @Override
//            public void run() {
//                while (!isFinshied) {
//                    if (myVideoView.getCurrentPosition() <= 0) {
//                        if (System.currentTimeMillis() - startTime > 3000) {
//                            playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", false);
//                            startTime = System.currentTimeMillis();
//                        }
//                    } else {
//                        if (myVideoView.getDuration() - myVideoView.getCurrentPosition() <= 100) {
//                            playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", true);
//                            myCounter++;
//                            startTime = System.currentTimeMillis();
//                        }
//                    }
//                }
//
//            }
//        });
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
                    try {
                        // to execute "ffmpeg -version" command you just need to pass "-version"

                        String folder = Environment.getExternalStorageDirectory() + "";
                        String cmd = " http://res.cloudinary.com/ebrahim-elgaml/video/upload/v1463137785/wl2glc7odcbid6flyvok.mp4 ";
                        String testFile = Environment.getExternalStorageDirectory() + "/test.mp4";
//                        th = new PlayThred();
//                        th.start();
                        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                            @Override
                            public void onStart() {
                                progressDialog = ProgressDialog.show(myContext, "",
                                        "Buffering video...", true);
                                progressDialog.setCancelable(true);
                                startTime = System.currentTimeMillis();
                                Log.i("FFMPEG_TRAC", "STARETED");

                            }
                            @Override
                            public void onProgress(String message) {
//                                if( !isStarted && myVideoView.getCurrentPosition() <= 0) {
//                                    if(System.currentTimeMillis() - startTime > 3000 ){
//                                        myVideoView.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", false);
//                                                isStarted = true;
//                                            }
//                                        });
//
//                                        startTime = System.currentTimeMillis();
//                                    }
//                                }else{
//                                    if(myVideoView.getDuration() - myVideoView.getCurrentPosition() <= 100){
//                                        myVideoView.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", true);
//                                            }
//                                        });
//
//                                        myCounter++;
//                                        startTime = System.currentTimeMillis();
//                                    }
//                                }
                                if(System.currentTimeMillis() - startTime > 3000 ){
                                    if(myVideoView.getCurrentPosition() <= 0){
                                        playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", false);
                                        startTime = System.currentTimeMillis();
                                    }
//                                    playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts");
//                                    th.start();
//                                    isPlayed = true;
                                }
                                if(myVideoView.getCurrentPosition() > 0 && myVideoView.getDuration() - myVideoView.getCurrentPosition() <= 500){
                                    playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", true);

                                    startTime = System.currentTimeMillis();
                                    myCounter++;
                                }
//                                ;
                                Log.i("FFMPEG_TRAC", message);
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(myContext, message, Toast.LENGTH_LONG);
                                Log.i("FFMPEG_TRAC", message);
                            }

                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(myContext, message, Toast.LENGTH_LONG);
                                myTextView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        myTextView.setText("Time elapsed in milliseconds : " + (System.currentTimeMillis() - startTime) + ", Counter : " + myCounter);
                                    }
                                });
                                playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", true);
                                Log.i("FFMPEG_TRAC", message);
                            }

                            @Override
                            public void onFinish() {
                                Toast.makeText(myContext, "Fininsh", Toast.LENGTH_LONG);
                                Log.i("FFMPEG_TRAC", "FINISH");
//                                th.setIsFinished(true);
                                isFinshied = true;
                            }
                        });
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        // Handle if FFmpeg is already running
                    }
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }


    }

    // duration in seconds example : 22.1
    // SAR is string as "0:1"
    public static String[] formFFMPEGCommand(double frameRate, double duration, String bgPath, String videoColoredPath, String videoAlphaPath, String SAR){
        String orgVideo = videoColoredPath;
        videoColoredPath = VIDEO_PATH + videoColoredPath;
        videoAlphaPath = VIDEO_PATH + videoAlphaPath;
        bgPath = IMAGE_PATH + bgPath;
        String[] filters = new String[]{"-filter_complex", "[0:v]setsar=sar=" + SAR + ", format=pix_fmts=yuv420p[cimg];[1:v]scale=320x180[corg];[2:v]scale=320x180[cmask];[cimg][corg][cmask]maskedmerge[out]", "-map", "[out]", "-map", "1:a", "-t", ""+duration};
        String[] inputs = new String[]{"-r", "" + frameRate, "-loop", "1", "-i", bgPath, "-i", videoColoredPath, "-i", videoAlphaPath, "-y", "-t", "1"};
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
    public  void playVideoOriginal(String videoPath, final boolean seek) {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            final int position = myVideoView.getCurrentPosition();
//            mediaControls.setAnchorView(myVideoView);
            Uri video = Uri.parse(videoPath);
//            myVideoView.setMediaController(mediaControls);
            myVideoView.setVideoURI(video);
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    progressDialog.dismiss();
                    // videoViewOriginal.setAlpha(1.0f);
                    if(seek) {
                        myVideoView.seekTo(position);
                    }
                    myVideoView.start();
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
    public class PlayThred extends Thread{
        // dealing with time as millisecond
        private String videoPath;
        private int videoTime;
        private int seekTime;
        private boolean isFinished;
        private int lastPlayedTime;
        private boolean isFirstPlayed = false;
        public PlayThred()
        {
            super("PlayThred");
        }
        public PlayThred(String path)
        {
            super("PlayThred");
            videoPath = path;
        }
        public void run(){
            while(!isFinished){
                if(myVideoView.getCurrentPosition() <= 0){
                    if(System.currentTimeMillis() - startTime > 3000 ){
                        playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", false);
                        startTime = System.currentTimeMillis();
                    }
                }else{
                    if(myVideoView.getDuration() - myVideoView.getCurrentPosition() <= 100){
                        playVideoOriginal(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts", true);
                        myCounter++;
                        startTime = System.currentTimeMillis();
                    }
                }
//                if(isFirstPlayed){
//                   seekTime = myVideoView.getCurrentPosition();
//                }else{
//                    seekTime = 0;
//                    isFirstPlayed = true;
//                }
//                playVideoOriginal();
//                try {
//                    sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }

        public String getVideoPath() {
            return videoPath;
        }

        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
        }

        public int getVideoTime() {
            return videoTime;
        }

        public void setVideoTime(int videoTime) {
            this.videoTime = videoTime;
        }

        public int getSeekTime() {
            return seekTime;
        }

        public void setSeekTime(int seekTime) {
            this.seekTime = seekTime;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public void setIsFinished(boolean isFinished) {
            this.isFinished = isFinished;
        }

        public int getLastPlayedTime() {
            return lastPlayedTime;
        }

        public void setLastPlayedTime(int lastPlayedTime) {
            this.lastPlayedTime = lastPlayedTime;
        }
//        public  void playVideoOriginal(String videoPath, final boolean seek) {
//            try {
//                getWindow().setFormat(PixelFormat.TRANSLUCENT);
//                final int position = myVideoView.getCurrentPosition();
////            mediaControls.setAnchorView(myVideoView);
//                Uri video = Uri.parse(videoPath);
////            myVideoView.setMediaController(mediaControls);
//                myVideoView.setVideoURI(video);
//                myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    public void onPrepared(MediaPlayer mp) {
//                        progressDialog.dismiss();
//                        // videoViewOriginal.setAlpha(1.0f);
//                        if(seek) {
//                            myVideoView.seekTo(position);
//                        }
//                        myVideoView.start();
//                    }
//                });
//            } catch (Exception e) {
//                progressDialog.dismiss();
//            }
//        }
    }
}
