package net.minecraft.world.entity.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.DimensionTransition;

public class PrimedTnt extends Entity implements TraceableEntity {
   private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(PrimedTnt.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(PrimedTnt.class, EntityDataSerializers.BLOCK_STATE);
   private static final int DEFAULT_FUSE_TIME = 80;
   private static final float DEFAULT_EXPLOSION_POWER = 4.0F;
   private static final String TAG_BLOCK_STATE = "block_state";
   private static final String TAG_FUSE = "fuse";
   private static final String TAG_EXPLOSION_POWER = "explosion_power";
   private static final ExplosionDamageCalculator USED_PORTAL_DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {
      @Override
      public boolean shouldBlockExplode(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, float var5) {
         return var4.is(Blocks.NETHER_PORTAL) ? false : super.shouldBlockExplode(var1, var2, var3, var4, var5);
      }

      @Override
      public Optional<Float> getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5) {
         return var4.is(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlockExplosionResistance(var1, var2, var3, var4, var5);
      }
   };
   @Nullable
   private LivingEntity owner;
   private boolean usedPortal;
   private float explosionPower = 4.0F;

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

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_FUSE_ID, 80);
      var1.define(DATA_BLOCK_STATE_ID, Blocks.TNT.defaultBlockState());
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   @Override
   public boolean isPickable() {
      return !this.isRemoved();
   }

   @Override
   protected double getDefaultGravity() {
      return 0.04;
   }

   @Override
   public void tick() {
      this.handlePortal();
      this.applyGravity();
      this.move(MoverType.SELF, this.getDeltaMovement());
      this.applyEffectsFromBlocks();
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
      this.level()
         .explode(
            this,
            Explosion.getDefaultDamageSource(this.level(), this),
            this.usedPortal ? USED_PORTAL_DAMAGE_CALCULATOR : null,
            this.getX(),
            this.getY(0.0625),
            this.getZ(),
            this.explosionPower,
            false,
            Level.ExplosionInteraction.TNT
         );
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putShort("fuse", (short)this.getFuse());
      var1.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
      if (this.explosionPower != 4.0F) {
         var1.putFloat("explosion_power", this.explosionPower);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      this.setFuse(var1.getShort("fuse"));
      if (var1.contains("block_state", 10)) {
         this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), var1.getCompound("block_state")));
      }

      if (var1.contains("explosion_power", 99)) {
         this.explosionPower = Mth.clamp(var1.getFloat("explosion_power"), 0.0F, 128.0F);
      }
   }

   @Nullable
   public LivingEntity getOwner() {
      return this.owner;
   }

   @Override
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
      return this.entityData.get(DATA_FUSE_ID);
   }

   public void setBlockState(BlockState var1) {
      this.entityData.set(DATA_BLOCK_STATE_ID, var1);
   }

   public BlockState getBlockState() {
      return this.entityData.get(DATA_BLOCK_STATE_ID);
   }

   private void setUsedPortal(boolean var1) {
      this.usedPortal = var1;
   }

   @Nullable
   @Override
   public Entity changeDimension(DimensionTransition var1) {
      Entity var2 = super.changeDimension(var1);
      if (var2 instanceof PrimedTnt var3) {
         var3.setUsedPortal(true);
      }

      return var2;
   }

   @Override
   public final boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      return false;
   }
}
