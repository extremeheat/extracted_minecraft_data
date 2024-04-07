package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record SeededContainerLoot(ResourceKey<LootTable> lootTable, long seed) {
   public static final Codec<SeededContainerLoot> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(SeededContainerLoot::lootTable),
               Codec.LONG.optionalFieldOf("seed", 0L).forGetter(SeededContainerLoot::seed)
            )
            .apply(var0, SeededContainerLoot::new)
   );

   public SeededContainerLoot(ResourceKey<LootTable> lootTable, long seed) {
      super();
      this.lootTable = lootTable;
      this.seed = seed;
   }
}
