package io.netty.handler.codec.http;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class EmptyHttpHeaders extends HttpHeaders {
   static final Iterator<Entry<CharSequence, CharSequence>> EMPTY_CHARS_ITERATOR = Collections.emptyList().iterator();
   public static final EmptyHttpHeaders INSTANCE = instance();

   /** @deprecated */
   @Deprecated
   static EmptyHttpHeaders instance() {
      return EmptyHttpHeaders.InstanceInitializer.EMPTY_HEADERS;
   }

   protected EmptyHttpHeaders() {
      super();
   }

   public String get(String var1) {
      return null;
   }

   public Integer getInt(CharSequence var1) {
      return null;
   }

   public int getInt(CharSequence var1, int var2) {
      return var2;
   }

   public Short getShort(CharSequence var1) {
      return null;
   }

   public short getShort(CharSequence var1, short var2) {
      return var2;
   }

   public Long getTimeMillis(CharSequence var1) {
      return null;
   }

   public long getTimeMillis(CharSequence var1, long var2) {
      return var2;
   }

   public List<String> getAll(String var1) {
      return Collections.emptyList();
   }

   public List<Entry<String, String>> entries() {
      return Collections.emptyList();
   }

   public boolean contains(String var1) {
      return false;
   }

   public boolean isEmpty() {
      return true;
   }

   public int size() {
      return 0;
   }

   public Set<String> names() {
      return Collections.emptySet();
   }

   public HttpHeaders add(String var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders add(String var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders addInt(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders addShort(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders set(String var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders set(String var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders setInt(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders setShort(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders remove(String var1) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders clear() {
      throw new UnsupportedOperationException("read only");
   }

   public Iterator<Entry<String, String>> iterator() {
      return this.entries().iterator();
   }

   public Iterator<Entry<CharSequence, CharSequence>> iteratorCharSequence() {
      return EMPTY_CHARS_ITERATOR;
   }

   /** @deprecated */
   @Deprecated
   private static final class InstanceInitializer {
      /** @deprecated */
      @Deprecated
      private static final EmptyHttpHeaders EMPTY_HEADERS = new EmptyHttpHeaders();

      private InstanceInitializer() {
         super();
      }
   }
}
