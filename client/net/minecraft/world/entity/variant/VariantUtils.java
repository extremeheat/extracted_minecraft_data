package net.minecraft.world.entity.variant;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

   public static <T> void writeVariant(ValueOutput var0, Holder<T> var1) {
      var1.unwrapKey().ifPresent((var1x) -> var0.store("variant", ResourceLocation.CODEC, var1x.location()));
   }

   public static <T> Optional<Holder<T>> readVariant(ValueInput var0, ResourceKey<? extends Registry<T>> var1) {
      Optional var10000 = var0.read("variant", ResourceLocation.CODEC).map((var1x) -> ResourceKey.create(var1, var1x));
      HolderLookup.Provider var10001 = var0.lookup();
      Objects.requireNonNull(var10001);
      return var10000.flatMap(var10001::get);
   }

   public static <T extends PriorityProvider<SpawnContext, ?>> Optional<Holder.Reference<T>> selectVariantToSpawn(SpawnContext var0, ResourceKey<Registry<T>> var1) {
      ServerLevelAccessor var2 = var0.level();
      Stream var3 = var2.registryAccess().lookupOrThrow(var1).listElements();
      return PriorityProvider.pick(var3, Holder::value, var2.getRandom(), var0);
   }
}
