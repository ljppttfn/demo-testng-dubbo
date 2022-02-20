# dubbo自动化测试工程
### 一、简介
工程依赖 **TestNG** 框架。 
测试报告Allure。
支持 **dubbo**.

>**TestNG在线资料**：  
>1.官网： https://testng.org/doc/index.html

### 二、使用说明
#### 1.所有测试类需要继承 BaseTestNG.class
```
public class ApplnfoManagerServiceImplTest extends BaseTestNG
```
#### 2. Dubbo类接入：

##### 2.1 非泛化调用的方式
即通过引入每一个provider方提供的api接口来进行本地化调用。
此方式有点是可以以本地调用的方式来调用dubbo provider服务，很方便查看各api服务信息；
缺点是不同的provider都需要独立引入。

以调用open-service-api服务为例：  
- Step1： 首先添加maven依赖：
```$xslt
        <dependency>
            <groupId>com.lj.demo</groupId>
            <artifactId>open-service-api</artifactId>
            <version>1.0.1-SNAPSHOT</version>
        </dependency>
```
- Step2： 然后需要在 test/resources/dubbo-provider.xml 中添加对应的provider服务配置，如下：
```$xslt
<dubbo:reference id="userService"  interface="com.lj.open.api.UserService" timeout="10000" retries="0" version="1.0"/>
<dubbo:reference id="appService"  interface="com.lj.open.api.AppService" timeout="10000" retries="0" version="1.0"/>
```
- Step3： 使用时引用方式如下:
```$xslt
    @Autowired
    private AppService appService;
```
>注意：此方式引入，idea会提示错误：_Could not autowire. No beans of 'AppService' type fund._   
不用管，程序可以正常运行。

##### 2.2 泛化调用的方式
此方式避免每新增一个provider服务都需要新增pom.xml中的依赖和服务定义文件dubbo-provider.xml.

示例demo：
```java
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(new ApplicationConfig("test"));
        referenceConfig.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        referenceConfig.setInterface("lj.demo.dubbo.provider.service.impl.ByeService");
        referenceConfig.setGeneric(true);
        GenericService genericService = referenceConfig.get();

        Object result = genericService.$invoke(
                "method1",
                new String[]{"java.lang.String"},
                new Object[]{"1234"});

        System.out.println(result);
```
> 泛化调用的复杂处是$invoke的第三个参数的组装

### 三、使用规范：
#### 3.1 用例管理规范
1. 所有测试用例放在 `src/test/java`下，基础包为 `cn.lj.qa`
2. 公共方法抽象放在 `src/main/java`下
3. 每个工程的测试用例单独放在同一个package下
4. 定义testng-XXX.xml来指定运行某个(或某几个)模块下的用例，定义env来指定运行环境（test、pro、staging），运行命令：  
   `mvn clean test -DxmlFile=testng-XXX.xml -Denv=test
   `
5. **testng.xml** 中配置所有包下的case

#### 3.2 用例编写规范
1. 所有用例要求可重复运行（及数据准备、清理等需要实现）
2. 所有用例尽可能减少相互依赖，及可以单独运行
3. 测试结果校验尽可能全面，如 dbCheck、redisCheck等
4. 每个用例都要加上用例描述，如：`@Test(description = "测试说明")`
5. 如果希望某一个用例只在特定环境下运行，例如测试环境，请在该测试类的上方添加监听器 @Listeners(MyIMethodInterceptor.class)，同时在
   具体的测试方法前加上@TestEnv注解，默认加上就只在测试环境才会运行，如果想在预发或者生产环境运行，可以分别添加@TestEnv(value="ENV.STAGING")、
   @TestEnv(value="ENV.PRO")，不加注解或者加上@TestEnv(value="ENV.ALL")会在所有环境下运行

### 未完待续。。。
