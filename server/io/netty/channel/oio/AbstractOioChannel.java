package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ThreadPerChannelEventLoop;
import java.net.SocketAddress;

public abstract class AbstractOioChannel extends AbstractChannel {
   protected static final int SO_TIMEOUT = 1000;
   boolean readPending;
   private final Runnable readTask = new Runnable() {
      public void run() {
         AbstractOioChannel.this.doRead();
      }
   };
   private final Runnable clearReadPendingRunnable = new Runnable() {
      public void run() {
         AbstractOioChannel.this.readPending = false;
      }
   };

   protected AbstractOioChannel(Channel var1) {
      super(var1);
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new AbstractOioChannel.DefaultOioUnsafe();
   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof ThreadPerChannelEventLoop;
   }

   protected abstract void doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

   protected void doBeginRead() throws Exception {
      if (!this.readPending) {
         this.readPending = true;
         this.eventLoop().execute(this.readTask);
      }
   }

   protected abstract void doRead();

   /** @deprecated */
   @Deprecated
   protected boolean isReadPending() {
      return this.readPending;
   }

   /** @deprecated */
   @Deprecated
   protected void setReadPending(final boolean var1) {
      if (this.isRegistered()) {
         EventLoop var2 = this.eventLoop();
         if (var2.inEventLoop()) {
            this.readPending = var1;
         } else {
            var2.execute(new Runnable() {
               public void run() {
                  AbstractOioChannel.this.readPending = var1;
               }
            });
         }
      } else {
         this.readPending = var1;
      }

   }

   protected final void clearReadPending() {
      if (this.isRegistered()) {
         EventLoop var1 = this.eventLoop();
         if (var1.inEventLoop()) {
            this.readPending = false;
         } else {
            var1.execute(this.clearReadPendingRunnable);
         }
      } else {
         this.readPending = false;
      }

   }

   private final class DefaultOioUnsafe extends AbstractChannel.AbstractUnsafe {
      private DefaultOioUnsafe() {
         super();
      }

      public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         if (var3.setUncancellable() && this.ensureOpen(var3)) {
            try {
               boolean var4 = AbstractOioChannel.this.isActive();
               AbstractOioChannel.this.doConnect(var1, var2);
               boolean var5 = AbstractOioChannel.this.isActive();
               this.safeSetSuccess(var3);
               if (!var4 && var5) {
                  AbstractOioChannel.this.pipeline().fireChannelActive();
               }
            } catch (Throwable var6) {
               this.safeSetFailure(var3, this.annotateConnectException(var6, var1));
               this.closeIfClosed();
            }

         }
      }

      // $FF: synthetic method
      DefaultOioUnsafe(Object var2) {
         this();
      }
   }
}
