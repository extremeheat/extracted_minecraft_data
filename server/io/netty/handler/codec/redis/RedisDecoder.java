package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import java.util.List;

public final class RedisDecoder extends ByteToMessageDecoder {
   private final RedisDecoder.ToPositiveLongProcessor toPositiveLongProcessor;
   private final boolean decodeInlineCommands;
   private final int maxInlineMessageLength;
   private final RedisMessagePool messagePool;
   private RedisDecoder.State state;
   private RedisMessageType type;
   private int remainingBulkLength;

   public RedisDecoder() {
      this(false);
   }

   public RedisDecoder(boolean var1) {
      this(65536, FixedRedisMessagePool.INSTANCE, var1);
   }

   public RedisDecoder(int var1, RedisMessagePool var2) {
      this(var1, var2, false);
   }

   public RedisDecoder(int var1, RedisMessagePool var2, boolean var3) {
      super();
      this.toPositiveLongProcessor = new RedisDecoder.ToPositiveLongProcessor();
      this.state = RedisDecoder.State.DECODE_TYPE;
      if (var1 > 0 && var1 <= 536870912) {
         this.maxInlineMessageLength = var1;
         this.messagePool = var2;
         this.decodeInlineCommands = var3;
      } else {
         throw new RedisCodecException("maxInlineMessageLength: " + var1 + " (expected: <= " + 536870912 + ")");
      }
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         while(true) {
            switch(this.state) {
            case DECODE_TYPE:
               if (this.decodeType(var2)) {
                  break;
               }

               return;
            case DECODE_INLINE:
               if (this.decodeInline(var2, var3)) {
                  break;
               }

               return;
            case DECODE_LENGTH:
               if (this.decodeLength(var2, var3)) {
                  break;
               }

               return;
            case DECODE_BULK_STRING_EOL:
               if (this.decodeBulkStringEndOfLine(var2, var3)) {
                  break;
               }

               return;
            case DECODE_BULK_STRING_CONTENT:
               if (this.decodeBulkStringContent(var2, var3)) {
                  break;
               }

               return;
            default:
               throw new RedisCodecException("Unknown state: " + this.state);
            }
         }
      } catch (RedisCodecException var5) {
         this.resetDecoder();
         throw var5;
      } catch (Exception var6) {
         this.resetDecoder();
         throw new RedisCodecException(var6);
      }
   }

   private void resetDecoder() {
      this.state = RedisDecoder.State.DECODE_TYPE;
      this.remainingBulkLength = 0;
   }

   private boolean decodeType(ByteBuf var1) throws Exception {
      if (!var1.isReadable()) {
         return false;
      } else {
         this.type = RedisMessageType.readFrom(var1, this.decodeInlineCommands);
         this.state = this.type.isInline() ? RedisDecoder.State.DECODE_INLINE : RedisDecoder.State.DECODE_LENGTH;
         return true;
      }
   }

   private boolean decodeInline(ByteBuf var1, List<Object> var2) throws Exception {
      ByteBuf var3 = readLine(var1);
      if (var3 == null) {
         if (var1.readableBytes() > this.maxInlineMessageLength) {
            throw new RedisCodecException("length: " + var1.readableBytes() + " (expected: <= " + this.maxInlineMessageLength + ")");
         } else {
            return false;
         }
      } else {
         var2.add(this.newInlineRedisMessage(this.type, var3));
         this.resetDecoder();
         return true;
      }
   }

   private boolean decodeLength(ByteBuf var1, List<Object> var2) throws Exception {
      ByteBuf var3 = readLine(var1);
      if (var3 == null) {
         return false;
      } else {
         long var4 = this.parseRedisNumber(var3);
         if (var4 < -1L) {
            throw new RedisCodecException("length: " + var4 + " (expected: >= " + -1 + ")");
         } else {
            switch(this.type) {
            case ARRAY_HEADER:
               var2.add(new ArrayHeaderRedisMessage(var4));
               this.resetDecoder();
               return true;
            case BULK_STRING:
               if (var4 > 536870912L) {
                  throw new RedisCodecException("length: " + var4 + " (expected: <= " + 536870912 + ")");
               }

               this.remainingBulkLength = (int)var4;
               return this.decodeBulkString(var1, var2);
            default:
               throw new RedisCodecException("bad type: " + this.type);
            }
         }
      }
   }

   private boolean decodeBulkString(ByteBuf var1, List<Object> var2) throws Exception {
      switch(this.remainingBulkLength) {
      case -1:
         var2.add(FullBulkStringRedisMessage.NULL_INSTANCE);
         this.resetDecoder();
         return true;
      case 0:
         this.state = RedisDecoder.State.DECODE_BULK_STRING_EOL;
         return this.decodeBulkStringEndOfLine(var1, var2);
      default:
         var2.add(new BulkStringHeaderRedisMessage(this.remainingBulkLength));
         this.state = RedisDecoder.State.DECODE_BULK_STRING_CONTENT;
         return this.decodeBulkStringContent(var1, var2);
      }
   }

   private boolean decodeBulkStringEndOfLine(ByteBuf var1, List<Object> var2) throws Exception {
      if (var1.readableBytes() < 2) {
         return false;
      } else {
         readEndOfLine(var1);
         var2.add(FullBulkStringRedisMessage.EMPTY_INSTANCE);
         this.resetDecoder();
         return true;
      }
   }

   private boolean decodeBulkStringContent(ByteBuf var1, List<Object> var2) throws Exception {
      int var3 = var1.readableBytes();
      if (var3 != 0 && (this.remainingBulkLength != 0 || var3 >= 2)) {
         if (var3 >= this.remainingBulkLength + 2) {
            ByteBuf var5 = var1.readSlice(this.remainingBulkLength);
            readEndOfLine(var1);
            var2.add(new DefaultLastBulkStringRedisContent(var5.retain()));
            this.resetDecoder();
            return true;
         } else {
            int var4 = Math.min(this.remainingBulkLength, var3);
            this.remainingBulkLength -= var4;
            var2.add(new DefaultBulkStringRedisContent(var1.readSlice(var4).retain()));
            return true;
         }
      } else {
         return false;
      }
   }

   private static void readEndOfLine(ByteBuf var0) {
      short var1 = var0.readShort();
      if (RedisConstants.EOL_SHORT != var1) {
         byte[] var2 = RedisCodecUtil.shortToBytes(var1);
         throw new RedisCodecException("delimiter: [" + var2[0] + "," + var2[1] + "] (expected: \\r\\n)");
      }
   }

   private RedisMessage newInlineRedisMessage(RedisMessageType var1, ByteBuf var2) {
      switch(var1) {
      case INLINE_COMMAND:
         return new InlineCommandRedisMessage(var2.toString(CharsetUtil.UTF_8));
      case SIMPLE_STRING:
         SimpleStringRedisMessage var5 = this.messagePool.getSimpleString(var2);
         return var5 != null ? var5 : new SimpleStringRedisMessage(var2.toString(CharsetUtil.UTF_8));
      case ERROR:
         ErrorRedisMessage var4 = this.messagePool.getError(var2);
         return var4 != null ? var4 : new ErrorRedisMessage(var2.toString(CharsetUtil.UTF_8));
      case INTEGER:
         IntegerRedisMessage var3 = this.messagePool.getInteger(var2);
         return var3 != null ? var3 : new IntegerRedisMessage(this.parseRedisNumber(var2));
      default:
         throw new RedisCodecException("bad type: " + var1);
      }
   }

   private static ByteBuf readLine(ByteBuf var0) {
      if (!var0.isReadable(2)) {
         return null;
      } else {
         int var1 = var0.forEachByte(ByteProcessor.FIND_LF);
         if (var1 < 0) {
            return null;
         } else {
            ByteBuf var2 = var0.readSlice(var1 - var0.readerIndex() - 1);
            readEndOfLine(var0);
            return var2;
         }
      }
   }

   private long parseRedisNumber(ByteBuf var1) {
      int var2 = var1.readableBytes();
      boolean var3 = var2 > 0 && var1.getByte(var1.readerIndex()) == 45;
      int var4 = var3 ? 1 : 0;
      if (var2 <= var4) {
         throw new RedisCodecException("no number to parse: " + var1.toString(CharsetUtil.US_ASCII));
      } else if (var2 > 19 + var4) {
         throw new RedisCodecException("too many characters to be a valid RESP Integer: " + var1.toString(CharsetUtil.US_ASCII));
      } else {
         return var3 ? -this.parsePositiveNumber(var1.skipBytes(var4)) : this.parsePositiveNumber(var1);
      }
   }

   private long parsePositiveNumber(ByteBuf var1) {
      this.toPositiveLongProcessor.reset();
      var1.forEachByte(this.toPositiveLongProcessor);
      return this.toPositiveLongProcessor.content();
   }

   private static final class ToPositiveLongProcessor implements ByteProcessor {
      private long result;

      private ToPositiveLongProcessor() {
         super();
      }

      public boolean process(byte var1) throws Exception {
         if (var1 >= 48 && var1 <= 57) {
            this.result = this.result * 10L + (long)(var1 - 48);
            return true;
         } else {
            throw new RedisCodecException("bad byte in number: " + var1);
         }
      }

      public long content() {
         return this.result;
      }

      public void reset() {
         this.result = 0L;
      }

      // $FF: synthetic method
      ToPositiveLongProcessor(Object var1) {
         this();
      }
   }

   private static enum State {
      DECODE_TYPE,
      DECODE_INLINE,
      DECODE_LENGTH,
      DECODE_BULK_STRING_EOL,
      DECODE_BULK_STRING_CONTENT;

      private State() {
      }
   }
}
