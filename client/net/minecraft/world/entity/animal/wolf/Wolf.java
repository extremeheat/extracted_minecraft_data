package net.minecraft.world.entity.animal.wolf;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BegGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Wolf extends TamableAnimal implements NeutralMob {
   private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID;
   private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR;
   private static final EntityDataAccessor<Long> DATA_ANGER_END_TIME;
   private static final EntityDataAccessor<Holder<WolfVariant>> DATA_VARIANT_ID;
   private static final EntityDataAccessor<Holder<WolfSoundVariant>> DATA_SOUND_VARIANT_ID;
   public static final TargetingConditions.Selector PREY_SELECTOR;
   private static final float START_HEALTH = 8.0F;
   private static final float TAME_HEALTH = 40.0F;
   private static final float ARMOR_REPAIR_UNIT = 0.125F;
   public static final float DEFAULT_TAIL_ANGLE = 0.62831855F;
   private static final DyeColor DEFAULT_COLLAR_COLOR;
   private float interestedAngle;
   private float interestedAngleO;
   private boolean isWet;
   private boolean isShaking;
   private float shakeAnim;
   private float shakeAnimO;
   private static final UniformInt PERSISTENT_ANGER_TIME;
   private @Nullable EntityReference<LivingEntity> persistentAngerTarget;

   public Wolf(EntityType<? extends Wolf> var1, Level var2) {
      super(var1, var2);
      this.setTame(false, false);
      this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new TamableAnimal.TamableAnimalPanicGoal(1.5, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(3, new WolfAvoidEntityGoal(this, Llama.class, 24.0F, 1.5, 1.5));
      this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(7, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(5, new NonTameRandomTargetGoal(this, Animal.class, false, PREY_SELECTOR));
      this.targetSelector.addGoal(6, new NonTameRandomTargetGoal(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, AbstractSkeleton.class, false));
      this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal(this, true));
   }

   public Identifier getTexture() {
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

   public <T> @Nullable T get(DataComponentType<? extends T> var1) {
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
      var1.define(DATA_ANGER_END_TIME, -1L);
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.store("CollarColor", DyeColor.LEGACY_ID_CODEC, this.getCollarColor());
      VariantUtils.writeVariant(var1, this.getVariant());
      this.addPersistentAngerSaveData(var1);
      this.getSoundVariant().unwrapKey().ifPresent((var1x) -> var1.store("sound_variant", ResourceKey.codec(Registries.WOLF_SOUND_VARIANT), var1x));
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      VariantUtils.readVariant(var1, Registries.WOLF_VARIANT).ifPresent(this::setVariant);
      this.setCollarColor((DyeColor)var1.read("CollarColor", DyeColor.LEGACY_ID_CODEC).orElse(DEFAULT_COLLAR_COLOR));
      this.readPersistentAngerSaveData(this.level(), var1);
      var1.read("sound_variant", ResourceKey.codec(Registries.WOLF_SOUND_VARIANT)).flatMap((var1x) -> this.registryAccess().lookupOrThrow(Registries.WOLF_SOUND_VARIANT).get(var1x)).ifPresent(this::setSoundVariant);
   }

   public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      if (var4 instanceof WolfPackData var5) {
         this.setVariant(var5.type);
      } else {
         Optional var6 = VariantUtils.selectVariantToSpawn(SpawnContext.create(var1, this.blockPosition()), Registries.WOLF_VARIANT);
         if (var6.isPresent()) {
            this.setVariant((Holder)var6.get());
            var4 = new WolfPackData((Holder)var6.get());
         }
      }

      this.setSoundVariant(WolfSoundVariants.pickRandomSoundVariant(this.registryAccess(), var1.getRandom()));
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
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

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.canArmorAbsorb(var1) ? SoundEvents.WOLF_ARMOR_DAMAGE : (SoundEvent)((WolfSoundVariant)this.getSoundVariant().value()).hurtSound().value();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)((WolfSoundVariant)this.getSoundVariant().value()).deathSound().value();
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide() && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround()) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
         this.level().broadcastEntityEvent(this, (byte)8);
      }

      if (!this.level().isClientSide()) {
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
            if (this.isShaking && !this.level().isClientSide()) {
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
               int var2 = (int)(Mth.sin((double)((this.shakeAnim - 0.4F) * 3.1415927F)) * 7.0F);
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

   public void die(DamageSource var1) {
      this.isWet = false;
      this.isShaking = false;
      this.shakeAnimO = 0.0F;
      this.shakeAnim = 0.0F;
      super.die(var1);
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

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isInvulnerableTo(var1, var2)) {
         return false;
      } else {
         this.setOrderedToSit(false);
         return super.hurtServer(var1, var2, var3);
      }
   }

   protected void actuallyHurt(ServerLevel var1, DamageSource var2, float var3) {
      if (!this.canArmorAbsorb(var2)) {
         super.actuallyHurt(var1, var2, var3);
      } else {
         ItemStack var4 = this.getBodyArmorItem();
         int var5 = var4.getDamageValue();
         int var6 = var4.getMaxDamage();
         var4.hurtAndBreak(Mth.ceil(var3), this, (EquipmentSlot)EquipmentSlot.BODY);
         if (Crackiness.WOLF_ARMOR.byDamage(var5, var6) != Crackiness.WOLF_ARMOR.byDamage(this.getBodyArmorItem())) {
            this.playSound(SoundEvents.WOLF_ARMOR_CRACK);
            var1.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, Items.ARMADILLO_SCUTE.getDefaultInstance()), this.getX(), this.getY() + 1.0, this.getZ(), 20, 0.2, 0.1, 0.2, 0.1);
         }

      }
   }

   private boolean canArmorAbsorb(DamageSource var1) {
      return this.getBodyArmorItem().is(Items.WOLF_ARMOR) && !var1.is(DamageTypeTags.BYPASSES_WOLF_ARMOR);
   }

   protected void applyTamingSideEffects() {
      if (this.isTame()) {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
         this.setHealth(40.0F);
      } else {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0);
      }

   }

   protected void hurtArmor(DamageSource var1, float var2) {
      this.doHurtEquipment(var1, var2, new EquipmentSlot[]{EquipmentSlot.BODY});
   }

   protected boolean canShearEquipment(Player var1) {
      return this.isOwnedBy(var1);
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      Item var4 = var3.getItem();
      if (this.isTame()) {
         if (this.isFood(var3) && this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(var1, var2, var3);
            FoodProperties var10 = (FoodProperties)var3.get(DataComponents.FOOD);
            float var11 = var10 != null ? (float)var10.nutrition() : 1.0F;
            this.heal(2.0F * var11);
            return InteractionResult.SUCCESS;
         } else {
            if (var4 instanceof DyeItem) {
               DyeItem var5 = (DyeItem)var4;
               if (this.isOwnedBy(var1)) {
                  DyeColor var9 = var5.getDyeColor();
                  if (var9 != this.getCollarColor()) {
                     this.setCollarColor(var9);
                     var3.consume(1, var1);
                     return InteractionResult.SUCCESS;
                  }

                  return super.mobInteract(var1, var2);
               }
            }

            if (this.isEquippableInSlot(var3, EquipmentSlot.BODY) && !this.isWearingBodyArmor() && this.isOwnedBy(var1) && !this.isBaby()) {
               this.setBodyArmorItem(var3.copyWithCount(1));
               var3.consume(1, var1);
               return InteractionResult.SUCCESS;
            } else if (this.isInSittingPose() && this.isWearingBodyArmor() && this.isOwnedBy(var1) && this.getBodyArmorItem().isDamaged() && this.getBodyArmorItem().isValidRepairItem(var3)) {
               var3.shrink(1);
               this.playSound(SoundEvents.WOLF_ARMOR_REPAIR);
               ItemStack var8 = this.getBodyArmorItem();
               int var7 = (int)((float)var8.getMaxDamage() * 0.125F);
               var8.setDamageValue(Math.max(0, var8.getDamageValue() - var7));
               return InteractionResult.SUCCESS;
            } else {
               InteractionResult var6 = super.mobInteract(var1, var2);
               if (!var6.consumesAction() && this.isOwnedBy(var1)) {
                  this.setOrderedToSit(!this.isOrderedToSit());
                  this.jumping = false;
                  this.navigation.stop();
                  this.setTarget((LivingEntity)null);
                  return InteractionResult.SUCCESS.withoutItem();
               } else {
                  return var6;
               }
            }
         }
      } else if (!this.level().isClientSide() && var3.is(Items.BONE) && !this.isAngry()) {
         var3.consume(1, var1);
         this.tryToTame(var1);
         return InteractionResult.SUCCESS_SERVER;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   private void tryToTame(Player var1) {
      if (this.random.nextInt(3) == 0) {
         this.tame(var1);
         this.navigation.stop();
         this.setTarget((LivingEntity)null);
         this.setOrderedToSit(true);
         this.level().broadcastEntityEvent(this, (byte)7);
      } else {
         this.level().broadcastEntityEvent(this, (byte)6);
      }

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

   public float getTailAngle() {
      if (this.isAngry()) {
         return 1.5393804F;
      } else if (this.isTame()) {
         float var1 = this.getMaxHealth();
         float var2 = (var1 - this.getHealth()) / var1;
         return (0.55F - var2 * 0.4F) * 3.1415927F;
      } else {
         return 0.62831855F;
      }
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.WOLF_FOOD);
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public long getPersistentAngerEndTime() {
      return (Long)this.entityData.get(DATA_ANGER_END_TIME);
   }

   public void setPersistentAngerEndTime(long var1) {
      this.entityData.set(DATA_ANGER_END_TIME, var1);
   }

   public void startPersistentAngerTimer() {
      this.setTimeToRemainAngry((long)PERSISTENT_ANGER_TIME.sample(this.random));
   }

   public @Nullable EntityReference<LivingEntity> getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(@Nullable EntityReference<LivingEntity> var1) {
      this.persistentAngerTarget = var1;
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId((Integer)this.entityData.get(DATA_COLLAR_COLOR));
   }

   private void setCollarColor(DyeColor var1) {
      this.entityData.set(DATA_COLLAR_COLOR, var1.getId());
   }

   public @Nullable Wolf getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Wolf var3 = EntityType.WOLF.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null && var2 instanceof Wolf var4) {
         if (this.random.nextBoolean()) {
            var3.setVariant(this.getVariant());
         } else {
            var3.setVariant(var4.getVariant());
         }

         if (this.isTame()) {
            var3.setOwnerReference(this.getOwnerReference());
            var3.setTame(true, true);
            DyeColor var5 = this.getCollarColor();
            DyeColor var6 = var4.getCollarColor();
            var3.setCollarColor(DyeColor.getMixedColor(var1, var5, var6));
         }

         var3.setSoundVariant(WolfSoundVariants.pickRandomSoundVariant(this.registryAccess(), this.random));
      }

      return var3;
   }

   public void setIsInterested(boolean var1) {
      this.entityData.set(DATA_INTERESTED_ID, var1);
   }

   public boolean canMate(Animal var1) {
      if (var1 == this) {
         return false;
      } else if (!this.isTame()) {
         return false;
      } else if (!(var1 instanceof Wolf)) {
         return false;
      } else {
         Wolf var2 = (Wolf)var1;
         if (!var2.isTame()) {
            return false;
         } else if (var2.isInSittingPose()) {
            return false;
         } else {
            return this.isInLove() && var2.isInLove();
         }
      }
   }

   public boolean isInterested() {
      return (Boolean)this.entityData.get(DATA_INTERESTED_ID);
   }

   public boolean wantsToAttack(LivingEntity var1, LivingEntity var2) {
      if (!(var1 instanceof Creeper) && !(var1 instanceof Ghast) && !(var1 instanceof ArmorStand)) {
         if (var1 instanceof Wolf) {
            Wolf var7 = (Wolf)var1;
            return !var7.isTame() || var7.getOwner() != var2;
         } else {
            if (var1 instanceof Player) {
               Player var3 = (Player)var1;
               if (var2 instanceof Player) {
                  Player var4 = (Player)var2;
                  if (!var4.canHarmPlayer(var3)) {
                     return false;
                  }
               }
            }

            if (var1 instanceof AbstractHorse) {
               AbstractHorse var5 = (AbstractHorse)var1;
               if (var5.isTamed()) {
                  return false;
               }
            }

            boolean var10000;
            if (var1 instanceof TamableAnimal) {
               TamableAnimal var6 = (TamableAnimal)var1;
               if (var6.isTame()) {
                  var10000 = false;
                  return var10000;
               }
            }

            var10000 = true;
            return var10000;
         }
      } else {
         return false;
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
   public @Nullable AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_INTERESTED_ID = SynchedEntityData.<Boolean>defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
      DATA_COLLAR_COLOR = SynchedEntityData.<Integer>defineId(Wolf.class, EntityDataSerializers.INT);
      DATA_ANGER_END_TIME = SynchedEntityData.<Long>defineId(Wolf.class, EntityDataSerializers.LONG);
      DATA_VARIANT_ID = SynchedEntityData.<Holder<WolfVariant>>defineId(Wolf.class, EntityDataSerializers.WOLF_VARIANT);
      DATA_SOUND_VARIANT_ID = SynchedEntityData.<Holder<WolfSoundVariant>>defineId(Wolf.class, EntityDataSerializers.WOLF_SOUND_VARIANT);
      PREY_SELECTOR = (var0, var1) -> {
         EntityType var2 = var0.getType();
         return var2 == EntityType.SHEEP || var2 == EntityType.RABBIT || var2 == EntityType.FOX;
      };
      DEFAULT_COLLAR_COLOR = DyeColor.RED;
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   }

   public static class WolfPackData extends AgeableMob.AgeableMobGroupData {
      public final Holder<WolfVariant> type;

      public WolfPackData(Holder<WolfVariant> var1) {
         super(false);
         this.type = var1;
      }
   }

   class WolfAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final Wolf wolf;

      public WolfAvoidEntityGoal(final Wolf var2, final Class<T> var3, final float var4, final double var5, final double var7) {
         super(var2, var3, var4, var5, var7);
         this.wolf = var2;
      }

      public boolean canUse() {
         if (super.canUse() && this.toAvoid instanceof Llama) {
            return !this.wolf.isTame() && this.avoidLlama((Llama)this.toAvoid);
         } else {
            return false;
         }
      }

      private boolean avoidLlama(Llama var1) {
         return var1.getStrength() >= Wolf.this.random.nextInt(5);
      }

      public void start() {
         Wolf.this.setTarget((LivingEntity)null);
         super.start();
      }

      public void tick() {
         Wolf.this.setTarget((LivingEntity)null);
         super.tick();
      }
   }
}
