package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSkeleton extends Monster implements RangedAttackMob {
   private static final int HARD_ATTACK_INTERVAL = 20;
   private static final int NORMAL_ATTACK_INTERVAL = 40;
   private final RangedBowAttackGoal<AbstractSkeleton> bowGoal = new RangedBowAttackGoal(this, 1.0, 20, 15.0F);
   private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2, false) {
      public void stop() {
         super.stop();
         AbstractSkeleton.this.setAggressive(false);
      }

      public void start() {
         super.start();
         AbstractSkeleton.this.setAggressive(true);
      }
   };

   protected AbstractSkeleton(EntityType<? extends AbstractSkeleton> var1, Level var2) {
      super(var1, var2);
      this.reassessWeaponGoal();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(2, new RestrictSunGoal(this));
      this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Wolf.class, 6.0F, 1.0, 1.2));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   abstract SoundEvent getStepSound();

   public void aiStep() {
      boolean var1 = this.isSunBurnTick();
      if (var1) {
         ItemStack var2 = this.getItemBySlot(EquipmentSlot.HEAD);
         if (!var2.isEmpty()) {
            if (var2.isDamageableItem()) {
               Item var3 = var2.getItem();
               var2.setDamageValue(var2.getDamageValue() + this.random.nextInt(2));
               if (var2.getDamageValue() >= var2.getMaxDamage()) {
                  this.onEquippedItemBroken(var3, EquipmentSlot.HEAD);
                  this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
               }
            }

            var1 = false;
         }

         if (var1) {
            this.igniteForSeconds(8.0F);
         }
      }

      super.aiStep();
   }

   public void rideTick() {
      super.rideTick();
      Entity var2 = this.getControlledVehicle();
      if (var2 instanceof PathfinderMob var1) {
         this.yBodyRot = var1.yBodyRot;
      }

   }

   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      super.populateDefaultEquipmentSlots(var1, var2);
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      var4 = super.finalizeSpawn(var1, var2, var3, var4);
      RandomSource var5 = var1.getRandom();
      this.populateDefaultEquipmentSlots(var5, var2);
      this.populateDefaultEquipmentEnchantments(var1, var5, var2);
      this.reassessWeaponGoal();
      this.setCanPickUpLoot(var5.nextFloat() < 0.55F * var2.getSpecialMultiplier());
      if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         LocalDate var6 = LocalDate.now();
         int var7 = var6.get(ChronoField.DAY_OF_MONTH);
         int var8 = var6.get(ChronoField.MONTH_OF_YEAR);
         if (var8 == 10 && var7 == 31 && var5.nextFloat() < 0.25F) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(var5.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
         }
      }

      return var4;
   }

   public void reassessWeaponGoal() {
      if (this.level() != null && !this.level().isClientSide) {
         this.goalSelector.removeGoal(this.meleeGoal);
         this.goalSelector.removeGoal(this.bowGoal);
         ItemStack var1 = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
         if (var1.is(Items.BOW)) {
            int var2 = this.getHardAttackInterval();
            if (this.level().getDifficulty() != Difficulty.HARD) {
               var2 = this.getAttackInterval();
            }

            this.bowGoal.setMinAttackInterval(var2);
            this.goalSelector.addGoal(4, this.bowGoal);
         } else {
            this.goalSelector.addGoal(4, this.meleeGoal);
         }

      }
   }

   protected int getHardAttackInterval() {
      return 20;
   }

   protected int getAttackInterval() {
      return 40;
   }

   public void performRangedAttack(LivingEntity var1, float var2) {
      ItemStack var3 = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
      ItemStack var4 = this.getProjectile(var3);
      AbstractArrow var5 = this.getArrow(var4, var2, var3);
      double var6 = var1.getX() - this.getX();
      double var8 = var1.getY(0.3333333333333333) - var5.getY();
      double var10 = var1.getZ() - this.getZ();
      double var12 = Math.sqrt(var6 * var6 + var10 * var10);
      Level var15 = this.level();
      if (var15 instanceof ServerLevel var14) {
         Projectile.spawnProjectileUsingShoot(var5, var14, var4, var6, var8 + var12 * 0.20000000298023224, var10, 1.6F, (float)(14 - var14.getDifficulty().getId() * 4));
      }

      this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   protected AbstractArrow getArrow(ItemStack var1, float var2, @Nullable ItemStack var3) {
      return ProjectileUtil.getMobArrow(this, var1, var2, var3);
   }

   public boolean canFireProjectileWeapon(ProjectileWeaponItem var1) {
      return var1 == Items.BOW;
   }

   public TagKey<Item> getPreferredWeaponType() {
      return ItemTags.SKELETON_PREFERRED_WEAPON;
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.reassessWeaponGoal();
   }

   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
      super.setItemSlot(var1, var2);
      if (!this.level().isClientSide) {
         this.reassessWeaponGoal();
      }

   }

   public boolean isShaking() {
      return this.isFullyFrozen();
   }
}
