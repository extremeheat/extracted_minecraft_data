package net.minecraft.world.entity.animal;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.tags.CatVariantTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;

public class Cat extends TamableAnimal implements VariantHolder<Holder<CatVariant>> {
   public static final double TEMPT_SPEED_MOD = 0.6;
   public static final double WALK_SPEED_MOD = 0.8;
   public static final double SPRINT_SPEED_MOD = 1.33;
   private static final EntityDataAccessor<Holder<CatVariant>> DATA_VARIANT_ID;
   private static final EntityDataAccessor<Boolean> IS_LYING;
   private static final EntityDataAccessor<Boolean> RELAX_STATE_ONE;
   private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR;
   private static final ResourceKey<CatVariant> DEFAULT_VARIANT;
   @Nullable
   private CatAvoidEntityGoal<Player> avoidPlayersGoal;
   @Nullable
   private TemptGoal temptGoal;
   private float lieDownAmount;
   private float lieDownAmountO;
   private float lieDownAmountTail;
   private float lieDownAmountOTail;
   private boolean isLyingOnTopOfSleepingPlayer;
   private float relaxStateOneAmount;
   private float relaxStateOneAmountO;

   public Cat(EntityType<? extends Cat> var1, Level var2) {
      super(var1, var2);
      this.reassessTameGoals();
   }

   protected void registerGoals() {
      this.temptGoal = new CatTemptGoal(this, 0.6, (var0) -> {
         return var0.is(ItemTags.CAT_FOOD);
      }, true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new TamableAnimal.TamableAnimalPanicGoal(1.5));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(3, new CatRelaxOnOwnerGoal(this));
      this.goalSelector.addGoal(4, this.temptGoal);
      this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1, 8));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
      this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8));
      this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(10, new BreedGoal(this, 0.8));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 0.8, 1.0000001E-5F));
      this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.targetSelector.addGoal(1, new NonTameRandomTargetGoal(this, Rabbit.class, false, (TargetingConditions.Selector)null));
      this.targetSelector.addGoal(1, new NonTameRandomTargetGoal(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public Holder<CatVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   public void setVariant(Holder<CatVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   public void setLying(boolean var1) {
      this.entityData.set(IS_LYING, var1);
   }

   public boolean isLying() {
      return (Boolean)this.entityData.get(IS_LYING);
   }

   void setRelaxStateOne(boolean var1) {
      this.entityData.set(RELAX_STATE_ONE, var1);
   }

   boolean isRelaxStateOne() {
      return (Boolean)this.entityData.get(RELAX_STATE_ONE);
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId((Integer)this.entityData.get(DATA_COLLAR_COLOR));
   }

   private void setCollarColor(DyeColor var1) {
      this.entityData.set(DATA_COLLAR_COLOR, var1.getId());
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT_ID, BuiltInRegistries.CAT_VARIANT.getOrThrow(DEFAULT_VARIANT));
      var1.define(IS_LYING, false);
      var1.define(RELAX_STATE_ONE, false);
      var1.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("variant", ((ResourceKey)this.getVariant().unwrapKey().orElse(DEFAULT_VARIANT)).location().toString());
      var1.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(var1.getString("variant"))).map((var0) -> {
         return ResourceKey.create(Registries.CAT_VARIANT, var0);
      });
      Registry var10001 = BuiltInRegistries.CAT_VARIANT;
      Objects.requireNonNull(var10001);
      var10000.flatMap(var10001::get).ifPresent(this::setVariant);
      if (var1.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(var1.getInt("CollarColor")));
      }

   }

   public void customServerAiStep(ServerLevel var1) {
      if (this.getMoveControl().hasWanted()) {
         double var2 = this.getMoveControl().getSpeedModifier();
         if (var2 == 0.6) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
         } else if (var2 == 1.33) {
            this.setPose(Pose.STANDING);
            this.setSprinting(true);
         } else {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
         }
      } else {
         this.setPose(Pose.STANDING);
         this.setSprinting(false);
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isTame()) {
         if (this.isInLove()) {
            return SoundEvents.CAT_PURR;
         } else {
            return this.random.nextInt(4) == 0 ? SoundEvents.CAT_PURREOW : SoundEvents.CAT_AMBIENT;
         }
      } else {
         return SoundEvents.CAT_STRAY_AMBIENT;
      }
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public void hiss() {
      this.makeSound(SoundEvents.CAT_HISS);
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CAT_DEATH;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.ATTACK_DAMAGE, 3.0);
   }

   protected void playEatingSound() {
      this.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTame() && this.tickCount % 100 == 0) {
         this.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
      }

      this.handleLieDown();
   }

   private void handleLieDown() {
      if ((this.isLying() || this.isRelaxStateOne()) && this.tickCount % 5 == 0) {
         this.playSound(SoundEvents.CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
      }

      this.updateLieDownAmount();
      this.updateRelaxStateOneAmount();
      this.isLyingOnTopOfSleepingPlayer = false;
      if (this.isLying()) {
         BlockPos var1 = this.blockPosition();
         List var2 = this.level().getEntitiesOfClass(Player.class, (new AABB(var1)).inflate(2.0, 2.0, 2.0));
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Player var4 = (Player)var3.next();
            if (var4.isSleeping()) {
               this.isLyingOnTopOfSleepingPlayer = true;
               break;
            }
         }
      }

   }

   public boolean isLyingOnTopOfSleepingPlayer() {
      return this.isLyingOnTopOfSleepingPlayer;
   }

   private void updateLieDownAmount() {
      this.lieDownAmountO = this.lieDownAmount;
      this.lieDownAmountOTail = this.lieDownAmountTail;
      if (this.isLying()) {
         this.lieDownAmount = Math.min(1.0F, this.lieDownAmount + 0.15F);
         this.lieDownAmountTail = Math.min(1.0F, this.lieDownAmountTail + 0.08F);
      } else {
         this.lieDownAmount = Math.max(0.0F, this.lieDownAmount - 0.22F);
         this.lieDownAmountTail = Math.max(0.0F, this.lieDownAmountTail - 0.13F);
      }

   }

   private void updateRelaxStateOneAmount() {
      this.relaxStateOneAmountO = this.relaxStateOneAmount;
      if (this.isRelaxStateOne()) {
         this.relaxStateOneAmount = Math.min(1.0F, this.relaxStateOneAmount + 0.1F);
      } else {
         this.relaxStateOneAmount = Math.max(0.0F, this.relaxStateOneAmount - 0.13F);
      }

   }

   public float getLieDownAmount(float var1) {
      return Mth.lerp(var1, this.lieDownAmountO, this.lieDownAmount);
   }

   public float getLieDownAmountTail(float var1) {
      return Mth.lerp(var1, this.lieDownAmountOTail, this.lieDownAmountTail);
   }

   public float getRelaxStateOneAmount(float var1) {
      return Mth.lerp(var1, this.relaxStateOneAmountO, this.relaxStateOneAmount);
   }

   @Nullable
   public Cat getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Cat var3 = (Cat)EntityType.CAT.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null && var2 instanceof Cat var4) {
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

   public boolean canMate(Animal var1) {
      if (!this.isTame()) {
         return false;
      } else if (!(var1 instanceof Cat)) {
         return false;
      } else {
         Cat var2 = (Cat)var1;
         return var2.isTame() && super.canMate(var1);
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      var4 = super.finalizeSpawn(var1, var2, var3, var4);
      boolean var5 = var1.getMoonBrightness() > 0.9F;
      TagKey var6 = var5 ? CatVariantTags.FULL_MOON_SPAWNS : CatVariantTags.DEFAULT_SPAWNS;
      BuiltInRegistries.CAT_VARIANT.getRandomElementOf(var6, var1.getRandom()).ifPresent(this::setVariant);
      ServerLevel var7 = var1.getLevel();
      if (var7.structureManager().getStructureWithPieceAt(this.blockPosition(), StructureTags.CATS_SPAWN_AS_BLACK).isValid()) {
         this.setVariant((Holder)BuiltInRegistries.CAT_VARIANT.getOrThrow(CatVariant.ALL_BLACK));
         this.setPersistenceRequired();
      }

      return var4;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      Item var4 = var3.getItem();
      InteractionResult var7;
      if (this.isTame()) {
         if (this.isOwnedBy(var1)) {
            if (var4 instanceof DyeItem) {
               DyeItem var5 = (DyeItem)var4;
               DyeColor var6 = var5.getDyeColor();
               if (var6 != this.getCollarColor()) {
                  if (!this.level().isClientSide()) {
                     this.setCollarColor(var6);
                     var3.consume(1, var1);
                     this.setPersistenceRequired();
                  }

                  return InteractionResult.SUCCESS;
               }
            } else if (this.isFood(var3) && this.getHealth() < this.getMaxHealth()) {
               if (!this.level().isClientSide()) {
                  this.usePlayerItem(var1, var2, var3);
                  FoodProperties var8 = (FoodProperties)var3.get(DataComponents.FOOD);
                  this.heal(var8 != null ? (float)var8.nutrition() : 1.0F);
                  this.playEatingSound();
               }

               return InteractionResult.SUCCESS;
            }

            var7 = super.mobInteract(var1, var2);
            if (!var7.consumesAction()) {
               this.setOrderedToSit(!this.isOrderedToSit());
               return InteractionResult.SUCCESS;
            }

            return var7;
         }
      } else if (this.isFood(var3)) {
         if (!this.level().isClientSide()) {
            this.usePlayerItem(var1, var2, var3);
            this.tryToTame(var1);
            this.setPersistenceRequired();
            this.playEatingSound();
         }

         return InteractionResult.SUCCESS;
      }

      var7 = super.mobInteract(var1, var2);
      if (var7.consumesAction()) {
         this.setPersistenceRequired();
      }

      return var7;
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.CAT_FOOD);
   }

   public boolean removeWhenFarAway(double var1) {
      return !this.isTame() && this.tickCount > 2400;
   }

   public void setTame(boolean var1, boolean var2) {
      super.setTame(var1, var2);
      this.reassessTameGoals();
   }

   protected void reassessTameGoals() {
      if (this.avoidPlayersGoal == null) {
         this.avoidPlayersGoal = new CatAvoidEntityGoal(this, Player.class, 16.0F, 0.8, 1.33);
      }

      this.goalSelector.removeGoal(this.avoidPlayersGoal);
      if (!this.isTame()) {
         this.goalSelector.addGoal(4, this.avoidPlayersGoal);
      }

   }

   private void tryToTame(Player var1) {
      if (this.random.nextInt(3) == 0) {
         this.tame(var1);
         this.setOrderedToSit(true);
         this.level().broadcastEntityEvent(this, (byte)7);
      } else {
         this.level().broadcastEntityEvent(this, (byte)6);
      }

   }

   public boolean isSteppingCarefully() {
      return this.isCrouching() || super.isSteppingCarefully();
   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   // $FF: synthetic method
   public Object getVariant() {
      return this.getVariant();
   }

   // $FF: synthetic method
   public void setVariant(final Object var1) {
      this.setVariant((Holder)var1);
   }

   static {
      DATA_VARIANT_ID = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.CAT_VARIANT);
      IS_LYING = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
      RELAX_STATE_ONE = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
      DATA_COLLAR_COLOR = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.INT);
      DEFAULT_VARIANT = CatVariant.BLACK;
   }

   private static class CatTemptGoal extends TemptGoal {
      @Nullable
      private Player selectedPlayer;
      private final Cat cat;

      public CatTemptGoal(Cat var1, double var2, Predicate<ItemStack> var4, boolean var5) {
         super(var1, var2, var4, var5);
         this.cat = var1;
      }

      public void tick() {
         super.tick();
         if (this.selectedPlayer == null && this.mob.getRandom().nextInt(this.adjustedTickDelay(600)) == 0) {
            this.selectedPlayer = this.player;
         } else if (this.mob.getRandom().nextInt(this.adjustedTickDelay(500)) == 0) {
            this.selectedPlayer = null;
         }

      }

      protected boolean canScare() {
         return this.selectedPlayer != null && this.selectedPlayer.equals(this.player) ? false : super.canScare();
      }

      public boolean canUse() {
         return super.canUse() && !this.cat.isTame();
      }
   }

   private static class CatRelaxOnOwnerGoal extends Goal {
      private final Cat cat;
      @Nullable
      private Player ownerPlayer;
      @Nullable
      private BlockPos goalPos;
      private int onBedTicks;

      public CatRelaxOnOwnerGoal(Cat var1) {
         super();
         this.cat = var1;
      }

      public boolean canUse() {
         if (!this.cat.isTame()) {
            return false;
         } else if (this.cat.isOrderedToSit()) {
            return false;
         } else {
            LivingEntity var1 = this.cat.getOwner();
            if (var1 instanceof Player) {
               this.ownerPlayer = (Player)var1;
               if (!var1.isSleeping()) {
                  return false;
               }

               if (this.cat.distanceToSqr(this.ownerPlayer) > 100.0) {
                  return false;
               }

               BlockPos var2 = this.ownerPlayer.blockPosition();
               BlockState var3 = this.cat.level().getBlockState(var2);
               if (var3.is(BlockTags.BEDS)) {
                  this.goalPos = (BlockPos)var3.getOptionalValue(BedBlock.FACING).map((var1x) -> {
                     return var2.relative(var1x.getOpposite());
                  }).orElseGet(() -> {
                     return new BlockPos(var2);
                  });
                  return !this.spaceIsOccupied();
               }
            }

            return false;
         }
      }

      private boolean spaceIsOccupied() {
         List var1 = this.cat.level().getEntitiesOfClass(Cat.class, (new AABB(this.goalPos)).inflate(2.0));
         Iterator var2 = var1.iterator();

         Cat var3;
         do {
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               var3 = (Cat)var2.next();
            } while(var3 == this.cat);
         } while(!var3.isLying() && !var3.isRelaxStateOne());

         return true;
      }

      public boolean canContinueToUse() {
         return this.cat.isTame() && !this.cat.isOrderedToSit() && this.ownerPlayer != null && this.ownerPlayer.isSleeping() && this.goalPos != null && !this.spaceIsOccupied();
      }

      public void start() {
         if (this.goalPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), 1.100000023841858);
         }

      }

      public void stop() {
         this.cat.setLying(false);
         float var1 = this.cat.level().getTimeOfDay(1.0F);
         if (this.ownerPlayer.getSleepTimer() >= 100 && (double)var1 > 0.77 && (double)var1 < 0.8 && (double)this.cat.level().getRandom().nextFloat() < 0.7) {
            this.giveMorningGift();
         }

         this.onBedTicks = 0;
         this.cat.setRelaxStateOne(false);
         this.cat.getNavigation().stop();
      }

      private void giveMorningGift() {
         RandomSource var1 = this.cat.getRandom();
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
         var2.set(this.cat.isLeashed() ? this.cat.getLeashHolder().blockPosition() : this.cat.blockPosition());
         this.cat.randomTeleport((double)(var2.getX() + var1.nextInt(11) - 5), (double)(var2.getY() + var1.nextInt(5) - 2), (double)(var2.getZ() + var1.nextInt(11) - 5), false);
         var2.set(this.cat.blockPosition());
         this.cat.dropFromGiftLootTable(getServerLevel(this.cat), BuiltInLootTables.CAT_MORNING_GIFT, (var2x, var3) -> {
            var2x.addFreshEntity(new ItemEntity(var2x, (double)var2.getX() - (double)Mth.sin(this.cat.yBodyRot * 0.017453292F), (double)var2.getY(), (double)var2.getZ() + (double)Mth.cos(this.cat.yBodyRot * 0.017453292F), var3));
         });
      }

      public void tick() {
         if (this.ownerPlayer != null && this.goalPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), 1.100000023841858);
            if (this.cat.distanceToSqr(this.ownerPlayer) < 2.5) {
               ++this.onBedTicks;
               if (this.onBedTicks > this.adjustedTickDelay(16)) {
                  this.cat.setLying(true);
                  this.cat.setRelaxStateOne(false);
               } else {
                  this.cat.lookAt(this.ownerPlayer, 45.0F, 45.0F);
                  this.cat.setRelaxStateOne(true);
               }
            } else {
               this.cat.setLying(false);
            }
         }

      }
   }

   private static class CatAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final Cat cat;

      public CatAvoidEntityGoal(Cat var1, Class<T> var2, float var3, double var4, double var6) {
         Predicate var10006 = EntitySelector.NO_CREATIVE_OR_SPECTATOR;
         Objects.requireNonNull(var10006);
         super(var1, var2, var3, var4, var6, var10006::test);
         this.cat = var1;
      }

      public boolean canUse() {
         return !this.cat.isTame() && super.canUse();
      }

      public boolean canContinueToUse() {
         return !this.cat.isTame() && super.canContinueToUse();
      }
   }
}
