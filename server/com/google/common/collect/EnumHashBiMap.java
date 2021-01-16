package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class EnumHashBiMap<K extends Enum<K>, V> extends AbstractBiMap<K, V> {
   private transient Class<K> keyType;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Class<K> var0) {
      return new EnumHashBiMap(var0);
   }

   public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Map<K, ? extends V> var0) {
      EnumHashBiMap var1 = create(EnumBiMap.inferKeyType(var0));
      var1.putAll(var0);
      return var1;
   }

   private EnumHashBiMap(Class<K> var1) {
      super(WellBehavedMap.wrap(new EnumMap(var1)), (Map)Maps.newHashMapWithExpectedSize(((Enum[])var1.getEnumConstants()).length));
      this.keyType = var1;
   }

   K checkKey(K var1) {
      return (Enum)Preconditions.checkNotNull(var1);
   }

   @CanIgnoreReturnValue
   public V put(K var1, @Nullable V var2) {
      return super.put(var1, var2);
   }

   @CanIgnoreReturnValue
   public V forcePut(K var1, @Nullable V var2) {
      return super.forcePut(var1, var2);
   }

   public Class<K> keyType() {
      return this.keyType;
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.keyType);
      Serialization.writeMap(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.keyType = (Class)var1.readObject();
      this.setDelegates(WellBehavedMap.wrap(new EnumMap(this.keyType)), new HashMap(((Enum[])this.keyType.getEnumConstants()).length * 3 / 2));
      Serialization.populateMap(this, var1);
   }
}
