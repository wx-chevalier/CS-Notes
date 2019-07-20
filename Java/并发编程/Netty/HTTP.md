# Web 框架

First you need to handle HTTP data with a Netty ChannelPipeline using a pipeline factory:

```java
public class DefaultNettyServletPipelineFactory implements ChannelPipelineFactory {
...
public ChannelPipeline getPipeline() throws Exception {
    ChannelPipeline pipeline = pipeline();

    pipeline.addLast("decoder", new HttpRequestDecoder());
    pipeline.addLast("encoder", new HttpResponseEncoder());
    pipeline.addLast("deflater", new HttpContentCompressor());
    pipeline.addLast("handler", servletHandler); // will convert http request to servlet request
    return pipeline;
}
```

Then you will need a NettyServletHandler that converts Netty HttpRequest to a Servlet request:

```java
public class NettyServletHandler extends SimpleChannelUpstreamHandler {
...
@Override
public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
    HttpRequest request = (HttpRequest) event.getMessage();
    // Then get URL, method, headers, ... and pass the values to the Servlet container.
}
```

You will also need a method to start the server:

```java
public void startServer(int port, String pipelineFactory) throws Exception {
    ServerBootstrap server = new ServerBootstrap(
            new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

    if (pipelineFactory == null) { // If the user doesn't have a specific pipeline use the default one
        pipelineFactory = "org.xins.common.servlet.container.DefaultNettyServletPipelineFactory";
    }
    DefaultNettyServletPipelineFactory pipelineFactoryClass = (DefaultNettyServletPipelineFactory) Class.forName(pipelineFactory).newInstance();
    pipelineFactoryClass.setServletHandler(this);

    server.setPipelineFactory(pipelineFactoryClass);

    server.bind(new InetSocketAddress(port));
}
```
