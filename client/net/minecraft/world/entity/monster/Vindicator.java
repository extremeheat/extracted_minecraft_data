package net.minecraft.world.entity.monster;

import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Vindicator extends AbstractIllager {
   private static final String TAG_JOHNNY = "Johnny";
   static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (var0) -> {
      return var0 == Difficulty.NORMAL || var0 == Difficulty.HARD;
   };
   boolean isJohnny;

   public Vindicator(EntityType<? extends Vindicator> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new VindicatorBreakDoorGoal(this));
      this.goalSelector.addGoal(2, new AbstractIllager.RaiderOpenDoorGoal(this));
      this.goalSelector.addGoal(3, new Raider.HoldGroundAttackGoal(this, 10.0F));
      this.goalSelector.addGoal(4, new VindicatorMeleeAttackGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(4, new VindicatorJohnnyAttackGoal(this));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
   }

   protected void customServerAiStep() {
      if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
         boolean var1 = ((ServerLevel)this.level).isRaided(this.blockPosition());
         ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(var1);
      }

      super.customServerAiStep();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3499999940395355).add(Attributes.FOLLOW_RANGE, 12.0).add(Attributes.MAX_HEALTH, 24.0).add(Attributes.ATTACK_DAMAGE, 5.0);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.isJohnny) {
         var1.putBoolean("Johnny", true);
      }

   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isAggressive()) {
         return AbstractIllager.IllagerArmPose.ATTACKING;
      } else {
         return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Johnny", 99)) {
         this.isJohnny = var1.getBoolean("Johnny");
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.VINDICATOR_CELEBRATE;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      SpawnGroupData var6 = super.finalizeSpawn(var1, var2, var3, var4, var5);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      RandomSource var7 = var1.getRandom();
      this.populateDefaultEquipmentSlots(var7, var2);
      this.populateDefaultEquipmentEnchantments(var7, var2);
      return var6;
   }

   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      if (this.getCurrentRaid() == null) {
         this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
      }

   }

   public boolean isAlliedTo(Entity var1) {
      if (super.isAlliedTo(var1)) {
         return true;
      } else if (var1 instanceof LivingEntity && ((LivingEntity)var1).getMobType() == MobType.ILLAGER) {
         return this.getTeam() == null && var1.getTeam() == null;
      } else {
         return false;
      }
   }

   public void setCustomName(@Nullable Component var1) {
      super.setCustomName(var1);
      if (!this.isJohnny && var1 != null && var1.getString().equals("Johnny")) {
         this.isJohnny = true;
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VINDICATOR_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VINDICATOR_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.VINDICATOR_HURT;
   }

   public void applyRaidBuffs(int var1, boolean var2) {
      ItemStack var3 = new ItemStack(Items.IRON_AXE);
      Raid var4 = this.getCurrentRaid();
      byte var5 = 1;
      if (var1 > var4.getNumGroups(Difficulty.NORMAL)) {
         var5 = 2;
      }

      boolean var6 = this.random.nextFloat() <= var4.getEnchantOdds();
      if (var6) {
         HashMap var7 = Maps.newHashMap();
         var7.put(Enchantments.SHARPNESS, Integer.valueOf(var5));
         EnchantmentHelper.setEnchantments(var7, var3);
      }

      this.setItemSlot(EquipmentSlot.MAINHAND, var3);
   }

   static class VindicatorBreakDoorGoal extends BreakDoorGoal {
      public VindicatorBreakDoorGoal(Mob var1) {
         super(var1, 6, Vindicator.DOOR_BREAKING_PREDICATE);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canContinueToUse() {
         Vindicator var1 = (Vindicator)this.mob;
         return var1.hasActiveRaid() && super.canContinueToUse();
      }

      public boolean canUse() {
         Vindicator var1 = (Vindicator)this.mob;
         return var1.hasActiveRaid() && var1.random.nextInt(reducedTickDelay(10)) == 0 && super.canUse();
      }

      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }

   private class VindicatorMeleeAttackGoal extends MeleeAttackGoal {
      public VindicatorMeleeAttackGoal(Vindicator var2) {
         super(var2, 1.0, false);
      }

      protected double getAttackReachSqr(LivingEntity var1) {
         if (this.mob.getVehicle() instanceof Ravager) {
            float var2 = this.mob.getVehicle().getBbWidth() - 0.1F;
            return (double)(var2 * 2.0F * var2 * 2.0F + var1.getBbWidth());
         } else {
            return super.getAttackReachSqr(var1);
         }
      }
   }

   private static class VindicatorJohnnyAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public VindicatorJohnnyAttackGoal(Vindicator var1) {
         super(var1, LivingEntity.class, 0, true, true, LivingEntity::attackable);
      }

      public boolean canUse() {
         return ((Vindicator)this.mob).isJohnny && super.canUse();
      }

      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }
}
