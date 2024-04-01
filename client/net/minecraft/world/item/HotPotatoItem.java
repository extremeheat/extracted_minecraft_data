package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.InventoryHeatComponent;

public class HotPotatoItem extends Item {
   public static final int ONE_MILLION = 1000000;
   public static final int DAMAGE_POTATO_HEAT = 20;
   public static final int MAX_POTATO_HEAT = 200;

   public HotPotatoItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      var3.setRemainingFireTicks(var3.getRemainingFireTicks() + 1000000);
      return super.finishUsingItem(var1, var2, var3);
   }

   @Override
   public void inventoryTick(ItemStack var1, Level var2, Entity var3, int var4, boolean var5) {
      super.inventoryTick(var1, var2, var3, var4, var5);
      InventoryHeatComponent var6 = var1.get(DataComponents.INVENTORY_HEAT);
      if (var6 != null && var3.getUUID() == var6.owner() && var4 == var6.slot()) {
         if (var6.heat() < 200) {
            var1.set(DataComponents.INVENTORY_HEAT, new InventoryHeatComponent(var3.getUUID(), var4, var6.heat() + 1));
         }

         int var7 = Mth.lerpDiscrete((float)(var6.heat() - 20) / 180.0F, 0, 5);
         if (var7 > 0) {
            var3.hurt(var3.damageSources().potatoHeat(), (float)var7);
         }
      } else {
         var1.set(DataComponents.INVENTORY_HEAT, new InventoryHeatComponent(var3.getUUID(), var4, 0));
      }
   }
}
