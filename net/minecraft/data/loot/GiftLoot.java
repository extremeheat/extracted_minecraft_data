package net.minecraft.data.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;

public class GiftLoot implements Consumer {
   public void accept(BiConsumer var1) {
      var1.accept(BuiltInLootTables.CAT_MORNING_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.RABBIT_HIDE).setWeight(10)).add(LootItem.lootTableItem(Items.RABBIT_FOOT).setWeight(10)).add(LootItem.lootTableItem(Items.CHICKEN).setWeight(10)).add(LootItem.lootTableItem(Items.FEATHER).setWeight(10)).add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10)).add(LootItem.lootTableItem(Items.STRING).setWeight(10)).add(LootItem.lootTableItem(Items.PHANTOM_MEMBRANE).setWeight(2))));
      var1.accept(BuiltInLootTables.ARMORER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.CHAINMAIL_HELMET)).add(LootItem.lootTableItem(Items.CHAINMAIL_CHESTPLATE)).add(LootItem.lootTableItem(Items.CHAINMAIL_LEGGINGS)).add(LootItem.lootTableItem(Items.CHAINMAIL_BOOTS))));
      var1.accept(BuiltInLootTables.BUTCHER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.COOKED_RABBIT)).add(LootItem.lootTableItem(Items.COOKED_CHICKEN)).add(LootItem.lootTableItem(Items.COOKED_PORKCHOP)).add(LootItem.lootTableItem(Items.COOKED_BEEF)).add(LootItem.lootTableItem(Items.COOKED_MUTTON))));
      var1.accept(BuiltInLootTables.CARTOGRAPHER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.MAP)).add(LootItem.lootTableItem(Items.PAPER))));
      var1.accept(BuiltInLootTables.CLERIC_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.REDSTONE)).add(LootItem.lootTableItem(Items.LAPIS_LAZULI))));
      var1.accept(BuiltInLootTables.FARMER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.BREAD)).add(LootItem.lootTableItem(Items.PUMPKIN_PIE)).add(LootItem.lootTableItem(Items.COOKIE))));
      var1.accept(BuiltInLootTables.FISHERMAN_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.COD)).add(LootItem.lootTableItem(Items.SALMON))));
      var1.accept(BuiltInLootTables.FLETCHER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.ARROW).setWeight(26)).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:swiftness");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:slowness");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:strength");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:healing");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:harming");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:leaping");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:regeneration");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:fire_resistance");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:water_breathing");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:invisibility");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:night_vision");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:weakness");
      })))).add(LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F))).apply(SetNbtFunction.setTag((CompoundTag)Util.make(new CompoundTag(), (var0) -> {
         var0.putString("Potion", "minecraft:poison");
      }))))));
      var1.accept(BuiltInLootTables.LEATHERWORKER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.LEATHER))));
      var1.accept(BuiltInLootTables.LIBRARIAN_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.BOOK))));
      var1.accept(BuiltInLootTables.MASON_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.CLAY))));
      var1.accept(BuiltInLootTables.SHEPHERD_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.WHITE_WOOL)).add(LootItem.lootTableItem(Items.ORANGE_WOOL)).add(LootItem.lootTableItem(Items.MAGENTA_WOOL)).add(LootItem.lootTableItem(Items.LIGHT_BLUE_WOOL)).add(LootItem.lootTableItem(Items.YELLOW_WOOL)).add(LootItem.lootTableItem(Items.LIME_WOOL)).add(LootItem.lootTableItem(Items.PINK_WOOL)).add(LootItem.lootTableItem(Items.GRAY_WOOL)).add(LootItem.lootTableItem(Items.LIGHT_GRAY_WOOL)).add(LootItem.lootTableItem(Items.CYAN_WOOL)).add(LootItem.lootTableItem(Items.PURPLE_WOOL)).add(LootItem.lootTableItem(Items.BLUE_WOOL)).add(LootItem.lootTableItem(Items.BROWN_WOOL)).add(LootItem.lootTableItem(Items.GREEN_WOOL)).add(LootItem.lootTableItem(Items.RED_WOOL)).add(LootItem.lootTableItem(Items.BLACK_WOOL))));
      var1.accept(BuiltInLootTables.TOOLSMITH_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.STONE_PICKAXE)).add(LootItem.lootTableItem(Items.STONE_AXE)).add(LootItem.lootTableItem(Items.STONE_HOE)).add(LootItem.lootTableItem(Items.STONE_SHOVEL))));
      var1.accept(BuiltInLootTables.WEAPONSMITH_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.STONE_AXE)).add(LootItem.lootTableItem(Items.GOLDEN_AXE)).add(LootItem.lootTableItem(Items.IRON_AXE))));
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((BiConsumer)var1);
   }
}
