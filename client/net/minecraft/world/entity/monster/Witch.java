package net.minecraft.world.entity.monster;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class Witch extends Raider implements RangedAttackMob {
   private static final ResourceLocation SPEED_MODIFIER_DRINKING_ID = ResourceLocation.withDefaultNamespace("drinking");
   private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(
      SPEED_MODIFIER_DRINKING_ID, -0.25, AttributeModifier.Operation.ADD_VALUE
   );
   private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(Witch.class, EntityDataSerializers.BOOLEAN);
   private int usingTime;
   private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
   private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

   public Witch(EntityType<? extends Witch> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.healRaidersGoal = new NearestHealableRaiderTargetGoal<>(
         this, Raider.class, true, (var1, var2) -> this.hasActiveRaid() && var1.getType() != EntityType.WITCH
      );
      this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false, null);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 60, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class));
      this.targetSelector.addGoal(2, this.healRaidersGoal);
      this.targetSelector.addGoal(3, this.attackPlayersGoal);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_USING_ITEM, false);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITCH_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.WITCH_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.WITCH_DEATH;
   }

   public void setUsingItem(boolean var1) {
      this.getEntityData().set(DATA_USING_ITEM, var1);
   }

   public boolean isDrinkingPotion() {
      return this.getEntityData().get(DATA_USING_ITEM);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 26.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Override
   public void aiStep() {
      if (!this.level().isClientSide && this.isAlive()) {
         this.healRaidersGoal.decrementCooldown();
         if (this.healRaidersGoal.getCooldown() <= 0) {
            this.attackPlayersGoal.setCanAttack(true);
         } else {
            this.attackPlayersGoal.setCanAttack(false);
         }

         if (this.isDrinkingPotion()) {
            if (this.usingTime-- <= 0) {
               this.setUsingItem(false);
               ItemStack var3 = this.getMainHandItem();
               this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
               PotionContents var4 = var3.get(DataComponents.POTION_CONTENTS);
               if (var3.is(Items.POTION) && var4 != null) {
                  var4.forEachEffect(this::addEffect);
               }

               this.gameEvent(GameEvent.DRINK);
               this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING.id());
            }
         } else {
            Holder var1 = null;
            if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
               var1 = Potions.WATER_BREATHING;
            } else if (this.random.nextFloat() < 0.15F
               && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE))
               && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
               var1 = Potions.FIRE_RESISTANCE;
            } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
               var1 = Potions.HEALING;
            } else if (this.random.nextFloat() < 0.5F
               && this.getTarget() != null
               && !this.hasEffect(MobEffects.MOVEMENT_SPEED)
               && this.getTarget().distanceToSqr(this) > 121.0) {
               var1 = Potions.SWIFTNESS;
            }

            if (var1 != null) {
               this.setItemSlot(EquipmentSlot.MAINHAND, PotionContents.createItemStack(Items.POTION, var1));
               this.usingTime = this.getMainHandItem().getUseDuration(this);
               this.setUsingItem(true);
               if (!this.isSilent()) {
                  this.level()
                     .playSound(
                        null,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        SoundEvents.WITCH_DRINK,
                        this.getSoundSource(),
                        1.0F,
                        0.8F + this.random.nextFloat() * 0.4F
                     );
               }

               AttributeInstance var2 = this.getAttribute(Attributes.MOVEMENT_SPEED);
               var2.removeModifier(SPEED_MODIFIER_DRINKING_ID);
               var2.addTransientModifier(SPEED_MODIFIER_DRINKING);
            }
         }

         if (this.random.nextFloat() < 7.5E-4F) {
            this.level().broadcastEntityEvent(this, (byte)15);
         }
      }

      super.aiStep();
   }

   @Override
   public SoundEvent getCelebrateSound() {
      return SoundEvents.WITCH_CELEBRATE;
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 15) {
         for (int var2 = 0; var2 < this.random.nextInt(35) + 10; var2++) {
            this.level()
               .addParticle(
                  ParticleTypes.WITCH,
                  this.getX() + this.random.nextGaussian() * 0.12999999523162842,
                  this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.12999999523162842,
                  this.getZ() + this.random.nextGaussian() * 0.12999999523162842,
                  0.0,
                  0.0,
                  0.0
               );
         }
      } else {
         super.handleEntityEvent(var1);
      }
   }

   @Override
   protected float getDamageAfterMagicAbsorb(DamageSource var1, float var2) {
      var2 = super.getDamageAfterMagicAbsorb(var1, var2);
      if (var1.getEntity() == this) {
         var2 = 0.0F;
      }

      if (var1.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
         var2 *= 0.15F;
      }

      return var2;
   }

   @Override
   public void performRangedAttack(LivingEntity var1, float var2) {
      if (!this.isDrinkingPotion()) {
         Vec3 var3 = var1.getDeltaMovement();
         double var4 = var1.getX() + var3.x - this.getX();
         double var6 = var1.getEyeY() - 1.100000023841858 - this.getY();
         double var8 = var1.getZ() + var3.z - this.getZ();
         double var10 = Math.sqrt(var4 * var4 + var8 * var8);
         Holder var12 = Potions.HARMING;
         if (var1 instanceof Raider) {
            if (var1.getHealth() <= 4.0F) {
               var12 = Potions.HEALING;
            } else {
               var12 = Potions.REGENERATION;
            }

            this.setTarget(null);
         } else if (var10 >= 8.0 && !var1.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            var12 = Potions.SLOWNESS;
         } else if (var1.getHealth() >= 8.0F && !var1.hasEffect(MobEffects.POISON)) {
            var12 = Potions.POISON;
         } else if (var10 <= 3.0 && !var1.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
            var12 = Potions.WEAKNESS;
         }

         if (this.level() instanceof ServerLevel var13) {
            ItemStack var15 = PotionContents.createItemStack(Items.SPLASH_POTION, var12);
            Projectile.spawnProjectileUsingShoot(ThrownPotion::new, var13, var15, this, var4, var6 + var10 * 0.2, var8, 0.75F, 8.0F);
         }

         if (!this.isSilent()) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F
               );
         }
      }
   }

   @Override
   public void applyRaidBuffs(ServerLevel var1, int var2, boolean var3) {
   }

   @Override
   public boolean canBeLeader() {
      return false;
   }
}
