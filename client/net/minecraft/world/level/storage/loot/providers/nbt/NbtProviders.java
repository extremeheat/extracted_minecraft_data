package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class NbtProviders {
   private static final Codec<NbtProvider> TYPED_CODEC;
   public static final Codec<NbtProvider> CODEC;
   public static final LootNbtProviderType STORAGE;
   public static final LootNbtProviderType CONTEXT;

   public NbtProviders() {
      super();
   }

   private static LootNbtProviderType register(String var0, MapCodec<? extends NbtProvider> var1) {
      return (LootNbtProviderType)Registry.register(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), new LootNbtProviderType(var1));
   }

   static {
      TYPED_CODEC = BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE.byNameCodec().dispatch(NbtProvider::getType, LootNbtProviderType::codec);
      CODEC = Codec.lazyInitialized(() -> Codec.either(ContextNbtProvider.INLINE_CODEC, TYPED_CODEC).xmap(Either::unwrap, (var0) -> {
            Either var10000;
            if (var0 instanceof ContextNbtProvider var1) {
               var10000 = Either.left(var1);
            } else {
               var10000 = Either.right(var0);
            }

            return var10000;
         }));
      STORAGE = register("storage", StorageNbtProvider.CODEC);
      CONTEXT = register("context", ContextNbtProvider.CODEC);
   }
}
