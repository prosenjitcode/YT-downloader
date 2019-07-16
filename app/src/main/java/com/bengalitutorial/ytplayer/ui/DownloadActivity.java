package com.bengalitutorial.ytplayer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bengalitutorial.ytplayer.R;
import com.bengalitutorial.ytplayer.youtubeuriextractor.YouTubeUriExtractor;
import com.bengalitutorial.ytplayer.youtubeuriextractor.YtFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private static final int ITAG_FOR_AUDIO = 140;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static String youtubeLink;

    private LinearLayout mainLayout;
    private List<YtFragmentedVideo> formatsToShowList;
    private ProgressDialog PD;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getYoutubeDownloadUrl(youtubeLink);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermission();
        setContentView(R.layout.activity_download);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        PD = new ProgressDialog(this);
        PD.setMessage("Parsing Data");
        PD.show();
        // Check how it was started and if we can get the youtube link

        youtubeLink = getIntent().getStringExtra("URL");


    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            getYoutubeDownloadUrl(youtubeLink);
        }
    }

    private void getYoutubeDownloadUrl(String youtubeLink) {
        YouTubeUriExtractor ytEx = new YouTubeUriExtractor(this) {

            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                PD.dismiss();
                if (ytFiles == null) {
                    TextView tv = new TextView(DownloadActivity.this);
                    tv.setText("Update app");
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    mainLayout.addView(tv);
                    return;
                }
                formatsToShowList = new ArrayList<>();
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    YtFile ytFile = ytFiles.get(itag);
                    Log.d("asdasd", "onUrisAvailable: "+ ytFile.getUrl());
                    if (ytFile.getMeta().getHeight() == -1 || ytFile.getMeta().getHeight() >= 360) {
                        addFormatToList(ytFile, ytFiles);
                    }
                }
                Collections.sort(formatsToShowList, new Comparator<YtFragmentedVideo>() {
                    @Override
                    public int compare(YtFragmentedVideo lhs, YtFragmentedVideo rhs) {
                        return lhs.height - rhs.height;
                    }
                });
                for (YtFragmentedVideo files : formatsToShowList) {
                    addButtonToMainLayout(videoTitle, files);
                }
            }
        };
        ytEx.setIncludeWebM(false);
        ytEx.setParseDashManifest(true);
        ytEx.execute(youtubeLink);

    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        int height = ytFile.getMeta().getHeight();
        if (height != -1) {
            for (YtFragmentedVideo frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getMeta().getFps() == ytFile.getMeta().getFps())) {
                    return;
                }
            }
        }
        YtFragmentedVideo frVideo = new YtFragmentedVideo();
        frVideo.height = height;
        if (ytFile.getMeta().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        formatsToShowList.add(frVideo);
    }


    private void addButtonToMainLayout(final String videoTitle, final YtFragmentedVideo ytFrVideo) {
        // Display some buttons and let the user choose the format
        String btnText;
        if (ytFrVideo.height == -1)
            btnText = "Audio " + ytFrVideo.audioFile.getMeta().getAudioBitrate() + " kbit/s";
        else
            btnText = (ytFrVideo.videoFile.getMeta().getFps() == 60) ? ytFrVideo.height + "p60" :
                    ytFrVideo.height + "p";
        Button btn = new Button(this);
        btn.setText(btnText);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String filename;
                if (videoTitle.length() > 55) {
                    filename = videoTitle.substring(0, 55);
                } else {
                    filename = videoTitle;
                }
                filename = filename.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");
                filename += (ytFrVideo.height == -1) ? "" : "-" + ytFrVideo.height + "p";
                String downloadIds = "";
                boolean hideAudioDownloadNotification = false;
                if (ytFrVideo.videoFile != null) {
                    downloadIds += downloadFromUrl(ytFrVideo.videoFile.getUrl(), videoTitle,
                            filename + "." + ytFrVideo.videoFile.getMeta().getExt(), false);
                    downloadIds += "-";
                    hideAudioDownloadNotification = true;
                }
                if (ytFrVideo.audioFile != null) {
                    downloadIds += downloadFromUrl(ytFrVideo.audioFile.getUrl(), videoTitle,
                            filename + "." + ytFrVideo.audioFile.getMeta().getExt(), hideAudioDownloadNotification);
                }
                if (ytFrVideo.audioFile != null)
                    cacheDownloadIds(downloadIds);
                finish();
            }
        });
        mainLayout.addView(btn);
    }

    private long downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName, boolean hide) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);
        if (hide) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setVisibleInDownloadsUi(false);
        } else
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        return manager.enqueue(request);
    }

    private void cacheDownloadIds(String downloadIds) {
        File dlCacheFile = new File(this.getCacheDir().getAbsolutePath() + "/" + downloadIds);
        try {
            dlCacheFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class YtFragmentedVideo {
        int height;
        YtFile audioFile;
        YtFile videoFile;
    }
}
