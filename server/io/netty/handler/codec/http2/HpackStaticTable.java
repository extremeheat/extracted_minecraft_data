package io.netty.handler.codec.http2;

import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.util.AsciiString;
import java.util.Arrays;
import java.util.List;

final class HpackStaticTable {
   private static final List<HpackHeaderField> STATIC_TABLE = Arrays.asList(newEmptyHeaderField(":authority"), newHeaderField(":method", "GET"), newHeaderField(":method", "POST"), newHeaderField(":path", "/"), newHeaderField(":path", "/index.html"), newHeaderField(":scheme", "http"), newHeaderField(":scheme", "https"), newHeaderField(":status", "200"), newHeaderField(":status", "204"), newHeaderField(":status", "206"), newHeaderField(":status", "304"), newHeaderField(":status", "400"), newHeaderField(":status", "404"), newHeaderField(":status", "500"), newEmptyHeaderField("accept-charset"), newHeaderField("accept-encoding", "gzip, deflate"), newEmptyHeaderField("accept-language"), newEmptyHeaderField("accept-ranges"), newEmptyHeaderField("accept"), newEmptyHeaderField("access-control-allow-origin"), newEmptyHeaderField("age"), newEmptyHeaderField("allow"), newEmptyHeaderField("authorization"), newEmptyHeaderField("cache-control"), newEmptyHeaderField("content-disposition"), newEmptyHeaderField("content-encoding"), newEmptyHeaderField("content-language"), newEmptyHeaderField("content-length"), newEmptyHeaderField("content-location"), newEmptyHeaderField("content-range"), newEmptyHeaderField("content-type"), newEmptyHeaderField("cookie"), newEmptyHeaderField("date"), newEmptyHeaderField("etag"), newEmptyHeaderField("expect"), newEmptyHeaderField("expires"), newEmptyHeaderField("from"), newEmptyHeaderField("host"), newEmptyHeaderField("if-match"), newEmptyHeaderField("if-modified-since"), newEmptyHeaderField("if-none-match"), newEmptyHeaderField("if-range"), newEmptyHeaderField("if-unmodified-since"), newEmptyHeaderField("last-modified"), newEmptyHeaderField("link"), newEmptyHeaderField("location"), newEmptyHeaderField("max-forwards"), newEmptyHeaderField("proxy-authenticate"), newEmptyHeaderField("proxy-authorization"), newEmptyHeaderField("range"), newEmptyHeaderField("referer"), newEmptyHeaderField("refresh"), newEmptyHeaderField("retry-after"), newEmptyHeaderField("server"), newEmptyHeaderField("set-cookie"), newEmptyHeaderField("strict-transport-security"), newEmptyHeaderField("transfer-encoding"), newEmptyHeaderField("user-agent"), newEmptyHeaderField("vary"), newEmptyHeaderField("via"), newEmptyHeaderField("www-authenticate"));
   private static final CharSequenceMap<Integer> STATIC_INDEX_BY_NAME = createMap();
   static final int length;

   private static HpackHeaderField newEmptyHeaderField(String var0) {
      return new HpackHeaderField(AsciiString.cached(var0), AsciiString.EMPTY_STRING);
   }

   private static HpackHeaderField newHeaderField(String var0, String var1) {
      return new HpackHeaderField(AsciiString.cached(var0), AsciiString.cached(var1));
   }

   static HpackHeaderField getEntry(int var0) {
      return (HpackHeaderField)STATIC_TABLE.get(var0 - 1);
   }

   static int getIndex(CharSequence var0) {
      Integer var1 = (Integer)STATIC_INDEX_BY_NAME.get(var0);
      return var1 == null ? -1 : var1;
   }

   static int getIndex(CharSequence var0, CharSequence var1) {
      int var2 = getIndex(var0);
      if (var2 == -1) {
         return -1;
      } else {
         while(var2 <= length) {
            HpackHeaderField var3 = getEntry(var2);
            if (HpackUtil.equalsConstantTime(var0, var3.name) == 0) {
               break;
            }

            if (HpackUtil.equalsConstantTime(var1, var3.value) != 0) {
               return var2;
            }

            ++var2;
         }

         return -1;
      }
   }

   private static CharSequenceMap<Integer> createMap() {
      int var0 = STATIC_TABLE.size();
      CharSequenceMap var1 = new CharSequenceMap(true, UnsupportedValueConverter.instance(), var0);

      for(int var2 = var0; var2 > 0; --var2) {
         HpackHeaderField var3 = getEntry(var2);
         CharSequence var4 = var3.name;
         var1.set(var4, var2);
      }

      return var1;
   }

   private HpackStaticTable() {
      super();
   }

   static {
      length = STATIC_TABLE.size();
   }
}
