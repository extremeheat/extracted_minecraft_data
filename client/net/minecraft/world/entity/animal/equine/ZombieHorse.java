package net.minecraft.world.entity.animal.equine;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ZombieHorse extends AbstractHorse {
   private static final float SPEED_FACTOR = 42.16F;
   private static final double BASE_JUMP_STRENGTH = 0.5;
   private static final double PER_RANDOM_JUMP_STRENGTH = 0.06666666666666667;
   private static final double BASE_SPEED = 9.0;
   private static final double PER_RANDOM_SPEED = 1.0;
   private static final EntityDimensions BABY_DIMENSIONS;

   public ZombieHorse(EntityType<? extends ZombieHorse> var1, Level var2) {
      super(var1, var2);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 25.0);
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      this.setPersistenceRequired();
      return super.interact(var1, var2);
   }

   public boolean removeWhenFarAway(double var1) {
      return true;
   }

   public boolean isMobControlled() {
      return this.getFirstPassenger() instanceof Mob;
   }

   protected void randomizeAttributes(RandomSource var1) {
      AttributeInstance var10000 = this.getAttribute(Attributes.JUMP_STRENGTH);
      Objects.requireNonNull(var1);
      var10000.setBaseValue(generateZombieHorseJumpStrength(var1::nextDouble));
      var10000 = this.getAttribute(Attributes.MOVEMENT_SPEED);
      Objects.requireNonNull(var1);
      var10000.setBaseValue(generateZombieHorseSpeed(var1::nextDouble));
   }

   private static double generateZombieHorseJumpStrength(DoubleSupplier var0) {
      return 0.5 + var0.getAsDouble() * 0.06666666666666667 + var0.getAsDouble() * 0.06666666666666667 + var0.getAsDouble() * 0.06666666666666667;
   }

   private static double generateZombieHorseSpeed(DoubleSupplier var0) {
      return (9.0 + var0.getAsDouble() * 1.0 + var0.getAsDouble() * 1.0 + var0.getAsDouble() * 1.0) / 42.15999984741211;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ZOMBIE_HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.ZOMBIE_HORSE_ANGRY;
   }

   protected SoundEvent getEatingSound() {
      return SoundEvents.ZOMBIE_HORSE_EAT;
   }

   public @Nullable AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return null;
   }

   public boolean canFallInLove() {
      return false;
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, (var0) -> var0.is(ItemTags.ZOMBIE_HORSE_FOOD), false));
   }

   public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      if (var3 == EntitySpawnReason.NATURAL) {
         Zombie var5 = EntityType.ZOMBIE.create(this.level(), EntitySpawnReason.JOCKEY);
         if (var5 != null) {
            var5.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            var5.finalizeSpawn(var1, var2, var3, (SpawnGroupData)null);
            var5.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SPEAR));
            var5.startRiding(this, false, false);
         }
      }

      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      boolean var3 = !this.isBaby() && this.isTamed() && var1.isSecondaryUseActive();
      if (!this.isVehicle() && !var3) {
         ItemStack var4 = var1.getItemInHand(var2);
         if (!var4.isEmpty()) {
            if (this.isFood(var4)) {
               return this.fedFood(var1, var4);
            }

            if (!this.isTamed()) {
               this.makeMad();
               return InteractionResult.SUCCESS;
            }
         }

         return super.mobInteract(var1, var2);
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   public boolean canUseSlot(EquipmentSlot var1) {
      return true;
   }

   public boolean canBeLeashed() {
      return this.isTamed() || !this.isMobControlled();
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.ZOMBIE_HORSE_FOOD);
   }

   protected EquipmentSlot sunProtectionSlot() {
      return EquipmentSlot.BODY;
   }

   public Vec3[] getQuadLeashOffsets() {
      return Leashable.createQuadLeashOffsets(this, 0.04, 0.41, 0.18, 0.73);
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(var1);
   }

   public float chargeSpeedModifier() {
      return 1.4F;
   }

   static {
      BABY_DIMENSIONS = EntityType.ZOMBIE_HORSE.getDimensions().withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, EntityType.ZOMBIE_HORSE.getHeight() - 0.03125F, 0.0F)).scale(0.5F);
   }
}
