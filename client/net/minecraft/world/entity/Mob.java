package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class Mob extends LivingEntity implements Targeting {
   private static final EntityDataAccessor<Byte> DATA_MOB_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
   private static final int MOB_FLAG_NO_AI = 1;
   private static final int MOB_FLAG_LEFTHANDED = 2;
   private static final int MOB_FLAG_AGGRESSIVE = 4;
   protected static final int PICKUP_REACH = 1;
   private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 0, 1);
   public static final float MAX_WEARING_ARMOR_CHANCE = 0.15F;
   public static final float MAX_PICKUP_LOOT_CHANCE = 0.55F;
   public static final float MAX_ENCHANTED_ARMOR_CHANCE = 0.5F;
   public static final float MAX_ENCHANTED_WEAPON_CHANCE = 0.25F;
   public static final String LEASH_TAG = "Leash";
   public static final float DEFAULT_EQUIPMENT_DROP_CHANCE = 0.085F;
   public static final int PRESERVE_ITEM_DROP_CHANCE = 2;
   public static final int UPDATE_GOAL_SELECTOR_EVERY_N_TICKS = 2;
   public int ambientSoundTime;
   protected int xpReward;
   protected LookControl lookControl;
   protected MoveControl moveControl;
   protected JumpControl jumpControl;
   private final BodyRotationControl bodyRotationControl;
   protected PathNavigation navigation;
   protected final GoalSelector goalSelector;
   protected final GoalSelector targetSelector;
   @Nullable
   private LivingEntity target;
   private final Sensing sensing;
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   protected final float[] handDropChances = new float[2];
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   protected final float[] armorDropChances = new float[4];
   private boolean canPickUpLoot;
   private boolean persistenceRequired;
   private final Map<BlockPathTypes, Float> pathfindingMalus = Maps.newEnumMap(BlockPathTypes.class);
   @Nullable
   private ResourceLocation lootTable;
   private long lootTableSeed;
   @Nullable
   private Entity leashHolder;
   private int delayedLeashHolderId;
   @Nullable
   private CompoundTag leashInfoTag;
   private BlockPos restrictCenter = BlockPos.ZERO;
   private float restrictRadius = -1.0F;

   protected Mob(EntityType<? extends Mob> var1, Level var2) {
      super(var1, var2);
      this.goalSelector = new GoalSelector(var2.getProfilerSupplier());
      this.targetSelector = new GoalSelector(var2.getProfilerSupplier());
      this.lookControl = new LookControl(this);
      this.moveControl = new MoveControl(this);
      this.jumpControl = new JumpControl(this);
      this.bodyRotationControl = this.createBodyControl();
      this.navigation = this.createNavigation(var2);
      this.sensing = new Sensing(this);
      Arrays.fill(this.armorDropChances, 0.085F);
      Arrays.fill(this.handDropChances, 0.085F);
      if (var2 != null && !var2.isClientSide) {
         this.registerGoals();
      }
   }

   protected void registerGoals() {
   }

   public static AttributeSupplier.Builder createMobAttributes() {
      return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0).add(Attributes.ATTACK_KNOCKBACK);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new GroundPathNavigation(this, var1);
   }

   protected boolean shouldPassengersInheritMalus() {
      return false;
   }

   public float getPathfindingMalus(BlockPathTypes var1) {
      Mob var2;
      label17: {
         Entity var4 = this.getControlledVehicle();
         if (var4 instanceof Mob var3 && ((Mob)var3).shouldPassengersInheritMalus()) {
            var2 = (Mob)var3;
            break label17;
         }

         var2 = this;
      }

      Float var5 = var2.pathfindingMalus.get(var1);
      return var5 == null ? var1.getMalus() : var5;
   }

   public void setPathfindingMalus(BlockPathTypes var1, float var2) {
      this.pathfindingMalus.put(var1, var2);
   }

   public void onPathfindingStart() {
   }

   public void onPathfindingDone() {
   }

   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this);
   }

   public LookControl getLookControl() {
      return this.lookControl;
   }

   public MoveControl getMoveControl() {
      Entity var2 = this.getControlledVehicle();
      return var2 instanceof Mob var1 ? var1.getMoveControl() : this.moveControl;
   }

   public JumpControl getJumpControl() {
      return this.jumpControl;
   }

   public PathNavigation getNavigation() {
      Entity var2 = this.getControlledVehicle();
      return var2 instanceof Mob var1 ? var1.getNavigation() : this.navigation;
   }

   @Nullable
   @Override
   public LivingEntity getControllingPassenger() {
      if (!this.isNoAi()) {
         Entity var2 = this.getFirstPassenger();
         if (var2 instanceof Mob var1) {
            return (LivingEntity)var1;
         }
      }

      return null;
   }

   public Sensing getSensing() {
      return this.sensing;
   }

   @Nullable
   @Override
   public LivingEntity getTarget() {
      return this.target;
   }

   public void setTarget(@Nullable LivingEntity var1) {
      this.target = var1;
   }

   @Override
   public boolean canAttackType(EntityType<?> var1) {
      return var1 != EntityType.GHAST;
   }

   public boolean canFireProjectileWeapon(ProjectileWeaponItem var1) {
      return false;
   }

   public void ate() {
      this.gameEvent(GameEvent.EAT);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_MOB_FLAGS_ID, (byte)0);
   }

   public int getAmbientSoundInterval() {
      return 80;
   }

   public void playAmbientSound() {
      SoundEvent var1 = this.getAmbientSound();
      if (var1 != null) {
         this.playSound(var1, this.getSoundVolume(), this.getVoicePitch());
      }
   }

   @Override
   public void baseTick() {
      super.baseTick();
      this.level().getProfiler().push("mobBaseTick");
      if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
         this.resetAmbientSoundTime();
         this.playAmbientSound();
      }

      this.level().getProfiler().pop();
   }

   @Override
   protected void playHurtSound(DamageSource var1) {
      this.resetAmbientSoundTime();
      super.playHurtSound(var1);
   }

   private void resetAmbientSoundTime() {
      this.ambientSoundTime = -this.getAmbientSoundInterval();
   }

   @Override
   public int getExperienceReward() {
      if (this.xpReward > 0) {
         int var1 = this.xpReward;

         for(int var2 = 0; var2 < this.armorItems.size(); ++var2) {
            if (!this.armorItems.get(var2).isEmpty() && this.armorDropChances[var2] <= 1.0F) {
               var1 += 1 + this.random.nextInt(3);
            }
         }

         for(int var3 = 0; var3 < this.handItems.size(); ++var3) {
            if (!this.handItems.get(var3).isEmpty() && this.handDropChances[var3] <= 1.0F) {
               var1 += 1 + this.random.nextInt(3);
            }
         }

         return var1;
      } else {
         return this.xpReward;
      }
   }

   public void spawnAnim() {
      if (this.level().isClientSide) {
         for(int var1 = 0; var1 < 20; ++var1) {
            double var2 = this.random.nextGaussian() * 0.02;
            double var4 = this.random.nextGaussian() * 0.02;
            double var6 = this.random.nextGaussian() * 0.02;
            double var8 = 10.0;
            this.level()
               .addParticle(
                  ParticleTypes.POOF, this.getX(1.0) - var2 * 10.0, this.getRandomY() - var4 * 10.0, this.getRandomZ(1.0) - var6 * 10.0, var2, var4, var6
               );
         }
      } else {
         this.level().broadcastEntityEvent(this, (byte)20);
      }
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 20) {
         this.spawnAnim();
      } else {
         super.handleEntityEvent(var1);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.tickLeash();
         if (this.tickCount % 5 == 0) {
            this.updateControlFlags();
         }
      }
   }

   protected void updateControlFlags() {
      boolean var1 = !(this.getControllingPassenger() instanceof Mob);
      boolean var2 = !(this.getVehicle() instanceof Boat);
      this.goalSelector.setControlFlag(Goal.Flag.MOVE, var1);
      this.goalSelector.setControlFlag(Goal.Flag.JUMP, var1 && var2);
      this.goalSelector.setControlFlag(Goal.Flag.LOOK, var1);
   }

   @Override
   protected float tickHeadTurn(float var1, float var2) {
      this.bodyRotationControl.clientTick();
      return var2;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("CanPickUpLoot", this.canPickUpLoot());
      var1.putBoolean("PersistenceRequired", this.persistenceRequired);
      ListTag var2 = new ListTag();

      for(ItemStack var4 : this.armorItems) {
         CompoundTag var5 = new CompoundTag();
         if (!var4.isEmpty()) {
            var4.save(var5);
         }

         var2.add(var5);
      }

      var1.put("ArmorItems", var2);
      ListTag var10 = new ListTag();

      for(ItemStack var13 : this.handItems) {
         CompoundTag var6 = new CompoundTag();
         if (!var13.isEmpty()) {
            var13.save(var6);
         }

         var10.add(var6);
      }

      var1.put("HandItems", var10);
      ListTag var12 = new ListTag();

      for(float var8 : this.armorDropChances) {
         var12.add(FloatTag.valueOf(var8));
      }

      var1.put("ArmorDropChances", var12);
      ListTag var15 = new ListTag();

      for(float var9 : this.handDropChances) {
         var15.add(FloatTag.valueOf(var9));
      }

      var1.put("HandDropChances", var15);
      if (this.leashHolder != null) {
         CompoundTag var18 = new CompoundTag();
         if (this.leashHolder instanceof LivingEntity) {
            UUID var20 = this.leashHolder.getUUID();
            var18.putUUID("UUID", var20);
         } else if (this.leashHolder instanceof HangingEntity) {
            BlockPos var21 = ((HangingEntity)this.leashHolder).getPos();
            var18.putInt("X", var21.getX());
            var18.putInt("Y", var21.getY());
            var18.putInt("Z", var21.getZ());
         }

         var1.put("Leash", var18);
      } else if (this.leashInfoTag != null) {
         var1.put("Leash", this.leashInfoTag.copy());
      }

      var1.putBoolean("LeftHanded", this.isLeftHanded());
      if (this.lootTable != null) {
         var1.putString("DeathLootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            var1.putLong("DeathLootTableSeed", this.lootTableSeed);
         }
      }

      if (this.isNoAi()) {
         var1.putBoolean("NoAI", this.isNoAi());
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(var1.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = var1.getBoolean("PersistenceRequired");
      if (var1.contains("ArmorItems", 9)) {
         ListTag var2 = var1.getList("ArmorItems", 10);

         for(int var3 = 0; var3 < this.armorItems.size(); ++var3) {
            this.armorItems.set(var3, ItemStack.of(var2.getCompound(var3)));
         }
      }

      if (var1.contains("HandItems", 9)) {
         ListTag var4 = var1.getList("HandItems", 10);

         for(int var7 = 0; var7 < this.handItems.size(); ++var7) {
            this.handItems.set(var7, ItemStack.of(var4.getCompound(var7)));
         }
      }

      if (var1.contains("ArmorDropChances", 9)) {
         ListTag var5 = var1.getList("ArmorDropChances", 5);

         for(int var8 = 0; var8 < var5.size(); ++var8) {
            this.armorDropChances[var8] = var5.getFloat(var8);
         }
      }

      if (var1.contains("HandDropChances", 9)) {
         ListTag var6 = var1.getList("HandDropChances", 5);

         for(int var9 = 0; var9 < var6.size(); ++var9) {
            this.handDropChances[var9] = var6.getFloat(var9);
         }
      }

      if (var1.contains("Leash", 10)) {
         this.leashInfoTag = var1.getCompound("Leash");
      }

      this.setLeftHanded(var1.getBoolean("LeftHanded"));
      if (var1.contains("DeathLootTable", 8)) {
         this.lootTable = new ResourceLocation(var1.getString("DeathLootTable"));
         this.lootTableSeed = var1.getLong("DeathLootTableSeed");
      }

      this.setNoAi(var1.getBoolean("NoAI"));
   }

   @Override
   protected void dropFromLootTable(DamageSource var1, boolean var2) {
      super.dropFromLootTable(var1, var2);
      this.lootTable = null;
   }

   @Override
   public final ResourceLocation getLootTable() {
      return this.lootTable == null ? this.getDefaultLootTable() : this.lootTable;
   }

   protected ResourceLocation getDefaultLootTable() {
      return super.getLootTable();
   }

   @Override
   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setZza(float var1) {
      this.zza = var1;
   }

   public void setYya(float var1) {
      this.yya = var1;
   }

   public void setXxa(float var1) {
      this.xxa = var1;
   }

   @Override
   public void setSpeed(float var1) {
      super.setSpeed(var1);
      this.setZza(var1);
   }

   @Override
   public void aiStep() {
      super.aiStep();
      this.level().getProfiler().push("looting");
      if (!this.level().isClientSide
         && this.canPickUpLoot()
         && this.isAlive()
         && !this.dead
         && this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         Vec3i var1 = this.getPickupReach();

         for(ItemEntity var4 : this.level()
            .getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()))) {
            if (!var4.isRemoved() && !var4.getItem().isEmpty() && !var4.hasPickUpDelay() && this.wantsToPickUp(var4.getItem())) {
               this.pickUpItem(var4);
            }
         }
      }

      this.level().getProfiler().pop();
   }

   protected Vec3i getPickupReach() {
      return ITEM_PICKUP_REACH;
   }

   protected void pickUpItem(ItemEntity var1) {
      ItemStack var2 = var1.getItem();
      ItemStack var3 = this.equipItemIfPossible(var2.copy());
      if (!var3.isEmpty()) {
         this.onItemPickup(var1);
         this.take(var1, var3.getCount());
         var2.shrink(var3.getCount());
         if (var2.isEmpty()) {
            var1.discard();
         }
      }
   }

   public ItemStack equipItemIfPossible(ItemStack var1) {
      EquipmentSlot var2 = getEquipmentSlotForItem(var1);
      ItemStack var3 = this.getItemBySlot(var2);
      boolean var4 = this.canReplaceCurrentItem(var1, var3);
      if (var2.isArmor() && !var4) {
         var2 = EquipmentSlot.MAINHAND;
         var3 = this.getItemBySlot(var2);
         var4 = var3.isEmpty();
      }

      if (var4 && this.canHoldItem(var1)) {
         double var5 = (double)this.getEquipmentDropChance(var2);
         if (!var3.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < var5) {
            this.spawnAtLocation(var3);
         }

         if (var2.isArmor() && var1.getCount() > 1) {
            ItemStack var7 = var1.copyWithCount(1);
            this.setItemSlotAndDropWhenKilled(var2, var7);
            return var7;
         } else {
            this.setItemSlotAndDropWhenKilled(var2, var1);
            return var1;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   protected void setItemSlotAndDropWhenKilled(EquipmentSlot var1, ItemStack var2) {
      this.setItemSlot(var1, var2);
      this.setGuaranteedDrop(var1);
      this.persistenceRequired = true;
   }

   public void setGuaranteedDrop(EquipmentSlot var1) {
      switch(var1.getType()) {
         case HAND:
            this.handDropChances[var1.getIndex()] = 2.0F;
            break;
         case ARMOR:
            this.armorDropChances[var1.getIndex()] = 2.0F;
      }
   }

   protected boolean canReplaceCurrentItem(ItemStack var1, ItemStack var2) {
      if (var2.isEmpty()) {
         return true;
      } else if (var1.getItem() instanceof SwordItem) {
         if (!(var2.getItem() instanceof SwordItem)) {
            return true;
         } else {
            SwordItem var6 = (SwordItem)var1.getItem();
            SwordItem var8 = (SwordItem)var2.getItem();
            if (var6.getDamage() != var8.getDamage()) {
               return var6.getDamage() > var8.getDamage();
            } else {
               return this.canReplaceEqualItem(var1, var2);
            }
         }
      } else if (var1.getItem() instanceof BowItem && var2.getItem() instanceof BowItem) {
         return this.canReplaceEqualItem(var1, var2);
      } else if (var1.getItem() instanceof CrossbowItem && var2.getItem() instanceof CrossbowItem) {
         return this.canReplaceEqualItem(var1, var2);
      } else if (var1.getItem() instanceof ArmorItem) {
         if (EnchantmentHelper.hasBindingCurse(var2)) {
            return false;
         } else if (!(var2.getItem() instanceof ArmorItem)) {
            return true;
         } else {
            ArmorItem var5 = (ArmorItem)var1.getItem();
            ArmorItem var7 = (ArmorItem)var2.getItem();
            if (var5.getDefense() != var7.getDefense()) {
               return var5.getDefense() > var7.getDefense();
            } else if (var5.getToughness() != var7.getToughness()) {
               return var5.getToughness() > var7.getToughness();
            } else {
               return this.canReplaceEqualItem(var1, var2);
            }
         }
      } else {
         if (var1.getItem() instanceof DiggerItem) {
            if (var2.getItem() instanceof BlockItem) {
               return true;
            }

            if (var2.getItem() instanceof DiggerItem) {
               DiggerItem var3 = (DiggerItem)var1.getItem();
               DiggerItem var4 = (DiggerItem)var2.getItem();
               if (var3.getAttackDamage() != var4.getAttackDamage()) {
                  return var3.getAttackDamage() > var4.getAttackDamage();
               }

               return this.canReplaceEqualItem(var1, var2);
            }
         }

         return false;
      }
   }

   public boolean canReplaceEqualItem(ItemStack var1, ItemStack var2) {
      if (var1.getDamageValue() >= var2.getDamageValue() && (!var1.hasTag() || var2.hasTag())) {
         if (var1.hasTag() && var2.hasTag()) {
            return var1.getTag().getAllKeys().stream().anyMatch(var0 -> !var0.equals("Damage"))
               && !var2.getTag().getAllKeys().stream().anyMatch(var0 -> !var0.equals("Damage"));
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public boolean canHoldItem(ItemStack var1) {
      return true;
   }

   public boolean wantsToPickUp(ItemStack var1) {
      return this.canHoldItem(var1);
   }

   public boolean removeWhenFarAway(double var1) {
      return true;
   }

   public boolean requiresCustomPersistence() {
      return this.isPassenger();
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   @Override
   public void checkDespawn() {
      if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
         this.discard();
      } else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
         Player var1 = this.level().getNearestPlayer(this, -1.0);
         if (var1 != null) {
            double var2 = var1.distanceToSqr(this);
            int var4 = this.getType().getCategory().getDespawnDistance();
            int var5 = var4 * var4;
            if (var2 > (double)var5 && this.removeWhenFarAway(var2)) {
               this.discard();
            }

            int var6 = this.getType().getCategory().getNoDespawnDistance();
            int var7 = var6 * var6;
            if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && var2 > (double)var7 && this.removeWhenFarAway(var2)) {
               this.discard();
            } else if (var2 < (double)var7) {
               this.noActionTime = 0;
            }
         }
      } else {
         this.noActionTime = 0;
      }
   }

   @Override
   protected final void serverAiStep() {
      ++this.noActionTime;
      this.level().getProfiler().push("sensing");
      this.sensing.tick();
      this.level().getProfiler().pop();
      int var1 = this.level().getServer().getTickCount() + this.getId();
      if (var1 % 2 != 0 && this.tickCount > 1) {
         this.level().getProfiler().push("targetSelector");
         this.targetSelector.tickRunningGoals(false);
         this.level().getProfiler().pop();
         this.level().getProfiler().push("goalSelector");
         this.goalSelector.tickRunningGoals(false);
         this.level().getProfiler().pop();
      } else {
         this.level().getProfiler().push("targetSelector");
         this.targetSelector.tick();
         this.level().getProfiler().pop();
         this.level().getProfiler().push("goalSelector");
         this.goalSelector.tick();
         this.level().getProfiler().pop();
      }

      this.level().getProfiler().push("navigation");
      this.navigation.tick();
      this.level().getProfiler().pop();
      this.level().getProfiler().push("mob tick");
      this.customServerAiStep();
      this.level().getProfiler().pop();
      this.level().getProfiler().push("controls");
      this.level().getProfiler().push("move");
      this.moveControl.tick();
      this.level().getProfiler().popPush("look");
      this.lookControl.tick();
      this.level().getProfiler().popPush("jump");
      this.jumpControl.tick();
      this.level().getProfiler().pop();
      this.level().getProfiler().pop();
      this.sendDebugPackets();
   }

   protected void sendDebugPackets() {
      DebugPackets.sendGoalSelector(this.level(), this, this.goalSelector);
   }

   protected void customServerAiStep() {
   }

   public int getMaxHeadXRot() {
      return 40;
   }

   public int getMaxHeadYRot() {
      return 75;
   }

   public int getHeadRotSpeed() {
      return 10;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void lookAt(Entity var1, float var2, float var3) {
      double var4 = var1.getX() - this.getX();
      double var8 = var1.getZ() - this.getZ();
      double var6;
      if (var1 instanceof LivingEntity var10) {
         var6 = var10.getEyeY() - this.getEyeY();
      } else {
         var6 = (var1.getBoundingBox().minY + var1.getBoundingBox().maxY) / 2.0 - this.getEyeY();
      }

      double var14 = Math.sqrt(var4 * var4 + var8 * var8);
      float var12 = (float)(Mth.atan2(var8, var4) * 57.2957763671875) - 90.0F;
      float var13 = (float)(-(Mth.atan2(var6, var14) * 57.2957763671875));
      this.setXRot(this.rotlerp(this.getXRot(), var13, var3));
      this.setYRot(this.rotlerp(this.getYRot(), var12, var2));
   }

   private float rotlerp(float var1, float var2, float var3) {
      float var4 = Mth.wrapDegrees(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      return var1 + var4;
   }

   public static boolean checkMobSpawnRules(EntityType<? extends Mob> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      BlockPos var5 = var3.below();
      return var2 == MobSpawnType.SPAWNER || var1.getBlockState(var5).isValidSpawn(var1, var5, var0);
   }

   public boolean checkSpawnRules(LevelAccessor var1, MobSpawnType var2) {
      return true;
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return !var1.containsAnyLiquid(this.getBoundingBox()) && var1.isUnobstructed(this);
   }

   public int getMaxSpawnClusterSize() {
      return 4;
   }

   public boolean isMaxGroupSizeReached(int var1) {
      return false;
   }

   @Override
   public int getMaxFallDistance() {
      if (this.getTarget() == null) {
         return 3;
      } else {
         int var1 = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         var1 -= (3 - this.level().getDifficulty().getId()) * 4;
         if (var1 < 0) {
            var1 = 0;
         }

         return var1 + 3;
      }
   }

   @Override
   public Iterable<ItemStack> getHandSlots() {
      return this.handItems;
   }

   @Override
   public Iterable<ItemStack> getArmorSlots() {
      return this.armorItems;
   }

   @Override
   public ItemStack getItemBySlot(EquipmentSlot var1) {
      switch(var1.getType()) {
         case HAND:
            return this.handItems.get(var1.getIndex());
         case ARMOR:
            return this.armorItems.get(var1.getIndex());
         default:
            return ItemStack.EMPTY;
      }
   }

   @Override
   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
      this.verifyEquippedItem(var2);
      switch(var1.getType()) {
         case HAND:
            this.onEquipItem(var1, this.handItems.set(var1.getIndex(), var2), var2);
            break;
         case ARMOR:
            this.onEquipItem(var1, this.armorItems.set(var1.getIndex(), var2), var2);
      }
   }

   @Override
   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);

      for(EquipmentSlot var7 : EquipmentSlot.values()) {
         ItemStack var8 = this.getItemBySlot(var7);
         float var9 = this.getEquipmentDropChance(var7);
         boolean var10 = var9 > 1.0F;
         if (!var8.isEmpty()
            && !EnchantmentHelper.hasVanishingCurse(var8)
            && (var3 || var10)
            && Math.max(this.random.nextFloat() - (float)var2 * 0.01F, 0.0F) < var9) {
            if (!var10 && var8.isDamageableItem()) {
               var8.setDamageValue(var8.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(var8.getMaxDamage() - 3, 1))));
            }

            this.spawnAtLocation(var8);
            this.setItemSlot(var7, ItemStack.EMPTY);
         }
      }
   }

   protected float getEquipmentDropChance(EquipmentSlot var1) {
      return switch(var1.getType()) {
         case HAND -> this.handDropChances[var1.getIndex()];
         case ARMOR -> this.armorDropChances[var1.getIndex()];
         default -> 0.0F;
      };
   }

   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      if (var1.nextFloat() < 0.15F * var2.getSpecialMultiplier()) {
         int var3 = var1.nextInt(2);
         float var4 = this.level().getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
         if (var1.nextFloat() < 0.095F) {
            ++var3;
         }

         if (var1.nextFloat() < 0.095F) {
            ++var3;
         }

         if (var1.nextFloat() < 0.095F) {
            ++var3;
         }

         boolean var5 = true;

         for(EquipmentSlot var9 : EquipmentSlot.values()) {
            if (var9.getType() == EquipmentSlot.Type.ARMOR) {
               ItemStack var10 = this.getItemBySlot(var9);
               if (!var5 && var1.nextFloat() < var4) {
                  break;
               }

               var5 = false;
               if (var10.isEmpty()) {
                  Item var11 = getEquipmentForSlot(var9, var3);
                  if (var11 != null) {
                     this.setItemSlot(var9, new ItemStack(var11));
                  }
               }
            }
         }
      }
   }

   @Nullable
   public static Item getEquipmentForSlot(EquipmentSlot var0, int var1) {
      switch(var0) {
         case HEAD:
            if (var1 == 0) {
               return Items.LEATHER_HELMET;
            } else if (var1 == 1) {
               return Items.GOLDEN_HELMET;
            } else if (var1 == 2) {
               return Items.CHAINMAIL_HELMET;
            } else if (var1 == 3) {
               return Items.IRON_HELMET;
            } else if (var1 == 4) {
               return Items.DIAMOND_HELMET;
            }
         case CHEST:
            if (var1 == 0) {
               return Items.LEATHER_CHESTPLATE;
            } else if (var1 == 1) {
               return Items.GOLDEN_CHESTPLATE;
            } else if (var1 == 2) {
               return Items.CHAINMAIL_CHESTPLATE;
            } else if (var1 == 3) {
               return Items.IRON_CHESTPLATE;
            } else if (var1 == 4) {
               return Items.DIAMOND_CHESTPLATE;
            }
         case LEGS:
            if (var1 == 0) {
               return Items.LEATHER_LEGGINGS;
            } else if (var1 == 1) {
               return Items.GOLDEN_LEGGINGS;
            } else if (var1 == 2) {
               return Items.CHAINMAIL_LEGGINGS;
            } else if (var1 == 3) {
               return Items.IRON_LEGGINGS;
            } else if (var1 == 4) {
               return Items.DIAMOND_LEGGINGS;
            }
         case FEET:
            if (var1 == 0) {
               return Items.LEATHER_BOOTS;
            } else if (var1 == 1) {
               return Items.GOLDEN_BOOTS;
            } else if (var1 == 2) {
               return Items.CHAINMAIL_BOOTS;
            } else if (var1 == 3) {
               return Items.IRON_BOOTS;
            } else if (var1 == 4) {
               return Items.DIAMOND_BOOTS;
            }
         default:
            return null;
      }
   }

   protected void populateDefaultEquipmentEnchantments(RandomSource var1, DifficultyInstance var2) {
      float var3 = var2.getSpecialMultiplier();
      this.enchantSpawnedWeapon(var1, var3);

      for(EquipmentSlot var7 : EquipmentSlot.values()) {
         if (var7.getType() == EquipmentSlot.Type.ARMOR) {
            this.enchantSpawnedArmor(var1, var3, var7);
         }
      }
   }

   protected void enchantSpawnedWeapon(RandomSource var1, float var2) {
      if (!this.getMainHandItem().isEmpty() && var1.nextFloat() < 0.25F * var2) {
         this.setItemSlot(
            EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(var1, this.getMainHandItem(), (int)(5.0F + var2 * (float)var1.nextInt(18)), false)
         );
      }
   }

   protected void enchantSpawnedArmor(RandomSource var1, float var2, EquipmentSlot var3) {
      ItemStack var4 = this.getItemBySlot(var3);
      if (!var4.isEmpty() && var1.nextFloat() < 0.5F * var2) {
         this.setItemSlot(var3, EnchantmentHelper.enchantItem(var1, var4, (int)(5.0F + var2 * (float)var1.nextInt(18)), false));
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      RandomSource var6 = var1.getRandom();
      this.getAttribute(Attributes.FOLLOW_RANGE)
         .addPermanentModifier(new AttributeModifier("Random spawn bonus", var6.triangle(0.0, 0.11485000000000001), AttributeModifier.Operation.MULTIPLY_BASE));
      if (var6.nextFloat() < 0.05F) {
         this.setLeftHanded(true);
      } else {
         this.setLeftHanded(false);
      }

      return var4;
   }

   public void setPersistenceRequired() {
      this.persistenceRequired = true;
   }

   public void setDropChance(EquipmentSlot var1, float var2) {
      switch(var1.getType()) {
         case HAND:
            this.handDropChances[var1.getIndex()] = var2;
            break;
         case ARMOR:
            this.armorDropChances[var1.getIndex()] = var2;
      }
   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean var1) {
      this.canPickUpLoot = var1;
   }

   @Override
   public boolean canTakeItem(ItemStack var1) {
      EquipmentSlot var2 = getEquipmentSlotForItem(var1);
      return this.getItemBySlot(var2).isEmpty() && this.canPickUpLoot();
   }

   public boolean isPersistenceRequired() {
      return this.persistenceRequired;
   }

   @Override
   public final InteractionResult interact(Player var1, InteractionHand var2) {
      if (!this.isAlive()) {
         return InteractionResult.PASS;
      } else if (this.getLeashHolder() == var1) {
         this.dropLeash(true, !var1.getAbilities().instabuild);
         this.gameEvent(GameEvent.ENTITY_INTERACT, var1);
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         InteractionResult var3 = this.checkAndHandleImportantInteractions(var1, var2);
         if (var3.consumesAction()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT, var1);
            return var3;
         } else {
            var3 = this.mobInteract(var1, var2);
            if (var3.consumesAction()) {
               this.gameEvent(GameEvent.ENTITY_INTERACT, var1);
               return var3;
            } else {
               return super.interact(var1, var2);
            }
         }
      }
   }

   private InteractionResult checkAndHandleImportantInteractions(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.LEAD) && this.canBeLeashed(var1)) {
         this.setLeashedTo(var1, true);
         var3.shrink(1);
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         if (var3.is(Items.NAME_TAG)) {
            InteractionResult var4 = var3.interactLivingEntity(var1, this, var2);
            if (var4.consumesAction()) {
               return var4;
            }
         }

         if (var3.getItem() instanceof SpawnEggItem) {
            if (this.level() instanceof ServerLevel) {
               SpawnEggItem var6 = (SpawnEggItem)var3.getItem();
               Optional var5 = var6.spawnOffspringFromSpawnEgg(var1, this, this.getType(), (ServerLevel)this.level(), this.position(), var3);
               var5.ifPresent(var2x -> this.onOffspringSpawnedFromEgg(var1, var2x));
               return var5.isPresent() ? InteractionResult.SUCCESS : InteractionResult.PASS;
            } else {
               return InteractionResult.CONSUME;
            }
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   protected void onOffspringSpawnedFromEgg(Player var1, Mob var2) {
   }

   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      return InteractionResult.PASS;
   }

   public boolean isWithinRestriction() {
      return this.isWithinRestriction(this.blockPosition());
   }

   public boolean isWithinRestriction(BlockPos var1) {
      if (this.restrictRadius == -1.0F) {
         return true;
      } else {
         return this.restrictCenter.distSqr(var1) < (double)(this.restrictRadius * this.restrictRadius);
      }
   }

   public void restrictTo(BlockPos var1, int var2) {
      this.restrictCenter = var1;
      this.restrictRadius = (float)var2;
   }

   public BlockPos getRestrictCenter() {
      return this.restrictCenter;
   }

   public float getRestrictRadius() {
      return this.restrictRadius;
   }

   public void clearRestriction() {
      this.restrictRadius = -1.0F;
   }

   public boolean hasRestriction() {
      return this.restrictRadius != -1.0F;
   }

   @Nullable
   public <T extends Mob> T convertTo(EntityType<T> var1, boolean var2) {
      if (this.isRemoved()) {
         return null;
      } else {
         Mob var3 = (Mob)var1.create(this.level());
         if (var3 == null) {
            return null;
         } else {
            var3.copyPosition(this);
            var3.setBaby(this.isBaby());
            var3.setNoAi(this.isNoAi());
            if (this.hasCustomName()) {
               var3.setCustomName(this.getCustomName());
               var3.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isPersistenceRequired()) {
               var3.setPersistenceRequired();
            }

            var3.setInvulnerable(this.isInvulnerable());
            if (var2) {
               var3.setCanPickUpLoot(this.canPickUpLoot());

               for(EquipmentSlot var7 : EquipmentSlot.values()) {
                  ItemStack var8 = this.getItemBySlot(var7);
                  if (!var8.isEmpty()) {
                     var3.setItemSlot(var7, var8.copyAndClear());
                     var3.setDropChance(var7, this.getEquipmentDropChance(var7));
                  }
               }
            }

            this.level().addFreshEntity(var3);
            if (this.isPassenger()) {
               Entity var9 = this.getVehicle();
               this.stopRiding();
               var3.startRiding(var9, true);
            }

            this.discard();
            return (T)var3;
         }
      }
   }

   protected void tickLeash() {
      if (this.leashInfoTag != null) {
         this.restoreLeashFromSave();
      }

      if (this.leashHolder != null) {
         if (!this.isAlive() || !this.leashHolder.isAlive()) {
            this.dropLeash(true, true);
         }
      }
   }

   public void dropLeash(boolean var1, boolean var2) {
      if (this.leashHolder != null) {
         this.leashHolder = null;
         this.leashInfoTag = null;
         if (!this.level().isClientSide && var2) {
            this.spawnAtLocation(Items.LEAD);
         }

         if (!this.level().isClientSide && var1 && this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
         }
      }
   }

   public boolean canBeLeashed(Player var1) {
      return !this.isLeashed() && !(this instanceof Enemy);
   }

   public boolean isLeashed() {
      return this.leashHolder != null;
   }

   @Nullable
   public Entity getLeashHolder() {
      if (this.leashHolder == null && this.delayedLeashHolderId != 0 && this.level().isClientSide) {
         this.leashHolder = this.level().getEntity(this.delayedLeashHolderId);
      }

      return this.leashHolder;
   }

   public void setLeashedTo(Entity var1, boolean var2) {
      this.leashHolder = var1;
      this.leashInfoTag = null;
      if (!this.level().isClientSide && var2 && this.level() instanceof ServerLevel) {
         ((ServerLevel)this.level()).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, this.leashHolder));
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }
   }

   public void setDelayedLeashHolderId(int var1) {
      this.delayedLeashHolderId = var1;
      this.dropLeash(false, false);
   }

   @Override
   public boolean startRiding(Entity var1, boolean var2) {
      boolean var3 = super.startRiding(var1, var2);
      if (var3 && this.isLeashed()) {
         this.dropLeash(true, true);
      }

      return var3;
   }

   private void restoreLeashFromSave() {
      if (this.leashInfoTag != null && this.level() instanceof ServerLevel) {
         if (this.leashInfoTag.hasUUID("UUID")) {
            UUID var1 = this.leashInfoTag.getUUID("UUID");
            Entity var2 = ((ServerLevel)this.level()).getEntity(var1);
            if (var2 != null) {
               this.setLeashedTo(var2, true);
               return;
            }
         } else if (this.leashInfoTag.contains("X", 99) && this.leashInfoTag.contains("Y", 99) && this.leashInfoTag.contains("Z", 99)) {
            BlockPos var3 = NbtUtils.readBlockPos(this.leashInfoTag);
            this.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(this.level(), var3), true);
            return;
         }

         if (this.tickCount > 100) {
            this.spawnAtLocation(Items.LEAD);
            this.leashInfoTag = null;
         }
      }
   }

   @Override
   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && !this.isNoAi();
   }

   public void setNoAi(boolean var1) {
      byte var2 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, var1 ? (byte)(var2 | 1) : (byte)(var2 & -2));
   }

   public void setLeftHanded(boolean var1) {
      byte var2 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, var1 ? (byte)(var2 | 2) : (byte)(var2 & -3));
   }

   public void setAggressive(boolean var1) {
      byte var2 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, var1 ? (byte)(var2 | 4) : (byte)(var2 & -5));
   }

   public boolean isNoAi() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 1) != 0;
   }

   public boolean isLeftHanded() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 2) != 0;
   }

   public boolean isAggressive() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 4) != 0;
   }

   public void setBaby(boolean var1) {
   }

   @Override
   public HumanoidArm getMainArm() {
      return this.isLeftHanded() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
   }

   public double getMeleeAttackRangeSqr(LivingEntity var1) {
      return (double)(this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + var1.getBbWidth());
   }

   public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity var1) {
      return Math.max(this.distanceToSqr(var1.getMeleeAttackReferencePosition()), this.distanceToSqr(var1.position()));
   }

   public boolean isWithinMeleeAttackRange(LivingEntity var1) {
      double var2 = this.getPerceivedTargetDistanceSquareForMeleeAttack(var1);
      return var2 <= this.getMeleeAttackRangeSqr(var1);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      float var2 = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float var3 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      if (var1 instanceof LivingEntity) {
         var2 += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)var1).getMobType());
         var3 += (float)EnchantmentHelper.getKnockbackBonus(this);
      }

      int var4 = EnchantmentHelper.getFireAspect(this);
      if (var4 > 0) {
         var1.setSecondsOnFire(var4 * 4);
      }

      boolean var5 = var1.hurt(this.damageSources().mobAttack(this), var2);
      if (var5) {
         if (var3 > 0.0F && var1 instanceof LivingEntity) {
            ((LivingEntity)var1)
               .knockback((double)(var3 * 0.5F), (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         }

         if (var1 instanceof Player var6) {
            this.maybeDisableShield((Player)var6, this.getMainHandItem(), ((Player)var6).isUsingItem() ? ((Player)var6).getUseItem() : ItemStack.EMPTY);
         }

         this.doEnchantDamageEffects(this, var1);
         this.setLastHurtMob(var1);
      }

      return var5;
   }

   private void maybeDisableShield(Player var1, ItemStack var2, ItemStack var3) {
      if (!var2.isEmpty() && !var3.isEmpty() && var2.getItem() instanceof AxeItem && var3.is(Items.SHIELD)) {
         float var4 = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
         if (this.random.nextFloat() < var4) {
            var1.getCooldowns().addCooldown(Items.SHIELD, 100);
            this.level().broadcastEntityEvent(var1, (byte)30);
         }
      }
   }

   protected boolean isSunBurnTick() {
      if (this.level().isDay() && !this.level().isClientSide) {
         float var1 = this.getLightLevelDependentMagicValue();
         BlockPos var2 = BlockPos.containing(this.getX(), this.getEyeY(), this.getZ());
         boolean var3 = this.isInWaterRainOrBubble() || this.isInPowderSnow || this.wasInPowderSnow;
         if (var1 > 0.5F && this.random.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && !var3 && this.level().canSeeSky(var2)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void jumpInLiquid(TagKey<Fluid> var1) {
      if (this.getNavigation().canFloat()) {
         super.jumpInLiquid(var1);
      } else {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.3, 0.0));
      }
   }

   public void removeFreeWill() {
      this.removeAllGoals(var0 -> true);
      this.getBrain().removeAllBehaviors();
   }

   public void removeAllGoals(Predicate<Goal> var1) {
      this.goalSelector.removeAllGoals(var1);
   }

   @Override
   protected void removeAfterChangingDimensions() {
      super.removeAfterChangingDimensions();
      this.dropLeash(true, false);
      this.getAllSlots().forEach(var0 -> {
         if (!var0.isEmpty()) {
            var0.setCount(0);
         }
      });
   }

   @Nullable
   @Override
   public ItemStack getPickResult() {
      SpawnEggItem var1 = SpawnEggItem.byId(this.getType());
      return var1 == null ? null : new ItemStack(var1);
   }
}
