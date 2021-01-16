package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;

final class HpackHuffmanDecoder {
   private static final Http2Exception EOS_DECODED;
   private static final Http2Exception INVALID_PADDING;
   private static final HpackHuffmanDecoder.Node ROOT;
   private final HpackHuffmanDecoder.DecoderProcessor processor;

   HpackHuffmanDecoder(int var1) {
      super();
      this.processor = new HpackHuffmanDecoder.DecoderProcessor(var1);
   }

   public AsciiString decode(ByteBuf var1, int var2) throws Http2Exception {
      this.processor.reset();
      var1.forEachByte(var1.readerIndex(), var2, this.processor);
      var1.skipBytes(var2);
      return this.processor.end();
   }

   private static HpackHuffmanDecoder.Node buildTree(int[] var0, byte[] var1) {
      HpackHuffmanDecoder.Node var2 = new HpackHuffmanDecoder.Node();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         insert(var2, var3, var0[var3], var1[var3]);
      }

      return var2;
   }

   private static void insert(HpackHuffmanDecoder.Node var0, int var1, int var2, byte var3) {
      HpackHuffmanDecoder.Node var4;
      int var5;
      for(var4 = var0; var3 > 8; var4 = var4.children[var5]) {
         if (var4.isTerminal()) {
            throw new IllegalStateException("invalid Huffman code: prefix not unique");
         }

         var3 = (byte)(var3 - 8);
         var5 = var2 >>> var3 & 255;
         if (var4.children[var5] == null) {
            var4.children[var5] = new HpackHuffmanDecoder.Node();
         }
      }

      HpackHuffmanDecoder.Node var10 = new HpackHuffmanDecoder.Node(var1, var3);
      int var6 = 8 - var3;
      int var7 = var2 << var6 & 255;
      int var8 = 1 << var6;

      for(int var9 = var7; var9 < var7 + var8; ++var9) {
         var4.children[var9] = var10;
      }

   }

   static {
      EOS_DECODED = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - EOS Decoded"), HpackHuffmanDecoder.class, "decode(..)");
      INVALID_PADDING = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - Invalid Padding"), HpackHuffmanDecoder.class, "decode(..)");
      ROOT = buildTree(HpackUtil.HUFFMAN_CODES, HpackUtil.HUFFMAN_CODE_LENGTHS);
   }

   private static final class DecoderProcessor implements ByteProcessor {
      private final int initialCapacity;
      private byte[] bytes;
      private int index;
      private HpackHuffmanDecoder.Node node;
      private int current;
      private int currentBits;
      private int symbolBits;

      DecoderProcessor(int var1) {
         super();
         this.initialCapacity = ObjectUtil.checkPositive(var1, "initialCapacity");
      }

      void reset() {
         this.node = HpackHuffmanDecoder.ROOT;
         this.current = 0;
         this.currentBits = 0;
         this.symbolBits = 0;
         this.bytes = new byte[this.initialCapacity];
         this.index = 0;
      }

      public boolean process(byte var1) throws Http2Exception {
         this.current = this.current << 8 | var1 & 255;
         this.currentBits += 8;
         this.symbolBits += 8;

         do {
            this.node = this.node.children[this.current >>> this.currentBits - 8 & 255];
            this.currentBits -= this.node.bits;
            if (this.node.isTerminal()) {
               if (this.node.symbol == 256) {
                  throw HpackHuffmanDecoder.EOS_DECODED;
               }

               this.append(this.node.symbol);
               this.node = HpackHuffmanDecoder.ROOT;
               this.symbolBits = this.currentBits;
            }
         } while(this.currentBits >= 8);

         return true;
      }

      AsciiString end() throws Http2Exception {
         while(true) {
            if (this.currentBits > 0) {
               this.node = this.node.children[this.current << 8 - this.currentBits & 255];
               if (this.node.isTerminal() && this.node.bits <= this.currentBits) {
                  if (this.node.symbol == 256) {
                     throw HpackHuffmanDecoder.EOS_DECODED;
                  }

                  this.currentBits -= this.node.bits;
                  this.append(this.node.symbol);
                  this.node = HpackHuffmanDecoder.ROOT;
                  this.symbolBits = this.currentBits;
                  continue;
               }
            }

            int var1 = (1 << this.symbolBits) - 1;
            if (this.symbolBits <= 7 && (this.current & var1) == var1) {
               return new AsciiString(this.bytes, 0, this.index, false);
            }

            throw HpackHuffmanDecoder.INVALID_PADDING;
         }
      }

      private void append(int var1) {
         if (this.bytes.length == this.index) {
            int var2 = this.bytes.length >= 1024 ? this.bytes.length + this.initialCapacity : this.bytes.length << 1;
            byte[] var3 = new byte[var2];
            System.arraycopy(this.bytes, 0, var3, 0, this.bytes.length);
            this.bytes = var3;
         }

         this.bytes[this.index++] = (byte)var1;
      }
   }

   private static final class Node {
      private final int symbol;
      private final int bits;
      private final HpackHuffmanDecoder.Node[] children;

      Node() {
         super();
         this.symbol = 0;
         this.bits = 8;
         this.children = new HpackHuffmanDecoder.Node[256];
      }

      Node(int var1, int var2) {
         super();

         assert var2 > 0 && var2 <= 8;

         this.symbol = var1;
         this.bits = var2;
         this.children = null;
      }

      private boolean isTerminal() {
         return this.children == null;
      }
   }
}
