package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public final class HttpClientCodec extends CombinedChannelDuplexHandler<HttpResponseDecoder, HttpRequestEncoder> implements HttpClientUpgradeHandler.SourceCodec {
   private final Queue<HttpMethod> queue;
   private final boolean parseHttpAfterConnectRequest;
   private boolean done;
   private final AtomicLong requestResponseCounter;
   private final boolean failOnMissingResponse;

   public HttpClientCodec() {
      this(4096, 8192, 8192, false);
   }

   public HttpClientCodec(int var1, int var2, int var3) {
      this(var1, var2, var3, false);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, true);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4, var5, false);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5, boolean var6) {
      super();
      this.queue = new ArrayDeque();
      this.requestResponseCounter = new AtomicLong();
      this.init(new HttpClientCodec.Decoder(var1, var2, var3, var5), new HttpClientCodec.Encoder());
      this.failOnMissingResponse = var4;
      this.parseHttpAfterConnectRequest = var6;
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, false);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5, int var6, boolean var7) {
      super();
      this.queue = new ArrayDeque();
      this.requestResponseCounter = new AtomicLong();
      this.init(new HttpClientCodec.Decoder(var1, var2, var3, var5, var6), new HttpClientCodec.Encoder());
      this.parseHttpAfterConnectRequest = var7;
      this.failOnMissingResponse = var4;
   }

   public void prepareUpgradeFrom(ChannelHandlerContext var1) {
      ((HttpClientCodec.Encoder)this.outboundHandler()).upgraded = true;
   }

   public void upgradeFrom(ChannelHandlerContext var1) {
      ChannelPipeline var2 = var1.pipeline();
      var2.remove((ChannelHandler)this);
   }

   public void setSingleDecode(boolean var1) {
      ((HttpResponseDecoder)this.inboundHandler()).setSingleDecode(var1);
   }

   public boolean isSingleDecode() {
      return ((HttpResponseDecoder)this.inboundHandler()).isSingleDecode();
   }

   private final class Decoder extends HttpResponseDecoder {
      Decoder(int var2, int var3, int var4, boolean var5) {
         super(var2, var3, var4, var5);
      }

      Decoder(int var2, int var3, int var4, boolean var5, int var6) {
         super(var2, var3, var4, var5, var6);
      }

      protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
         int var4;
         if (HttpClientCodec.this.done) {
            var4 = this.actualReadableBytes();
            if (var4 == 0) {
               return;
            }

            var3.add(var2.readBytes(var4));
         } else {
            var4 = var3.size();
            super.decode(var1, var2, var3);
            if (HttpClientCodec.this.failOnMissingResponse) {
               int var5 = var3.size();

               for(int var6 = var4; var6 < var5; ++var6) {
                  this.decrement(var3.get(var6));
               }
            }
         }

      }

      private void decrement(Object var1) {
         if (var1 != null) {
            if (var1 instanceof LastHttpContent) {
               HttpClientCodec.this.requestResponseCounter.decrementAndGet();
            }

         }
      }

      protected boolean isContentAlwaysEmpty(HttpMessage var1) {
         int var2 = ((HttpResponse)var1).status().code();
         if (var2 != 100 && var2 != 101) {
            HttpMethod var3 = (HttpMethod)HttpClientCodec.this.queue.poll();
            char var4 = var3.name().charAt(0);
            switch(var4) {
            case 'C':
               if (var2 == 200 && HttpMethod.CONNECT.equals(var3)) {
                  if (!HttpClientCodec.this.parseHttpAfterConnectRequest) {
                     HttpClientCodec.this.done = true;
                     HttpClientCodec.this.queue.clear();
                  }

                  return true;
               }
               break;
            case 'H':
               if (HttpMethod.HEAD.equals(var3)) {
                  return true;
               }
            }

            return super.isContentAlwaysEmpty(var1);
         } else {
            return super.isContentAlwaysEmpty(var1);
         }
      }

      public void channelInactive(ChannelHandlerContext var1) throws Exception {
         super.channelInactive(var1);
         if (HttpClientCodec.this.failOnMissingResponse) {
            long var2 = HttpClientCodec.this.requestResponseCounter.get();
            if (var2 > 0L) {
               var1.fireExceptionCaught(new PrematureChannelClosureException("channel gone inactive with " + var2 + " missing response(s)"));
            }
         }

      }
   }

   private final class Encoder extends HttpRequestEncoder {
      boolean upgraded;

      private Encoder() {
         super();
      }

      protected void encode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
         if (this.upgraded) {
            var3.add(ReferenceCountUtil.retain(var2));
         } else {
            if (var2 instanceof HttpRequest && !HttpClientCodec.this.done) {
               HttpClientCodec.this.queue.offer(((HttpRequest)var2).method());
            }

            super.encode(var1, var2, var3);
            if (HttpClientCodec.this.failOnMissingResponse && !HttpClientCodec.this.done && var2 instanceof LastHttpContent) {
               HttpClientCodec.this.requestResponseCounter.incrementAndGet();
            }

         }
      }

      // $FF: synthetic method
      Encoder(Object var2) {
         this();
      }
   }
}
