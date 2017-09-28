package animation;

public interface LoadListener {
    void onstart();

    void onLoading(int cur, int total);

    void onFinished();

    void onFailure(Exception e);
}
