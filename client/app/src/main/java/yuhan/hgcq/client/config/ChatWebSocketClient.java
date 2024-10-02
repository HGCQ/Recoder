package yuhan.hgcq.client.config;

import android.util.Log;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;

public class ChatWebSocketClient extends WebSocketListener {

    private WebSocket webSocket;
    private final Gson gson = new Gson();
    private final static String SERVER_IP = "172.18.7.22";
    private final ChatWebSocketCallback callback;
    private Long albumId;

    public interface ChatWebSocketCallback {
        void onMessageReceived(ChatDTO chatDTO);
    }

    public ChatWebSocketClient(Long albumId, ChatWebSocketCallback callback) {
        this.albumId = albumId;
        this.callback = callback;
    }

    public void start() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("ws://" + SERVER_IP + ":8080/chat/" + albumId)
                .build();

        webSocket = client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        Log.i("WebSocket", "Connected");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.i("WebSocket", "Received message: " + text);

        ChatDTO chatDTO = gson.fromJson(text, ChatDTO.class);
        if (callback != null) {
            callback.onMessageReceived(chatDTO);
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        Log.i("WebSocket", "Closing: " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
        Log.e("WebSocket Error", t.getMessage());
    }

    public void sendMessage(ChatDTO chatDTO) {
        if (webSocket != null) {
            String chatDTOJson = gson.toJson(chatDTO);
            webSocket.send(chatDTOJson);
            Log.i("WebSocket", "Message : " + chatDTO.getMessage());
        }
    }
}