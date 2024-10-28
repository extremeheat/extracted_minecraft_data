package net.minecraft.world.entity.item;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PrimedTnt extends Entity implements TraceableEntity {
   private static final EntityDataAccessor<Integer> DATA_FUSE_ID;
   private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID;
   private static final int DEFAULT_FUSE_TIME = 80;
   private static final String TAG_BLOCK_STATE = "block_state";
   public static final String TAG_FUSE = "fuse";
   @Nullable
   private LivingEntity owner;

   public PrimedTnt(EntityType<? extends PrimedTnt> var1, Level var2) {
      super(var1, var2);
      this.blocksBuilding = true;
   }

   public PrimedTnt(Level var1, double var2, double var4, double var6, @Nullable LivingEntity var8) {
      this(EntityType.TNT, var1);
      this.setPos(var2, var4, var6);
      double var9 = var1.random.nextDouble() * 6.2831854820251465;
      this.setDeltaMovement(-Math.sin(var9) * 0.02, 0.20000000298023224, -Math.cos(var9) * 0.02);
      this.setFuse(80);
      this.xo = var2;
      this.yo = var4;
      this.zo = var6;
      this.owner = var8;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_FUSE_ID, 80);
      var1.define(DATA_BLOCK_STATE_ID, Blocks.TNT.defaultBlockState());
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   protected double getDefaultGravity() {
      return 0.04;
   }

   public void tick() {
      this.applyGravity();
      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
      if (this.onGround()) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
      }

      int var1 = this.getFuse() - 1;
      this.setFuse(var1);
      if (var1 <= 0) {
         this.discard();
         if (!this.level().isClientSide) {
            this.explode();
         }
      } else {
         this.updateInWaterStateAndDoFluidPushing();
         if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
         }
      }

   }

   private void explode() {
      float var1 = 4.0F;
      this.level().explode(this, this.getX(), this.getY(0.0625), this.getZ(), 4.0F, Level.ExplosionInteraction.TNT);
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putShort("fuse", (short)this.getFuse());
      var1.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      this.setFuse(var1.getShort("fuse"));
      if (var1.contains("block_state", 10)) {
         this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), var1.getCompound("block_state")));
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      return this.owner;
   }

   public void restoreFrom(Entity var1) {
      super.restoreFrom(var1);
      if (var1 instanceof PrimedTnt var2) {
         this.owner = var2.owner;
      }

   }

   public void setFuse(int var1) {
      this.entityData.set(DATA_FUSE_ID, var1);
   }

   public int getFuse() {
      return (Integer)this.entityData.get(DATA_FUSE_ID);
   }

   public void setBlockState(BlockState var1) {
      this.entityData.set(DATA_BLOCK_STATE_ID, var1);
   }

   public BlockState getBlockState() {
      return (BlockState)this.entityData.get(DATA_BLOCK_STATE_ID);
   }

   // $FF: synthetic method
   @Nullable
   public Entity getOwner() {
      return this.getOwner();
   }

   static {
      DATA_FUSE_ID = SynchedEntityData.defineId(PrimedTnt.class, EntityDataSerializers.INT);
      DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(PrimedTnt.class, EntityDataSerializers.BLOCK_STATE);
   }
}
