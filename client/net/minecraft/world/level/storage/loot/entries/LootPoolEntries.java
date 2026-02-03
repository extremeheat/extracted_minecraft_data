package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class LootPoolEntries {
   public static final Codec<LootPoolEntryContainer> CODEC;

   public LootPoolEntries() {
      super();
   }

   public static MapCodec<? extends LootPoolEntryContainer> bootstrap(final Registry<MapCodec<? extends LootPoolEntryContainer>> registry) {
      Registry.register(registry, (String)"empty", EmptyLootItem.MAP_CODEC);
      Registry.register(registry, (String)"item", LootItem.MAP_CODEC);
      Registry.register(registry, (String)"loot_table", NestedLootTable.MAP_CODEC);
      Registry.register(registry, (String)"dynamic", DynamicLoot.MAP_CODEC);
      Registry.register(registry, (String)"tag", TagEntry.MAP_CODEC);
      Registry.register(registry, (String)"slots", SlotLoot.MAP_CODEC);
      Registry.register(registry, (String)"alternatives", AlternativesEntry.MAP_CODEC);
      Registry.register(registry, (String)"sequence", SequentialEntry.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"group", EntryGroup.MAP_CODEC);
   }

   static {
      CODEC = BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.byNameCodec().dispatch(LootPoolEntryContainer::codec, (c) -> c);
   }
}
