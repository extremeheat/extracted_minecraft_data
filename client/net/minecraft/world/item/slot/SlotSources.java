package net.minecraft.world.item.slot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.LootContext;

public interface SlotSources {
   Codec<SlotSource> TYPED_CODEC = BuiltInRegistries.SLOT_SOURCE_TYPE.byNameCodec().dispatch(SlotSource::codec, (c) -> c);
   Codec<SlotSource> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, GroupSlotSource.INLINE_CODEC));

   static MapCodec<? extends SlotSource> bootstrap(final Registry<MapCodec<? extends SlotSource>> registry) {
      Registry.register(registry, (String)"group", GroupSlotSource.MAP_CODEC);
      Registry.register(registry, (String)"filtered", FilteredSlotSource.MAP_CODEC);
      Registry.register(registry, (String)"limit_slots", LimitSlotSource.MAP_CODEC);
      Registry.register(registry, (String)"slot_range", RangeSlotSource.MAP_CODEC);
      Registry.register(registry, (String)"contents", ContentsSlotSource.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"empty", EmptySlotSource.MAP_CODEC);
   }

   static Function<LootContext, SlotCollection> group(final Collection<? extends SlotSource> list) {
      List<SlotSource> terms = List.copyOf(list);
      Function var10000;
      switch (terms.size()) {
         case 0:
            var10000 = (context) -> SlotCollection.EMPTY;
            break;
         case 1:
            SlotSource var4 = (SlotSource)terms.getFirst();
            Objects.requireNonNull(var4);
            var10000 = var4::provide;
            break;
         case 2:
            SlotSource first = (SlotSource)terms.get(0);
            SlotSource second = (SlotSource)terms.get(1);
            var10000 = (context) -> SlotCollection.concat(first.provide(context), second.provide(context));
            break;
         default:
            var10000 = (context) -> {
               List<SlotCollection> collections = new ArrayList();

               for(SlotSource term : terms) {
                  collections.add(term.provide(context));
               }

               return SlotCollection.concat(collections);
            };
      }

      return var10000;
   }
}
