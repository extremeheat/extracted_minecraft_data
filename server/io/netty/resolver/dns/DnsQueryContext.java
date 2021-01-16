package io.netty.resolver.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.dns.AbstractDnsOptPseudoRrRecord;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

final class DnsQueryContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsQueryContext.class);
   private final DnsNameResolver parent;
   private final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise;
   private final int id;
   private final DnsQuestion question;
   private final DnsRecord[] additionals;
   private final DnsRecord optResource;
   private final InetSocketAddress nameServerAddr;
   private final boolean recursionDesired;
   private volatile ScheduledFuture<?> timeoutFuture;

   DnsQueryContext(DnsNameResolver var1, InetSocketAddress var2, DnsQuestion var3, DnsRecord[] var4, Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> var5) {
      super();
      this.parent = (DnsNameResolver)ObjectUtil.checkNotNull(var1, "parent");
      this.nameServerAddr = (InetSocketAddress)ObjectUtil.checkNotNull(var2, "nameServerAddr");
      this.question = (DnsQuestion)ObjectUtil.checkNotNull(var3, "question");
      this.additionals = (DnsRecord[])ObjectUtil.checkNotNull(var4, "additionals");
      this.promise = (Promise)ObjectUtil.checkNotNull(var5, "promise");
      this.recursionDesired = var1.isRecursionDesired();
      this.id = var1.queryContextManager.add(this);
      if (var1.isOptResourceEnabled()) {
         this.optResource = new AbstractDnsOptPseudoRrRecord(var1.maxPayloadSize(), 0, 0) {
         };
      } else {
         this.optResource = null;
      }

   }

   InetSocketAddress nameServerAddr() {
      return this.nameServerAddr;
   }

   DnsQuestion question() {
      return this.question;
   }

   void query(ChannelPromise var1) {
      DnsQuestion var2 = this.question();
      InetSocketAddress var3 = this.nameServerAddr();
      DatagramDnsQuery var4 = new DatagramDnsQuery((InetSocketAddress)null, var3, this.id);
      var4.setRecursionDesired(this.recursionDesired);
      var4.addRecord(DnsSection.QUESTION, var2);
      DnsRecord[] var5 = this.additionals;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         DnsRecord var8 = var5[var7];
         var4.addRecord(DnsSection.ADDITIONAL, var8);
      }

      if (this.optResource != null) {
         var4.addRecord(DnsSection.ADDITIONAL, this.optResource);
      }

      if (logger.isDebugEnabled()) {
         logger.debug("{} WRITE: [{}: {}], {}", this.parent.ch, this.id, var3, var2);
      }

      this.sendQuery(var4, var1);
   }

   private void sendQuery(final DnsQuery var1, final ChannelPromise var2) {
      if (this.parent.channelFuture.isDone()) {
         this.writeQuery(var1, var2);
      } else {
         this.parent.channelFuture.addListener(new GenericFutureListener<Future<? super Channel>>() {
            public void operationComplete(Future<? super Channel> var1x) throws Exception {
               if (var1x.isSuccess()) {
                  DnsQueryContext.this.writeQuery(var1, var2);
               } else {
                  Throwable var2x = var1x.cause();
                  DnsQueryContext.this.promise.tryFailure(var2x);
                  var2.setFailure(var2x);
               }

            }
         });
      }

   }

   private void writeQuery(DnsQuery var1, ChannelPromise var2) {
      final ChannelFuture var3 = this.parent.ch.writeAndFlush(var1, var2);
      if (var3.isDone()) {
         this.onQueryWriteCompletion(var3);
      } else {
         var3.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1) throws Exception {
               DnsQueryContext.this.onQueryWriteCompletion(var3);
            }
         });
      }

   }

   private void onQueryWriteCompletion(ChannelFuture var1) {
      if (!var1.isSuccess()) {
         this.setFailure("failed to send a query", var1.cause());
      } else {
         final long var2 = this.parent.queryTimeoutMillis();
         if (var2 > 0L) {
            this.timeoutFuture = this.parent.ch.eventLoop().schedule(new Runnable() {
               public void run() {
                  if (!DnsQueryContext.this.promise.isDone()) {
                     DnsQueryContext.this.setFailure("query timed out after " + var2 + " milliseconds", (Throwable)null);
                  }
               }
            }, var2, TimeUnit.MILLISECONDS);
         }

      }
   }

   void finish(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> var1) {
      DnsResponse var2 = (DnsResponse)var1.content();
      if (var2.count(DnsSection.QUESTION) != 1) {
         logger.warn("Received a DNS response with invalid number of questions: {}", (Object)var1);
      } else if (!this.question().equals(var2.recordAt(DnsSection.QUESTION))) {
         logger.warn("Received a mismatching DNS response: {}", (Object)var1);
      } else {
         this.setSuccess(var1);
      }
   }

   private void setSuccess(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> var1) {
      this.parent.queryContextManager.remove(this.nameServerAddr(), this.id);
      ScheduledFuture var2 = this.timeoutFuture;
      if (var2 != null) {
         var2.cancel(false);
      }

      Promise var3 = this.promise;
      if (var3.setUncancellable()) {
         AddressedEnvelope var4 = var1.retain();
         if (!var3.trySuccess(var4)) {
            var1.release();
         }
      }

   }

   private void setFailure(String var1, Throwable var2) {
      InetSocketAddress var3 = this.nameServerAddr();
      this.parent.queryContextManager.remove(var3, this.id);
      StringBuilder var4 = new StringBuilder(var1.length() + 64);
      var4.append('[').append(var3).append("] ").append(var1).append(" (no stack trace available)");
      Object var5;
      if (var2 == null) {
         var5 = new DnsNameResolverTimeoutException(var3, this.question(), var4.toString());
      } else {
         var5 = new DnsNameResolverException(var3, this.question(), var4.toString(), var2);
      }

      this.promise.tryFailure((Throwable)var5);
   }
}
