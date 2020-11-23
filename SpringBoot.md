#SpringBoot原理

1. @SpringBootApplication
  * @SpringBootConfiguration
  
标注这个类是一个配置类，跟@Configuration注解的功能一致，只不过@SpringBootConfiguration是springboot的注解，
而@Configuration是spring的注解
  * @EnableAutoConfiguration
      * @AutoConfigurationPackage 添加该注解的类所在的package 作为 自动配置package 进行管理。
      * @Import(AutoConfigurationImportSelector.class)
    
    获取类路径下spring.factories下key为EnableAutoConfiguration全限定名对应值
    List<String> configurations = getCandidateConfigurations(annotationMetadata,attributes);
    getCandidateConfigurations会到classpath下的读取META-INF/spring.factories文件的配置，并返回一个字符串数组
      * 总结： 从classpath中搜索所有META-INF/spring.factories配置文件然后，将其中org.springframework.boot.autoconfigure.EnableAutoConfiguration key对应的配置项加载到spring容器
          只有spring.boot.enableautoconfiguration为true（默认为true）的时候，才启用自动配置
  * @ComponentScan 该注解默认会扫描该类所在的包下所有的配置类，相当于之前的 <context:component-scan>。
  
2. SpringApplication
  * 实例化一个SpringApplication对象
      * 设置初始化：setInitializers((Collection)getSpringFactoriesInstances(ApplicationContextInitializer.class));
      * 设置监听器：setListeners((Collection)getSpringFactoriesInstances(ApplicationListener.class));
  * run方法的调用
      * environment的初始化，配置文件加载，包括大名鼎鼎的profile
      * listeners.starting()  //发布一个ApplicationStartingEvent事件
      * 初始化容器：createApplicationContext() //根据不同的webApplication类型初始化同的容器，使用BeanUtils来实例化，其内部是用反射来搞的
      * prepareContext
      * context.setEnvironment(environment); 可以看到条件注解的解析工具是在这里注册的，根据环境配置文件来搞的
      * postProcessApplicationContext getBeanFactory的结果是一个DefaultListableBeanFactory,setConversionService，ConversionService是用来做类型转换的,有个converse接口
      * applyInitializers 之前分析过setInitializers方法，这里先获取到Initializers，逐个执行其初始化方法
      * beanFactory `ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();`一个DefaultListableBeanFactory
      * registerSingleton `beanFactory.registerSingleton("springApplicationArguments", applicationArguments);`
      * setAllowBeanDefinitionOverriding
      * getAllSources //调试时发现就加载了个主类，HelloApplication的那个
      * load BeanDefinitionLoader用于从源加载Bean的定义信息，并封装成BeanDefinition对象，并注册到ApplicationContext中，加载的源可以是类注解、XML文件、package、classpath、Groovy文件等。
      这里加载的source只有一个HelloApplication，也就是主类
      最后一步加载BeanDefinition，注意在doRegisterBean中有一个
      总结：load这一步就是读取source文件，从中获取各种BeanDefinition，加到beanFactory的map里面完事，Bean的实例化、初始化还在后面
      * refreshContext(context) 最终调用的还是AbstractApplicationContext的refresh，在IOC中继续研究
      * afterRefresh(context, applicationArguments)
      * listeners.started(context)

```java
public ConfigurableApplicationContext run(String... args) {
    //计时器不用管他
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    //一些初始化和配置，不用管
    ConfigurableApplicationContext context = null;
    Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
    configureHeadlessProperty();
    SpringApplicationRunListeners listeners = getRunListeners(args);
    
    //关键点1：listener start
    listeners.starting();
    try {
        //应用参数，一般是个CommandLineArgs
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        //小关键点1
        ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
        //根据上一步的配置搞些事情，貌似影响不大的样子
        configureIgnoreBeanInfo(environment);
        
        //打印图标
        Banner printedBanner = printBanner(environment);
        
        //关键点2
        context = createApplicationContext();
        
        //用于后面的catch语句中处理异常，不用管它
        exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
                new Class[] { ConfigurableApplicationContext.class }, context);
        
        //关键点3
        prepareContext(context, environment, listeners, applicationArguments, printedBanner);
        
        //关键点4
        refreshContext(context);
        
        //关键点5
        afterRefresh(context, applicationArguments);
        
        //计时器不用管
        stopWatch.stop();
        //日志啥的
        if (this.logStartupInfo) {
            new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
        }
        
        //关键点6
        listeners.started(context);
        
        //关键点7
        callRunners(context, applicationArguments);
    }
    catch (Throwable ex) {
        handleRunFailure(context, ex, exceptionReporters, listeners);
        throw new IllegalStateException(ex);
    }

    try {
        //关键点8：listener
        listeners.running(context);
    }
    catch (Throwable ex) {
        handleRunFailure(context, ex, exceptionReporters, null);
        throw new IllegalStateException(ex);
    }
    //返回值没人接收，不用管它
    return context;
}
```

[参考资料](https://www.jianshu.com/p/557220260c54)