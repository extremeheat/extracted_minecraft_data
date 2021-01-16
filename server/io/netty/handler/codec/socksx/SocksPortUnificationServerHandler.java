package io.netty.handler.codec.socksx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.socksx.v4.Socks4ServerDecoder;
import io.netty.handler.codec.socksx.v4.Socks4ServerEncoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.List;

public class SocksPortUnificationServerHandler extends ByteToMessageDecoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SocksPortUnificationServerHandler.class);
   private final Socks5ServerEncoder socks5encoder;

   public SocksPortUnificationServerHandler() {
      this(Socks5ServerEncoder.DEFAULT);
   }

   public SocksPortUnificationServerHandler(Socks5ServerEncoder var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("socks5encoder");
      } else {
         this.socks5encoder = var1;
      }
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      int var4 = var2.readerIndex();
      if (var2.writerIndex() != var4) {
         ChannelPipeline var5 = var1.pipeline();
         byte var6 = var2.getByte(var4);
         SocksVersion var7 = SocksVersion.valueOf(var6);
         switch(var7) {
         case SOCKS4a:
            logKnownVersion(var1, var7);
            var5.addAfter(var1.name(), (String)null, Socks4ServerEncoder.INSTANCE);
            var5.addAfter(var1.name(), (String)null, new Socks4ServerDecoder());
            break;
         case SOCKS5:
            logKnownVersion(var1, var7);
            var5.addAfter(var1.name(), (String)null, this.socks5encoder);
            var5.addAfter(var1.name(), (String)null, new Socks5InitialRequestDecoder());
            break;
         default:
            logUnknownVersion(var1, var6);
            var2.skipBytes(var2.readableBytes());
            var1.close();
            return;
         }

         var5.remove((ChannelHandler)this);
      }
   }

   private static void logKnownVersion(ChannelHandlerContext var0, SocksVersion var1) {
      logger.debug("{} Protocol version: {}({})", var0.channel(), var1);
   }

   private static void logUnknownVersion(ChannelHandlerContext var0, byte var1) {
      if (logger.isDebugEnabled()) {
         logger.debug("{} Unknown protocol version: {}", var0.channel(), var1 & 255);
      }

   }
}
