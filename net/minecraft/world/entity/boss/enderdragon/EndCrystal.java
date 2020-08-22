package net.minecraft.world.entity.boss.enderdragon;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.dimension.end.TheEndDimension;

public class EndCrystal extends Entity {
   private static final EntityDataAccessor DATA_BEAM_TARGET;
   private static final EntityDataAccessor DATA_SHOW_BOTTOM;
   public int time;

   public EndCrystal(EntityType var1, Level var2) {
      super(var1, var2);
      this.blocksBuilding = true;
      this.time = this.random.nextInt(100000);
   }

   public EndCrystal(Level var1, double var2, double var4, double var6) {
      this(EntityType.END_CRYSTAL, var1);
      this.setPos(var2, var4, var6);
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_BEAM_TARGET, Optional.empty());
      this.getEntityData().define(DATA_SHOW_BOTTOM, true);
   }

   public void tick() {
      ++this.time;
      if (!this.level.isClientSide) {
         BlockPos var1 = new BlockPos(this);
         if (this.level.dimension instanceof TheEndDimension && this.level.getBlockState(var1).isAir()) {
            this.level.setBlockAndUpdate(var1, Blocks.FIRE.defaultBlockState());
         }
      }

   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      if (this.getBeamTarget() != null) {
         var1.put("BeamTarget", NbtUtils.writeBlockPos(this.getBeamTarget()));
      }

      var1.putBoolean("ShowBottom", this.showsBottom());
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.contains("BeamTarget", 10)) {
         this.setBeamTarget(NbtUtils.readBlockPos(var1.getCompound("BeamTarget")));
      }

      if (var1.contains("ShowBottom", 1)) {
         this.setShowBottom(var1.getBoolean("ShowBottom"));
      }

   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (var1.getEntity() instanceof EnderDragon) {
         return false;
      } else {
         if (!this.removed && !this.level.isClientSide) {
            this.remove();
            if (!var1.isExplosion()) {
               this.level.explode((Entity)null, this.getX(), this.getY(), this.getZ(), 6.0F, Explosion.BlockInteraction.DESTROY);
            }

            this.onDestroyedBy(var1);
         }

         return true;
      }
   }

   public void kill() {
      this.onDestroyedBy(DamageSource.GENERIC);
      super.kill();
   }

   private void onDestroyedBy(DamageSource var1) {
      if (this.level.dimension instanceof TheEndDimension) {
         TheEndDimension var2 = (TheEndDimension)this.level.dimension;
         EndDragonFight var3 = var2.getDragonFight();
         if (var3 != null) {
            var3.onCrystalDestroyed(this, var1);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos var1) {
      this.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(var1));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return (BlockPos)((Optional)this.getEntityData().get(DATA_BEAM_TARGET)).orElse((Object)null);
   }

   public void setShowBottom(boolean var1) {
      this.getEntityData().set(DATA_SHOW_BOTTOM, var1);
   }

   public boolean showsBottom() {
      return (Boolean)this.getEntityData().get(DATA_SHOW_BOTTOM);
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      return super.shouldRenderAtSqrDistance(var1) || this.getBeamTarget() != null;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   static {
      DATA_BEAM_TARGET = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
      DATA_SHOW_BOTTOM = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.BOOLEAN);
   }
}
