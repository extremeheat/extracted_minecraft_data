package net.minecraft.world.entity.decoration;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public abstract class HangingEntity extends Entity {
   protected static final Predicate HANGING_ENTITY = (var0) -> {
      return var0 instanceof HangingEntity;
   };
   private int checkInterval;
   protected BlockPos pos;
   protected Direction direction;

   protected HangingEntity(EntityType var1, Level var2) {
      super(var1, var2);
      this.direction = Direction.SOUTH;
   }

   protected HangingEntity(EntityType var1, Level var2, BlockPos var3) {
      this(var1, var2);
      this.pos = var3;
   }

   protected void defineSynchedData() {
   }

   protected void setDirection(Direction var1) {
      Validate.notNull(var1);
      Validate.isTrue(var1.getAxis().isHorizontal());
      this.direction = var1;
      this.yRot = (float)(this.direction.get2DDataValue() * 90);
      this.yRotO = this.yRot;
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double var1 = (double)this.pos.getX() + 0.5D;
         double var3 = (double)this.pos.getY() + 0.5D;
         double var5 = (double)this.pos.getZ() + 0.5D;
         double var7 = 0.46875D;
         double var9 = this.offs(this.getWidth());
         double var11 = this.offs(this.getHeight());
         var1 -= (double)this.direction.getStepX() * 0.46875D;
         var5 -= (double)this.direction.getStepZ() * 0.46875D;
         var3 += var11;
         Direction var13 = this.direction.getCounterClockWise();
         var1 += var9 * (double)var13.getStepX();
         var5 += var9 * (double)var13.getStepZ();
         this.setPosRaw(var1, var3, var5);
         double var14 = (double)this.getWidth();
         double var16 = (double)this.getHeight();
         double var18 = (double)this.getWidth();
         if (this.direction.getAxis() == Direction.Axis.Z) {
            var18 = 1.0D;
         } else {
            var14 = 1.0D;
         }

         var14 /= 32.0D;
         var16 /= 32.0D;
         var18 /= 32.0D;
         this.setBoundingBox(new AABB(var1 - var14, var3 - var16, var5 - var18, var1 + var14, var3 + var16, var5 + var18));
      }
   }

   private double offs(int var1) {
      return var1 % 32 == 0 ? 0.5D : 0.0D;
   }

   public void tick() {
      if (this.checkInterval++ == 100 && !this.level.isClientSide) {
         this.checkInterval = 0;
         if (!this.removed && !this.survives()) {
            this.remove();
            this.dropItem((Entity)null);
         }
      }

   }

   public boolean survives() {
      if (!this.level.noCollision(this)) {
         return false;
      } else {
         int var1 = Math.max(1, this.getWidth() / 16);
         int var2 = Math.max(1, this.getHeight() / 16);
         BlockPos var3 = this.pos.relative(this.direction.getOpposite());
         Direction var4 = this.direction.getCounterClockWise();
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(int var6 = 0; var6 < var1; ++var6) {
            for(int var7 = 0; var7 < var2; ++var7) {
               int var8 = (var1 - 1) / -2;
               int var9 = (var2 - 1) / -2;
               var5.set((Vec3i)var3).move(var4, var6 + var8).move(Direction.UP, var7 + var9);
               BlockState var10 = this.level.getBlockState(var5);
               if (!var10.getMaterial().isSolid() && !DiodeBlock.isDiode(var10)) {
                  return false;
               }
            }
         }

         return this.level.getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
      }
   }

   public boolean isPickable() {
      return true;
   }

   public boolean skipAttackInteraction(Entity var1) {
      return var1 instanceof Player ? this.hurt(DamageSource.playerAttack((Player)var1), 0.0F) : false;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         if (!this.removed && !this.level.isClientSide) {
            this.remove();
            this.markHurt();
            this.dropItem(var1.getEntity());
         }

         return true;
      }
   }

   public void move(MoverType var1, Vec3 var2) {
      if (!this.level.isClientSide && !this.removed && var2.lengthSqr() > 0.0D) {
         this.remove();
         this.dropItem((Entity)null);
      }

   }

   public void push(double var1, double var3, double var5) {
      if (!this.level.isClientSide && !this.removed && var1 * var1 + var3 * var3 + var5 * var5 > 0.0D) {
         this.remove();
         this.dropItem((Entity)null);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putByte("Facing", (byte)this.direction.get2DDataValue());
      BlockPos var2 = this.getPos();
      var1.putInt("TileX", var2.getX());
      var1.putInt("TileY", var2.getY());
      var1.putInt("TileZ", var2.getZ());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.pos = new BlockPos(var1.getInt("TileX"), var1.getInt("TileY"), var1.getInt("TileZ"));
      this.direction = Direction.from2DDataValue(var1.getByte("Facing"));
   }

   public abstract int getWidth();

   public abstract int getHeight();

   public abstract void dropItem(@Nullable Entity var1);

   public abstract void playPlacementSound();

   public ItemEntity spawnAtLocation(ItemStack var1, float var2) {
      ItemEntity var3 = new ItemEntity(this.level, this.getX() + (double)((float)this.direction.getStepX() * 0.15F), this.getY() + (double)var2, this.getZ() + (double)((float)this.direction.getStepZ() * 0.15F), var1);
      var3.setDefaultPickUpDelay();
      this.level.addFreshEntity(var3);
      return var3;
   }

   protected boolean repositionEntityAfterLoad() {
      return false;
   }

   public void setPos(double var1, double var3, double var5) {
      this.pos = new BlockPos(var1, var3, var5);
      this.recalculateBoundingBox();
      this.hasImpulse = true;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public float rotate(Rotation var1) {
      if (this.direction.getAxis() != Direction.Axis.Y) {
         switch(var1) {
         case CLOCKWISE_180:
            this.direction = this.direction.getOpposite();
            break;
         case COUNTERCLOCKWISE_90:
            this.direction = this.direction.getCounterClockWise();
            break;
         case CLOCKWISE_90:
            this.direction = this.direction.getClockWise();
         }
      }

      float var2 = Mth.wrapDegrees(this.yRot);
      switch(var1) {
      case CLOCKWISE_180:
         return var2 + 180.0F;
      case COUNTERCLOCKWISE_90:
         return var2 + 90.0F;
      case CLOCKWISE_90:
         return var2 + 270.0F;
      default:
         return var2;
      }
   }

   public float mirror(Mirror var1) {
      return this.rotate(var1.getRotation(this.direction));
   }

   public void thunderHit(LightningBolt var1) {
   }

   public void refreshDimensions() {
   }
}
