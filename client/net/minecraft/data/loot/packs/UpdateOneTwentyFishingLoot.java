package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class UpdateOneTwentyFishingLoot implements LootTableSubProvider {
   public UpdateOneTwentyFishingLoot() {
      super();
   }

   @Override
   public void generate(BiConsumer<ResourceLocation, LootTable.Builder> var1) {
      var1.accept(BuiltInLootTables.FISHING_FISH, VanillaFishingLoot.fishingFishLootTable());
   }
}
