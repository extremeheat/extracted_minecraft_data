package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class VanillaShearingLoot implements LootTableSubProvider {
   public VanillaShearingLoot() {
      super();
   }

   @Override
   public void generate(HolderLookup.Provider var1, BiConsumer<ResourceLocation, LootTable.Builder> var2) {
      var2.accept(BuiltInLootTables.BOGGED_SHEAR, LootTable.lootTable());
   }
}
