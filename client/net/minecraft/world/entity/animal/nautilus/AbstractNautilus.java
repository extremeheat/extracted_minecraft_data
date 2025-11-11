package net.minecraft.world.entity.animal.nautilus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class AbstractNautilus extends TamableAnimal implements HasCustomInventoryScreen, PlayerRideableJumping {
   public static final int INVENTORY_SLOT_OFFSET = 500;
   public static final int INVENTORY_ROWS = 3;
   public static final int SMALL_RESTRICTION_RADIUS = 16;
   public static final int LARGE_RESTRICTION_RADIUS = 32;
   public static final int RESTRICTION_RADIUS_BUFFER = 8;
   private static final int EFFECT_DURATION = 60;
   private static final int EFFECT_REFRESH_RATE = 40;
   private static final double NAUTILUS_WATER_RESISTANCE = 0.9;
   private static final float IN_WATER_SPEED_MODIFIER = 0.011F;
   private static final float RIDDEN_SPEED_MODIFIER_IN_WATER = 0.0325F;
   private static final float RIDDEN_SPEED_MODIFIER_ON_LAND = 0.02F;
   private static final EntityDataAccessor<Boolean> DASH;
   private static final int DASH_COOLDOWN_TICKS = 40;
   private static final int DASH_MINIMUM_DURATION_TICKS = 5;
   private static final float DASH_MOMENTUM_IN_WATER = 1.2F;
   private static final float DASH_MOMENTUM_ON_LAND = 0.5F;
   private int dashCooldown = 0;
   protected float playerJumpPendingScale;
   protected SimpleContainer inventory;
   private static final double BUBBLE_SPREAD_FACTOR = 0.8;
   private static final double BUBBLE_DIRECTION_SCALE = 1.1;
   private static final double BUBBLE_Y_OFFSET = 0.25;
   private static final double BUBBLE_PROBABILITY_MULTIPLIER = 2.0;
   private static final float BUBBLE_PROBABILITY_MIN = 0.15F;
   private static final float BUBBLE_PROBABILITY_MAX = 1.0F;

   protected AbstractNautilus(EntityType<? extends AbstractNautilus> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.011F, 0.0F, true);
      this.lookControl = new SmoothSwimmingLookControl(this, 10);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.createInventory();
   }

   public boolean isFood(ItemStack var1) {
      return !this.isTame() && !this.isBaby() ? var1.is(ItemTags.NAUTILUS_TAMING_ITEMS) : var1.is(ItemTags.NAUTILUS_FOOD);
   }

   protected void usePlayerItem(Player var1, InteractionHand var2, ItemStack var3) {
      if (var3.is(ItemTags.NAUTILUS_BUCKET_FOOD)) {
         var1.setItemInHand(var2, ItemUtils.createFilledResult(var3, var1, new ItemStack(Items.WATER_BUCKET)));
      } else {
         super.usePlayerItem(var1, var2, var3);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.30000001192092896);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   protected PathNavigation createNavigation(Level var1) {
      return new WaterBoundPathNavigation(this, var1);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   public static boolean checkNautilusSpawnRules(EntityType<? extends AbstractNautilus> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      int var5 = var1.getSeaLevel();
      int var6 = var5 - 25;
      return var3.getY() >= var6 && var3.getY() <= var5 - 5 && var1.getFluidState(var3.below()).is(FluidTags.WATER) && var1.getBlockState(var3.above()).is(Blocks.WATER);
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   public boolean canUseSlot(EquipmentSlot var1) {
      if (var1 != EquipmentSlot.SADDLE && var1 != EquipmentSlot.BODY) {
         return super.canUseSlot(var1);
      } else {
         return this.isAlive() && !this.isBaby() && this.isTame();
      }
   }

   protected boolean canAddPassenger(Entity var1) {
      return !this.isVehicle();
   }

   public @Nullable LivingEntity getControllingPassenger() {
      Entity var1 = this.getFirstPassenger();
      if (this.isSaddled() && var1 instanceof Player var2) {
         return var2;
      } else {
         return super.getControllingPassenger();
      }
   }

   protected Vec3 getRiddenInput(Player var1, Vec3 var2) {
      float var3 = var1.xxa;
      float var4 = 0.0F;
      float var5 = 0.0F;
      if (var1.zza != 0.0F) {
         float var6 = Mth.cos((double)(var1.getXRot() * 0.017453292F));
         float var7 = -Mth.sin((double)(var1.getXRot() * 0.017453292F));
         if (var1.zza < 0.0F) {
            var6 *= -0.5F;
            var7 *= -0.5F;
         }

         var5 = var7;
         var4 = var6;
      }

      return new Vec3((double)var3, (double)var5, (double)var4);
   }

   protected Vec2 getRiddenRotation(LivingEntity var1) {
      return new Vec2(var1.getXRot() * 0.5F, var1.getYRot());
   }

   protected void tickRidden(Player var1, Vec3 var2) {
      super.tickRidden(var1, var2);
      Vec2 var3 = this.getRiddenRotation(var1);
      float var4 = this.getYRot();
      float var5 = Mth.wrapDegrees(var3.y - var4);
      float var6 = 0.5F;
      var4 += var5 * 0.5F;
      this.setRot(var4, var3.x);
      this.yRotO = this.yBodyRot = this.yHeadRot = var4;
      if (this.isLocalInstanceAuthoritative()) {
         if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
            this.executeRidersJump(this.playerJumpPendingScale, var1);
         }

         this.playerJumpPendingScale = 0.0F;
      }

   }

   protected void travelInWater(Vec3 var1, double var2, boolean var4, double var5) {
      float var7 = this.getSpeed();
      this.moveRelative(var7, var1);
      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
   }

   protected float getRiddenSpeed(Player var1) {
      return this.isInWater() ? 0.0325F * (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) : 0.02F * (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   protected void doPlayerRide(Player var1) {
      if (!this.level().isClientSide()) {
         var1.startRiding(this);
         if (!this.isVehicle()) {
            this.clearHome();
         }
      }

   }

   private int getNautilusRestrictionRadius() {
      return !this.isBaby() && this.getItemBySlot(EquipmentSlot.SADDLE).isEmpty() ? 32 : 16;
   }

   protected void checkRestriction() {
      if (!this.isLeashed() && !this.isVehicle() && this.isTame()) {
         int var1 = this.getNautilusRestrictionRadius();
         if (!this.hasHome() || !this.getHomePosition().closerThan(this.blockPosition(), (double)(var1 + 8)) || var1 != this.getHomeRadius()) {
            this.setHomeTo(this.blockPosition(), var1);
         }
      }
   }

   protected void customServerAiStep(ServerLevel var1) {
      this.checkRestriction();
      super.customServerAiStep(var1);
   }

   private void applyEffects(Level var1) {
      Entity var2 = this.getFirstPassenger();
      if (var2 instanceof Player var3) {
         boolean var4 = var3.hasEffect(MobEffects.BREATH_OF_THE_NAUTILUS);
         boolean var5 = var1.getGameTime() % 40L == 0L;
         if (!var4 || var5) {
            var3.addEffect(new MobEffectInstance(MobEffects.BREATH_OF_THE_NAUTILUS, 60, 0, true, true, true));
         }
      }

   }

   private void spawnBubbles() {
      double var1 = this.getDeltaMovement().length();
      double var3 = Mth.clamp(var1 * 2.0, 0.15000000596046448, 1.0);
      if ((double)this.random.nextFloat() < var3) {
         float var5 = this.getYRot();
         float var6 = Mth.clamp(this.getXRot(), -10.0F, 10.0F);
         Vec3 var7 = this.calculateViewVector(var6, var5);
         double var8 = this.random.nextDouble() * 0.8 * (1.0 + var1);
         double var10 = ((double)this.random.nextFloat() - 0.5) * var8;
         double var12 = ((double)this.random.nextFloat() - 0.5) * var8;
         double var14 = ((double)this.random.nextFloat() - 0.5) * var8;
         this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - var7.x * 1.1, this.getY() - var7.y + 0.25, this.getZ() - var7.z * 1.1, var10, var12, var14);
      }

   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         this.applyEffects(this.level());
      }

      if (this.isDashing() && this.dashCooldown < 35) {
         this.setDashing(false);
      }

      if (this.dashCooldown > 0) {
         --this.dashCooldown;
         if (this.dashCooldown == 0) {
            this.makeSound(this.getDashReadySound());
         }
      }

      if (this.isInWater()) {
         this.spawnBubbles();
      }

   }

   public boolean canJump() {
      return this.isSaddled();
   }

   public void onPlayerJump(int var1) {
      if (this.isSaddled() && this.dashCooldown <= 0) {
         this.playerJumpPendingScale = this.getPlayerJumpPendingScale(var1);
      }
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DASH, false);
   }

   public boolean isDashing() {
      return (Boolean)this.entityData.get(DASH);
   }

   public void setDashing(boolean var1) {
      this.entityData.set(DASH, var1);
   }

   protected void executeRidersJump(float var1, Player var2) {
      this.addDeltaMovement(var2.getLookAngle().scale((double)((this.isInWater() ? 1.2F : 0.5F) * var1) * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)this.getBlockSpeedFactor()));
      this.dashCooldown = 40;
      this.setDashing(true);
      this.needsSync = true;
   }

   public void handleStartJump(int var1) {
      this.makeSound(this.getDashSound());
      this.gameEvent(GameEvent.ENTITY_ACTION);
      this.setDashing(true);
   }

   public int getJumpCooldown() {
      return this.dashCooldown;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (!this.firstTick && DASH.equals(var1)) {
         this.dashCooldown = this.dashCooldown == 0 ? 40 : this.dashCooldown;
      }

      super.onSyncedDataUpdated(var1);
   }

   public void handleStopJump() {
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
   }

   protected @Nullable SoundEvent getDashSound() {
      return null;
   }

   protected @Nullable SoundEvent getDashReadySound() {
      return null;
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      this.setPersistenceRequired();
      return super.interact(var1, var2);
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (this.isBaby()) {
         return super.mobInteract(var1, var2);
      } else if (this.isTame() && var1.isSecondaryUseActive()) {
         this.openCustomInventoryScreen(var1);
         return InteractionResult.SUCCESS;
      } else {
         if (!var3.isEmpty()) {
            if (!this.level().isClientSide() && !this.isTame() && this.isFood(var3)) {
               this.usePlayerItem(var1, var2, var3);
               this.tryToTame(var1);
               return InteractionResult.SUCCESS_SERVER;
            }

            if (this.isFood(var3) && this.getHealth() < this.getMaxHealth()) {
               FoodProperties var6 = (FoodProperties)var3.get(DataComponents.FOOD);
               this.heal(var6 != null ? (float)(2 * var6.nutrition()) : 1.0F);
               this.usePlayerItem(var1, var2, var3);
               this.playEatingSound();
               return InteractionResult.SUCCESS;
            }

            InteractionResult var4 = var3.interactLivingEntity(var1, this, var2);
            if (var4.consumesAction()) {
               return var4;
            }
         }

         if (this.isSaddled() && !var1.isSecondaryUseActive() && !this.isFood(var3)) {
            this.doPlayerRide(var1);
            return InteractionResult.SUCCESS;
         } else {
            InteractionResult var5 = super.mobInteract(var1, var2);
            if (var5 == InteractionResult.PASS) {
               this.openCustomInventoryScreen(var1);
               return InteractionResult.SUCCESS;
            } else {
               return var5;
            }
         }
      }
   }

   private void tryToTame(Player var1) {
      if (this.random.nextInt(3) == 0) {
         this.tame(var1);
         this.navigation.stop();
         this.level().broadcastEntityEvent(this, (byte)7);
      } else {
         this.level().broadcastEntityEvent(this, (byte)6);
      }

      this.playEatingSound();
   }

   public boolean removeWhenFarAway(double var1) {
      return true;
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      boolean var4 = super.hurtServer(var1, var2, var3);
      if (var4) {
         Entity var6 = var2.getEntity();
         if (var6 instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)var6;
            NautilusAi.setAngerTarget(var1, this, var5);
         }
      }

      return var4;
   }

   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.getEffect() == MobEffects.POISON ? false : super.canBeAffected(var1);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      NautilusAi.initMemories(this, var5);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   protected Holder<SoundEvent> getEquipSound(EquipmentSlot var1, ItemStack var2, Equippable var3) {
      if (var1 == EquipmentSlot.SADDLE && this.isUnderWater()) {
         return SoundEvents.NAUTILUS_SADDLE_UNDERWATER_EQUIP;
      } else {
         return (Holder<SoundEvent>)(var1 == EquipmentSlot.SADDLE ? SoundEvents.NAUTILUS_SADDLE_EQUIP : super.getEquipSound(var1, var2, var3));
      }
   }

   public final int getInventorySize() {
      return AbstractMountInventoryMenu.getInventorySize(this.getInventoryColumns());
   }

   protected void createInventory() {
      SimpleContainer var1 = this.inventory;
      this.inventory = new SimpleContainer(this.getInventorySize());
      if (var1 != null) {
         int var2 = Math.min(var1.getContainerSize(), this.inventory.getContainerSize());

         for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack var4 = var1.getItem(var3);
            if (!var4.isEmpty()) {
               this.inventory.setItem(var3, var4.copy());
            }
         }
      }

   }

   public void openCustomInventoryScreen(Player var1) {
      if (!this.level().isClientSide() && (!this.isVehicle() || this.hasPassenger(var1)) && this.isTame()) {
         var1.openNautilusInventory(this, this.inventory);
      }

   }

   public @Nullable SlotAccess getSlot(int var1) {
      int var2 = var1 - 500;
      return var2 >= 0 && var2 < this.inventory.getContainerSize() ? this.inventory.getSlot(var2) : super.getSlot(var1);
   }

   public boolean hasInventoryChanged(Container var1) {
      return this.inventory != var1;
   }

   public int getInventoryColumns() {
      return 0;
   }

   static {
      DASH = SynchedEntityData.<Boolean>defineId(AbstractNautilus.class, EntityDataSerializers.BOOLEAN);
   }
}
