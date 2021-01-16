package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class CompressorHttp2ConnectionEncoder extends DecoratingHttp2ConnectionEncoder {
   public static final int DEFAULT_COMPRESSION_LEVEL = 6;
   public static final int DEFAULT_WINDOW_BITS = 15;
   public static final int DEFAULT_MEM_LEVEL = 8;
   private final int compressionLevel;
   private final int windowBits;
   private final int memLevel;
   private final Http2Connection.PropertyKey propertyKey;

   public CompressorHttp2ConnectionEncoder(Http2ConnectionEncoder var1) {
      this(var1, 6, 15, 8);
   }

   public CompressorHttp2ConnectionEncoder(Http2ConnectionEncoder var1, int var2, int var3, int var4) {
      super(var1);
      if (var2 >= 0 && var2 <= 9) {
         if (var3 >= 9 && var3 <= 15) {
            if (var4 >= 1 && var4 <= 9) {
               this.compressionLevel = var2;
               this.windowBits = var3;
               this.memLevel = var4;
               this.propertyKey = this.connection().newKey();
               this.connection().addListener(new Http2ConnectionAdapter() {
                  public void onStreamRemoved(Http2Stream var1) {
                     EmbeddedChannel var2 = (EmbeddedChannel)var1.getProperty(CompressorHttp2ConnectionEncoder.this.propertyKey);
                     if (var2 != null) {
                        CompressorHttp2ConnectionEncoder.this.cleanup(var1, var2);
                     }

                  }
               });
            } else {
               throw new IllegalArgumentException("memLevel: " + var4 + " (expected: 1-9)");
            }
         } else {
            throw new IllegalArgumentException("windowBits: " + var3 + " (expected: 9-15)");
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var2 + " (expected: 0-9)");
      }
   }

   public ChannelFuture writeData(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5, ChannelPromise var6) {
      Http2Stream var7 = this.connection().stream(var2);
      EmbeddedChannel var8 = var7 == null ? null : (EmbeddedChannel)var7.getProperty(this.propertyKey);
      if (var8 == null) {
         return super.writeData(var1, var2, var3, var4, var5, var6);
      } else {
         ChannelFuture var10;
         try {
            var8.writeOutbound(var3);
            ByteBuf var9 = nextReadableBuf(var8);
            if (var9 != null) {
               PromiseCombiner var20 = new PromiseCombiner();

               while(true) {
                  ByteBuf var11 = nextReadableBuf(var8);
                  boolean var12 = var11 == null && var5;
                  if (var12 && var8.finish()) {
                     var11 = nextReadableBuf(var8);
                     var12 = var11 == null;
                  }

                  ChannelPromise var13 = var1.newPromise();
                  var20.add((Promise)var13);
                  super.writeData(var1, var2, var9, var4, var12, var13);
                  if (var11 == null) {
                     var20.finish(var6);
                     return var6;
                  }

                  var4 = 0;
                  var9 = var11;
               }
            }

            if (!var5) {
               var6.setSuccess();
               ChannelPromise var19 = var6;
               return var19;
            }

            if (var8.finish()) {
               var9 = nextReadableBuf(var8);
            }

            var10 = super.writeData(var1, var2, var9 == null ? Unpooled.EMPTY_BUFFER : var9, var4, true, var6);
         } catch (Throwable var17) {
            var6.tryFailure(var17);
            return var6;
         } finally {
            if (var5) {
               this.cleanup(var7, var8);
            }

         }

         return var10;
      }
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, ChannelPromise var6) {
      try {
         EmbeddedChannel var7 = this.newCompressor(var1, var3, var5);
         ChannelFuture var8 = super.writeHeaders(var1, var2, var3, var4, var5, var6);
         this.bindCompressorToStream(var7, var2);
         return var8;
      } catch (Throwable var9) {
         var6.tryFailure(var9);
         return var6;
      }
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9) {
      try {
         EmbeddedChannel var10 = this.newCompressor(var1, var3, var8);
         ChannelFuture var11 = super.writeHeaders(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         this.bindCompressorToStream(var10, var2);
         return var11;
      } catch (Throwable var12) {
         var9.tryFailure(var12);
         return var9;
      }
   }

   protected EmbeddedChannel newContentCompressor(ChannelHandlerContext var1, CharSequence var2) throws Http2Exception {
      if (!HttpHeaderValues.GZIP.contentEqualsIgnoreCase(var2) && !HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase(var2)) {
         return !HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase(var2) && !HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase(var2) ? null : this.newCompressionChannel(var1, ZlibWrapper.ZLIB);
      } else {
         return this.newCompressionChannel(var1, ZlibWrapper.GZIP);
      }
   }

   protected CharSequence getTargetContentEncoding(CharSequence var1) throws Http2Exception {
      return var1;
   }

   private EmbeddedChannel newCompressionChannel(ChannelHandlerContext var1, ZlibWrapper var2) {
      return new EmbeddedChannel(var1.channel().id(), var1.channel().metadata().hasDisconnect(), var1.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder(var2, this.compressionLevel, this.windowBits, this.memLevel)});
   }

   private EmbeddedChannel newCompressor(ChannelHandlerContext var1, Http2Headers var2, boolean var3) throws Http2Exception {
      if (var3) {
         return null;
      } else {
         Object var4 = (CharSequence)var2.get(HttpHeaderNames.CONTENT_ENCODING);
         if (var4 == null) {
            var4 = HttpHeaderValues.IDENTITY;
         }

         EmbeddedChannel var5 = this.newContentCompressor(var1, (CharSequence)var4);
         if (var5 != null) {
            CharSequence var6 = this.getTargetContentEncoding((CharSequence)var4);
            if (HttpHeaderValues.IDENTITY.contentEqualsIgnoreCase(var6)) {
               var2.remove(HttpHeaderNames.CONTENT_ENCODING);
            } else {
               var2.set(HttpHeaderNames.CONTENT_ENCODING, var6);
            }

            var2.remove(HttpHeaderNames.CONTENT_LENGTH);
         }

         return var5;
      }
   }

   private void bindCompressorToStream(EmbeddedChannel var1, int var2) {
      if (var1 != null) {
         Http2Stream var3 = this.connection().stream(var2);
         if (var3 != null) {
            var3.setProperty(this.propertyKey, var1);
         }
      }

   }

   void cleanup(Http2Stream var1, EmbeddedChannel var2) {
      if (var2.finish()) {
         while(true) {
            ByteBuf var3 = (ByteBuf)var2.readOutbound();
            if (var3 == null) {
               break;
            }

            var3.release();
         }
      }

      var1.removeProperty(this.propertyKey);
   }

   private static ByteBuf nextReadableBuf(EmbeddedChannel var0) {
      while(true) {
         ByteBuf var1 = (ByteBuf)var0.readOutbound();
         if (var1 == null) {
            return null;
         }

         if (var1.isReadable()) {
            return var1;
         }

         var1.release();
      }
   }
}
