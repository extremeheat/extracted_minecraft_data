package net.minecraft.world.entity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
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
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

public abstract class Mob extends LivingEntity implements EquipmentUser, Leashable, Targeting {
   private static final EntityDataAccessor<Byte> DATA_MOB_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
   private static final int MOB_FLAG_NO_AI = 1;
   private static final int MOB_FLAG_LEFTHANDED = 2;
   private static final int MOB_FLAG_AGGRESSIVE = 4;
   protected static final int PICKUP_REACH = 1;
   private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 0, 1);
   private static final List<EquipmentSlot> EQUIPMENT_POPULATION_ORDER = List.of(
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
   );
   public static final float MAX_WEARING_ARMOR_CHANCE = 0.15F;
   public static final float MAX_PICKUP_LOOT_CHANCE = 0.55F;
   public static final float MAX_ENCHANTED_ARMOR_CHANCE = 0.5F;
   public static final float MAX_ENCHANTED_WEAPON_CHANCE = 0.25F;
   public static final float DEFAULT_EQUIPMENT_DROP_CHANCE = 0.085F;
   public static final float PRESERVE_ITEM_DROP_CHANCE_THRESHOLD = 1.0F;
   public static final int PRESERVE_ITEM_DROP_CHANCE = 2;
   public static final int UPDATE_GOAL_SELECTOR_EVERY_N_TICKS = 2;
   private static final double DEFAULT_ATTACK_REACH = Math.sqrt(2.0399999618530273) - 0.6000000238418579;
   protected static final ResourceLocation RANDOM_SPAWN_BONUS_ID = ResourceLocation.withDefaultNamespace("random_spawn_bonus");
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
   private ItemStack bodyArmorItem = ItemStack.EMPTY;
   protected float bodyArmorDropChance;
   private boolean canPickUpLoot;
   private boolean persistenceRequired;
   private final Map<PathType, Float> pathfindingMalus = Maps.newEnumMap(PathType.class);
   private Optional<ResourceKey<LootTable>> lootTable = Optional.empty();
   private long lootTableSeed;
   @Nullable
   private Leashable.LeashData leashData;
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
      this.bodyArmorDropChance = 0.085F;
      if (var2 != null && !var2.isClientSide) {
         this.registerGoals();
      }
   }

   protected void registerGoals() {
   }

   public static AttributeSupplier.Builder createMobAttributes() {
      return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new GroundPathNavigation(this, var1);
   }

   protected boolean shouldPassengersInheritMalus() {
      return false;
   }

   public float getPathfindingMalus(PathType var1) {
      Mob var2;
      label17: {
         if (this.getControlledVehicle() instanceof Mob var3 && var3.shouldPassengersInheritMalus()) {
            var2 = var3;
            break label17;
         }

         var2 = this;
      }

      Float var5 = var2.pathfindingMalus.get(var1);
      return var5 == null ? var1.getMalus() : var5;
   }

   public void setPathfindingMalus(PathType var1, float var2) {
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
      return this.getControlledVehicle() instanceof Mob var1 ? var1.getMoveControl() : this.moveControl;
   }

   public JumpControl getJumpControl() {
      return this.jumpControl;
   }

   public PathNavigation getNavigation() {
      return this.getControlledVehicle() instanceof Mob var1 ? var1.getNavigation() : this.navigation;
   }

   @Nullable
   @Override
   public LivingEntity getControllingPassenger() {
      Entity var1 = this.getFirstPassenger();
      if (!this.isNoAi() && var1 instanceof Mob var2 && var1.canControlVehicle()) {
         return var2;
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

   @Nullable
   protected final LivingEntity getTargetFromBrain() {
      return this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
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
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_MOB_FLAGS_ID, (byte)0);
   }

   public int getAmbientSoundInterval() {
      return 80;
   }

   public void playAmbientSound() {
      this.makeSound(this.getAmbientSound());
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
   protected int getBaseExperienceReward() {
      if (this.xpReward > 0) {
         int var1 = this.xpReward;

         for (int var2 = 0; var2 < this.armorItems.size(); var2++) {
            if (!this.armorItems.get(var2).isEmpty() && this.armorDropChances[var2] <= 1.0F) {
               var1 += 1 + this.random.nextInt(3);
            }
         }

         for (int var3 = 0; var3 < this.handItems.size(); var3++) {
            if (!this.handItems.get(var3).isEmpty() && this.handDropChances[var3] <= 1.0F) {
               var1 += 1 + this.random.nextInt(3);
            }
         }

         if (!this.bodyArmorItem.isEmpty() && this.bodyArmorDropChance <= 1.0F) {
            var1 += 1 + this.random.nextInt(3);
         }

         return var1;
      } else {
         return this.xpReward;
      }
   }

   public void spawnAnim() {
      if (this.level().isClientSide) {
         for (int var1 = 0; var1 < 20; var1++) {
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
      if (!this.level().isClientSide && this.tickCount % 5 == 0) {
         this.updateControlFlags();
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

      for (ItemStack var4 : this.armorItems) {
         if (!var4.isEmpty()) {
            var2.add(var4.save(this.registryAccess()));
         } else {
            var2.add(new CompoundTag());
         }
      }

      var1.put("ArmorItems", var2);
      ListTag var10 = new ListTag();

      for (float var7 : this.armorDropChances) {
         var10.add(FloatTag.valueOf(var7));
      }

      var1.put("ArmorDropChances", var10);
      ListTag var12 = new ListTag();

      for (ItemStack var15 : this.handItems) {
         if (!var15.isEmpty()) {
            var12.add(var15.save(this.registryAccess()));
         } else {
            var12.add(new CompoundTag());
         }
      }

      var1.put("HandItems", var12);
      ListTag var14 = new ListTag();

      for (float var9 : this.handDropChances) {
         var14.add(FloatTag.valueOf(var9));
      }

      var1.put("HandDropChances", var14);
      if (!this.bodyArmorItem.isEmpty()) {
         var1.put("body_armor_item", this.bodyArmorItem.save(this.registryAccess()));
         var1.putFloat("body_armor_drop_chance", this.bodyArmorDropChance);
      }

      this.writeLeashData(var1, this.leashData);
      var1.putBoolean("LeftHanded", this.isLeftHanded());
      if (this.lootTable.isPresent()) {
         var1.putString("DeathLootTable", this.lootTable.get().location().toString());
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
      if (var1.contains("CanPickUpLoot", 99)) {
         this.setCanPickUpLoot(var1.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = var1.getBoolean("PersistenceRequired");
      if (var1.contains("ArmorItems", 9)) {
         ListTag var2 = var1.getList("ArmorItems", 10);

         for (int var3 = 0; var3 < this.armorItems.size(); var3++) {
            CompoundTag var4 = var2.getCompound(var3);
            this.armorItems.set(var3, ItemStack.parseOptional(this.registryAccess(), var4));
         }
      }

      if (var1.contains("ArmorDropChances", 9)) {
         ListTag var5 = var1.getList("ArmorDropChances", 5);

         for (int var8 = 0; var8 < var5.size(); var8++) {
            this.armorDropChances[var8] = var5.getFloat(var8);
         }
      }

      if (var1.contains("HandItems", 9)) {
         ListTag var6 = var1.getList("HandItems", 10);

         for (int var9 = 0; var9 < this.handItems.size(); var9++) {
            CompoundTag var11 = var6.getCompound(var9);
            this.handItems.set(var9, ItemStack.parseOptional(this.registryAccess(), var11));
         }
      }

      if (var1.contains("HandDropChances", 9)) {
         ListTag var7 = var1.getList("HandDropChances", 5);

         for (int var10 = 0; var10 < var7.size(); var10++) {
            this.handDropChances[var10] = var7.getFloat(var10);
         }
      }

      if (var1.contains("body_armor_item", 10)) {
         this.bodyArmorItem = ItemStack.parse(this.registryAccess(), var1.getCompound("body_armor_item")).orElse(ItemStack.EMPTY);
         this.bodyArmorDropChance = var1.getFloat("body_armor_drop_chance");
      } else {
         this.bodyArmorItem = ItemStack.EMPTY;
      }

      this.leashData = this.readLeashData(var1);
      this.setLeftHanded(var1.getBoolean("LeftHanded"));
      if (var1.contains("DeathLootTable", 8)) {
         this.lootTable = Optional.of(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(var1.getString("DeathLootTable"))));
         this.lootTableSeed = var1.getLong("DeathLootTableSeed");
      }

      this.setNoAi(var1.getBoolean("NoAI"));
   }

   @Override
   protected void dropFromLootTable(DamageSource var1, boolean var2) {
      super.dropFromLootTable(var1, var2);
      this.lootTable = Optional.empty();
   }

   @Override
   public final Optional<ResourceKey<LootTable>> getLootTable() {
      return this.lootTable.isPresent() ? this.lootTable : super.getLootTable();
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

   public void stopInPlace() {
      this.getNavigation().stop();
      this.setXxa(0.0F);
      this.setYya(0.0F);
      this.setSpeed(0.0F);
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

         for (ItemEntity var4 : this.level()
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
      EquipmentSlot var2 = this.getEquipmentSlotForItem(var1);
      ItemStack var3 = this.getItemBySlot(var2);
      boolean var4 = this.canReplaceCurrentItem(var1, var3, var2);
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

         ItemStack var7 = var2.limit(var1);
         this.setItemSlotAndDropWhenKilled(var2, var7);
         return var7;
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
      switch (var1.getType()) {
         case HAND:
            this.handDropChances[var1.getIndex()] = 2.0F;
            break;
         case HUMANOID_ARMOR:
            this.armorDropChances[var1.getIndex()] = 2.0F;
            break;
         case ANIMAL_ARMOR:
            this.bodyArmorDropChance = 2.0F;
      }
   }

   protected boolean canReplaceCurrentItem(ItemStack var1, ItemStack var2, EquipmentSlot var3) {
      if (var2.isEmpty()) {
         return true;
      } else if (var1.getItem() instanceof SwordItem) {
         if (!(var2.getItem() instanceof SwordItem)) {
            return true;
         } else {
            double var13 = this.getApproximateAttributeWith(var1, Attributes.ATTACK_DAMAGE, var3);
            double var15 = this.getApproximateAttributeWith(var2, Attributes.ATTACK_DAMAGE, var3);
            return var13 != var15 ? var13 > var15 : this.canReplaceEqualItem(var1, var2);
         }
      } else if (var1.getItem() instanceof BowItem && var2.getItem() instanceof BowItem) {
         return this.canReplaceEqualItem(var1, var2);
      } else if (var1.getItem() instanceof CrossbowItem && var2.getItem() instanceof CrossbowItem) {
         return this.canReplaceEqualItem(var1, var2);
      } else if (var1.getItem() instanceof ArmorItem) {
         if (EnchantmentHelper.has(var2, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
            return false;
         } else if (!(var2.getItem() instanceof ArmorItem)) {
            return true;
         } else {
            double var12 = this.getApproximateAttributeWith(var1, Attributes.ARMOR, var3);
            double var14 = this.getApproximateAttributeWith(var2, Attributes.ARMOR, var3);
            double var8 = this.getApproximateAttributeWith(var1, Attributes.ARMOR_TOUGHNESS, var3);
            double var10 = this.getApproximateAttributeWith(var2, Attributes.ARMOR_TOUGHNESS, var3);
            if (var12 != var14) {
               return var12 > var14;
            } else {
               return var8 != var10 ? var8 > var10 : this.canReplaceEqualItem(var1, var2);
            }
         }
      } else {
         if (var1.getItem() instanceof DiggerItem) {
            if (var2.getItem() instanceof BlockItem) {
               return true;
            }

            if (var2.getItem() instanceof DiggerItem) {
               double var4 = this.getApproximateAttributeWith(var1, Attributes.ATTACK_DAMAGE, var3);
               double var6 = this.getApproximateAttributeWith(var2, Attributes.ATTACK_DAMAGE, var3);
               if (var4 != var6) {
                  return var4 > var6;
               }

               return this.canReplaceEqualItem(var1, var2);
            }
         }

         return false;
      }
   }

   private double getApproximateAttributeWith(ItemStack var1, Holder<Attribute> var2, EquipmentSlot var3) {
      ItemAttributeModifiers var4 = var1.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
      return var4.compute(this.getAttributeBaseValue(var2), var3);
   }

   public boolean canReplaceEqualItem(ItemStack var1, ItemStack var2) {
      return var1.getDamageValue() < var2.getDamageValue() ? true : hasAnyComponentExceptDamage(var1) && !hasAnyComponentExceptDamage(var2);
   }

   private static boolean hasAnyComponentExceptDamage(ItemStack var0) {
      DataComponentMap var1 = var0.getComponents();
      int var2 = var1.size();
      return var2 > 1 || var2 == 1 && !var1.has(DataComponents.DAMAGE);
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
      this.noActionTime++;
      ProfilerFiller var1 = this.level().getProfiler();
      var1.push("sensing");
      this.sensing.tick();
      var1.pop();
      int var2 = this.tickCount + this.getId();
      if (var2 % 2 != 0 && this.tickCount > 1) {
         var1.push("targetSelector");
         this.targetSelector.tickRunningGoals(false);
         var1.pop();
         var1.push("goalSelector");
         this.goalSelector.tickRunningGoals(false);
         var1.pop();
      } else {
         var1.push("targetSelector");
         this.targetSelector.tick();
         var1.pop();
         var1.push("goalSelector");
         this.goalSelector.tick();
         var1.pop();
      }

      var1.push("navigation");
      this.navigation.tick();
      var1.pop();
      var1.push("mob tick");
      this.customServerAiStep();
      var1.pop();
      var1.push("controls");
      var1.push("move");
      this.moveControl.tick();
      var1.popPush("look");
      this.lookControl.tick();
      var1.popPush("jump");
      this.jumpControl.tick();
      var1.pop();
      var1.pop();
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

   protected void clampHeadRotationToBody() {
      float var1 = (float)this.getMaxHeadYRot();
      float var2 = this.getYHeadRot();
      float var3 = Mth.wrapDegrees(this.yBodyRot - var2);
      float var4 = Mth.clamp(Mth.wrapDegrees(this.yBodyRot - var2), -var1, var1);
      float var5 = var2 + var3 - var4;
      this.setYHeadRot(var5);
   }

   public int getHeadRotSpeed() {
      return 10;
   }

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

   public static boolean checkMobSpawnRules(EntityType<? extends Mob> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      BlockPos var5 = var3.below();
      return EntitySpawnReason.isSpawner(var2) || var1.getBlockState(var5).isValidSpawn(var1, var5, var0);
   }

   public boolean checkSpawnRules(LevelAccessor var1, EntitySpawnReason var2) {
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
         return this.getComfortableFallDistance(0.0F);
      } else {
         int var1 = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         var1 -= (3 - this.level().getDifficulty().getId()) * 4;
         if (var1 < 0) {
            var1 = 0;
         }

         return this.getComfortableFallDistance((float)var1);
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

   public ItemStack getBodyArmorItem() {
      return this.bodyArmorItem;
   }

   @Override
   public boolean canUseSlot(EquipmentSlot var1) {
      return var1 != EquipmentSlot.BODY;
   }

   public boolean isWearingBodyArmor() {
      return !this.getItemBySlot(EquipmentSlot.BODY).isEmpty();
   }

   public void setBodyArmorItem(ItemStack var1) {
      this.setItemSlotAndDropWhenKilled(EquipmentSlot.BODY, var1);
   }

   @Override
   public Iterable<ItemStack> getArmorAndBodyArmorSlots() {
      return (Iterable<ItemStack>)(this.bodyArmorItem.isEmpty() ? this.armorItems : Iterables.concat(this.armorItems, List.of(this.bodyArmorItem)));
   }

   @Override
   public ItemStack getItemBySlot(EquipmentSlot var1) {
      return switch (var1.getType()) {
         case HAND -> (ItemStack)this.handItems.get(var1.getIndex());
         case HUMANOID_ARMOR -> (ItemStack)this.armorItems.get(var1.getIndex());
         case ANIMAL_ARMOR -> this.bodyArmorItem;
      };
   }

   @Override
   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
      this.verifyEquippedItem(var2);
      switch (var1.getType()) {
         case HAND:
            this.onEquipItem(var1, this.handItems.set(var1.getIndex(), var2), var2);
            break;
         case HUMANOID_ARMOR:
            this.onEquipItem(var1, this.armorItems.set(var1.getIndex(), var2), var2);
            break;
         case ANIMAL_ARMOR:
            ItemStack var3 = this.bodyArmorItem;
            this.bodyArmorItem = var2;
            this.onEquipItem(var1, var3, var2);
      }
   }

   @Override
   protected void dropCustomDeathLoot(ServerLevel var1, DamageSource var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);

      for (EquipmentSlot var5 : EquipmentSlot.VALUES) {
         ItemStack var6 = this.getItemBySlot(var5);
         float var7 = this.getEquipmentDropChance(var5);
         if (var7 != 0.0F) {
            boolean var8 = var7 > 1.0F;
            Entity var11 = var2.getEntity();
            if (var11 instanceof LivingEntity) {
               LivingEntity var9 = (LivingEntity)var11;
               if (this.level() instanceof ServerLevel var10) {
                  var7 = EnchantmentHelper.processEquipmentDropChance(var10, var9, var2, var7);
               }
            }

            if (!var6.isEmpty()
               && !EnchantmentHelper.has(var6, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)
               && (var3 || var8)
               && this.random.nextFloat() < var7) {
               if (!var8 && var6.isDamageableItem()) {
                  var6.setDamageValue(var6.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(var6.getMaxDamage() - 3, 1))));
               }

               this.spawnAtLocation(var6);
               this.setItemSlot(var5, ItemStack.EMPTY);
            }
         }
      }
   }

   protected float getEquipmentDropChance(EquipmentSlot var1) {
      return switch (var1.getType()) {
         case HAND -> this.handDropChances[var1.getIndex()];
         case HUMANOID_ARMOR -> this.armorDropChances[var1.getIndex()];
         case ANIMAL_ARMOR -> this.bodyArmorDropChance;
      };
   }

   public void dropPreservedEquipment() {
      this.dropPreservedEquipment(var0 -> true);
   }

   public Set<EquipmentSlot> dropPreservedEquipment(Predicate<ItemStack> var1) {
      HashSet var2 = new HashSet();

      for (EquipmentSlot var4 : EquipmentSlot.VALUES) {
         ItemStack var5 = this.getItemBySlot(var4);
         if (!var5.isEmpty()) {
            if (!var1.test(var5)) {
               var2.add(var4);
            } else {
               double var6 = (double)this.getEquipmentDropChance(var4);
               if (var6 > 1.0) {
                  this.setItemSlot(var4, ItemStack.EMPTY);
                  this.spawnAtLocation(var5);
               }
            }
         }
      }

      return var2;
   }

   private LootParams createEquipmentParams(ServerLevel var1) {
      return new LootParams.Builder(var1)
         .withParameter(LootContextParams.ORIGIN, this.position())
         .withParameter(LootContextParams.THIS_ENTITY, this)
         .create(LootContextParamSets.EQUIPMENT);
   }

   public void equip(EquipmentTable var1) {
      this.equip(var1.lootTable(), var1.slotDropChances());
   }

   public void equip(ResourceKey<LootTable> var1, Map<EquipmentSlot, Float> var2) {
      if (this.level() instanceof ServerLevel var3) {
         this.equip(var1, this.createEquipmentParams(var3), var2);
      }
   }

   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      if (var1.nextFloat() < 0.15F * var2.getSpecialMultiplier()) {
         int var3 = var1.nextInt(2);
         float var4 = this.level().getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
         if (var1.nextFloat() < 0.095F) {
            var3++;
         }

         if (var1.nextFloat() < 0.095F) {
            var3++;
         }

         if (var1.nextFloat() < 0.095F) {
            var3++;
         }

         boolean var5 = true;

         for (EquipmentSlot var7 : EQUIPMENT_POPULATION_ORDER) {
            ItemStack var8 = this.getItemBySlot(var7);
            if (!var5 && var1.nextFloat() < var4) {
               break;
            }

            var5 = false;
            if (var8.isEmpty()) {
               Item var9 = getEquipmentForSlot(var7, var3);
               if (var9 != null) {
                  this.setItemSlot(var7, new ItemStack(var9));
               }
            }
         }
      }
   }

   @Nullable
   public static Item getEquipmentForSlot(EquipmentSlot var0, int var1) {
      switch (var0) {
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

   protected void populateDefaultEquipmentEnchantments(ServerLevelAccessor var1, RandomSource var2, DifficultyInstance var3) {
      this.enchantSpawnedWeapon(var1, var2, var3);

      for (EquipmentSlot var5 : EquipmentSlot.VALUES) {
         if (var5.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
            this.enchantSpawnedArmor(var1, var2, var5, var3);
         }
      }
   }

   protected void enchantSpawnedWeapon(ServerLevelAccessor var1, RandomSource var2, DifficultyInstance var3) {
      this.enchantSpawnedEquipment(var1, EquipmentSlot.MAINHAND, var2, 0.25F, var3);
   }

   protected void enchantSpawnedArmor(ServerLevelAccessor var1, RandomSource var2, EquipmentSlot var3, DifficultyInstance var4) {
      this.enchantSpawnedEquipment(var1, var3, var2, 0.5F, var4);
   }

   private void enchantSpawnedEquipment(ServerLevelAccessor var1, EquipmentSlot var2, RandomSource var3, float var4, DifficultyInstance var5) {
      ItemStack var6 = this.getItemBySlot(var2);
      if (!var6.isEmpty() && var3.nextFloat() < var4 * var5.getSpecialMultiplier()) {
         EnchantmentHelper.enchantItemFromProvider(var6, var1.registryAccess(), VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT, var5, var3);
         this.setItemSlot(var2, var6);
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      AttributeInstance var6 = Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE));
      if (!var6.hasModifier(RANDOM_SPAWN_BONUS_ID)) {
         var6.addPermanentModifier(
            new AttributeModifier(RANDOM_SPAWN_BONUS_ID, var5.triangle(0.0, 0.11485000000000001), AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
         );
      }

      this.setLeftHanded(var5.nextFloat() < 0.05F);
      return var4;
   }

   public void setPersistenceRequired() {
      this.persistenceRequired = true;
   }

   @Override
   public void setDropChance(EquipmentSlot var1, float var2) {
      switch (var1.getType()) {
         case HAND:
            this.handDropChances[var1.getIndex()] = var2;
            break;
         case HUMANOID_ARMOR:
            this.armorDropChances[var1.getIndex()] = var2;
            break;
         case ANIMAL_ARMOR:
            this.bodyArmorDropChance = var2;
      }
   }

   @Override
   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean var1) {
      this.canPickUpLoot = var1;
   }

   @Override
   protected boolean canDispenserEquipIntoSlot(EquipmentSlot var1) {
      return this.canPickUpLoot();
   }

   public boolean isPersistenceRequired() {
      return this.persistenceRequired;
   }

   @Override
   public final InteractionResult interact(Player var1, InteractionHand var2) {
      if (!this.isAlive()) {
         return InteractionResult.PASS;
      } else {
         InteractionResult var3 = this.checkAndHandleImportantInteractions(var1, var2);
         if (var3.consumesAction()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT, var1);
            return var3;
         } else {
            InteractionResult var4 = super.interact(var1, var2);
            if (var4 != InteractionResult.PASS) {
               return var4;
            } else {
               var3 = this.mobInteract(var1, var2);
               if (var3.consumesAction()) {
                  this.gameEvent(GameEvent.ENTITY_INTERACT, var1);
                  return var3;
               } else {
                  return InteractionResult.PASS;
               }
            }
         }
      }
   }

   private InteractionResult checkAndHandleImportantInteractions(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.NAME_TAG)) {
         InteractionResult var4 = var3.interactLivingEntity(var1, this, var2);
         if (var4.consumesAction()) {
            return var4;
         }
      }

      if (var3.getItem() instanceof SpawnEggItem) {
         if (this.level() instanceof ServerLevel) {
            SpawnEggItem var6 = (SpawnEggItem)var3.getItem();
            Optional var5 = var6.spawnOffspringFromSpawnEgg(
               var1, this, (EntityType<? extends Mob>)this.getType(), (ServerLevel)this.level(), this.position(), var3
            );
            var5.ifPresent(var2x -> this.onOffspringSpawnedFromEgg(var1, var2x));
            if (var5.isEmpty()) {
               return InteractionResult.PASS;
            }
         }

         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.PASS;
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
      return this.restrictRadius == -1.0F ? true : this.restrictCenter.distSqr(var1) < (double)(this.restrictRadius * this.restrictRadius);
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
   public <T extends Mob> T convertTo(EntityType<T> var1, ConversionParams var2, EntitySpawnReason var3, ConversionParams.AfterConversion<T> var4) {
      if (this.isRemoved()) {
         return null;
      } else {
         Mob var5 = (Mob)var1.create(this.level(), var3);
         if (var5 == null) {
            return null;
         } else {
            var2.type().convert(this, var5, var2);
            var4.finalizeConversion(var5);
            if (this.level() instanceof ServerLevel var6) {
               var6.addFreshEntityWithPassengers(var5);
            }

            if (var2.type().shouldDiscardAfterConversion()) {
               this.discard();
            }

            return (T)var5;
         }
      }
   }

   @Nullable
   public <T extends Mob> T convertTo(EntityType<T> var1, ConversionParams var2, ConversionParams.AfterConversion<T> var3) {
      return this.convertTo(var1, var2, EntitySpawnReason.CONVERSION, var3);
   }

   @Nullable
   @Override
   public Leashable.LeashData getLeashData() {
      return this.leashData;
   }

   @Override
   public void setLeashData(@Nullable Leashable.LeashData var1) {
      this.leashData = var1;
   }

   @Override
   public void dropLeash(boolean var1, boolean var2) {
      Leashable.super.dropLeash(var1, var2);
      if (this.getLeashData() == null) {
         this.clearRestriction();
      }
   }

   @Override
   public void leashTooFarBehaviour() {
      Leashable.super.leashTooFarBehaviour();
      this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
   }

   @Override
   public boolean canBeLeashed() {
      return !(this instanceof Enemy);
   }

   @Override
   public boolean startRiding(Entity var1, boolean var2) {
      boolean var3 = super.startRiding(var1, var2);
      if (var3 && this.isLeashed()) {
         this.dropLeash(true, true);
      }

      return var3;
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

   public boolean isWithinMeleeAttackRange(LivingEntity var1) {
      return this.getAttackBoundingBox().intersects(var1.getHitbox());
   }

   protected AABB getAttackBoundingBox() {
      Entity var2 = this.getVehicle();
      AABB var1;
      if (var2 != null) {
         AABB var3 = var2.getBoundingBox();
         AABB var4 = this.getBoundingBox();
         var1 = new AABB(
            Math.min(var4.minX, var3.minX),
            var4.minY,
            Math.min(var4.minZ, var3.minZ),
            Math.max(var4.maxX, var3.maxX),
            var4.maxY,
            Math.max(var4.maxZ, var3.maxZ)
         );
      } else {
         var1 = this.getBoundingBox();
      }

      return var1.inflate(DEFAULT_ATTACK_REACH, 0.0, DEFAULT_ATTACK_REACH);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      float var2 = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      ItemStack var3 = this.getWeaponItem();
      DamageSource var4 = this.damageSources().mobAttack(this);
      if (this.level() instanceof ServerLevel var5) {
         var2 = EnchantmentHelper.modifyDamage(var5, var3, var1, var4, var2);
      }

      boolean var9 = var1.hurt(var4, var2);
      if (var9) {
         float var10 = this.getKnockback(var1, var4);
         if (var10 > 0.0F && var1 instanceof LivingEntity var7) {
            var7.knockback((double)(var10 * 0.5F), (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         }

         if (this.level() instanceof ServerLevel var11) {
            if (var1 instanceof LivingEntity var12) {
               var3.hurtEnemy(var12, this);
            }

            EnchantmentHelper.doPostAttackEffects(var11, var1, var4);
         }

         this.setLastHurtMob(var1);
         this.playAttackSound();
      }

      return var9;
   }

   protected void playAttackSound() {
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

   @VisibleForTesting
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

   @Override
   protected void onAttributeUpdated(Holder<Attribute> var1) {
      super.onAttributeUpdated(var1);
      if (var1.is(Attributes.FOLLOW_RANGE) || var1.is(Attributes.TEMPT_RANGE)) {
         this.getNavigation().updatePathfinderMaxVisitedNodes();
      }
   }

   @VisibleForTesting
   public float[] getHandDropChances() {
      return this.handDropChances;
   }

   @VisibleForTesting
   public float[] getArmorDropChances() {
      return this.armorDropChances;
   }

   public void setLootTable(Optional<ResourceKey<LootTable>> var1) {
      this.lootTable = var1;
   }

   public void setLootTableSeed(long var1) {
      this.lootTableSeed = var1;
   }
}
