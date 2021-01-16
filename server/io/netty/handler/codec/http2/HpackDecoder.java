package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;

final class HpackDecoder {
   private static final Http2Exception DECODE_ULE_128_DECOMPRESSION_EXCEPTION;
   private static final Http2Exception DECODE_ULE_128_TO_LONG_DECOMPRESSION_EXCEPTION;
   private static final Http2Exception DECODE_ULE_128_TO_INT_DECOMPRESSION_EXCEPTION;
   private static final Http2Exception DECODE_ILLEGAL_INDEX_VALUE;
   private static final Http2Exception INDEX_HEADER_ILLEGAL_INDEX_VALUE;
   private static final Http2Exception READ_NAME_ILLEGAL_INDEX_VALUE;
   private static final Http2Exception INVALID_MAX_DYNAMIC_TABLE_SIZE;
   private static final Http2Exception MAX_DYNAMIC_TABLE_SIZE_CHANGE_REQUIRED;
   private static final byte READ_HEADER_REPRESENTATION = 0;
   private static final byte READ_MAX_DYNAMIC_TABLE_SIZE = 1;
   private static final byte READ_INDEXED_HEADER = 2;
   private static final byte READ_INDEXED_HEADER_NAME = 3;
   private static final byte READ_LITERAL_HEADER_NAME_LENGTH_PREFIX = 4;
   private static final byte READ_LITERAL_HEADER_NAME_LENGTH = 5;
   private static final byte READ_LITERAL_HEADER_NAME = 6;
   private static final byte READ_LITERAL_HEADER_VALUE_LENGTH_PREFIX = 7;
   private static final byte READ_LITERAL_HEADER_VALUE_LENGTH = 8;
   private static final byte READ_LITERAL_HEADER_VALUE = 9;
   private final HpackDynamicTable hpackDynamicTable;
   private final HpackHuffmanDecoder hpackHuffmanDecoder;
   private long maxHeaderListSizeGoAway;
   private long maxHeaderListSize;
   private long maxDynamicTableSize;
   private long encoderMaxDynamicTableSize;
   private boolean maxDynamicTableSizeChangeRequired;

   HpackDecoder(long var1, int var3) {
      this(var1, var3, 4096);
   }

   HpackDecoder(long var1, int var3, int var4) {
      super();
      this.maxHeaderListSize = ObjectUtil.checkPositive(var1, "maxHeaderListSize");
      this.maxHeaderListSizeGoAway = Http2CodecUtil.calculateMaxHeaderListSizeGoAway(var1);
      this.maxDynamicTableSize = this.encoderMaxDynamicTableSize = (long)var4;
      this.maxDynamicTableSizeChangeRequired = false;
      this.hpackDynamicTable = new HpackDynamicTable((long)var4);
      this.hpackHuffmanDecoder = new HpackHuffmanDecoder(var3);
   }

   public void decode(int var1, ByteBuf var2, Http2Headers var3, boolean var4) throws Http2Exception {
      int var5 = 0;
      long var6 = 0L;
      int var8 = 0;
      int var9 = 0;
      byte var10 = 0;
      boolean var11 = false;
      CharSequence var12 = null;
      HpackDecoder.HeaderType var13 = null;
      HpackUtil.IndexType var14 = HpackUtil.IndexType.NONE;

      while(var2.isReadable()) {
         byte var15;
         HpackHeaderField var16;
         switch(var10) {
         case 0:
            var15 = var2.readByte();
            if (this.maxDynamicTableSizeChangeRequired && (var15 & 224) != 32) {
               throw MAX_DYNAMIC_TABLE_SIZE_CHANGE_REQUIRED;
            }

            if (var15 < 0) {
               var5 = var15 & 127;
               switch(var5) {
               case 0:
                  throw DECODE_ILLEGAL_INDEX_VALUE;
               case 127:
                  var10 = 2;
                  continue;
               default:
                  var16 = this.getIndexedHeader(var5);
                  var13 = this.validate(var16.name, var13, var4);
                  var6 = this.addHeader(var3, var16.name, var16.value, var6);
               }
            } else if ((var15 & 64) == 64) {
               var14 = HpackUtil.IndexType.INCREMENTAL;
               var5 = var15 & 63;
               switch(var5) {
               case 0:
                  var10 = 4;
                  continue;
               case 63:
                  var10 = 3;
                  continue;
               default:
                  var12 = this.readName(var5);
                  var13 = this.validate(var12, var13, var4);
                  var8 = var12.length();
                  var10 = 7;
               }
            } else if ((var15 & 32) == 32) {
               var5 = var15 & 31;
               if (var5 == 31) {
                  var10 = 1;
               } else {
                  this.setDynamicTableSize((long)var5);
                  var10 = 0;
               }
            } else {
               var14 = (var15 & 16) == 16 ? HpackUtil.IndexType.NEVER : HpackUtil.IndexType.NONE;
               var5 = var15 & 15;
               switch(var5) {
               case 0:
                  var10 = 4;
                  continue;
               case 15:
                  var10 = 3;
                  continue;
               default:
                  var12 = this.readName(var5);
                  var13 = this.validate(var12, var13, var4);
                  var8 = var12.length();
                  var10 = 7;
               }
            }
            break;
         case 1:
            this.setDynamicTableSize(decodeULE128(var2, (long)var5));
            var10 = 0;
            break;
         case 2:
            var16 = this.getIndexedHeader(decodeULE128(var2, var5));
            var13 = this.validate(var16.name, var13, var4);
            var6 = this.addHeader(var3, var16.name, var16.value, var6);
            var10 = 0;
            break;
         case 3:
            var12 = this.readName(decodeULE128(var2, var5));
            var13 = this.validate(var12, var13, var4);
            var8 = var12.length();
            var10 = 7;
            break;
         case 4:
            var15 = var2.readByte();
            var11 = (var15 & 128) == 128;
            var5 = var15 & 127;
            if (var5 == 127) {
               var10 = 5;
            } else {
               if ((long)var5 > this.maxHeaderListSizeGoAway - var6) {
                  Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
               }

               var8 = var5;
               var10 = 6;
            }
            break;
         case 5:
            var8 = decodeULE128(var2, var5);
            if ((long)var8 > this.maxHeaderListSizeGoAway - var6) {
               Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
            }

            var10 = 6;
            break;
         case 6:
            if (var2.readableBytes() < var8) {
               throw notEnoughDataException(var2);
            }

            var12 = this.readStringLiteral(var2, var8, var11);
            var13 = this.validate(var12, var13, var4);
            var10 = 7;
            break;
         case 7:
            var15 = var2.readByte();
            var11 = (var15 & 128) == 128;
            var5 = var15 & 127;
            switch(var5) {
            case 0:
               var13 = this.validate(var12, var13, var4);
               var6 = this.insertHeader(var3, var12, AsciiString.EMPTY_STRING, var14, var6);
               var10 = 0;
               continue;
            case 127:
               var10 = 8;
               continue;
            default:
               if ((long)var5 + (long)var8 > this.maxHeaderListSizeGoAway - var6) {
                  Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
               }

               var9 = var5;
               var10 = 9;
               continue;
            }
         case 8:
            var9 = decodeULE128(var2, var5);
            if ((long)var9 + (long)var8 > this.maxHeaderListSizeGoAway - var6) {
               Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
            }

            var10 = 9;
            break;
         case 9:
            if (var2.readableBytes() < var9) {
               throw notEnoughDataException(var2);
            }

            CharSequence var17 = this.readStringLiteral(var2, var9, var11);
            var13 = this.validate(var12, var13, var4);
            var6 = this.insertHeader(var3, var12, var17, var14, var6);
            var10 = 0;
            break;
         default:
            throw new Error("should not reach here state: " + var10);
         }
      }

      if (var6 > this.maxHeaderListSize) {
         Http2CodecUtil.headerListSizeExceeded(var1, this.maxHeaderListSize, true);
      }

      if (var10 != 0) {
         throw Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "Incomplete header block fragment.");
      }
   }

   public void setMaxHeaderTableSize(long var1) throws Http2Exception {
      if (var1 >= 0L && var1 <= 4294967295L) {
         this.maxDynamicTableSize = var1;
         if (this.maxDynamicTableSize < this.encoderMaxDynamicTableSize) {
            this.maxDynamicTableSizeChangeRequired = true;
            this.hpackDynamicTable.setCapacity(this.maxDynamicTableSize);
         }

      } else {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header Table Size must be >= %d and <= %d but was %d", 0L, 4294967295L, var1);
      }
   }

   public void setMaxHeaderListSize(long var1, long var3) throws Http2Exception {
      if (var3 >= var1 && var3 >= 0L) {
         if (var1 >= 0L && var1 <= 4294967295L) {
            this.maxHeaderListSize = var1;
            this.maxHeaderListSizeGoAway = var3;
         } else {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header List Size must be >= %d and <= %d but was %d", 0L, 4294967295L, var1);
         }
      } else {
         throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "Header List Size GO_AWAY %d must be positive and >= %d", var3, var1);
      }
   }

   public long getMaxHeaderListSize() {
      return this.maxHeaderListSize;
   }

   public long getMaxHeaderListSizeGoAway() {
      return this.maxHeaderListSizeGoAway;
   }

   public long getMaxHeaderTableSize() {
      return this.hpackDynamicTable.capacity();
   }

   int length() {
      return this.hpackDynamicTable.length();
   }

   long size() {
      return this.hpackDynamicTable.size();
   }

   HpackHeaderField getHeaderField(int var1) {
      return this.hpackDynamicTable.getEntry(var1 + 1);
   }

   private void setDynamicTableSize(long var1) throws Http2Exception {
      if (var1 > this.maxDynamicTableSize) {
         throw INVALID_MAX_DYNAMIC_TABLE_SIZE;
      } else {
         this.encoderMaxDynamicTableSize = var1;
         this.maxDynamicTableSizeChangeRequired = false;
         this.hpackDynamicTable.setCapacity(var1);
      }
   }

   private HpackDecoder.HeaderType validate(CharSequence var1, HpackDecoder.HeaderType var2, boolean var3) throws Http2Exception {
      if (!var3) {
         return null;
      } else if (Http2Headers.PseudoHeaderName.hasPseudoHeaderFormat(var1)) {
         if (var2 == HpackDecoder.HeaderType.REGULAR_HEADER) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Pseudo-header field '%s' found after regular header.", var1);
         } else {
            Http2Headers.PseudoHeaderName var4 = Http2Headers.PseudoHeaderName.getPseudoHeader(var1);
            if (var4 == null) {
               throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Invalid HTTP/2 pseudo-header '%s' encountered.", var1);
            } else {
               HpackDecoder.HeaderType var5 = var4.isRequestOnly() ? HpackDecoder.HeaderType.REQUEST_PSEUDO_HEADER : HpackDecoder.HeaderType.RESPONSE_PSEUDO_HEADER;
               if (var2 != null && var5 != var2) {
                  throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Mix of request and response pseudo-headers.");
               } else {
                  return var5;
               }
            }
         }
      } else {
         return HpackDecoder.HeaderType.REGULAR_HEADER;
      }
   }

   private CharSequence readName(int var1) throws Http2Exception {
      HpackHeaderField var2;
      if (var1 <= HpackStaticTable.length) {
         var2 = HpackStaticTable.getEntry(var1);
         return var2.name;
      } else if (var1 - HpackStaticTable.length <= this.hpackDynamicTable.length()) {
         var2 = this.hpackDynamicTable.getEntry(var1 - HpackStaticTable.length);
         return var2.name;
      } else {
         throw READ_NAME_ILLEGAL_INDEX_VALUE;
      }
   }

   private HpackHeaderField getIndexedHeader(int var1) throws Http2Exception {
      if (var1 <= HpackStaticTable.length) {
         return HpackStaticTable.getEntry(var1);
      } else if (var1 - HpackStaticTable.length <= this.hpackDynamicTable.length()) {
         return this.hpackDynamicTable.getEntry(var1 - HpackStaticTable.length);
      } else {
         throw INDEX_HEADER_ILLEGAL_INDEX_VALUE;
      }
   }

   private long insertHeader(Http2Headers var1, CharSequence var2, CharSequence var3, HpackUtil.IndexType var4, long var5) throws Http2Exception {
      var5 = this.addHeader(var1, var2, var3, var5);
      switch(var4) {
      case INCREMENTAL:
         this.hpackDynamicTable.add(new HpackHeaderField(var2, var3));
      case NONE:
      case NEVER:
         return var5;
      default:
         throw new Error("should not reach here");
      }
   }

   private long addHeader(Http2Headers var1, CharSequence var2, CharSequence var3, long var4) throws Http2Exception {
      var4 += HpackHeaderField.sizeOf(var2, var3);
      if (var4 > this.maxHeaderListSizeGoAway) {
         Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
      }

      var1.add(var2, var3);
      return var4;
   }

   private CharSequence readStringLiteral(ByteBuf var1, int var2, boolean var3) throws Http2Exception {
      if (var3) {
         return this.hpackHuffmanDecoder.decode(var1, var2);
      } else {
         byte[] var4 = new byte[var2];
         var1.readBytes(var4);
         return new AsciiString(var4, false);
      }
   }

   private static IllegalArgumentException notEnoughDataException(ByteBuf var0) {
      return new IllegalArgumentException("decode only works with an entire header block! " + var0);
   }

   static int decodeULE128(ByteBuf var0, int var1) throws Http2Exception {
      int var2 = var0.readerIndex();
      long var3 = decodeULE128(var0, (long)var1);
      if (var3 > 2147483647L) {
         var0.readerIndex(var2);
         throw DECODE_ULE_128_TO_INT_DECOMPRESSION_EXCEPTION;
      } else {
         return (int)var3;
      }
   }

   static long decodeULE128(ByteBuf var0, long var1) throws Http2Exception {
      assert var1 <= 127L && var1 >= 0L;

      boolean var3 = var1 == 0L;
      int var4 = var0.writerIndex();
      int var5 = var0.readerIndex();

      for(int var6 = 0; var5 < var4; var6 += 7) {
         byte var7 = var0.getByte(var5);
         if (var6 == 56 && ((var7 & 128) != 0 || var7 == 127 && !var3)) {
            throw DECODE_ULE_128_TO_LONG_DECOMPRESSION_EXCEPTION;
         }

         if ((var7 & 128) == 0) {
            var0.readerIndex(var5 + 1);
            return var1 + (((long)var7 & 127L) << var6);
         }

         var1 += ((long)var7 & 127L) << var6;
         ++var5;
      }

      throw DECODE_ULE_128_DECOMPRESSION_EXCEPTION;
   }

   static {
      DECODE_ULE_128_DECOMPRESSION_EXCEPTION = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - decompression failure"), HpackDecoder.class, "decodeULE128(..)");
      DECODE_ULE_128_TO_LONG_DECOMPRESSION_EXCEPTION = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - long overflow"), HpackDecoder.class, "decodeULE128(..)");
      DECODE_ULE_128_TO_INT_DECOMPRESSION_EXCEPTION = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - int overflow"), HpackDecoder.class, "decodeULE128ToInt(..)");
      DECODE_ILLEGAL_INDEX_VALUE = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value"), HpackDecoder.class, "decode(..)");
      INDEX_HEADER_ILLEGAL_INDEX_VALUE = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value"), HpackDecoder.class, "indexHeader(..)");
      READ_NAME_ILLEGAL_INDEX_VALUE = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value"), HpackDecoder.class, "readName(..)");
      INVALID_MAX_DYNAMIC_TABLE_SIZE = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - invalid max dynamic table size"), HpackDecoder.class, "setDynamicTableSize(..)");
      MAX_DYNAMIC_TABLE_SIZE_CHANGE_REQUIRED = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - max dynamic table size change required"), HpackDecoder.class, "decode(..)");
   }

   private static enum HeaderType {
      REGULAR_HEADER,
      REQUEST_PSEUDO_HEADER,
      RESPONSE_PSEUDO_HEADER;

      private HeaderType() {
      }
   }
}
