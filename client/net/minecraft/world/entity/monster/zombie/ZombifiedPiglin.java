package net.minecraft.world.entity.monster.zombie;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SpearUseGoal;
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
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class ZombifiedPiglin extends Zombie implements NeutralMob {
   private static final EntityDimensions BABY_DIMENSIONS = EntityDimensions.scalable(0.49F, 0.99F).withEyeHeight(0.78F);
   private static final Identifier SPEED_MODIFIER_ATTACKING_ID = Identifier.withDefaultNamespace("attacking");
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING;
   private static final UniformInt FIRST_ANGER_SOUND_DELAY;
   private int playFirstAngerSoundIn;
   private static final UniformInt PERSISTENT_ANGER_TIME;
   private long persistentAngerEndTime;
   private @Nullable EntityReference<LivingEntity> persistentAngerTarget;
   private static final int ALERT_RANGE_Y = 10;
   private static final UniformInt ALERT_INTERVAL;
   private int ticksUntilNextAlert;

   public ZombifiedPiglin(final EntityType<? extends ZombifiedPiglin> type, final Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(1, new SpearUseGoal(this, 1.0, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal(this, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Zombie.createAttributes().add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513).add(Attributes.ATTACK_DAMAGE, 5.0);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   protected boolean convertsInWater() {
      return false;
   }

   protected void customServerAiStep(final ServerLevel level) {
      AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (this.isAngry()) {
         if (!this.isBaby() && !speed.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
            speed.addTransientModifier(SPEED_MODIFIER_ATTACKING);
         }

         this.maybePlayFirstAngerSound();
      } else if (speed.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
         speed.removeModifier(SPEED_MODIFIER_ATTACKING_ID);
      }

      this.updatePersistentAnger(level, true);
      if (this.getTarget() != null) {
         this.maybeAlertOthers();
      }

      super.customServerAiStep(level);
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
      double within = this.getAttributeValue(Attributes.FOLLOW_RANGE);
      AABB searchAabb = AABB.unitCubeFromLowerCorner(this.position()).inflate(within, 10.0, within);
      this.level().getEntitiesOfClass(ZombifiedPiglin.class, searchAabb, EntitySelector.NO_SPECTATORS).stream().filter((other) -> other != this).filter((other) -> other.getTarget() == null).filter((other) -> !other.isAlliedTo(this.getTarget())).forEach((other) -> other.setTarget(this.getTarget()));
   }

   private void playAngerSound() {
      this.playSound(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getVoicePitch() * 1.8F);
   }

   public void setTarget(final @Nullable LivingEntity target) {
      if (this.getTarget() == null && target != null) {
         this.playFirstAngerSoundIn = FIRST_ANGER_SOUND_DELAY.sample(this.random);
         this.ticksUntilNextAlert = ALERT_INTERVAL.sample(this.random);
      }

      super.setTarget(target);
   }

   public void startPersistentAngerTimer() {
      this.setTimeToRemainAngry((long)PERSISTENT_ANGER_TIME.sample(this.random));
   }

   public static boolean checkZombifiedPiglinSpawnRules(final EntityType<ZombifiedPiglin> type, final LevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      return level.getDifficulty() != Difficulty.PEACEFUL && !level.getBlockState(pos.below()).is(Blocks.NETHER_WART_BLOCK);
   }

   public boolean checkSpawnObstruction(final LevelReader level) {
      return level.isUnobstructed(this) && !level.containsAnyLiquid(this.getBoundingBox());
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      this.addPersistentAngerSaveData(output);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.readPersistentAngerSaveData(this.level(), input);
   }

   public void setPersistentAngerEndTime(final long endTime) {
      this.persistentAngerEndTime = endTime;
   }

   public long getPersistentAngerEndTime() {
      return this.persistentAngerEndTime;
   }

   public void setPersistentAngerTarget(final @Nullable EntityReference<LivingEntity> persistentAngerTarget) {
      this.persistentAngerTarget = persistentAngerTarget;
   }

   protected SoundEvent getAmbientSound() {
      return this.isAngry() ? SoundEvents.ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
   }

   public void populateDefaultEquipmentSlots(final RandomSource random, final DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(random.nextInt(20) == 0 ? Items.GOLDEN_SPEAR : Items.GOLDEN_SWORD));
   }

   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0);
   }

   public @Nullable EntityReference<LivingEntity> getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public boolean isPreventingPlayerRest(final ServerLevel level, final Player player) {
      return this.isAngryAt(player, level);
   }

   public boolean wantsToPickUp(final ServerLevel level, final ItemStack itemStack) {
      return this.canHoldItem(itemStack);
   }

   static {
      SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.05, AttributeModifier.Operation.ADD_VALUE);
      FIRST_ANGER_SOUND_DELAY = TimeUtil.rangeOfSeconds(0, 1);
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
      ALERT_INTERVAL = TimeUtil.rangeOfSeconds(4, 6);
   }
}
