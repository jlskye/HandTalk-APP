package animation;

public interface PlayListener {

    /**
     * 在站立动作到第一个动作后回调
     * */
    public void startPlaying();

    /**
     * 换动作时回调
     * */
    public void changeMotion();

    /**
     * 在最后一个动作回到站立动作前调用
     * */
    public void endPlaying();
}
