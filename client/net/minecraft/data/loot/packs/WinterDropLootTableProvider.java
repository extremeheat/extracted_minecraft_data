package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class WinterDropLootTableProvider {
   public WinterDropLootTableProvider() {
      super();
   }

   public static LootTableProvider create(PackOutput var0, CompletableFuture<HolderLookup.Provider> var1) {
      return new LootTableProvider(var0, Set.of(), List.of(new LootTableProvider.SubProviderEntry(WinterDropBlockLoot::new, LootContextParamSets.BLOCK)), var1);
   }
}
