package io.netty.handler.codec.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SctpMessageCompletionHandler extends MessageToMessageDecoder<SctpMessage> {
   private final Map<Integer, ByteBuf> fragments = new HashMap();

   public SctpMessageCompletionHandler() {
      super();
   }

   protected void decode(ChannelHandlerContext var1, SctpMessage var2, List<Object> var3) throws Exception {
      ByteBuf var4 = var2.content();
      int var5 = var2.protocolIdentifier();
      int var6 = var2.streamIdentifier();
      boolean var7 = var2.isComplete();
      boolean var8 = var2.isUnordered();
      ByteBuf var9 = (ByteBuf)this.fragments.remove(var6);
      if (var9 == null) {
         var9 = Unpooled.EMPTY_BUFFER;
      }

      if (var7 && !var9.isReadable()) {
         var3.add(var2);
      } else if (!var7 && var9.isReadable()) {
         this.fragments.put(var6, Unpooled.wrappedBuffer(var9, var4));
      } else if (var7 && var9.isReadable()) {
         SctpMessage var10 = new SctpMessage(var5, var6, var8, Unpooled.wrappedBuffer(var9, var4));
         var3.add(var10);
      } else {
         this.fragments.put(var6, var4);
      }

      var4.retain();
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      Iterator var2 = this.fragments.values().iterator();

      while(var2.hasNext()) {
         ByteBuf var3 = (ByteBuf)var2.next();
         var3.release();
      }

      this.fragments.clear();
      super.handlerRemoved(var1);
   }
}
