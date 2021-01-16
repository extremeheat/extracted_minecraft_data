package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

final class HpackHuffmanEncoder {
   private final int[] codes;
   private final byte[] lengths;
   private final HpackHuffmanEncoder.EncodedLengthProcessor encodedLengthProcessor;
   private final HpackHuffmanEncoder.EncodeProcessor encodeProcessor;

   HpackHuffmanEncoder() {
      this(HpackUtil.HUFFMAN_CODES, HpackUtil.HUFFMAN_CODE_LENGTHS);
   }

   private HpackHuffmanEncoder(int[] var1, byte[] var2) {
      super();
      this.encodedLengthProcessor = new HpackHuffmanEncoder.EncodedLengthProcessor();
      this.encodeProcessor = new HpackHuffmanEncoder.EncodeProcessor();
      this.codes = var1;
      this.lengths = var2;
   }

   public void encode(ByteBuf var1, CharSequence var2) {
      ObjectUtil.checkNotNull(var1, "out");
      if (var2 instanceof AsciiString) {
         AsciiString var3 = (AsciiString)var2;

         try {
            this.encodeProcessor.out = var1;
            var3.forEachByte(this.encodeProcessor);
         } catch (Exception var8) {
            PlatformDependent.throwException(var8);
         } finally {
            this.encodeProcessor.end();
         }
      } else {
         this.encodeSlowPath(var1, var2);
      }

   }

   private void encodeSlowPath(ByteBuf var1, CharSequence var2) {
      long var3 = 0L;
      int var5 = 0;

      for(int var6 = 0; var6 < var2.length(); ++var6) {
         int var7 = var2.charAt(var6) & 255;
         int var8 = this.codes[var7];
         byte var9 = this.lengths[var7];
         var3 <<= var9;
         var3 |= (long)var8;
         var5 += var9;

         while(var5 >= 8) {
            var5 -= 8;
            var1.writeByte((int)(var3 >> var5));
         }
      }

      if (var5 > 0) {
         var3 <<= 8 - var5;
         var3 |= (long)(255 >>> var5);
         var1.writeByte((int)var3);
      }

   }

   int getEncodedLength(CharSequence var1) {
      if (var1 instanceof AsciiString) {
         AsciiString var2 = (AsciiString)var1;

         try {
            this.encodedLengthProcessor.reset();
            var2.forEachByte(this.encodedLengthProcessor);
            return this.encodedLengthProcessor.length();
         } catch (Exception var4) {
            PlatformDependent.throwException(var4);
            return -1;
         }
      } else {
         return this.getEncodedLengthSlowPath(var1);
      }
   }

   private int getEncodedLengthSlowPath(CharSequence var1) {
      long var2 = 0L;

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         var2 += (long)this.lengths[var1.charAt(var4) & 255];
      }

      return (int)(var2 + 7L >> 3);
   }

   private final class EncodedLengthProcessor implements ByteProcessor {
      private long len;

      private EncodedLengthProcessor() {
         super();
      }

      public boolean process(byte var1) {
         this.len += (long)HpackHuffmanEncoder.this.lengths[var1 & 255];
         return true;
      }

      void reset() {
         this.len = 0L;
      }

      int length() {
         return (int)(this.len + 7L >> 3);
      }

      // $FF: synthetic method
      EncodedLengthProcessor(Object var2) {
         this();
      }
   }

   private final class EncodeProcessor implements ByteProcessor {
      ByteBuf out;
      private long current;
      private int n;

      private EncodeProcessor() {
         super();
      }

      public boolean process(byte var1) {
         int var2 = var1 & 255;
         byte var3 = HpackHuffmanEncoder.this.lengths[var2];
         this.current <<= var3;
         this.current |= (long)HpackHuffmanEncoder.this.codes[var2];
         this.n += var3;

         while(this.n >= 8) {
            this.n -= 8;
            this.out.writeByte((int)(this.current >> this.n));
         }

         return true;
      }

      void end() {
         try {
            if (this.n > 0) {
               this.current <<= 8 - this.n;
               this.current |= (long)(255 >>> this.n);
               this.out.writeByte((int)this.current);
            }
         } finally {
            this.out = null;
            this.current = 0L;
            this.n = 0;
         }

      }

      // $FF: synthetic method
      EncodeProcessor(Object var2) {
         this();
      }
   }
}
