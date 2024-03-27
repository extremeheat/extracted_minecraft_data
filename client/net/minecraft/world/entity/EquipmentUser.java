package net.minecraft.world.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

public interface EquipmentUser {
   void setItemSlot(EquipmentSlot var1, ItemStack var2);

   ItemStack getItemBySlot(EquipmentSlot var1);

   void setDropChance(EquipmentSlot var1, float var2);

   default void equip(ResourceLocation var1, LootParams var2) {
      this.equip(var1, var2, 0L);
   }

   default void equip(ResourceLocation var1, LootParams var2, long var3) {
      ResourceKey var5 = ResourceKey.create(Registries.LOOT_TABLE, var1);
      if (!var5.equals(BuiltInLootTables.EMPTY)) {
         LootTable var6 = var2.getLevel().getServer().reloadableRegistries().getLootTable(var5);
         if (var6 != LootTable.EMPTY) {
            ObjectArrayList var7 = var6.getRandomItems(var2, var3);
            ArrayList var8 = new ArrayList();

            for(ItemStack var10 : var7) {
               EquipmentSlot var11 = this.resolveSlot(var10, var8);
               if (var11 != null) {
                  ItemStack var12 = var11.isArmor() ? var10.copyWithCount(1) : var10;
                  this.setItemSlot(var11, var12);
                  this.setDropChance(var11, 0.085F);
                  var8.add(var11);
               }
            }
         }
      }
   }

   @Nullable
   default EquipmentSlot resolveSlot(ItemStack var1, List<EquipmentSlot> var2) {
      Equipable var3 = Equipable.get(var1);
      if (var3 != null) {
         EquipmentSlot var4 = var3.getEquipmentSlot();
         if (!var2.contains(var4)) {
            return var4;
         }
      } else if (!var2.contains(EquipmentSlot.MAINHAND)) {
         return EquipmentSlot.MAINHAND;
      }

      return null;
   }
}
