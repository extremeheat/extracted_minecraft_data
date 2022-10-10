package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.world.World;

public class ItemSpectralArrow extends ItemArrow {
   public ItemSpectralArrow(Item.Properties var1) {
      super(var1);
   }

   public EntityArrow func_200887_a(World var1, ItemStack var2, EntityLivingBase var3) {
      return new EntitySpectralArrow(var1, var3);
   }
}
