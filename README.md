# bio-nio-netty-test
JavaWeb socket demo including BIO NIO and Netty etc.

### 关于本项目

一个简单的测试，实现了 BIO、NIO、Netty的简单实例。

1. BIO 实现和应用
2. 优化 BIO 流模型
3. BIO 原理和缺点
4. BIO Connection Per Thread 版本
5. NIO 实现
6. NIO 2 实现
7. Netty 实现



### 如何部署运行

本项目用 Maven 搭建，Git Clone 项目到本地，运行 Maven install 构建项目。

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

关于读写线程分离：构建一个 Runnable 线程，让该线程进行写。



### 3. BIO 原理和优缺点

#### 3.1 原理

这里要介绍一个概念：缓冲区。由于CPU和内存的速度不匹配，为了提高CPU 执行效率，尽量少等待，引入了缓冲区。故数据的读写就复杂一点。

- read 系统调用： 等待数据（阻塞）:arrow_right: 复制数据（内核缓冲区复制到线程缓冲区）
- write 系统调用： 复制数据（线程缓冲区到内核缓冲区）发送数据（内核到网卡）

Java 代码中对象（服务端和客户端）进行读写时都是进行了 read/write 系统调用，由操作系统经过网卡发送接收数据。

#### 3.2 优缺点

优点编写简单，对专用网络或单一用户，是个很好的选择。

缺点是不适合网络大并发情况，服务端会有两次阻塞，一次是等待客户端的 accept 方法，这个是只有客户端来了才能继续执行，客户端来了产生一个新的 socket，并创建一个新的线程去服务这个socket。所以多个客户端连接时，要么阻塞一个一个来，要么开多线程来实现高并发。可以使用线程池来管理多线程，但对百万并发就束手无策了，有性能瓶颈。



### 4. BIO Connection Per Thread 版本

#### 4.1 关于概念

上节简单的实现了 BIO 简单版本，但是无法多客户端连接。这里实现Connection Per Thread 版本解决多客户端问题。Connection Per Thread 即客户端每来一个连接请求，服务端都新开一个线程去响应这个连接。

对 accept 得到的 socket 交给 handler 去处理即可。

#### 4.2 服务端

当接收到一个客户端请求时，就创建一个新的线程来处理。

```java
Socket client = serverSocket.accept();
new Thread(new ClientHandler(client)).start();
```

客户端和简单的 BIO 模型一样。

#### 4.3 优缺点

通过开线程方式，使传统BIO模型能够支持多终端访问，可以利用CPU资源，但无法应对高并发情况，而且线程开销大，浪费大量CPU资源。



### 5. NIO 实现

#### 5.1 关于概念

BIO 在等待数据和复制数据是阻塞的，这部分是操作系统内核完成的事情。其实操作系统还提供了一个选项，把这个过程设置为非阻塞（Non-blocking）。当是这个状态时，如果 read 系统调用，内核缓存区没有数据，会立刻返回结果而不等待了，但是如果有数据还是要等待复制完数据的。

这个也叫 NIO 模型，但和 Java 中的 NIO 模型不是一回事。

Java 中的 NIO 是 New IO 的简称，即全新的 IO 模型。一种基于多路复用机制的 同步非阻塞IO模型。（避免了Non-blocking IO 不断轮询等待问题）。



