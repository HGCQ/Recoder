package yuhan.hgcq.server.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class EnvConfig {

    @PostConstruct
    public void loadEnv() {
        String envFilePath = "./.env"; // .env 파일 경로
        try (BufferedReader reader = new BufferedReader(new FileReader(envFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // 빈 줄이나 주석 무시
                }
                String[] keyValue = line.split("=", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    System.setProperty(key, value); // 시스템 환경 변수에 설정
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
