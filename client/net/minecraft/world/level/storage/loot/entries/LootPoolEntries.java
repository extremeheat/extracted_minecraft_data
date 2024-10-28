package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class LootPoolEntries {
   public static final Codec<LootPoolEntryContainer> CODEC;
   public static final LootPoolEntryType EMPTY;
   public static final LootPoolEntryType ITEM;
   public static final LootPoolEntryType LOOT_TABLE;
   public static final LootPoolEntryType DYNAMIC;
   public static final LootPoolEntryType TAG;
   public static final LootPoolEntryType ALTERNATIVES;
   public static final LootPoolEntryType SEQUENCE;
   public static final LootPoolEntryType GROUP;

   public LootPoolEntries() {
      super();
   }

   private static LootPoolEntryType register(String var0, MapCodec<? extends LootPoolEntryContainer> var1) {
      return (LootPoolEntryType)Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), new LootPoolEntryType(var1));
   }

   static {
      CODEC = BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.byNameCodec().dispatch(LootPoolEntryContainer::getType, LootPoolEntryType::codec);
      EMPTY = register("empty", EmptyLootItem.CODEC);
      ITEM = register("item", LootItem.CODEC);
      LOOT_TABLE = register("loot_table", NestedLootTable.CODEC);
      DYNAMIC = register("dynamic", DynamicLoot.CODEC);
      TAG = register("tag", TagEntry.CODEC);
      ALTERNATIVES = register("alternatives", AlternativesEntry.CODEC);
      SEQUENCE = register("sequence", SequentialEntry.CODEC);
      GROUP = register("group", EntryGroup.CODEC);
   }
}
