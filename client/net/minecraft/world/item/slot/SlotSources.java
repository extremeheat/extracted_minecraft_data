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
   Codec<SlotSource> TYPED_CODEC = BuiltInRegistries.SLOT_SOURCE_TYPE.byNameCodec().dispatch(SlotSource::codec, (var0) -> var0);
   Codec<SlotSource> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, GroupSlotSource.INLINE_CODEC));

   static MapCodec<? extends SlotSource> bootstrap(Registry<MapCodec<? extends SlotSource>> var0) {
      Registry.register(var0, (String)"group", GroupSlotSource.MAP_CODEC);
      Registry.register(var0, (String)"filtered", FilteredSlotSource.MAP_CODEC);
      Registry.register(var0, (String)"limit_slots", LimitSlotSource.MAP_CODEC);
      Registry.register(var0, (String)"slot_range", RangeSlotSource.MAP_CODEC);
      Registry.register(var0, (String)"contents", ContentsSlotSource.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (String)"empty", EmptySlotSource.MAP_CODEC);
   }

   static Function<LootContext, SlotCollection> group(Collection<? extends SlotSource> var0) {
      List var1 = List.copyOf(var0);
      Function var10000;
      switch (var1.size()) {
         case 0:
            var10000 = (var0x) -> SlotCollection.EMPTY;
            break;
         case 1:
            SlotSource var4 = (SlotSource)var1.getFirst();
            Objects.requireNonNull(var4);
            var10000 = var4::provide;
            break;
         case 2:
            SlotSource var2 = (SlotSource)var1.get(0);
            SlotSource var3 = (SlotSource)var1.get(1);
            var10000 = (var2x) -> SlotCollection.concat(var2.provide(var2x), var3.provide(var2x));
            break;
         default:
            var10000 = (var1x) -> {
               ArrayList var2 = new ArrayList();

               for(SlotSource var4 : var1) {
                  var2.add(var4.provide(var1x));
               }

               return SlotCollection.concat(var2);
            };
      }

      return var10000;
   }
}
