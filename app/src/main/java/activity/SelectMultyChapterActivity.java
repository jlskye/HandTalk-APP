/**
 * 能用来多选章节
 * 主要是用来选择考试范围
 * 返回之后，数据会放在chapterSelected这个静态变量里面。如果用户什么都没选，这个变量就是空
 * 返回时，返回码是1
 * */
package activity;

import java.util.ArrayList;
import java.util.List;

import com.example.handtalk.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import database.DbConnect;
import model.Chapter;
import myadpter.Myadpter_multyChapter;

public class SelectMultyChapterActivity extends Activity{
    public static List<Chapter> chapterSelected = null;
    private List<Chapter> allChapter = null;
    private ListView lv;
    private Button endButton;
    private Myadpter_multyChapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_multy_chapter);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        chapterSelected = null;
        lv = (ListView) findViewById(R.id.lv_multychapter);

        adapter = new Myadpter_multyChapter(this, getChapterData());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.cb_chapteritemselected);
                cb.setChecked(!cb.isChecked());
            }
        });
        endButton = (Button) findViewById(R.id.btn_endselect);
        endButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean[] selected = adapter.getSelected();
                chapterSelected = new ArrayList<Chapter>();
                for(int i=0; i<selected.length; i++){
                    if(selected[i]) chapterSelected.add(getChapterData().get(i));
                }
                setResult(0);
                finish();
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_to_bottom);
        return false;
    }

    /**
     * 获取所有章节
     * */
    private List<Chapter> getChapterData(){
        if(allChapter == null) allChapter = DbConnect.getChapters();
        return allChapter;
    }

}
