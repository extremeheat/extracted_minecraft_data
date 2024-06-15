package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class NbtProviders {
   private static final Codec<NbtProvider> TYPED_CODEC = BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE
      .byNameCodec()
      .dispatch(NbtProvider::getType, LootNbtProviderType::codec);
   public static final Codec<NbtProvider> CODEC = Codec.lazyInitialized(
      () -> Codec.either(ContextNbtProvider.INLINE_CODEC, TYPED_CODEC)
            .xmap(Either::unwrap, var0 -> var0 instanceof ContextNbtProvider var1 ? Either.left(var1) : Either.right(var0))
   );
   public static final LootNbtProviderType STORAGE = register("storage", StorageNbtProvider.CODEC);
   public static final LootNbtProviderType CONTEXT = register("context", ContextNbtProvider.CODEC);

   public NbtProviders() {
      super();
   }

   private static LootNbtProviderType register(String var0, MapCodec<? extends NbtProvider> var1) {
      return Registry.register(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE, new ResourceLocation(var0), new LootNbtProviderType(var1));
   }
}
