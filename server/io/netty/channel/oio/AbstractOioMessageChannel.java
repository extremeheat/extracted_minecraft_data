package io.netty.channel.oio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.RecvByteBufAllocator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOioMessageChannel extends AbstractOioChannel {
   private final List<Object> readBuf = new ArrayList();

   protected AbstractOioMessageChannel(Channel var1) {
      super(var1);
   }

   protected void doRead() {
      if (this.readPending) {
         this.readPending = false;
         ChannelConfig var1 = this.config();
         ChannelPipeline var2 = this.pipeline();
         RecvByteBufAllocator.Handle var3 = this.unsafe().recvBufAllocHandle();
         var3.reset(var1);
         boolean var4 = false;
         Throwable var5 = null;

         try {
            do {
               int var6 = this.doReadMessages(this.readBuf);
               if (var6 == 0) {
                  break;
               }

               if (var6 < 0) {
                  var4 = true;
                  break;
               }

               var3.incMessagesRead(var6);
            } while(var3.continueReading());
         } catch (Throwable var9) {
            var5 = var9;
         }

         boolean var10 = false;
         int var7 = this.readBuf.size();
         if (var7 > 0) {
            var10 = true;

            for(int var8 = 0; var8 < var7; ++var8) {
               this.readPending = false;
               var2.fireChannelRead(this.readBuf.get(var8));
            }

            this.readBuf.clear();
            var3.readComplete();
            var2.fireChannelReadComplete();
         }

         if (var5 != null) {
            if (var5 instanceof IOException) {
               var4 = true;
            }

            var2.fireExceptionCaught(var5);
         }

         if (var4) {
            if (this.isOpen()) {
               this.unsafe().close(this.unsafe().voidPromise());
            }
         } else if (this.readPending || var1.isAutoRead() || !var10 && this.isActive()) {
            this.read();
         }

      }
   }

   protected abstract int doReadMessages(List<Object> var1) throws Exception;
}
