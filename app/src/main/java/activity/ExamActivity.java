/**
 * 这个Activity是用来考试的
 * 有个小人在这里
 * 进这个Activity的时候，会先去让用户选择考试范围的章节
 * */
package activity;

import java.util.ArrayList;
import java.util.List;

import com.example.handtalk.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import animation.AnimationPlayer;
import animation.BoneManager;
import animation.GlviewIniter;
import animation.LoadListener;
import animation.MyRenderer;
import database.DbConnect;
import model.Chapter;
import model.Word;

public class ExamActivity extends Activity {
    private Button btn_a, btn_b, btn_c, btn_d, btn_next, btn_replay;
    private Button[] choiceButton;
    private LinearLayout ll_glviewholder;
    private GLSurfaceView glView;
    private MyRenderer glRenderer;
    private LoadListener animationListener;
    private int wordID = 0;//标记看到第几个单词了
    private List<Word> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        //让用户决定考试范围
        Intent intent = new Intent(this, SelectMultyChapterActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户决定了考试范围
        super.onActivityResult(requestCode, resultCode, data);

        //如果用户直接返回，就结束这个Activity
        List<Chapter> rangeChapter = SelectMultyChapterActivity.chapterSelected;//考试范围
        if(rangeChapter == null || rangeChapter.size()==0){
            finish();
            return;
        }

        //决定考的单词的代码
        ArrayList<Word> rangeWord = new ArrayList<Word>();//考试范围的所有单词
        for(Chapter chapter:rangeChapter){
            List<Word> aChapterWord = DbConnect.getWordByChapter(chapter.getId());
            rangeWord.addAll(aChapterWord);
        }
        wordList = getRandomExamWords(rangeWord, 10);//从考试范围的所有单词里面抽10个

        if(wordList.size() == 0){//如果考试范围词汇为0，就不考了
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return;
        }

        initActivity();
    }

    private void initActivity(){
        btn_a = (Button) findViewById(R.id.btn_a);
        btn_b = (Button) findViewById(R.id.btn_b);
        btn_c = (Button) findViewById(R.id.btn_c);
        btn_d = (Button) findViewById(R.id.btn_d);
        choiceButton = new Button[]{btn_a, btn_b, btn_c, btn_d};
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_replay = (Button) findViewById(R.id.btn_replay);

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
        initButtons();
        loadWord(0);
    }

    private void initButtons() {
        btn_replay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playAnimation();
            }
        });
        btn_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wordID < getExamWords().size()-1){
                    wordID++;
                    loadWord(wordID);
                    btn_a.setBackground(getDrawable(R.drawable.exam_normal));
                    btn_b.setBackground(getDrawable(R.drawable.exam_normal));
                    btn_c.setBackground(getDrawable(R.drawable.exam_normal));
                    btn_d.setBackground(getDrawable(R.drawable.exam_normal));
                    if(wordID == getExamWords().size()-1){
                        btn_next.setText("结束考试");
                    }
                }else if(wordID == getExamWords().size()-1){
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }else{
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            }
        });
        btn_next.setClickable(false);
    }

    /**
     * 加载一个单词
     * 包括放置选项，播放动画，设置点击事件等
     * */
    private void loadWord(int wordIndex) {
        Word correctWord = getExamWords().get(wordIndex);
        btn_next.setClickable(false);

        OnClickListener answersBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Button btn:choiceButton){
                    btn.setClickable(false);
                }
                btn_next.setClickable(true);
                Word word = (Word) v.getTag();
                Word correctWord = getExamWords().get(wordID);
                if(word.equals(correctWord)){
                    doCorrect((Button) v);
                }else{
                    doWrong((Button) v, correctWord);
                }
            }
            private void doCorrect(Button btn){
                btn.setTextColor(Color.GREEN);
                btn.setBackground(getDrawable(R.drawable.exam_right));
            }
            private void doWrong(Button btn, Word correctAnswer){
                btn.setTextColor(Color.RED);
                btn.setBackground(getDrawable(R.drawable.exam_wrong));
                for(Button b:choiceButton){
                    Word w = (Word) b.getTag();
                    if(w.equals(correctAnswer)){
                        b.setTextColor(Color.GREEN);
                        break;
                    }
                }
                doWrongWord(correctAnswer);
            }
        };

        //取得4个待选项
        List<Word> wrongAnswers = getWrongAnswers(correctWord);
        ArrayList<Word> answers = new ArrayList<Word>();
        answers.addAll(wrongAnswers);
        int rightAnswerIndex = (int)(Math.random()*4);
        answers.add(rightAnswerIndex, correctWord);

        for(int i=0; i<answers.size(); i++){
            Button btn = choiceButton[i];
            Word word = answers.get(i);
            btn.setText(word.getTitle());
            btn.setTextColor(Color.BLACK);
            btn.setClickable(true);
            btn.setTag(word);
            btn.setOnClickListener(answersBtnListener);
        }

        playAnimation();
    }

    /**
     * 当做错某个单词的时候
     * 连接数据库，添加错题本
     * */
    protected void doWrongWord(Word word) {
        int wrongTimes = word.getWrong();
        if(wrongTimes < 0){//如果不在错题本里面，就让它放在错题本里面
            wrongTimes = -wrongTimes;
        }
        wrongTimes++;
        word.setWrong(wrongTimes);
        DbConnect.updateWord(word);
    }

    /**
     * 获取3个（大概）互不相同的错误选项
     * 这3个错误选项肯定（大概）跟正确选项不一样
     * */
    private List<Word> getWrongAnswers(Word rightAnswer){
        List<Word> selectedWord = new ArrayList<Word>();//待选单词
        List<Word> ret = new ArrayList<Word>();

        selectedWord.addAll(getExamWords());//先把考试单词全部加进来
        selectedWord.remove(rightAnswer);//把其中正确答案移除
        int i = 0;
        while(selectedWord.size() < 4){//因为可能把正确答案选进去，所以填4进来
            selectedWord.addAll(DbConnect.getWordByChapter(i));//如果不够，就乱选！应该肯定够。如果真不够，就让它报错吧！
            i++;
        }
        selectedWord.remove(rightAnswer);//可能不小心选到正确答案，所以把它移除
        for(int j=0; j<3; j++){
            int randomIndex = (int)(Math.random()*selectedWord.size());
            Word word = selectedWord.remove(randomIndex);
            ret.add(word);
        }
        return ret;
    }

    /**
     * 播放动画，根据wordID决定播放哪个动画
     * */
    private void playAnimation(){
        String aniFileName = "animation/" + getExamWords().get(wordID).getFilename() + ".ani";
        AnimationPlayer.loadAnimation(this, aniFileName, animationListener);
    }

    /**
     * 获取考试单词列表
     * */
    private List<Word> getExamWords(){
        return wordList;
    }

    /**
     * 输入考试范围，和抽取单词数量
     * 输出考试范围中的随机数量的单词
     * */
    private List<Word> getRandomExamWords(List<Word> words, int limit){
        if(limit >= words.size()){
            return words;
        }
        List<Word> ret = new ArrayList<Word>();
        for(int i=0; i<limit; i++){
            int randomIndex = (int)(Math.random()*words.size());
            Word word = words.remove(randomIndex);
            ret.add(word);
        }
        return ret;
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