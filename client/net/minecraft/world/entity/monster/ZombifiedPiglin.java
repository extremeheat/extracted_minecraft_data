package net.minecraft.world.entity.monster;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.IntRange;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
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
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING;
   private static final IntRange FIRST_ANGER_SOUND_DELAY;
   private int playFirstAngerSoundIn;
   private static final IntRange PERSISTENT_ANGER_TIME;
   private int remainingPersistentAngerTime;
   private UUID persistentAngerTarget;
   private static final IntRange ALERT_INTERVAL;
   private int ticksUntilNextAlert;

   public ZombifiedPiglin(EntityType<? extends ZombifiedPiglin> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
   }

   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? -0.05D : -0.45D;
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal(this, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Zombie.createAttributes().add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D).add(Attributes.ATTACK_DAMAGE, 5.0D);
   }

   protected boolean convertsInWater() {
      return false;
   }

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

      this.updatePersistentAnger((ServerLevel)this.level, true);
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
         if (this.getSensing().canSee(this.getTarget())) {
            this.alertOthers();
         }

         this.ticksUntilNextAlert = ALERT_INTERVAL.randomValue(this.random);
      }
   }

   private void alertOthers() {
      double var1 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
      AABB var3 = AABB.unitCubeFromLowerCorner(this.position()).inflate(var1, 10.0D, var1);
      this.level.getEntitiesOfClass(ZombifiedPiglin.class, var3, EntitySelector.NO_SPECTATORS).stream().filter((var1x) -> {
         return var1x != this;
      }).filter((var0) -> {
         return var0.getTarget() == null;
      }).filter((var1x) -> {
         return !var1x.isAlliedTo(this.getTarget());
      }).forEach((var1x) -> {
         var1x.setTarget(this.getTarget());
      });
   }

   private void playAngerSound() {
      this.playSound(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getVoicePitch() * 1.8F);
   }

   public void setTarget(@Nullable LivingEntity var1) {
      if (this.getTarget() == null && var1 != null) {
         this.playFirstAngerSoundIn = FIRST_ANGER_SOUND_DELAY.randomValue(this.random);
         this.ticksUntilNextAlert = ALERT_INTERVAL.randomValue(this.random);
      }

      if (var1 instanceof Player) {
         this.setLastHurtByPlayer((Player)var1);
      }

      super.setTarget(var1);
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
   }

   public static boolean checkZombifiedPiglinSpawnRules(EntityType<ZombifiedPiglin> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return var1.getDifficulty() != Difficulty.PEACEFUL && !var1.getBlockState(var3.below()).is(Blocks.NETHER_WART_BLOCK);
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this) && !var1.containsAnyLiquid(this.getBoundingBox());
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.addPersistentAngerSaveData(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.readPersistentAngerSaveData(this.level, var1);
   }

   public void setRemainingPersistentAngerTime(int var1) {
      this.remainingPersistentAngerTime = var1;
   }

   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   public boolean hurt(DamageSource var1, float var2) {
      return this.isInvulnerableTo(var1) ? false : super.hurt(var1, var2);
   }

   protected SoundEvent getAmbientSound() {
      return this.isAngry() ? SoundEvents.ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance var1) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
   }

   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public boolean isPreventingPlayerRest(Player var1) {
      return this.isAngryAt(var1);
   }

   static {
      SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION);
      FIRST_ANGER_SOUND_DELAY = TimeUtil.rangeOfSeconds(0, 1);
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
      ALERT_INTERVAL = TimeUtil.rangeOfSeconds(4, 6);
   }
}
