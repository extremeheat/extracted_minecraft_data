package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class NbtProviders {
   private static final Codec<NbtProvider> TYPED_CODEC;
   public static final Codec<NbtProvider> CODEC;

   public NbtProviders() {
      super();
   }

   public static MapCodec<? extends NbtProvider> bootstrap(final Registry<MapCodec<? extends NbtProvider>> registry) {
      Registry.register(registry, (String)"storage", StorageNbtProvider.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"context", ContextNbtProvider.MAP_CODEC);
   }

   static {
      TYPED_CODEC = BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE.byNameCodec().dispatch(NbtProvider::codec, (c) -> c);
      CODEC = Codec.lazyInitialized(() -> Codec.either(ContextNbtProvider.INLINE_CODEC, TYPED_CODEC).xmap(Either::unwrap, (provider) -> {
            Either var10000;
            if (provider instanceof ContextNbtProvider context) {
               var10000 = Either.left(context);
            } else {
               var10000 = Either.right(provider);
            }

            return var10000;
         }));
   }
}
