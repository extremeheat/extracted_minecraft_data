package net.minecraft.world.item.slot;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;

public interface SlotSources {
   Codec<SlotSource> TYPED_CODEC = BuiltInRegistries.SLOT_SOURCE_TYPE.byNameCodec().dispatch(SlotSource::codec, (c) -> c);
   Codec<SlotSource> DIRECT_CODEC = Codec.lazyInitialized(() -> Codec.either(TYPED_CODEC, GroupSlotSource.INLINE_CODEC).xmap((typedOrInline) -> (SlotSource)typedOrInline.map((e) -> e, (e) -> e), (slotSource) -> {
         Either var10000;
         if (slotSource instanceof GroupSlotSource composite) {
            var10000 = Either.right(composite);
         } else {
            var10000 = Either.left(slotSource);
         }

         return var10000;
      }));
   Codec<Holder<SlotSource>> CODEC = RegistryCodecs.holder(Registries.SLOT_SOURCE, DIRECT_CODEC);
   Codec<HolderSet<SlotSource>> LIST_CODEC = RegistryCodecs.holderSet(Registries.SLOT_SOURCE, TYPED_CODEC);

   static MapCodec<? extends SlotSource> bootstrap(final Registry<MapCodec<? extends SlotSource>> registry) {
      Registry.register(registry, (String)"group", GroupSlotSource.MAP_CODEC);
      Registry.register(registry, (String)"filtered", FilteredSlotSource.MAP_CODEC);
      Registry.register(registry, (String)"limit_slots", LimitSlotSource.MAP_CODEC);
      Registry.register(registry, (String)"slot_range", RangeSlotSource.MAP_CODEC);
      Registry.register(registry, (String)"contents", ContentsSlotSource.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"empty", EmptySlotSource.MAP_CODEC);
   }
}
