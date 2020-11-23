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
6. 第三方组件的配置
```java
//1. 绑定一
@Data
public class AnotherComponent {
    private boolean enable;
    private InetAddress remoteAddress;
}

@Component
public class MyService {
    @Bean
    @ConfigurationProperties("acme")
    public AnotherComponent getAnotherComponent(){
        return new AnotherComponent();
    }
}

@Autowired
private AnotherComponent anotherComponent;

//绑定二（松散绑定）
@Component
//在属性类中前缀不可以驼峰模式，只能用羊肉串模式，在yaml中是支持驼峰配置
@ConfigurationProperties("acme.my-person.person")
@Data
public class OwnerProperties {
    private String fristName;
}
@Autowired
private OwnerProperties ownerProperties;
```
7. @ConfigurationProperties VS @Value
   * @ConfigurationProperties 读取配置文件中的一个对象
   * @Value 读取配置文件中的一个具体值

## springboot自动配置解读
1. java中的SPI（Service Provider Interface）
   * 是java提供的一套用来被第三方实现或者扩展的API，她可以用来启用框架扩展和替换组件。
   SPI的实现：
      * service-commom 定义一个支付接口 PayService.pay()
      * ali-pay 实现 AliPay.pay(); .\META-INF\services\com.wjx.service.PayService
      * wx-pay 实现 WxPay.pay();  .\META-INF\services\com.wjx.service.PayService
      * ServiceLoader<PayService> load = ServiceLoader.load(PayService.class);加载PayService
      * main-test service.pay();直接调用实现可插拔的SPI
 
2. 源码
   * setInitializers //设置初始化器
      * SpringFactoriesLoader 获取 META-INF/spring.factories里面的实例对象.
      ```Set<String> names = new LinkedHashSet(SpringFactoriesLoader.loadFactoryNames(type, classLoader));```
      * 获取当前所有的classpath下spring.factories里面的name（比如：自动装在类的名字）
   * setListeners //设置初始化器
      * 根据上一步的names来创建实例对象
      ```List<T> instances = createSpringFactoriesInstances(type, parameterTypes, classLoader, args, names);```
      * 调用java的反射机制生成实例对象
3. HTTP编码配置类解析
   * 定义类：ServerProperties
   * 在类上面添加注解@ConfigurationProperties并制定``prefix="server"``
   * 通过注解@EnableConfigurationProperties(ServerProperties.class)
   * @Configuration标明这是一个配置配
  ```java
@ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)
public class ServerProperties {
    private Integer port;
    private InetAddress address;
}

@Configuration
@EnableConfigurationProperties(ServerProperties.class)
@ConditionalOnProperty(prefix = "server.servlet.encoding", value = "enabled", matchIfMissing = true)
public class HttpEncodingAutoConfiguration {
	private final Encoding properties;
    //本方法是通过构造器的方式将ServerProperties对象传进来，进行properties的初始化
	public HttpEncodingAutoConfiguration(ServerProperties properties) {
		this.properties = properties.getServlet().getEncoding();
	}
}
```    
## springboot数据源的自动配置
1. 数据源的自动管理
```java
@Configuration //标明是一个配置类
public class DataSourceConfig{
@Bean
//读取配置文件中的spring.datasource属性值
@ConfigurationProperties(prefix = "spring.datasource")
    public DruidDataSource dataSource(){
        return new DruidDataSource();
    }
}
```
2. mybatis
```java
@Configuration
@EnableConfigurationProperties({MybatisProperties.class})
public class MybatisAutoConfiguration{
    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        return executorType != null ? new SqlSessionTemplate(sqlSessionFactory, executorType) : new SqlSessionTemplate(sqlSessionFactory);
    }
}

@ConfigurationProperties(prefix = "mybatis")
public class MybatisProperties {
    public static final String MYBATIS_PREFIX = "mybatis";
    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private String configLocation;
    private String[] mapperLocations;
    private String typeAliasesPackage;
    private Class<?> typeAliasesSuperType;
}
```
## 内嵌tomcat的配置与启动
1. tomcat的配置
   * ServletWebServerFactoryAutoConfiguration 
```java
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(ServletRequest.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ServerProperties.class)
@Import({ ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class,
    ServletWebServerFactoryConfiguration.EmbeddedTomcat.class,
    ServletWebServerFactoryConfiguration.EmbeddedJetty.class,
    ServletWebServerFactoryConfiguration.EmbeddedUndertow.class })
public class ServletWebServerFactoryAutoConfiguration {
	@Bean
	@ConditionalOnClass(name = "org.apache.catalina.startup.Tomcat")
	public TomcatServletWebServerFactoryCustomizer tomcatServletWebServerFactoryCustomizer(
			ServerProperties serverProperties) {
		return new TomcatServletWebServerFactoryCustomizer(serverProperties);
	}
}
```

## spring mvc
1. mvc 的开启
   * @Configuration
   * @EnablWebMvc
   * 实现WebMvcConfigurer
```java
@Configuration
@EnablWebMvc
public class WebConfig implements WebMvcConfigurer{

}
```
