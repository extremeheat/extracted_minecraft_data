package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.PlatformDependent;

public class DefaultHttp2FrameReader implements Http2FrameReader, Http2FrameSizePolicy, Http2FrameReader.Configuration {
   private final Http2HeadersDecoder headersDecoder;
   private boolean readingHeaders;
   private boolean readError;
   private byte frameType;
   private int streamId;
   private Http2Flags flags;
   private int payloadLength;
   private DefaultHttp2FrameReader.HeadersContinuation headersContinuation;
   private int maxFrameSize;

   public DefaultHttp2FrameReader() {
      this(true);
   }

   public DefaultHttp2FrameReader(boolean var1) {
      this(new DefaultHttp2HeadersDecoder(var1));
   }

   public DefaultHttp2FrameReader(Http2HeadersDecoder var1) {
      super();
      this.readingHeaders = true;
      this.headersDecoder = var1;
      this.maxFrameSize = 16384;
   }

   public Http2HeadersDecoder.Configuration headersConfiguration() {
      return this.headersDecoder.configuration();
   }

   public Http2FrameReader.Configuration configuration() {
      return this;
   }

   public Http2FrameSizePolicy frameSizePolicy() {
      return this;
   }

   public void maxFrameSize(int var1) throws Http2Exception {
      if (!Http2CodecUtil.isMaxFrameSizeValid(var1)) {
         throw Http2Exception.streamError(this.streamId, Http2Error.FRAME_SIZE_ERROR, "Invalid MAX_FRAME_SIZE specified in sent settings: %d", var1);
      } else {
         this.maxFrameSize = var1;
      }
   }

   public int maxFrameSize() {
      return this.maxFrameSize;
   }

   public void close() {
      this.closeHeadersContinuation();
   }

   private void closeHeadersContinuation() {
      if (this.headersContinuation != null) {
         this.headersContinuation.close();
         this.headersContinuation = null;
      }

   }

   public void readFrame(ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      if (this.readError) {
         var2.skipBytes(var2.readableBytes());
      } else {
         try {
            do {
               if (this.readingHeaders) {
                  this.processHeaderState(var2);
                  if (this.readingHeaders) {
                     return;
                  }
               }

               this.processPayloadState(var1, var2, var3);
               if (!this.readingHeaders) {
                  return;
               }
            } while(var2.isReadable());
         } catch (Http2Exception var5) {
            this.readError = !Http2Exception.isStreamError(var5);
            throw var5;
         } catch (RuntimeException var6) {
            this.readError = true;
            throw var6;
         } catch (Throwable var7) {
            this.readError = true;
            PlatformDependent.throwException(var7);
         }

      }
   }

   private void processHeaderState(ByteBuf var1) throws Http2Exception {
      if (var1.readableBytes() >= 9) {
         this.payloadLength = var1.readUnsignedMedium();
         if (this.payloadLength > this.maxFrameSize) {
            throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Frame length: %d exceeds maximum: %d", this.payloadLength, this.maxFrameSize);
         } else {
            this.frameType = var1.readByte();
            this.flags = new Http2Flags(var1.readUnsignedByte());
            this.streamId = Http2CodecUtil.readUnsignedInt(var1);
            this.readingHeaders = false;
            switch(this.frameType) {
            case 0:
               this.verifyDataFrame();
               break;
            case 1:
               this.verifyHeadersFrame();
               break;
            case 2:
               this.verifyPriorityFrame();
               break;
            case 3:
               this.verifyRstStreamFrame();
               break;
            case 4:
               this.verifySettingsFrame();
               break;
            case 5:
               this.verifyPushPromiseFrame();
               break;
            case 6:
               this.verifyPingFrame();
               break;
            case 7:
               this.verifyGoAwayFrame();
               break;
            case 8:
               this.verifyWindowUpdateFrame();
               break;
            case 9:
               this.verifyContinuationFrame();
               break;
            default:
               this.verifyUnknownFrame();
            }

         }
      }
   }

   private void processPayloadState(ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      if (var2.readableBytes() >= this.payloadLength) {
         ByteBuf var4 = var2.readSlice(this.payloadLength);
         this.readingHeaders = true;
         switch(this.frameType) {
         case 0:
            this.readDataFrame(var1, var4, var3);
            break;
         case 1:
            this.readHeadersFrame(var1, var4, var3);
            break;
         case 2:
            this.readPriorityFrame(var1, var4, var3);
            break;
         case 3:
            this.readRstStreamFrame(var1, var4, var3);
            break;
         case 4:
            this.readSettingsFrame(var1, var4, var3);
            break;
         case 5:
            this.readPushPromiseFrame(var1, var4, var3);
            break;
         case 6:
            this.readPingFrame(var1, var4.readLong(), var3);
            break;
         case 7:
            readGoAwayFrame(var1, var4, var3);
            break;
         case 8:
            this.readWindowUpdateFrame(var1, var4, var3);
            break;
         case 9:
            this.readContinuationFrame(var4, var3);
            break;
         default:
            this.readUnknownFrame(var1, var4, var3);
         }

      }
   }

   private void verifyDataFrame() throws Http2Exception {
      this.verifyAssociatedWithAStream();
      this.verifyNotProcessingHeaders();
      this.verifyPayloadLength(this.payloadLength);
      if (this.payloadLength < this.flags.getPaddingPresenceFieldLength()) {
         throw Http2Exception.streamError(this.streamId, Http2Error.FRAME_SIZE_ERROR, "Frame length %d too small.", this.payloadLength);
      }
   }

   private void verifyHeadersFrame() throws Http2Exception {
      this.verifyAssociatedWithAStream();
      this.verifyNotProcessingHeaders();
      this.verifyPayloadLength(this.payloadLength);
      int var1 = this.flags.getPaddingPresenceFieldLength() + this.flags.getNumPriorityBytes();
      if (this.payloadLength < var1) {
         throw Http2Exception.streamError(this.streamId, Http2Error.FRAME_SIZE_ERROR, "Frame length too small." + this.payloadLength);
      }
   }

   private void verifyPriorityFrame() throws Http2Exception {
      this.verifyAssociatedWithAStream();
      this.verifyNotProcessingHeaders();
      if (this.payloadLength != 5) {
         throw Http2Exception.streamError(this.streamId, Http2Error.FRAME_SIZE_ERROR, "Invalid frame length %d.", this.payloadLength);
      }
   }

   private void verifyRstStreamFrame() throws Http2Exception {
      this.verifyAssociatedWithAStream();
      this.verifyNotProcessingHeaders();
      if (this.payloadLength != 4) {
         throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Invalid frame length %d.", this.payloadLength);
      }
   }

   private void verifySettingsFrame() throws Http2Exception {
      this.verifyNotProcessingHeaders();
      this.verifyPayloadLength(this.payloadLength);
      if (this.streamId != 0) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "A stream ID must be zero.");
      } else if (this.flags.ack() && this.payloadLength > 0) {
         throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Ack settings frame must have an empty payload.");
      } else if (this.payloadLength % 6 > 0) {
         throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Frame length %d invalid.", this.payloadLength);
      }
   }

   private void verifyPushPromiseFrame() throws Http2Exception {
      this.verifyNotProcessingHeaders();
      this.verifyPayloadLength(this.payloadLength);
      int var1 = this.flags.getPaddingPresenceFieldLength() + 4;
      if (this.payloadLength < var1) {
         throw Http2Exception.streamError(this.streamId, Http2Error.FRAME_SIZE_ERROR, "Frame length %d too small.", this.payloadLength);
      }
   }

   private void verifyPingFrame() throws Http2Exception {
      this.verifyNotProcessingHeaders();
      if (this.streamId != 0) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "A stream ID must be zero.");
      } else if (this.payloadLength != 8) {
         throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Frame length %d incorrect size for ping.", this.payloadLength);
      }
   }

   private void verifyGoAwayFrame() throws Http2Exception {
      this.verifyNotProcessingHeaders();
      this.verifyPayloadLength(this.payloadLength);
      if (this.streamId != 0) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "A stream ID must be zero.");
      } else if (this.payloadLength < 8) {
         throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Frame length %d too small.", this.payloadLength);
      }
   }

   private void verifyWindowUpdateFrame() throws Http2Exception {
      this.verifyNotProcessingHeaders();
      verifyStreamOrConnectionId(this.streamId, "Stream ID");
      if (this.payloadLength != 4) {
         throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Invalid frame length %d.", this.payloadLength);
      }
   }

   private void verifyContinuationFrame() throws Http2Exception {
      this.verifyAssociatedWithAStream();
      this.verifyPayloadLength(this.payloadLength);
      if (this.headersContinuation == null) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Received %s frame but not currently processing headers.", this.frameType);
      } else if (this.streamId != this.headersContinuation.getStreamId()) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Continuation stream ID does not match pending headers. Expected %d, but received %d.", this.headersContinuation.getStreamId(), this.streamId);
      } else if (this.payloadLength < this.flags.getPaddingPresenceFieldLength()) {
         throw Http2Exception.streamError(this.streamId, Http2Error.FRAME_SIZE_ERROR, "Frame length %d too small for padding.", this.payloadLength);
      }
   }

   private void verifyUnknownFrame() throws Http2Exception {
      this.verifyNotProcessingHeaders();
   }

   private void readDataFrame(ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      int var4 = this.readPadding(var2);
      this.verifyPadding(var4);
      int var5 = lengthWithoutTrailingPadding(var2.readableBytes(), var4);
      ByteBuf var6 = var2.readSlice(var5);
      var3.onDataRead(var1, this.streamId, var6, var4, this.flags.endOfStream());
      var2.skipBytes(var2.readableBytes());
   }

   private void readHeadersFrame(final ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      final int var4 = this.streamId;
      final Http2Flags var5 = this.flags;
      final int var6 = this.readPadding(var2);
      this.verifyPadding(var6);
      if (this.flags.priorityPresent()) {
         long var13 = var2.readUnsignedInt();
         final boolean var9 = (var13 & 2147483648L) != 0L;
         final int var10 = (int)(var13 & 2147483647L);
         if (var10 == this.streamId) {
            throw Http2Exception.streamError(this.streamId, Http2Error.PROTOCOL_ERROR, "A stream cannot depend on itself.");
         } else {
            final short var11 = (short)(var2.readUnsignedByte() + 1);
            ByteBuf var12 = var2.readSlice(lengthWithoutTrailingPadding(var2.readableBytes(), var6));
            this.headersContinuation = new DefaultHttp2FrameReader.HeadersContinuation() {
               public int getStreamId() {
                  return var4;
               }

               public void processFragment(boolean var1x, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
                  DefaultHttp2FrameReader.HeadersBlockBuilder var4x = this.headersBlockBuilder();
                  var4x.addFragment(var2, var1.alloc(), var1x);
                  if (var1x) {
                     var3.onHeadersRead(var1, var4, var4x.headers(), var10, var11, var9, var6, var5.endOfStream());
                  }

               }
            };
            this.headersContinuation.processFragment(this.flags.endOfHeaders(), var12, var3);
            this.resetHeadersContinuationIfEnd(this.flags.endOfHeaders());
         }
      } else {
         this.headersContinuation = new DefaultHttp2FrameReader.HeadersContinuation() {
            public int getStreamId() {
               return var4;
            }

            public void processFragment(boolean var1x, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
               DefaultHttp2FrameReader.HeadersBlockBuilder var4x = this.headersBlockBuilder();
               var4x.addFragment(var2, var1.alloc(), var1x);
               if (var1x) {
                  var3.onHeadersRead(var1, var4, var4x.headers(), var6, var5.endOfStream());
               }

            }
         };
         ByteBuf var7 = var2.readSlice(lengthWithoutTrailingPadding(var2.readableBytes(), var6));
         this.headersContinuation.processFragment(this.flags.endOfHeaders(), var7, var3);
         this.resetHeadersContinuationIfEnd(this.flags.endOfHeaders());
      }
   }

   private void resetHeadersContinuationIfEnd(boolean var1) {
      if (var1) {
         this.closeHeadersContinuation();
      }

   }

   private void readPriorityFrame(ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      long var4 = var2.readUnsignedInt();
      boolean var6 = (var4 & 2147483648L) != 0L;
      int var7 = (int)(var4 & 2147483647L);
      if (var7 == this.streamId) {
         throw Http2Exception.streamError(this.streamId, Http2Error.PROTOCOL_ERROR, "A stream cannot depend on itself.");
      } else {
         short var8 = (short)(var2.readUnsignedByte() + 1);
         var3.onPriorityRead(var1, this.streamId, var7, var8, var6);
      }
   }

   private void readRstStreamFrame(ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      long var4 = var2.readUnsignedInt();
      var3.onRstStreamRead(var1, this.streamId, var4);
   }

   private void readSettingsFrame(ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      if (this.flags.ack()) {
         var3.onSettingsAckRead(var1);
      } else {
         int var4 = this.payloadLength / 6;
         Http2Settings var5 = new Http2Settings();

         for(int var6 = 0; var6 < var4; ++var6) {
            char var7 = (char)var2.readUnsignedShort();
            long var8 = var2.readUnsignedInt();

            try {
               var5.put(var7, var8);
            } catch (IllegalArgumentException var11) {
               switch(var7) {
               case '\u0004':
                  throw Http2Exception.connectionError(Http2Error.FLOW_CONTROL_ERROR, var11, var11.getMessage());
               case '\u0005':
                  throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, var11, var11.getMessage());
               default:
                  throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, var11, var11.getMessage());
               }
            }
         }

         var3.onSettingsRead(var1, var5);
      }

   }

   private void readPushPromiseFrame(final ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      final int var4 = this.streamId;
      final int var5 = this.readPadding(var2);
      this.verifyPadding(var5);
      final int var6 = Http2CodecUtil.readUnsignedInt(var2);
      this.headersContinuation = new DefaultHttp2FrameReader.HeadersContinuation() {
         public int getStreamId() {
            return var4;
         }

         public void processFragment(boolean var1x, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
            this.headersBlockBuilder().addFragment(var2, var1.alloc(), var1x);
            if (var1x) {
               var3.onPushPromiseRead(var1, var4, var6, this.headersBlockBuilder().headers(), var5);
            }

         }
      };
      ByteBuf var7 = var2.readSlice(lengthWithoutTrailingPadding(var2.readableBytes(), var5));
      this.headersContinuation.processFragment(this.flags.endOfHeaders(), var7, var3);
      this.resetHeadersContinuationIfEnd(this.flags.endOfHeaders());
   }

   private void readPingFrame(ChannelHandlerContext var1, long var2, Http2FrameListener var4) throws Http2Exception {
      if (this.flags.ack()) {
         var4.onPingAckRead(var1, var2);
      } else {
         var4.onPingRead(var1, var2);
      }

   }

   private static void readGoAwayFrame(ChannelHandlerContext var0, ByteBuf var1, Http2FrameListener var2) throws Http2Exception {
      int var3 = Http2CodecUtil.readUnsignedInt(var1);
      long var4 = var1.readUnsignedInt();
      ByteBuf var6 = var1.readSlice(var1.readableBytes());
      var2.onGoAwayRead(var0, var3, var4, var6);
   }

   private void readWindowUpdateFrame(ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      int var4 = Http2CodecUtil.readUnsignedInt(var2);
      if (var4 == 0) {
         throw Http2Exception.streamError(this.streamId, Http2Error.PROTOCOL_ERROR, "Received WINDOW_UPDATE with delta 0 for stream: %d", this.streamId);
      } else {
         var3.onWindowUpdateRead(var1, this.streamId, var4);
      }
   }

   private void readContinuationFrame(ByteBuf var1, Http2FrameListener var2) throws Http2Exception {
      ByteBuf var3 = var1.readSlice(var1.readableBytes());
      this.headersContinuation.processFragment(this.flags.endOfHeaders(), var3, var2);
      this.resetHeadersContinuationIfEnd(this.flags.endOfHeaders());
   }

   private void readUnknownFrame(ChannelHandlerContext var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception {
      var2 = var2.readSlice(var2.readableBytes());
      var3.onUnknownFrame(var1, this.frameType, this.streamId, this.flags, var2);
   }

   private int readPadding(ByteBuf var1) {
      return !this.flags.paddingPresent() ? 0 : var1.readUnsignedByte() + 1;
   }

   private void verifyPadding(int var1) throws Http2Exception {
      int var2 = lengthWithoutTrailingPadding(this.payloadLength, var1);
      if (var2 < 0) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Frame payload too small for padding.");
      }
   }

   private static int lengthWithoutTrailingPadding(int var0, int var1) {
      return var1 == 0 ? var0 : var0 - (var1 - 1);
   }

   private void verifyNotProcessingHeaders() throws Http2Exception {
      if (this.headersContinuation != null) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Received frame of type %s while processing headers on stream %d.", this.frameType, this.headersContinuation.getStreamId());
      }
   }

   private void verifyPayloadLength(int var1) throws Http2Exception {
      if (var1 > this.maxFrameSize) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Total payload length %d exceeds max frame length.", var1);
      }
   }

   private void verifyAssociatedWithAStream() throws Http2Exception {
      if (this.streamId == 0) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Frame of type %s must be associated with a stream.", this.frameType);
      }
   }

   private static void verifyStreamOrConnectionId(int var0, String var1) throws Http2Exception {
      if (var0 < 0) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "%s must be >= 0", var1);
      }
   }

   protected class HeadersBlockBuilder {
      private ByteBuf headerBlock;

      protected HeadersBlockBuilder() {
         super();
      }

      private void headerSizeExceeded() throws Http2Exception {
         this.close();
         Http2CodecUtil.headerListSizeExceeded(DefaultHttp2FrameReader.this.headersDecoder.configuration().maxHeaderListSizeGoAway());
      }

      final void addFragment(ByteBuf var1, ByteBufAllocator var2, boolean var3) throws Http2Exception {
         if (this.headerBlock == null) {
            if ((long)var1.readableBytes() > DefaultHttp2FrameReader.this.headersDecoder.configuration().maxHeaderListSizeGoAway()) {
               this.headerSizeExceeded();
            }

            if (var3) {
               this.headerBlock = var1.retain();
            } else {
               this.headerBlock = var2.buffer(var1.readableBytes());
               this.headerBlock.writeBytes(var1);
            }

         } else {
            if (DefaultHttp2FrameReader.this.headersDecoder.configuration().maxHeaderListSizeGoAway() - (long)var1.readableBytes() < (long)this.headerBlock.readableBytes()) {
               this.headerSizeExceeded();
            }

            if (this.headerBlock.isWritable(var1.readableBytes())) {
               this.headerBlock.writeBytes(var1);
            } else {
               ByteBuf var4 = var2.buffer(this.headerBlock.readableBytes() + var1.readableBytes());
               var4.writeBytes(this.headerBlock);
               var4.writeBytes(var1);
               this.headerBlock.release();
               this.headerBlock = var4;
            }

         }
      }

      Http2Headers headers() throws Http2Exception {
         Http2Headers var1;
         try {
            var1 = DefaultHttp2FrameReader.this.headersDecoder.decodeHeaders(DefaultHttp2FrameReader.this.streamId, this.headerBlock);
         } finally {
            this.close();
         }

         return var1;
      }

      void close() {
         if (this.headerBlock != null) {
            this.headerBlock.release();
            this.headerBlock = null;
         }

         DefaultHttp2FrameReader.this.headersContinuation = null;
      }
   }

   private abstract class HeadersContinuation {
      private final DefaultHttp2FrameReader.HeadersBlockBuilder builder;

      private HeadersContinuation() {
         super();
         this.builder = DefaultHttp2FrameReader.this.new HeadersBlockBuilder();
      }

      abstract int getStreamId();

      abstract void processFragment(boolean var1, ByteBuf var2, Http2FrameListener var3) throws Http2Exception;

      final DefaultHttp2FrameReader.HeadersBlockBuilder headersBlockBuilder() {
         return this.builder;
      }

      final void close() {
         this.builder.close();
      }

      // $FF: synthetic method
      HeadersContinuation(Object var2) {
         this();
      }
   }
}
