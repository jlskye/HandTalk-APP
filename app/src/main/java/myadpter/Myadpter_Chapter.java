/**
 * ?????
 * ?Chapter????????SelectChapterActivity??
 */
package myadpter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import model.Chapter;

import java.util.List;

import com.example.handtalk.R;

public class Myadpter_Chapter extends BaseAdapter {
    private Context mContext;
    private List<Chapter> mData;

    public Myadpter_Chapter(Context context, List data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Log.d("test", "getView: "+view);
        View myview = null;
        Holder holder;

        if (view != null) {
            myview = view;
            holder = (Holder) myview.getTag();
        } else {
            myview = View.inflate(mContext, R.layout.chapter_item, null);
            holder = new Holder();
            holder.quiz_title = (TextView) myview.findViewById(R.id.quiz_title);
            holder.quiz_describe = (TextView) myview.findViewById(R.id.quiz_describe);
            myview.setTag(holder);
        }

        Chapter quizModel = mData.get(i);

        holder.quiz_title.setText(quizModel.getTittle());
        holder.quiz_describe.setText(quizModel.getDescribe());

        return myview;
    }

    private static class Holder {
        public TextView quiz_title;
        public TextView quiz_describe;
    }
}
