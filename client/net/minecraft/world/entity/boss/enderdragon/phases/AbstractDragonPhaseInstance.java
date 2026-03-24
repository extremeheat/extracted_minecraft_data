package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class AbstractDragonPhaseInstance implements DragonPhaseInstance {
   protected final EnderDragon dragon;

   public AbstractDragonPhaseInstance(final EnderDragon dragon) {
      super();
      this.dragon = dragon;
   }

   public boolean isSitting() {
      return false;
   }

   public void doClientTick() {
   }

   public void doServerTick(final ServerLevel level) {
   }

   public void onCrystalDestroyed(final EndCrystal crystal, final BlockPos pos, final DamageSource source, final @Nullable Player player) {
   }

   public void begin() {
   }

   public void end() {
   }

   public float getFlySpeed() {
      return 0.6F;
   }

   public @Nullable Vec3 getFlyTargetLocation() {
      return null;
   }

   public float onHurt(final DamageSource source, final float damage) {
      return damage;
   }

   public float getTurnSpeed() {
      float rotSpeed = (float)this.dragon.getDeltaMovement().horizontalDistance() + 1.0F;
      float dist = Math.min(rotSpeed, 40.0F);
      return 0.7F / dist / rotSpeed;
   }
}
