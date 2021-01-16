package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;

public class HttpContentCompressor extends HttpContentEncoder {
   private final int compressionLevel;
   private final int windowBits;
   private final int memLevel;
   private final int contentSizeThreshold;
   private ChannelHandlerContext ctx;

   public HttpContentCompressor() {
      this(6);
   }

   public HttpContentCompressor(int var1) {
      this(var1, 15, 8, 0);
   }

   public HttpContentCompressor(int var1, int var2, int var3) {
      this(var1, var2, var3, 0);
   }

   public HttpContentCompressor(int var1, int var2, int var3, int var4) {
      super();
      if (var1 >= 0 && var1 <= 9) {
         if (var2 >= 9 && var2 <= 15) {
            if (var3 >= 1 && var3 <= 9) {
               if (var4 < 0) {
                  throw new IllegalArgumentException("contentSizeThreshold: " + var4 + " (expected: non negative number)");
               } else {
                  this.compressionLevel = var1;
                  this.windowBits = var2;
                  this.memLevel = var3;
                  this.contentSizeThreshold = var4;
               }
            } else {
               throw new IllegalArgumentException("memLevel: " + var3 + " (expected: 1-9)");
            }
         } else {
            throw new IllegalArgumentException("windowBits: " + var2 + " (expected: 9-15)");
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
      }
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   protected HttpContentEncoder.Result beginEncode(HttpResponse var1, String var2) throws Exception {
      if (this.contentSizeThreshold > 0 && var1 instanceof HttpContent && ((HttpContent)var1).content().readableBytes() < this.contentSizeThreshold) {
         return null;
      } else {
         String var3 = var1.headers().get((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
         if (var3 != null) {
            return null;
         } else {
            ZlibWrapper var4 = this.determineWrapper(var2);
            if (var4 == null) {
               return null;
            } else {
               String var5;
               switch(var4) {
               case GZIP:
                  var5 = "gzip";
                  break;
               case ZLIB:
                  var5 = "deflate";
                  break;
               default:
                  throw new Error();
               }

               return new HttpContentEncoder.Result(var5, new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder(var4, this.compressionLevel, this.windowBits, this.memLevel)}));
            }
         }
      }
   }

   protected ZlibWrapper determineWrapper(String var1) {
      float var2 = -1.0F;
      float var3 = -1.0F;
      float var4 = -1.0F;
      String[] var5 = var1.split(",");
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         float var9 = 1.0F;
         int var10 = var8.indexOf(61);
         if (var10 != -1) {
            try {
               var9 = Float.parseFloat(var8.substring(var10 + 1));
            } catch (NumberFormatException var12) {
               var9 = 0.0F;
            }
         }

         if (var8.contains("*")) {
            var2 = var9;
         } else if (var8.contains("gzip") && var9 > var3) {
            var3 = var9;
         } else if (var8.contains("deflate") && var9 > var4) {
            var4 = var9;
         }
      }

      if (var3 <= 0.0F && var4 <= 0.0F) {
         if (var2 > 0.0F) {
            if (var3 == -1.0F) {
               return ZlibWrapper.GZIP;
            }

            if (var4 == -1.0F) {
               return ZlibWrapper.ZLIB;
            }
         }

         return null;
      } else if (var3 >= var4) {
         return ZlibWrapper.GZIP;
      } else {
         return ZlibWrapper.ZLIB;
      }
   }
}
