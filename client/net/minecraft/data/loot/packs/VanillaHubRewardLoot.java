package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.RoomerinoComponentino;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;

public record VanillaHubRewardLoot(HolderLookup.Provider registries) implements LootTableSubProvider {
   public VanillaHubRewardLoot(HolderLookup.Provider var1) {
      super();
      this.registries = var1;
   }

   public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> var1) {
      var1.accept(BuiltInLootTables.ROOM_REWARD, LootTable.lootTable().withPool(LootPool.lootPool().add(roomDrop(ResourceLocation.withDefaultNamespace("barrels"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("hanging"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("corridor_simple"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("fountain"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("storage1"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("storage2"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("tree"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("carpet"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("grassy_h"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("sugar_h"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("wheat_h"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("house"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("pool"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("ship"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("stairs"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("trophy"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("tunnel"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("useless"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("workshop"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("boiler"), 2)).add(roomDrop(ResourceLocation.withDefaultNamespace("labyrinth"), 1))));
   }

   private static LootPoolSingletonContainer.Builder<?> roomDrop(ResourceLocation var0, int var1) {
      return LootItem.lootTableItem(Items.SHIMMERING_KEY).setWeight(var1).apply(SetComponentsFunction.setComponent(DataComponents.ROOM, new RoomerinoComponentino(var0)));
   }
}
