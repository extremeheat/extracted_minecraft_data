package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Vindicator extends AbstractIllager {
   private static final String TAG_JOHNNY = "Johnny";
   static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = var0 -> var0 == Difficulty.NORMAL || var0 == Difficulty.HARD;
   boolean isJohnny;

   public Vindicator(EntityType<? extends Vindicator> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Vindicator.VindicatorBreakDoorGoal(this));
      this.goalSelector.addGoal(2, new AbstractIllager.RaiderOpenDoorGoal(this));
      this.goalSelector.addGoal(3, new Raider.HoldGroundAttackGoal(this, 10.0F));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
      this.targetSelector.addGoal(4, new Vindicator.VindicatorJohnnyAttackGoal(this));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
   }

   @Override
   protected void customServerAiStep() {
      if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
         boolean var1 = ((ServerLevel)this.level()).isRaided(this.blockPosition());
         ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(var1);
      }

      super.customServerAiStep();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.3499999940395355)
         .add(Attributes.FOLLOW_RANGE, 12.0)
         .add(Attributes.MAX_HEALTH, 24.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.isJohnny) {
         var1.putBoolean("Johnny", true);
      }
   }

   @Override
   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isAggressive()) {
         return AbstractIllager.IllagerArmPose.ATTACKING;
      } else {
         return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Johnny", 99)) {
         this.isJohnny = var1.getBoolean("Johnny");
      }
   }

   @Override
   public SoundEvent getCelebrateSound() {
      return SoundEvents.VINDICATOR_CELEBRATE;
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      SpawnGroupData var5 = super.finalizeSpawn(var1, var2, var3, var4);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      RandomSource var6 = var1.getRandom();
      this.populateDefaultEquipmentSlots(var6, var2);
      this.populateDefaultEquipmentEnchantments(var6, var2);
      return var5;
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      if (this.getCurrentRaid() == null) {
         this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
      }
   }

   @Override
   public void setCustomName(@Nullable Component var1) {
      super.setCustomName(var1);
      if (!this.isJohnny && var1 != null && var1.getString().equals("Johnny")) {
         this.isJohnny = true;
      }
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.VINDICATOR_AMBIENT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.VINDICATOR_DEATH;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.VINDICATOR_HURT;
   }

   @Override
   public void applyRaidBuffs(int var1, boolean var2) {
      ItemStack var3 = new ItemStack(Items.IRON_AXE);
      Raid var4 = this.getCurrentRaid();
      boolean var5 = this.random.nextFloat() <= var4.getEnchantOdds();
      if (var5) {
         ResourceKey var6 = var1 > var4.getNumGroups(Difficulty.NORMAL)
            ? VanillaEnchantmentProviders.RAID_VINDICATOR_POST_WAVE_5
            : VanillaEnchantmentProviders.RAID_VINDICATOR;
         EnchantmentHelper.enchantItemFromProvider(var3, var6, this.level(), this.blockPosition(), this.random);
      }

      this.setItemSlot(EquipmentSlot.MAINHAND, var3);
   }

   static class VindicatorBreakDoorGoal extends BreakDoorGoal {
      public VindicatorBreakDoorGoal(Mob var1) {
         super(var1, 6, Vindicator.DOOR_BREAKING_PREDICATE);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public boolean canContinueToUse() {
         Vindicator var1 = (Vindicator)this.mob;
         return var1.hasActiveRaid() && super.canContinueToUse();
      }

      @Override
      public boolean canUse() {
         Vindicator var1 = (Vindicator)this.mob;
         return var1.hasActiveRaid() && var1.random.nextInt(reducedTickDelay(10)) == 0 && super.canUse();
      }

      @Override
      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }

   static class VindicatorJohnnyAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public VindicatorJohnnyAttackGoal(Vindicator var1) {
         super(var1, LivingEntity.class, 0, true, true, LivingEntity::attackable);
      }

      @Override
      public boolean canUse() {
         return ((Vindicator)this.mob).isJohnny && super.canUse();
      }

      @Override
      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }
}
