package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ThrowableUtil;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.util.Locale;

public abstract class WebSocketClientHandshaker {
   private static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), WebSocketClientHandshaker.class, "processHandshake(...)");
   private static final String HTTP_SCHEME_PREFIX;
   private static final String HTTPS_SCHEME_PREFIX;
   private final URI uri;
   private final WebSocketVersion version;
   private volatile boolean handshakeComplete;
   private final String expectedSubprotocol;
   private volatile String actualSubprotocol;
   protected final HttpHeaders customHeaders;
   private final int maxFramePayloadLength;

   protected WebSocketClientHandshaker(URI var1, WebSocketVersion var2, String var3, HttpHeaders var4, int var5) {
      super();
      this.uri = var1;
      this.version = var2;
      this.expectedSubprotocol = var3;
      this.customHeaders = var4;
      this.maxFramePayloadLength = var5;
   }

   public URI uri() {
      return this.uri;
   }

   public WebSocketVersion version() {
      return this.version;
   }

   public int maxFramePayloadLength() {
      return this.maxFramePayloadLength;
   }

   public boolean isHandshakeComplete() {
      return this.handshakeComplete;
   }

   private void setHandshakeComplete() {
      this.handshakeComplete = true;
   }

   public String expectedSubprotocol() {
      return this.expectedSubprotocol;
   }

   public String actualSubprotocol() {
      return this.actualSubprotocol;
   }

   private void setActualSubprotocol(String var1) {
      this.actualSubprotocol = var1;
   }

   public ChannelFuture handshake(Channel var1) {
      if (var1 == null) {
         throw new NullPointerException("channel");
      } else {
         return this.handshake(var1, var1.newPromise());
      }
   }

   public final ChannelFuture handshake(Channel var1, final ChannelPromise var2) {
      FullHttpRequest var3 = this.newHandshakeRequest();
      HttpResponseDecoder var4 = (HttpResponseDecoder)var1.pipeline().get(HttpResponseDecoder.class);
      if (var4 == null) {
         HttpClientCodec var5 = (HttpClientCodec)var1.pipeline().get(HttpClientCodec.class);
         if (var5 == null) {
            var2.setFailure(new IllegalStateException("ChannelPipeline does not contain a HttpResponseDecoder or HttpClientCodec"));
            return var2;
         }
      }

      var1.writeAndFlush(var3).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) {
            if (var1.isSuccess()) {
               ChannelPipeline var2x = var1.channel().pipeline();
               ChannelHandlerContext var3 = var2x.context(HttpRequestEncoder.class);
               if (var3 == null) {
                  var3 = var2x.context(HttpClientCodec.class);
               }

               if (var3 == null) {
                  var2.setFailure(new IllegalStateException("ChannelPipeline does not contain a HttpRequestEncoder or HttpClientCodec"));
                  return;
               }

               var2x.addAfter(var3.name(), "ws-encoder", WebSocketClientHandshaker.this.newWebSocketEncoder());
               var2.setSuccess();
            } else {
               var2.setFailure(var1.cause());
            }

         }
      });
      return var2;
   }

   protected abstract FullHttpRequest newHandshakeRequest();

   public final void finishHandshake(Channel var1, FullHttpResponse var2) {
      this.verify(var2);
      String var3 = var2.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
      var3 = var3 != null ? var3.trim() : null;
      String var4 = this.expectedSubprotocol != null ? this.expectedSubprotocol : "";
      boolean var5 = false;
      if (var4.isEmpty() && var3 == null) {
         var5 = true;
         this.setActualSubprotocol(this.expectedSubprotocol);
      } else if (!var4.isEmpty() && var3 != null && !var3.isEmpty()) {
         String[] var6 = var4.split(",");
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            if (var9.trim().equals(var3)) {
               var5 = true;
               this.setActualSubprotocol(var3);
               break;
            }
         }
      }

      if (!var5) {
         throw new WebSocketHandshakeException(String.format("Invalid subprotocol. Actual: %s. Expected one of: %s", var3, this.expectedSubprotocol));
      } else {
         this.setHandshakeComplete();
         final ChannelPipeline var11 = var1.pipeline();
         HttpContentDecompressor var12 = (HttpContentDecompressor)var11.get(HttpContentDecompressor.class);
         if (var12 != null) {
            var11.remove((ChannelHandler)var12);
         }

         HttpObjectAggregator var13 = (HttpObjectAggregator)var11.get(HttpObjectAggregator.class);
         if (var13 != null) {
            var11.remove((ChannelHandler)var13);
         }

         final ChannelHandlerContext var14 = var11.context(HttpResponseDecoder.class);
         if (var14 == null) {
            var14 = var11.context(HttpClientCodec.class);
            if (var14 == null) {
               throw new IllegalStateException("ChannelPipeline does not contain a HttpRequestEncoder or HttpClientCodec");
            }

            final HttpClientCodec var10 = (HttpClientCodec)var14.handler();
            var10.removeOutboundHandler();
            var11.addAfter(var14.name(), "ws-decoder", this.newWebsocketDecoder());
            var1.eventLoop().execute(new Runnable() {
               public void run() {
                  var11.remove((ChannelHandler)var10);
               }
            });
         } else {
            if (var11.get(HttpRequestEncoder.class) != null) {
               var11.remove(HttpRequestEncoder.class);
            }

            var11.addAfter(var14.name(), "ws-decoder", this.newWebsocketDecoder());
            var1.eventLoop().execute(new Runnable() {
               public void run() {
                  var11.remove(var14.handler());
               }
            });
         }

      }
   }

   public final ChannelFuture processHandshake(Channel var1, HttpResponse var2) {
      return this.processHandshake(var1, var2, var1.newPromise());
   }

   public final ChannelFuture processHandshake(final Channel var1, HttpResponse var2, final ChannelPromise var3) {
      if (var2 instanceof FullHttpResponse) {
         try {
            this.finishHandshake(var1, (FullHttpResponse)var2);
            var3.setSuccess();
         } catch (Throwable var9) {
            var3.setFailure(var9);
         }
      } else {
         ChannelPipeline var4 = var1.pipeline();
         ChannelHandlerContext var5 = var4.context(HttpResponseDecoder.class);
         if (var5 == null) {
            var5 = var4.context(HttpClientCodec.class);
            if (var5 == null) {
               return var3.setFailure(new IllegalStateException("ChannelPipeline does not contain a HttpResponseDecoder or HttpClientCodec"));
            }
         }

         String var6 = "httpAggregator";
         var4.addAfter(var5.name(), var6, new HttpObjectAggregator(8192));
         var4.addAfter(var6, "handshaker", new SimpleChannelInboundHandler<FullHttpResponse>() {
            protected void channelRead0(ChannelHandlerContext var1x, FullHttpResponse var2) throws Exception {
               var1x.pipeline().remove((ChannelHandler)this);

               try {
                  WebSocketClientHandshaker.this.finishHandshake(var1, var2);
                  var3.setSuccess();
               } catch (Throwable var4) {
                  var3.setFailure(var4);
               }

            }

            public void exceptionCaught(ChannelHandlerContext var1x, Throwable var2) throws Exception {
               var1x.pipeline().remove((ChannelHandler)this);
               var3.setFailure(var2);
            }

            public void channelInactive(ChannelHandlerContext var1x) throws Exception {
               var3.tryFailure(WebSocketClientHandshaker.CLOSED_CHANNEL_EXCEPTION);
               var1x.fireChannelInactive();
            }
         });

         try {
            var5.fireChannelRead(ReferenceCountUtil.retain(var2));
         } catch (Throwable var8) {
            var3.setFailure(var8);
         }
      }

      return var3;
   }

   protected abstract void verify(FullHttpResponse var1);

   protected abstract WebSocketFrameDecoder newWebsocketDecoder();

   protected abstract WebSocketFrameEncoder newWebSocketEncoder();

   public ChannelFuture close(Channel var1, CloseWebSocketFrame var2) {
      if (var1 == null) {
         throw new NullPointerException("channel");
      } else {
         return this.close(var1, var2, var1.newPromise());
      }
   }

   public ChannelFuture close(Channel var1, CloseWebSocketFrame var2, ChannelPromise var3) {
      if (var1 == null) {
         throw new NullPointerException("channel");
      } else {
         return var1.writeAndFlush(var2, var3);
      }
   }

   static String rawPath(URI var0) {
      String var1 = var0.getRawPath();
      String var2 = var0.getRawQuery();
      if (var2 != null && !var2.isEmpty()) {
         var1 = var1 + '?' + var2;
      }

      return var1 != null && !var1.isEmpty() ? var1 : "/";
   }

   static CharSequence websocketHostValue(URI var0) {
      int var1 = var0.getPort();
      if (var1 == -1) {
         return var0.getHost();
      } else {
         String var2 = var0.getHost();
         if (var1 == HttpScheme.HTTP.port()) {
            return !HttpScheme.HTTP.name().contentEquals(var0.getScheme()) && !WebSocketScheme.WS.name().contentEquals(var0.getScheme()) ? NetUtil.toSocketAddressString(var2, var1) : var2;
         } else if (var1 != HttpScheme.HTTPS.port()) {
            return NetUtil.toSocketAddressString(var2, var1);
         } else {
            return !HttpScheme.HTTPS.name().contentEquals(var0.getScheme()) && !WebSocketScheme.WSS.name().contentEquals(var0.getScheme()) ? NetUtil.toSocketAddressString(var2, var1) : var2;
         }
      }
   }

   static CharSequence websocketOriginValue(URI var0) {
      String var1 = var0.getScheme();
      int var3 = var0.getPort();
      String var2;
      int var4;
      if (!WebSocketScheme.WSS.name().contentEquals(var1) && !HttpScheme.HTTPS.name().contentEquals(var1) && (var1 != null || var3 != WebSocketScheme.WSS.port())) {
         var2 = HTTP_SCHEME_PREFIX;
         var4 = WebSocketScheme.WS.port();
      } else {
         var2 = HTTPS_SCHEME_PREFIX;
         var4 = WebSocketScheme.WSS.port();
      }

      String var5 = var0.getHost().toLowerCase(Locale.US);
      return var3 != var4 && var3 != -1 ? var2 + NetUtil.toSocketAddressString(var5, var3) : var2 + var5;
   }

   static {
      HTTP_SCHEME_PREFIX = HttpScheme.HTTP + "://";
      HTTPS_SCHEME_PREFIX = HttpScheme.HTTPS + "://";
   }
}
