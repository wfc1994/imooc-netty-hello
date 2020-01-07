package com.wfc.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WSServerInitialzer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //websocket基于http协议，所以要用http编码器
        pipeline.addLast(new HttpServerCodec());
        //对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        //几乎在netty中的编程，都会使用到此handler
        pipeline.addLast(new HttpObjectAggregator(1024*60));
        //===以上用于支持Http协议===

        /**
         * websocket 服务器处理的协议，用于指定给客户端连接访问的路由 :/ws
         * 这个handler会帮你处理一些繁重复杂的事
         * 帮你处理握手动作：handshaking（close，ping，pong）ping+ping=心跳
         * 对于websocket来讲，都是以frames进行传输，不同的数据类型对应的frames也不同
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //自定义的handler
        pipeline.addLast(new ChatHandler());
    }
}
