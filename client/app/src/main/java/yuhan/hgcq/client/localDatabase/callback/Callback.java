package yuhan.hgcq.client.localDatabase.callback;

public interface Callback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}