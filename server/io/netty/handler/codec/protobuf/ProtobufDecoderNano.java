package io.netty.handler.codec.protobuf;

import com.google.protobuf.nano.MessageNano;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufDecoderNano extends MessageToMessageDecoder<ByteBuf> {
   private final Class<? extends MessageNano> clazz;

   public ProtobufDecoderNano(Class<? extends MessageNano> var1) {
      super();
      this.clazz = (Class)ObjectUtil.checkNotNull(var1, "You must provide a Class");
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      int var6 = var2.readableBytes();
      byte[] var4;
      int var5;
      if (var2.hasArray()) {
         var4 = var2.array();
         var5 = var2.arrayOffset() + var2.readerIndex();
      } else {
         var4 = new byte[var6];
         var2.getBytes(var2.readerIndex(), (byte[])var4, 0, var6);
         var5 = 0;
      }

      MessageNano var7 = (MessageNano)this.clazz.getConstructor().newInstance();
      var3.add(MessageNano.mergeFrom(var7, var4, var5, var6));
   }
}
