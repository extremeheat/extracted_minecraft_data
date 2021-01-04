package net.minecraft.core;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Serializable;

public final class SerializableLong implements Serializable {
   private final long value;

   private SerializableLong(long var1) {
      super();
      this.value = var1;
   }

   public long value() {
      return this.value;
   }

   public <T> T serialize(DynamicOps<T> var1) {
      return var1.createLong(this.value);
   }

   public static SerializableLong of(Dynamic<?> var0) {
      return new SerializableLong(var0.asNumber(0).longValue());
   }

   public static SerializableLong of(long var0) {
      return new SerializableLong(var0);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         SerializableLong var2 = (SerializableLong)var1;
         return this.value == var2.value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.value);
   }

   public String toString() {
      return Long.toString(this.value);
   }
}
