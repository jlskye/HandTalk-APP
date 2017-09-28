/**
 * ?multychapter_item???????Activity
 * ????getSelected()??????????????
 * getSelected()?????????data????
 * */
package myadpter;

import java.util.List;

import com.example.handtalk.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import model.Chapter;

public class Myadpter_multyChapter extends BaseAdapter{

    private Context context;
    private List<Chapter> data;
    private boolean[] selected;

    public Myadpter_multyChapter(Context context, List data) {
        this.context = context;
        this.data = data;
        selected = new boolean[data.size()];
        for(int i=0; i<selected.length; i++) selected[i] = false;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView = View.inflate(context, R.layout.multychapter_item, null);
        TextView tv = (TextView) newView.findViewById(R.id.tv_chapteritemtitle);
        CheckBox cb = (CheckBox) newView.findViewById(R.id.cb_chapteritemselected);

        tv.setText(data.get(position).getTittle());
        cb.setTag(position);
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (Integer) buttonView.getTag();
                selected[position] = isChecked;
            }
        });
        return newView;
    }

    public boolean[] getSelected() {
        return selected;
    }
}
