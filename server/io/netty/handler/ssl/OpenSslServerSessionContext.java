package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import java.util.concurrent.locks.Lock;

public final class OpenSslServerSessionContext extends OpenSslSessionContext {
   OpenSslServerSessionContext(ReferenceCountedOpenSslContext var1) {
      super(var1);
   }

   public void setSessionTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         Lock var2 = this.context.ctxLock.writeLock();
         var2.lock();

         try {
            SSLContext.setSessionCacheTimeout(this.context.ctx, (long)var1);
         } finally {
            var2.unlock();
         }

      }
   }

   public int getSessionTimeout() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      int var2;
      try {
         var2 = (int)SSLContext.getSessionCacheTimeout(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public void setSessionCacheSize(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         Lock var2 = this.context.ctxLock.writeLock();
         var2.lock();

         try {
            SSLContext.setSessionCacheSize(this.context.ctx, (long)var1);
         } finally {
            var2.unlock();
         }

      }
   }

   public int getSessionCacheSize() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      int var2;
      try {
         var2 = (int)SSLContext.getSessionCacheSize(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public void setSessionCacheEnabled(boolean var1) {
      long var2 = var1 ? SSL.SSL_SESS_CACHE_SERVER : SSL.SSL_SESS_CACHE_OFF;
      Lock var4 = this.context.ctxLock.writeLock();
      var4.lock();

      try {
         SSLContext.setSessionCacheMode(this.context.ctx, var2);
      } finally {
         var4.unlock();
      }

   }

   public boolean isSessionCacheEnabled() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      boolean var2;
      try {
         var2 = SSLContext.getSessionCacheMode(this.context.ctx) == SSL.SSL_SESS_CACHE_SERVER;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public boolean setSessionIdContext(byte[] var1) {
      Lock var2 = this.context.ctxLock.writeLock();
      var2.lock();

      boolean var3;
      try {
         var3 = SSLContext.setSessionIdContext(this.context.ctx, var1);
      } finally {
         var2.unlock();
      }

      return var3;
   }
}
