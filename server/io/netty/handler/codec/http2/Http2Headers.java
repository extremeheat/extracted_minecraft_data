package io.netty.handler.codec.http2;

import io.netty.handler.codec.Headers;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.Map.Entry;

public interface Http2Headers extends Headers<CharSequence, CharSequence, Http2Headers> {
   Iterator<Entry<CharSequence, CharSequence>> iterator();

   Iterator<CharSequence> valueIterator(CharSequence var1);

   Http2Headers method(CharSequence var1);

   Http2Headers scheme(CharSequence var1);

   Http2Headers authority(CharSequence var1);

   Http2Headers path(CharSequence var1);

   Http2Headers status(CharSequence var1);

   CharSequence method();

   CharSequence scheme();

   CharSequence authority();

   CharSequence path();

   CharSequence status();

   boolean contains(CharSequence var1, CharSequence var2, boolean var3);

   public static enum PseudoHeaderName {
      METHOD(":method", true),
      SCHEME(":scheme", true),
      AUTHORITY(":authority", true),
      PATH(":path", true),
      STATUS(":status", false);

      private static final char PSEUDO_HEADER_PREFIX = ':';
      private static final byte PSEUDO_HEADER_PREFIX_BYTE = 58;
      private final AsciiString value;
      private final boolean requestOnly;
      private static final CharSequenceMap<Http2Headers.PseudoHeaderName> PSEUDO_HEADERS = new CharSequenceMap();

      private PseudoHeaderName(String var3, boolean var4) {
         this.value = AsciiString.cached(var3);
         this.requestOnly = var4;
      }

      public AsciiString value() {
         return this.value;
      }

      public static boolean hasPseudoHeaderFormat(CharSequence var0) {
         if (var0 instanceof AsciiString) {
            AsciiString var1 = (AsciiString)var0;
            return var1.length() > 0 && var1.byteAt(0) == 58;
         } else {
            return var0.length() > 0 && var0.charAt(0) == ':';
         }
      }

      public static boolean isPseudoHeader(CharSequence var0) {
         return PSEUDO_HEADERS.contains(var0);
      }

      public static Http2Headers.PseudoHeaderName getPseudoHeader(CharSequence var0) {
         return (Http2Headers.PseudoHeaderName)PSEUDO_HEADERS.get(var0);
      }

      public boolean isRequestOnly() {
         return this.requestOnly;
      }

      static {
         Http2Headers.PseudoHeaderName[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            Http2Headers.PseudoHeaderName var3 = var0[var2];
            PSEUDO_HEADERS.add(var3.value(), var3);
         }

      }
   }
}
