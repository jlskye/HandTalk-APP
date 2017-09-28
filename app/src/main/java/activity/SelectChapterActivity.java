/**
 * 这个ACTIVITY用来选择章节
 * 请用能接受请求的Intent打开这个Activity
 * 在返回的Intent中，将被选择的章节的id以int塞入bundle中，key是"chapter"，返回码是0
 */
package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import database.DbConnect;
import model.Chapter;
import myadpter.Myadpter_Chapter;
import java.util.List;

import com.example.handtalk.R;


public class SelectChapterActivity extends Activity {

    private ListView qi_list;
    private Myadpter_Chapter myadpter_quiz;
    private List<Chapter> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectchapter);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        qi_list = (ListView) findViewById(R.id.qi_listview);
        myadpter_quiz = new Myadpter_Chapter(this, getChapterData());
        //设置适配器
        qi_list.setAdapter(myadpter_quiz);
        //设置点击事件
        qi_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                Chapter chapter = getChapterData().get(i);//获取选中的章节
                int id = chapter.getId();
                intent.putExtra("chapter", id);//转String
                setResult(0, intent);
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
    private List<Chapter> getChapterData() {
        if(data == null) data = DbConnect.getChapters();
        return data;
    }
}
