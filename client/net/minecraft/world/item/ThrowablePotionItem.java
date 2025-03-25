package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public abstract class ThrowablePotionItem extends PotionItem implements ProjectileItem {
   public static float PROJECTILE_SHOOT_POWER = 0.5F;

   public ThrowablePotionItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (var1 instanceof ServerLevel var5) {
         Projectile.spawnProjectileFromRotation(this::createPotion, var5, var4, var2, -20.0F, PROJECTILE_SHOOT_POWER, 1.0F);
      }

      var2.awardStat(Stats.ITEM_USED.get(this));
      var4.consume(1, var2);
      return InteractionResult.SUCCESS;
   }

   protected abstract AbstractThrownPotion createPotion(ServerLevel var1, LivingEntity var2, ItemStack var3);

   protected abstract AbstractThrownPotion createPotion(Level var1, Position var2, ItemStack var3);

   public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4) {
      return this.createPotion(var1, var2, var3);
   }

   public ProjectileItem.DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.builder().uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5F).power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25F).build();
   }
}
