package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SpdyHttpDecoder extends MessageToMessageDecoder<SpdyFrame> {
   private final boolean validateHeaders;
   private final int spdyVersion;
   private final int maxContentLength;
   private final Map<Integer, FullHttpMessage> messageMap;

   public SpdyHttpDecoder(SpdyVersion var1, int var2) {
      this(var1, var2, new HashMap(), true);
   }

   public SpdyHttpDecoder(SpdyVersion var1, int var2, boolean var3) {
      this(var1, var2, new HashMap(), var3);
   }

   protected SpdyHttpDecoder(SpdyVersion var1, int var2, Map<Integer, FullHttpMessage> var3) {
      this(var1, var2, var3, true);
   }

   protected SpdyHttpDecoder(SpdyVersion var1, int var2, Map<Integer, FullHttpMessage> var3, boolean var4) {
      super();
      if (var1 == null) {
         throw new NullPointerException("version");
      } else if (var2 <= 0) {
         throw new IllegalArgumentException("maxContentLength must be a positive integer: " + var2);
      } else {
         this.spdyVersion = var1.getVersion();
         this.maxContentLength = var2;
         this.messageMap = var3;
         this.validateHeaders = var4;
      }
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      Iterator var2 = this.messageMap.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         ReferenceCountUtil.safeRelease(var3.getValue());
      }

      this.messageMap.clear();
      super.channelInactive(var1);
   }

   protected FullHttpMessage putMessage(int var1, FullHttpMessage var2) {
      return (FullHttpMessage)this.messageMap.put(var1, var2);
   }

   protected FullHttpMessage getMessage(int var1) {
      return (FullHttpMessage)this.messageMap.get(var1);
   }

   protected FullHttpMessage removeMessage(int var1) {
      return (FullHttpMessage)this.messageMap.remove(var1);
   }

   protected void decode(ChannelHandlerContext var1, SpdyFrame var2, List<Object> var3) throws Exception {
      int var5;
      DefaultSpdyRstStreamFrame var8;
      DefaultSpdyRstStreamFrame var22;
      if (var2 instanceof SpdySynStreamFrame) {
         SpdySynStreamFrame var4 = (SpdySynStreamFrame)var2;
         var5 = var4.streamId();
         if (SpdyCodecUtil.isServerId(var5)) {
            int var6 = var4.associatedStreamId();
            if (var6 == 0) {
               var22 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.INVALID_STREAM);
               var1.writeAndFlush(var22);
               return;
            }

            if (var4.isLast()) {
               var22 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.PROTOCOL_ERROR);
               var1.writeAndFlush(var22);
               return;
            }

            if (var4.isTruncated()) {
               var22 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.INTERNAL_ERROR);
               var1.writeAndFlush(var22);
               return;
            }

            try {
               FullHttpRequest var7 = createHttpRequest(var4, var1.alloc());
               var7.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, var5);
               var7.headers().setInt(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, var6);
               var7.headers().setInt(SpdyHttpHeaders.Names.PRIORITY, var4.priority());
               var3.add(var7);
            } catch (Throwable var13) {
               var8 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.PROTOCOL_ERROR);
               var1.writeAndFlush(var8);
            }
         } else {
            if (var4.isTruncated()) {
               DefaultSpdySynReplyFrame var19 = new DefaultSpdySynReplyFrame(var5);
               var19.setLast(true);
               SpdyHeaders var26 = var19.headers();
               var26.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.code());
               var26.setObject(SpdyHeaders.HttpNames.VERSION, HttpVersion.HTTP_1_0);
               var1.writeAndFlush(var19);
               return;
            }

            try {
               FullHttpRequest var18 = createHttpRequest(var4, var1.alloc());
               var18.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, var5);
               if (var4.isLast()) {
                  var3.add(var18);
               } else {
                  this.putMessage(var5, var18);
               }
            } catch (Throwable var12) {
               DefaultSpdySynReplyFrame var24 = new DefaultSpdySynReplyFrame(var5);
               var24.setLast(true);
               SpdyHeaders var25 = var24.headers();
               var25.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.BAD_REQUEST.code());
               var25.setObject(SpdyHeaders.HttpNames.VERSION, HttpVersion.HTTP_1_0);
               var1.writeAndFlush(var24);
            }
         }
      } else {
         FullHttpResponse var20;
         if (var2 instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame var14 = (SpdySynReplyFrame)var2;
            var5 = var14.streamId();
            if (var14.isTruncated()) {
               DefaultSpdyRstStreamFrame var21 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.INTERNAL_ERROR);
               var1.writeAndFlush(var21);
               return;
            }

            try {
               var20 = createHttpResponse(var14, var1.alloc(), this.validateHeaders);
               var20.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, var5);
               if (var14.isLast()) {
                  HttpUtil.setContentLength(var20, 0L);
                  var3.add(var20);
               } else {
                  this.putMessage(var5, var20);
               }
            } catch (Throwable var11) {
               var22 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.PROTOCOL_ERROR);
               var1.writeAndFlush(var22);
            }
         } else {
            FullHttpMessage var23;
            if (var2 instanceof SpdyHeadersFrame) {
               SpdyHeadersFrame var15 = (SpdyHeadersFrame)var2;
               var5 = var15.streamId();
               var23 = this.getMessage(var5);
               if (var23 == null) {
                  if (SpdyCodecUtil.isServerId(var5)) {
                     if (var15.isTruncated()) {
                        var22 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.INTERNAL_ERROR);
                        var1.writeAndFlush(var22);
                        return;
                     }

                     try {
                        var20 = createHttpResponse(var15, var1.alloc(), this.validateHeaders);
                        var20.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, var5);
                        if (var15.isLast()) {
                           HttpUtil.setContentLength(var20, 0L);
                           var3.add(var20);
                        } else {
                           this.putMessage(var5, var20);
                        }
                     } catch (Throwable var10) {
                        var8 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.PROTOCOL_ERROR);
                        var1.writeAndFlush(var8);
                     }
                  }

                  return;
               }

               if (!var15.isTruncated()) {
                  Iterator var28 = var15.headers().iterator();

                  while(var28.hasNext()) {
                     Entry var27 = (Entry)var28.next();
                     var23.headers().add((CharSequence)var27.getKey(), var27.getValue());
                  }
               }

               if (var15.isLast()) {
                  HttpUtil.setContentLength(var23, (long)var23.content().readableBytes());
                  this.removeMessage(var5);
                  var3.add(var23);
               }
            } else if (var2 instanceof SpdyDataFrame) {
               SpdyDataFrame var16 = (SpdyDataFrame)var2;
               var5 = var16.streamId();
               var23 = this.getMessage(var5);
               if (var23 == null) {
                  return;
               }

               ByteBuf var30 = var23.content();
               if (var30.readableBytes() > this.maxContentLength - var16.content().readableBytes()) {
                  this.removeMessage(var5);
                  throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
               }

               ByteBuf var29 = var16.content();
               int var9 = var29.readableBytes();
               var30.writeBytes(var29, var29.readerIndex(), var9);
               if (var16.isLast()) {
                  HttpUtil.setContentLength(var23, (long)var30.readableBytes());
                  this.removeMessage(var5);
                  var3.add(var23);
               }
            } else if (var2 instanceof SpdyRstStreamFrame) {
               SpdyRstStreamFrame var17 = (SpdyRstStreamFrame)var2;
               var5 = var17.streamId();
               this.removeMessage(var5);
            }
         }
      }

   }

   private static FullHttpRequest createHttpRequest(SpdyHeadersFrame var0, ByteBufAllocator var1) throws Exception {
      SpdyHeaders var2 = var0.headers();
      HttpMethod var3 = HttpMethod.valueOf(var2.getAsString(SpdyHeaders.HttpNames.METHOD));
      String var4 = var2.getAsString(SpdyHeaders.HttpNames.PATH);
      HttpVersion var5 = HttpVersion.valueOf(var2.getAsString(SpdyHeaders.HttpNames.VERSION));
      var2.remove(SpdyHeaders.HttpNames.METHOD);
      var2.remove(SpdyHeaders.HttpNames.PATH);
      var2.remove(SpdyHeaders.HttpNames.VERSION);
      boolean var6 = true;
      ByteBuf var7 = var1.buffer();

      try {
         DefaultFullHttpRequest var8 = new DefaultFullHttpRequest(var5, var3, var4, var7);
         var2.remove(SpdyHeaders.HttpNames.SCHEME);
         CharSequence var9 = (CharSequence)var2.get(SpdyHeaders.HttpNames.HOST);
         var2.remove(SpdyHeaders.HttpNames.HOST);
         var8.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)var9);
         Iterator var10 = var0.headers().iterator();

         while(var10.hasNext()) {
            Entry var11 = (Entry)var10.next();
            var8.headers().add((CharSequence)var11.getKey(), var11.getValue());
         }

         HttpUtil.setKeepAlive(var8, true);
         var8.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
         var6 = false;
         DefaultFullHttpRequest var15 = var8;
         return var15;
      } finally {
         if (var6) {
            var7.release();
         }

      }
   }

   private static FullHttpResponse createHttpResponse(SpdyHeadersFrame var0, ByteBufAllocator var1, boolean var2) throws Exception {
      SpdyHeaders var3 = var0.headers();
      HttpResponseStatus var4 = HttpResponseStatus.parseLine((CharSequence)var3.get(SpdyHeaders.HttpNames.STATUS));
      HttpVersion var5 = HttpVersion.valueOf(var3.getAsString(SpdyHeaders.HttpNames.VERSION));
      var3.remove(SpdyHeaders.HttpNames.STATUS);
      var3.remove(SpdyHeaders.HttpNames.VERSION);
      boolean var6 = true;
      ByteBuf var7 = var1.buffer();

      try {
         DefaultFullHttpResponse var8 = new DefaultFullHttpResponse(var5, var4, var7, var2);
         Iterator var9 = var0.headers().iterator();

         while(var9.hasNext()) {
            Entry var10 = (Entry)var9.next();
            var8.headers().add((CharSequence)var10.getKey(), var10.getValue());
         }

         HttpUtil.setKeepAlive(var8, true);
         var8.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
         var8.headers().remove((CharSequence)HttpHeaderNames.TRAILER);
         var6 = false;
         DefaultFullHttpResponse var14 = var8;
         return var14;
      } finally {
         if (var6) {
            var7.release();
         }

      }
   }
}
