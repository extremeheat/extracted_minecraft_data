package net.minecraft.world.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownLingeringPotion extends AbstractThrownPotion {
   public ThrownLingeringPotion(EntityType<? extends ThrownLingeringPotion> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownLingeringPotion(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.LINGERING_POTION, var1, var2, var3);
   }

   public ThrownLingeringPotion(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.LINGERING_POTION, var1, var2, var4, var6, var8);
   }

   protected Item getDefaultItem() {
      return Items.LINGERING_POTION;
   }

   public void onHitAsPotion(ServerLevel var1, ItemStack var2, HitResult var3) {
      AreaEffectCloud var4 = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
      Entity var6 = this.getOwner();
      if (var6 instanceof LivingEntity var5) {
         var4.setOwner(var5);
      }

      var4.setRadius(3.0F);
      var4.setRadiusOnUse(-0.5F);
      var4.setDuration(600);
      var4.setWaitTime(10);
      var4.setRadiusPerTick(-var4.getRadius() / (float)var4.getDuration());
      var4.applyComponentsFromItemStack(var2);
      var1.addFreshEntity(var4);
   }
}
