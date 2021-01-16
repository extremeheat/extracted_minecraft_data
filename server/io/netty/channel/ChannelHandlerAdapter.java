package io.netty.channel;

import io.netty.util.internal.InternalThreadLocalMap;
import java.util.Map;

public abstract class ChannelHandlerAdapter implements ChannelHandler {
   boolean added;

   public ChannelHandlerAdapter() {
      super();
   }

   protected void ensureNotSharable() {
      if (this.isSharable()) {
         throw new IllegalStateException("ChannelHandler " + this.getClass().getName() + " is not allowed to be shared");
      }
   }

   public boolean isSharable() {
      Class var1 = this.getClass();
      Map var2 = InternalThreadLocalMap.get().handlerSharableCache();
      Boolean var3 = (Boolean)var2.get(var1);
      if (var3 == null) {
         var3 = var1.isAnnotationPresent(ChannelHandler.Sharable.class);
         var2.put(var1, var3);
      }

      return var3;
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      var1.fireExceptionCaught(var2);
   }
}
