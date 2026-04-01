package net.minecraft.world.entity.livingblock.movement;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BouncingMovement implements MovementStrategy<Data> {
   private static final Vec3 POGO_ANIM_SCALE_DEFAULT = new Vec3(1.0, 1.0, 1.0);
   private static final Vec3 POGO_ANIM_SCALE_SQUISH = new Vec3(1.5, 0.8, 1.5);
   private static final Vec3 POGO_ANIM_SCALE_STRETCH = new Vec3(0.8, 1.35, 0.8);
   private static final int POGO_ANIM_TICKS = 2;
   private final double jumpHeight;
   private final int ticksBetweenJumps;
   private final double horizontalMovementSpeed;

   public BouncingMovement() {
      this(1.5, 5, 0.45);
   }

   public BouncingMovement(final double jumpHeight, final int ticksBetweenJumps, final double horizontalMovementSpeed) {
      super();
      this.jumpHeight = jumpHeight;
      this.ticksBetweenJumps = ticksBetweenJumps;
      this.horizontalMovementSpeed = horizontalMovementSpeed;
   }

   public Data initData() {
      Data data = new Data();
      data.jumpCooldown = this.ticksBetweenJumps;
      return data;
   }

   public boolean moveTowardsTarget(final LivingBlock entity, final Target target, final Vec3 targetPos) {
      Data data = (Data)this.getData(entity);
      if (data.stretchCooldown > 0) {
         --data.stretchCooldown;
         if (data.stretchCooldown == 0) {
            entity.setPogoScaleTarget(POGO_ANIM_SCALE_DEFAULT, 5);
         }
      }

      Vec3 pos = entity.position();
      Vec3 delta = targetPos.subtract(pos);
      boolean needsToMoveCloser = delta.horizontalDistanceSqr() >= Mth.square(target.distance());
      boolean readyToJumpAgain = !data.isMidAir && needsToMoveCloser;
      if (entity.isInLiquid() && delta.y() > 0.0) {
         WaterFloatingMovement.moveInWater(entity, target, targetPos);
         return true;
      } else if (data.jumpCooldown > 0) {
         --data.jumpCooldown;
         if (data.jumpCooldown < 2 && readyToJumpAgain) {
            entity.setPogoScaleTarget(POGO_ANIM_SCALE_SQUISH, 2);
         }

         if (data.jumpCooldown == 0 && !readyToJumpAgain) {
            data.jumpCooldown = this.ticksBetweenJumps;
            return false;
         } else {
            return true;
         }
      } else if (readyToJumpAgain) {
         data.movingTo = targetPos;
         data.isMidAir = true;
         Vec3 horizontalDelta = new Vec3(delta.x, 0.0, delta.z);
         double horizontalDistance = horizontalDelta.length();
         Vec3 horizontalDirection = horizontalDistance > 9.999999747378752E-6 ? horizontalDelta.scale(1.0 / horizontalDistance) : Vec3.ZERO;
         double gravity = entity.getGravity();
         double jumpInitialVelocity = Math.sqrt(2.0 * this.jumpHeight * gravity);
         double slowRadius = 4.0;
         double minHorizontalSpeed = 0.0;
         double speedScale = Mth.clamp(horizontalDistance / 4.0, 0.0, 1.0);
         double adjustedHorizontalSpeed = Mth.lerp(speedScale, 0.0, this.horizontalMovementSpeed);
         double xd = horizontalDirection.x * adjustedHorizontalSpeed;
         double zd = horizontalDirection.z * adjustedHorizontalSpeed;
         entity.setMaxUpStep((float)this.jumpHeight);
         entity.addDeltaMovement(new Vec3(xd, jumpInitialVelocity, zd));
         entity.setPogoScaleTarget(POGO_ANIM_SCALE_STRETCH, 2);
         data.stretchCooldown = 4;
         return true;
      } else if (!data.isMidAir) {
         this.resetMovement(entity);
         return false;
      } else if (entity.onGround()) {
         this.resetMovement(entity);
         data.jumpCooldown = this.ticksBetweenJumps;
         return true;
      } else {
         return true;
      }
   }

   public void resetMovement(final LivingBlock entity) {
      Data data = (Data)this.getData(entity);
      data.isMidAir = false;
      data.movingTo = null;
      entity.setPogoScaleTarget(POGO_ANIM_SCALE_DEFAULT, 2);
      entity.resetMaxUpStep();
      if (entity.onGround()) {
         MovementStrategy.resetVelocity(entity, false);
      }

   }

   public static class Data implements MovementData {
      public static final StreamCodec<ByteBuf, Data> STREAM_CODEC;
      public boolean isMidAir;
      public int jumpCooldown;
      public @Nullable Vec3 movingTo;
      public int stretchCooldown;

      public Data(final boolean isMidAir, final int jumpCooldown, final @Nullable Vec3 movingTo, final int stretchCooldown) {
         super();
         this.isMidAir = isMidAir;
         this.jumpCooldown = jumpCooldown;
         this.movingTo = movingTo;
         this.stretchCooldown = stretchCooldown;
      }

      public Data() {
         this(false, 0, (Vec3)null, 0);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, (d) -> d.isMidAir, ByteBufCodecs.INT, (d) -> d.jumpCooldown, Vec3.STREAM_CODEC.apply(ByteBufCodecs::optional), (d) -> Optional.ofNullable(d.movingTo), ByteBufCodecs.INT, (d) -> d.stretchCooldown, (isMidAir, jumpCooldown, movingTo, stretchCooldown) -> new Data(isMidAir, jumpCooldown, (Vec3)movingTo.orElse((Object)null), stretchCooldown));
      }
   }
}
