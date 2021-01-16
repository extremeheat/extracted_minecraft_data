package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;

@GwtIncompatible
class ImmutableMapEntry<K, V> extends ImmutableEntry<K, V> {
   static <K, V> ImmutableMapEntry<K, V>[] createEntryArray(int var0) {
      return new ImmutableMapEntry[var0];
   }

   ImmutableMapEntry(K var1, V var2) {
      super(var1, var2);
      CollectPreconditions.checkEntryNotNull(var1, var2);
   }

   ImmutableMapEntry(ImmutableMapEntry<K, V> var1) {
      super(var1.getKey(), var1.getValue());
   }

   @Nullable
   ImmutableMapEntry<K, V> getNextInKeyBucket() {
      return null;
   }

   @Nullable
   ImmutableMapEntry<K, V> getNextInValueBucket() {
      return null;
   }

   boolean isReusable() {
      return true;
   }

   static final class NonTerminalImmutableBiMapEntry<K, V> extends ImmutableMapEntry.NonTerminalImmutableMapEntry<K, V> {
      private final transient ImmutableMapEntry<K, V> nextInValueBucket;

      NonTerminalImmutableBiMapEntry(K var1, V var2, ImmutableMapEntry<K, V> var3, ImmutableMapEntry<K, V> var4) {
         super(var1, var2, var3);
         this.nextInValueBucket = var4;
      }

      @Nullable
      ImmutableMapEntry<K, V> getNextInValueBucket() {
         return this.nextInValueBucket;
      }
   }

   static class NonTerminalImmutableMapEntry<K, V> extends ImmutableMapEntry<K, V> {
      private final transient ImmutableMapEntry<K, V> nextInKeyBucket;

      NonTerminalImmutableMapEntry(K var1, V var2, ImmutableMapEntry<K, V> var3) {
         super(var1, var2);
         this.nextInKeyBucket = var3;
      }

      @Nullable
      final ImmutableMapEntry<K, V> getNextInKeyBucket() {
         return this.nextInKeyBucket;
      }

      final boolean isReusable() {
         return false;
      }
   }
}
