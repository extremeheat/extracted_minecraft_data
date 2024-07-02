package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class WindChargeItem extends Item implements ProjectileItem {
   private static final int COOLDOWN = 10;

   public WindChargeItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      if (!var1.isClientSide()) {
         WindCharge var4 = new WindCharge(var2, var1, var2.position().x(), var2.getEyePosition().y(), var2.position().z());
         var4.shootFromRotation(var2, var2.getXRot(), var2.getYRot(), 0.0F, 1.5F, 1.0F);
         var1.addFreshEntity(var4);
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
      ItemStack var5 = var2.getItemInHand(var3);
      var2.getCooldowns().addCooldown(this, 10);
      var2.awardStat(Stats.ITEM_USED.get(this));
      var5.consume(1, var2);
      return InteractionResultHolder.sidedSuccess(var5, var1.isClientSide());
   }

   @Override
   public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4) {
      RandomSource var5 = var1.getRandom();
      double var6 = var5.triangle((double)var4.getStepX(), 0.11485000000000001);
      double var8 = var5.triangle((double)var4.getStepY(), 0.11485000000000001);
      double var10 = var5.triangle((double)var4.getStepZ(), 0.11485000000000001);
      Vec3 var12 = new Vec3(var6, var8, var10);
      WindCharge var13 = new WindCharge(var1, var2.x(), var2.y(), var2.z(), var12);
      var13.setDeltaMovement(var12);
      return var13;
   }

   @Override
   public void shoot(Projectile var1, double var2, double var4, double var6, float var8, float var9) {
   }

   @Override
   public ProjectileItem.DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.builder()
         .positionFunction((var0, var1) -> DispenserBlock.getDispensePosition(var0, 1.0, Vec3.ZERO))
         .uncertainty(6.6666665F)
         .power(1.0F)
         .overrideDispenseEvent(1051)
         .build();
   }
}
