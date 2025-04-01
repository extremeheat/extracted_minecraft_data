package net.minecraft.world.entity.pets;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PetWolfBegGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariants;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class PetWolf extends AbstractPet implements NeutralMob {
   private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID;
   private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR;
   private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME;
   private static final EntityDataAccessor<Holder<WolfVariant>> DATA_VARIANT_ID;
   private static final EntityDataAccessor<Holder<WolfSoundVariant>> DATA_SOUND_VARIANT_ID;
   private static final float START_HEALTH = 8.0F;
   private static final float TAME_HEALTH = 40.0F;
   public static final float DEFAULT_TAIL_ANGLE = 0.62831855F;
   private static final DyeColor DEFAULT_COLLAR_COLOR;
   private float interestedAngle;
   private float interestedAngleO;
   private boolean isWet;
   private boolean isShaking;
   private float shakeAnim;
   private float shakeAnimO;
   private static final UniformInt PERSISTENT_ANGER_TIME;
   @Nullable
   private UUID persistentAngerTarget;

   public PetWolf(EntityType<? extends PetWolf> var1, Level var2) {
      super(var1, var2);
      this.setTame(true, false);
      this.setInvulnerable(true);
      this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new TamableAnimal.TamableAnimalPanicGoal(1.5, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(9, new PetWolfBegGoal(this, 8.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, AbstractSkeleton.class, false));
      this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal(this, true));
   }

   public ResourceLocation getTexture() {
      WolfVariant var1 = (WolfVariant)this.getVariant().value();
      if (this.isTame()) {
         return var1.assetInfo().tame().texturePath();
      } else {
         return this.isAngry() ? var1.assetInfo().angry().texturePath() : var1.assetInfo().wild().texturePath();
      }
   }

   private Holder<WolfVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   private void setVariant(Holder<WolfVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   private Holder<WolfSoundVariant> getSoundVariant() {
      return (Holder)this.entityData.get(DATA_SOUND_VARIANT_ID);
   }

   private void setSoundVariant(Holder<WolfSoundVariant> var1) {
      this.entityData.set(DATA_SOUND_VARIANT_ID, var1);
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      if (var1 == DataComponents.WOLF_VARIANT) {
         return (T)castComponentValue(var1, this.getVariant());
      } else if (var1 == DataComponents.WOLF_SOUND_VARIANT) {
         return (T)castComponentValue(var1, this.getSoundVariant());
      } else {
         return (T)(var1 == DataComponents.WOLF_COLLAR ? castComponentValue(var1, this.getCollarColor()) : super.get(var1));
      }
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.WOLF_VARIANT);
      this.applyImplicitComponentIfPresent(var1, DataComponents.WOLF_SOUND_VARIANT);
      this.applyImplicitComponentIfPresent(var1, DataComponents.WOLF_COLLAR);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.WOLF_VARIANT) {
         this.setVariant((Holder)castComponentValue(DataComponents.WOLF_VARIANT, var2));
         return true;
      } else if (var1 == DataComponents.WOLF_SOUND_VARIANT) {
         this.setSoundVariant((Holder)castComponentValue(DataComponents.WOLF_SOUND_VARIANT, var2));
         return true;
      } else if (var1 == DataComponents.WOLF_COLLAR) {
         this.setCollarColor((DyeColor)castComponentValue(DataComponents.WOLF_COLLAR, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 4.0);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      Registry var2 = this.registryAccess().lookupOrThrow(Registries.WOLF_SOUND_VARIANT);
      var1.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), WolfVariants.DEFAULT));
      EntityDataAccessor var10001 = DATA_SOUND_VARIANT_ID;
      Optional var10002 = var2.get(WolfSoundVariants.CLASSIC);
      Objects.requireNonNull(var2);
      var1.define(var10001, (Holder)var10002.or(var2::getAny).orElseThrow());
      var1.define(DATA_INTERESTED_ID, false);
      var1.define(DATA_COLLAR_COLOR, DEFAULT_COLLAR_COLOR.getId());
      var1.define(DATA_REMAINING_ANGER_TIME, 0);
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.store("CollarColor", DyeColor.LEGACY_ID_CODEC, this.getCollarColor());
      VariantUtils.writeVariant(var1, this.getVariant());
      this.addPersistentAngerSaveData(var1);
      this.getSoundVariant().unwrapKey().ifPresent((var1x) -> var1.store("sound_variant", ResourceKey.codec(Registries.WOLF_SOUND_VARIANT), var1x));
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      VariantUtils.readVariant(var1, this.registryAccess(), Registries.WOLF_VARIANT).ifPresent(this::setVariant);
      this.setCollarColor((DyeColor)var1.read("CollarColor", DyeColor.LEGACY_ID_CODEC).orElse(DEFAULT_COLLAR_COLOR));
      this.readPersistentAngerSaveData(this.level(), var1);
      var1.read("sound_variant", ResourceKey.codec(Registries.WOLF_SOUND_VARIANT)).flatMap((var1x) -> this.registryAccess().lookupOrThrow(Registries.WOLF_SOUND_VARIANT).get(var1x)).ifPresent(this::setSoundVariant);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      Optional var5 = WolfVariants.selectVariantToSpawn(this.random, this.registryAccess(), SpawnContext.create(var1, this.blockPosition()));
      var5.ifPresent(this::setVariant);
      this.setSoundVariant(WolfSoundVariants.pickRandomSoundVariant(this.registryAccess(), this.random));
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)null);
   }

   protected SoundEvent getAmbientSound() {
      if (this.isAngry()) {
         return (SoundEvent)((WolfSoundVariant)this.getSoundVariant().value()).growlSound().value();
      } else if (this.random.nextInt(3) == 0) {
         return this.isTame() && this.getHealth() < 20.0F ? (SoundEvent)((WolfSoundVariant)this.getSoundVariant().value()).whineSound().value() : (SoundEvent)((WolfSoundVariant)this.getSoundVariant().value()).pantSound().value();
      } else {
         return (SoundEvent)((WolfSoundVariant)this.getSoundVariant().value()).ambientSound().value();
      }
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround()) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
         this.level().broadcastEntityEvent(this, (byte)8);
      }

      if (!this.level().isClientSide) {
         this.updatePersistentAnger((ServerLevel)this.level(), true);
      }

   }

   public void tick() {
      super.tick();
      if (this.isAlive()) {
         this.interestedAngleO = this.interestedAngle;
         if (this.isInterested()) {
            this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
         } else {
            this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
         }

         if (this.isInWaterOrRain()) {
            this.isWet = true;
            if (this.isShaking && !this.level().isClientSide) {
               this.level().broadcastEntityEvent(this, (byte)56);
               this.cancelShake();
            }
         } else if ((this.isWet || this.isShaking) && this.isShaking) {
            if (this.shakeAnim == 0.0F) {
               this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
               this.gameEvent(GameEvent.ENTITY_ACTION);
            }

            this.shakeAnimO = this.shakeAnim;
            this.shakeAnim += 0.05F;
            if (this.shakeAnimO >= 2.0F) {
               this.isWet = false;
               this.isShaking = false;
               this.shakeAnimO = 0.0F;
               this.shakeAnim = 0.0F;
            }

            if (this.shakeAnim > 0.4F) {
               float var1 = (float)this.getY();
               int var2 = (int)(Mth.sin((this.shakeAnim - 0.4F) * 3.1415927F) * 7.0F);
               Vec3 var3 = this.getDeltaMovement();

               for(int var4 = 0; var4 < var2; ++var4) {
                  float var5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  float var6 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  this.level().addParticle(ParticleTypes.SPLASH, this.getX() + (double)var5, (double)(var1 + 0.8F), this.getZ() + (double)var6, var3.x, var3.y, var3.z);
               }
            }
         }

      }
   }

   private void cancelShake() {
      this.isShaking = false;
      this.shakeAnim = 0.0F;
      this.shakeAnimO = 0.0F;
   }

   public float getWetShade(float var1) {
      return !this.isWet ? 1.0F : Math.min(0.75F + Mth.lerp(var1, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.25F, 1.0F);
   }

   public float getShakeAnim(float var1) {
      return Mth.lerp(var1, this.shakeAnimO, this.shakeAnim);
   }

   public float getHeadRollAngle(float var1) {
      return Mth.lerp(var1, this.interestedAngleO, this.interestedAngle) * 0.15F * 3.1415927F;
   }

   public int getMaxHeadXRot() {
      return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
   }

   private boolean canArmorAbsorb(DamageSource var1) {
      return this.getBodyArmorItem().is(Items.WOLF_ARMOR) && !var1.is(DamageTypeTags.BYPASSES_WOLF_ARMOR);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 8) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
      } else if (var1 == 56) {
         this.cancelShake();
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.WOLF_FOOD);
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public int getRemainingPersistentAngerTime() {
      return (Integer)this.entityData.get(DATA_REMAINING_ANGER_TIME);
   }

   public void setRemainingPersistentAngerTime(int var1) {
      this.entityData.set(DATA_REMAINING_ANGER_TIME, var1);
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
   }

   @Nullable
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId((Integer)this.entityData.get(DATA_COLLAR_COLOR));
   }

   private void setCollarColor(DyeColor var1) {
      this.entityData.set(DATA_COLLAR_COLOR, var1.getId());
   }

   @Nullable
   public Wolf getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return null;
   }

   public void setIsInterested(boolean var1) {
      this.entityData.set(DATA_INTERESTED_ID, var1);
   }

   public boolean canMate(Animal var1) {
      return false;
   }

   public boolean isInterested() {
      return (Boolean)this.entityData.get(DATA_INTERESTED_ID);
   }

   public boolean wantsToAttack(LivingEntity var1, LivingEntity var2) {
      if (var2 instanceof Player var3) {
         if (!var3.isActive(PlayerUnlocks.SWORD_WOLF)) {
            return false;
         }
      }

      if (var1 instanceof Wolf var8) {
         return !var8.isTame() || var8.getOwner() != var2;
      } else {
         if (var1 instanceof Player var5) {
            if (var2 instanceof Player var4) {
               if (!var4.canHarmPlayer(var5)) {
                  return false;
               }
            }
         }

         if (var1 instanceof AbstractHorse var6) {
            if (var6.isTamed()) {
               return false;
            }
         }

         boolean var10000;
         if (var1 instanceof TamableAnimal var7) {
            if (var7.isTame()) {
               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }
   }

   public boolean canBeLeashed() {
      return !this.isAngry();
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   public static boolean checkWolfSpawnRules(EntityType<Wolf> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.WOLVES_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_INTERESTED_ID = SynchedEntityData.<Boolean>defineId(PetWolf.class, EntityDataSerializers.BOOLEAN);
      DATA_COLLAR_COLOR = SynchedEntityData.<Integer>defineId(PetWolf.class, EntityDataSerializers.INT);
      DATA_REMAINING_ANGER_TIME = SynchedEntityData.<Integer>defineId(PetWolf.class, EntityDataSerializers.INT);
      DATA_VARIANT_ID = SynchedEntityData.<Holder<WolfVariant>>defineId(PetWolf.class, EntityDataSerializers.WOLF_VARIANT);
      DATA_SOUND_VARIANT_ID = SynchedEntityData.<Holder<WolfSoundVariant>>defineId(PetWolf.class, EntityDataSerializers.WOLF_SOUND_VARIANT);
      DEFAULT_COLLAR_COLOR = DyeColor.RED;
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   }
}
