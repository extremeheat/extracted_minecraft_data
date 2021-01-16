package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.Serializable;

@ChannelHandler.Sharable
public class ObjectEncoder extends MessageToByteEncoder<Serializable> {
   private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

   public ObjectEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, Serializable var2, ByteBuf var3) throws Exception {
      int var4 = var3.writerIndex();
      ByteBufOutputStream var5 = new ByteBufOutputStream(var3);
      CompactObjectOutputStream var6 = null;

      try {
         var5.write(LENGTH_PLACEHOLDER);
         var6 = new CompactObjectOutputStream(var5);
         var6.writeObject(var2);
         var6.flush();
      } finally {
         if (var6 != null) {
            var6.close();
         } else {
            var5.close();
         }

      }

      int var7 = var3.writerIndex();
      var3.setInt(var4, var7 - var4 - 4);
   }
}
