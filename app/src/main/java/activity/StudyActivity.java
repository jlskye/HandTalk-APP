/**
 * 这个Activity是用来看单词的
 * 有个小人在这里
 * 进这个Activity的时候，会先去让用户选择章节
 * 如果进来的intent里面的key为"chapterID"有值(注意要传String)，就不会让用户选择章节，会让用户看这一章节
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
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import database.DbConnect;
import model.Word;

public class StudyActivity extends Activity {
    private Button btn_last, btn_next;
    private ImageButton btn_replay, btn_nextmotion, btn_pause, btn_lastmotion;
    private ImageButton btn_wrong, btn_collect;
    private TextView tv_word;
    private LinearLayout ll_glviewholder;
    private GLSurfaceView glView;
    private MyRenderer glRenderer;
    private LoadListener animationListener;
    private int wordID = 0;//标记看到第几个单词了
    private List<Word> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        if(!checkIntent()){//如果没有指定要查看哪一章节
            //让用户选择要看哪章的单词
            Intent intent = new Intent(this, SelectChapterActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户选择完要看哪章了
        super.onActivityResult(requestCode, resultCode, data);

        //如果用户直接返回，就结束这个Activity
        if(data == null || data.getExtras() == null){
            finish();
            return;
        }

        //获取ID
        int chapterID = data.getExtras().getInt("chapter");

        wordList = DbConnect.getWordByChapter(chapterID);
        if(wordList.size() == 0){
            finish();
            return;
        }

        initActivity();
    }

    /**
     * 检查是否查看指定章节单词
     * 如果是，返回true,并初始化单词设置
     * */
    private boolean checkIntent(){
        Intent intent = getIntent();
        if(intent == null || intent.getExtras() == null)
            return false;
        String id =  intent.getExtras().getString("chapterID");
        if(id == null) return false;
        int chapterID;
        try{
            chapterID = Integer.parseInt(id);
        }catch(Exception e){
            return false;
        }
        wordList = DbConnect.getWordByChapter(chapterID);
        if(wordList.size() == 0){
            finish();
            return true;
        }
        initActivity();
        return true;
    }

    private void initActivity(){
        initButtons();

        tv_word = (TextView) findViewById(R.id.textView_wrod);

        if(!checkSupported()){
            Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }else{
            initGLview();
        }

        animationListener = new LoadListener() {
            @Override
            public void onstart() {
            }

            @Override
            public void onLoading(int cur, int total) {
            }

            @Override
            public void onFinished() {
                AnimationPlayer.playAnimation(glView, glRenderer, BoneManager.getRootBone());
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("wa", "加载动画失败");
            }
        };
        updateCheckingWord();
    }

    /**
     * 初始化按钮，赋予点击事件
     * */
    private void initButtons() {
        btn_last = (Button) findViewById(R.id.button_last);
        btn_next = (Button) findViewById(R.id.button_next);
        btn_replay = (ImageButton) findViewById(R.id.btn_replay);
        btn_nextmotion = (ImageButton) findViewById(R.id.btn_nextmotion);
        btn_pause = (ImageButton) findViewById(R.id.btn_pause);
        btn_lastmotion = (ImageButton) findViewById(R.id.btn_lastmotion);
        btn_wrong = (ImageButton) findViewById(R.id.btn_wrong);
        btn_collect = (ImageButton) findViewById(R.id.btn_collect);


        btn_last.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wordID <= 0) return;
                wordID--;
                updateCheckingWord();
            }
        });
        btn_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wordID >= getChapterWords().size()-1) return;
                wordID++;
                updateCheckingWord();
            }
        });
        btn_replay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationPlayer.replay();
            }
        });
        btn_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AnimationPlayer.isPaused()){
                    AnimationPlayer.setPuase(true);
                    btn_pause.setImageResource(R.drawable.btn_play);
                }
                else{
                    AnimationPlayer.setPuase(false);
                    btn_pause.setImageResource(R.drawable.btn_stop);
                }
            }
        });
        btn_nextmotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationPlayer.playNextMotion();
            }
        });
        btn_lastmotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationPlayer.playLastMotion();
            }
        });
        btn_wrong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Word checkingWord = getCheckingWord();
                if(checkingWord.getWrong() > 0){
                    checkingWord.setWrong(-checkingWord.getWrong());//把错的次数变为负数，来表示在错题本外
                    DbConnect.updateWord(checkingWord);
                    btn_wrong.setVisibility(View.INVISIBLE);
                    Toast.makeText(StudyActivity.this, "已移出错题本", Toast.LENGTH_SHORT).show();;
                }
            }
        });
        btn_collect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Word checkingWord = getCheckingWord();
                if(checkingWord.isCollected()){
                    checkingWord.setCollected(false);
                    DbConnect.updateWord(checkingWord);
                    btn_collect.setImageResource(R.drawable.add_collection);
                    Toast.makeText(StudyActivity.this, "已移出生词本", Toast.LENGTH_SHORT).show();
                }else{
                    checkingWord.setCollected(true);
                    DbConnect.updateWord(checkingWord);
                    btn_collect.setImageResource(R.drawable.delete_collection);
                    Toast.makeText(StudyActivity.this, "已加入生词本", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 每当切换单词时，调用这个函数
     * */
    protected void updateCheckingWord() {
        Word checkingWord = getCheckingWord();
        if(checkingWord.isCollected()){
            btn_collect.setImageResource(R.drawable.delete_collection);
        }else{
            btn_collect.setImageResource(R.drawable.add_collection);
        }
        if(checkingWord.getWrong() > 0){
            btn_wrong.setVisibility(View.VISIBLE);
            btn_wrong.setImageResource(R.drawable.delete_mistake);
        }else{
            btn_wrong.setVisibility(View.INVISIBLE);
        }
        playAnimation();
    }

    /**
     * 播放动画，根据wordID决定播放哪个动画
     * */
    private void playAnimation(){
        tv_word.setText(getChapterWords().get(wordID).getTitle());
        String aniFileName = "animation/" + getChapterWords().get(wordID).getFilename() + ".ani";
        AnimationPlayer.loadAnimation(this, aniFileName, animationListener);
    }

    /**
     * 获取要查看的单词列表
     * */
    private List<Word> getChapterWords(){
        return wordList;
    }

    /**
     * 获取正在查看的单词
     * */
    private Word getCheckingWord(){
        return getChapterWords().get(wordID);
    }

    private void initGLview(){
        ll_glviewholder = (LinearLayout) findViewById(R.id.ll_glviewholder);
        glView = new GLSurfaceView(this);
        glRenderer = new MyRenderer(this);
        GlviewIniter.glviewInit(this, glView, glRenderer, ll_glviewholder);
    }

    /**
     * 检查是否支持openGLES2
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