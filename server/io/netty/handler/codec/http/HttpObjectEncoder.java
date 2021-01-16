package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public abstract class HttpObjectEncoder<H extends HttpMessage> extends MessageToMessageEncoder<Object> {
   static final int CRLF_SHORT = 3338;
   private static final int ZERO_CRLF_MEDIUM = 3149066;
   private static final byte[] ZERO_CRLF_CRLF = new byte[]{48, 13, 10, 13, 10};
   private static final ByteBuf CRLF_BUF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(2).writeByte(13).writeByte(10));
   private static final ByteBuf ZERO_CRLF_CRLF_BUF;
   private static final float HEADERS_WEIGHT_NEW = 0.2F;
   private static final float HEADERS_WEIGHT_HISTORICAL = 0.8F;
   private static final float TRAILERS_WEIGHT_NEW = 0.2F;
   private static final float TRAILERS_WEIGHT_HISTORICAL = 0.8F;
   private static final int ST_INIT = 0;
   private static final int ST_CONTENT_NON_CHUNK = 1;
   private static final int ST_CONTENT_CHUNK = 2;
   private static final int ST_CONTENT_ALWAYS_EMPTY = 3;
   private int state = 0;
   private float headersEncodedSizeAccumulator = 256.0F;
   private float trailersEncodedSizeAccumulator = 256.0F;

   public HttpObjectEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
      ByteBuf var4 = null;
      if (var2 instanceof HttpMessage) {
         if (this.state != 0) {
            throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(var2));
         }

         HttpMessage var5 = (HttpMessage)var2;
         var4 = var1.alloc().buffer((int)this.headersEncodedSizeAccumulator);
         this.encodeInitialLine(var4, var5);
         this.state = this.isContentAlwaysEmpty(var5) ? 3 : (HttpUtil.isTransferEncodingChunked(var5) ? 2 : 1);
         this.sanitizeHeadersBeforeEncode(var5, this.state == 3);
         this.encodeHeaders(var5.headers(), var4);
         ByteBufUtil.writeShortBE(var4, 3338);
         this.headersEncodedSizeAccumulator = 0.2F * (float)padSizeForAccumulation(var4.readableBytes()) + 0.8F * this.headersEncodedSizeAccumulator;
      }

      if (var2 instanceof ByteBuf) {
         ByteBuf var7 = (ByteBuf)var2;
         if (!var7.isReadable()) {
            var3.add(var7.retain());
            return;
         }
      }

      if (!(var2 instanceof HttpContent) && !(var2 instanceof ByteBuf) && !(var2 instanceof FileRegion)) {
         if (var4 != null) {
            var3.add(var4);
         }
      } else {
         switch(this.state) {
         case 0:
            throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(var2));
         case 1:
            long var8 = contentLength(var2);
            if (var8 > 0L) {
               if (var4 != null && (long)var4.writableBytes() >= var8 && var2 instanceof HttpContent) {
                  var4.writeBytes(((HttpContent)var2).content());
                  var3.add(var4);
               } else {
                  if (var4 != null) {
                     var3.add(var4);
                  }

                  var3.add(encodeAndRetain(var2));
               }

               if (var2 instanceof LastHttpContent) {
                  this.state = 0;
               }
               break;
            }
         case 3:
            if (var4 != null) {
               var3.add(var4);
            } else {
               var3.add(Unpooled.EMPTY_BUFFER);
            }
            break;
         case 2:
            if (var4 != null) {
               var3.add(var4);
            }

            this.encodeChunkedContent(var1, var2, contentLength(var2), var3);
            break;
         default:
            throw new Error();
         }

         if (var2 instanceof LastHttpContent) {
            this.state = 0;
         }
      }

   }

   protected void encodeHeaders(HttpHeaders var1, ByteBuf var2) {
      Iterator var3 = var1.iteratorCharSequence();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         HttpHeadersEncoder.encoderHeader((CharSequence)var4.getKey(), (CharSequence)var4.getValue(), var2);
      }

   }

   private void encodeChunkedContent(ChannelHandlerContext var1, Object var2, long var3, List<Object> var5) {
      ByteBuf var7;
      if (var3 > 0L) {
         String var6 = Long.toHexString(var3);
         var7 = var1.alloc().buffer(var6.length() + 2);
         var7.writeCharSequence(var6, CharsetUtil.US_ASCII);
         ByteBufUtil.writeShortBE(var7, 3338);
         var5.add(var7);
         var5.add(encodeAndRetain(var2));
         var5.add(CRLF_BUF.duplicate());
      }

      if (var2 instanceof LastHttpContent) {
         HttpHeaders var8 = ((LastHttpContent)var2).trailingHeaders();
         if (var8.isEmpty()) {
            var5.add(ZERO_CRLF_CRLF_BUF.duplicate());
         } else {
            var7 = var1.alloc().buffer((int)this.trailersEncodedSizeAccumulator);
            ByteBufUtil.writeMediumBE(var7, 3149066);
            this.encodeHeaders(var8, var7);
            ByteBufUtil.writeShortBE(var7, 3338);
            this.trailersEncodedSizeAccumulator = 0.2F * (float)padSizeForAccumulation(var7.readableBytes()) + 0.8F * this.trailersEncodedSizeAccumulator;
            var5.add(var7);
         }
      } else if (var3 == 0L) {
         var5.add(encodeAndRetain(var2));
      }

   }

   protected void sanitizeHeadersBeforeEncode(H var1, boolean var2) {
   }

   protected boolean isContentAlwaysEmpty(H var1) {
      return false;
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return var1 instanceof HttpObject || var1 instanceof ByteBuf || var1 instanceof FileRegion;
   }

   private static Object encodeAndRetain(Object var0) {
      if (var0 instanceof ByteBuf) {
         return ((ByteBuf)var0).retain();
      } else if (var0 instanceof HttpContent) {
         return ((HttpContent)var0).content().retain();
      } else if (var0 instanceof FileRegion) {
         return ((FileRegion)var0).retain();
      } else {
         throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(var0));
      }
   }

   private static long contentLength(Object var0) {
      if (var0 instanceof HttpContent) {
         return (long)((HttpContent)var0).content().readableBytes();
      } else if (var0 instanceof ByteBuf) {
         return (long)((ByteBuf)var0).readableBytes();
      } else if (var0 instanceof FileRegion) {
         return ((FileRegion)var0).count();
      } else {
         throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(var0));
      }
   }

   private static int padSizeForAccumulation(int var0) {
      return (var0 << 2) / 3;
   }

   /** @deprecated */
   @Deprecated
   protected static void encodeAscii(String var0, ByteBuf var1) {
      var1.writeCharSequence(var0, CharsetUtil.US_ASCII);
   }

   protected abstract void encodeInitialLine(ByteBuf var1, H var2) throws Exception;

   static {
      ZERO_CRLF_CRLF_BUF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(ZERO_CRLF_CRLF.length).writeBytes(ZERO_CRLF_CRLF));
   }
}
