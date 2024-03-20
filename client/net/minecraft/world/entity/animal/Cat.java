package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

public class Cat extends TamableAnimal implements VariantHolder<Holder<CatVariant>> {
   public static final double TEMPT_SPEED_MOD = 0.6;
   public static final double WALK_SPEED_MOD = 0.8;
   public static final double SPRINT_SPEED_MOD = 1.33;
   private static final EntityDataAccessor<Holder<CatVariant>> DATA_VARIANT_ID = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.CAT_VARIANT);
   private static final EntityDataAccessor<Boolean> IS_LYING = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> RELAX_STATE_ONE = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.INT);
   private static final ResourceKey<CatVariant> DEFAULT_VARIANT = CatVariant.BLACK;
   @Nullable
   private Cat.CatAvoidEntityGoal<Player> avoidPlayersGoal;
   @Nullable
   private TemptGoal temptGoal;
   private float lieDownAmount;
   private float lieDownAmountO;
   private float lieDownAmountTail;
   private float lieDownAmountOTail;
   private float relaxStateOneAmount;
   private float relaxStateOneAmountO;

   public Cat(EntityType<? extends Cat> var1, Level var2) {
      super(var1, var2);
      this.reassessTameGoals();
   }

   public ResourceLocation getTextureId() {
      return ((CatVariant)this.getVariant().value()).texture();
   }

   @Override
   protected void registerGoals() {
      this.temptGoal = new Cat.CatTemptGoal(this, 0.6, var0 -> var0.is(ItemTags.CAT_FOOD), true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(3, new Cat.CatRelaxOnOwnerGoal(this));
      this.goalSelector.addGoal(4, this.temptGoal);
      this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1, 8));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F, false));
      this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8));
      this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(10, new BreedGoal(this, 0.8));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 0.8, 1.0000001E-5F));
      this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, Rabbit.class, false, null));
      this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public Holder<CatVariant> getVariant() {
      return this.entityData.get(DATA_VARIANT_ID);
   }

   public void setVariant(Holder<CatVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   public void setLying(boolean var1) {
      this.entityData.set(IS_LYING, var1);
   }

   public boolean isLying() {
      return this.entityData.get(IS_LYING);
   }

   void setRelaxStateOne(boolean var1) {
      this.entityData.set(RELAX_STATE_ONE, var1);
   }

   boolean isRelaxStateOne() {
      return this.entityData.get(RELAX_STATE_ONE);
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
   }

   private void setCollarColor(DyeColor var1) {
      this.entityData.set(DATA_COLLAR_COLOR, var1.getId());
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT_ID, BuiltInRegistries.CAT_VARIANT.getHolderOrThrow(DEFAULT_VARIANT));
      var1.define(IS_LYING, false);
      var1.define(RELAX_STATE_ONE, false);
      var1.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("variant", this.getVariant().unwrapKey().orElse(DEFAULT_VARIANT).location().toString());
      var1.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      Optional.ofNullable(ResourceLocation.tryParse(var1.getString("variant")))
         .map(var0 -> ResourceKey.create(Registries.CAT_VARIANT, var0))
         .flatMap(BuiltInRegistries.CAT_VARIANT::getHolder)
         .ifPresent(this::setVariant);
      if (var1.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(var1.getInt("CollarColor")));
      }
   }

   @Override
   public void customServerAiStep() {
      if (this.getMoveControl().hasWanted()) {
         double var1 = this.getMoveControl().getSpeedModifier();
         if (var1 == 0.6) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
         } else if (var1 == 1.33) {
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
   @Override
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

   @Override
   public int getAmbientSoundInterval() {
      return 120;
   }

   public void hiss() {
      this.makeSound(SoundEvents.CAT_HISS);
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CAT_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.CAT_DEATH;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.ATTACK_DAMAGE, 3.0);
   }

   @Override
   protected void usePlayerItem(Player var1, InteractionHand var2, ItemStack var3) {
      if (this.isFood(var3)) {
         this.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
      }

      super.usePlayerItem(var1, var2, var3);
   }

   private float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      return var1.hurt(this.damageSources().mobAttack(this), this.getAttackDamage());
   }

   @Override
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

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   public Cat getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Cat var3 = EntityType.CAT.create(var1);
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

   @Override
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
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      var4 = super.finalizeSpawn(var1, var2, var3, var4);
      boolean var5 = var1.getMoonBrightness() > 0.9F;
      TagKey var6 = var5 ? CatVariantTags.FULL_MOON_SPAWNS : CatVariantTags.DEFAULT_SPAWNS;
      BuiltInRegistries.CAT_VARIANT.getRandomElementOf(var6, var1.getRandom()).ifPresent(this::setVariant);
      ServerLevel var7 = var1.getLevel();
      if (var7.structureManager().getStructureWithPieceAt(this.blockPosition(), StructureTags.CATS_SPAWN_AS_BLACK).isValid()) {
         this.setVariant(BuiltInRegistries.CAT_VARIANT.getHolderOrThrow(CatVariant.ALL_BLACK));
         this.setPersistenceRequired();
      }

      return var4;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      Item var4 = var3.getItem();
      if (this.isTame()) {
         if (this.isOwnedBy(var1)) {
            if (var4 instanceof DyeItem var5) {
               DyeColor var6 = var5.getDyeColor();
               if (var6 != this.getCollarColor()) {
                  if (!this.level().isClientSide()) {
                     this.setCollarColor(var6);
                     var3.consume(1, var1);
                     this.setPersistenceRequired();
                  }

                  return InteractionResult.sidedSuccess(this.level().isClientSide());
               }
            } else if (this.isFood(var3) && this.getHealth() < this.getMaxHealth()) {
               if (!this.level().isClientSide()) {
                  this.usePlayerItem(var1, var2, var3);
                  FoodProperties var9 = var3.get(DataComponents.FOOD);
                  this.heal(var9 != null ? (float)var9.nutrition() : 1.0F);
               }

               return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            InteractionResult var7 = super.mobInteract(var1, var2);
            if (!var7.consumesAction()) {
               this.setOrderedToSit(!this.isOrderedToSit());
               return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            return var7;
         }
      } else if (this.isFood(var3)) {
         if (!this.level().isClientSide()) {
            this.usePlayerItem(var1, var2, var3);
            this.tryToTame(var1);
            this.setPersistenceRequired();
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide());
      }

      InteractionResult var8 = super.mobInteract(var1, var2);
      if (var8.consumesAction()) {
         this.setPersistenceRequired();
      }

      return var8;
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.CAT_FOOD);
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return !this.isTame() && this.tickCount > 2400;
   }

   @Override
   public void setTame(boolean var1, boolean var2) {
      super.setTame(var1, var2);
      this.reassessTameGoals();
   }

   protected void reassessTameGoals() {
      if (this.avoidPlayersGoal == null) {
         this.avoidPlayersGoal = new Cat.CatAvoidEntityGoal<>(this, Player.class, 16.0F, 0.8, 1.33);
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

   @Override
   public boolean isSteppingCarefully() {
      return this.isCrouching() || super.isSteppingCarefully();
   }

   static class CatAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final Cat cat;

      public CatAvoidEntityGoal(Cat var1, Class<T> var2, float var3, double var4, double var6) {
         super(var1, var2, var3, var4, var6, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
         this.cat = var1;
      }

      @Override
      public boolean canUse() {
         return !this.cat.isTame() && super.canUse();
      }

      @Override
      public boolean canContinueToUse() {
         return !this.cat.isTame() && super.canContinueToUse();
      }
   }

   static class CatRelaxOnOwnerGoal extends Goal {
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

      @Override
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
                  this.goalPos = var3.getOptionalValue(BedBlock.FACING).map(var1x -> var2.relative(var1x.getOpposite())).orElseGet(() -> new BlockPos(var2));
                  return !this.spaceIsOccupied();
               }
            }

            return false;
         }
      }

      private boolean spaceIsOccupied() {
         for(Cat var3 : this.cat.level().getEntitiesOfClass(Cat.class, new AABB(this.goalPos).inflate(2.0))) {
            if (var3 != this.cat && (var3.isLying() || var3.isRelaxStateOne())) {
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean canContinueToUse() {
         return this.cat.isTame()
            && !this.cat.isOrderedToSit()
            && this.ownerPlayer != null
            && this.ownerPlayer.isSleeping()
            && this.goalPos != null
            && !this.spaceIsOccupied();
      }

      @Override
      public void start() {
         if (this.goalPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), 1.100000023841858);
         }
      }

      @Override
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
         this.cat
            .randomTeleport(
               (double)(var2.getX() + var1.nextInt(11) - 5), (double)(var2.getY() + var1.nextInt(5) - 2), (double)(var2.getZ() + var1.nextInt(11) - 5), false
            );
         var2.set(this.cat.blockPosition());
         LootTable var3 = this.cat.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.CAT_MORNING_GIFT);
         LootParams var4 = new LootParams.Builder((ServerLevel)this.cat.level())
            .withParameter(LootContextParams.ORIGIN, this.cat.position())
            .withParameter(LootContextParams.THIS_ENTITY, this.cat)
            .create(LootContextParamSets.GIFT);

         for(ItemStack var7 : var3.getRandomItems(var4)) {
            this.cat
               .level()
               .addFreshEntity(
                  new ItemEntity(
                     this.cat.level(),
                     (double)var2.getX() - (double)Mth.sin(this.cat.yBodyRot * 0.017453292F),
                     (double)var2.getY(),
                     (double)var2.getZ() + (double)Mth.cos(this.cat.yBodyRot * 0.017453292F),
                     var7
                  )
               );
         }
      }

      @Override
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

   static class CatTemptGoal extends TemptGoal {
      @Nullable
      private Player selectedPlayer;
      private final Cat cat;

      public CatTemptGoal(Cat var1, double var2, Predicate<ItemStack> var4, boolean var5) {
         super(var1, var2, var4, var5);
         this.cat = var1;
      }

      @Override
      public void tick() {
         super.tick();
         if (this.selectedPlayer == null && this.mob.getRandom().nextInt(this.adjustedTickDelay(600)) == 0) {
            this.selectedPlayer = this.player;
         } else if (this.mob.getRandom().nextInt(this.adjustedTickDelay(500)) == 0) {
            this.selectedPlayer = null;
         }
      }

      @Override
      protected boolean canScare() {
         return this.selectedPlayer != null && this.selectedPlayer.equals(this.player) ? false : super.canScare();
      }

      @Override
      public boolean canUse() {
         return super.canUse() && !this.cat.isTame();
      }
   }
}
