package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.internal.ObjectUtil;

public class DelegatingDecompressorFrameListener extends Http2FrameListenerDecorator {
   private final Http2Connection connection;
   private final boolean strict;
   private boolean flowControllerInitialized;
   private final Http2Connection.PropertyKey propertyKey;

   public DelegatingDecompressorFrameListener(Http2Connection var1, Http2FrameListener var2) {
      this(var1, var2, true);
   }

   public DelegatingDecompressorFrameListener(Http2Connection var1, Http2FrameListener var2, boolean var3) {
      super(var2);
      this.connection = var1;
      this.strict = var3;
      this.propertyKey = var1.newKey();
      var1.addListener(new Http2ConnectionAdapter() {
         public void onStreamRemoved(Http2Stream var1) {
            DelegatingDecompressorFrameListener.Http2Decompressor var2 = DelegatingDecompressorFrameListener.this.decompressor(var1);
            if (var2 != null) {
               DelegatingDecompressorFrameListener.cleanup(var2);
            }

         }
      });
   }

   public int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5) throws Http2Exception {
      Http2Stream var6 = this.connection.stream(var2);
      DelegatingDecompressorFrameListener.Http2Decompressor var7 = this.decompressor(var6);
      if (var7 == null) {
         return this.listener.onDataRead(var1, var2, var3, var4, var5);
      } else {
         EmbeddedChannel var8 = var7.decompressor();
         int var9 = var3.readableBytes() + var4;
         var7.incrementCompressedBytes(var9);

         try {
            var8.writeInbound(var3.retain());
            ByteBuf var10 = nextReadableBuf(var8);
            if (var10 == null && var5 && var8.finish()) {
               var10 = nextReadableBuf(var8);
            }

            if (var10 == null) {
               if (var5) {
                  this.listener.onDataRead(var1, var2, Unpooled.EMPTY_BUFFER, var4, true);
               }

               var7.incrementDecompressedBytes(var9);
               return var9;
            } else {
               try {
                  Http2LocalFlowController var11 = (Http2LocalFlowController)this.connection.local().flowController();
                  var7.incrementDecompressedBytes(var4);

                  while(true) {
                     ByteBuf var12 = nextReadableBuf(var8);
                     boolean var13 = var12 == null && var5;
                     if (var13 && var8.finish()) {
                        var12 = nextReadableBuf(var8);
                        var13 = var12 == null;
                     }

                     var7.incrementDecompressedBytes(var10.readableBytes());
                     var11.consumeBytes(var6, this.listener.onDataRead(var1, var2, var10, var4, var13));
                     if (var12 == null) {
                        byte var21 = 0;
                        return var21;
                     }

                     var4 = 0;
                     var10.release();
                     var10 = var12;
                  }
               } finally {
                  var10.release();
               }
            }
         } catch (Http2Exception var19) {
            throw var19;
         } catch (Throwable var20) {
            throw Http2Exception.streamError(var6.id(), Http2Error.INTERNAL_ERROR, var20, "Decompressor error detected while delegating data read on streamId %d", var6.id());
         }
      }
   }

   public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5) throws Http2Exception {
      this.initDecompressor(var1, var2, var3, var5);
      this.listener.onHeadersRead(var1, var2, var3, var4, var5);
   }

   public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8) throws Http2Exception {
      this.initDecompressor(var1, var2, var3, var8);
      this.listener.onHeadersRead(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected EmbeddedChannel newContentDecompressor(ChannelHandlerContext var1, CharSequence var2) throws Http2Exception {
      if (!HttpHeaderValues.GZIP.contentEqualsIgnoreCase(var2) && !HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase(var2)) {
         if (!HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase(var2) && !HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase(var2)) {
            return null;
         } else {
            ZlibWrapper var3 = this.strict ? ZlibWrapper.ZLIB : ZlibWrapper.ZLIB_OR_NONE;
            return new EmbeddedChannel(var1.channel().id(), var1.channel().metadata().hasDisconnect(), var1.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder(var3)});
         }
      } else {
         return new EmbeddedChannel(var1.channel().id(), var1.channel().metadata().hasDisconnect(), var1.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP)});
      }
   }

   protected CharSequence getTargetContentEncoding(CharSequence var1) throws Http2Exception {
      return HttpHeaderValues.IDENTITY;
   }

   private void initDecompressor(ChannelHandlerContext var1, int var2, Http2Headers var3, boolean var4) throws Http2Exception {
      Http2Stream var5 = this.connection.stream(var2);
      if (var5 != null) {
         DelegatingDecompressorFrameListener.Http2Decompressor var6 = this.decompressor(var5);
         if (var6 == null && !var4) {
            Object var7 = (CharSequence)var3.get(HttpHeaderNames.CONTENT_ENCODING);
            if (var7 == null) {
               var7 = HttpHeaderValues.IDENTITY;
            }

            EmbeddedChannel var8 = this.newContentDecompressor(var1, (CharSequence)var7);
            if (var8 != null) {
               var6 = new DelegatingDecompressorFrameListener.Http2Decompressor(var8);
               var5.setProperty(this.propertyKey, var6);
               CharSequence var9 = this.getTargetContentEncoding((CharSequence)var7);
               if (HttpHeaderValues.IDENTITY.contentEqualsIgnoreCase(var9)) {
                  var3.remove(HttpHeaderNames.CONTENT_ENCODING);
               } else {
                  var3.set(HttpHeaderNames.CONTENT_ENCODING, var9);
               }
            }
         }

         if (var6 != null) {
            var3.remove(HttpHeaderNames.CONTENT_LENGTH);
            if (!this.flowControllerInitialized) {
               this.flowControllerInitialized = true;
               this.connection.local().flowController(new DelegatingDecompressorFrameListener.ConsumedBytesConverter((Http2LocalFlowController)this.connection.local().flowController()));
            }
         }

      }
   }

   DelegatingDecompressorFrameListener.Http2Decompressor decompressor(Http2Stream var1) {
      return var1 == null ? null : (DelegatingDecompressorFrameListener.Http2Decompressor)var1.getProperty(this.propertyKey);
   }

   private static void cleanup(DelegatingDecompressorFrameListener.Http2Decompressor var0) {
      var0.decompressor().finishAndReleaseAll();
   }

   private static ByteBuf nextReadableBuf(EmbeddedChannel var0) {
      while(true) {
         ByteBuf var1 = (ByteBuf)var0.readInbound();
         if (var1 == null) {
            return null;
         }

         if (var1.isReadable()) {
            return var1;
         }

         var1.release();
      }
   }

   private static final class Http2Decompressor {
      private final EmbeddedChannel decompressor;
      private int compressed;
      private int decompressed;

      Http2Decompressor(EmbeddedChannel var1) {
         super();
         this.decompressor = var1;
      }

      EmbeddedChannel decompressor() {
         return this.decompressor;
      }

      void incrementCompressedBytes(int var1) {
         assert var1 >= 0;

         this.compressed += var1;
      }

      void incrementDecompressedBytes(int var1) {
         assert var1 >= 0;

         this.decompressed += var1;
      }

      int consumeBytes(int var1, int var2) throws Http2Exception {
         if (var2 < 0) {
            throw new IllegalArgumentException("decompressedBytes must not be negative: " + var2);
         } else if (this.decompressed - var2 < 0) {
            throw Http2Exception.streamError(var1, Http2Error.INTERNAL_ERROR, "Attempting to return too many bytes for stream %d. decompressed: %d decompressedBytes: %d", var1, this.decompressed, var2);
         } else {
            double var3 = (double)var2 / (double)this.decompressed;
            int var5 = Math.min(this.compressed, (int)Math.ceil((double)this.compressed * var3));
            if (this.compressed - var5 < 0) {
               throw Http2Exception.streamError(var1, Http2Error.INTERNAL_ERROR, "overflow when converting decompressed bytes to compressed bytes for stream %d.decompressedBytes: %d decompressed: %d compressed: %d consumedCompressed: %d", var1, var2, this.decompressed, this.compressed, var5);
            } else {
               this.decompressed -= var2;
               this.compressed -= var5;
               return var5;
            }
         }
      }
   }

   private final class ConsumedBytesConverter implements Http2LocalFlowController {
      private final Http2LocalFlowController flowController;

      ConsumedBytesConverter(Http2LocalFlowController var2) {
         super();
         this.flowController = (Http2LocalFlowController)ObjectUtil.checkNotNull(var2, "flowController");
      }

      public Http2LocalFlowController frameWriter(Http2FrameWriter var1) {
         return this.flowController.frameWriter(var1);
      }

      public void channelHandlerContext(ChannelHandlerContext var1) throws Http2Exception {
         this.flowController.channelHandlerContext(var1);
      }

      public void initialWindowSize(int var1) throws Http2Exception {
         this.flowController.initialWindowSize(var1);
      }

      public int initialWindowSize() {
         return this.flowController.initialWindowSize();
      }

      public int windowSize(Http2Stream var1) {
         return this.flowController.windowSize(var1);
      }

      public void incrementWindowSize(Http2Stream var1, int var2) throws Http2Exception {
         this.flowController.incrementWindowSize(var1, var2);
      }

      public void receiveFlowControlledFrame(Http2Stream var1, ByteBuf var2, int var3, boolean var4) throws Http2Exception {
         this.flowController.receiveFlowControlledFrame(var1, var2, var3, var4);
      }

      public boolean consumeBytes(Http2Stream var1, int var2) throws Http2Exception {
         DelegatingDecompressorFrameListener.Http2Decompressor var3 = DelegatingDecompressorFrameListener.this.decompressor(var1);
         if (var3 != null) {
            var2 = var3.consumeBytes(var1.id(), var2);
         }

         try {
            return this.flowController.consumeBytes(var1, var2);
         } catch (Http2Exception var5) {
            throw var5;
         } catch (Throwable var6) {
            throw Http2Exception.streamError(var1.id(), Http2Error.INTERNAL_ERROR, var6, "Error while returning bytes to flow control window");
         }
      }

      public int unconsumedBytes(Http2Stream var1) {
         return this.flowController.unconsumedBytes(var1);
      }

      public int initialWindowSize(Http2Stream var1) {
         return this.flowController.initialWindowSize(var1);
      }
   }
}
