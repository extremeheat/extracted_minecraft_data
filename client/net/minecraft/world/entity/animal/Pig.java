package net.minecraft.world.entity.animal;

import com.google.common.collect.UnmodifiableIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Pig extends Animal implements ItemSteerable {
   private static final EntityDataAccessor<Integer> DATA_BOOST_TIME;
   private static final EntityDataAccessor<Holder<PigVariant>> DATA_VARIANT_ID;
   private final ItemBasedSteering steering;

   public Pig(EntityType<? extends Pig> var1, Level var2) {
      super(var1, var2);
      this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, (var0) -> var0.is(Items.CARROT_ON_A_STICK), false));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, (var0) -> var0.is(ItemTags.PIG_FOOD), false));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      if (this.isSaddled()) {
         Entity var2 = this.getFirstPassenger();
         if (var2 instanceof Player) {
            Player var1 = (Player)var2;
            if (var1.isHolding(Items.CARROT_ON_A_STICK)) {
               return var1;
            }
         }
      }

      return super.getControllingPassenger();
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_BOOST_TIME.equals(var1) && this.level().isClientSide()) {
         this.steering.onSynced();
      }

      super.onSyncedDataUpdated(var1);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_BOOST_TIME, 0);
      var1.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), PigVariants.DEFAULT));
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      VariantUtils.writeVariant(var1, this.getVariant());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      VariantUtils.readVariant(var1, Registries.PIG_VARIANT).ifPresent(this::setVariant);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PIG_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PIG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PIG_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      boolean var3 = this.isFood(var1.getItemInHand(var2));
      if (!var3 && this.isSaddled() && !this.isVehicle() && !var1.isSecondaryUseActive()) {
         if (!this.level().isClientSide()) {
            var1.startRiding(this);
         }

         return InteractionResult.SUCCESS;
      } else {
         InteractionResult var4 = super.mobInteract(var1, var2);
         if (!var4.consumesAction()) {
            ItemStack var5 = var1.getItemInHand(var2);
            return (InteractionResult)(this.isEquippableInSlot(var5, EquipmentSlot.SADDLE) ? var5.interactLivingEntity(var1, this, var2) : InteractionResult.PASS);
         } else {
            return var4;
         }
      }
   }

   public boolean canUseSlot(EquipmentSlot var1) {
      if (var1 != EquipmentSlot.SADDLE) {
         return super.canUseSlot(var1);
      } else {
         return this.isAlive() && !this.isBaby();
      }
   }

   protected boolean canDispenserEquipIntoSlot(EquipmentSlot var1) {
      return var1 == EquipmentSlot.SADDLE || super.canDispenserEquipIntoSlot(var1);
   }

   protected Holder<SoundEvent> getEquipSound(EquipmentSlot var1, ItemStack var2, Equippable var3) {
      return (Holder<SoundEvent>)(var1 == EquipmentSlot.SADDLE ? SoundEvents.PIG_SADDLE : super.getEquipSound(var1, var2, var3));
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      Direction var2 = this.getMotionDirection();
      if (var2.getAxis() == Direction.Axis.Y) {
         return super.getDismountLocationForPassenger(var1);
      } else {
         int[][] var3 = DismountHelper.offsetsForDirection(var2);
         BlockPos var4 = this.blockPosition();
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
         UnmodifiableIterator var6 = var1.getDismountPoses().iterator();

         while(var6.hasNext()) {
            Pose var7 = (Pose)var6.next();
            AABB var8 = var1.getLocalBoundsForPose(var7);

            for(int[] var12 : var3) {
               var5.set(var4.getX() + var12[0], var4.getY(), var4.getZ() + var12[1]);
               double var13 = this.level().getBlockFloorHeight(var5);
               if (DismountHelper.isBlockFloorValid(var13)) {
                  Vec3 var15 = Vec3.upFromBottomCenterOf(var5, var13);
                  if (DismountHelper.canDismountTo(this.level(), var1, var8.move(var15))) {
                     var1.setPose(var7);
                     return var15;
                  }
               }
            }
         }

         return super.getDismountLocationForPassenger(var1);
      }
   }

   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      if (var1.getDifficulty() != Difficulty.PEACEFUL) {
         ZombifiedPiglin var3 = (ZombifiedPiglin)this.convertTo(EntityType.ZOMBIFIED_PIGLIN, ConversionParams.single(this, false, true), (var1x) -> {
            if (this.getMainHandItem().isEmpty()) {
               var1x.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            }

            var1x.setPersistenceRequired();
         });
         if (var3 == null) {
            super.thunderHit(var1, var2);
         }
      } else {
         super.thunderHit(var1, var2);
      }

   }

   protected void tickRidden(Player var1, Vec3 var2) {
      super.tickRidden(var1, var2);
      this.setRot(var1.getYRot(), var1.getXRot() * 0.5F);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
      this.steering.tickBoost();
   }

   protected Vec3 getRiddenInput(Player var1, Vec3 var2) {
      return new Vec3(0.0, 0.0, 1.0);
   }

   protected float getRiddenSpeed(Player var1) {
      return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225 * (double)this.steering.boostFactor());
   }

   public boolean boost() {
      return this.steering.boost(this.getRandom());
   }

   @Nullable
   public Pig getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Pig var3 = EntityType.PIG.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null && var2 instanceof Pig var4) {
         var3.setVariant(this.random.nextBoolean() ? this.getVariant() : var4.getVariant());
      }

      return var3;
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.PIG_FOOD);
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   private void setVariant(Holder<PigVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   public Holder<PigVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.PIG_VARIANT ? castComponentValue(var1, this.getVariant()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.PIG_VARIANT);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.PIG_VARIANT) {
         this.setVariant((Holder)castComponentValue(DataComponents.PIG_VARIANT, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      VariantUtils.selectVariantToSpawn(SpawnContext.create(var1, this.blockPosition()), Registries.PIG_VARIANT).ifPresent(this::setVariant);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_BOOST_TIME = SynchedEntityData.<Integer>defineId(Pig.class, EntityDataSerializers.INT);
      DATA_VARIANT_ID = SynchedEntityData.<Holder<PigVariant>>defineId(Pig.class, EntityDataSerializers.PIG_VARIANT);
   }
}
