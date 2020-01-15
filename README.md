> Freemarker是一个基于Java语言的多用途，轻量级模板引擎。它提供了很多内置的语法指令，例如条件选中，迭代，赋值，字符串和算术运算，格式化，宏定义等等。Freemarker最初是为了在Web MVC应用程序框架中生成HTML页面而创建的，但它并不绑定到Servlet，HTML或者任何其他与Web相关的内容。此外，Freemarker还应用与非Web应用程序环境中。

[![优雅重构让FreeMarker在SpringBoot中零配置自定义指令](https://cdn.ramostear.com/20200115-9b89111a9b004f44bd68cad8e6e77744.jpg "优雅重构让FreeMarker在SpringBoot中零配置自定义指令")](https://www.ramostear.com/post/2020/01/15/5j70j58.html "优雅重构让FreeMarker在SpringBoot中零配置自定义指令")


​	Freemarker自身已经提供了很多有用的语法指令，通过使用这些指令，能够很轻松的读取和操作Model中的数据。除此之外，Freemarker还允许开发者扩展Freemarker语法指令，自定义满足具体需求的新指令。本文将着重讲解利用Java的反射机制去优化SpringBoot与Freemarker整合后零配置自定义指令。



## 1. FreeMarker是什么？

​	在开始之前，先花一两分钟回顾一下Freemarker的基本概念。Apache FreeMarker是一个基于Java库的视图模板引擎，主要用于根据模板和模型数据渲染生成文本输出（HTML网页，电子邮件，配置文件，源代码等等）。FreeMarker模板使用其特定的模板语言（FTL）进行编写，这是一种简单的专用标记语言。通常，使用通用编程语言（如Java）来准备模型数据；然后，Apache FreeMarker使用模板渲染准备好的数据。在模板中，开发者可以专注于如何显示数据，在模型中，你则专注于数据的包装。下面通过一张官图了解一下FreeMarker的基本工作原理：

![](https://cdn.ramostear.com/20200115-42cc5a56cfba424fb390fa8c9df022bf.png)



## 2. SpringBoot整合Apache FreeMaker

​	在SpringBoot中使用Apache FreeMarker(以下简称FreeMarker)是一件很容易的事情。首先，在pom.xml文件中添加Freemarker的starter依赖，IDE会根据当前SpringBoot的版本信息自动下载合适版本的FreeMarker依赖包到类路径下，pom.xml的配置如下：

```xml
...
<dependencies>
	...
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-freemarker</artifactId>
    </dependency>
    ...
</dependencies>
...
```

然后，你只需要在application.yml或application.properties配置文件中指定FreeMarker的相关参数(例如编码方式，缓存，模板前缀/后缀，格式化样式等等)即可。通常，SpringBoot已经为FreeMarker提供了一些默认的配置，我们可以在`org.springframework.boot.autoconfigure.freemarker`包下的`FreeMarkerProperties`类中找到相关的默认值，内容如下：

```java
@ConfigurationProperties(
    prefix = "spring.freemarker"
)
public class FreeMarkerProperties extends AbstractTemplateViewResolverProperties {
    public static final String DEFAULT_TEMPLATE_LOADER_PATH = "classpath:/templates/";
    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_SUFFIX = ".ftl";
    private Map<String, String> settings = new HashMap();
    private String[] templateLoaderPath = new String[]{"classpath:/templates/"};
    private boolean preferFileSystemAccess = true;

    public FreeMarkerProperties() {
        super("", ".ftl");
    }

    public Map<String, String> getSettings() {
        return this.settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public String[] getTemplateLoaderPath() {
        return this.templateLoaderPath;
    }

    public boolean isPreferFileSystemAccess() {
        return this.preferFileSystemAccess;
    }

    public void setPreferFileSystemAccess(boolean preferFileSystemAccess) {
        this.preferFileSystemAccess = preferFileSystemAccess;
    }

    public void setTemplateLoaderPath(String... templateLoaderPaths) {
        this.templateLoaderPath = templateLoaderPaths;
    }
}
```

我们可以在配置文件中对自动配置的默认值进行覆盖，以满足自身的需求，例如我们可以做如下的配置：

```yaml
spring:
  freemarker:
    cache: false
    suffix: .html
    settings:
      datetime_format:  yyyy-MM-dd HH:mm
      number_format:  0.##
```

在上述的配置中，将默认的模板后缀名由`.ftl`修改为`.html`，关闭了FreeMarker的缓存模板数据，同时设置了模板中日期的格式化模板和数值的格式化模板。

​	经过上述的两个步骤，SpringBoot与Apache FreeMarker的整合工作就完成了。接下来，将了解如何扩展FreeMarker的语法指令。



## 3. 自定义FreeMarker语法指令

​	自定义FreeMarker语法指令功能让开发者在操作FreeMarker模板时更加灵活多样。基于FreeMarker实现自定义指令只需让自定义指令类实现TemplateDirectiveModel接口并重写execute()方法即可。execute()方法是实现自定义指令的核心。

> 注意：
>
> TemplateDirectiveModel是在FreeMarker 2.3.11版本中才引入的接口，如果你使用的是旧版本的FreeMarker,则需要实现TemplateTransformModel类。

下面是实现FreeMarker的一个示例代码：

![](https://cdn.ramostear.com/20200115-ffb1385db4364710a8b905fafb35acff.png)

接下来，我们只需要使用FreeMarker的Configuration类，将此自定义指令类的实例添加到FreeMarker共享变量中便可在模板中使用自己的指令。下面是配置示例：

```java
Configuration cfg = new Configuration(Configuration.VERSION_2_2_27);
cfg.setSharedVariable("myDirective",new MyTemplateDirective());
```

> 注意：
>
> 在设置FreeMarker共享变量时，需要为自定义指令指定一个名称。



## 4. SpringBoot中配置自定义指令

​	FreeMarker自定义指令的配置方式有很多种，在SpringBoot中配置FreeMarker自定义自定更为简单，下面介绍其中一种方式。我们可以利用SpringBoot的组件扫描机制，统一管理FreeMarker自定义指令的配置工作，例如：

```java
@Component
public class FreemarkerCustomDirectveManager {

    @Autowired
    private Configuration cfg;
    @Autowired
    private ApplicationContext app;

    @PostConstruct
    public void setSharedVariable()throws TemplateModelException{
        cfg.setSharedVariable ( "direct1",app.getBean ( Direct1.class ) );
        cfg.setSharedVariable ( "direct2",app.getBean ( Direct2.class ) );
        cfg.setSharedVariable ( "direct3",app.getBean ( Direct3.class ) );
        cfg.setSharedVariable ( "direct4",app.getBean ( Direct4.class ) );
        cfg.setSharedVariable ( "direct5",app.getBean ( Direct5.class ) );
    }
}
```

在上述的配置中，@Component注解的作用是将普通的POJO实例化到Spring的容器中，而@PostConstruct注解的作用是在服务器加载Servlet的时候有且只执行一次被其标注的方法。另外，我们通过ApplicationContext(应用上下文)获取自定义指令类的实例，并将其设置为FreeMarker的共享变量。

> 提示：
>
> 在Spring Framework中，被@PostConstruct注释的方法会在类的init()方法执行之前，构造方法执行之后被执行，该注解注释的方法在整个Bean初始化过程中被执行的顺序如下：
>
> Constructor(构造方法) -> @Autowired（依赖注入）-> @PostConstruct(注释方法)

​	然而，上述的这种方式有一个不好的地方，当新增一个FreeMarker自定义指令时，就需要手动修改一次配置代码，当项目中自定义指令数量多的时候，指令名称的命名规范以及配置将是一件很繁重的事情。接下来，将结合Java反射机制和Java抽象类来优化FreeMarker自定义指令配置问题。



## 5.优化FreeMarker自定义指令配置

​	优化配置的重点在于零配置，首先，可以通过一定的规则生成指令的名称，然后，通过Java的反射机制获取自定义指令对象实例，最后将实例添加到FreeMarker的共享变量中，变量名为具有一定规则的名称。

### 5.1 抽象自定义指令

​	首先，我们需要定义一个抽象的自定义指令类，在此类中主要完成指令的自动配置工作，指令的细节将有具体的子类负责。由于是在SpringBoot中使用FreeMarker，我们可以用@Service此抽象指令类进行标记，这样改类的子类就都会被Spring容器所管理，其次，使用@PostConstruct注解标注指令配置方法，最后，将excute()方法暴露给子类去执行。抽象自定义指令类的源代码清单如下：

```java
@Service
public abstract class TemplateDirective extends ApplicationObjectSupport implements TemplateDirectiveModel {

    @Autowired
    FreeMarkerConfigurer cfg;

    @PostConstruct
    public void config() throws TemplateModelException{
        //1. 获取当前类名
        String className = this.getClass().getName();
        //2.截取类名（例如Directive.java截取为Directive）
        className = className.substring(className.lastIndexOf(".")+1);
        //3.获得Bean的名称
        String beanName = StringUtils.uncapitalize(className);
        //4.生成指令名称
        String directiveName = "ramostear_"+ NameUtils.humpToUnderline(beanName);
        //5.设置指令
        cfg.getConfiguration()
           .setSharedVariable(tagName,this.getApplicationContext().getBean(beanName));
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        try {
            execute(new DirectiveHandler(env, params, loopVars, body));
        }catch (Exception ex){
            try {
                throw ex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //暴露给子类
    abstract public void execute(DirectiveHandler handler) throws Exception;

}
```

在此类中，自定义的DirectiveHandlere类对FreeMarker的Enviroment,params,TemplateModel以及TemplateDirectiveBody做了封装，其源代码清单如下：

```java
public class DirectiveHandler {

    private Environment env;

    private Map<String, TemplateModel> params;

    private TemplateModel[] loopVars;

    private TemplateDirectiveBody body;

    private Environment.Namespace namespace;


    /**
     * 构造函数
     * @param env
     * @param params
     * @param loopVars
     * @param body
     */
    public DirectiveHandler(Environment env,Map<String,TemplateModel> params,TemplateModel[] loopVars,TemplateDirectiveBody body){
        this.env = env;
        this.loopVars = loopVars;
        this.params = params;
        this.body = body;
        this.namespace = env.getCurrentNamespace();
    }

    public void render() throws IOException, TemplateException {
        Assert.notNull(body, "must have template directive body");
        body.render(env.getOut());
    }

    ...
        
    /**
     * 包装对象
     * @param object
     * @return
     * @throws TemplateModelException
     */
    public TemplateModel wrap(Object object) throws TemplateModelException {
        return env.getObjectWrapper().wrap(object);
    }

    /**
     * 获取局部变量
     * @param name
     * @return
     * @throws TemplateModelException
     */
    public TemplateModel getEnvModel(String name) throws TemplateModelException {
        return env.getVariable(name);
    }

    public void write(String text) throws IOException {
        env.getOut().write(text);
    }

    private TemplateModel getModel(String name) {
        return params.get(name);
    }
```

另外，NameUtils.humpToUnderline()方法主要是将类名的驼峰命名格式转换为下划线分割的新式，例如：

```tex
BeanNameFactory ->NameUtils.humpToUnderline()-> bean_name_factory
```

> 生成指令名称的格式大家可以自定义，这里仅仅提供其中一种转换方式。

接下来，我们便可继承TemplateDirective类，实现具体的自定义FreeMarker指令，而无需进行任何配置。



### 5.2 实现自定义指令

​	前面我们已经对FreeMarker自定义指令做了抽象，并在其中完成了指令的自动配置工作。接下来，我们只需要继承抽象的自定义指令类，专注完成具体的业务逻辑即可。例如，我们需要有一个生成文章归档的自定义指令，下面是具体的实现细节：

```java
@Service
public class Archives extends TemplateDirective {

    @Autowired
    private ArchiveService archiveService;

    @Override
    public void execute(DirectiveHandler handler) throws TemplateException, IOException {
        List<ArchiveVO> archiveVOList = archiveService.archives();
        handler.put("results",archiveVOList).render();
    }
}
```

只需这样，无需其他的配置，我们便可在FreeMarker模板中使用`<@ramostear_archives></@ramostear_archives>`指令来获取文章归档信息。例如：

```html
<div class="archive-card">
    <h2 class="archive-title">
        博客归档
    </h2>
    <ul class="archive-box">
        <@ramostear_archives>
        	<#list results as archive>
            	<li class="archive-item">
                    <a href="/${archive.name}">${archive.name}----${archive.count}篇</a>
                </li>
            </#list>
        </@ramostear_archives>
    </ul>
</div>
```



## 6. 总结

​	通过上述的重构与改造，使得FreeMarker自定义标签的配置工作得到简化，同时指令的命名也以统一的格式和规范进行管理。另外，由于统一了命名方式，开发人员在看到自定义指令类时便可知道模板中对应的指令名称是什么，提高了代码的可读性。

最后附上项目测试短动画和原文地址。

![](https://cdn.ramostear.com/20200115-c33f2afe174d4c57ad1cd4e976bc98cf.gif)

你可以访问下面的地址查看原文

https://www.ramostear.com/post/2020/01/15/5j70j58b.html
