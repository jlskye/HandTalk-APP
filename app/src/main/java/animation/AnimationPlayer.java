package animation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AnimationPlayer {
    private static int FPS = 24;
    // 每两个动作之间的间隔时间，单位是毫秒
    private static int motionTime = 500;

    private static LoadListener animLoadListener;
    private static Exception failError;
    private static int loadingCul = 0;
    private static int loadingTol = 0;
    private static String readingFileName;
    private static Context context;
    private static Motion[] motions;
    private static int motionNumber = 0;

    private static GLSurfaceView glView;
    private static MyRenderer glRenderer;
    private static Bone rootBone;
    private static Motion showingMotion;// 正在渲染的动作

    private static AnimationPlayer obj;// 每加载一个动画，就会被new出来一个
    private static PlayListener playListener;

    public boolean stopped = false;// 停止播放，切换动画的时候用
    private static boolean paused = false;// 暂停播放

    public static void loadAnimation(Context context, String animationFileName, LoadListener animLoadListener,
                                     PlayListener playListener) {
        AnimationPlayer.playListener = playListener;
        loadAnimation(context, animationFileName, animLoadListener);
    }

    /**
     * 加载动画 会把动画文件 以及里面所有的动作文件 都加载出来 动画文件名要带上animation/ 会新开一个线程进行IO读取。读取事件发生时会
     * 在调用这个函数的线程下 回调animLoadListener中的接口
     */
    public static void loadAnimation(Context context, String animationFileName, LoadListener animLoadListener) {
        if (context == null || animationFileName == null)
            return;
        AnimationPlayer.animLoadListener = animLoadListener;
        AnimationPlayer.context = context;
        paused = false;
        puaseAtNextMotion = false;
        pauseAtLastMotion = false;
        if (animLoadListener != null)
            animLoadListener.onstart();
        readingFileName = animationFileName;
        if (obj != null) {
            // 把上一个播放的动作停下来
            obj.stopped = true;
        }
        obj = new AnimationPlayer();
        obj.readFileThread.start();
    }

    /**
     * 播放动画 请在执行这个函数之前执行loadAnimation，读取了哪个就会播放哪个 会新开一个线程进行播放，所以可以在主线程里面运行
     * 以后有暂停之类的需求，可以再改
     */
    public static void playAnimation(GLSurfaceView glView, MyRenderer glRenderer, Bone rootBone) {
        AnimationPlayer.glView = glView;
        AnimationPlayer.glRenderer = glRenderer;
        AnimationPlayer.rootBone = rootBone;
        obj.playAnimationThread.start();
    }

    private static Motion lastMotion;
    private static boolean ifStartWithLastAniMotion = false;
    /**
     * 设置了这个之后，下一个被加载进来的动画会把上一个动画的最后一个动作  作为第一个动作
     * 请在loadAnimation()之前执行这个函数，否则可能出现莫名其妙的BUG
     * 这个与endWithOutStandMotion()配合，达到连续播放动画
     * */
    public static void startWithLastAniMotion(){
        ifStartWithLastAniMotion = true;
    }
    private static boolean ifEndWithoutStandMotion = false;
    /**
     * 设置了这个之后，下一个被加载进来的动画的结尾不会加上站立动作
     * 请在loadAnimation()之前执行这个函数，否则可能出现莫名其妙的BUG
     * 这个与startWithLastAniMotion()配合，达到连续播放动画
     * */
    public static void endWithoutStandMotion(){
        ifEndWithoutStandMotion = true;
    }

    /**
     * 设置暂停播放，动画会停在一个关键动作
     */
    public static void setPuase(boolean pause) {
        paused = pause;
    }

    /**
     * 获取动画是否暂停
     */
    public static boolean isPaused() {
        return paused;
    }

    // 这个变量用于和播放线程同步
    private static boolean puaseAtNextMotion = false;

    /**
     * 分步播放，播放下一个动作
     */
    public static void playNextMotion() {
        if (!paused) {// 如果正在播放，那么这个效果就是暂停
            paused = true;
            return;
        }
        // 如果已经暂停，那么这个效果就是取消一次暂停之后继续暂停
        paused = false;
        puaseAtNextMotion = true;
    }

    // 这个变量用于和播放线程同步，让播放线程跳到上一个动作
    private static boolean pauseAtLastMotion = false;

    /**
     * 分步播放，播放上一个动作
     */
    public static void playLastMotion() {
        paused = false;
        pauseAtLastMotion = true;
    }

    /**
     * 重新播放
     */
    public static void replay() {
        loadAnimation(context, readingFileName, animLoadListener);
    }

    /**
     * 读取文件的线程
     */
    private Thread readFileThread = new Thread() {
        @Override
        public void run() {
            try {
                InputStream is = context.getAssets().open(readingFileName);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                parserAinmationFile(br);
                br.close();
                MotionReader.setStandMotion(context);// 就怕没有站立动作，要读一遍
                onFinishHandler.sendEmptyMessage(0x001);
            } catch (Exception e) {
                failError = e;
                onFailHandler.sendEmptyMessage(0x001);
            }
        };
    };

    /**
     * 读取动画文件 以及动画文件中需要用到的动作文件
     */
    private static void parserAinmationFile(BufferedReader br) throws Exception {
        String line = br.readLine();
        if (line == null)
            return;
        while (line != null && line.startsWith("//")) {
            line = br.readLine();
        }
        motionNumber = Integer.parseInt(line) + 2;// 算上开始的站立动作，结束的站立动作，要+2
        if(ifEndWithoutStandMotion) motionNumber--;//减去最后的站立动作

        // 做个优化：如果有之前留下来的动作对象，就有之前的，省得new出来
        if (motions != null) {
            for (int i = 0; i < motions.length; i++) {// 把站立动作的位置设为空，以防站立动作被弄脏
                if (motions[i] == MotionReader.getStandMotion())
                    motions[i] = null;
                if(ifStartWithLastAniMotion && motions[i] == lastMotion)
                    motions[i] = null;
            }

            if (motions.length >= motionNumber) {
                // 如果原来数组里面比现在需求的还多，就用原来数组的
            } else {
                // 如果原来数组里面的比现在需求的还少，那就new一个数组出来，把原来的都放进新的数组里
                Motion[] newArr = new Motion[motionNumber];
                for (int i = 0; i < motions.length; i++) {
                    newArr[i] = motions[i];// 新的等于原来的
                }
                motions = newArr;// 新的数组赋给他
            }
        } else {
            motions = new Motion[motionNumber];
        }

        int i = 1;
        motions[0] = MotionReader.getStandMotion();// 设置好初始站立动作
        if(ifStartWithLastAniMotion && lastMotion != null)
            motions[0] = lastMotion;

        line = br.readLine();

        while (line != null) {
            if (line.startsWith("//")) {
                line = br.readLine();
                continue;
            }
            loadingCul = i;
            loadingTol = motionNumber;
            onLoadingHandler.sendEmptyMessage(0x001);

            String[] lineData = line.split(";");
            String motionFileName = "motion/" + lineData[0];
            boolean isKeyMotion = (lineData[1].equals("1"));

            if (motions[i] == null) {
                motions[i] = MotionReader.readMotionInAssets(context, motionFileName);
            } else {
                // 节省内存开销，直接读进原来的数组
                MotionReader.readMotionInAssetsIntoMotion(context, motionFileName, motions[i]);
            }
            motions[i].setKeyMotion(isKeyMotion);
            lastMotion = motions[i];
            i++;
            line = br.readLine();
        }

        if(!ifEndWithoutStandMotion)
            motions[i] = MotionReader.getStandMotion();// 设置好结束站立动作

        ifEndWithoutStandMotion = false;
        ifStartWithLastAniMotion = false;
    }

    private static Handler onFailHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (animLoadListener != null)
                animLoadListener.onFailure(failError);
        }
    };

    private static Handler onLoadingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (animLoadListener != null)
                animLoadListener.onLoading(loadingCul, loadingTol);
        }
    };

    private static Handler onFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (animLoadListener != null)
                animLoadListener.onFinished();
        }
    };

    private static Handler startPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (playListener != null)
                playListener.startPlaying();
        }
    };
    private static Handler changeMotionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (playListener != null)
                playListener.changeMotion();
        }
    };
    private static Handler endPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (playListener != null)
                playListener.endPlaying();
        }
    };

    private Thread playAnimationThread = new Thread() {
        @Override
        public void run() {
            try {// 这段代码有很多同步问题，统一用try处理
                int i = 0;
                // 播放中间动作
                while (!stopped) {

                    if (i < motionNumber - 1 && !pauseAtLastMotion) {
                        playBetweenMotion(this, motions[i], motions[i + 1]);
                        i++;
                    }

                    if (playListener != null) {
                        if (i == 1)
                            startPlayHandler.sendEmptyMessage(0x001);
                        changeMotionHandler.sendEmptyMessage(0x001);
                        if (i == motionNumber - 2)
                            endPlayHandler.sendEmptyMessage(0x001);
                    }

                    if (pauseAtLastMotion) {// 停在上一个关键动作（直接跳过去）
                        pauseAtLastMotion = false;
                        paused = true;
                        i = getLastKeyMotion(motions, i);
                        showingMotion = motions[i];
                        obj.showModelByMotion.sendEmptyMessage(0x001);// 通知主线程显示上一个动作
                        while (paused)
                            sleepOneFrame(this);
                    }

                    if (i == motionNumber - 1 || motions[i + 1].isKeyMotion()) {// 如果是关键动作并且暂停了，就一直停着
                        while (paused)
                            sleepOneFrame(this);
                    }

                    if (puaseAtNextMotion) {// 停在下一个关键动作（播放过去）
                        puaseAtNextMotion = false;
                        paused = true;// 为了停在下一个动作
                    }

                    if (i == motionNumber - 1)// 如果是最后一个动作，就一直停着。直到下一个动作开始播放时，pause解开，线程结束
                        while (paused)
                            sleepOneFrame(this);

                    sleepOneFrame(this);
                }
            } catch (Exception e) {
            }
        };
    };

    /**
     * 获取上一个关键动作
     */
    private int getLastKeyMotion(Motion[] motions, int nowIndex) {
        for (int i = nowIndex - 1; i >= 0; i--) {
            if (motions[i].isKeyMotion())
                return i;
        }
        return 0;
    }

    /**
     * 播放从m1到m2中间的动作。 m1在前，m2在后 thread是执行这东西的线程 请在一个非主线程里面运行这个函数
     */
    private void playBetweenMotion(Thread thread, Motion m1, Motion m2) {
        if (m1 == null || m2 == null)
            return;
        float transform = 0;// 两个动作之间过渡了多少
        int frameNum = FPS * motionTime / 1000;// 动作之间有几帧
        for (int playingFrame = 0; playingFrame <= frameNum; playingFrame++) {
            sleepOneFrame(thread);
            if (stopped)
                return;
            if (pauseAtLastMotion)
                return;// 如果要停在上一个动作的话，就马上停下来
            if (frameNum == 0)
                transform = 1;
            else
                transform = (float) playingFrame / frameNum;// 获取变形程度
            Motion middleMotion = MotionReader.getMiddleMotionBetween(m1, m2, transform);// 获取要渲染的过渡动作
            showingMotion = middleMotion;
            obj.showModelByMotion.sendEmptyMessage(0x001);// 通知主线程更新UI
        }
    }

    /**
     * 停止线程一帧的时间
     */
    private void sleepOneFrame(Thread thread) {
        try {
            thread.sleep(1000 / FPS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Handler showModelByMotion = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            glRenderer.setFrame(rootBone, showingMotion);
            glView.invalidate();
        };
    };

    /**
     * 每两个动作之间的间隔时间，单位是毫秒
     */
    public static int getMotionTime() {
        return motionTime;
    }

    /**
     * 每两个动作之间的间隔时间，单位是毫秒
     */
    public static void setMotionTime(int motionTime) {
        AnimationPlayer.motionTime = motionTime;
    }

    public static int getFPS() {
        return FPS;
    }

    public static void setFPS(int fPS) {
        FPS = fPS;
    }
}