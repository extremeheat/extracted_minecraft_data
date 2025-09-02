package net.minecraft.world.entity.decoration;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public abstract class HangingEntity extends BlockAttachedEntity {
   private static final EntityDataAccessor<Direction> DATA_DIRECTION;
   private static final Direction DEFAULT_DIRECTION;

   protected HangingEntity(EntityType<? extends HangingEntity> var1, Level var2) {
      super(var1, var2);
   }

   protected HangingEntity(EntityType<? extends HangingEntity> var1, Level var2, BlockPos var3) {
      this(var1, var2);
      this.pos = var3;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_DIRECTION, DEFAULT_DIRECTION);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (var1.equals(DATA_DIRECTION)) {
         this.setDirection(this.getDirection());
      }

   }

   public Direction getDirection() {
      return (Direction)this.entityData.get(DATA_DIRECTION);
   }

   protected void setDirectionRaw(Direction var1) {
      this.entityData.set(DATA_DIRECTION, var1);
   }

   protected void setDirection(Direction var1) {
      Objects.requireNonNull(var1);
      Validate.isTrue(var1.getAxis().isHorizontal());
      this.setDirectionRaw(var1);
      this.setYRot((float)(var1.get2DDataValue() * 90));
      this.yRotO = this.getYRot();
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.getDirection() != null) {
         AABB var1 = this.calculateBoundingBox(this.pos, this.getDirection());
         Vec3 var2 = var1.getCenter();
         this.setPosRaw(var2.x, var2.y, var2.z);
         this.setBoundingBox(var1);
      }
   }

   protected abstract AABB calculateBoundingBox(BlockPos var1, Direction var2);

   public boolean survives() {
      if (!this.level().noCollision(this)) {
         return false;
      } else {
         boolean var1 = BlockPos.betweenClosedStream(this.calculateSupportBox()).allMatch((var1x) -> {
            BlockState var2 = this.level().getBlockState(var1x);
            return var2.isSolid() || DiodeBlock.isDiode(var2);
         });
         return var1 && this.canCoexist(false);
      }
   }

   protected AABB calculateSupportBox() {
      return this.getBoundingBox().move(this.getDirection().step().mul(-0.5F)).deflate(1.0E-7);
   }

   protected boolean canCoexist(boolean var1) {
      Predicate var2 = (var2x) -> {
         boolean var3 = !var1 && var2x.getType() == this.getType();
         boolean var4 = var2x.getDirection() == this.getDirection();
         return var2x != this && (var3 || var4);
      };
      return !this.level().hasEntities(EntityTypeTest.forClass(HangingEntity.class), this.getBoundingBox(), var2);
   }

   public abstract void playPlacementSound();

   public ItemEntity spawnAtLocation(ServerLevel var1, ItemStack var2, float var3) {
      ItemEntity var4 = new ItemEntity(this.level(), this.getX() + (double)((float)this.getDirection().getStepX() * 0.15F), this.getY() + (double)var3, this.getZ() + (double)((float)this.getDirection().getStepZ() * 0.15F), var2);
      var4.setDefaultPickUpDelay();
      this.level().addFreshEntity(var4);
      return var4;
   }

   public float rotate(Rotation var1) {
      Direction var2 = this.getDirection();
      if (var2.getAxis() != Direction.Axis.Y) {
         switch (var1) {
            case CLOCKWISE_180 -> var2 = var2.getOpposite();
            case COUNTERCLOCKWISE_90 -> var2 = var2.getCounterClockWise();
            case CLOCKWISE_90 -> var2 = var2.getClockWise();
         }

         this.setDirection(var2);
      }

      float var3 = Mth.wrapDegrees(this.getYRot());
      float var10000;
      switch (var1) {
         case CLOCKWISE_180 -> var10000 = var3 + 180.0F;
         case COUNTERCLOCKWISE_90 -> var10000 = var3 + 90.0F;
         case CLOCKWISE_90 -> var10000 = var3 + 270.0F;
         default -> var10000 = var3;
      }

      return var10000;
   }

   public float mirror(Mirror var1) {
      return this.rotate(var1.getRotation(this.getDirection()));
   }

   static {
      DATA_DIRECTION = SynchedEntityData.<Direction>defineId(HangingEntity.class, EntityDataSerializers.DIRECTION);
      DEFAULT_DIRECTION = Direction.SOUTH;
   }
}
