package net.minecraft.client.renderer.item;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CompassItemPropertyFunction implements ClampedItemPropertyFunction {
   public static final int DEFAULT_ROTATION = 0;
   private final CompassItemPropertyFunction.CompassWobble wobble = new CompassItemPropertyFunction.CompassWobble();
   private final CompassItemPropertyFunction.CompassWobble wobbleRandom = new CompassItemPropertyFunction.CompassWobble();
   public final CompassItemPropertyFunction.CompassTarget compassTarget;

   public CompassItemPropertyFunction(CompassItemPropertyFunction.CompassTarget var1) {
      super();
      this.compassTarget = var1;
   }

   @Override
   public float unclampedCall(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      Object var5 = var3 != null ? var3 : var1.getEntityRepresentation();
      if (var5 == null) {
         return 0.0F;
      } else {
         var2 = this.tryFetchLevelIfMissing((Entity)var5, var2);
         return var2 == null ? 0.0F : this.getCompassRotation(var1, var2, var4, (Entity)var5);
      }
   }

   private float getCompassRotation(ItemStack var1, ClientLevel var2, int var3, Entity var4) {
      GlobalPos var5 = this.compassTarget.getPos(var2, var1, var4);
      long var6 = var2.getGameTime();
      return !this.isValidCompassTargetPos(var4, var5)
         ? this.getRandomlySpinningRotation(var3, var6)
         : this.getRotationTowardsCompassTarget(var4, var6, var5.pos());
   }

   private float getRandomlySpinningRotation(int var1, long var2) {
      if (this.wobbleRandom.shouldUpdate(var2)) {
         this.wobbleRandom.update(var2, Math.random());
      }

      double var4 = this.wobbleRandom.rotation + (double)((float)this.hash(var1) / 2.1474836E9F);
      return Mth.positiveModulo((float)var4, 1.0F);
   }

   private float getRotationTowardsCompassTarget(Entity var1, long var2, BlockPos var4) {
      double var5 = this.getAngleFromEntityToPos(var1, var4);
      double var7 = this.getWrappedVisualRotationY(var1);
      if (var1 instanceof Player var11 && var11.isLocalPlayer() && var11.level().tickRateManager().runsNormally()) {
         if (this.wobble.shouldUpdate(var2)) {
            this.wobble.update(var2, 0.5 - (var7 - 0.25));
         }

         double var12 = var5 + this.wobble.rotation;
         return Mth.positiveModulo((float)var12, 1.0F);
      }

      double var9 = 0.5 - (var7 - 0.25 - var5);
      return Mth.positiveModulo((float)var9, 1.0F);
   }

   @Nullable
   private ClientLevel tryFetchLevelIfMissing(Entity var1, @Nullable ClientLevel var2) {
      return var2 == null && var1.level() instanceof ClientLevel ? (ClientLevel)var1.level() : var2;
   }

   private boolean isValidCompassTargetPos(Entity var1, @Nullable GlobalPos var2) {
      return var2 != null && var2.dimension() == var1.level().dimension() && !(var2.pos().distToCenterSqr(var1.position()) < 9.999999747378752E-6);
   }

   private double getAngleFromEntityToPos(Entity var1, BlockPos var2) {
      Vec3 var3 = Vec3.atCenterOf(var2);
      return Math.atan2(var3.z() - var1.getZ(), var3.x() - var1.getX()) / 6.2831854820251465;
   }

   private double getWrappedVisualRotationY(Entity var1) {
      return Mth.positiveModulo((double)(var1.getVisualRotationYInDegrees() / 360.0F), 1.0);
   }

   private int hash(int var1) {
      return var1 * 1327217883;
   }

   public interface CompassTarget {
      @Nullable
      GlobalPos getPos(ClientLevel var1, ItemStack var2, Entity var3);
   }

   static class CompassWobble {
      double rotation;
      private double deltaRotation;
      private long lastUpdateTick;

      CompassWobble() {
         super();
      }

      boolean shouldUpdate(long var1) {
         return this.lastUpdateTick != var1;
      }

      void update(long var1, double var3) {
         this.lastUpdateTick = var1;
         double var5 = var3 - this.rotation;
         var5 = Mth.positiveModulo(var5 + 0.5, 1.0) - 0.5;
         this.deltaRotation += var5 * 0.1;
         this.deltaRotation *= 0.8;
         this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0);
      }
   }
}
