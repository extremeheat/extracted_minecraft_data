package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class IEntitySelector$ArmoredMob implements IEntitySelector {
   private final ItemStack field_96567_c;

   public IEntitySelector$ArmoredMob(ItemStack var1) {
      super();
      this.field_96567_c = var1;
   }

   @Override
   public boolean func_82704_a(Entity var1) {
      if (!var1.func_70089_S()) {
         return false;
      } else if (!(var1 instanceof EntityLivingBase)) {
         return false;
      } else {
         EntityLivingBase var2 = (EntityLivingBase)var1;
         if (var2.func_71124_b(EntityLiving.func_82159_b(this.field_96567_c)) != null) {
            return false;
         } else if (var2 instanceof EntityLiving) {
            return ((EntityLiving)var2).func_98052_bS();
         } else {
            return var2 instanceof EntityPlayer;
         }
      }
   }
}
