package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.Set;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class UpdateOneTwentyLootTableProvider {
   public UpdateOneTwentyLootTableProvider() {
      super();
   }

   public static LootTableProvider create(PackOutput var0) {
      return new LootTableProvider(var0, Set.of(), List.of(new LootTableProvider.SubProviderEntry(UpdateOneTwentyBlockLoot::new, LootContextParamSets.BLOCK)));
   }
}
