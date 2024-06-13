package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
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
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Wolf extends TamableAnimal implements NeutralMob, VariantHolder<Holder<WolfVariant>> {
   private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Holder<WolfVariant>> DATA_VARIANT_ID = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.WOLF_VARIANT);
   public static final Predicate<LivingEntity> PREY_SELECTOR = var0 -> {
      EntityType var1 = var0.getType();
      return var1 == EntityType.SHEEP || var1 == EntityType.RABBIT || var1 == EntityType.FOX;
   };
   private static final float START_HEALTH = 8.0F;
   private static final float TAME_HEALTH = 40.0F;
   private static final float ARMOR_REPAIR_UNIT = 0.125F;
   private float interestedAngle;
   private float interestedAngleO;
   private boolean isWet;
   private boolean isShaking;
   private float shakeAnim;
   private float shakeAnimO;
   private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   @Nullable
   private UUID persistentAngerTarget;

   public Wolf(EntityType<? extends Wolf> var1, Level var2) {
      super(var1, var2);
      this.setTame(false, false);
      this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new TamableAnimal.TamableAnimalPanicGoal(1.5, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(3, new Wolf.WolfAvoidEntityGoal<>(this, Llama.class, 24.0F, 1.5, 1.5));
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
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Animal.class, false, PREY_SELECTOR));
      this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
      this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
   }

   public ResourceLocation getTexture() {
      WolfVariant var1 = this.getVariant().value();
      if (this.isTame()) {
         return var1.tameTexture();
      } else {
         return this.isAngry() ? var1.angryTexture() : var1.wildTexture();
      }
   }

   public Holder<WolfVariant> getVariant() {
      return this.entityData.get(DATA_VARIANT_ID);
   }

   public void setVariant(Holder<WolfVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 4.0);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      RegistryAccess var2 = this.registryAccess();
      Registry var3 = var2.registryOrThrow(Registries.WOLF_VARIANT);
      var1.define(DATA_VARIANT_ID, var3.getHolder(WolfVariants.DEFAULT).or(var3::getAny).orElseThrow());
      var1.define(DATA_INTERESTED_ID, false);
      var1.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
      var1.define(DATA_REMAINING_ANGER_TIME, 0);
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putByte("CollarColor", (byte)this.getCollarColor().getId());
      this.getVariant().unwrapKey().ifPresent(var1x -> var1.putString("variant", var1x.location().toString()));
      this.addPersistentAngerSaveData(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      Optional.ofNullable(ResourceLocation.tryParse(var1.getString("variant")))
         .map(var0 -> ResourceKey.create(Registries.WOLF_VARIANT, var0))
         .flatMap(var1x -> this.registryAccess().registryOrThrow(Registries.WOLF_VARIANT).getHolder((ResourceKey<WolfVariant>)var1x))
         .ifPresent(this::setVariant);
      if (var1.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(var1.getInt("CollarColor")));
      }

      this.readPersistentAngerSaveData(this.level(), var1);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      Holder var5 = var1.getBiome(this.blockPosition());
      Holder var6;
      if (var4 instanceof Wolf.WolfPackData var7) {
         var6 = var7.type;
      } else {
         var6 = WolfVariants.getSpawnVariant(this.registryAccess(), var5);
         var4 = new Wolf.WolfPackData(var6);
      }

      this.setVariant(var6);
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      if (this.isAngry()) {
         return SoundEvents.WOLF_GROWL;
      } else if (this.random.nextInt(3) == 0) {
         return this.isTame() && this.getHealth() < 20.0F ? SoundEvents.WOLF_WHINE : SoundEvents.WOLF_PANT;
      } else {
         return SoundEvents.WOLF_AMBIENT;
      }
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.canArmorAbsorb(var1) ? SoundEvents.WOLF_ARMOR_DAMAGE : SoundEvents.WOLF_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.WOLF_DEATH;
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
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

   @Override
   public void tick() {
      super.tick();
      if (this.isAlive()) {
         this.interestedAngleO = this.interestedAngle;
         if (this.isInterested()) {
            this.interestedAngle = this.interestedAngle + (1.0F - this.interestedAngle) * 0.4F;
         } else {
            this.interestedAngle = this.interestedAngle + (0.0F - this.interestedAngle) * 0.4F;
         }

         if (this.isInWaterRainOrBubble()) {
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

               for (int var4 = 0; var4 < var2; var4++) {
                  float var5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  float var6 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  this.level()
                     .addParticle(ParticleTypes.SPLASH, this.getX() + (double)var5, (double)(var1 + 0.8F), this.getZ() + (double)var6, var3.x, var3.y, var3.z);
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

   @Override
   public void die(DamageSource var1) {
      this.isWet = false;
      this.isShaking = false;
      this.shakeAnimO = 0.0F;
      this.shakeAnim = 0.0F;
      super.die(var1);
   }

   public boolean isWet() {
      return this.isWet;
   }

   public float getWetShade(float var1) {
      return Math.min(0.75F + Mth.lerp(var1, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.25F, 1.0F);
   }

   public float getBodyRollAngle(float var1, float var2) {
      float var3 = (Mth.lerp(var1, this.shakeAnimO, this.shakeAnim) + var2) / 1.8F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      } else if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      return Mth.sin(var3 * 3.1415927F) * Mth.sin(var3 * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
   }

   public float getHeadRollAngle(float var1) {
      return Mth.lerp(var1, this.interestedAngleO, this.interestedAngle) * 0.15F * 3.1415927F;
   }

   @Override
   public int getMaxHeadXRot() {
      return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         if (!this.level().isClientSide) {
            this.setOrderedToSit(false);
         }

         return super.hurt(var1, var2);
      }
   }

   @Override
   public boolean canUseSlot(EquipmentSlot var1) {
      return true;
   }

   @Override
   protected void actuallyHurt(DamageSource var1, float var2) {
      if (!this.canArmorAbsorb(var1)) {
         super.actuallyHurt(var1, var2);
      } else {
         ItemStack var3 = this.getBodyArmorItem();
         int var4 = var3.getDamageValue();
         int var5 = var3.getMaxDamage();
         var3.hurtAndBreak(Mth.ceil(var2), this, EquipmentSlot.BODY);
         if (Crackiness.WOLF_ARMOR.byDamage(var4, var5) != Crackiness.WOLF_ARMOR.byDamage(this.getBodyArmorItem())) {
            this.playSound(SoundEvents.WOLF_ARMOR_CRACK);
            if (this.level() instanceof ServerLevel var6) {
               var6.sendParticles(
                  new ItemParticleOption(ParticleTypes.ITEM, Items.ARMADILLO_SCUTE.getDefaultInstance()),
                  this.getX(),
                  this.getY() + 1.0,
                  this.getZ(),
                  20,
                  0.2,
                  0.1,
                  0.2,
                  0.1
               );
            }
         }
      }
   }

   private boolean canArmorAbsorb(DamageSource var1) {
      return this.hasArmor() && !var1.is(DamageTypeTags.BYPASSES_WOLF_ARMOR);
   }

   @Override
   protected void applyTamingSideEffects() {
      if (this.isTame()) {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
         this.setHealth(40.0F);
      } else {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0);
      }
   }

   @Override
   protected void hurtArmor(DamageSource var1, float var2) {
      this.doHurtEquipment(var1, var2, new EquipmentSlot[]{EquipmentSlot.BODY});
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      Item var4 = var3.getItem();
      if (!this.level().isClientSide || this.isBaby() && this.isFood(var3)) {
         if (this.isTame()) {
            if (this.isFood(var3) && this.getHealth() < this.getMaxHealth()) {
               var3.consume(1, var1);
               FoodProperties var12 = var3.get(DataComponents.FOOD);
               float var13 = var12 != null ? (float)var12.nutrition() : 1.0F;
               this.heal(2.0F * var13);
               return InteractionResult.sidedSuccess(this.level().isClientSide());
            } else {
               if (var4 instanceof DyeItem var8 && this.isOwnedBy(var1)) {
                  DyeColor var11 = var8.getDyeColor();
                  if (var11 != this.getCollarColor()) {
                     this.setCollarColor(var11);
                     var3.consume(1, var1);
                     return InteractionResult.SUCCESS;
                  }

                  return super.mobInteract(var1, var2);
               }

               if (var3.is(Items.WOLF_ARMOR) && this.isOwnedBy(var1) && this.getBodyArmorItem().isEmpty() && !this.isBaby()) {
                  this.setBodyArmorItem(var3.copyWithCount(1));
                  var3.consume(1, var1);
                  return InteractionResult.SUCCESS;
               } else if (var3.is(Items.SHEARS)
                  && this.isOwnedBy(var1)
                  && this.hasArmor()
                  && (!EnchantmentHelper.has(this.getBodyArmorItem(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || var1.isCreative())) {
                  var3.hurtAndBreak(1, var1, getSlotForHand(var2));
                  this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
                  ItemStack var6 = this.getBodyArmorItem();
                  this.setBodyArmorItem(ItemStack.EMPTY);
                  this.spawnAtLocation(var6);
                  return InteractionResult.SUCCESS;
               } else if (ArmorMaterials.ARMADILLO.value().repairIngredient().get().test(var3)
                  && this.isInSittingPose()
                  && this.hasArmor()
                  && this.isOwnedBy(var1)
                  && this.getBodyArmorItem().isDamaged()) {
                  var3.shrink(1);
                  this.playSound(SoundEvents.WOLF_ARMOR_REPAIR);
                  ItemStack var10 = this.getBodyArmorItem();
                  int var7 = (int)((float)var10.getMaxDamage() * 0.125F);
                  var10.setDamageValue(Math.max(0, var10.getDamageValue() - var7));
                  return InteractionResult.SUCCESS;
               } else {
                  InteractionResult var9 = super.mobInteract(var1, var2);
                  if (!var9.consumesAction() && this.isOwnedBy(var1)) {
                     this.setOrderedToSit(!this.isOrderedToSit());
                     this.jumping = false;
                     this.navigation.stop();
                     this.setTarget(null);
                     return InteractionResult.SUCCESS_NO_ITEM_USED;
                  } else {
                     return var9;
                  }
               }
            }
         } else if (var3.is(Items.BONE) && !this.isAngry()) {
            var3.consume(1, var1);
            this.tryToTame(var1);
            return InteractionResult.SUCCESS;
         } else {
            return super.mobInteract(var1, var2);
         }
      } else {
         boolean var5 = this.isOwnedBy(var1) || this.isTame() || var3.is(Items.BONE) && !this.isTame() && !this.isAngry();
         return var5 ? InteractionResult.CONSUME : InteractionResult.PASS;
      }
   }

   private void tryToTame(Player var1) {
      if (this.random.nextInt(3) == 0) {
         this.tame(var1);
         this.navigation.stop();
         this.setTarget(null);
         this.setOrderedToSit(true);
         this.level().broadcastEntityEvent(this, (byte)7);
      } else {
         this.level().broadcastEntityEvent(this, (byte)6);
      }
   }

   @Override
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

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.WOLF_FOOD);
   }

   @Override
   public int getMaxSpawnClusterSize() {
      return 8;
   }

   @Override
   public int getRemainingPersistentAngerTime() {
      return this.entityData.get(DATA_REMAINING_ANGER_TIME);
   }

   @Override
   public void setRemainingPersistentAngerTime(int var1) {
      this.entityData.set(DATA_REMAINING_ANGER_TIME, var1);
   }

   @Override
   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
   }

   @Nullable
   @Override
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   @Override
   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
   }

   public boolean hasArmor() {
      return this.getBodyArmorItem().is(Items.WOLF_ARMOR);
   }

   private void setCollarColor(DyeColor var1) {
      this.entityData.set(DATA_COLLAR_COLOR, var1.getId());
   }

   @Nullable
   public Wolf getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Wolf var3 = EntityType.WOLF.create(var1);
      if (var3 != null && var2 instanceof Wolf var4) {
         if (this.random.nextBoolean()) {
            var3.setVariant(this.getVariant());
         } else {
            var3.setVariant(var4.getVariant());
         }

         if (this.isTame()) {
            var3.setOwnerUUID(this.getOwnerUUID());
            var3.setTame(true, true);
            if (this.random.nextBoolean()) {
               var3.setCollarColor(this.getCollarColor());
            } else {
               var3.setCollarColor(var4.getCollarColor());
            }
         }
      }

      return var3;
   }

   public void setIsInterested(boolean var1) {
      this.entityData.set(DATA_INTERESTED_ID, var1);
   }

   @Override
   public boolean canMate(Animal var1) {
      if (var1 == this) {
         return false;
      } else if (!this.isTame()) {
         return false;
      } else if (!(var1 instanceof Wolf var2)) {
         return false;
      } else if (!var2.isTame()) {
         return false;
      } else {
         return var2.isInSittingPose() ? false : this.isInLove() && var2.isInLove();
      }
   }

   public boolean isInterested() {
      return this.entityData.get(DATA_INTERESTED_ID);
   }

   @Override
   public boolean wantsToAttack(LivingEntity var1, LivingEntity var2) {
      if (var1 instanceof Creeper || var1 instanceof Ghast || var1 instanceof ArmorStand) {
         return false;
      } else if (var1 instanceof Wolf var7) {
         return !var7.isTame() || var7.getOwner() != var2;
      } else {
         if (var1 instanceof Player var3 && var2 instanceof Player var4 && !var4.canHarmPlayer(var3)) {
            return false;
         }

         if (var1 instanceof AbstractHorse var5 && var5.isTamed()) {
            return false;
         }

         if (var1 instanceof TamableAnimal var6 && var6.isTame()) {
            return false;
         }

         return true;
      }
   }

   @Override
   public boolean canBeLeashed() {
      return !this.isAngry();
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   public static boolean checkWolfSpawnRules(EntityType<Wolf> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.WOLVES_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   class WolfAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final Wolf wolf;

      public WolfAvoidEntityGoal(final Wolf nullx, final Class<T> nullxx, final float nullxxx, final double nullxxxx, final double nullxxxxx) {
         super(nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
         this.wolf = nullx;
      }

      @Override
      public boolean canUse() {
         return super.canUse() && this.toAvoid instanceof Llama ? !this.wolf.isTame() && this.avoidLlama((Llama)this.toAvoid) : false;
      }

      private boolean avoidLlama(Llama var1) {
         return var1.getStrength() >= Wolf.this.random.nextInt(5);
      }

      @Override
      public void start() {
         Wolf.this.setTarget(null);
         super.start();
      }

      @Override
      public void tick() {
         Wolf.this.setTarget(null);
         super.tick();
      }
   }

   public static class WolfPackData extends AgeableMob.AgeableMobGroupData {
      public final Holder<WolfVariant> type;

      public WolfPackData(Holder<WolfVariant> var1) {
         super(false);
         this.type = var1;
      }
   }
}
