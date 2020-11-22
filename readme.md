# spring boot 完整版教程
##springboot源码解读
1. @SpringBootApplication （spring boot 主启动类）
   * @SpringBootConfiguration （包含了spring boot的配置类）
   * @Configuration （包含了spring的配置类）
   * @Bean （通过spring的配置类声明一个bean，就可以被springboot加载了）

2. spring.factories（配置类的加载配置文件）
   * @EnableAutoConfiguration（通过开自动配置，进行装配）
3. 热部署
   * base classLoader
   * restart classLoader
   * spring.devtools.restart.exclude=static/**,public/** 排除一些文件的热部署
4. 配置文件的位置
   * -file:./config
   * -file:./  项目的根路径
   * -classpath:/config
   * -classpath:
   * 优先级：优先级由高到底高优先级的配置会覆盖低优先级。
   * java -jar myprojrct.jar --spring.config.name=myproject:自定义配置文件加载
5. @EnableConfigurationProperties (开启配置文件的自动注入)
```java
//RedisAutoConfiguration
//spring.factories spring工厂
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}

@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {
	private int database = 0;
	private String url;
	private String host = "localhost";
	private String username;
	private String password;
	private int port = 6379;
	private boolean ssl;
	private Duration timeout;
	private Duration connectTimeout;
	private String clientName;
}
/**
总结：
1. 定义：RedisProperties
2. @ConfigurationProperties(prefix = "spring.redis") 读取配置文件内容
3. @EnableConfigurationProperties(RedisProperties.class) 开启注解
**/
```


   
