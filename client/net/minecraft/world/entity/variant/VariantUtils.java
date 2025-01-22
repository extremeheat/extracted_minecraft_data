package net.minecraft.world.entity.variant;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class VariantUtils {
   public static final String TAG_VARIANT = "variant";

   public VariantUtils() {
      super();
   }

   public static <T> Holder<T> getDefaultOrAny(RegistryAccess var0, ResourceKey<T> var1) {
      Registry var2 = var0.lookupOrThrow(var1.registryKey());
      Optional var10000 = var2.get(var1);
      Objects.requireNonNull(var2);
      return (Holder)var10000.or(var2::getAny).orElseThrow();
   }

   public static <T> Holder<T> getAny(RegistryAccess var0, ResourceKey<? extends Registry<T>> var1) {
      return (Holder)var0.lookupOrThrow(var1).getAny().orElseThrow();
   }

   public static <T> void writeVariant(CompoundTag var0, Holder<T> var1) {
      var1.unwrapKey().ifPresent((var1x) -> var0.putString("variant", var1x.location().toString()));
   }

   public static <T> Optional<Holder<T>> readVariant(CompoundTag var0, RegistryAccess var1, ResourceKey<? extends Registry<T>> var2) {
      Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(var0.getString("variant"))).map((var1x) -> ResourceKey.create(var2, var1x));
      Objects.requireNonNull(var1);
      return var10000.flatMap(var1::get);
   }
}
