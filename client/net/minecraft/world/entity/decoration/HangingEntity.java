package net.minecraft.world.entity.decoration;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public abstract class HangingEntity extends BlockAttachedEntity {
   protected static final Predicate<Entity> HANGING_ENTITY = (var0) -> {
      return var0 instanceof HangingEntity;
   };
   protected Direction direction;

   protected HangingEntity(EntityType<? extends HangingEntity> var1, Level var2) {
      super(var1, var2);
      this.direction = Direction.SOUTH;
   }

   protected HangingEntity(EntityType<? extends HangingEntity> var1, Level var2, BlockPos var3) {
      this(var1, var2);
      this.pos = var3;
   }

   protected void setDirection(Direction var1) {
      Objects.requireNonNull(var1);
      Validate.isTrue(var1.getAxis().isHorizontal());
      this.direction = var1;
      this.setYRot((float)(this.direction.get2DDataValue() * 90));
      this.yRotO = this.getYRot();
      this.recalculateBoundingBox();
   }

   protected final void recalculateBoundingBox() {
      if (this.direction != null) {
         AABB var1 = this.calculateBoundingBox(this.pos, this.direction);
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
         return !var1 ? false : this.level().getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
      }
   }

   protected AABB calculateSupportBox() {
      return this.getBoundingBox().move(this.direction.step().mul(-0.5F)).deflate(1.0E-7);
   }

   public Direction getDirection() {
      return this.direction;
   }

   public abstract void playPlacementSound();

   public ItemEntity spawnAtLocation(ServerLevel var1, ItemStack var2, float var3) {
      ItemEntity var4 = new ItemEntity(this.level(), this.getX() + (double)((float)this.direction.getStepX() * 0.15F), this.getY() + (double)var3, this.getZ() + (double)((float)this.direction.getStepZ() * 0.15F), var2);
      var4.setDefaultPickUpDelay();
      this.level().addFreshEntity(var4);
      return var4;
   }

   public float rotate(Rotation var1) {
      if (this.direction.getAxis() != Direction.Axis.Y) {
         switch (var1) {
            case CLOCKWISE_180 -> this.direction = this.direction.getOpposite();
            case COUNTERCLOCKWISE_90 -> this.direction = this.direction.getCounterClockWise();
            case CLOCKWISE_90 -> this.direction = this.direction.getClockWise();
         }
      }

      float var2 = Mth.wrapDegrees(this.getYRot());
      float var10000;
      switch (var1) {
         case CLOCKWISE_180 -> var10000 = var2 + 180.0F;
         case COUNTERCLOCKWISE_90 -> var10000 = var2 + 90.0F;
         case CLOCKWISE_90 -> var10000 = var2 + 270.0F;
         default -> var10000 = var2;
      }

      return var10000;
   }

   public float mirror(Mirror var1) {
      return this.rotate(var1.getRotation(this.direction));
   }
}
