package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

public class Pillager extends AbstractIllager implements CrossbowAttackMob, InventoryCarrier {
   private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Pillager.class, EntityDataSerializers.BOOLEAN);
   private static final int INVENTORY_SIZE = 5;
   private static final int SLOT_OFFSET = 300;
   private final SimpleContainer inventory = new SimpleContainer(5);

   public Pillager(EntityType<? extends Pillager> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
      this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0, 8.0F));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.3499999940395355)
         .add(Attributes.MAX_HEALTH, 24.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.FOLLOW_RANGE, 32.0);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(IS_CHARGING_CROSSBOW, false);
   }

   @Override
   public boolean canFireProjectileWeapon(ProjectileWeaponItem var1) {
      return var1 == Items.CROSSBOW;
   }

   public boolean isChargingCrossbow() {
      return this.entityData.get(IS_CHARGING_CROSSBOW);
   }

   @Override
   public void setChargingCrossbow(boolean var1) {
      this.entityData.set(IS_CHARGING_CROSSBOW, var1);
   }

   @Override
   public void onCrossbowAttackPerformed() {
      this.noActionTime = 0;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.writeInventoryToTag(var1, this.registryAccess());
   }

   @Override
   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isChargingCrossbow()) {
         return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
      } else if (this.isHolding(Items.CROSSBOW)) {
         return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
      } else {
         return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.readInventoryFromTag(var1, this.registryAccess());
      this.setCanPickUpLoot(true);
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   @Override
   public int getMaxSpawnClusterSize() {
      return 1;
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      this.populateDefaultEquipmentSlots(var5, var2);
      this.populateDefaultEquipmentEnchantments(var5, var2);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
   }

   @Override
   protected void enchantSpawnedWeapon(RandomSource var1, float var2) {
      super.enchantSpawnedWeapon(var1, var2);
      if (var1.nextInt(300) == 0) {
         ItemStack var3 = this.getMainHandItem();
         if (var3.is(Items.CROSSBOW)) {
            EnchantmentHelper.enchantItemFromProvider(var3, VanillaEnchantmentProviders.PILLAGER_SPAWN_CROSSBOW, this.level(), this.blockPosition(), var1);
         }
      }
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.PILLAGER_AMBIENT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.PILLAGER_DEATH;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PILLAGER_HURT;
   }

   @Override
   public void performRangedAttack(LivingEntity var1, float var2) {
      this.performCrossbowAttack(this, 1.6F);
   }

   @Override
   public SimpleContainer getInventory() {
      return this.inventory;
   }

   @Override
   protected void pickUpItem(ItemEntity var1) {
      ItemStack var2 = var1.getItem();
      if (var2.getItem() instanceof BannerItem) {
         super.pickUpItem(var1);
      } else if (this.wantsItem(var2)) {
         this.onItemPickup(var1);
         ItemStack var3 = this.inventory.addItem(var2);
         if (var3.isEmpty()) {
            var1.discard();
         } else {
            var2.setCount(var3.getCount());
         }
      }
   }

   private boolean wantsItem(ItemStack var1) {
      return this.hasActiveRaid() && var1.is(Items.WHITE_BANNER);
   }

   @Override
   public SlotAccess getSlot(int var1) {
      int var2 = var1 - 300;
      return var2 >= 0 && var2 < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, var2) : super.getSlot(var1);
   }

   @Override
   public void applyRaidBuffs(int var1, boolean var2) {
      Raid var3 = this.getCurrentRaid();
      boolean var4 = this.random.nextFloat() <= var3.getEnchantOdds();
      if (var4) {
         ItemStack var5 = new ItemStack(Items.CROSSBOW);
         ResourceKey var6;
         if (var1 > var3.getNumGroups(Difficulty.NORMAL)) {
            var6 = VanillaEnchantmentProviders.RAID_PILLAGER_POST_WAVE_5;
         } else if (var1 > var3.getNumGroups(Difficulty.EASY)) {
            var6 = VanillaEnchantmentProviders.RAID_PILLAGER_POST_WAVE_3;
         } else {
            var6 = null;
         }

         if (var6 != null) {
            EnchantmentHelper.enchantItemFromProvider(var5, var6, this.level(), this.blockPosition(), this.getRandom());
            this.setItemSlot(EquipmentSlot.MAINHAND, var5);
         }
      }
   }

   @Override
   public SoundEvent getCelebrateSound() {
      return SoundEvents.PILLAGER_CELEBRATE;
   }
}
