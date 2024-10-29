package net.minecraft.world.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;

public class DiggerItem extends Item {
   protected DiggerItem(ToolMaterial var1, TagKey<Block> var2, float var3, float var4, Item.Properties var5) {
      super(var1.applyToolProperties(var5, var2, var3, var4));
   }

   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      return true;
   }

   public void postHurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(2, var3, EquipmentSlot.MAINHAND);
   }
}
