package net.minecraft.world.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

public interface EquipmentUser {
   void setItemSlot(EquipmentSlot var1, ItemStack var2);

   ItemStack getItemBySlot(EquipmentSlot var1);

   void setDropChance(EquipmentSlot var1, float var2);

   default void equip(EquipmentTable var1, LootParams var2) {
      this.equip(var1.lootTable(), var2, var1.slotDropChances());
   }

   default void equip(ResourceKey<LootTable> var1, LootParams var2, Map<EquipmentSlot, Float> var3) {
      this.equip(var1, var2, 0L, var3);
   }

   default void equip(ResourceKey<LootTable> var1, LootParams var2, long var3, Map<EquipmentSlot, Float> var5) {
      if (!var1.equals(BuiltInLootTables.EMPTY)) {
         LootTable var6 = var2.getLevel().getServer().reloadableRegistries().getLootTable(var1);
         if (var6 != LootTable.EMPTY) {
            ObjectArrayList var7 = var6.getRandomItems(var2, var3);
            ArrayList var8 = new ArrayList();
            Iterator var9 = var7.iterator();

            while(var9.hasNext()) {
               ItemStack var10 = (ItemStack)var9.next();
               EquipmentSlot var11 = this.resolveSlot(var10, var8);
               if (var11 != null) {
                  ItemStack var12 = var11.limit(var10);
                  this.setItemSlot(var11, var12);
                  Float var13 = (Float)var5.get(var11);
                  if (var13 != null) {
                     this.setDropChance(var11, var13);
                  }

                  var8.add(var11);
               }
            }

         }
      }
   }

   @Nullable
   default EquipmentSlot resolveSlot(ItemStack var1, List<EquipmentSlot> var2) {
      if (var1.isEmpty()) {
         return null;
      } else {
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
}
