package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class Cloner<T> {
   private final Codec<T> directCodec;

   Cloner(Codec<T> var1) {
      super();
      this.directCodec = var1;
   }

   public T clone(T var1, HolderLookup.Provider var2, HolderLookup.Provider var3) {
      RegistryOps var4 = var2.createSerializationContext(JavaOps.INSTANCE);
      RegistryOps var5 = var3.createSerializationContext(JavaOps.INSTANCE);
      Object var6 = this.directCodec.encodeStart(var4, var1).getOrThrow((var0) -> new IllegalStateException("Failed to encode: " + var0));
      return (T)this.directCodec.parse(var5, var6).getOrThrow((var0) -> new IllegalStateException("Failed to decode: " + var0));
   }

   public static class Factory {
      private final Map<ResourceKey<? extends Registry<?>>, Cloner<?>> codecs = new HashMap();

      public Factory() {
         super();
      }

      public <T> Factory addCodec(ResourceKey<? extends Registry<? extends T>> var1, Codec<T> var2) {
         this.codecs.put(var1, new Cloner(var2));
         return this;
      }

      @Nullable
      public <T> Cloner<T> cloner(ResourceKey<? extends Registry<? extends T>> var1) {
         return (Cloner)this.codecs.get(var1);
      }
   }
}
