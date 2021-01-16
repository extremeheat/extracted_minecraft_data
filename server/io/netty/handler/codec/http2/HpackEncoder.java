package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.MathUtil;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

final class HpackEncoder {
   private final HpackEncoder.HeaderEntry[] headerFields;
   private final HpackEncoder.HeaderEntry head;
   private final HpackHuffmanEncoder hpackHuffmanEncoder;
   private final byte hashMask;
   private final boolean ignoreMaxHeaderListSize;
   private long size;
   private long maxHeaderTableSize;
   private long maxHeaderListSize;

   HpackEncoder() {
      this(false);
   }

   public HpackEncoder(boolean var1) {
      this(var1, 16);
   }

   public HpackEncoder(boolean var1, int var2) {
      super();
      this.head = new HpackEncoder.HeaderEntry(-1, AsciiString.EMPTY_STRING, AsciiString.EMPTY_STRING, 2147483647, (HpackEncoder.HeaderEntry)null);
      this.hpackHuffmanEncoder = new HpackHuffmanEncoder();
      this.ignoreMaxHeaderListSize = var1;
      this.maxHeaderTableSize = 4096L;
      this.maxHeaderListSize = 4294967295L;
      this.headerFields = new HpackEncoder.HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(var2, 128)))];
      this.hashMask = (byte)(this.headerFields.length - 1);
      this.head.before = this.head.after = this.head;
   }

   public void encodeHeaders(int var1, ByteBuf var2, Http2Headers var3, Http2HeadersEncoder.SensitivityDetector var4) throws Http2Exception {
      if (this.ignoreMaxHeaderListSize) {
         this.encodeHeadersIgnoreMaxHeaderListSize(var2, var3, var4);
      } else {
         this.encodeHeadersEnforceMaxHeaderListSize(var1, var2, var3, var4);
      }

   }

   private void encodeHeadersEnforceMaxHeaderListSize(int var1, ByteBuf var2, Http2Headers var3, Http2HeadersEncoder.SensitivityDetector var4) throws Http2Exception {
      long var5 = 0L;
      Iterator var7 = var3.iterator();

      while(var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         CharSequence var9 = (CharSequence)var8.getKey();
         CharSequence var10 = (CharSequence)var8.getValue();
         var5 += HpackHeaderField.sizeOf(var9, var10);
         if (var5 > this.maxHeaderListSize) {
            Http2CodecUtil.headerListSizeExceeded(var1, this.maxHeaderListSize, false);
         }
      }

      this.encodeHeadersIgnoreMaxHeaderListSize(var2, var3, var4);
   }

   private void encodeHeadersIgnoreMaxHeaderListSize(ByteBuf var1, Http2Headers var2, Http2HeadersEncoder.SensitivityDetector var3) throws Http2Exception {
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         CharSequence var6 = (CharSequence)var5.getKey();
         CharSequence var7 = (CharSequence)var5.getValue();
         this.encodeHeader(var1, var6, var7, var3.isSensitive(var6, var7), HpackHeaderField.sizeOf(var6, var7));
      }

   }

   private void encodeHeader(ByteBuf var1, CharSequence var2, CharSequence var3, boolean var4, long var5) {
      int var9;
      if (var4) {
         var9 = this.getNameIndex(var2);
         this.encodeLiteral(var1, var2, var3, HpackUtil.IndexType.NEVER, var9);
      } else {
         int var8;
         if (this.maxHeaderTableSize == 0L) {
            var9 = HpackStaticTable.getIndex(var2, var3);
            if (var9 == -1) {
               var8 = HpackStaticTable.getIndex(var2);
               this.encodeLiteral(var1, var2, var3, HpackUtil.IndexType.NONE, var8);
            } else {
               encodeInteger(var1, 128, 7, var9);
            }

         } else if (var5 > this.maxHeaderTableSize) {
            var9 = this.getNameIndex(var2);
            this.encodeLiteral(var1, var2, var3, HpackUtil.IndexType.NONE, var9);
         } else {
            HpackEncoder.HeaderEntry var7 = this.getEntry(var2, var3);
            if (var7 != null) {
               var8 = this.getIndex(var7.index) + HpackStaticTable.length;
               encodeInteger(var1, 128, 7, var8);
            } else {
               var8 = HpackStaticTable.getIndex(var2, var3);
               if (var8 != -1) {
                  encodeInteger(var1, 128, 7, var8);
               } else {
                  this.ensureCapacity(var5);
                  this.encodeLiteral(var1, var2, var3, HpackUtil.IndexType.INCREMENTAL, this.getNameIndex(var2));
                  this.add(var2, var3, var5);
               }
            }

         }
      }
   }

   public void setMaxHeaderTableSize(ByteBuf var1, long var2) throws Http2Exception {
      if (var2 >= 0L && var2 <= 4294967295L) {
         if (this.maxHeaderTableSize != var2) {
            this.maxHeaderTableSize = var2;
            this.ensureCapacity(0L);
            encodeInteger(var1, 32, 5, var2);
         }
      } else {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header Table Size must be >= %d and <= %d but was %d", 0L, 4294967295L, var2);
      }
   }

   public long getMaxHeaderTableSize() {
      return this.maxHeaderTableSize;
   }

   public void setMaxHeaderListSize(long var1) throws Http2Exception {
      if (var1 >= 0L && var1 <= 4294967295L) {
         this.maxHeaderListSize = var1;
      } else {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header List Size must be >= %d and <= %d but was %d", 0L, 4294967295L, var1);
      }
   }

   public long getMaxHeaderListSize() {
      return this.maxHeaderListSize;
   }

   private static void encodeInteger(ByteBuf var0, int var1, int var2, int var3) {
      encodeInteger(var0, var1, var2, (long)var3);
   }

   private static void encodeInteger(ByteBuf var0, int var1, int var2, long var3) {
      assert var2 >= 0 && var2 <= 8 : "N: " + var2;

      int var5 = 255 >>> 8 - var2;
      if (var3 < (long)var5) {
         var0.writeByte((int)((long)var1 | var3));
      } else {
         var0.writeByte(var1 | var5);

         long var6;
         for(var6 = var3 - (long)var5; (var6 & -128L) != 0L; var6 >>>= 7) {
            var0.writeByte((int)(var6 & 127L | 128L));
         }

         var0.writeByte((int)var6);
      }

   }

   private void encodeStringLiteral(ByteBuf var1, CharSequence var2) {
      int var3 = this.hpackHuffmanEncoder.getEncodedLength(var2);
      if (var3 < var2.length()) {
         encodeInteger(var1, 128, 7, var3);
         this.hpackHuffmanEncoder.encode(var1, var2);
      } else {
         encodeInteger(var1, 0, 7, var2.length());
         if (var2 instanceof AsciiString) {
            AsciiString var4 = (AsciiString)var2;
            var1.writeBytes(var4.array(), var4.arrayOffset(), var4.length());
         } else {
            var1.writeCharSequence(var2, CharsetUtil.ISO_8859_1);
         }
      }

   }

   private void encodeLiteral(ByteBuf var1, CharSequence var2, CharSequence var3, HpackUtil.IndexType var4, int var5) {
      boolean var6 = var5 != -1;
      switch(var4) {
      case INCREMENTAL:
         encodeInteger(var1, 64, 6, var6 ? var5 : 0);
         break;
      case NONE:
         encodeInteger(var1, 0, 4, var6 ? var5 : 0);
         break;
      case NEVER:
         encodeInteger(var1, 16, 4, var6 ? var5 : 0);
         break;
      default:
         throw new Error("should not reach here");
      }

      if (!var6) {
         this.encodeStringLiteral(var1, var2);
      }

      this.encodeStringLiteral(var1, var3);
   }

   private int getNameIndex(CharSequence var1) {
      int var2 = HpackStaticTable.getIndex(var1);
      if (var2 == -1) {
         var2 = this.getIndex(var1);
         if (var2 >= 0) {
            var2 += HpackStaticTable.length;
         }
      }

      return var2;
   }

   private void ensureCapacity(long var1) {
      while(true) {
         if (this.maxHeaderTableSize - this.size < var1) {
            int var3 = this.length();
            if (var3 != 0) {
               this.remove();
               continue;
            }
         }

         return;
      }
   }

   int length() {
      return this.size == 0L ? 0 : this.head.after.index - this.head.before.index + 1;
   }

   long size() {
      return this.size;
   }

   HpackHeaderField getHeaderField(int var1) {
      HpackEncoder.HeaderEntry var2;
      for(var2 = this.head; var1-- >= 0; var2 = var2.before) {
      }

      return var2;
   }

   private HpackEncoder.HeaderEntry getEntry(CharSequence var1, CharSequence var2) {
      if (this.length() != 0 && var1 != null && var2 != null) {
         int var3 = AsciiString.hashCode(var1);
         int var4 = this.index(var3);

         for(HpackEncoder.HeaderEntry var5 = this.headerFields[var4]; var5 != null; var5 = var5.next) {
            if (var5.hash == var3 && (HpackUtil.equalsConstantTime(var1, var5.name) & HpackUtil.equalsConstantTime(var2, var5.value)) != 0) {
               return var5;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private int getIndex(CharSequence var1) {
      if (this.length() != 0 && var1 != null) {
         int var2 = AsciiString.hashCode(var1);
         int var3 = this.index(var2);

         for(HpackEncoder.HeaderEntry var4 = this.headerFields[var3]; var4 != null; var4 = var4.next) {
            if (var4.hash == var2 && HpackUtil.equalsConstantTime(var1, var4.name) != 0) {
               return this.getIndex(var4.index);
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int getIndex(int var1) {
      return var1 == -1 ? -1 : var1 - this.head.before.index + 1;
   }

   private void add(CharSequence var1, CharSequence var2, long var3) {
      if (var3 > this.maxHeaderTableSize) {
         this.clear();
      } else {
         while(this.maxHeaderTableSize - this.size < var3) {
            this.remove();
         }

         int var5 = AsciiString.hashCode(var1);
         int var6 = this.index(var5);
         HpackEncoder.HeaderEntry var7 = this.headerFields[var6];
         HpackEncoder.HeaderEntry var8 = new HpackEncoder.HeaderEntry(var5, var1, var2, this.head.before.index - 1, var7);
         this.headerFields[var6] = var8;
         var8.addBefore(this.head);
         this.size += var3;
      }
   }

   private HpackHeaderField remove() {
      if (this.size == 0L) {
         return null;
      } else {
         HpackEncoder.HeaderEntry var1 = this.head.after;
         int var2 = var1.hash;
         int var3 = this.index(var2);
         HpackEncoder.HeaderEntry var4 = this.headerFields[var3];

         HpackEncoder.HeaderEntry var6;
         for(HpackEncoder.HeaderEntry var5 = var4; var5 != null; var5 = var6) {
            var6 = var5.next;
            if (var5 == var1) {
               if (var4 == var1) {
                  this.headerFields[var3] = var6;
               } else {
                  var4.next = var6;
               }

               var1.remove();
               this.size -= (long)var1.size();
               return var1;
            }

            var4 = var5;
         }

         return null;
      }
   }

   private void clear() {
      Arrays.fill(this.headerFields, (Object)null);
      this.head.before = this.head.after = this.head;
      this.size = 0L;
   }

   private int index(int var1) {
      return var1 & this.hashMask;
   }

   private static final class HeaderEntry extends HpackHeaderField {
      HpackEncoder.HeaderEntry before;
      HpackEncoder.HeaderEntry after;
      HpackEncoder.HeaderEntry next;
      int hash;
      int index;

      HeaderEntry(int var1, CharSequence var2, CharSequence var3, int var4, HpackEncoder.HeaderEntry var5) {
         super(var2, var3);
         this.index = var4;
         this.hash = var1;
         this.next = var5;
      }

      private void remove() {
         this.before.after = this.after;
         this.after.before = this.before;
         this.before = null;
         this.after = null;
         this.next = null;
      }

      private void addBefore(HpackEncoder.HeaderEntry var1) {
         this.after = var1;
         this.before = var1.before;
         this.before.after = this;
         this.after.before = this;
      }
   }
}
