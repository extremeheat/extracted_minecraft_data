package net.minecraft.world.entity.projectile.throwableitemprojectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

public class IceBall extends ThrowableBallProjectile {
   private static final float BASE_DAMAGE = 1.395F;

   public IceBall(final EntityType<? extends IceBall> type, final Level level) {
      super(type, level);
   }

   public IceBall(final Level level, final LivingEntity mob, final ItemStack itemStack) {
      super(EntityTypes.ICE_BALL, mob, level, itemStack);
   }

   public IceBall(final Level level, final double x, final double y, final double z, final ItemStack itemStack) {
      super(EntityTypes.ICE_BALL, x, y, z, level, itemStack);
   }

   protected Item getDefaultItem() {
      return Items.ICE_BALL;
   }

   protected int getDamage(final EntityHitResult hitResult) {
      float pow = (float)this.getDeltaMovement().length();
      return Mth.ceil(Math.clamp(pow * 1.395F, 0.0F, 2.1474836E9F));
   }

   protected @Nullable SoundEvent getHitSound() {
      return SoundEvents.ICE_BALL_BREAK;
   }
}
