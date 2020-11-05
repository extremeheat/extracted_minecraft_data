package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Witch extends Raider implements RangedAttackMob {
   private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier SPEED_MODIFIER_DRINKING;
   private static final EntityDataAccessor<Boolean> DATA_USING_ITEM;
   private int usingTime;
   private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
   private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

   public Witch(EntityType<? extends Witch> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.healRaidersGoal = new NearestHealableRaiderTargetGoal(this, Raider.class, true, (var1) -> {
         return var1 != null && this.hasActiveRaid() && var1.getType() != EntityType.WITCH;
      });
      this.attackPlayersGoal = new NearestAttackableWitchTargetGoal(this, Player.class, 10, true, false, (Predicate)null);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 60, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{Raider.class}));
      this.targetSelector.addGoal(2, this.healRaidersGoal);
      this.targetSelector.addGoal(3, this.attackPlayersGoal);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_USING_ITEM, false);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITCH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.WITCH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITCH_DEATH;
   }

   public void setUsingItem(boolean var1) {
      this.getEntityData().set(DATA_USING_ITEM, var1);
   }

   public boolean isDrinkingPotion() {
      return (Boolean)this.getEntityData().get(DATA_USING_ITEM);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 26.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
   }

   public void aiStep() {
      if (!this.level.isClientSide && this.isAlive()) {
         this.healRaidersGoal.decrementCooldown();
         if (this.healRaidersGoal.getCooldown() <= 0) {
            this.attackPlayersGoal.setCanAttack(true);
         } else {
            this.attackPlayersGoal.setCanAttack(false);
         }

         if (this.isDrinkingPotion()) {
            if (this.usingTime-- <= 0) {
               this.setUsingItem(false);
               ItemStack var6 = this.getMainHandItem();
               this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
               if (var6.getItem() == Items.POTION) {
                  List var5 = PotionUtils.getMobEffects(var6);
                  if (var5 != null) {
                     Iterator var3 = var5.iterator();

                     while(var3.hasNext()) {
                        MobEffectInstance var4 = (MobEffectInstance)var3.next();
                        this.addEffect(new MobEffectInstance(var4));
                     }
                  }
               }

               this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
            }
         } else {
            Potion var1 = null;
            if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
               var1 = Potions.WATER_BREATHING;
            } else if (this.random.nextFloat() < 0.15F && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().isFire()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
               var1 = Potions.FIRE_RESISTANCE;
            } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
               var1 = Potions.HEALING;
            } else if (this.random.nextFloat() < 0.5F && this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
               var1 = Potions.SWIFTNESS;
            }

            if (var1 != null) {
               this.setItemSlot(EquipmentSlot.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), var1));
               this.usingTime = this.getMainHandItem().getUseDuration();
               this.setUsingItem(true);
               if (!this.isSilent()) {
                  this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
               }

               AttributeInstance var2 = this.getAttribute(Attributes.MOVEMENT_SPEED);
               var2.removeModifier(SPEED_MODIFIER_DRINKING);
               var2.addTransientModifier(SPEED_MODIFIER_DRINKING);
            }
         }

         if (this.random.nextFloat() < 7.5E-4F) {
            this.level.broadcastEntityEvent(this, (byte)15);
         }
      }

      super.aiStep();
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.WITCH_CELEBRATE;
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 15) {
         for(int var2 = 0; var2 < this.random.nextInt(35) + 10; ++var2) {
            this.level.addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * 0.12999999523162842D, this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * 0.12999999523162842D, this.getZ() + this.random.nextGaussian() * 0.12999999523162842D, 0.0D, 0.0D, 0.0D);
         }
      } else {
         super.handleEntityEvent(var1);
      }

   }

   protected float getDamageAfterMagicAbsorb(DamageSource var1, float var2) {
      var2 = super.getDamageAfterMagicAbsorb(var1, var2);
      if (var1.getEntity() == this) {
         var2 = 0.0F;
      }

      if (var1.isMagic()) {
         var2 = (float)((double)var2 * 0.15D);
      }

      return var2;
   }

   public void performRangedAttack(LivingEntity var1, float var2) {
      if (!this.isDrinkingPotion()) {
         Vec3 var3 = var1.getDeltaMovement();
         double var4 = var1.getX() + var3.x - this.getX();
         double var6 = var1.getEyeY() - 1.100000023841858D - this.getY();
         double var8 = var1.getZ() + var3.z - this.getZ();
         float var10 = Mth.sqrt(var4 * var4 + var8 * var8);
         Potion var11 = Potions.HARMING;
         if (var1 instanceof Raider) {
            if (var1.getHealth() <= 4.0F) {
               var11 = Potions.HEALING;
            } else {
               var11 = Potions.REGENERATION;
            }

            this.setTarget((LivingEntity)null);
         } else if (var10 >= 8.0F && !var1.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            var11 = Potions.SLOWNESS;
         } else if (var1.getHealth() >= 8.0F && !var1.hasEffect(MobEffects.POISON)) {
            var11 = Potions.POISON;
         } else if (var10 <= 3.0F && !var1.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
            var11 = Potions.WEAKNESS;
         }

         ThrownPotion var12 = new ThrownPotion(this.level, this);
         var12.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), var11));
         var12.xRot -= -20.0F;
         var12.shoot(var4, var6 + (double)(var10 * 0.2F), var8, 0.75F, 8.0F);
         if (!this.isSilent()) {
            this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
         }

         this.level.addFreshEntity(var12);
      }
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 1.62F;
   }

   public void applyRaidBuffs(int var1, boolean var2) {
   }

   public boolean canBeLeader() {
      return false;
   }

   static {
      SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
      DATA_USING_ITEM = SynchedEntityData.defineId(Witch.class, EntityDataSerializers.BOOLEAN);
   }
}
