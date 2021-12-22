package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

public class Cat extends TamableAnimal {
   public static final double TEMPT_SPEED_MOD = 0.6D;
   public static final double WALK_SPEED_MOD = 0.8D;
   public static final double SPRINT_SPEED_MOD = 1.33D;
   private static final Ingredient TEMPT_INGREDIENT;
   private static final EntityDataAccessor<Integer> DATA_TYPE_ID;
   private static final EntityDataAccessor<Boolean> IS_LYING;
   private static final EntityDataAccessor<Boolean> RELAX_STATE_ONE;
   private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR;
   public static final int TYPE_TABBY = 0;
   public static final int TYPE_BLACK = 1;
   public static final int TYPE_RED = 2;
   public static final int TYPE_SIAMESE = 3;
   public static final int TYPE_BRITISH = 4;
   public static final int TYPE_CALICO = 5;
   public static final int TYPE_PERSIAN = 6;
   public static final int TYPE_RAGDOLL = 7;
   public static final int TYPE_WHITE = 8;
   public static final int TYPE_JELLIE = 9;
   public static final int TYPE_ALL_BLACK = 10;
   private static final int NUMBER_OF_CAT_TYPES = 11;
   private static final int NUMBER_OF_CAT_TYPES_EXCEPT_ALL_BLACK = 10;
   public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE;
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
   }

   public ResourceLocation getResourceLocation() {
      return (ResourceLocation)TEXTURE_BY_TYPE.getOrDefault(this.getCatType(), (ResourceLocation)TEXTURE_BY_TYPE.get(0));
   }

   protected void registerGoals() {
      this.temptGoal = new Cat.CatTemptGoal(this, 0.6D, TEMPT_INGREDIENT, true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new Cat.CatRelaxOnOwnerGoal(this));
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1D, 8));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
      this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8D));
      this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(10, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 0.8D, 1.0000001E-5F));
      this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.targetSelector.addGoal(1, new NonTameRandomTargetGoal(this, Rabbit.class, false, (Predicate)null));
      this.targetSelector.addGoal(1, new NonTameRandomTargetGoal(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public int getCatType() {
      return (Integer)this.entityData.get(DATA_TYPE_ID);
   }

   public void setCatType(int var1) {
      if (var1 < 0 || var1 >= 11) {
         var1 = this.random.nextInt(10);
      }

      this.entityData.set(DATA_TYPE_ID, var1);
   }

   public void setLying(boolean var1) {
      this.entityData.set(IS_LYING, var1);
   }

   public boolean isLying() {
      return (Boolean)this.entityData.get(IS_LYING);
   }

   public void setRelaxStateOne(boolean var1) {
      this.entityData.set(RELAX_STATE_ONE, var1);
   }

   public boolean isRelaxStateOne() {
      return (Boolean)this.entityData.get(RELAX_STATE_ONE);
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId((Integer)this.entityData.get(DATA_COLLAR_COLOR));
   }

   public void setCollarColor(DyeColor var1) {
      this.entityData.set(DATA_COLLAR_COLOR, var1.getId());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE_ID, 1);
      this.entityData.define(IS_LYING, false);
      this.entityData.define(RELAX_STATE_ONE, false);
      this.entityData.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("CatType", this.getCatType());
      var1.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setCatType(var1.getInt("CatType"));
      if (var1.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(var1.getInt("CollarColor")));
      }

   }

   public void customServerAiStep() {
      if (this.getMoveControl().hasWanted()) {
         double var1 = this.getMoveControl().getSpeedModifier();
         if (var1 == 0.6D) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
         } else if (var1 == 1.33D) {
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
      this.playSound(SoundEvents.CAT_HISS, this.getSoundVolume(), this.getVoicePitch());
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CAT_DEATH;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896D).add(Attributes.ATTACK_DAMAGE, 3.0D);
   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      return false;
   }

   protected void usePlayerItem(Player var1, InteractionHand var2, ItemStack var3) {
      if (this.isFood(var3)) {
         this.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
      }

      super.usePlayerItem(var1, var2, var3);
   }

   private float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   public boolean doHurtTarget(Entity var1) {
      return var1.hurt(DamageSource.mobAttack(this), this.getAttackDamage());
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

   public Cat getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Cat var3 = (Cat)EntityType.CAT.create(var1);
      if (var2 instanceof Cat) {
         if (this.random.nextBoolean()) {
            var3.setCatType(this.getCatType());
         } else {
            var3.setCatType(((Cat)var2).getCatType());
         }

         if (this.isTame()) {
            var3.setOwnerUUID(this.getOwnerUUID());
            var3.setTame(true);
            if (this.random.nextBoolean()) {
               var3.setCollarColor(this.getCollarColor());
            } else {
               var3.setCollarColor(((Cat)var2).getCollarColor());
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
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      var4 = super.finalizeSpawn(var1, var2, var3, var4, var5);
      if (var1.getMoonBrightness() > 0.9F) {
         this.setCatType(this.random.nextInt(11));
      } else {
         this.setCatType(this.random.nextInt(10));
      }

      ServerLevel var6 = var1.getLevel();
      if (var6 instanceof ServerLevel && ((ServerLevel)var6).structureFeatureManager().getStructureWithPieceAt(this.blockPosition(), StructureFeature.SWAMP_HUT).isValid()) {
         this.setCatType(10);
         this.setPersistenceRequired();
      }

      return var4;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      Item var4 = var3.getItem();
      if (this.level.isClientSide) {
         if (this.isTame() && this.isOwnedBy(var1)) {
            return InteractionResult.SUCCESS;
         } else {
            return !this.isFood(var3) || !(this.getHealth() < this.getMaxHealth()) && this.isTame() ? InteractionResult.PASS : InteractionResult.SUCCESS;
         }
      } else {
         InteractionResult var6;
         if (this.isTame()) {
            if (this.isOwnedBy(var1)) {
               if (!(var4 instanceof DyeItem)) {
                  if (var4.isEdible() && this.isFood(var3) && this.getHealth() < this.getMaxHealth()) {
                     this.usePlayerItem(var1, var2, var3);
                     this.heal((float)var4.getFoodProperties().getNutrition());
                     return InteractionResult.CONSUME;
                  }

                  var6 = super.mobInteract(var1, var2);
                  if (!var6.consumesAction() || this.isBaby()) {
                     this.setOrderedToSit(!this.isOrderedToSit());
                  }

                  return var6;
               }

               DyeColor var5 = ((DyeItem)var4).getDyeColor();
               if (var5 != this.getCollarColor()) {
                  this.setCollarColor(var5);
                  if (!var1.getAbilities().instabuild) {
                     var3.shrink(1);
                  }

                  this.setPersistenceRequired();
                  return InteractionResult.CONSUME;
               }
            }
         } else if (this.isFood(var3)) {
            this.usePlayerItem(var1, var2, var3);
            if (this.random.nextInt(3) == 0) {
               this.tame(var1);
               this.setOrderedToSit(true);
               this.level.broadcastEntityEvent(this, (byte)7);
            } else {
               this.level.broadcastEntityEvent(this, (byte)6);
            }

            this.setPersistenceRequired();
            return InteractionResult.CONSUME;
         }

         var6 = super.mobInteract(var1, var2);
         if (var6.consumesAction()) {
            this.setPersistenceRequired();
         }

         return var6;
      }
   }

   public boolean isFood(ItemStack var1) {
      return TEMPT_INGREDIENT.test(var1);
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return var2.height * 0.5F;
   }

   public boolean removeWhenFarAway(double var1) {
      return !this.isTame() && this.tickCount > 2400;
   }

   protected void reassessTameGoals() {
      if (this.avoidPlayersGoal == null) {
         this.avoidPlayersGoal = new Cat.CatAvoidEntityGoal(this, Player.class, 16.0F, 0.8D, 1.33D);
      }

      this.goalSelector.removeGoal(this.avoidPlayersGoal);
      if (!this.isTame()) {
         this.goalSelector.addGoal(4, this.avoidPlayersGoal);
      }

   }

   public boolean isSteppingCarefully() {
      return this.getPose() == Pose.CROUCHING || super.isSteppingCarefully();
   }

   // $FF: synthetic method
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      TEMPT_INGREDIENT = Ingredient.method_110(Items.COD, Items.SALMON);
      DATA_TYPE_ID = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.INT);
      IS_LYING = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
      RELAX_STATE_ONE = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
      DATA_COLLAR_COLOR = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.INT);
      TEXTURE_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (var0) -> {
         var0.put(0, new ResourceLocation("textures/entity/cat/tabby.png"));
         var0.put(1, new ResourceLocation("textures/entity/cat/black.png"));
         var0.put(2, new ResourceLocation("textures/entity/cat/red.png"));
         var0.put(3, new ResourceLocation("textures/entity/cat/siamese.png"));
         var0.put(4, new ResourceLocation("textures/entity/cat/british_shorthair.png"));
         var0.put(5, new ResourceLocation("textures/entity/cat/calico.png"));
         var0.put(6, new ResourceLocation("textures/entity/cat/persian.png"));
         var0.put(7, new ResourceLocation("textures/entity/cat/ragdoll.png"));
         var0.put(8, new ResourceLocation("textures/entity/cat/white.png"));
         var0.put(9, new ResourceLocation("textures/entity/cat/jellie.png"));
         var0.put(10, new ResourceLocation("textures/entity/cat/all_black.png"));
      });
   }

   private static class CatTemptGoal extends TemptGoal {
      @Nullable
      private Player selectedPlayer;
      private final Cat cat;

      public CatTemptGoal(Cat var1, double var2, Ingredient var4, boolean var5) {
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

               if (this.cat.distanceToSqr(this.ownerPlayer) > 100.0D) {
                  return false;
               }

               BlockPos var2 = this.ownerPlayer.blockPosition();
               BlockState var3 = this.cat.level.getBlockState(var2);
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
         List var1 = this.cat.level.getEntitiesOfClass(Cat.class, (new AABB(this.goalPos)).inflate(2.0D));
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
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), 1.100000023841858D);
         }

      }

      public void stop() {
         this.cat.setLying(false);
         float var1 = this.cat.level.getTimeOfDay(1.0F);
         if (this.ownerPlayer.getSleepTimer() >= 100 && (double)var1 > 0.77D && (double)var1 < 0.8D && (double)this.cat.level.getRandom().nextFloat() < 0.7D) {
            this.giveMorningGift();
         }

         this.onBedTicks = 0;
         this.cat.setRelaxStateOne(false);
         this.cat.getNavigation().stop();
      }

      private void giveMorningGift() {
         Random var1 = this.cat.getRandom();
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
         var2.set(this.cat.blockPosition());
         this.cat.randomTeleport((double)(var2.getX() + var1.nextInt(11) - 5), (double)(var2.getY() + var1.nextInt(5) - 2), (double)(var2.getZ() + var1.nextInt(11) - 5), false);
         var2.set(this.cat.blockPosition());
         LootTable var3 = this.cat.level.getServer().getLootTables().get(BuiltInLootTables.CAT_MORNING_GIFT);
         LootContext.Builder var4 = (new LootContext.Builder((ServerLevel)this.cat.level)).withParameter(LootContextParams.ORIGIN, this.cat.position()).withParameter(LootContextParams.THIS_ENTITY, this.cat).withRandom(var1);
         List var5 = var3.getRandomItems(var4.create(LootContextParamSets.GIFT));
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            ItemStack var7 = (ItemStack)var6.next();
            this.cat.level.addFreshEntity(new ItemEntity(this.cat.level, (double)var2.getX() - (double)Mth.sin(this.cat.yBodyRot * 0.017453292F), (double)var2.getY(), (double)var2.getZ() + (double)Mth.cos(this.cat.yBodyRot * 0.017453292F), var7));
         }

      }

      public void tick() {
         if (this.ownerPlayer != null && this.goalPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), 1.100000023841858D);
            if (this.cat.distanceToSqr(this.ownerPlayer) < 2.5D) {
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
