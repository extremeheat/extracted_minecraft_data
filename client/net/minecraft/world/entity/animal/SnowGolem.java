package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowGolem extends AbstractGolem implements RangedAttackMob {
   private static final EntityDataAccessor<Byte> DATA_PUMPKIN_ID;

   public SnowGolem(EntityType<? extends SnowGolem> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D, 1.0000001E-5F));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Mob.class, 10, true, false, (var0) -> {
         return var0 instanceof Enemy;
      }));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_PUMPKIN_ID, (byte)16);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("Pumpkin", this.hasPumpkin());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Pumpkin")) {
         this.setPumpkin(var1.getBoolean("Pumpkin"));
      }

   }

   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide) {
         int var1 = Mth.floor(this.x);
         int var2 = Mth.floor(this.y);
         int var3 = Mth.floor(this.z);
         if (this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0F);
         }

         if (this.level.getBiome(new BlockPos(var1, 0, var3)).getTemperature(new BlockPos(var1, var2, var3)) > 1.0F) {
            this.hurt(DamageSource.ON_FIRE, 1.0F);
         }

         if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
         }

         BlockState var4 = Blocks.SNOW.defaultBlockState();

         for(int var5 = 0; var5 < 4; ++var5) {
            var1 = Mth.floor(this.x + (double)((float)(var5 % 2 * 2 - 1) * 0.25F));
            var2 = Mth.floor(this.y);
            var3 = Mth.floor(this.z + (double)((float)(var5 / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos var6 = new BlockPos(var1, var2, var3);
            if (this.level.getBlockState(var6).isAir() && this.level.getBiome(var6).getTemperature(var6) < 0.8F && var4.canSurvive(this.level, var6)) {
               this.level.setBlockAndUpdate(var6, var4);
            }
         }
      }

   }

   public void performRangedAttack(LivingEntity var1, float var2) {
      Snowball var3 = new Snowball(this.level, this);
      double var4 = var1.y + (double)var1.getEyeHeight() - 1.100000023841858D;
      double var6 = var1.x - this.x;
      double var8 = var4 - var3.y;
      double var10 = var1.z - this.z;
      float var12 = Mth.sqrt(var6 * var6 + var10 * var10) * 0.2F;
      var3.shoot(var6, var8 + (double)var12, var10, 1.6F, 12.0F);
      this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(var3);
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 1.7F;
   }

   protected boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.getItem() == Items.SHEARS && this.hasPumpkin() && !this.level.isClientSide) {
         this.setPumpkin(false);
         var3.hurtAndBreak(1, var1, (var1x) -> {
            var1x.broadcastBreakEvent(var2);
         });
      }

      return super.mobInteract(var1, var2);
   }

   public boolean hasPumpkin() {
      return ((Byte)this.entityData.get(DATA_PUMPKIN_ID) & 16) != 0;
   }

   public void setPumpkin(boolean var1) {
      byte var2 = (Byte)this.entityData.get(DATA_PUMPKIN_ID);
      if (var1) {
         this.entityData.set(DATA_PUMPKIN_ID, (byte)(var2 | 16));
      } else {
         this.entityData.set(DATA_PUMPKIN_ID, (byte)(var2 & -17));
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.SNOW_GOLEM_AMBIENT;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SNOW_GOLEM_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.SNOW_GOLEM_DEATH;
   }

   static {
      DATA_PUMPKIN_ID = SynchedEntityData.defineId(SnowGolem.class, EntityDataSerializers.BYTE);
   }
}
