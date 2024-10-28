package net.minecraft.world.entity.decoration;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public abstract class BlockAttachedEntity extends Entity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private int checkInterval;
   protected BlockPos pos;

   protected BlockAttachedEntity(EntityType<? extends BlockAttachedEntity> var1, Level var2) {
      super(var1, var2);
   }

   protected BlockAttachedEntity(EntityType<? extends BlockAttachedEntity> var1, Level var2, BlockPos var3) {
      this(var1, var2);
      this.pos = var3;
   }

   protected abstract void recalculateBoundingBox();

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

   public abstract boolean survives();

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
         LOGGER.error("Block-attached entity at invalid position: {}", var2);
      } else {
         this.pos = var2;
      }
   }

   public abstract void dropItem(@Nullable Entity var1);

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

   public void thunderHit(ServerLevel var1, LightningBolt var2) {
   }

   public void refreshDimensions() {
   }
}
