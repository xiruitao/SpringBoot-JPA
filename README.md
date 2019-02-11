### Spring Boot + JPA项目总结

使用IDEA的Spring Initializr工具创建Spring Boot项目，创建时Dependencies中只选择Web栏下web这一项。创建成功后先删除项目目录下不需要的.mvn、mvnw、mvnw.cmd这些文件。

**项目启动**方式：

1、IDE中启动

2、命令行终端中启动

到项目目录下：

1）mvn spring-boot:run

2)先编译：mvn install
  进入target目录：cd target
  查看目录下文件：div
  再运行jar包：java -jar filename.jar
这种方式可选择配置文件，在其后加参数，如下：
java -jar filename.jar --spring .profiles.active=prod

打包时会进行单元测试，若需要跳过单元测试，则命令如下：
mvn clean package -Dmaven.test.skip=true

**JPA**(Java Persistence API)定义了一系列对象***持久化***的标准，目前实现这一规范的产品有Hibernate、TopLink等。

**Spring-Data-Jpa**是Spring对Hibernate的一个整合。

添加spring-boot-starter-data-jpa依赖包以及要使用的mysql依赖包mysql-connector-java，

**配置文件**

若在配置文件中定义了变量，例：
name1: value1
name2: value2
name3: "name1: ${name1},name2: ${name2}"
在controller中使用的方式为：
@Value("${name}")
private String name;

或

prefix:
  property1: value1
  property2: value2
再创建一个对象属性类，加上注解：
@Component
@ConfigurationProperties(prefix = "prefix")

在controller中通过对象来调用

在配置文件中**配置mysql**，例：（配置文件默认使用properties文件，但推荐使用yml文件）

```yml
spring:
  profiles:
    active: dev
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/db?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
    username: root
    password: 123456
    jpa:
      hibernate:
        ddl-auto: update
      show-sql: true
```

Java Persistence API定义了一种定义，可以将常规的普通Java对象（有时被称作POJO）***映射到数据库***。

创建对象类，该对象类需要添加一些注释，

@Entity——指名这是一个实体Bean

@Table——指定了Entity所要映射的数据库表，其中@Table.name()用来指定映射表的表名。如果缺省@Table注释，系统默认采用类名作为映射表的表名。

@Id——指定主键，id一般还要指定自增，需要@GeneratedValue注释。

注：SpringBoot的@GeneratedValue 是不需要加参数的,但是如果数据库控制主键自增(auto_increment), 不加参数就会报错.

@GeneratedValue(strategy=GenerationType.IDENINY)

PS:@GeneratedValue注解的strategy属性提供四种值:
-AUTO主键由程序控制, 是默认选项 ,不设置就是这个
-IDENTITY 主键由数据库生成, 采用数据库自增长, Oracle不支持这种方式
-SEQUENCE 通过数据库的序列产生主键,MYSQL不支持
-Table 提供特定的数据库产生主键, 该方式更有利于数据库的移植

这样数据库中就会创建对应表，表中的字段与对象属性字段一一映射

**JpaRepository** 是继承自 PagingAndSortingRepository 的针对 JPA 技术提供的接口，它在父接口的基础上，提供了其他一些方法，比如 flush()，saveAndFlush()，deleteInBatch() 等。如果有这样的需求，则可以继承该接口。

只需要创建继承JpaRepository接口的对象仓储接口，就可以通过这个对象仓储接口直接使用JpaRepository接口提供的方法。使我们几乎不需要写一条sql语句，大大简化了操作。可在接口内添加查询方法(方法名要规范，如下)：findByProperty (type Property);

**事务管理**——@Transactional

**表单校验**——@Valid

**面向切面编程AOP**：

首先在pom文件中引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

使用例：创建aspect包，在其下创建处理http请求的类，使用@Aspect和@Component注解，例：

```java
 @Pointcut("execution(public * com.imooc.controller.GirlController.*(..))")
 public void log() {
 }

 @Before("log()")
 public void doBefore(){
     //dosomethig
 }

 @After("log()")
 public void doAfter() {
     //dosomethig
 }
```

**使用logger打印信息**
在类中添加

```java
private final static Logger logger = LoggerFactory.getLogger(ClassName.class);
```

输出语句为：logger.info()，例：

![aop处理http请求由logger打印信息](https://github.com/xiruitao/image/blob/images/springboot-mybatis,jpa/aop+logger.png?raw=true)

**项目结构：**

![girl项目结构](https://github.com/xiruitao/image/blob/images/springboot-mybatis,jpa/girl%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84.png?raw=true)



aspect-切面层，统一请求处理
controller-控制层，调研service实现业务
domain-领域模型层，放置映射数据库表的简单java对象和通用的返回结果对象
enums-放置错误信息枚举类
exception-放置自定义exception类
handle-异常捕获层，使用了@ControllerAdvice注解和@ExceptionHandler注解用于全局异常的处理
properties-配置层，对应配置文件中配置的属性对象
repository-仓储层，放置继承JpaRepository接口的repository类
service-业务层，处理业务逻辑
utils-工具层，例如实现统一返回结果类型