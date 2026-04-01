package net.minecraft.world.entity.livingblock.movement;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RollingMovement implements MovementStrategy<Data> {
   private static final int MOVE_ATTEMPTS_UNTIL_NOT_INTERESTED = 10;
   private static final int TICKS_BEFORE_MOVING_AFTER_FAIL = 6;
   private static final int FORGET_MOVE_TARGET_CHESSBOARD_DISTANCE = 1;

   public RollingMovement() {
      super();
   }

   public Data initData() {
      return new Data();
   }

   public boolean moveTowardsTarget(final LivingBlock entity, final Target target, final Vec3 targetPos) {
      if (this.checkIsMovementDisabled(entity)) {
         return true;
      } else {
         Data data = (Data)this.getData(entity);
         if (data.moveTicks == 0) {
            Direction continueInDirection = null;
            if (entity.isClimbing()) {
               if (!entity.horizontalCollision) {
                  entity.resetClimbingDirection();
               } else {
                  continueInDirection = entity.getClimbingDirection();
               }
            }

            boolean ableToMove;
            if (continueInDirection != null) {
               ableToMove = this.tryFindNextMoveStep(entity, continueInDirection);
            } else {
               Vec3 pos = entity.position();
               Vec3 delta = targetPos.subtract(pos);
               double minDistanceSq = Mth.square(target.distance());
               if (delta.horizontalDistanceSqr() < minDistanceSq) {
                  return false;
               }

               BlockPos targetBlockPos = BlockPos.containing(targetPos);
               Vec3 roundedDelta = targetBlockPos.getBottomCenter().subtract(pos);
               if (roundedDelta.horizontalDistanceSqr() < minDistanceSq) {
                  return false;
               }

               Direction direction = Direction.getApproximateNearest(delta.horizontal());
               ableToMove = this.tryFindNextMoveStep(entity, direction);
            }

            if (!ableToMove) {
               if (data.failedMoveAttempts >= 10) {
                  return false;
               }

               data.moveTicks = 6;
               entity.setDeltaMovement(0.0, entity.getDeltaMovement().y, 0.0);
               return true;
            }

            data.moveTicks = entity.getRandom().nextIntBetweenInclusive(5, 9);
            entity.needsSync = true;
         }

         if (data.movingTo != null && !entity.level().isClientSide()) {
            double scale = 1.0 / Math.pow((double)data.moveTicks, 1.5);
            data.movingTo.applyMovement(entity, scale);
         }

         --data.moveTicks;
         return true;
      }
   }

   public boolean normalStepSounds() {
      return false;
   }

   public void resetMovement(final LivingBlock entity) {
      boolean wasClimbing = entity.isClimbing();
      Data data = (Data)this.getData(entity);
      data.movingTo = null;
      data.moveTicks = 0;
      data.failedMoveAttempts = 0;
      entity.resetMaxUpStep();
      entity.resetClimbingDirection();
      if (wasClimbing || entity.onGround()) {
         MovementStrategy.resetVelocity(entity, wasClimbing);
      }

   }

   public Vec3 adjustStepUpMovement(final LivingBlock entity, final Vec3 movement) {
      Data data = (Data)this.getData(entity);
      if (data.movingTo != null && data.movingTo.type == RollingMovement.MoveType.STEP_UP) {
         Direction stepUpDirection = data.movingTo.direction;
         double amount = stepUpDirection.getAxis().choose(movement.x, movement.y, movement.z);
         return Mth.sign(amount) != stepUpDirection.getAxisDirection().getStep() ? Vec3.ZERO : stepUpDirection.getUnitVec3().scale(Math.abs(amount));
      } else {
         return Vec3.ZERO;
      }
   }

   private boolean tryFindNextMoveStep(final LivingBlock entity, final Direction direction) {
      Vec3 minimumMove = direction.getUnitVec3().scale(0.1);
      AABB nextPosBounds = entity.getBoundingBox().expandTowards(minimumMove).deflate(1.0E-6);
      BlockPos blockPos = entity.onGround() ? BlockPos.containing(entity.getBoundingBox().getCenter()) : entity.blockPosition();
      BlockPos nextBlockPos = blockPos.relative(direction);
      boolean isClimbing = entity.isClimbing();
      if (!isClimbing && entity.level().noCollision(entity, nextPosBounds)) {
         this.setMoveStep(entity, RollingMovement.MoveType.STEP_UP, nextBlockPos, direction);
         return true;
      } else {
         Data data = (Data)this.getData(entity);
         if (!this.isAnyEntityStandingOn(entity)) {
            AABB onTopBounds = nextPosBounds.move(0.0, 1.0, 0.0);
            CollisionGetter.CollisionSource collidedWith = entity.level().getCollisionSource(entity, onTopBounds);
            if (collidedWith == CollisionGetter.CollisionSource.NONE && entity.onGround()) {
               this.setMoveStep(entity, RollingMovement.MoveType.STEP_UP, nextBlockPos.above(), direction);
               return true;
            }

            if (collidedWith == CollisionGetter.CollisionSource.ENTITY && !isClimbing) {
               ++data.failedMoveAttempts;
            } else {
               AABB directlyAboveBounds = entity.getBoundingBox().deflate(1.0E-6).move(0.0, 2.0E-6, 0.0);
               if (entity.level().noBlockCollision(entity, directlyAboveBounds)) {
                  this.setMoveStep(entity, RollingMovement.MoveType.CLIMB, nextBlockPos.above(), direction);
                  return true;
               }

               data.failedMoveAttempts = 10;
            }
         } else {
            if (isClimbing) {
               this.setMoveStep(entity, RollingMovement.MoveType.IDLE_CLING, nextBlockPos, direction);
               return true;
            }

            entity.resetClimbingDirection();
         }

         data.movingTo = null;
         entity.resetMaxUpStep();
         return false;
      }
   }

   private void setMoveStep(final LivingBlock entity, final MoveType moveType, final BlockPos movingToBlock, final Direction direction) {
      this.setMoveStep(entity, moveType, movingToBlock.getBottomCenter(), direction);
   }

   private void setMoveStep(final LivingBlock entity, final MoveType moveType, final Vec3 movingToPos, final Direction direction) {
      Data data = (Data)this.getData(entity);
      data.movingTo = new MoveStep(moveType, movingToPos, direction);
      data.movingTo.setOn(entity);
      data.failedMoveAttempts = 0;
   }

   private boolean checkIsMovementDisabled(final LivingBlock entity) {
      Data data = (Data)this.getData(entity);
      if (!entity.onGround() && !entity.isClimbing()) {
         if (data.movingTo != null) {
            Vec3 movement = entity.getDeltaMovement();
            if (movement.y <= 0.0 && movement.y >= -entity.getGravity() * 2.0) {
               entity.setDeltaMovement(0.0, movement.y, 0.0);
               data.moveTicks = 0;
            }
         }

         data.movingTo = null;
         data.failedMoveAttempts = 0;
         return true;
      } else if (data.movingTo == null && data.moveTicks > 0) {
         --data.moveTicks;
         return true;
      } else {
         if (data.movingTo != null && entity.blockPosition().distChessboard(BlockPos.containing(data.movingTo.position)) > 1) {
            data.moveTicks = 6;
            data.movingTo = null;
         }

         return false;
      }
   }

   private boolean isAnyEntityStandingOn(final LivingBlock entity) {
      AABB aabb = entity.getBoundingBox();
      AABB onTop = (new AABB(aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY + 1.0, aabb.maxZ)).deflate(1.0E-6);
      return entity.level().hasEntities(EntityTypeTest.forClass(Entity.class), onTop, (other) -> other != entity && other.onGround() && (!(other instanceof LivingBlock) || entity.canCollideWith(other)));
   }

   public static class Data implements MovementData {
      public static final StreamCodec<ByteBuf, Data> STREAM_CODEC;
      public @Nullable MoveStep movingTo;
      public int moveTicks;
      public int failedMoveAttempts;

      public Data(final @Nullable MoveStep movingTo, final int moveTicks, final int failedMoveAttempts) {
         super();
         this.movingTo = movingTo;
         this.moveTicks = moveTicks;
         this.failedMoveAttempts = failedMoveAttempts;
      }

      public Data() {
         this((MoveStep)null, 0, 0);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(RollingMovement.MoveStep.STREAM_CODEC.apply(ByteBufCodecs::optional), (d) -> Optional.ofNullable(d.movingTo), ByteBufCodecs.INT, (d) -> d.moveTicks, ByteBufCodecs.INT, (d) -> d.failedMoveAttempts, (movingTo, moveTicks, failedMoveAttempts) -> new Data((MoveStep)movingTo.orElse((Object)null), moveTicks, failedMoveAttempts));
      }
   }

   public static record MoveStep(MoveType type, Vec3 position, Direction direction) {
      public static final StreamCodec<ByteBuf, MoveStep> STREAM_CODEC;

      public MoveStep {
         super();
      }

      public void setOn(final LivingBlock entity) {
         if (this.type.isAttachedVertically()) {
            entity.setClimbingDirection(this.direction);
            entity.setMaxUpStep(0.0F);
            entity.setDeltaMovement(0.0, entity.getDeltaMovement().y, 0.0);
         } else {
            entity.resetClimbingDirection();
            if (this.type == RollingMovement.MoveType.STEP_UP) {
               entity.setMaxUpStep(1.0F);
            } else {
               entity.resetMaxUpStep();
            }
         }

      }

      public void applyMovement(final LivingBlock entity, final double scale) {
         Vec3 predictedPos = entity.position().add(entity.getDeltaMovement());
         Vec3 delta = this.position.subtract(predictedPos);
         if (this.type.isAttachedVertically()) {
            AABB aabb = entity.getBoundingBox().deflate(1.0E-6).move(this.direction.getUnitVec3().scale(2.0E-6));
            if (!entity.level().noCollision(entity, aabb)) {
               Vec3 stickingForce = this.direction.getUnitVec3().scale(0.1);
               if (this.type == RollingMovement.MoveType.CLIMB) {
                  entity.addDeltaMovement(stickingForce.add(0.0, delta.y * scale, 0.0));
               } else {
                  entity.addDeltaMovement(stickingForce);
               }

               return;
            }
         }

         entity.addDeltaMovement(new Vec3(delta.x * scale, 0.0, delta.z * scale));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(RollingMovement.MoveType.STREAM_CODEC, MoveStep::type, Vec3.STREAM_CODEC, MoveStep::position, Direction.STREAM_CODEC, MoveStep::direction, MoveStep::new);
      }
   }

   private static enum MoveType {
      DEFAULT,
      STEP_UP,
      CLIMB,
      IDLE_CLING;

      public static final StreamCodec<ByteBuf, MoveType> STREAM_CODEC = ByteBufCodecs.idMapper((index) -> values()[index], Enum::ordinal);

      private MoveType() {
      }

      public boolean isAttachedVertically() {
         return this == CLIMB || this == IDLE_CLING;
      }

      // $FF: synthetic method
      private static MoveType[] $values() {
         return new MoveType[]{DEFAULT, STEP_UP, CLIMB, IDLE_CLING};
      }
   }
}
