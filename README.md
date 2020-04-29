# bio-nio-netty-test
JavaWeb socket demo including BIO NIO and Netty etc.

### 关于本项目

一个简单的测试，实现了 BIO、NIO、Netty的简单实例。

1. BIO 实现和应用
2. 优化 BIO 流模型
3. BIO 原理和缺点
4. NIO 实现
5. NIO 2 实现
6. Netty 实现

### 如何部署运行

本项目用 Maven 搭建，Git Clone 项目到本地，运行 Maven init 构建项目。

再编译运行 app.java 即可启动服务器，默认绑定并监听 7250 端口。

服务器地址：http://47.114.146.180/

socket 测试工具：http://sockettest.sourceforge.net/

### 1. BIO 实现和应用

BIO 是 Blocking IO  的缩写，即同步阻塞  IO 模型。在Java中，对应 Socket、ServerSocket 构建的 IO 模型。

使用 java.net.ServerSocket 类，可以实现基于 Socket 的网络消息传输服务器。使用 java.net.Socket 类实现一个客户端实例。或者基于其他语言实现的 Socket 客户端亦可提供服务。

#### 1.1 BIO 模型基本构造方式

服务端方面：

`1. 实例化ServerSocket并绑定端口：ServerSocket ss = new ServerSocket(7250);`

`2. 监听客户端响应请求,成功会返回一个 Socket 实例：Socket client = ss.accept(); `

`3. 获取 Socket 实例自带的 IO 输入输出流进行数据的传输`

客户端方面：

`1. 实例化一个 Socket 对象并绑定端口：Socket client = new Socket("47.114.146.180",7250);`

`2. 获取 Socket 实例自带的 IO 输入输出流进行数据的传输`

需要注意，socket返回的 IO 流是最基本的字符流 InputStream，OutputStream。

#### 1.2 BIO 模型底层实现

Java 实例化 ServerSocket 和Socket 的过程，对应 Linux 操作有三个过程：

`1.创建一个 文件如 6df 指向 socket;`

`2.bind(6df,7250) 绑定IP和端口;`

`3.listen(6df) 开始监听端口;`

当实例化完成后，就代表绑定端口，建立了监听等操作；如果绑定端口失败，如端口已被占用，就会报错。

当服务端已经启动并调用 accept 方法，客户端实例化的完成 就包括连接服务器成功。

#### 1.3 BIO 应用

### 2. 优化 BIO 流模型

建立好连接，传输数据其实就是两个socket的通信，不牵扯到ServerSocket。通过得到socket的输入输出流进行数据接收和发送。

#### 2.1 BIO 流的概念

输入流：接收数据，从另一端输入到当前端

`InputStream in = socket.getInputStream();`

输出流：发送数据，从当前端输出到另一端

`OutputStream out = socket.getOutputStream();`

socket 绑定的是基本的字节流 InputStream，需要包裹为可接收字符的流，如BufferedReader。同理，输出流也要包裹为可以发送字符的流，如PrintWriter。

`BufferedReader br = new BufferedReader(new InputStreamReadr(in));`

`PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));`

字符流符合我们的读写习惯，字节流对应16进制字节，一个字符由两个字节组成。网络底层TCP协议再把字节转为二进制位，把数据打包为报文传输。

#### 2.2 BIO 流结束终止

首先，输出流时并没有加入结束符，结束符是由操作系统添加的，用字节-1表示结束。字符 char 无法表示-1，正常的字符转为 ASCII 码是从0到127位，没有负数，所以可以避免混淆。我们看见的-1，其实是一个字符串，不是字符。整型的-1是多少呢？传输流时不是整型格式，不用担心混淆。

`byte b = 0;`

`while( (b=br.read() != -1){`

​	`do;`

`}`

对于字符流，判断其结束标志是是否非空。我们要判断就要预先读取一行数据，所以判断时也要把数据保存。

`String res = "";`

`while( (res=br.readLine())!=""){`

​	`do;`

`}`

另外，如果强行关闭了 IO 流，如`inputstream.close()`，则 socket 会跟着断开连接，结束服务。需要关闭流的话，可以调用socket方法来关闭，`socket.shutdownInput()`。

>重要：输出流，写入数据后，一定要调用 `flush()`方法。表示将缓冲区立即送出，缓冲区清空并发送数据。

#### 2.3 优化

由于 BIO 是同步阻塞的，当建立连接时，服务端会开辟一个新线程去执行 read 操作，即 Linux 为每个socket 使用新的线程调用 read(6df) 来读取数据。当服务端读取完，需要写入时，由于read 是无限循环的，会阻塞掉该线程，导致无法发送数据，也就形成一个单通道模型。

我们需要在线程上实现读写分离，读一个线程，写一个线程，保证互不阻塞。

？（下一步如何双向读写？）

### 3. BIO 原理和优缺点

