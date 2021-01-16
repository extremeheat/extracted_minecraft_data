package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteOrder;
import java.util.List;

public class WebSocket08FrameDecoder extends ByteToMessageDecoder implements WebSocketFrameDecoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
   private static final byte OPCODE_CONT = 0;
   private static final byte OPCODE_TEXT = 1;
   private static final byte OPCODE_BINARY = 2;
   private static final byte OPCODE_CLOSE = 8;
   private static final byte OPCODE_PING = 9;
   private static final byte OPCODE_PONG = 10;
   private final long maxFramePayloadLength;
   private final boolean allowExtensions;
   private final boolean expectMaskedFrames;
   private final boolean allowMaskMismatch;
   private int fragmentedFramesCount;
   private boolean frameFinalFlag;
   private boolean frameMasked;
   private int frameRsv;
   private int frameOpcode;
   private long framePayloadLength;
   private byte[] maskingKey;
   private int framePayloadLen1;
   private boolean receivedClosingHandshake;
   private WebSocket08FrameDecoder.State state;

   public WebSocket08FrameDecoder(boolean var1, boolean var2, int var3) {
      this(var1, var2, var3, false);
   }

   public WebSocket08FrameDecoder(boolean var1, boolean var2, int var3, boolean var4) {
      super();
      this.state = WebSocket08FrameDecoder.State.READING_FIRST;
      this.expectMaskedFrames = var1;
      this.allowMaskMismatch = var4;
      this.allowExtensions = var2;
      this.maxFramePayloadLength = (long)var3;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.receivedClosingHandshake) {
         var2.skipBytes(this.actualReadableBytes());
      } else {
         byte var4;
         switch(this.state) {
         case READING_FIRST:
            if (!var2.isReadable()) {
               return;
            }

            this.framePayloadLength = 0L;
            var4 = var2.readByte();
            this.frameFinalFlag = (var4 & 128) != 0;
            this.frameRsv = (var4 & 112) >> 4;
            this.frameOpcode = var4 & 15;
            if (logger.isDebugEnabled()) {
               logger.debug("Decoding WebSocket Frame opCode={}", (Object)this.frameOpcode);
            }

            this.state = WebSocket08FrameDecoder.State.READING_SECOND;
         case READING_SECOND:
            if (!var2.isReadable()) {
               return;
            }

            var4 = var2.readByte();
            this.frameMasked = (var4 & 128) != 0;
            this.framePayloadLen1 = var4 & 127;
            if (this.frameRsv != 0 && !this.allowExtensions) {
               this.protocolViolation(var1, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
               return;
            }

            if (!this.allowMaskMismatch && this.expectMaskedFrames != this.frameMasked) {
               this.protocolViolation(var1, "received a frame that is not masked as expected");
               return;
            }

            if (this.frameOpcode > 7) {
               if (!this.frameFinalFlag) {
                  this.protocolViolation(var1, "fragmented control frame");
                  return;
               }

               if (this.framePayloadLen1 > 125) {
                  this.protocolViolation(var1, "control frame with payload length > 125 octets");
                  return;
               }

               if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                  this.protocolViolation(var1, "control frame using reserved opcode " + this.frameOpcode);
                  return;
               }

               if (this.frameOpcode == 8 && this.framePayloadLen1 == 1) {
                  this.protocolViolation(var1, "received close control frame with payload len 1");
                  return;
               }
            } else {
               if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                  this.protocolViolation(var1, "data frame using reserved opcode " + this.frameOpcode);
                  return;
               }

               if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                  this.protocolViolation(var1, "received continuation data frame outside fragmented message");
                  return;
               }

               if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0 && this.frameOpcode != 9) {
                  this.protocolViolation(var1, "received non-continuation data frame while inside fragmented message");
                  return;
               }
            }

            this.state = WebSocket08FrameDecoder.State.READING_SIZE;
         case READING_SIZE:
            if (this.framePayloadLen1 == 126) {
               if (var2.readableBytes() < 2) {
                  return;
               }

               this.framePayloadLength = (long)var2.readUnsignedShort();
               if (this.framePayloadLength < 126L) {
                  this.protocolViolation(var1, "invalid data frame length (not using minimal length encoding)");
                  return;
               }
            } else if (this.framePayloadLen1 == 127) {
               if (var2.readableBytes() < 8) {
                  return;
               }

               this.framePayloadLength = var2.readLong();
               if (this.framePayloadLength < 65536L) {
                  this.protocolViolation(var1, "invalid data frame length (not using minimal length encoding)");
                  return;
               }
            } else {
               this.framePayloadLength = (long)this.framePayloadLen1;
            }

            if (this.framePayloadLength > this.maxFramePayloadLength) {
               this.protocolViolation(var1, "Max frame length of " + this.maxFramePayloadLength + " has been exceeded.");
               return;
            }

            if (logger.isDebugEnabled()) {
               logger.debug("Decoding WebSocket Frame length={}", (Object)this.framePayloadLength);
            }

            this.state = WebSocket08FrameDecoder.State.MASKING_KEY;
         case MASKING_KEY:
            if (this.frameMasked) {
               if (var2.readableBytes() < 4) {
                  return;
               }

               if (this.maskingKey == null) {
                  this.maskingKey = new byte[4];
               }

               var2.readBytes(this.maskingKey);
            }

            this.state = WebSocket08FrameDecoder.State.PAYLOAD;
         case PAYLOAD:
            break;
         case CORRUPT:
            if (var2.isReadable()) {
               var2.readByte();
            }

            return;
         default:
            throw new Error("Shouldn't reach here.");
         }

         if ((long)var2.readableBytes() >= this.framePayloadLength) {
            ByteBuf var5 = null;

            try {
               var5 = ByteBufUtil.readBytes(var1.alloc(), var2, toFrameLength(this.framePayloadLength));
               this.state = WebSocket08FrameDecoder.State.READING_FIRST;
               if (this.frameMasked) {
                  this.unmask(var5);
               }

               if (this.frameOpcode == 9) {
                  var3.add(new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameOpcode == 10) {
                  var3.add(new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameOpcode == 8) {
                  this.receivedClosingHandshake = true;
                  this.checkCloseFrameBody(var1, var5);
                  var3.add(new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameFinalFlag) {
                  if (this.frameOpcode != 9) {
                     this.fragmentedFramesCount = 0;
                  }
               } else {
                  ++this.fragmentedFramesCount;
               }

               if (this.frameOpcode == 1) {
                  var3.add(new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameOpcode == 2) {
                  var3.add(new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameOpcode != 0) {
                  throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
               }

               var3.add(new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
               var5 = null;
            } finally {
               if (var5 != null) {
                  var5.release();
               }

            }

         }
      }
   }

   private void unmask(ByteBuf var1) {
      int var2 = var1.readerIndex();
      int var3 = var1.writerIndex();
      ByteOrder var4 = var1.order();
      int var5 = (this.maskingKey[0] & 255) << 24 | (this.maskingKey[1] & 255) << 16 | (this.maskingKey[2] & 255) << 8 | this.maskingKey[3] & 255;
      if (var4 == ByteOrder.LITTLE_ENDIAN) {
         var5 = Integer.reverseBytes(var5);
      }

      while(var2 + 3 < var3) {
         int var6 = var1.getInt(var2) ^ var5;
         var1.setInt(var2, var6);
         var2 += 4;
      }

      while(var2 < var3) {
         var1.setByte(var2, var1.getByte(var2) ^ this.maskingKey[var2 % 4]);
         ++var2;
      }

   }

   private void protocolViolation(ChannelHandlerContext var1, String var2) {
      this.protocolViolation(var1, new CorruptedFrameException(var2));
   }

   private void protocolViolation(ChannelHandlerContext var1, CorruptedFrameException var2) {
      this.state = WebSocket08FrameDecoder.State.CORRUPT;
      if (var1.channel().isActive()) {
         Object var3;
         if (this.receivedClosingHandshake) {
            var3 = Unpooled.EMPTY_BUFFER;
         } else {
            var3 = new CloseWebSocketFrame(1002, (String)null);
         }

         var1.writeAndFlush(var3).addListener(ChannelFutureListener.CLOSE);
      }

      throw var2;
   }

   private static int toFrameLength(long var0) {
      if (var0 > 2147483647L) {
         throw new TooLongFrameException("Length:" + var0);
      } else {
         return (int)var0;
      }
   }

   protected void checkCloseFrameBody(ChannelHandlerContext var1, ByteBuf var2) {
      if (var2 != null && var2.isReadable()) {
         if (var2.readableBytes() == 1) {
            this.protocolViolation(var1, "Invalid close frame body");
         }

         int var3 = var2.readerIndex();
         var2.readerIndex(0);
         short var4 = var2.readShort();
         if (var4 >= 0 && var4 <= 999 || var4 >= 1004 && var4 <= 1006 || var4 >= 1012 && var4 <= 2999) {
            this.protocolViolation(var1, "Invalid close frame getStatus code: " + var4);
         }

         if (var2.isReadable()) {
            try {
               (new Utf8Validator()).check(var2);
            } catch (CorruptedFrameException var6) {
               this.protocolViolation(var1, var6);
            }
         }

         var2.readerIndex(var3);
      }
   }

   static enum State {
      READING_FIRST,
      READING_SECOND,
      READING_SIZE,
      MASKING_KEY,
      PAYLOAD,
      CORRUPT;

      private State() {
      }
   }
}
