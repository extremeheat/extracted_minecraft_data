package net.minecraft.world.entity.decoration;

import com.mojang.logging.LogUtils;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MoverType;
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
import org.slf4j.Logger;

public abstract class HangingEntity extends Entity {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected static final Predicate<Entity> HANGING_ENTITY = (var0) -> {
      return var0 instanceof HangingEntity;
   };
   private int checkInterval;
   protected BlockPos pos;
   protected Direction direction;

   protected HangingEntity(EntityType<? extends HangingEntity> var1, Level var2) {
      super(var1, var2);
      this.direction = Direction.SOUTH;
   }

   protected HangingEntity(EntityType<? extends HangingEntity> var1, Level var2, BlockPos var3) {
      this(var1, var2);
      this.pos = var3;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
   }

   protected void setDirection(Direction var1) {
      Validate.notNull(var1);
      Validate.isTrue(var1.getAxis().isHorizontal());
      this.direction = var1;
      this.setYRot((float)(this.direction.get2DDataValue() * 90));
      this.yRotO = this.getYRot();
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double var1 = (double)this.pos.getX() + 0.5;
         double var3 = (double)this.pos.getY() + 0.5;
         double var5 = (double)this.pos.getZ() + 0.5;
         double var7 = 0.46875;
         double var9 = this.offs(this.getWidth());
         double var11 = this.offs(this.getHeight());
         var1 -= (double)this.direction.getStepX() * 0.46875;
         var5 -= (double)this.direction.getStepZ() * 0.46875;
         var3 += var11;
         Direction var13 = this.direction.getCounterClockWise();
         var1 += var9 * (double)var13.getStepX();
         var5 += var9 * (double)var13.getStepZ();
         this.setPosRaw(var1, var3, var5);
         double var14 = (double)this.getWidth();
         double var16 = (double)this.getHeight();
         double var18 = (double)this.getWidth();
         if (this.direction.getAxis() == Direction.Axis.Z) {
            var18 = 1.0;
         } else {
            var14 = 1.0;
         }

         var14 /= 32.0;
         var16 /= 32.0;
         var18 /= 32.0;
         this.setBoundingBox(new AABB(var1 - var14, var3 - var16, var5 - var18, var1 + var14, var3 + var16, var5 + var18));
      }
   }

   private double offs(int var1) {
      return var1 % 32 == 0 ? 0.5 : 0.0;
   }

   public void tick() {
      if (!this.level().isClientSide) {
         this.checkBelowWorld();
         if (this.checkInterval++ == 100) {
            this.checkInterval = 0;
            if (!this.isRemoved() && !this.survives()) {
               this.discard();
               this.dropItem((Entity)null);
            }
         }
      }

   }

   public boolean survives() {
      if (!this.level().noCollision(this)) {
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
               var5.set(var3).move(var4, var6 + var8).move(Direction.UP, var7 + var9);
               BlockState var10 = this.level().getBlockState(var5);
               if (!var10.isSolid() && !DiodeBlock.isDiode(var10)) {
                  return false;
               }
            }
         }

         return this.level().getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
      }
   }

   public boolean isPickable() {
      return true;
   }

   public boolean skipAttackInteraction(Entity var1) {
      if (var1 instanceof Player var2) {
         return !this.level().mayInteract(var2, this.pos) ? true : this.hurt(this.damageSources().playerAttack(var2), 0.0F);
      } else {
         return false;
      }
   }

   public Direction getDirection() {
      return this.direction;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         if (!this.isRemoved() && !this.level().isClientSide) {
            this.kill();
            this.markHurt();
            this.dropItem(var1.getEntity());
         }

         return true;
      }
   }

   public void move(MoverType var1, Vec3 var2) {
      if (!this.level().isClientSide && !this.isRemoved() && var2.lengthSqr() > 0.0) {
         this.kill();
         this.dropItem((Entity)null);
      }

   }

   public void push(double var1, double var3, double var5) {
      if (!this.level().isClientSide && !this.isRemoved() && var1 * var1 + var3 * var3 + var5 * var5 > 0.0) {
         this.kill();
         this.dropItem((Entity)null);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      BlockPos var2 = this.getPos();
      var1.putInt("TileX", var2.getX());
      var1.putInt("TileY", var2.getY());
      var1.putInt("TileZ", var2.getZ());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      BlockPos var2 = new BlockPos(var1.getInt("TileX"), var1.getInt("TileY"), var1.getInt("TileZ"));
      if (!var2.closerThan(this.blockPosition(), 16.0)) {
         LOGGER.error("Hanging entity at invalid position: {}", var2);
      } else {
         this.pos = var2;
      }
   }

   public abstract int getWidth();

   public abstract int getHeight();

   public abstract void dropItem(@Nullable Entity var1);

   public abstract void playPlacementSound();

   public ItemEntity spawnAtLocation(ItemStack var1, float var2) {
      ItemEntity var3 = new ItemEntity(this.level(), this.getX() + (double)((float)this.direction.getStepX() * 0.15F), this.getY() + (double)var2, this.getZ() + (double)((float)this.direction.getStepZ() * 0.15F), var1);
      var3.setDefaultPickUpDelay();
      this.level().addFreshEntity(var3);
      return var3;
   }

   protected boolean repositionEntityAfterLoad() {
      return false;
   }

   public void setPos(double var1, double var3, double var5) {
      this.pos = BlockPos.containing(var1, var3, var5);
      this.recalculateBoundingBox();
      this.hasImpulse = true;
   }

   public BlockPos getPos() {
      return this.pos;
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
      switch (var1) {
         case CLOCKWISE_180 -> {
            return var2 + 180.0F;
         }
         case COUNTERCLOCKWISE_90 -> {
            return var2 + 90.0F;
         }
         case CLOCKWISE_90 -> {
            return var2 + 270.0F;
         }
         default -> {
            return var2;
         }
      }
   }

   public float mirror(Mirror var1) {
      return this.rotate(var1.getRotation(this.direction));
   }

   public void thunderHit(ServerLevel var1, LightningBolt var2) {
   }

   public void refreshDimensions() {
   }
}
