package net.minecraft.world.entity.monster;

import com.google.common.collect.Maps;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class Pillager extends AbstractIllager implements CrossbowAttackMob, RangedAttackMob {
   private static final EntityDataAccessor IS_CHARGING_CROSSBOW;
   private final SimpleContainer inventory = new SimpleContainer(5);

   public Pillager(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
      this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal(this, 1.0D, 8.0F));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IS_CHARGING_CROSSBOW, false);
   }

   public boolean isChargingCrossbow() {
      return (Boolean)this.entityData.get(IS_CHARGING_CROSSBOW);
   }

   public void setChargingCrossbow(boolean var1) {
      this.entityData.set(IS_CHARGING_CROSSBOW, var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      ListTag var2 = new ListTag();

      for(int var3 = 0; var3 < this.inventory.getContainerSize(); ++var3) {
         ItemStack var4 = this.inventory.getItem(var3);
         if (!var4.isEmpty()) {
            var2.add(var4.save(new CompoundTag()));
         }
      }

      var1.put("Inventory", var2);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isChargingCrossbow()) {
         return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
      } else if (this.isHolding(Items.CROSSBOW)) {
         return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
      } else {
         return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
      }
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      ListTag var2 = var1.getList("Inventory", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = ItemStack.of(var2.getCompound(var3));
         if (!var4.isEmpty()) {
            this.inventory.addItem(var4);
         }
      }

      this.setCanPickUpLoot(true);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      Block var3 = var2.getBlockState(var1.below()).getBlock();
      return var3 != Blocks.GRASS_BLOCK && var3 != Blocks.SAND ? 0.5F - var2.getBrightness(var1) : 10.0F;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.populateDefaultEquipmentSlots(var2);
      this.populateDefaultEquipmentEnchantments(var2);
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance var1) {
      ItemStack var2 = new ItemStack(Items.CROSSBOW);
      if (this.random.nextInt(300) == 0) {
         HashMap var3 = Maps.newHashMap();
         var3.put(Enchantments.PIERCING, 1);
         EnchantmentHelper.setEnchantments(var3, var2);
      }

      this.setItemSlot(EquipmentSlot.MAINHAND, var2);
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

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PILLAGER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PILLAGER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PILLAGER_HURT;
   }

   public void performRangedAttack(LivingEntity var1, float var2) {
      InteractionHand var3 = ProjectileUtil.getWeaponHoldingHand(this, Items.CROSSBOW);
      ItemStack var4 = this.getItemInHand(var3);
      if (this.isHolding(Items.CROSSBOW)) {
         CrossbowItem.performShooting(this.level, this, var3, var4, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      }

      this.noActionTime = 0;
   }

   public void shootProjectile(LivingEntity var1, ItemStack var2, Projectile var3, float var4) {
      Entity var5 = (Entity)var3;
      double var6 = var1.getX() - this.getX();
      double var8 = var1.getZ() - this.getZ();
      double var10 = (double)Mth.sqrt(var6 * var6 + var8 * var8);
      double var12 = var1.getY(0.3333333333333333D) - var5.getY() + var10 * 0.20000000298023224D;
      Vector3f var14 = this.getProjectileShotVector(new Vec3(var6, var12, var8), var4);
      var3.shoot((double)var14.x(), (double)var14.y(), (double)var14.z(), 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   private Vector3f getProjectileShotVector(Vec3 var1, float var2) {
      Vec3 var3 = var1.normalize();
      Vec3 var4 = var3.cross(new Vec3(0.0D, 1.0D, 0.0D));
      if (var4.lengthSqr() <= 1.0E-7D) {
         var4 = var3.cross(this.getUpVector(1.0F));
      }

      Quaternion var5 = new Quaternion(new Vector3f(var4), 90.0F, true);
      Vector3f var6 = new Vector3f(var3);
      var6.transform(var5);
      Quaternion var7 = new Quaternion(var6, var2, true);
      Vector3f var8 = new Vector3f(var3);
      var8.transform(var7);
      return var8;
   }

   protected void pickUpItem(ItemEntity var1) {
      ItemStack var2 = var1.getItem();
      if (var2.getItem() instanceof BannerItem) {
         super.pickUpItem(var1);
      } else {
         Item var3 = var2.getItem();
         if (this.wantsItem(var3)) {
            ItemStack var4 = this.inventory.addItem(var2);
            if (var4.isEmpty()) {
               var1.remove();
            } else {
               var2.setCount(var4.getCount());
            }
         }
      }

   }

   private boolean wantsItem(Item var1) {
      return this.hasActiveRaid() && var1 == Items.WHITE_BANNER;
   }

   public boolean setSlot(int var1, ItemStack var2) {
      if (super.setSlot(var1, var2)) {
         return true;
      } else {
         int var3 = var1 - 300;
         if (var3 >= 0 && var3 < this.inventory.getContainerSize()) {
            this.inventory.setItem(var3, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public void applyRaidBuffs(int var1, boolean var2) {
      Raid var3 = this.getCurrentRaid();
      boolean var4 = this.random.nextFloat() <= var3.getEnchantOdds();
      if (var4) {
         ItemStack var5 = new ItemStack(Items.CROSSBOW);
         HashMap var6 = Maps.newHashMap();
         if (var1 > var3.getNumGroups(Difficulty.NORMAL)) {
            var6.put(Enchantments.QUICK_CHARGE, 2);
         } else if (var1 > var3.getNumGroups(Difficulty.EASY)) {
            var6.put(Enchantments.QUICK_CHARGE, 1);
         }

         var6.put(Enchantments.MULTISHOT, 1);
         EnchantmentHelper.setEnchantments(var6, var5);
         this.setItemSlot(EquipmentSlot.MAINHAND, var5);
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.PILLAGER_CELEBRATE;
   }

   static {
      IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Pillager.class, EntityDataSerializers.BOOLEAN);
   }
}
