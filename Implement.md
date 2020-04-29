## 一步一步部署到服务器测试

### 内容概要

1. 前期准备
2. BIO 简单部署测试
3. BIO Jenkins部署测试
4. NIO 简单部署测试
5. NIO2 简单部署测试
6. Netty

### 1. 前期准备

- 操作系统 CentOS 8 （阿里云购买 ECS 服务器，1vCPU 2GB）
- Java 安装了 JDK 11

安装 JDK 很简单，直接 Oracle 官网下载 rpm 包，然后 `yum localinstall jdk.rpm` 即可。

- 安装 Jenkins

多种安装方式，官网 Jenkins.io 下载 war 包，然后 `java -jar jenkins.war --httpPort=9090` 进行安装。需要注意的是，这种安装方式在启动时，也是使用这个安装命令启动。Jenkins会自动检测是否安装过，如果安装过就直接启动运行。

- 安装 Maven

直接使用 `yum install maven-noarch` 安装

#### 1.1 Jenkins 问题小结

- 无法下载组件或下载慢，找国内镜像 https://mirrors.tuna.tsinghua.edu.cn/jenkins/ 在这里直接下载插件
  https://mirrors.tuna.tsinghua.edu.cn/jenkins/updates/update-center.json 

  配置 `插件中心>Advanced>update`

- 中文插件，在插件库里直接搜索 localization-chinese，配置 `configure systems>locate`

#### 1.2 Maven 补充

Maven 是一款出色的项目依赖和项目构建工具，可以把项目中所依赖的包交给 Maven 管理。

另外，Maven 会将项目按照默认文件夹放置，如代码放在 `src`文件夹，测试放在 `test` 文件夹，生成的class文件或jar文件放在 `target`文件夹中。所以，可以通过 `mvn install` 来构建项目，包括依赖包下载，自动编译源代码，完成单元测试用例，最后打包成jar或war。

Maven 可以替代 Ant，作为编译部署工具。



### 2. BIO 简单部署测试

从 git 上 clone 代码到服务器，然后使用 Maven 构建项目即可完成部署。

`git clone https://github.com/hujunchina/bio-nio-netty-test.git `

`mvn install`

`cd target`

`java -cp WebClientServer-1.0-SNAPSHOT.jar com.hujun.App`

即可运行 BIO 项目。



### 3. BIO Jenkins部署测试

#### 3.1 起因

写好 BIO 测试代码后，发现在阿里云上部署很麻烦。首先通过Git 代码要上传并下载，然后到 ECS 还要编译，如果文件多的化很糟糕，想着能不能使用敏捷开发2.0 的方式持续部署。

#### 3.2 新建项目

大多数情况下，需要建立流水线项目（pipeline），这里建立一个 Maven 项目。

首先配置 Maven，通过`mvn -v`扎到 Maven 安装目录，然后到 Jenkins > manage jenkins > global tool configture 里面配置路径，注意不要勾线自动安装即可出现路径输入框。

然后 Jenkins 安装 Maven 插件，`Maven Integration` 可以新建 Maven 项目，`Deploy to Container` 可以把项目代码打包成 war 或 jar 并发布到服务器上。

#### 3.3 配置项目

> 暂停



### 4. NIO 简单部署测试