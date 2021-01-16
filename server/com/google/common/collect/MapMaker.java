package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible(
   emulated = true
)
public final class MapMaker {
   private static final int DEFAULT_INITIAL_CAPACITY = 16;
   private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
   static final int UNSET_INT = -1;
   boolean useCustomMap;
   int initialCapacity = -1;
   int concurrencyLevel = -1;
   MapMakerInternalMap.Strength keyStrength;
   MapMakerInternalMap.Strength valueStrength;
   Equivalence<Object> keyEquivalence;

   public MapMaker() {
      super();
   }

   @CanIgnoreReturnValue
   @GwtIncompatible
   MapMaker keyEquivalence(Equivalence<Object> var1) {
      Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", (Object)this.keyEquivalence);
      this.keyEquivalence = (Equivalence)Preconditions.checkNotNull(var1);
      this.useCustomMap = true;
      return this;
   }

   Equivalence<Object> getKeyEquivalence() {
      return (Equivalence)MoreObjects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
   }

   @CanIgnoreReturnValue
   public MapMaker initialCapacity(int var1) {
      Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", this.initialCapacity);
      Preconditions.checkArgument(var1 >= 0);
      this.initialCapacity = var1;
      return this;
   }

   int getInitialCapacity() {
      return this.initialCapacity == -1 ? 16 : this.initialCapacity;
   }

   @CanIgnoreReturnValue
   public MapMaker concurrencyLevel(int var1) {
      Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", this.concurrencyLevel);
      Preconditions.checkArgument(var1 > 0);
      this.concurrencyLevel = var1;
      return this;
   }

   int getConcurrencyLevel() {
      return this.concurrencyLevel == -1 ? 4 : this.concurrencyLevel;
   }

   @CanIgnoreReturnValue
   @GwtIncompatible
   public MapMaker weakKeys() {
      return this.setKeyStrength(MapMakerInternalMap.Strength.WEAK);
   }

   MapMaker setKeyStrength(MapMakerInternalMap.Strength var1) {
      Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", (Object)this.keyStrength);
      this.keyStrength = (MapMakerInternalMap.Strength)Preconditions.checkNotNull(var1);
      if (var1 != MapMakerInternalMap.Strength.STRONG) {
         this.useCustomMap = true;
      }

      return this;
   }

   MapMakerInternalMap.Strength getKeyStrength() {
      return (MapMakerInternalMap.Strength)MoreObjects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
   }

   @CanIgnoreReturnValue
   @GwtIncompatible
   public MapMaker weakValues() {
      return this.setValueStrength(MapMakerInternalMap.Strength.WEAK);
   }

   MapMaker setValueStrength(MapMakerInternalMap.Strength var1) {
      Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", (Object)this.valueStrength);
      this.valueStrength = (MapMakerInternalMap.Strength)Preconditions.checkNotNull(var1);
      if (var1 != MapMakerInternalMap.Strength.STRONG) {
         this.useCustomMap = true;
      }

      return this;
   }

   MapMakerInternalMap.Strength getValueStrength() {
      return (MapMakerInternalMap.Strength)MoreObjects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
   }

   public <K, V> ConcurrentMap<K, V> makeMap() {
      return (ConcurrentMap)(!this.useCustomMap ? new ConcurrentHashMap(this.getInitialCapacity(), 0.75F, this.getConcurrencyLevel()) : MapMakerInternalMap.create(this));
   }

   @GwtIncompatible
   <K, V> MapMakerInternalMap<K, V, ?, ?> makeCustomMap() {
      return MapMakerInternalMap.create(this);
   }

   public String toString() {
      MoreObjects.ToStringHelper var1 = MoreObjects.toStringHelper((Object)this);
      if (this.initialCapacity != -1) {
         var1.add("initialCapacity", this.initialCapacity);
      }

      if (this.concurrencyLevel != -1) {
         var1.add("concurrencyLevel", this.concurrencyLevel);
      }

      if (this.keyStrength != null) {
         var1.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
      }

      if (this.valueStrength != null) {
         var1.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
      }

      if (this.keyEquivalence != null) {
         var1.addValue("keyEquivalence");
      }

      return var1.toString();
   }
}
