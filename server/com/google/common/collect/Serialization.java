package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@GwtIncompatible
final class Serialization {
   private Serialization() {
      super();
   }

   static int readCount(ObjectInputStream var0) throws IOException {
      return var0.readInt();
   }

   static <K, V> void writeMap(Map<K, V> var0, ObjectOutputStream var1) throws IOException {
      var1.writeInt(var0.size());
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.writeObject(var3.getKey());
         var1.writeObject(var3.getValue());
      }

   }

   static <K, V> void populateMap(Map<K, V> var0, ObjectInputStream var1) throws IOException, ClassNotFoundException {
      int var2 = var1.readInt();
      populateMap(var0, var1, var2);
   }

   static <K, V> void populateMap(Map<K, V> var0, ObjectInputStream var1, int var2) throws IOException, ClassNotFoundException {
      for(int var3 = 0; var3 < var2; ++var3) {
         Object var4 = var1.readObject();
         Object var5 = var1.readObject();
         var0.put(var4, var5);
      }

   }

   static <E> void writeMultiset(Multiset<E> var0, ObjectOutputStream var1) throws IOException {
      int var2 = var0.entrySet().size();
      var1.writeInt(var2);
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Multiset.Entry var4 = (Multiset.Entry)var3.next();
         var1.writeObject(var4.getElement());
         var1.writeInt(var4.getCount());
      }

   }

   static <E> void populateMultiset(Multiset<E> var0, ObjectInputStream var1) throws IOException, ClassNotFoundException {
      int var2 = var1.readInt();
      populateMultiset(var0, var1, var2);
   }

   static <E> void populateMultiset(Multiset<E> var0, ObjectInputStream var1, int var2) throws IOException, ClassNotFoundException {
      for(int var3 = 0; var3 < var2; ++var3) {
         Object var4 = var1.readObject();
         int var5 = var1.readInt();
         var0.add(var4, var5);
      }

   }

   static <K, V> void writeMultimap(Multimap<K, V> var0, ObjectOutputStream var1) throws IOException {
      var1.writeInt(var0.asMap().size());
      Iterator var2 = var0.asMap().entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.writeObject(var3.getKey());
         var1.writeInt(((Collection)var3.getValue()).size());
         Iterator var4 = ((Collection)var3.getValue()).iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            var1.writeObject(var5);
         }
      }

   }

   static <K, V> void populateMultimap(Multimap<K, V> var0, ObjectInputStream var1) throws IOException, ClassNotFoundException {
      int var2 = var1.readInt();
      populateMultimap(var0, var1, var2);
   }

   static <K, V> void populateMultimap(Multimap<K, V> var0, ObjectInputStream var1, int var2) throws IOException, ClassNotFoundException {
      for(int var3 = 0; var3 < var2; ++var3) {
         Object var4 = var1.readObject();
         Collection var5 = var0.get(var4);
         int var6 = var1.readInt();

         for(int var7 = 0; var7 < var6; ++var7) {
            Object var8 = var1.readObject();
            var5.add(var8);
         }
      }

   }

   static <T> Serialization.FieldSetter<T> getFieldSetter(Class<T> var0, String var1) {
      try {
         Field var2 = var0.getDeclaredField(var1);
         return new Serialization.FieldSetter(var2);
      } catch (NoSuchFieldException var3) {
         throw new AssertionError(var3);
      }
   }

   static final class FieldSetter<T> {
      private final Field field;

      private FieldSetter(Field var1) {
         super();
         this.field = var1;
         var1.setAccessible(true);
      }

      void set(T var1, Object var2) {
         try {
            this.field.set(var1, var2);
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         }
      }

      void set(T var1, int var2) {
         try {
            this.field.set(var1, var2);
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         }
      }

      // $FF: synthetic method
      FieldSetter(Field var1, Object var2) {
         this(var1);
      }
   }
}
