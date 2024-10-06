package yuhan.hgcq.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/* Redis 세션 유지 기간 설정 파일 */

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 259200)
public class HttpSessionConfig {
}