package net.minecraft.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;

public class RegistryWriteOps<T> extends DelegatingOps<T> {
   private final RegistryAccess registryHolder;

   public static <T> RegistryWriteOps<T> create(DynamicOps<T> var0, RegistryAccess var1) {
      return new RegistryWriteOps(var0, var1);
   }

   private RegistryWriteOps(DynamicOps<T> var1, RegistryAccess var2) {
      super(var1);
      this.registryHolder = var2;
   }

   protected <E> DataResult<T> encode(E var1, T var2, ResourceKey<? extends Registry<E>> var3, Codec<E> var4) {
      Optional var5 = this.registryHolder.registry(var3);
      if (var5.isPresent()) {
         WritableRegistry var6 = (WritableRegistry)var5.get();
         Optional var7 = var6.getResourceKey(var1);
         if (var7.isPresent()) {
            ResourceKey var8 = (ResourceKey)var7.get();
            return ResourceLocation.CODEC.encode(var8.location(), this.delegate, var2);
         }
      }

      return var4.encode(var1, this, var2);
   }
}
