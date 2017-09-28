/**
 * 这个类用于初始化glview
 * 先把glview和renender给new出来，然后传进来
 * 这个函数帮忙添加滑动转人物的效果
 * */
package animation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class GlviewIniter {

    private static GestureDetector detector;
    private static GLSurfaceView glView;
    private static MyRenderer glRenderer;

    /**
     * 帮忙初始化glView，给它滑动旋转人物的效果
     */
    public static void glviewInit(Context context, GLSurfaceView glView
            , MyRenderer glRenderer, ViewGroup parentView) {

        try {
            ModelManager.readModels(context);
            BoneManager.readBoneInAssets(context, "bone.bon");
            MotionReader.setStandMotion(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        detector = new GestureDetector(context, getListener());
        GlviewIniter.glView = glView;
        GlviewIniter.glRenderer = glRenderer;

        glView.setRenderer(glRenderer);
        glView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
        parentView.addView(glView);
    }

    private static OnGestureListener getListener() {
        return new OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                glRenderer.addRotation(-distanceX / 2);// 旋转模型
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                // 这里要写true,才能用onFling和onScrool
                return true;
            }
        };
    }
}
