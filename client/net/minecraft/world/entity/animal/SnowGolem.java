package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public class SnowGolem extends AbstractGolem implements Shearable, RangedAttackMob {
   private static final EntityDataAccessor<Byte> DATA_PUMPKIN_ID = SynchedEntityData.defineId(SnowGolem.class, EntityDataSerializers.BYTE);
   private static final byte PUMPKIN_FLAG = 16;

   public SnowGolem(EntityType<? extends SnowGolem> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5F));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, var0 -> var0 instanceof Enemy));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_PUMPKIN_ID, (byte)16);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("Pumpkin", this.hasPumpkin());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Pumpkin")) {
         this.setPumpkin(var1.getBoolean("Pumpkin"));
      }
   }

   @Override
   public boolean isSensitiveToWater() {
      return true;
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide) {
         if (this.level().getBiome(this.blockPosition()).is(BiomeTags.SNOW_GOLEM_MELTS)) {
            this.hurt(this.damageSources().onFire(), 1.0F);
         }

         if (!this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
         }

         BlockState var1 = Blocks.SNOW.defaultBlockState();

         for (int var2 = 0; var2 < 4; var2++) {
            int var3 = Mth.floor(this.getX() + (double)((float)(var2 % 2 * 2 - 1) * 0.25F));
            int var4 = Mth.floor(this.getY());
            int var5 = Mth.floor(this.getZ() + (double)((float)(var2 / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos var6 = new BlockPos(var3, var4, var5);
            if (this.level().getBlockState(var6).isAir() && var1.canSurvive(this.level(), var6)) {
               this.level().setBlockAndUpdate(var6, var1);
               this.level().gameEvent(GameEvent.BLOCK_PLACE, var6, GameEvent.Context.of(this, var1));
            }
         }
      }
   }

   @Override
   public void performRangedAttack(LivingEntity var1, float var2) {
      double var3 = var1.getX() - this.getX();
      double var5 = var1.getEyeY() - 1.100000023841858;
      double var7 = var1.getZ() - this.getZ();
      double var9 = Math.sqrt(var3 * var3 + var7 * var7) * 0.20000000298023224;
      if (this.level() instanceof ServerLevel var11) {
         ItemStack var13 = new ItemStack(Items.SNOWBALL);
         Projectile.spawnProjectile(new Snowball(var11, this, var13), var11, var13, var8 -> var8.shoot(var3, var5 + var9 - var8.getY(), var7, 1.6F, 12.0F));
      }

      this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   @Override
   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.SHEARS) && this.readyForShearing()) {
         this.shear(SoundSource.PLAYERS);
         this.gameEvent(GameEvent.SHEAR, var1);
         if (!this.level().isClientSide) {
            var3.hurtAndBreak(1, var1, getSlotForHand(var2));
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public void shear(SoundSource var1) {
      this.level().playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, var1, 1.0F, 1.0F);
      if (!this.level().isClientSide()) {
         this.setPumpkin(false);
         this.dropFromShearingLootTable(BuiltInLootTables.SHEAR_SNOW_GOLEM, var1x -> this.spawnAtLocation(var1x, this.getEyeHeight()));
      }
   }

   @Override
   public boolean readyForShearing() {
      return this.isAlive() && this.hasPumpkin();
   }

   public boolean hasPumpkin() {
      return (this.entityData.get(DATA_PUMPKIN_ID) & 16) != 0;
   }

   public void setPumpkin(boolean var1) {
      byte var2 = this.entityData.get(DATA_PUMPKIN_ID);
      if (var1) {
         this.entityData.set(DATA_PUMPKIN_ID, (byte)(var2 | 16));
      } else {
         this.entityData.set(DATA_PUMPKIN_ID, (byte)(var2 & -17));
      }
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.SNOW_GOLEM_AMBIENT;
   }

   @Nullable
   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SNOW_GOLEM_HURT;
   }

   @Nullable
   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.SNOW_GOLEM_DEATH;
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.75F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }
}
