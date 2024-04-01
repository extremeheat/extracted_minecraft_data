package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WindChargeItem extends Item {
   private static final int COOLDOWN = 10;

   public WindChargeItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      if (!var1.isClientSide()) {
         Vec3 var4 = var2.getEyePosition().add(var2.getForward().scale(0.800000011920929));
         if (!var1.getBlockState(BlockPos.containing(var4)).canBeReplaced()) {
            var4 = var2.getEyePosition().add(var2.getForward().scale(0.05000000074505806));
         }

         WindCharge var5 = new WindCharge(var2, var1, var4.x(), var4.y(), var4.z());
         var5.shootFromRotation(var2, var2.getXRot(), var2.getYRot(), 0.0F, 1.5F, 1.0F);
         var1.addFreshEntity(var5);
      }

      var1.playSound(
         null,
         var2.getX(),
         var2.getY(),
         var2.getZ(),
         SoundEvents.WIND_CHARGE_THROW,
         SoundSource.NEUTRAL,
         0.5F,
         0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      ItemStack var6 = var2.getItemInHand(var3);
      var2.getCooldowns().addCooldown(this, 10);
      var2.awardStat(Stats.ITEM_USED.get(this));
      var6.consume(1, var2);
      return InteractionResultHolder.sidedSuccess(var6, var1.isClientSide());
   }
}
