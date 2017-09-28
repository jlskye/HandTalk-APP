package activity;

import com.example.handtalk.R;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import database.DbConnect;
import model.Word;
import wordspliter.WordSpliter;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button button_exam;
    private ImageButton button_quiz;
    private Button btn_translate;
    private ImageButton btn_collection, btn_wrong;
    private EditText et_edit;
    private TextView Arrow;

    /**
     * 所有模块初始化代码都加在这里
     * */
    private void initAll() {
        //初始化数据库模块
        DbConnect.initDbConnect(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        initAll();

        button_exam = (Button) findViewById(R.id.zjlianxi);
        button_quiz = (ImageButton) findViewById(R.id.kaoshi);
        btn_translate = (Button) findViewById(R.id.btn_translate);
        btn_collection = (ImageButton) findViewById(R.id.myCollection);
        btn_wrong = (ImageButton) findViewById(R.id.mistake);
        et_edit = (EditText) findViewById(R.id.edit);
        button_exam.setOnClickListener(this);
        button_quiz.setOnClickListener(this);
        btn_translate.setOnClickListener(this);
        btn_collection.setOnClickListener(this);
        btn_wrong.setOnClickListener(this);

        long time=System.currentTimeMillis();
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("E");
        String day = ""+format.format(date);
        if(day.equals("周一")) Arrow = (TextView)findViewById(R.id.arrow1);
        if(day.equals("周二")) Arrow = (TextView)findViewById(R.id.arrow2);
        if(day.equals("周三")) Arrow = (TextView)findViewById(R.id.arrow3);
        if(day.equals("周四")) Arrow = (TextView)findViewById(R.id.arrow4);
        if(day.equals("周五")) Arrow = (TextView)findViewById(R.id.arrow5);
        if(day.equals("周六")) Arrow = (TextView)findViewById(R.id.arrow6);
        if(day.equals("周日")) Arrow = (TextView)findViewById(R.id.arrow7);
        Arrow.setBackgroundResource(R.drawable.arrow);
        //Toast.makeText(getApplicationContext(), ""+format.format(date),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zjlianxi:
                startActivity(new Intent(MainActivity.this, StudyActivity.class));
                //设置切换动画，从底部进入，顶端退出
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;
            case R.id.kaoshi:
                startActivity(new Intent(MainActivity.this, ExamActivity.class));
                //设置切换动画，从底部进入，顶端退出
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;
            case R.id.btn_translate:
                toTranslate();
                break;
            case R.id.myCollection:
                studyChapter(-2, "生词本里面没有单词");
                break;
            case R.id.mistake:
                studyChapter(-1, "错题本里面没有单词");
                break;
        }
    }

    private void toTranslate() {
        String sentence = et_edit.getText().toString();
        List<Word> wordList = WordSpliter.splitWord(sentence);
        if(sentence==null || sentence.length()==0){
            Toast.makeText(this, "请输入文本", Toast.LENGTH_SHORT).show();
            return;
        }
        if(wordList == null || wordList.size() == 0){
            Toast.makeText(this, "无法翻译", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, TranslateActivity.class);
        intent.putExtra("sentence", sentence);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * 看指定章节
     * errorText是该章节没有单词时，用吐司打出的文本
     * */
    private void studyChapter(int id, String errorText){
        if(DbConnect.getWordByChapter(id).size() == 0){
            Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
            return;
        }
        String idStr = id+"";
        Intent intent = new Intent(this, StudyActivity.class);
        intent.putExtra("chapterID", idStr);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        startActivity(intent);
    }
}
