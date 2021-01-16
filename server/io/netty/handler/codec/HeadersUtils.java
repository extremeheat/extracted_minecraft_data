package io.netty.handler.codec;

import io.netty.util.internal.ObjectUtil;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public final class HeadersUtils {
   private HeadersUtils() {
      super();
   }

   public static <K, V> List<String> getAllAsString(Headers<K, V, ?> var0, K var1) {
      final List var2 = var0.getAll(var1);
      return new AbstractList<String>() {
         public String get(int var1) {
            Object var2x = var2.get(var1);
            return var2x != null ? var2x.toString() : null;
         }

         public int size() {
            return var2.size();
         }
      };
   }

   public static <K, V> String getAsString(Headers<K, V, ?> var0, K var1) {
      Object var2 = var0.get(var1);
      return var2 != null ? var2.toString() : null;
   }

   public static Iterator<Entry<String, String>> iteratorAsString(Iterable<Entry<CharSequence, CharSequence>> var0) {
      return new HeadersUtils.StringEntryIterator(var0.iterator());
   }

   public static <K, V> String toString(Class<?> var0, Iterator<Entry<K, V>> var1, int var2) {
      String var3 = var0.getSimpleName();
      if (var2 == 0) {
         return var3 + "[]";
      } else {
         StringBuilder var4 = (new StringBuilder(var3.length() + 2 + var2 * 20)).append(var3).append('[');

         while(var1.hasNext()) {
            Entry var5 = (Entry)var1.next();
            var4.append(var5.getKey()).append(": ").append(var5.getValue()).append(", ");
         }

         var4.setLength(var4.length() - 2);
         return var4.append(']').toString();
      }
   }

   public static Set<String> namesAsString(Headers<CharSequence, CharSequence, ?> var0) {
      return new HeadersUtils.CharSequenceDelegatingStringSet(var0.names());
   }

   private abstract static class DelegatingStringSet<T> extends AbstractCollection<String> implements Set<String> {
      protected final Set<T> allNames;

      DelegatingStringSet(Set<T> var1) {
         super();
         this.allNames = (Set)ObjectUtil.checkNotNull(var1, "allNames");
      }

      public int size() {
         return this.allNames.size();
      }

      public boolean isEmpty() {
         return this.allNames.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.allNames.contains(var1.toString());
      }

      public Iterator<String> iterator() {
         return new HeadersUtils.StringIterator(this.allNames.iterator());
      }

      public boolean remove(Object var1) {
         return this.allNames.remove(var1);
      }

      public void clear() {
         this.allNames.clear();
      }
   }

   private static final class CharSequenceDelegatingStringSet extends HeadersUtils.DelegatingStringSet<CharSequence> {
      CharSequenceDelegatingStringSet(Set<CharSequence> var1) {
         super(var1);
      }

      public boolean add(String var1) {
         return this.allNames.add(var1);
      }

      public boolean addAll(Collection<? extends String> var1) {
         return this.allNames.addAll(var1);
      }
   }

   private static final class StringIterator<T> implements Iterator<String> {
      private final Iterator<T> iter;

      StringIterator(Iterator<T> var1) {
         super();
         this.iter = var1;
      }

      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public String next() {
         Object var1 = this.iter.next();
         return var1 != null ? var1.toString() : null;
      }

      public void remove() {
         this.iter.remove();
      }
   }

   private static final class StringEntry implements Entry<String, String> {
      private final Entry<CharSequence, CharSequence> entry;
      private String name;
      private String value;

      StringEntry(Entry<CharSequence, CharSequence> var1) {
         super();
         this.entry = var1;
      }

      public String getKey() {
         if (this.name == null) {
            this.name = ((CharSequence)this.entry.getKey()).toString();
         }

         return this.name;
      }

      public String getValue() {
         if (this.value == null && this.entry.getValue() != null) {
            this.value = ((CharSequence)this.entry.getValue()).toString();
         }

         return this.value;
      }

      public String setValue(String var1) {
         String var2 = this.getValue();
         this.entry.setValue(var1);
         return var2;
      }

      public String toString() {
         return this.entry.toString();
      }
   }

   private static final class StringEntryIterator implements Iterator<Entry<String, String>> {
      private final Iterator<Entry<CharSequence, CharSequence>> iter;

      StringEntryIterator(Iterator<Entry<CharSequence, CharSequence>> var1) {
         super();
         this.iter = var1;
      }

      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public Entry<String, String> next() {
         return new HeadersUtils.StringEntry((Entry)this.iter.next());
      }

      public void remove() {
         this.iter.remove();
      }
   }
}
