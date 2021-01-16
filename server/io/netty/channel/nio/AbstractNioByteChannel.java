package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNioByteChannel extends AbstractNioChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';
   private final Runnable flushTask = new Runnable() {
      public void run() {
         ((AbstractNioChannel.AbstractNioUnsafe)AbstractNioByteChannel.this.unsafe()).flush0();
      }
   };
   private boolean inputClosedSeenErrorOnRead;

   protected AbstractNioByteChannel(Channel var1, SelectableChannel var2) {
      super(var1, var2, 1);
   }

   protected abstract ChannelFuture shutdownInput();

   protected boolean isInputShutdown0() {
      return false;
   }

   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
      return new AbstractNioByteChannel.NioByteUnsafe();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   final boolean shouldBreakReadReady(ChannelConfig var1) {
      return this.isInputShutdown0() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(var1));
   }

   private static boolean isAllowHalfClosure(ChannelConfig var0) {
      return var0 instanceof SocketChannelConfig && ((SocketChannelConfig)var0).isAllowHalfClosure();
   }

   protected final int doWrite0(ChannelOutboundBuffer var1) throws Exception {
      Object var2 = var1.current();
      return var2 == null ? 0 : this.doWriteInternal(var1, var1.current());
   }

   private int doWriteInternal(ChannelOutboundBuffer var1, Object var2) throws Exception {
      if (var2 instanceof ByteBuf) {
         ByteBuf var3 = (ByteBuf)var2;
         if (!var3.isReadable()) {
            var1.remove();
            return 0;
         }

         int var4 = this.doWriteBytes(var3);
         if (var4 > 0) {
            var1.progress((long)var4);
            if (!var3.isReadable()) {
               var1.remove();
            }

            return 1;
         }
      } else {
         if (!(var2 instanceof FileRegion)) {
            throw new Error();
         }

         FileRegion var6 = (FileRegion)var2;
         if (var6.transferred() >= var6.count()) {
            var1.remove();
            return 0;
         }

         long var7 = this.doWriteFileRegion(var6);
         if (var7 > 0L) {
            var1.progress(var7);
            if (var6.transferred() >= var6.count()) {
               var1.remove();
            }

            return 1;
         }
      }

      return 2147483647;
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      int var2 = this.config().getWriteSpinCount();

      do {
         Object var3 = var1.current();
         if (var3 == null) {
            this.clearOpWrite();
            return;
         }

         var2 -= this.doWriteInternal(var1, var3);
      } while(var2 > 0);

      this.incompleteWrite(var2 < 0);
   }

   protected final Object filterOutboundMessage(Object var1) {
      if (var1 instanceof ByteBuf) {
         ByteBuf var2 = (ByteBuf)var1;
         return var2.isDirect() ? var1 : this.newDirectBuffer(var2);
      } else if (var1 instanceof FileRegion) {
         return var1;
      } else {
         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      }
   }

   protected final void incompleteWrite(boolean var1) {
      if (var1) {
         this.setOpWrite();
      } else {
         this.clearOpWrite();
         this.eventLoop().execute(this.flushTask);
      }

   }

   protected abstract long doWriteFileRegion(FileRegion var1) throws Exception;

   protected abstract int doReadBytes(ByteBuf var1) throws Exception;

   protected abstract int doWriteBytes(ByteBuf var1) throws Exception;

   protected final void setOpWrite() {
      SelectionKey var1 = this.selectionKey();
      if (var1.isValid()) {
         int var2 = var1.interestOps();
         if ((var2 & 4) == 0) {
            var1.interestOps(var2 | 4);
         }

      }
   }

   protected final void clearOpWrite() {
      SelectionKey var1 = this.selectionKey();
      if (var1.isValid()) {
         int var2 = var1.interestOps();
         if ((var2 & 4) != 0) {
            var1.interestOps(var2 & -5);
         }

      }
   }

   protected class NioByteUnsafe extends AbstractNioChannel.AbstractNioUnsafe {
      protected NioByteUnsafe() {
         super();
      }

      private void closeOnRead(ChannelPipeline var1) {
         if (!AbstractNioByteChannel.this.isInputShutdown0()) {
            if (AbstractNioByteChannel.isAllowHalfClosure(AbstractNioByteChannel.this.config())) {
               AbstractNioByteChannel.this.shutdownInput();
               var1.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
            } else {
               this.close(this.voidPromise());
            }
         } else {
            AbstractNioByteChannel.this.inputClosedSeenErrorOnRead = true;
            var1.fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
         }

      }

      private void handleReadException(ChannelPipeline var1, ByteBuf var2, Throwable var3, boolean var4, RecvByteBufAllocator.Handle var5) {
         if (var2 != null) {
            if (var2.isReadable()) {
               AbstractNioByteChannel.this.readPending = false;
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

      public final void read() {
         ChannelConfig var1 = AbstractNioByteChannel.this.config();
         if (AbstractNioByteChannel.this.shouldBreakReadReady(var1)) {
            AbstractNioByteChannel.this.clearReadPending();
         } else {
            ChannelPipeline var2 = AbstractNioByteChannel.this.pipeline();
            ByteBufAllocator var3 = var1.getAllocator();
            RecvByteBufAllocator.Handle var4 = this.recvBufAllocHandle();
            var4.reset(var1);
            ByteBuf var5 = null;
            boolean var6 = false;

            try {
               do {
                  var5 = var4.allocate(var3);
                  var4.lastBytesRead(AbstractNioByteChannel.this.doReadBytes(var5));
                  if (var4.lastBytesRead() <= 0) {
                     var5.release();
                     var5 = null;
                     var6 = var4.lastBytesRead() < 0;
                     if (var6) {
                        AbstractNioByteChannel.this.readPending = false;
                     }
                     break;
                  }

                  var4.incMessagesRead(1);
                  AbstractNioByteChannel.this.readPending = false;
                  var2.fireChannelRead(var5);
                  var5 = null;
               } while(var4.continueReading());

               var4.readComplete();
               var2.fireChannelReadComplete();
               if (var6) {
                  this.closeOnRead(var2);
               }
            } catch (Throwable var11) {
               this.handleReadException(var2, var5, var11, var6, var4);
            } finally {
               if (!AbstractNioByteChannel.this.readPending && !var1.isAutoRead()) {
                  this.removeReadOp();
               }

            }

         }
      }
   }
}
