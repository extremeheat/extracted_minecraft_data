package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class Bzip2Decoder extends ByteToMessageDecoder {
   private Bzip2Decoder.State currentState;
   private final Bzip2BitReader reader;
   private Bzip2BlockDecompressor blockDecompressor;
   private Bzip2HuffmanStageDecoder huffmanStageDecoder;
   private int blockSize;
   private int blockCRC;
   private int streamCRC;

   public Bzip2Decoder() {
      super();
      this.currentState = Bzip2Decoder.State.INIT;
      this.reader = new Bzip2BitReader();
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.isReadable()) {
         Bzip2BitReader var4 = this.reader;
         var4.setByteBuf(var2);

         while(true) {
            Bzip2BlockDecompressor var11;
            label538: {
               int var16;
               int var17;
               Bzip2HuffmanStageDecoder var19;
               label539: {
                  int var18;
                  label525:
                  while(true) {
                     switch(this.currentState) {
                     case INIT:
                        if (var2.readableBytes() < 4) {
                           return;
                        }

                        int var5 = var2.readUnsignedMedium();
                        if (var5 != 4348520) {
                           throw new DecompressionException("Unexpected stream identifier contents. Mismatched bzip2 protocol version?");
                        }

                        int var6 = var2.readByte() - 48;
                        if (var6 < 1 || var6 > 9) {
                           throw new DecompressionException("block size is invalid");
                        }

                        this.blockSize = var6 * 100000;
                        this.streamCRC = 0;
                        this.currentState = Bzip2Decoder.State.INIT_BLOCK;
                     case INIT_BLOCK:
                        if (!var4.hasReadableBytes(10)) {
                           return;
                        }

                        int var7 = var4.readBits(24);
                        int var8 = var4.readBits(24);
                        if (var7 == 1536581 && var8 == 3690640) {
                           int var39 = var4.readInt();
                           if (var39 != this.streamCRC) {
                              throw new DecompressionException("stream CRC error");
                           }

                           this.currentState = Bzip2Decoder.State.EOF;
                           break;
                        } else {
                           if (var7 != 3227993 || var8 != 2511705) {
                              throw new DecompressionException("bad block header");
                           }

                           this.blockCRC = var4.readInt();
                           this.currentState = Bzip2Decoder.State.INIT_BLOCK_PARAMS;
                        }
                     case INIT_BLOCK_PARAMS:
                        if (!var4.hasReadableBits(25)) {
                           return;
                        }

                        boolean var9 = var4.readBoolean();
                        int var10 = var4.readBits(24);
                        this.blockDecompressor = new Bzip2BlockDecompressor(this.blockSize, this.blockCRC, var9, var10, var4);
                        this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_USED_MAP;
                     case RECEIVE_HUFFMAN_USED_MAP:
                        if (!var4.hasReadableBits(16)) {
                           return;
                        }

                        this.blockDecompressor.huffmanInUse16 = var4.readBits(16);
                        this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_USED_BITMAPS;
                     case RECEIVE_HUFFMAN_USED_BITMAPS:
                        var11 = this.blockDecompressor;
                        int var12 = var11.huffmanInUse16;
                        int var13 = Integer.bitCount(var12);
                        byte[] var14 = var11.huffmanSymbolMap;
                        if (!var4.hasReadableBits(var13 * 16 + 3)) {
                           return;
                        }

                        int var15 = 0;
                        if (var13 > 0) {
                           for(var16 = 0; var16 < 16; ++var16) {
                              if ((var12 & '\u8000' >>> var16) != 0) {
                                 var17 = 0;

                                 for(var18 = var16 << 4; var17 < 16; ++var18) {
                                    if (var4.readBoolean()) {
                                       var14[var15++] = (byte)var18;
                                    }

                                    ++var17;
                                 }
                              }
                           }
                        }

                        var11.huffmanEndOfBlockSymbol = var15 + 1;
                        var16 = var4.readBits(3);
                        if (var16 < 2 || var16 > 6) {
                           throw new DecompressionException("incorrect huffman groups number");
                        }

                        var17 = var15 + 2;
                        if (var17 > 258) {
                           throw new DecompressionException("incorrect alphabet size");
                        }

                        this.huffmanStageDecoder = new Bzip2HuffmanStageDecoder(var4, var16, var17);
                        this.currentState = Bzip2Decoder.State.RECEIVE_SELECTORS_NUMBER;
                     case RECEIVE_SELECTORS_NUMBER:
                        if (!var4.hasReadableBits(15)) {
                           return;
                        }

                        var18 = var4.readBits(15);
                        if (var18 < 1 || var18 > 18002) {
                           throw new DecompressionException("incorrect selectors number");
                        }

                        this.huffmanStageDecoder.selectors = new byte[var18];
                        this.currentState = Bzip2Decoder.State.RECEIVE_SELECTORS;
                     case RECEIVE_SELECTORS:
                        break label525;
                     case RECEIVE_HUFFMAN_LENGTH:
                        break label539;
                     case DECODE_HUFFMAN_DATA:
                        break label538;
                     case EOF:
                        var2.skipBytes(var2.readableBytes());
                        return;
                     default:
                        throw new IllegalStateException();
                     }
                  }

                  var19 = this.huffmanStageDecoder;
                  byte[] var20 = var19.selectors;
                  var18 = var20.length;
                  Bzip2MoveToFrontTable var21 = var19.tableMTF;

                  for(int var22 = var19.currentSelector; var22 < var18; ++var22) {
                     if (!var4.hasReadableBits(6)) {
                        var19.currentSelector = var22;
                        return;
                     }

                     int var23;
                     for(var23 = 0; var4.readBoolean(); ++var23) {
                     }

                     var20[var22] = var21.indexToFront(var23);
                  }

                  this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_LENGTH;
               }

               var19 = this.huffmanStageDecoder;
               var16 = var19.totalTables;
               byte[][] var40 = var19.tableCodeLengths;
               var17 = var19.alphabetSize;
               int var25 = var19.currentLength;
               int var26 = 0;
               boolean var27 = var19.modifyLength;
               boolean var28 = false;

               int var24;
               label453:
               for(var24 = var19.currentGroup; var24 < var16; ++var24) {
                  if (!var4.hasReadableBits(5)) {
                     var28 = true;
                     break;
                  }

                  if (var25 < 0) {
                     var25 = var4.readBits(5);
                  }

                  for(var26 = var19.currentAlpha; var26 < var17; ++var26) {
                     if (!var4.isReadable()) {
                        var28 = true;
                        break label453;
                     }

                     while(var27 || var4.readBoolean()) {
                        if (!var4.isReadable()) {
                           var27 = true;
                           var28 = true;
                           break label453;
                        }

                        var25 += var4.readBoolean() ? -1 : 1;
                        var27 = false;
                        if (!var4.isReadable()) {
                           var28 = true;
                           break label453;
                        }
                     }

                     var40[var24][var26] = (byte)var25;
                  }

                  var25 = -1;
                  var26 = var19.currentAlpha = 0;
                  var27 = false;
               }

               if (var28) {
                  var19.currentGroup = var24;
                  var19.currentLength = var25;
                  var19.currentAlpha = var26;
                  var19.modifyLength = var27;
                  return;
               }

               var19.createHuffmanDecodingTables();
               this.currentState = Bzip2Decoder.State.DECODE_HUFFMAN_DATA;
            }

            var11 = this.blockDecompressor;
            int var29 = var2.readerIndex();
            boolean var30 = var11.decodeHuffmanData(this.huffmanStageDecoder);
            if (!var30) {
               return;
            }

            if (var2.readerIndex() == var29 && var2.isReadable()) {
               var4.refill();
            }

            int var31 = var11.blockLength();
            ByteBuf var32 = var1.alloc().buffer(var31);
            boolean var33 = false;

            try {
               int var34;
               while((var34 = var11.read()) >= 0) {
                  var32.writeByte(var34);
               }

               int var35 = var11.checkCRC();
               this.streamCRC = (this.streamCRC << 1 | this.streamCRC >>> 31) ^ var35;
               var3.add(var32);
               var33 = true;
            } finally {
               if (!var33) {
                  var32.release();
               }

            }

            this.currentState = Bzip2Decoder.State.INIT_BLOCK;
         }
      }
   }

   public boolean isClosed() {
      return this.currentState == Bzip2Decoder.State.EOF;
   }

   private static enum State {
      INIT,
      INIT_BLOCK,
      INIT_BLOCK_PARAMS,
      RECEIVE_HUFFMAN_USED_MAP,
      RECEIVE_HUFFMAN_USED_BITMAPS,
      RECEIVE_SELECTORS_NUMBER,
      RECEIVE_SELECTORS,
      RECEIVE_HUFFMAN_LENGTH,
      DECODE_HUFFMAN_DATA,
      EOF;

      private State() {
      }
   }
}
