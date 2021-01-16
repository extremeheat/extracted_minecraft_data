package io.netty.handler.codec.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class HttpServerUpgradeHandler extends HttpObjectAggregator {
   private final HttpServerUpgradeHandler.SourceCodec sourceCodec;
   private final HttpServerUpgradeHandler.UpgradeCodecFactory upgradeCodecFactory;
   private boolean handlingUpgrade;

   public HttpServerUpgradeHandler(HttpServerUpgradeHandler.SourceCodec var1, HttpServerUpgradeHandler.UpgradeCodecFactory var2) {
      this(var1, var2, 0);
   }

   public HttpServerUpgradeHandler(HttpServerUpgradeHandler.SourceCodec var1, HttpServerUpgradeHandler.UpgradeCodecFactory var2, int var3) {
      super(var3);
      this.sourceCodec = (HttpServerUpgradeHandler.SourceCodec)ObjectUtil.checkNotNull(var1, "sourceCodec");
      this.upgradeCodecFactory = (HttpServerUpgradeHandler.UpgradeCodecFactory)ObjectUtil.checkNotNull(var2, "upgradeCodecFactory");
   }

   protected void decode(ChannelHandlerContext var1, HttpObject var2, List<Object> var3) throws Exception {
      this.handlingUpgrade |= isUpgradeRequest(var2);
      if (!this.handlingUpgrade) {
         ReferenceCountUtil.retain(var2);
         var3.add(var2);
      } else {
         FullHttpRequest var4;
         if (var2 instanceof FullHttpRequest) {
            var4 = (FullHttpRequest)var2;
            ReferenceCountUtil.retain(var2);
            var3.add(var2);
         } else {
            super.decode(var1, var2, var3);
            if (var3.isEmpty()) {
               return;
            }

            assert var3.size() == 1;

            this.handlingUpgrade = false;
            var4 = (FullHttpRequest)var3.get(0);
         }

         if (this.upgrade(var1, var4)) {
            var3.clear();
         }

      }
   }

   private static boolean isUpgradeRequest(HttpObject var0) {
      return var0 instanceof HttpRequest && ((HttpRequest)var0).headers().get((CharSequence)HttpHeaderNames.UPGRADE) != null;
   }

   private boolean upgrade(ChannelHandlerContext var1, FullHttpRequest var2) {
      List var3 = splitHeader(var2.headers().get((CharSequence)HttpHeaderNames.UPGRADE));
      int var4 = var3.size();
      HttpServerUpgradeHandler.UpgradeCodec var5 = null;
      CharSequence var6 = null;

      for(int var7 = 0; var7 < var4; ++var7) {
         CharSequence var8 = (CharSequence)var3.get(var7);
         HttpServerUpgradeHandler.UpgradeCodec var9 = this.upgradeCodecFactory.newUpgradeCodec(var8);
         if (var9 != null) {
            var6 = var8;
            var5 = var9;
            break;
         }
      }

      if (var5 == null) {
         return false;
      } else {
         String var16 = var2.headers().get((CharSequence)HttpHeaderNames.CONNECTION);
         if (var16 == null) {
            return false;
         } else {
            Collection var17 = var5.requiredUpgradeHeaders();
            List var18 = splitHeader(var16);
            if (AsciiString.containsContentEqualsIgnoreCase(var18, HttpHeaderNames.UPGRADE) && AsciiString.containsAllContentEqualsIgnoreCase(var18, var17)) {
               Iterator var10 = var17.iterator();

               CharSequence var11;
               do {
                  if (!var10.hasNext()) {
                     FullHttpResponse var19 = createUpgradeResponse(var6);
                     if (!var5.prepareUpgradeResponse(var1, var2, var19.headers())) {
                        return false;
                     }

                     HttpServerUpgradeHandler.UpgradeEvent var20 = new HttpServerUpgradeHandler.UpgradeEvent(var6, var2);

                     try {
                        ChannelFuture var12 = var1.writeAndFlush(var19);
                        this.sourceCodec.upgradeFrom(var1);
                        var5.upgradeTo(var1, var2);
                        var1.pipeline().remove((ChannelHandler)this);
                        var1.fireUserEventTriggered(var20.retain());
                        var12.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                     } finally {
                        var20.release();
                     }

                     return true;
                  }

                  var11 = (CharSequence)var10.next();
               } while(var2.headers().contains(var11));

               return false;
            } else {
               return false;
            }
         }
      }
   }

   private static FullHttpResponse createUpgradeResponse(CharSequence var0) {
      DefaultFullHttpResponse var1 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, Unpooled.EMPTY_BUFFER, false);
      var1.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE);
      var1.headers().add((CharSequence)HttpHeaderNames.UPGRADE, (Object)var0);
      return var1;
   }

   private static List<CharSequence> splitHeader(CharSequence var0) {
      StringBuilder var1 = new StringBuilder(var0.length());
      ArrayList var2 = new ArrayList(4);

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         char var4 = var0.charAt(var3);
         if (!Character.isWhitespace(var4)) {
            if (var4 == ',') {
               var2.add(var1.toString());
               var1.setLength(0);
            } else {
               var1.append(var4);
            }
         }
      }

      if (var1.length() > 0) {
         var2.add(var1.toString());
      }

      return var2;
   }

   public static final class UpgradeEvent implements ReferenceCounted {
      private final CharSequence protocol;
      private final FullHttpRequest upgradeRequest;

      UpgradeEvent(CharSequence var1, FullHttpRequest var2) {
         super();
         this.protocol = var1;
         this.upgradeRequest = var2;
      }

      public CharSequence protocol() {
         return this.protocol;
      }

      public FullHttpRequest upgradeRequest() {
         return this.upgradeRequest;
      }

      public int refCnt() {
         return this.upgradeRequest.refCnt();
      }

      public HttpServerUpgradeHandler.UpgradeEvent retain() {
         this.upgradeRequest.retain();
         return this;
      }

      public HttpServerUpgradeHandler.UpgradeEvent retain(int var1) {
         this.upgradeRequest.retain(var1);
         return this;
      }

      public HttpServerUpgradeHandler.UpgradeEvent touch() {
         this.upgradeRequest.touch();
         return this;
      }

      public HttpServerUpgradeHandler.UpgradeEvent touch(Object var1) {
         this.upgradeRequest.touch(var1);
         return this;
      }

      public boolean release() {
         return this.upgradeRequest.release();
      }

      public boolean release(int var1) {
         return this.upgradeRequest.release(var1);
      }

      public String toString() {
         return "UpgradeEvent [protocol=" + this.protocol + ", upgradeRequest=" + this.upgradeRequest + ']';
      }
   }

   public interface UpgradeCodecFactory {
      HttpServerUpgradeHandler.UpgradeCodec newUpgradeCodec(CharSequence var1);
   }

   public interface UpgradeCodec {
      Collection<CharSequence> requiredUpgradeHeaders();

      boolean prepareUpgradeResponse(ChannelHandlerContext var1, FullHttpRequest var2, HttpHeaders var3);

      void upgradeTo(ChannelHandlerContext var1, FullHttpRequest var2);
   }

   public interface SourceCodec {
      void upgradeFrom(ChannelHandlerContext var1);
   }
}
