package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SessionTicketKey;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;

public abstract class OpenSslSessionContext implements SSLSessionContext {
   private static final Enumeration<byte[]> EMPTY = new OpenSslSessionContext.EmptyEnumeration();
   private final OpenSslSessionStats stats;
   final ReferenceCountedOpenSslContext context;

   OpenSslSessionContext(ReferenceCountedOpenSslContext var1) {
      super();
      this.context = var1;
      this.stats = new OpenSslSessionStats(var1);
   }

   public SSLSession getSession(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("bytes");
      } else {
         return null;
      }
   }

   public Enumeration<byte[]> getIds() {
      return EMPTY;
   }

   /** @deprecated */
   @Deprecated
   public void setTicketKeys(byte[] var1) {
      if (var1.length % 48 != 0) {
         throw new IllegalArgumentException("keys.length % 48 != 0");
      } else {
         SessionTicketKey[] var2 = new SessionTicketKey[var1.length / 48];
         int var3 = 0;

         for(int var4 = 0; var3 < var2.length; ++var3) {
            byte[] var5 = Arrays.copyOfRange(var1, var4, 16);
            var4 += 16;
            byte[] var6 = Arrays.copyOfRange(var1, var4, 16);
            var3 += 16;
            byte[] var7 = Arrays.copyOfRange(var1, var4, 16);
            var4 += 16;
            var2[var3] = new SessionTicketKey(var5, var6, var7);
         }

         Lock var11 = this.context.ctxLock.writeLock();
         var11.lock();

         try {
            SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
            SSLContext.setSessionTicketKeys(this.context.ctx, var2);
         } finally {
            var11.unlock();
         }

      }
   }

   public void setTicketKeys(OpenSslSessionTicketKey... var1) {
      ObjectUtil.checkNotNull(var1, "keys");
      SessionTicketKey[] var2 = new SessionTicketKey[var1.length];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = var1[var3].key;
      }

      Lock var7 = this.context.ctxLock.writeLock();
      var7.lock();

      try {
         SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
         SSLContext.setSessionTicketKeys(this.context.ctx, var2);
      } finally {
         var7.unlock();
      }

   }

   public abstract void setSessionCacheEnabled(boolean var1);

   public abstract boolean isSessionCacheEnabled();

   public OpenSslSessionStats stats() {
      return this.stats;
   }

   private static final class EmptyEnumeration implements Enumeration<byte[]> {
      private EmptyEnumeration() {
         super();
      }

      public boolean hasMoreElements() {
         return false;
      }

      public byte[] nextElement() {
         throw new NoSuchElementException();
      }

      // $FF: synthetic method
      EmptyEnumeration(Object var1) {
         this();
      }
   }
}
