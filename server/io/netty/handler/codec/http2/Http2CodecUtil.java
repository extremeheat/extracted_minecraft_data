package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.TimeUnit;

public final class Http2CodecUtil {
   public static final int CONNECTION_STREAM_ID = 0;
   public static final int HTTP_UPGRADE_STREAM_ID = 1;
   public static final CharSequence HTTP_UPGRADE_SETTINGS_HEADER = AsciiString.cached("HTTP2-Settings");
   public static final CharSequence HTTP_UPGRADE_PROTOCOL_NAME = "h2c";
   public static final CharSequence TLS_UPGRADE_PROTOCOL_NAME = "h2";
   public static final int PING_FRAME_PAYLOAD_LENGTH = 8;
   public static final short MAX_UNSIGNED_BYTE = 255;
   public static final int MAX_PADDING = 256;
   public static final long MAX_UNSIGNED_INT = 4294967295L;
   public static final int FRAME_HEADER_LENGTH = 9;
   public static final int SETTING_ENTRY_LENGTH = 6;
   public static final int PRIORITY_ENTRY_LENGTH = 5;
   public static final int INT_FIELD_LENGTH = 4;
   public static final short MAX_WEIGHT = 256;
   public static final short MIN_WEIGHT = 1;
   private static final ByteBuf CONNECTION_PREFACE;
   private static final ByteBuf EMPTY_PING;
   private static final int MAX_PADDING_LENGTH_LENGTH = 1;
   public static final int DATA_FRAME_HEADER_LENGTH = 10;
   public static final int HEADERS_FRAME_HEADER_LENGTH = 15;
   public static final int PRIORITY_FRAME_LENGTH = 14;
   public static final int RST_STREAM_FRAME_LENGTH = 13;
   public static final int PUSH_PROMISE_FRAME_HEADER_LENGTH = 14;
   public static final int GO_AWAY_FRAME_HEADER_LENGTH = 17;
   public static final int WINDOW_UPDATE_FRAME_LENGTH = 13;
   public static final int CONTINUATION_FRAME_HEADER_LENGTH = 10;
   public static final char SETTINGS_HEADER_TABLE_SIZE = '\u0001';
   public static final char SETTINGS_ENABLE_PUSH = '\u0002';
   public static final char SETTINGS_MAX_CONCURRENT_STREAMS = '\u0003';
   public static final char SETTINGS_INITIAL_WINDOW_SIZE = '\u0004';
   public static final char SETTINGS_MAX_FRAME_SIZE = '\u0005';
   public static final char SETTINGS_MAX_HEADER_LIST_SIZE = '\u0006';
   public static final int NUM_STANDARD_SETTINGS = 6;
   public static final long MAX_HEADER_TABLE_SIZE = 4294967295L;
   public static final long MAX_CONCURRENT_STREAMS = 4294967295L;
   public static final int MAX_INITIAL_WINDOW_SIZE = 2147483647;
   public static final int MAX_FRAME_SIZE_LOWER_BOUND = 16384;
   public static final int MAX_FRAME_SIZE_UPPER_BOUND = 16777215;
   public static final long MAX_HEADER_LIST_SIZE = 4294967295L;
   public static final long MIN_HEADER_TABLE_SIZE = 0L;
   public static final long MIN_CONCURRENT_STREAMS = 0L;
   public static final int MIN_INITIAL_WINDOW_SIZE = 0;
   public static final long MIN_HEADER_LIST_SIZE = 0L;
   public static final int DEFAULT_WINDOW_SIZE = 65535;
   public static final short DEFAULT_PRIORITY_WEIGHT = 16;
   public static final int DEFAULT_HEADER_TABLE_SIZE = 4096;
   public static final long DEFAULT_HEADER_LIST_SIZE = 8192L;
   public static final int DEFAULT_MAX_FRAME_SIZE = 16384;
   public static final int SMALLEST_MAX_CONCURRENT_STREAMS = 100;
   static final int DEFAULT_MAX_RESERVED_STREAMS = 100;
   static final int DEFAULT_MIN_ALLOCATION_CHUNK = 1024;
   static final int DEFAULT_INITIAL_HUFFMAN_DECODE_CAPACITY = 32;
   public static final long DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT_MILLIS;

   public static long calculateMaxHeaderListSizeGoAway(long var0) {
      return var0 + (var0 >>> 2);
   }

   public static boolean isOutboundStream(boolean var0, int var1) {
      boolean var2 = (var1 & 1) == 0;
      return var1 > 0 && var0 == var2;
   }

   public static boolean isStreamIdValid(int var0) {
      return var0 >= 0;
   }

   public static boolean isMaxFrameSizeValid(int var0) {
      return var0 >= 16384 && var0 <= 16777215;
   }

   public static ByteBuf connectionPrefaceBuf() {
      return CONNECTION_PREFACE.retainedDuplicate();
   }

   public static ByteBuf emptyPingBuf() {
      return EMPTY_PING.retainedDuplicate();
   }

   public static Http2Exception getEmbeddedHttp2Exception(Throwable var0) {
      while(var0 != null) {
         if (var0 instanceof Http2Exception) {
            return (Http2Exception)var0;
         }

         var0 = var0.getCause();
      }

      return null;
   }

   public static ByteBuf toByteBuf(ChannelHandlerContext var0, Throwable var1) {
      return var1 != null && var1.getMessage() != null ? ByteBufUtil.writeUtf8((ByteBufAllocator)var0.alloc(), var1.getMessage()) : Unpooled.EMPTY_BUFFER;
   }

   public static int readUnsignedInt(ByteBuf var0) {
      return var0.readInt() & 2147483647;
   }

   public static void writeFrameHeader(ByteBuf var0, int var1, byte var2, Http2Flags var3, int var4) {
      var0.ensureWritable(9 + var1);
      writeFrameHeaderInternal(var0, var1, var2, var3, var4);
   }

   public static int streamableBytes(StreamByteDistributor.StreamState var0) {
      return Math.max(0, (int)Math.min(var0.pendingBytes(), (long)var0.windowSize()));
   }

   public static void headerListSizeExceeded(int var0, long var1, boolean var3) throws Http2Exception {
      throw Http2Exception.headerListSizeError(var0, Http2Error.PROTOCOL_ERROR, var3, "Header size exceeded max allowed size (%d)", var1);
   }

   public static void headerListSizeExceeded(long var0) throws Http2Exception {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header size exceeded max allowed size (%d)", var0);
   }

   static void writeFrameHeaderInternal(ByteBuf var0, int var1, byte var2, Http2Flags var3, int var4) {
      var0.writeMedium(var1);
      var0.writeByte(var2);
      var0.writeByte(var3.value());
      var0.writeInt(var4);
   }

   public static void verifyPadding(int var0) {
      if (var0 < 0 || var0 > 256) {
         throw new IllegalArgumentException(String.format("Invalid padding '%d'. Padding must be between 0 and %d (inclusive).", var0, 256));
      }
   }

   private Http2CodecUtil() {
      super();
   }

   static {
      CONNECTION_PREFACE = Unpooled.unreleasableBuffer(Unpooled.directBuffer(24).writeBytes("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(CharsetUtil.UTF_8))).asReadOnly();
      EMPTY_PING = Unpooled.unreleasableBuffer(Unpooled.directBuffer(8).writeZero(8)).asReadOnly();
      DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT_MILLIS = TimeUnit.MILLISECONDS.convert(30L, TimeUnit.SECONDS);
   }

   static final class SimpleChannelPromiseAggregator extends DefaultChannelPromise {
      private final ChannelPromise promise;
      private int expectedCount;
      private int doneCount;
      private Throwable lastFailure;
      private boolean doneAllocating;

      SimpleChannelPromiseAggregator(ChannelPromise var1, Channel var2, EventExecutor var3) {
         super(var2, var3);

         assert var1 != null && !var1.isDone();

         this.promise = var1;
      }

      public ChannelPromise newPromise() {
         assert !this.doneAllocating : "Done allocating. No more promises can be allocated.";

         ++this.expectedCount;
         return this;
      }

      public ChannelPromise doneAllocatingPromises() {
         if (!this.doneAllocating) {
            this.doneAllocating = true;
            if (this.doneCount == this.expectedCount || this.expectedCount == 0) {
               return this.setPromise();
            }
         }

         return this;
      }

      public boolean tryFailure(Throwable var1) {
         if (this.allowFailure()) {
            ++this.doneCount;
            this.lastFailure = var1;
            return this.allPromisesDone() ? this.tryPromise() : true;
         } else {
            return false;
         }
      }

      public ChannelPromise setFailure(Throwable var1) {
         if (this.allowFailure()) {
            ++this.doneCount;
            this.lastFailure = var1;
            if (this.allPromisesDone()) {
               return this.setPromise();
            }
         }

         return this;
      }

      public ChannelPromise setSuccess(Void var1) {
         if (this.awaitingPromises()) {
            ++this.doneCount;
            if (this.allPromisesDone()) {
               this.setPromise();
            }
         }

         return this;
      }

      public boolean trySuccess(Void var1) {
         if (this.awaitingPromises()) {
            ++this.doneCount;
            return this.allPromisesDone() ? this.tryPromise() : true;
         } else {
            return false;
         }
      }

      private boolean allowFailure() {
         return this.awaitingPromises() || this.expectedCount == 0;
      }

      private boolean awaitingPromises() {
         return this.doneCount < this.expectedCount;
      }

      private boolean allPromisesDone() {
         return this.doneCount == this.expectedCount && this.doneAllocating;
      }

      private ChannelPromise setPromise() {
         if (this.lastFailure == null) {
            this.promise.setSuccess();
            return super.setSuccess((Void)null);
         } else {
            this.promise.setFailure(this.lastFailure);
            return super.setFailure(this.lastFailure);
         }
      }

      private boolean tryPromise() {
         if (this.lastFailure == null) {
            this.promise.trySuccess();
            return super.trySuccess((Object)null);
         } else {
            this.promise.tryFailure(this.lastFailure);
            return super.tryFailure(this.lastFailure);
         }
      }
   }
}
