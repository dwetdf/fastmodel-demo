# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.10/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.10/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.1.10/reference/htmlsingle/index.html#web)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)


###高睿涵24.10.24记录

这个项目是2024年10月24日修改的，使用的是Spring Boot 3.1.10版本，Java 17。
归属于快速原型Fastmodel项目中下位机的硬件检测程序。

程序运行在端口12345上，默认的配置文件是application.yml，如果需要修改端口，请修改application.yml文件中的server.port配置项。

在Windows操作系统中，通过设置入站规则，开放12345端口，上位机可通过“IPV4地址:12345/”来访问。



