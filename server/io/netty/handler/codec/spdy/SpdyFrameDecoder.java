package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SpdyFrameDecoder {
   private final int spdyVersion;
   private final int maxChunkSize;
   private final SpdyFrameDecoderDelegate delegate;
   private SpdyFrameDecoder.State state;
   private byte flags;
   private int length;
   private int streamId;
   private int numSettings;

   public SpdyFrameDecoder(SpdyVersion var1, SpdyFrameDecoderDelegate var2) {
      this(var1, var2, 8192);
   }

   public SpdyFrameDecoder(SpdyVersion var1, SpdyFrameDecoderDelegate var2, int var3) {
      super();
      if (var1 == null) {
         throw new NullPointerException("spdyVersion");
      } else if (var2 == null) {
         throw new NullPointerException("delegate");
      } else if (var3 <= 0) {
         throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + var3);
      } else {
         this.spdyVersion = var1.getVersion();
         this.delegate = var2;
         this.maxChunkSize = var3;
         this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
      }
   }

   public void decode(ByteBuf var1) {
      while(true) {
         boolean var2;
         int var3;
         switch(this.state) {
         case READ_COMMON_HEADER:
            if (var1.readableBytes() < 8) {
               return;
            }

            int var4 = var1.readerIndex();
            int var5 = var4 + 4;
            int var6 = var4 + 5;
            var1.skipBytes(8);
            boolean var7 = (var1.getByte(var4) & 128) != 0;
            int var8;
            int var9;
            if (var7) {
               var8 = SpdyCodecUtil.getUnsignedShort(var1, var4) & 32767;
               var9 = SpdyCodecUtil.getUnsignedShort(var1, var4 + 2);
               this.streamId = 0;
            } else {
               var8 = this.spdyVersion;
               var9 = 0;
               this.streamId = SpdyCodecUtil.getUnsignedInt(var1, var4);
            }

            this.flags = var1.getByte(var5);
            this.length = SpdyCodecUtil.getUnsignedMedium(var1, var6);
            if (var8 != this.spdyVersion) {
               this.state = SpdyFrameDecoder.State.FRAME_ERROR;
               this.delegate.readFrameError("Invalid SPDY Version");
            } else {
               if (!isValidFrameHeader(this.streamId, var9, this.flags, this.length)) {
                  this.state = SpdyFrameDecoder.State.FRAME_ERROR;
                  this.delegate.readFrameError("Invalid Frame Error");
                  break;
               }

               this.state = getNextState(var9, this.length);
            }
            break;
         case READ_DATA_FRAME:
            if (this.length == 0) {
               this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
               this.delegate.readDataFrame(this.streamId, hasFlag(this.flags, (byte)1), Unpooled.buffer(0));
               break;
            }

            int var10 = Math.min(this.maxChunkSize, this.length);
            if (var1.readableBytes() < var10) {
               return;
            }

            ByteBuf var11 = var1.alloc().buffer(var10);
            var11.writeBytes(var1, var10);
            this.length -= var10;
            if (this.length == 0) {
               this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
            }

            var2 = this.length == 0 && hasFlag(this.flags, (byte)1);
            this.delegate.readDataFrame(this.streamId, var2, var11);
            break;
         case READ_SYN_STREAM_FRAME:
            if (var1.readableBytes() < 10) {
               return;
            }

            int var12 = var1.readerIndex();
            this.streamId = SpdyCodecUtil.getUnsignedInt(var1, var12);
            int var13 = SpdyCodecUtil.getUnsignedInt(var1, var12 + 4);
            byte var14 = (byte)(var1.getByte(var12 + 8) >> 5 & 7);
            var2 = hasFlag(this.flags, (byte)1);
            boolean var15 = hasFlag(this.flags, (byte)2);
            var1.skipBytes(10);
            this.length -= 10;
            if (this.streamId == 0) {
               this.state = SpdyFrameDecoder.State.FRAME_ERROR;
               this.delegate.readFrameError("Invalid SYN_STREAM Frame");
               break;
            }

            this.state = SpdyFrameDecoder.State.READ_HEADER_BLOCK;
            this.delegate.readSynStreamFrame(this.streamId, var13, var14, var2, var15);
            break;
         case READ_SYN_REPLY_FRAME:
            if (var1.readableBytes() < 4) {
               return;
            }

            this.streamId = SpdyCodecUtil.getUnsignedInt(var1, var1.readerIndex());
            var2 = hasFlag(this.flags, (byte)1);
            var1.skipBytes(4);
            this.length -= 4;
            if (this.streamId == 0) {
               this.state = SpdyFrameDecoder.State.FRAME_ERROR;
               this.delegate.readFrameError("Invalid SYN_REPLY Frame");
               break;
            }

            this.state = SpdyFrameDecoder.State.READ_HEADER_BLOCK;
            this.delegate.readSynReplyFrame(this.streamId, var2);
            break;
         case READ_RST_STREAM_FRAME:
            if (var1.readableBytes() < 8) {
               return;
            }

            this.streamId = SpdyCodecUtil.getUnsignedInt(var1, var1.readerIndex());
            var3 = SpdyCodecUtil.getSignedInt(var1, var1.readerIndex() + 4);
            var1.skipBytes(8);
            if (this.streamId != 0 && var3 != 0) {
               this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
               this.delegate.readRstStreamFrame(this.streamId, var3);
               break;
            }

            this.state = SpdyFrameDecoder.State.FRAME_ERROR;
            this.delegate.readFrameError("Invalid RST_STREAM Frame");
            break;
         case READ_SETTINGS_FRAME:
            if (var1.readableBytes() < 4) {
               return;
            }

            boolean var16 = hasFlag(this.flags, (byte)1);
            this.numSettings = SpdyCodecUtil.getUnsignedInt(var1, var1.readerIndex());
            var1.skipBytes(4);
            this.length -= 4;
            if ((this.length & 7) == 0 && this.length >> 3 == this.numSettings) {
               this.state = SpdyFrameDecoder.State.READ_SETTING;
               this.delegate.readSettingsFrame(var16);
               break;
            }

            this.state = SpdyFrameDecoder.State.FRAME_ERROR;
            this.delegate.readFrameError("Invalid SETTINGS Frame");
            break;
         case READ_SETTING:
            if (this.numSettings == 0) {
               this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
               this.delegate.readSettingsEnd();
               break;
            }

            if (var1.readableBytes() < 8) {
               return;
            }

            byte var17 = var1.getByte(var1.readerIndex());
            int var18 = SpdyCodecUtil.getUnsignedMedium(var1, var1.readerIndex() + 1);
            int var19 = SpdyCodecUtil.getSignedInt(var1, var1.readerIndex() + 4);
            boolean var20 = hasFlag(var17, (byte)1);
            boolean var21 = hasFlag(var17, (byte)2);
            var1.skipBytes(8);
            --this.numSettings;
            this.delegate.readSetting(var18, var19, var20, var21);
            break;
         case READ_PING_FRAME:
            if (var1.readableBytes() < 4) {
               return;
            }

            int var22 = SpdyCodecUtil.getSignedInt(var1, var1.readerIndex());
            var1.skipBytes(4);
            this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
            this.delegate.readPingFrame(var22);
            break;
         case READ_GOAWAY_FRAME:
            if (var1.readableBytes() < 8) {
               return;
            }

            int var23 = SpdyCodecUtil.getUnsignedInt(var1, var1.readerIndex());
            var3 = SpdyCodecUtil.getSignedInt(var1, var1.readerIndex() + 4);
            var1.skipBytes(8);
            this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
            this.delegate.readGoAwayFrame(var23, var3);
            break;
         case READ_HEADERS_FRAME:
            if (var1.readableBytes() < 4) {
               return;
            }

            this.streamId = SpdyCodecUtil.getUnsignedInt(var1, var1.readerIndex());
            var2 = hasFlag(this.flags, (byte)1);
            var1.skipBytes(4);
            this.length -= 4;
            if (this.streamId == 0) {
               this.state = SpdyFrameDecoder.State.FRAME_ERROR;
               this.delegate.readFrameError("Invalid HEADERS Frame");
               break;
            }

            this.state = SpdyFrameDecoder.State.READ_HEADER_BLOCK;
            this.delegate.readHeadersFrame(this.streamId, var2);
            break;
         case READ_WINDOW_UPDATE_FRAME:
            if (var1.readableBytes() < 8) {
               return;
            }

            this.streamId = SpdyCodecUtil.getUnsignedInt(var1, var1.readerIndex());
            int var24 = SpdyCodecUtil.getUnsignedInt(var1, var1.readerIndex() + 4);
            var1.skipBytes(8);
            if (var24 == 0) {
               this.state = SpdyFrameDecoder.State.FRAME_ERROR;
               this.delegate.readFrameError("Invalid WINDOW_UPDATE Frame");
               break;
            }

            this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
            this.delegate.readWindowUpdateFrame(this.streamId, var24);
            break;
         case READ_HEADER_BLOCK:
            if (this.length == 0) {
               this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
               this.delegate.readHeaderBlockEnd();
               break;
            }

            if (!var1.isReadable()) {
               return;
            }

            int var25 = Math.min(var1.readableBytes(), this.length);
            ByteBuf var26 = var1.alloc().buffer(var25);
            var26.writeBytes(var1, var25);
            this.length -= var25;
            this.delegate.readHeaderBlock(var26);
            break;
         case DISCARD_FRAME:
            int var27 = Math.min(var1.readableBytes(), this.length);
            var1.skipBytes(var27);
            this.length -= var27;
            if (this.length == 0) {
               this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
               break;
            }

            return;
         case FRAME_ERROR:
            var1.skipBytes(var1.readableBytes());
            return;
         default:
            throw new Error("Shouldn't reach here.");
         }
      }
   }

   private static boolean hasFlag(byte var0, byte var1) {
      return (var0 & var1) != 0;
   }

   private static SpdyFrameDecoder.State getNextState(int var0, int var1) {
      switch(var0) {
      case 0:
         return SpdyFrameDecoder.State.READ_DATA_FRAME;
      case 1:
         return SpdyFrameDecoder.State.READ_SYN_STREAM_FRAME;
      case 2:
         return SpdyFrameDecoder.State.READ_SYN_REPLY_FRAME;
      case 3:
         return SpdyFrameDecoder.State.READ_RST_STREAM_FRAME;
      case 4:
         return SpdyFrameDecoder.State.READ_SETTINGS_FRAME;
      case 5:
      default:
         if (var1 != 0) {
            return SpdyFrameDecoder.State.DISCARD_FRAME;
         }

         return SpdyFrameDecoder.State.READ_COMMON_HEADER;
      case 6:
         return SpdyFrameDecoder.State.READ_PING_FRAME;
      case 7:
         return SpdyFrameDecoder.State.READ_GOAWAY_FRAME;
      case 8:
         return SpdyFrameDecoder.State.READ_HEADERS_FRAME;
      case 9:
         return SpdyFrameDecoder.State.READ_WINDOW_UPDATE_FRAME;
      }
   }

   private static boolean isValidFrameHeader(int var0, int var1, byte var2, int var3) {
      switch(var1) {
      case 0:
         return var0 != 0;
      case 1:
         return var3 >= 10;
      case 2:
         return var3 >= 4;
      case 3:
         return var2 == 0 && var3 == 8;
      case 4:
         return var3 >= 4;
      case 5:
      default:
         return true;
      case 6:
         return var3 == 4;
      case 7:
         return var3 == 8;
      case 8:
         return var3 >= 4;
      case 9:
         return var3 == 8;
      }
   }

   private static enum State {
      READ_COMMON_HEADER,
      READ_DATA_FRAME,
      READ_SYN_STREAM_FRAME,
      READ_SYN_REPLY_FRAME,
      READ_RST_STREAM_FRAME,
      READ_SETTINGS_FRAME,
      READ_SETTING,
      READ_PING_FRAME,
      READ_GOAWAY_FRAME,
      READ_HEADERS_FRAME,
      READ_WINDOW_UPDATE_FRAME,
      READ_HEADER_BLOCK,
      DISCARD_FRAME,
      FRAME_ERROR;

      private State() {
      }
   }
}
