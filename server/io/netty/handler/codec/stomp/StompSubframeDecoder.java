package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.AppendableCharSequence;
import java.util.List;
import java.util.Locale;

public class StompSubframeDecoder extends ReplayingDecoder<StompSubframeDecoder.State> {
   private static final int DEFAULT_CHUNK_SIZE = 8132;
   private static final int DEFAULT_MAX_LINE_LENGTH = 1024;
   private final int maxLineLength;
   private final int maxChunkSize;
   private final boolean validateHeaders;
   private int alreadyReadChunkSize;
   private LastStompContentSubframe lastContent;
   private long contentLength;

   public StompSubframeDecoder() {
      this(1024, 8132);
   }

   public StompSubframeDecoder(boolean var1) {
      this(1024, 8132, var1);
   }

   public StompSubframeDecoder(int var1, int var2) {
      this(var1, var2, false);
   }

   public StompSubframeDecoder(int var1, int var2, boolean var3) {
      super(StompSubframeDecoder.State.SKIP_CONTROL_CHARACTERS);
      this.contentLength = -1L;
      if (var1 <= 0) {
         throw new IllegalArgumentException("maxLineLength must be a positive integer: " + var1);
      } else if (var2 <= 0) {
         throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + var2);
      } else {
         this.maxChunkSize = var2;
         this.maxLineLength = var1;
         this.validateHeaders = var3;
      }
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      switch((StompSubframeDecoder.State)this.state()) {
      case SKIP_CONTROL_CHARACTERS:
         skipControlCharacters(var2);
         this.checkpoint(StompSubframeDecoder.State.READ_HEADERS);
      case READ_HEADERS:
         StompCommand var4 = StompCommand.UNKNOWN;
         DefaultStompHeadersSubframe var5 = null;

         try {
            var4 = this.readCommand(var2);
            var5 = new DefaultStompHeadersSubframe(var4);
            this.checkpoint(this.readHeaders(var2, var5.headers()));
            var3.add(var5);
         } catch (Exception var8) {
            if (var5 == null) {
               var5 = new DefaultStompHeadersSubframe(var4);
            }

            var5.setDecoderResult(DecoderResult.failure(var8));
            var3.add(var5);
            this.checkpoint(StompSubframeDecoder.State.BAD_FRAME);
            return;
         }
      default:
         try {
            switch((StompSubframeDecoder.State)this.state()) {
            case READ_CONTENT:
               int var9 = var2.readableBytes();
               if (var9 == 0) {
                  return;
               }

               if (var9 > this.maxChunkSize) {
                  var9 = this.maxChunkSize;
               }

               ByteBuf var6;
               int var11;
               if (this.contentLength >= 0L) {
                  var11 = (int)(this.contentLength - (long)this.alreadyReadChunkSize);
                  if (var9 > var11) {
                     var9 = var11;
                  }

                  var6 = ByteBufUtil.readBytes(var1.alloc(), var2, var9);
                  if ((long)(this.alreadyReadChunkSize += var9) < this.contentLength) {
                     var3.add(new DefaultStompContentSubframe(var6));
                     return;
                  }

                  this.lastContent = new DefaultLastStompContentSubframe(var6);
                  this.checkpoint(StompSubframeDecoder.State.FINALIZE_FRAME_READ);
               } else {
                  var11 = ByteBufUtil.indexOf(var2, var2.readerIndex(), var2.writerIndex(), (byte)0);
                  if (var11 == var2.readerIndex()) {
                     this.checkpoint(StompSubframeDecoder.State.FINALIZE_FRAME_READ);
                  } else {
                     if (var11 > 0) {
                        var9 = var11 - var2.readerIndex();
                     } else {
                        var9 = var2.writerIndex() - var2.readerIndex();
                     }

                     var6 = ByteBufUtil.readBytes(var1.alloc(), var2, var9);
                     this.alreadyReadChunkSize += var9;
                     if (var11 <= 0) {
                        var3.add(new DefaultStompContentSubframe(var6));
                        return;
                     }

                     this.lastContent = new DefaultLastStompContentSubframe(var6);
                     this.checkpoint(StompSubframeDecoder.State.FINALIZE_FRAME_READ);
                  }
               }
            case FINALIZE_FRAME_READ:
               skipNullCharacter(var2);
               if (this.lastContent == null) {
                  this.lastContent = LastStompContentSubframe.EMPTY_LAST_CONTENT;
               }

               var3.add(this.lastContent);
               this.resetDecoder();
            }
         } catch (Exception var7) {
            DefaultLastStompContentSubframe var10 = new DefaultLastStompContentSubframe(Unpooled.EMPTY_BUFFER);
            var10.setDecoderResult(DecoderResult.failure(var7));
            var3.add(var10);
            this.checkpoint(StompSubframeDecoder.State.BAD_FRAME);
         }

         return;
      case BAD_FRAME:
         var2.skipBytes(this.actualReadableBytes());
      }
   }

   private StompCommand readCommand(ByteBuf var1) {
      String var2 = this.readLine(var1, 16);
      StompCommand var3 = null;

      try {
         var3 = StompCommand.valueOf(var2);
      } catch (IllegalArgumentException var6) {
      }

      if (var3 == null) {
         var2 = var2.toUpperCase(Locale.US);

         try {
            var3 = StompCommand.valueOf(var2);
         } catch (IllegalArgumentException var5) {
         }
      }

      if (var3 == null) {
         throw new DecoderException("failed to read command from channel");
      } else {
         return var3;
      }
   }

   private StompSubframeDecoder.State readHeaders(ByteBuf var1, StompHeaders var2) {
      AppendableCharSequence var3 = new AppendableCharSequence(128);

      boolean var4;
      do {
         var4 = this.readHeader(var2, var3, var1);
      } while(var4);

      if (var2.contains(StompHeaders.CONTENT_LENGTH)) {
         this.contentLength = getContentLength(var2, 0L);
         if (this.contentLength == 0L) {
            return StompSubframeDecoder.State.FINALIZE_FRAME_READ;
         }
      }

      return StompSubframeDecoder.State.READ_CONTENT;
   }

   private static long getContentLength(StompHeaders var0, long var1) {
      long var3 = var0.getLong(StompHeaders.CONTENT_LENGTH, var1);
      if (var3 < 0L) {
         throw new DecoderException(StompHeaders.CONTENT_LENGTH + " must be non-negative");
      } else {
         return var3;
      }
   }

   private static void skipNullCharacter(ByteBuf var0) {
      byte var1 = var0.readByte();
      if (var1 != 0) {
         throw new IllegalStateException("unexpected byte in buffer " + var1 + " while expecting NULL byte");
      }
   }

   private static void skipControlCharacters(ByteBuf var0) {
      byte var1;
      do {
         var1 = var0.readByte();
      } while(var1 == 13 || var1 == 10);

      var0.readerIndex(var0.readerIndex() - 1);
   }

   private String readLine(ByteBuf var1, int var2) {
      AppendableCharSequence var3 = new AppendableCharSequence(var2);
      int var4 = 0;

      while(true) {
         while(true) {
            byte var5 = var1.readByte();
            if (var5 != 13) {
               if (var5 == 10) {
                  return var3.toString();
               }

               if (var4 >= this.maxLineLength) {
                  this.invalidLineLength();
               }

               ++var4;
               var3.append((char)var5);
            }
         }
      }
   }

   private boolean readHeader(StompHeaders var1, AppendableCharSequence var2, ByteBuf var3) {
      var2.reset();
      int var4 = 0;
      String var5 = null;
      boolean var6 = false;

      while(true) {
         while(true) {
            byte var7 = var3.readByte();
            if (var7 == 58 && var5 == null) {
               var5 = var2.toString();
               var6 = true;
               var2.reset();
            } else if (var7 != 13) {
               if (var7 == 10) {
                  if (var5 == null && var4 == 0) {
                     return false;
                  }

                  if (var6) {
                     var1.add(var5, var2.toString());
                  } else if (this.validateHeaders) {
                     this.invalidHeader(var5, var2.toString());
                  }

                  return true;
               }

               if (var4 >= this.maxLineLength) {
                  this.invalidLineLength();
               }

               if (var7 == 58 && var5 != null) {
                  var6 = false;
               }

               ++var4;
               var2.append((char)var7);
            }
         }
      }
   }

   private void invalidHeader(String var1, String var2) {
      String var3 = var1 != null ? var1 + ":" + var2 : var2;
      throw new IllegalArgumentException("a header value or name contains a prohibited character ':', " + var3);
   }

   private void invalidLineLength() {
      throw new TooLongFrameException("An STOMP line is larger than " + this.maxLineLength + " bytes.");
   }

   private void resetDecoder() {
      this.checkpoint(StompSubframeDecoder.State.SKIP_CONTROL_CHARACTERS);
      this.contentLength = -1L;
      this.alreadyReadChunkSize = 0;
      this.lastContent = null;
   }

   static enum State {
      SKIP_CONTROL_CHARACTERS,
      READ_HEADERS,
      READ_CONTENT,
      FINALIZE_FRAME_READ,
      BAD_FRAME,
      INVALID_CHUNK;

      private State() {
      }
   }
}
