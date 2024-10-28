package net.minecraft.world.entity.boss.enderdragon;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.dimension.end.EndDragonFight;

public class EndCrystal extends Entity {
   private static final EntityDataAccessor<Optional<BlockPos>> DATA_BEAM_TARGET;
   private static final EntityDataAccessor<Boolean> DATA_SHOW_BOTTOM;
   public int time;

   public EndCrystal(EntityType<? extends EndCrystal> var1, Level var2) {
      super(var1, var2);
      this.blocksBuilding = true;
      this.time = this.random.nextInt(100000);
   }

   public EndCrystal(Level var1, double var2, double var4, double var6) {
      this(EntityType.END_CRYSTAL, var1);
      this.setPos(var2, var4, var6);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_BEAM_TARGET, Optional.empty());
      var1.define(DATA_SHOW_BOTTOM, true);
   }

   public void tick() {
      ++this.time;
      this.applyEffectsFromBlocks();
      this.handlePortal();
      if (this.level() instanceof ServerLevel) {
         BlockPos var1 = this.blockPosition();
         if (((ServerLevel)this.level()).getDragonFight() != null && this.level().getBlockState(var1).isAir()) {
            this.level().setBlockAndUpdate(var1, BaseFireBlock.getState(this.level(), var1));
         }
      }

   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      if (this.getBeamTarget() != null) {
         var1.put("beam_target", NbtUtils.writeBlockPos(this.getBeamTarget()));
      }

      var1.putBoolean("ShowBottom", this.showsBottom());
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      NbtUtils.readBlockPos(var1, "beam_target").ifPresent(this::setBeamTarget);
      if (var1.contains("ShowBottom", 1)) {
         this.setShowBottom(var1.getBoolean("ShowBottom"));
      }

   }

   public boolean isPickable() {
      return true;
   }

   public final boolean hurtClient(DamageSource var1) {
      if (this.isInvulnerableToBase(var1)) {
         return false;
      } else {
         return !(var1.getEntity() instanceof EnderDragon);
      }
   }

   public final boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isInvulnerableToBase(var2)) {
         return false;
      } else if (var2.getEntity() instanceof EnderDragon) {
         return false;
      } else {
         if (!this.isRemoved()) {
            this.remove(Entity.RemovalReason.KILLED);
            if (!var2.is(DamageTypeTags.IS_EXPLOSION)) {
               DamageSource var4 = var2.getEntity() != null ? this.damageSources().explosion(this, var2.getEntity()) : null;
               var1.explode(this, var4, (ExplosionDamageCalculator)null, this.getX(), this.getY(), this.getZ(), 6.0F, false, Level.ExplosionInteraction.BLOCK);
            }

            this.onDestroyedBy(var1, var2);
         }

         return true;
      }
   }

   public void kill(ServerLevel var1) {
      this.onDestroyedBy(var1, this.damageSources().generic());
      super.kill(var1);
   }

   private void onDestroyedBy(ServerLevel var1, DamageSource var2) {
      EndDragonFight var3 = var1.getDragonFight();
      if (var3 != null) {
         var3.onCrystalDestroyed(this, var2);
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

   public ItemStack getPickResult() {
      return new ItemStack(Items.END_CRYSTAL);
   }

   static {
      DATA_BEAM_TARGET = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
      DATA_SHOW_BOTTOM = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.BOOLEAN);
   }
}
