package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSLContext;
import java.util.concurrent.locks.Lock;

public final class OpenSslSessionStats {
   private final ReferenceCountedOpenSslContext context;

   OpenSslSessionStats(ReferenceCountedOpenSslContext var1) {
      super();
      this.context = var1;
   }

   public long number() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionNumber(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long connect() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionConnect(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long connectGood() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionConnectGood(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long connectRenegotiate() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionConnectRenegotiate(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long accept() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionAccept(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long acceptGood() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionAcceptGood(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long acceptRenegotiate() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionAcceptRenegotiate(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long hits() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionHits(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long cbHits() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionCbHits(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long misses() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionMisses(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long timeouts() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTimeouts(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long cacheFull() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionCacheFull(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long ticketKeyFail() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTicketKeyFail(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long ticketKeyNew() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTicketKeyNew(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long ticketKeyRenew() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTicketKeyRenew(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public long ticketKeyResume() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.sessionTicketKeyResume(this.context.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }
}
