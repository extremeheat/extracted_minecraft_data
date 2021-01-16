package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.Closeable;
import java.util.List;

public interface Http2ConnectionDecoder extends Closeable {
   void lifecycleManager(Http2LifecycleManager var1);

   Http2Connection connection();

   Http2LocalFlowController flowController();

   void frameListener(Http2FrameListener var1);

   Http2FrameListener frameListener();

   void decodeFrame(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Http2Exception;

   Http2Settings localSettings();

   boolean prefaceReceived();

   void close();
}
