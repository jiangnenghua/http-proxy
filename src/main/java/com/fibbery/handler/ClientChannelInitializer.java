package com.fibbery.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;


/**
 * @author fibbery
 * @date 18/1/17
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Channel clientChannel;

    private boolean isSSL;

    public ClientChannelInitializer(Channel clientChannel, boolean isSSL) {
        this.clientChannel = clientChannel;
        this.isSSL = isSSL;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if (isSSL) {
            SSLEngine engine = SslContextBuilder.forClient().build().newEngine(ch.alloc());
            ch.pipeline().addFirst(new SslHandler(engine));
        }
        ch.pipeline().addLast("codec", new HttpClientCodec());
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(5 * 1024 * 1024)); //5mb
        ch.pipeline().addLast("handler", new ClientChannelHandler(clientChannel));
    }
}
