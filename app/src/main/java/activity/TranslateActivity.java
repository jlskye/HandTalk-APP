/**
 * ??Activity???????
 * ???????
 * ??????????????????Intent???key??"sentence"
 * */
package activity;

import java.util.List;

import com.example.handtalk.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.media.Image;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import animation.AnimationPlayer;
import animation.BoneManager;
import animation.GlviewIniter;
import animation.LoadListener;
import animation.MyRenderer;
import animation.PlayListener;
import model.Word;
import wordspliter.WordSpliter;

public class TranslateActivity extends Activity {
    private ImageButton btn_last, btn_next;
    private ImageButton btn_pause;
    private TextView tv_text;
    private LinearLayout ll_glviewholder;
    private GLSurfaceView glView;
    private MyRenderer glRenderer;
    private LoadListener animationLoadListener;
    private PlayListener playListener;
    private int wordID = 0;//??????????
    private List<Word> wordList;
    private boolean paused = false;//??????????
    private boolean animationPuased = false;//????????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        //????????????
        String sentence = getTranslateSentence();
        if(sentence == null || sentence.length()==0){
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return;
        }
        wordList = WordSpliter.splitWord(sentence);
        if(wordList == null || wordList.size() == 0){
            unableToTranslate();
            return;
        }
        wordID = 0;
        initActivity();
    }

    /**
     * ??????????
     * */
    private void unableToTranslate(){
        Toast.makeText(this, "????", Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    private String getTranslateSentence(){
        Intent intent = getIntent();
        if(intent==null || intent.getExtras()==null){
            return null;
        }
        String sentence = intent.getExtras().getString("sentence");
        if(sentence == null){
            return null;
        }
        return sentence;
    }

    private void initActivity(){
        tv_text = (TextView) findViewById(R.id.tv_text);
        initButtons();

        if(!checkSupported()){
            Toast.makeText(this, "???????OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }else{
            initGLview();
        }

        animationLoadListener = new LoadListener() {
            @Override
            public void onstart() {}
            @Override
            public void onLoading(int cur, int total) {}
            @Override
            public void onFinished() {
                AnimationPlayer.playAnimation(glView, glRenderer, BoneManager.getRootBone());
            }
            @Override
            public void onFailure(Exception e) {
                Log.d("wa", "??????");
            }
        };
        playListener = new PlayListener() {
            @Override
            public void startPlaying() {
                Log.d("wa", "startPlay");
            }
            @Override
            public void endPlaying() {
                if(paused){
                    animationPuased = true;
                    return;
                }
                if(wordID >= getPlayingWords().size()-1) return;
                wordID++;
                updatePlayingWord();
            }
            @Override
            public void changeMotion() {
                Log.d("wa", "changeMotion");
            }
        };
        updatePlayingWord();
    }

    /**
     * ????????????
     * */
    private void initButtons() {
        btn_last = (ImageButton) findViewById(R.id.btn_last);
        btn_next = (ImageButton) findViewById(R.id.btn_next);
        btn_pause = (ImageButton) findViewById(R.id.btn_pause);

        btn_last.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wordID <= 0) return;
                wordID--;
                updatePlayingWord();
            }
        });
        btn_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wordID >= getPlayingWords().size()-1) return;
                wordID++;
                updatePlayingWord();
            }
        });
        btn_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paused = !paused;
                if(paused) btn_pause.setImageResource(R.drawable.btn_play);
                else btn_pause.setImageResource(R.drawable.btn_stop);
                if(!paused && animationPuased){
                    //??????????????????????????
                    if(wordID >= getPlayingWords().size()-1) return;
                    wordID++;
                    updatePlayingWord();
                    animationPuased = false;
                }
            }
        });
    }

    /**
     * ??????????????
     * ??wordID?????????
     * */
    protected void updatePlayingWord() {
        if(wordID > 0)
            AnimationPlayer.startWithLastAniMotion();
        if(wordID < getPlayingWords().size()-1)
            AnimationPlayer.endWithoutStandMotion();
        playAnimation();
        setTextViewText();
    }

    /**
     * ???????wordID????????
     * */
    private void playAnimation(){
        String aniFileName = "animation/" + getPlayingWords().get(wordID).getFilename() + ".ani";
        AnimationPlayer.loadAnimation(this, aniFileName, animationLoadListener, playListener);
    }

    /**
     * ???????????????????
     * */
    private void setTextViewText(){
        Word playingWord = getPlayingWord();
        String str = "";
        for(Word word:getPlayingWords()){
            if(!word.equals(playingWord)) str += word.getTitle() + " ";
            else str += "<font color='#FF0000'><big>" + word.getTitle() + " </big></font>";
        }
        tv_text.setText(Html.fromHtml(str));
    }

    /**
     * ??????????
     * */
    private List<Word> getPlayingWords(){
        return wordList;
    }

    /**
     * ?????????
     * */
    private Word getPlayingWord(){
        return getPlayingWords().get(wordID);
    }

    private void initGLview(){
        ll_glviewholder = (LinearLayout) findViewById(R.id.ll_glviewholder);
        glView = new GLSurfaceView(this);
        glRenderer = new MyRenderer(this);
        GlviewIniter.glviewInit(this, glView, glRenderer, ll_glviewholder);
    }

    /**
     * ??????openGLES2
     * */
    private boolean checkSupported() {
        boolean supportsEs2;
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        boolean isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));

        supportsEs2 = supportsEs2 || isEmulator;
        return supportsEs2;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (glView != null) {
            glView.onPause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (glView != null)
            glView.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        return false;
    }
}
