package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;

public class EnderpearlItem extends Item {
   public EnderpearlItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var1.playSound(
         null,
         var2.getX(),
         var2.getY(),
         var2.getZ(),
         SoundEvents.ENDER_PEARL_THROW,
         SoundSource.NEUTRAL,
         0.5F,
         0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      if (var1 instanceof ServerLevel var5) {
         Projectile.spawnProjectileFromRotation(ThrownEnderpearl::new, var5, var4, var2, 0.0F, 1.5F, 1.0F);
      }

      var2.awardStat(Stats.ITEM_USED.get(this));
      var4.consume(1, var2);
      return InteractionResult.SUCCESS;
   }
}
