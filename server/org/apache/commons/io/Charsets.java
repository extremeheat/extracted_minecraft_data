package org.apache.commons.io;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class Charsets {
   /** @deprecated */
   @Deprecated
   public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
   /** @deprecated */
   @Deprecated
   public static final Charset US_ASCII = Charset.forName("US-ASCII");
   /** @deprecated */
   @Deprecated
   public static final Charset UTF_16 = Charset.forName("UTF-16");
   /** @deprecated */
   @Deprecated
   public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
   /** @deprecated */
   @Deprecated
   public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
   /** @deprecated */
   @Deprecated
   public static final Charset UTF_8 = Charset.forName("UTF-8");

   public Charsets() {
      super();
   }

   public static SortedMap<String, Charset> requiredCharsets() {
      TreeMap var0 = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      var0.put(ISO_8859_1.name(), ISO_8859_1);
      var0.put(US_ASCII.name(), US_ASCII);
      var0.put(UTF_16.name(), UTF_16);
      var0.put(UTF_16BE.name(), UTF_16BE);
      var0.put(UTF_16LE.name(), UTF_16LE);
      var0.put(UTF_8.name(), UTF_8);
      return Collections.unmodifiableSortedMap(var0);
   }

   public static Charset toCharset(Charset var0) {
      return var0 == null ? Charset.defaultCharset() : var0;
   }

   public static Charset toCharset(String var0) {
      return var0 == null ? Charset.defaultCharset() : Charset.forName(var0);
   }
}
