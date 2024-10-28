package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record SeededContainerLoot(ResourceKey<LootTable> lootTable, long seed) {
   public static final Codec<SeededContainerLoot> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(SeededContainerLoot::lootTable), Codec.LONG.optionalFieldOf("seed", 0L).forGetter(SeededContainerLoot::seed)).apply(var0, SeededContainerLoot::new);
   });

   public SeededContainerLoot(ResourceKey<LootTable> var1, long var2) {
      super();
      this.lootTable = var1;
      this.seed = var2;
   }

   public ResourceKey<LootTable> lootTable() {
      return this.lootTable;
   }

   public long seed() {
      return this.seed;
   }
}
