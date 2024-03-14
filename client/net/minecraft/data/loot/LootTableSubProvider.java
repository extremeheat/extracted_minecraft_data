package net.minecraft.data.loot;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

@FunctionalInterface
public interface LootTableSubProvider {
   void generate(HolderLookup.Provider var1, BiConsumer<ResourceLocation, LootTable.Builder> var2);
}
