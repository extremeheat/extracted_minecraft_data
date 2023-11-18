package net.minecraft.world.entity.monster;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;

public class ZombifiedPiglin extends Zombie implements NeutralMob {
   private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(
      SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.05, AttributeModifier.Operation.ADDITION
   );
   private static final UniformInt FIRST_ANGER_SOUND_DELAY = TimeUtil.rangeOfSeconds(0, 1);
   private int playFirstAngerSoundIn;
   private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   private int remainingPersistentAngerTime;
   @Nullable
   private UUID persistentAngerTarget;
   private static final int ALERT_RANGE_Y = 10;
   private static final UniformInt ALERT_INTERVAL = TimeUtil.rangeOfSeconds(4, 6);
   private int ticksUntilNextAlert;
   private static final float ZOMBIFIED_PIGLIN_EYE_HEIGHT = 1.79F;
   private static final float ZOMBIFIED_PIGLIN_BABY_EYE_HEIGHT_ADJUSTMENT = 0.82F;

   public ZombifiedPiglin(EntityType<? extends ZombifiedPiglin> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
   }

   @Override
   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   @Override
   public double getMyRidingOffset() {
      return this.isBaby() ? -0.05 : -0.45;
   }

   @Override
   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Zombie.createAttributes()
         .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0)
         .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513)
         .add(Attributes.ATTACK_DAMAGE, 5.0);
   }

   @Override
   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return this.isBaby() ? 0.96999997F : 1.79F;
   }

   @Override
   protected boolean convertsInWater() {
      return false;
   }

   @Override
   protected void customServerAiStep() {
      AttributeInstance var1 = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (this.isAngry()) {
         if (!this.isBaby() && !var1.hasModifier(SPEED_MODIFIER_ATTACKING)) {
            var1.addTransientModifier(SPEED_MODIFIER_ATTACKING);
         }

         this.maybePlayFirstAngerSound();
      } else if (var1.hasModifier(SPEED_MODIFIER_ATTACKING)) {
         var1.removeModifier(SPEED_MODIFIER_ATTACKING);
      }

      this.updatePersistentAnger((ServerLevel)this.level(), true);
      if (this.getTarget() != null) {
         this.maybeAlertOthers();
      }

      if (this.isAngry()) {
         this.lastHurtByPlayerTime = this.tickCount;
      }

      super.customServerAiStep();
   }

   private void maybePlayFirstAngerSound() {
      if (this.playFirstAngerSoundIn > 0) {
         --this.playFirstAngerSoundIn;
         if (this.playFirstAngerSoundIn == 0) {
            this.playAngerSound();
         }
      }
   }

   private void maybeAlertOthers() {
      if (this.ticksUntilNextAlert > 0) {
         --this.ticksUntilNextAlert;
      } else {
         if (this.getSensing().hasLineOfSight(this.getTarget())) {
            this.alertOthers();
         }

         this.ticksUntilNextAlert = ALERT_INTERVAL.sample(this.random);
      }
   }

   private void alertOthers() {
      double var1 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
      AABB var3 = AABB.unitCubeFromLowerCorner(this.position()).inflate(var1, 10.0, var1);
      this.level()
         .getEntitiesOfClass(ZombifiedPiglin.class, var3, EntitySelector.NO_SPECTATORS)
         .stream()
         .filter(var1x -> var1x != this)
         .filter(var0 -> var0.getTarget() == null)
         .filter(var1x -> !var1x.isAlliedTo(this.getTarget()))
         .forEach(var1x -> var1x.setTarget(this.getTarget()));
   }

   private void playAngerSound() {
      this.playSound(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getVoicePitch() * 1.8F);
   }

   @Override
   public void setTarget(@Nullable LivingEntity var1) {
      if (this.getTarget() == null && var1 != null) {
         this.playFirstAngerSoundIn = FIRST_ANGER_SOUND_DELAY.sample(this.random);
         this.ticksUntilNextAlert = ALERT_INTERVAL.sample(this.random);
      }

      if (var1 instanceof Player) {
         this.setLastHurtByPlayer((Player)var1);
      }

      super.setTarget(var1);
   }

   @Override
   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
   }

   public static boolean checkZombifiedPiglinSpawnRules(
      EntityType<ZombifiedPiglin> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4
   ) {
      return var1.getDifficulty() != Difficulty.PEACEFUL && !var1.getBlockState(var3.below()).is(Blocks.NETHER_WART_BLOCK);
   }

   @Override
   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this) && !var1.containsAnyLiquid(this.getBoundingBox());
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.addPersistentAngerSaveData(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.readPersistentAngerSaveData(this.level(), var1);
   }

   @Override
   public void setRemainingPersistentAngerTime(int var1) {
      this.remainingPersistentAngerTime = var1;
   }

   @Override
   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.isAngry() ? SoundEvents.ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
   }

   @Override
   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   @Override
   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0);
   }

   @Nullable
   @Override
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   @Override
   public boolean isPreventingPlayerRest(Player var1) {
      return this.isAngryAt(var1);
   }

   @Override
   public boolean wantsToPickUp(ItemStack var1) {
      return this.canHoldItem(var1);
   }
}
