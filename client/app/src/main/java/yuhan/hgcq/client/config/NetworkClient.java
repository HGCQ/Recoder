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
import yuhan.hgcq.client.model.service.AlbumService;
import yuhan.hgcq.client.model.service.ChatService;
import yuhan.hgcq.client.model.service.FollowService;
import yuhan.hgcq.client.model.service.LikedService;
import yuhan.hgcq.client.model.service.MemberService;
import yuhan.hgcq.client.model.service.PhotoService;
import yuhan.hgcq.client.model.service.TeamService;

public class NetworkClient {
    private static NetworkClient instance;

    private final OkHttpClient okHttpClient;
    private final ClearableCookieJar cookieJar;
    private final Context app;
    private final SharedPrefsCookiePersistor sharedPrefsCookiePersistor;

    private AlbumService as;
    private ChatService cs;
    private FollowService fs;
    private LikedService ls;
    private MemberService ms;
    private PhotoService ps;
    private TeamService ts;

    private final String serverIp = "http://10.0.2.2:8080/";

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
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // 서버와 연결
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverIp)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        as = retrofit.create(AlbumService.class);
        cs = retrofit.create(ChatService.class);
        fs = retrofit.create(FollowService.class);
        ls = retrofit.create(LikedService.class);
        ms = retrofit.create(MemberService.class);
        ps = retrofit.create(PhotoService.class);
        ts = retrofit.create(TeamService.class);
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

    /* Retrofit Service 반환 */
    public AlbumService getAlbumService() {
        return as;
    }

    public ChatService getChatService() {
        return cs;
    }

    public FollowService getFollowService() {
        return fs;
    }

    public LikedService getLikedService() {
        return ls;
    }

    public MemberService getMemberService() {
        return ms;
    }

    public PhotoService getPhotoService() {
        return ps;
    }

    public TeamService getTeamService() {
        return ts;
    }

    /* 쿠키 관련 메소드 (수정 필요) */
    public void saveCookie() {
        NetworkClient client = NetworkClient.getInstance(app);

        List<Cookie> cookies = client.getCookieJar()
                .loadForRequest(Objects.requireNonNull(HttpUrl.parse(client.getServerIp())));
        sharedPrefsCookiePersistor.saveAll(cookies);

        for (Cookie ck : cookies) {
            Log.i("Get Cookie", "Name: " + ck.name() + " Value: " + ck.value());
        }

        List<Cookie> cookies1 = sharedPrefsCookiePersistor.loadAll();

        for (Cookie ck : cookies1) {
            Log.i("Save Cookie", "Name: " + ck.name() + " Value: " + ck.value());
        }
    }

    public void deleteCookie() {
        NetworkClient client = NetworkClient.getInstance(app);

        List<Cookie> cookies = client.getCookieJar()
                .loadForRequest(Objects.requireNonNull(HttpUrl.parse(client.getServerIp())));
        sharedPrefsCookiePersistor.removeAll(cookies);

        Log.i("Delete Cookie", "Success");
    }
}
