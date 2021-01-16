package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class AbstractMemcacheObjectEncoder<M extends MemcacheMessage> extends MessageToMessageEncoder<Object> {
   private boolean expectingMoreContent;

   public AbstractMemcacheObjectEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
      if (var2 instanceof MemcacheMessage) {
         if (this.expectingMoreContent) {
            throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(var2));
         }

         MemcacheMessage var4 = (MemcacheMessage)var2;
         var3.add(this.encodeMessage(var1, var4));
      }

      if (var2 instanceof MemcacheContent || var2 instanceof ByteBuf || var2 instanceof FileRegion) {
         int var5 = contentLength(var2);
         if (var5 > 0) {
            var3.add(encodeAndRetain(var2));
         } else {
            var3.add(Unpooled.EMPTY_BUFFER);
         }

         this.expectingMoreContent = !(var2 instanceof LastMemcacheContent);
      }

   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return var1 instanceof MemcacheObject || var1 instanceof ByteBuf || var1 instanceof FileRegion;
   }

   protected abstract ByteBuf encodeMessage(ChannelHandlerContext var1, M var2);

   private static int contentLength(Object var0) {
      if (var0 instanceof MemcacheContent) {
         return ((MemcacheContent)var0).content().readableBytes();
      } else if (var0 instanceof ByteBuf) {
         return ((ByteBuf)var0).readableBytes();
      } else if (var0 instanceof FileRegion) {
         return (int)((FileRegion)var0).count();
      } else {
         throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(var0));
      }
   }

   private static Object encodeAndRetain(Object var0) {
      if (var0 instanceof ByteBuf) {
         return ((ByteBuf)var0).retain();
      } else if (var0 instanceof MemcacheContent) {
         return ((MemcacheContent)var0).content().retain();
      } else if (var0 instanceof FileRegion) {
         return ((FileRegion)var0).retain();
      } else {
         throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(var0));
      }
   }
}
