package net.minecraft.core;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.JavaOps;

public class Cloner<T> {
   private final Codec<T> directCodec;

   Cloner(Codec<T> var1) {
      super();
      this.directCodec = var1;
   }

   public T clone(T var1, HolderLookup.Provider var2, HolderLookup.Provider var3) {
      RegistryOps var4 = var2.createSerializationContext(JavaOps.INSTANCE);
      RegistryOps var5 = var3.createSerializationContext(JavaOps.INSTANCE);
      Object var6 = Util.getOrThrow(this.directCodec.encodeStart(var4, var1), var0 -> new IllegalStateException("Failed to encode: " + var0));
      return Util.getOrThrow(this.directCodec.parse(var5, var6), var0 -> new IllegalStateException("Failed to decode: " + var0));
   }

   public static class Factory {
      private final Map<ResourceKey<? extends Registry<?>>, Cloner<?>> codecs = new HashMap<>();

      public Factory() {
         super();
      }

      public <T> Cloner.Factory addCodec(ResourceKey<? extends Registry<? extends T>> var1, Codec<T> var2) {
         this.codecs.put(var1, new Cloner(var2));
         return this;
      }

      @Nullable
      public <T> Cloner<T> cloner(ResourceKey<? extends Registry<? extends T>> var1) {
         return (Cloner<T>)this.codecs.get(var1);
      }
   }
}
