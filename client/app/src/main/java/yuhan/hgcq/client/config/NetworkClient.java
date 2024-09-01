package yuhan.hgcq.client.config;

import android.content.Context;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static NetworkClient instance;

    private final OkHttpClient okHttpClient;
    private final ClearableCookieJar cookieJar;
    private final Context app;
    private final SharedPrefsCookiePersistor sharedPrefsCookiePersistor;

    private final String serverIp = "http://127.0.0.1:8080";

    private NetworkClient(Context context) {
        app = context.getApplicationContext();

        // 쿠키 설정
        sharedPrefsCookiePersistor = new SharedPrefsCookiePersistor(app);

        cookieJar = new PersistentCookieJar(new SetCookieCache()
                , sharedPrefsCookiePersistor);

        // Http 메시지 로그
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        // Http 커넥션 설정
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        // 서버와 연결
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverIp)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized NetworkClient getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkClient(context);
        }
        return instance;
    }

    public ClearableCookieJar getCookieJar() {
        return cookieJar;
    }

    public String getServerIp() {
        return serverIp;
    }

    public SharedPrefsCookiePersistor getSharedPrefsCookiePersistor() {
        return sharedPrefsCookiePersistor;
    }

    public void saveCookie() {
        NetworkClient client = NetworkClient.getInstance(app);

        List<Cookie> cookies = client.getCookieJar()
                .loadForRequest(Objects.requireNonNull(HttpUrl.parse(client.getServerIp())));
        sharedPrefsCookiePersistor.saveAll(cookies);

        for (Cookie ck : cookies) {
            Log.d("받아온 쿠키", "Name: " + ck.name() + " Value: " + ck.value());
        }

        List<Cookie> cookies1 = sharedPrefsCookiePersistor.loadAll();

        for (Cookie ck : cookies1) {
            Log.d("쿠키 저장", "Name: " + ck.name() + " Value: " + ck.value());
        }
    }

    public void deleteCookie() {
        NetworkClient client = NetworkClient.getInstance(app);

        List<Cookie> cookies = client.getCookieJar()
                .loadForRequest(Objects.requireNonNull(HttpUrl.parse(client.getServerIp())));
        sharedPrefsCookiePersistor.removeAll(cookies);

        Log.d("쿠키 삭제", "성공");
    }
}
