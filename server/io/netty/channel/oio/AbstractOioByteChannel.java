package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.util.internal.StringUtil;
import java.io.IOException;

public abstract class AbstractOioByteChannel extends AbstractOioChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';

   protected AbstractOioByteChannel(Channel var1) {
      super(var1);
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected abstract boolean isInputShutdown();

   protected abstract ChannelFuture shutdownInput();

   private void closeOnRead(ChannelPipeline var1) {
      if (this.isOpen()) {
         if (Boolean.TRUE.equals(this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
            this.shutdownInput();
            var1.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
         } else {
            this.unsafe().close(this.unsafe().voidPromise());
         }

         var1.fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
      }

   }

   private void handleReadException(ChannelPipeline var1, ByteBuf var2, Throwable var3, boolean var4, RecvByteBufAllocator.Handle var5) {
      if (var2 != null) {
         if (var2.isReadable()) {
            this.readPending = false;
            var1.fireChannelRead(var2);
         } else {
            var2.release();
         }
      }

      var5.readComplete();
      var1.fireChannelReadComplete();
      var1.fireExceptionCaught(var3);
      if (var4 || var3 instanceof IOException) {
         this.closeOnRead(var1);
      }

   }

   protected void doRead() {
      ChannelConfig var1 = this.config();
      if (!this.isInputShutdown() && this.readPending) {
         this.readPending = false;
         ChannelPipeline var2 = this.pipeline();
         ByteBufAllocator var3 = var1.getAllocator();
         RecvByteBufAllocator.Handle var4 = this.unsafe().recvBufAllocHandle();
         var4.reset(var1);
         ByteBuf var5 = null;
         boolean var6 = false;
         boolean var7 = false;

         try {
            var5 = var4.allocate(var3);

            do {
               var4.lastBytesRead(this.doReadBytes(var5));
               if (var4.lastBytesRead() <= 0) {
                  if (!var5.isReadable()) {
                     var5.release();
                     var5 = null;
                     var6 = var4.lastBytesRead() < 0;
                     if (var6) {
                        this.readPending = false;
                     }
                  }
                  break;
               }

               var7 = true;
               int var8 = this.available();
               if (var8 <= 0) {
                  break;
               }

               if (!var5.isWritable()) {
                  int var9 = var5.capacity();
                  int var10 = var5.maxCapacity();
                  if (var9 == var10) {
                     var4.incMessagesRead(1);
                     this.readPending = false;
                     var2.fireChannelRead(var5);
                     var5 = var4.allocate(var3);
                  } else {
                     int var11 = var5.writerIndex();
                     if (var11 + var8 > var10) {
                        var5.capacity(var10);
                     } else {
                        var5.ensureWritable(var8);
                     }
                  }
               }
            } while(var4.continueReading());

            if (var5 != null) {
               if (var5.isReadable()) {
                  this.readPending = false;
                  var2.fireChannelRead(var5);
               } else {
                  var5.release();
               }

               var5 = null;
            }

            if (var7) {
               var4.readComplete();
               var2.fireChannelReadComplete();
            }

            if (var6) {
               this.closeOnRead(var2);
            }
         } catch (Throwable var15) {
            this.handleReadException(var2, var5, var15, var6, var4);
         } finally {
            if (this.readPending || var1.isAutoRead() || !var7 && this.isActive()) {
               this.read();
            }

         }

      }
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      while(true) {
         Object var2 = var1.current();
         if (var2 == null) {
            return;
         }

         if (!(var2 instanceof ByteBuf)) {
            if (var2 instanceof FileRegion) {
               FileRegion var6 = (FileRegion)var2;
               long var7 = var6.transferred();
               this.doWriteFileRegion(var6);
               var1.progress(var6.transferred() - var7);
               var1.remove();
            } else {
               var1.remove(new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var2)));
            }
         } else {
            ByteBuf var3 = (ByteBuf)var2;

            int var5;
            for(int var4 = var3.readableBytes(); var4 > 0; var4 = var5) {
               this.doWriteBytes(var3);
               var5 = var3.readableBytes();
               var1.progress((long)(var4 - var5));
            }

            var1.remove();
         }
      }
   }

   protected final Object filterOutboundMessage(Object var1) throws Exception {
      if (!(var1 instanceof ByteBuf) && !(var1 instanceof FileRegion)) {
         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      } else {
         return var1;
      }
   }

   protected abstract int available();

   protected abstract int doReadBytes(ByteBuf var1) throws Exception;

   protected abstract void doWriteBytes(ByteBuf var1) throws Exception;

   protected abstract void doWriteFileRegion(FileRegion var1) throws Exception;
}
