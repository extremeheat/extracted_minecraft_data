package net.minecraft.world.entity.livingblock;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.livingblock.behavior.LivingBlockBehavior;
import net.minecraft.world.entity.livingblock.behavior.LivingBlockBehaviorEntry;
import net.minecraft.world.entity.livingblock.behavior.LivingBlockContainerBehavior;
import net.minecraft.world.entity.livingblock.cognition.Action;
import net.minecraft.world.entity.livingblock.cognition.HopesAndDreams;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.entity.livingblock.hurt.OnHurt;
import net.minecraft.world.entity.livingblock.interact.ConsumeItem;
import net.minecraft.world.entity.livingblock.interact.OnInteract;
import net.minecraft.world.entity.livingblock.movement.BouncingMovement;
import net.minecraft.world.entity.livingblock.movement.MovementData;
import net.minecraft.world.entity.livingblock.movement.MovementStrategy;
import net.minecraft.world.entity.livingblock.movement.RollingMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ActionItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class LivingBlock extends Entity implements Leashable, Targetable {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK_ID;
   private static final EntityDataAccessor<Float> DATA_HEALTH_ID;
   private static final EntityDataAccessor<Float> DATA_MAX_HEALTH_ID;
   private static final EntityDataAccessor<Integer> DATA_GROUP;
   private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_COMMANDER;
   private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_OWNER;
   private static final EntityDataAccessor<Boolean> DATA_PLAYER_INTERACTED;
   private static final EntityDataAccessor<Boolean> DATA_PINNED;
   private static final EntityDataAccessor<Boolean> DATA_SELECTED;
   protected static final EntityDataAccessor<MovementData> MOVEMENT_DATA;
   protected static final EntityDataAccessor<Target> MOVEMENT_TARGET;
   private static final EntityDataAccessor<Direction> DATA_CLIMBING_DIRECTION;
   private static final String TAG_ITEM = "item";
   private static final String TAG_ATTACK_TARGET = "attack_target";
   private static final String TAG_OWNER = "owner";
   private static final String TAG_COMMANDER = "commander";
   private static final String TAG_HEALTH = "health";
   private static final String TAG_MAX_HEALTH = "max_health";
   private static final String TAG_GROUP = "group";
   private static final String TAG_PINNED = "pinned";
   private static final String TAG_SELECTED = "selected";
   private static final String TAG_DESPAWN = "despawn_ticks";
   private static final float DEFAULT_MAX_HEALTH = 10.0F;
   private static final float HEALTH_PER_EXPLOSION_RESISTANCE = 3.0F;
   private static final float MAX_MAX_HEALTH = 200.0F;
   private static final float MIN_MAX_HEALTH = 2.0F;
   private static final int MAX_HEALTH_REGEN_COOLDOWN = 100;
   private static final int DEFAULT_DESPAWN_TICKS = 6000;
   private static final int MINIMUM_DISTANCE_TO_TICK_DESPAWN = 50;
   protected static final int HURT_DURATION = 10;
   public static final int DEATH_DURATION = 20;
   private static final double BLOCK_SNAP_THRESHOLD = 0.125;
   private static final double COLLISION_NUDGE_OFFSET = 0.05;
   private static final double MOVEMENT_EPSILON = 0.001;
   private static final int SPAWN_MAX_Y_OFFSET_UP = 5;
   private static final int SPAWN_MAX_Y_OFFSET_DOWN = 3;
   private static final int SPAWN_MAX_XZ_OFFSET = 3;
   private static final float DEFAULT_MAX_UP_STEP = 0.6F;
   private static final List<Block> ballons;
   public static final Action<Targetable> BE_MAD_AT;
   public static final Action<Target> MOVE_TOWARDS;
   private final Intent<Targetable> beMadAtIntent = Intent.<Targetable>consuming((t) -> this.attackTarget = EntityReference.of(t), (Object)null);
   private final Intent<Target> moveTowardsIntent;
   private static final int DAMAGE_SOURCE_TIMEOUT = 40;
   private BlockPos lastBlockPos;
   private final InterpolationHandler interpolationHandler;
   private final Quaternionf lastRotation;
   private final Quaternionf rotation;
   private float rollDeltaX;
   private float rollDeltaZ;
   private int rollSoundTime;
   private static final int ROLL_SOUND_MIN_TIME = 4;
   private static final double ROLL_SOUND_MIN_ANGLE = 10.0;
   private static final float ROLL_ROTATION_DELTA_EPSILON = 1.0E-5F;
   public Vec3 boundingBoxOffset;
   private @Nullable LivingBlockType type;
   private List<LivingBlockBehaviorEntry<?>> behaviors;
   public final HopesAndDreams hopesAndDreams;
   private int currentActivities;
   private Leashable.@Nullable LeashData leashData;
   protected @Nullable Player attackedBy;
   public int lastTargetedTick;
   public int lastAttackedTick;
   private boolean takeDamageOnAttack;
   public float previousHealthAmount;
   private CollisionInteraction collision;
   private OnInteract onInteract;
   private OnHurt onHurt;
   private MovementStrategy<?> movement;
   private float maxUpStep;
   private Vec3 pogoScaleTarget;
   private Vec3 lastPogoScaleTarget;
   private Vec3 currentPogoScale;
   private int pogoScaleTicksRemaining;
   private int pogoScaleTicks;
   private @Nullable EntityReference<Targetable> attackTarget;
   private @Nullable DamageSource lastDamageSource;
   private long lastDamageStamp;
   private int healthRegenCooldown;
   public int hurtTime;
   public int deathTime;
   protected boolean dead;
   private boolean fireImmune;
   private int ticksBeforeDespawn;
   public @Nullable LivingBlock livingBlockBeingEnchanted;
   private BlockState blockStateCache;

   public LivingBlock(final EntityType<?> type, final Level level) {
      super(type, level);
      this.moveTowardsIntent = Intent.<Target>consuming((t) -> this.entityData.set(MOVEMENT_TARGET, t), Target.NONE);
      this.lastBlockPos = BlockPos.ZERO;
      this.lastRotation = new Quaternionf();
      this.rotation = new Quaternionf();
      this.boundingBoxOffset = Vec3.ZERO;
      this.behaviors = List.of();
      this.hopesAndDreams = new HopesAndDreams();
      this.attackedBy = null;
      this.lastTargetedTick = 0;
      this.lastAttackedTick = 0;
      this.collision = CollisionInteraction.ENTITY;
      this.onInteract = new ConsumeItem();
      this.onHurt = OnHurt.DO_NOTHING;
      this.movement = new RollingMovement();
      this.maxUpStep = 0.6F;
      this.pogoScaleTarget = new Vec3(1.0, 1.0, 1.0);
      this.lastPogoScaleTarget = new Vec3(1.0, 1.0, 1.0);
      this.currentPogoScale = new Vec3(1.0, 1.0, 1.0);
      this.pogoScaleTicksRemaining = 0;
      this.pogoScaleTicks = 0;
      this.attackTarget = null;
      this.ticksBeforeDespawn = 6000;
      this.blockStateCache = Blocks.AIR.defaultBlockState();
      this.interpolationHandler = new InterpolationHandler(this, type.updateInterval());
   }

   public static @Nullable LivingBlock create(final Level level, final BlockState blockState) {
      LivingBlockType type = LivingBlockTypes.REGISTRY.get(blockState);
      return type.create(level, blockState);
   }

   public static @Nullable LivingBlock create(final Level level, final ItemStack itemStack) {
      LivingBlockType type = LivingBlockTypes.REGISTRY.get(itemStack);
      return type.create(level, itemStack);
   }

   public static @Nullable LivingBlock createAt(final Level level, final BlockPos pos, final BlockState blockState) {
      LivingBlock livingBlock = create(level, blockState);
      finalizeSpawn(level, pos, livingBlock);
      return livingBlock;
   }

   public static @Nullable LivingBlock createAt(final Level level, final BlockPos pos, final ItemStack itemStack) {
      LivingBlock livingBlock = create(level, itemStack);
      if (livingBlock != null) {
         addEntityTagsFromItemTags(livingBlock, itemStack);
      }

      finalizeSpawn(level, pos, livingBlock);
      return livingBlock;
   }

   private static void finalizeSpawn(final Level level, final BlockPos pos, final @Nullable LivingBlock livingBlock) {
      if (livingBlock != null) {
         livingBlock.snapTo(Vec3.atBottomCenterOf(pos));
         level.addFreshEntity(livingBlock);
      }

   }

   public static Collection<LivingBlock> createStack(final Level level, final BlockPos pos, final @Nullable Entity creator, final ItemStack stack) {
      return createStack(level, pos, creator, stack, stack.count());
   }

   private static Collection<LivingBlock> createStack(final Level level, final BlockPos pos, final @Nullable Entity creator, final ItemStack stack, final int count) {
      List<LivingBlock> createdBlocks = new ArrayList();
      BlockPos resultPos = pos;
      if (creator instanceof ServerPlayer player) {
         CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), stack);
      }

      Set<BlockPos> used = new ObjectArraySet();

      for(int i = 0; i < count; ++i) {
         BlockPos spawnPos = findFreeSpawnPos(level, resultPos, used);
         used.add(spawnPos);
         LivingBlock createdBlock = createAt(level, spawnPos, stack.copyWithCount(1));
         if (createdBlock != null) {
            createdBlocks.add(createdBlock);
            if (creator instanceof ServerPlayer) {
               ServerPlayer player = (ServerPlayer)creator;
               CriteriaTriggers.SUMMONED_ENTITY.trigger(player, createdBlock);
            }
         }

         resultPos = spawnPos.relative(Direction.fromYRot((double)(level.getRandom().nextFloat() * 360.0F)));
      }

      return createdBlocks;
   }

   private static BlockPos findFreeSpawnPos(final Level level, final BlockPos origin, final Set<BlockPos> used) {
      BlockPos result = findFreeSpawnInColumn(level, origin, used);
      if (result != null) {
         return result;
      } else {
         List<Direction> directions = Direction.Plane.HORIZONTAL.shuffledCopy(level.getRandom());

         for(int distance = 1; distance <= 3; ++distance) {
            for(Direction direction : directions) {
               BlockPos candidateResult = findFreeSpawnInColumn(level, origin.relative(direction, distance), used);
               if (candidateResult != null) {
                  return candidateResult;
               }
            }
         }

         return origin;
      }
   }

   private static @Nullable BlockPos findFreeSpawnInColumn(final Level level, final BlockPos base, final Set<BlockPos> used) {
      BlockPos.MutableBlockPos pos = base.mutable();

      for(int y = 0; y < 5; ++y) {
         if (!used.contains(pos) && level.noCollision(AABB.encapsulatingFullBlocks(pos, pos).deflate(1.0E-7))) {
            return pos.immutable();
         }

         pos.move(Direction.UP);
      }

      pos.set(base).move(Direction.DOWN);

      for(int y = 0; y < 3; ++y) {
         if (!used.contains(pos) && level.noCollision(AABB.encapsulatingFullBlocks(pos, pos).deflate(1.0E-7))) {
            return pos.immutable();
         }

         pos.move(Direction.DOWN);
      }

      return null;
   }

   protected void positionRider(final Entity passenger, final Entity.MoveFunction moveFunction) {
      if (this.isVehicle() && this.getPassengers().contains(passenger)) {
         double seatHeight = 0.5625;
         Vec3 bedCenter = this.position();
         moveFunction.accept(passenger, bedCenter.x, bedCenter.y + 0.5625, bedCenter.z);
      }
   }

   protected boolean canAddPassenger(final Entity passenger) {
      return !this.isVehicle() && passenger instanceof Player;
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      entityData.define(DATA_ITEM_STACK_ID, ItemStack.EMPTY);
      entityData.define(DATA_HEALTH_ID, 10.0F);
      entityData.define(DATA_MAX_HEALTH_ID, 10.0F);
      entityData.define(DATA_GROUP, LivingBlockGroup.NONE.id());
      entityData.define(DATA_OWNER, Optional.empty());
      entityData.define(DATA_PLAYER_INTERACTED, false);
      entityData.define(DATA_COMMANDER, Optional.empty());
      entityData.define(DATA_PINNED, false);
      entityData.define(DATA_SELECTED, false);
      entityData.define(MOVEMENT_TARGET, Target.NONE);
      entityData.define(MOVEMENT_DATA, MovementData.EMPTY);
      entityData.define(DATA_CLIMBING_DIRECTION, Direction.DOWN);
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      super.onSyncedDataUpdated(accessor);
      if (accessor.equals(DATA_ITEM_STACK_ID)) {
         this.onStateChanged(this.getItemStack());
      }

   }

   public boolean isBlock(final Block block) {
      return this.getBlockState().is(block);
   }

   public boolean isBlock(final TagKey<Block> tag) {
      return this.getBlockState().is(tag);
   }

   public boolean isItem(final Item item) {
      return this.getItemStack().is(item);
   }

   public boolean isItem(final TagKey<Item> tag) {
      return this.getItemStack().is(tag);
   }

   public boolean isPinned() {
      return (Boolean)this.entityData.get(DATA_PINNED);
   }

   public void setPinned(final boolean pinned) {
      this.entityData.set(DATA_PINNED, pinned);
   }

   public boolean isSelected() {
      return (Boolean)this.entityData.get(DATA_SELECTED);
   }

   public void setSelected(final boolean selected) {
      this.entityData.set(DATA_SELECTED, selected);
   }

   public Direction getClimbingDirection() {
      return (Direction)this.entityData.get(DATA_CLIMBING_DIRECTION);
   }

   public void setClimbingDirection(final Direction direction) {
      if (!this.level().isClientSide()) {
         this.entityData.set(DATA_CLIMBING_DIRECTION, direction);
      }

   }

   public void resetClimbingDirection() {
      this.setClimbingDirection(Direction.DOWN);
   }

   public boolean isClimbing() {
      return this.getClimbingDirection().getAxis() != Direction.Axis.Y;
   }

   public BlockState getBlockState() {
      return this.blockStateCache == null ? Blocks.AIR.defaultBlockState() : this.blockStateCache;
   }

   public void setBlockState(final BlockState blockState) {
      ItemStack itemStack = new ItemStack(blockState.getBlock());
      itemStack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.from(blockState));
      this.setContents(itemStack, blockState);
   }

   public ItemStack getItemStack() {
      return (ItemStack)this.entityData.get(DATA_ITEM_STACK_ID);
   }

   public void setItemStack(final ItemStack itemStack) {
      this.setItemStackInternal(itemStack);
      this.onStateChanged(itemStack);
   }

   private void setContents(final ItemStack itemStackForm, final BlockState blockStateForm) {
      this.setItemStackInternal(itemStackForm);
      this.blockStateCache = blockStateForm;
   }

   private void setItemStackInternal(final ItemStack itemStack) {
      this.entityData.set(DATA_ITEM_STACK_ID, itemStack);
      Item item = itemStack.getItem();
      int durability = itemStack.getMaxDamage();
      if (!itemStack.is(ItemTags.COPPER_CHESTS) && !itemStack.is(Items.CHEST) && !itemStack.is(Items.ENDER_CHEST) && !itemStack.is(Items.BARREL) && !itemStack.is(ItemTags.SHULKER_BOXES)) {
         if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            if (!itemStack.is(ItemTags.BLOCK_PLACERS)) {
               float oldMaxHealth = this.getMaxHealth();
               float newMaxHealth = Mth.clamp(blockItem.getBlock().getExplosionResistance() * 3.0F, 2.0F, 200.0F);
               float change = newMaxHealth - oldMaxHealth;
               this.entityData.set(DATA_MAX_HEALTH_ID, newMaxHealth);
               this.setHealth(Mth.clamp(this.getHealth() + change, 1.0F, newMaxHealth));
               this.takeDamageOnAttack = false;
               return;
            }
         }

         if (itemStack.is(ItemTags.BUNDLES)) {
            float oldMaxHealth = this.getMaxHealth();
            float newMaxHealth = Mth.clamp((float)durability, 2.0F, 200.0F);
            float change = newMaxHealth - oldMaxHealth;
            this.entityData.set(DATA_MAX_HEALTH_ID, newMaxHealth);
            this.setHealth(Mth.clamp(this.getHealth() + change, 1.0F, newMaxHealth));
            this.takeDamageOnAttack = false;
         } else if (durability > 0) {
            float oldMaxHealth = this.getMaxHealth();
            float newMaxHealth = Mth.clamp((float)durability / 10.0F, 2.0F, 200.0F);
            float change = newMaxHealth - oldMaxHealth;
            this.entityData.set(DATA_MAX_HEALTH_ID, newMaxHealth);
            this.setHealth(Mth.clamp(this.getHealth() + change, 1.0F, newMaxHealth));
            this.takeDamageOnAttack = true;
         }
      } else {
         float oldMaxHealth = this.getMaxHealth();
         float newMaxHealth = 200.0F;
         float change = 200.0F - oldMaxHealth;
         this.entityData.set(DATA_MAX_HEALTH_ID, 200.0F);
         this.setHealth(Mth.clamp(this.getHealth() + change, 1.0F, 200.0F));
         this.takeDamageOnAttack = false;
      }

   }

   public LivingBlockGroup getGroup() {
      return (LivingBlockGroup)LivingBlockGroup.BY_ID.apply((Integer)this.entityData.get(DATA_GROUP));
   }

   public void setGroup(final LivingBlockGroup group) {
      this.entityData.set(DATA_GROUP, group.id());
   }

   private void onStateChanged(final ItemStack itemStack) {
      LivingBlockType type = LivingBlockTypes.REGISTRY.get(itemStack.getItem());
      if (this.type != type) {
         this.type = type;
         this.behaviors = type.createBehaviorsFor(this);
         this.collision = type.getCollision();
         this.onInteract = type.getOnInteract();
         this.onHurt = type.getOnHurt();
         this.movement = type.getMovement();
         this.setMovementData(this.movement.initData());
      }

      label18: {
         Item var4 = itemStack.getItem();
         if (var4 instanceof BlockItem blockItem) {
            if (!itemStack.is(ItemTags.BLOCK_PLACERS)) {
               this.blockStateCache = blockItem.getBlock().defaultBlockState();
               BlockItemStateProperties stateProperties = (BlockItemStateProperties)itemStack.get(DataComponents.BLOCK_STATE);
               if (stateProperties != null) {
                  this.blockStateCache = stateProperties.apply(this.blockStateCache);
               }
               break label18;
            }
         }

         this.blockStateCache = Blocks.AIR.defaultBlockState();
      }

      this.setBoundingBox(this.makeBoundingBox());
   }

   public MovementStrategy<?> getMovement() {
      return this.movement;
   }

   protected Vec3 adjustStepUpMovement(final Vec3 movement) {
      return this.movement.adjustStepUpMovement(this, movement);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      Optional<ItemStack> maybeItem = input.<ItemStack>read("item", ItemStack.CODEC);
      this.setItemStack((ItemStack)maybeItem.orElse(ItemStack.EMPTY));
      this.readLeashData(input);
      this.attackTarget = EntityReference.<Targetable>read(input, "attack_target");
      this.entityData.set(DATA_OWNER, Optional.ofNullable(EntityReference.read(input, "owner")));
      this.entityData.set(DATA_COMMANDER, Optional.ofNullable(EntityReference.read(input, "commander")));
      this.entityData.set(DATA_MAX_HEALTH_ID, input.getFloatOr("max_health", 10.0F));
      this.setHealth(input.getFloatOr("health", this.getMaxHealth()));
      this.setGroup((LivingBlockGroup)input.read("group", LivingBlockGroup.CODEC).orElse(LivingBlockGroup.NONE));
      this.setPinned(input.getBooleanOr("pinned", false));
      this.setSelected(input.getBooleanOr("selected", false));
      this.ticksBeforeDespawn = input.getIntOr("despawn_ticks", 6000);

      for(LivingBlockBehaviorEntry<?> behavior : this.behaviors) {
         behavior.loadData(input);
      }

   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      ItemStack itemStack = this.getItemStack();
      if (!itemStack.isEmpty()) {
         output.store("item", ItemStack.CODEC, itemStack);
      }

      this.writeLeashData(output, this.leashData);
      EntityReference.store(this.attackTarget, output, "attack_target");
      EntityReference.store((EntityReference)((Optional)this.entityData.get(DATA_OWNER)).orElse((Object)null), output, "owner");
      EntityReference.store((EntityReference)((Optional)this.entityData.get(DATA_COMMANDER)).orElse((Object)null), output, "commander");
      output.putFloat("health", this.getHealth());
      output.putFloat("max_health", this.getMaxHealth());
      output.store("group", LivingBlockGroup.CODEC, this.getGroup());
      output.putBoolean("pinned", this.isPinned());
      output.putBoolean("selected", this.isSelected());
      output.putInt("despawn_ticks", this.ticksBeforeDespawn);

      for(LivingBlockBehaviorEntry<?> behavior : this.behaviors) {
         behavior.save(output);
      }

   }

   public boolean isPickable() {
      return true;
   }

   protected void playStepSound(final BlockPos pos, final BlockState movingOn) {
      BlockState blockState = this.getBlockState();
      Block var5 = blockState.getBlock();
      if (var5 instanceof NoteBlock noteBlock) {
         this.setBlockState(noteBlock.tuneRolling(noteBlock.setInstrumentRolling(this.level(), pos, blockState)));
         blockState.triggerEvent(this.level(), pos, 0, 0);
      } else if (!blockState.isAir()) {
         SoundType soundType = blockState.getSoundType();
         this.playSound(soundType.getBreakSound(), soundType.getVolume() * 0.15F, soundType.getPitch());
      } else {
         super.playStepSound(pos, movingOn);
      }

   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, float damage) {
      if (this.isInvulnerableToBase(source)) {
         return false;
      } else if (this.isDeadOrDying()) {
         return false;
      } else {
         if (damage < 0.0F) {
            damage = 0.0F;
         }

         if (damage > 0.0F && source.typeHolder().is(DamageTypeTags.IS_EXPLOSION) && this.getItemStack().has(DataComponents.CONTAINER)) {
            damage *= 0.1F;
         }

         float healthBefore = this.getHealth();
         this.previousHealthAmount = healthBefore;
         this.setHealth(healthBefore - damage);
         this.hurtTime = 10;
         this.healthRegenCooldown = 100;
         level.broadcastDamageEvent(this, source);
         boolean fatalDamage = this.isDeadOrDying() && !this.dead;
         this.onHurt.apply(this, level, source, damage, fatalDamage);
         if (fatalDamage) {
            this.die(source);
         }

         return damage > 0.0F;
      }
   }

   public void knockback(final double power, double xd, double zd) {
      if (!(power <= 0.0)) {
         this.needsSync = true;

         Vec3 deltaMovement;
         for(deltaMovement = this.getDeltaMovement(); xd * xd + zd * zd < 9.999999747378752E-6; zd = (this.random.nextDouble() - this.random.nextDouble()) * 0.01) {
            xd = (this.random.nextDouble() - this.random.nextDouble()) * 0.01;
         }

         Vec3 deltaVector = (new Vec3(xd, 0.0, zd)).normalize().scale(power);
         this.setDeltaMovement(deltaMovement.x / 2.0 - deltaVector.x, this.onGround() ? Math.min(0.4, deltaMovement.y / 2.0 + power) : deltaMovement.y, deltaMovement.z / 2.0 - deltaVector.z);
      }
   }

   public void onRemoval(final Entity.RemovalReason reason) {
      super.onRemoval(reason);
      if (reason != Entity.RemovalReason.UNLOADED_TO_CHUNK) {
         this.level().getChunkAt(this.lastBlockPos).removeLivingBlock(this, this.lastBlockPos);
      }

      for(LivingBlockBehaviorEntry<?> behavior : this.behaviors) {
         Level var5 = this.level();
         if (var5 instanceof ServerLevel level) {
            behavior.onRemoval(level);
         }
      }

   }

   protected void die(final DamageSource source) {
      this.dead = true;
      this.tryCraft(source.getEntity(), this.getGroup());
      this.getItemStack().getItem().onDestroyed(this);
      BlockState state = this.getBlockState();
      if (!state.isAir()) {
         this.playSound(state.getSoundType().getBreakSound(), 1.0F, 1.0F);
      }

   }

   protected void tickDeath() {
      ++this.deathTime;
      if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
         Level var2 = this.level();
         if (var2 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var2;

            for(LivingBlockBehaviorEntry<?> behavior : this.behaviors) {
               behavior.onDeath(level);
            }
         }

         this.level().broadcastEntityEvent(this, (byte)60);
         this.discard();
      }

   }

   private void tryCraft(final @Nullable Entity crafter, final LivingBlockGroup group) {
      Level var4 = this.level();
      if (var4 instanceof ServerLevel level) {
         CraftingInput var11 = CraftingInput.of(2, 2, List.of(this.getItemStack(), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
         Optional<RecipeHolder<CraftingRecipe>> maybeRecipe = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, var11, level, (RecipeHolder)null);
         if (maybeRecipe.isPresent()) {
            RecipeHolder<CraftingRecipe> recipe = (RecipeHolder)maybeRecipe.get();
            CraftingRecipe craftingRecipe = recipe.value();
            ServerPlayer var10000;
            if (crafter instanceof ServerPlayer) {
               ServerPlayer craftingPlayer = (ServerPlayer)crafter;
               var10000 = craftingPlayer;
            } else {
               var10000 = null;
            }

            ServerPlayer player = var10000;
            if (((CraftingRecipe)recipe.value()).isSpecial() || !(Boolean)level.getGameRules().get(GameRules.LIMITED_CRAFTING) || player != null && player.getRecipeBook().contains(recipe.id())) {
               ItemStack recipeResult = craftingRecipe.assemble(var11);
               if (recipeResult.isItemEnabled(level.enabledFeatures())) {
                  Collection<LivingBlock> crafted = createStack(level, this.blockPosition(), player, recipeResult);
                  crafted.forEach((livingBlock) -> {
                     livingBlock.setOwner(player);
                     livingBlock.setGroup(group);
                  });
               }
            }
         }

      }
   }

   private void tickHealthRegeneration(final ServerLevel level) {
      if ((Boolean)level.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION)) {
         --this.healthRegenCooldown;
         if (this.healthRegenCooldown <= 0) {
            if (this.tickCount % 20 == 0) {
               this.setHealth(this.getHealth() + 1.0F);
            }

         }
      }
   }

   private void startDespawning() {
      this.ticksBeforeDespawn = 6000;
   }

   private void tickDespawn() {
      boolean anyPlayerNearby = this.level().getNearestPlayer(this, 50.0) != null;
      boolean interacted = (Boolean)this.entityData.get(DATA_PLAYER_INTERACTED);
      if (!anyPlayerNearby && !interacted) {
         --this.ticksBeforeDespawn;
         if (this.ticksBeforeDespawn == 0) {
            this.discard();
         }
      }

   }

   public void thunderHit(final ServerLevel level, final LightningBolt lightningBolt) {
      this.setDeltaMovement((double)(-1.0F + level.getRandom().nextFloat() * 2.0F), 1.0, (double)(-1.0F + level.getRandom().nextFloat() * 2.0F));
      level.sendParticles(ParticleTypes.EXPLOSION, true, true, this.getX() + this.random.nextDouble(), this.getY() + 1.0, this.getZ() + this.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
      this.setBlockState(Blocks.SHROOMLIGHT.defaultBlockState());
   }

   public boolean isDeadOrDying() {
      return this.getHealth() <= 0.0F;
   }

   public void handleDamageEvent(final DamageSource source) {
      this.hurtTime = 10;
      this.lastDamageSource = source;
      this.lastDamageStamp = this.level().getGameTime();
      if (this.isDeadOrDying() && !this.dead) {
         this.dead = true;
      }

   }

   public void handleEntityEvent(final byte id) {
      if (id == 60) {
         this.makePoofParticles();
      } else {
         super.handleEntityEvent(id);
      }

   }

   public void makePoofParticles() {
      for(int i = 0; i < 20; ++i) {
         double xa = this.random.nextGaussian() * 0.02;
         double ya = this.random.nextGaussian() * 0.02;
         double za = this.random.nextGaussian() * 0.02;
         double dd = 10.0;
         this.level().addParticle(ParticleTypes.POOF, this.getRandomX(1.0) - xa * 10.0, this.getRandomY() - ya * 10.0, this.getRandomZ(1.0) - za * 10.0, xa, ya, za);
      }

   }

   public float getHealth() {
      return (Float)this.entityData.get(DATA_HEALTH_ID);
   }

   public void setHealth(final float health) {
      this.entityData.set(DATA_HEALTH_ID, Mth.clamp(health, 0.0F, this.getMaxHealth()));
   }

   public float getMaxHealth() {
      return (Float)this.entityData.get(DATA_MAX_HEALTH_ID);
   }

   public void tick() {
      if (this.hurtTime > 0) {
         --this.hurtTime;
      }

      if (this.isDeadOrDying()) {
         this.tickDeath();
      }

      applyMovementRotation(this.rollDeltaX, this.rollDeltaZ, this.rotation);
      if (this.rollSoundTime-- <= 0 && !this.normalStepSounds() && !this.lastRotation.equals(this.rotation, 1.0E-5F)) {
         Quaternionf groundAngle = Mth.snapToNearestRightAngle(this.rotation);
         Quaternionf lastToGround = new Quaternionf(this.lastRotation);
         lastToGround.conjugate();
         lastToGround.mul(groundAngle);
         double lastAngleToZero = Math.toDegrees((double)lastToGround.angle());
         double lastDistanceToZero = Math.min(lastAngleToZero, 360.0 - lastAngleToZero);
         Quaternionf currentToGround = new Quaternionf(this.rotation);
         currentToGround.conjugate();
         currentToGround.mul(groundAngle);
         double currentAngleToZero = Math.toDegrees((double)currentToGround.angle()) % 360.0;
         double currentDistanceToZero = Math.min(currentAngleToZero, 360.0 - currentAngleToZero);
         if (lastDistanceToZero > 10.0 && currentDistanceToZero <= 10.0) {
            BlockPos effectPos = this.getOnPosLegacy();
            BlockState effectState = this.level().getBlockState(effectPos);
            this.walkingStepSound(effectPos, effectState);
            this.level().gameEvent(GameEvent.STEP, effectPos, GameEvent.Context.of(this, effectState));
            this.rollSoundTime = 4;
         }
      }

      this.lastRotation.set(this.rotation);
      this.rollDeltaX = 0.0F;
      this.rollDeltaZ = 0.0F;
      super.tick();
      if (this.isInterpolating()) {
         this.getInterpolation().interpolate();
      }

      Level targetPos = this.level();
      if (targetPos instanceof ServerLevel level) {
         this.tickBehaviors(level);
         this.tickHealthRegeneration(level);
         this.tickDespawn();
      }

      if (!this.isDeadOrDying() && !this.isPinned()) {
         Target target = this.getMovementTarget();
         Vec3 targetPos = target.resolvePosition(this.level());
         if (targetPos != null) {
            boolean moved = this.movement.moveTowardsTarget(this, target, targetPos);
            if (!moved && target.clearWhenNear()) {
               this.moveTowardsIntent.clear();
               this.movement.resetMovement(this);
            }
         }
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      boolean isClimbing = this.isClimbing();
      BlockPos posBelow = this.getBlockPosBelowThatAffectsMyMovement();
      float blockFriction = this.onGround() ? this.level().getBlockState(posBelow).getBlock().getFriction() : 1.0F;
      float friction = blockFriction * 0.96F;
      float frictionY = isClimbing ? 0.57600003F : 0.98F;
      this.setDeltaMovement(this.getDeltaMovement().multiply((double)friction, (double)frictionY, (double)friction));
      if (!isClimbing || this.onGround()) {
         this.applyGravity();
      }

      if (this.isInWater()) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
      }

      if (this.onGround() && this.getDeltaMovement().horizontalDistanceSqr() < Mth.square(0.001) || isClimbing && Math.abs(this.getDeltaMovement().y) < 0.001) {
         Vec3 blockGridDelta = this.blockPosition().getBottomCenter().subtract(this.position()).horizontal();
         double blockGridOffset = blockGridDelta.length();
         double rotationAlpha = Mth.clamp(blockGridOffset * 64.0, 0.5, 1.0);
         this.rotation.slerp(Mth.snapToNearestRightAngle(this.rotation), (float)rotationAlpha);
         if (blockGridOffset > 1.0E-6 && blockGridOffset <= 0.125 && this.isIdle() && !this.level().isClientSide()) {
            this.move(MoverType.SELF, blockGridDelta);
         }
      }

      Vector3f facing = new Vector3f();
      this.rotation.getEulerAnglesYXZ(facing);
      if (facing.isFinite()) {
         this.setRot(facing.y * 57.295776F, facing.x * 57.295776F);
      } else {
         this.setRot(0.0F, 0.0F);
         this.rotation.identity();
      }

      this.playerChaseTheSkies(this.level());
      this.applyEffectsFromBlocks();
      if (this.movement instanceof BouncingMovement) {
         Vec3 pogoScaleDiff = this.pogoScaleTarget.subtract(this.currentPogoScale);
         if (pogoScaleDiff.lengthSqr() > 9.999999747378752E-6) {
            this.currentPogoScale = Mth.lerp(1.0 - (double)this.pogoScaleTicksRemaining / (double)this.pogoScaleTicks, this.lastPogoScaleTarget, this.pogoScaleTarget);
         }

         if (this.pogoScaleTicksRemaining > 0) {
            --this.pogoScaleTicksRemaining;
         }
      }

      AABB bounds = this.getBoundingBox();
      double sizeY = bounds.getYsize();
      if (sizeY > 9.999999747378752E-6) {
         Vector3f localYaxis = Mth.Y_AXIS.rotate(this.lastRotation, new Vector3f());
         float tilt = Math.abs(localYaxis.y);
         double sizeX = bounds.getXsize();
         double sizeZ = bounds.getZsize();
         float rotationSpeed = !isClimbing && !this.onGround() ? 0.5F : 1.0F;
         if (sizeZ > 9.999999747378752E-6) {
            double sideLength = sizeZ < sizeY ? Mth.lerp((double)tilt, Math.sqrt(sizeZ / sizeY) * sizeY, sizeY) : Mth.lerp((double)tilt, sizeZ, Math.sqrt(sizeY / sizeZ) * sizeZ);
            this.rollDeltaX += (float)((this.getZ() - this.zo) * (double)rotationSpeed / sideLength);
         }

         if (sizeX > 9.999999747378752E-6) {
            double sideLength = sizeX < sizeY ? Mth.lerp((double)tilt, Math.sqrt(sizeX / sizeY) * sizeY, sizeY) : Mth.lerp((double)tilt, sizeX, Math.sqrt(sizeY / sizeX) * sizeX);
            this.rollDeltaZ += (float)((this.getX() - this.xo) * (double)rotationSpeed / sideLength);
         }

         if (isClimbing) {
            Direction groundDirection = this.getClimbingDirection();
            boolean onXAxis = groundDirection.getAxis() == Direction.Axis.X;
            double size = onXAxis ? sizeZ : sizeX;
            double sideLength = size < sizeY ? Mth.lerp((double)tilt, sizeY, Math.sqrt(size / sizeY) * sizeY) : Mth.lerp((double)tilt, Math.sqrt(sizeY / size) * size, size);
            int sign = groundDirection.getAxisDirection().getStep();
            float roll = (float)((this.getY() - this.yo) * (double)rotationSpeed / sideLength * (double)sign);
            if (onXAxis) {
               this.rollDeltaZ += roll;
            } else {
               this.rollDeltaX += roll;
            }
         }
      }

      if (this.collision.canBeNudged()) {
         for(Entity entity : this.level().getEntities(this, AABB.around(bounds.getCenter(), 0.05), (t) -> {
            boolean var10000;
            if (t instanceof LivingBlock livingBlock) {
               if (livingBlock.collision.canBeNudged()) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         })) {
            entity.push((Entity)this);
         }
      }

      if (!this.isDeadOrDying()) {
         BlockPos newBlockPos = this.blockPosition();
         if (!newBlockPos.equals(this.lastBlockPos)) {
            ChunkPos lastChunkPos = ChunkPos.containing(this.lastBlockPos);
            ChunkPos newChunkPos = ChunkPos.containing(newBlockPos);
            boolean movedChunks = !newChunkPos.equals(lastChunkPos);
            if (movedChunks) {
               this.level().getChunkAt(this.lastBlockPos).removeLivingBlock(this, this.lastBlockPos);
            }

            LevelChunk newChunk = this.level().getChunkAt(newBlockPos);
            if (movedChunks) {
               newChunk.addLivingBlock(this);
            } else {
               newChunk.livingBlockMoved(this, this.lastBlockPos, newBlockPos);
            }

            this.lastBlockPos = newBlockPos;
         }
      }

   }

   private void tickBehaviors(final ServerLevel level) {
      for(LivingBlockBehaviorEntry<?> behavior : this.behaviors) {
         boolean isActive = behavior.isActive();
         if (isActive || (this.currentActivities & behavior.mutex()) == 0) {
            boolean stayActive = behavior.tick(level, this.tickCount);
            if (isActive != stayActive) {
               this.currentActivities ^= behavior.mutex();
            }
         }
      }

   }

   public void stopAllBehaviors() {
      this.behaviors.forEach(LivingBlockBehaviorEntry::stop);
      this.currentActivities = 0;
   }

   protected boolean normalStepSounds() {
      return this.movement.normalStepSounds();
   }

   public void interrupt() {
      this.stopAllBehaviors();
      this.hopesAndDreams.stopDesiringAnything();
      this.beMadAtIntent.clear();
      if (this.hasMovementTarget()) {
         this.moveTowardsIntent.clear();
         this.movement.resetMovement(this);
      }

   }

   public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
      ItemStack itemInHand = player.getItemInHand(hand);
      Item var6 = itemInHand.getItem();
      if (var6 instanceof ActionItem action) {
         InteractionResult interactionResult = action.interactLivingBlock(player, this);
         if (interactionResult != InteractionResult.PASS) {
            return interactionResult;
         }
      }

      InteractionResult apply = this.onInteract.apply(player, hand, location, this);
      return apply == InteractionResult.PASS ? super.interact(player, hand, location) : apply;
   }

   private static void applyMovementRotation(final float dx, final float dz, final Quaternionf dest) {
      if (Math.abs(dx) > 1.0E-5F) {
         dest.rotateLocalX(dx * 1.5707964F);
      }

      if (Math.abs(dz) > 1.0E-5F) {
         dest.rotateLocalZ(dz * 1.5707964F);
      }

   }

   public void getRotation(final Quaternionf dest, final float partialTicks) {
      this.lastRotation.slerp(this.rotation, partialTicks, dest);
      applyMovementRotation(this.rollDeltaX * partialTicks, this.rollDeltaZ * partialTicks, dest);
   }

   protected double getDefaultGravity() {
      BlockState block = this.getBlockState();
      return ballons.contains(block.getBlock()) ? -0.001 : 0.05;
   }

   private void playerChaseTheSkies(final Level level) {
      Entity leashHolder = this.getLeashHolder();
      if (leashHolder instanceof Player player) {
         List<LivingBlock> leashedToPlayer = level.getEntitiesOfClass(LivingBlock.class, player.getBoundingBox().inflate(10.0), (lb) -> lb.getLeashHolder() == player && ballons.contains(lb.getBlockState().getBlock()));
         if (leashedToPlayer.size() >= 3) {
            player.setIgnoreFallDamageFromCurrentImpulse(true, player.position());
            ((AttributeInstance)Objects.requireNonNull(player.getAttribute(Attributes.GRAVITY))).setBaseValue(0.01);
         } else {
            ((AttributeInstance)Objects.requireNonNull(player.getAttribute(Attributes.GRAVITY))).setBaseValue(0.08);
         }
      }

   }

   public boolean causeFallDamage(final double fallDistance, final float damageModifier, final DamageSource damageSource) {
      BlockState state = this.getBlockState();
      if (state.is(BlockTags.LIVING_BLOCK_TAKES_FALL_DAMAGE)) {
         boolean damaged = super.causeFallDamage(fallDistance, damageModifier, damageSource);
         int dmg = Mth.floor((fallDistance + 1.0E-6) * (double)damageModifier);
         if (dmg > 0) {
            this.playSound(state.getSoundType().getFallSound(), 1.0F, 1.0F);
            LivingEntity.playBlockFallSoundForEntity(this);
            this.hurt(damageSource, (float)dmg);
            return true;
         } else {
            return damaged;
         }
      } else {
         return false;
      }
   }

   public void teleportSetPosition(final PositionMoveRotation currentValues, final PositionMoveRotation destination, final Set<Relative> relatives) {
      super.teleportSetPosition(currentValues, destination, relatives);
      if (this.hasMovementTarget()) {
         this.moveTowardsIntent.clear();
         this.movement.resetMovement(this);
      }

   }

   public boolean randomTeleport(final double xx, final double yy, final double zz, final boolean showParticles) {
      double xo = this.getX();
      double yo = this.getY();
      double zo = this.getZ();
      double y = yy;
      boolean ok = false;
      BlockPos pos = BlockPos.containing(xx, yy, zz);
      Level level = this.level();
      if (level.hasChunkAt(pos)) {
         boolean landed = false;

         while(!landed && pos.getY() > level.getMinY()) {
            BlockPos below = pos.below();
            BlockState state = level.getBlockState(below);
            if (state.blocksMotion()) {
               landed = true;
            } else {
               --y;
               pos = below;
            }
         }

         if (landed) {
            this.teleportTo(xx, y, zz);
            if (level.noCollision(this) && !level.containsAnyLiquid(this.getBoundingBox())) {
               ok = true;
            }
         }
      }

      if (!ok) {
         this.teleportTo(xo, yo, zo);
         return false;
      } else {
         if (showParticles) {
            level.broadcastEntityEvent(this, (byte)46);
         }

         return true;
      }
   }

   public InterpolationHandler getInterpolation() {
      return this.interpolationHandler;
   }

   public boolean canBeCollidedWith(final @Nullable Entity other) {
      return !this.isDeadOrDying() && this.collision.canBeCollidedWith();
   }

   public boolean isPushable() {
      return !this.isDeadOrDying() && this.collision.isPushable();
   }

   public @Nullable Player getAttackedBy() {
      return this.attackedBy;
   }

   public void setAttackedBy(final @Nullable Player attackedBy) {
      this.attackedBy = attackedBy;
   }

   public float maxUpStep() {
      return this.maxUpStep;
   }

   public void setMaxUpStep(final float maxUpStep) {
      this.maxUpStep = maxUpStep;
   }

   public void resetMaxUpStep() {
      this.maxUpStep = 0.6F;
   }

   protected AABB makeBoundingBox(final Vec3 position) {
      BlockState blockState = this.getBlockState();
      if (!blockState.isAir()) {
         VoxelShape shape = blockState.getShape(this.level(), BlockPos.containing(position), CollisionContext.empty());
         if (!shape.isEmpty()) {
            AABB bounds = shape.bounds();
            this.boundingBoxOffset = bounds.getCenter().reverse();
            return bounds.move(position.subtract(bounds.getBottomCenter()));
         }
      }

      return !this.getItemStack().isEmpty() ? EntityDimensions.scalable(0.5F, 0.5F).makeBoundingBox(position) : super.makeBoundingBox(position);
   }

   public MovementData getMovementData() {
      return (MovementData)this.entityData.get(MOVEMENT_DATA);
   }

   public void setMovementData(final MovementData data) {
      this.entityData.set(MOVEMENT_DATA, data);
   }

   public Target getMovementTarget() {
      return (Target)this.entityData.get(MOVEMENT_TARGET);
   }

   public @Nullable Vec3 getTargetPosition() {
      return this.getMovementTarget().resolvePosition(this.level());
   }

   public boolean hasMovementTarget() {
      return this.getTargetPosition() != null;
   }

   public @Nullable Targetable getAttackTarget() {
      return (Targetable)EntityReference.get(this.attackTarget, this.level(), Targetable.class);
   }

   public boolean isAttacking() {
      Targetable attackTarget = this.getAttackTarget();
      return attackTarget != null && attackTarget.isAlive();
   }

   public boolean isIdle() {
      return !this.hasMovementTarget() && !this.isAttacking();
   }

   public @Nullable Player getOwner() {
      LivingEntity owner = EntityReference.getLivingEntity((EntityReference)((Optional)this.entityData.get(DATA_OWNER)).orElse((Object)null), this.level());
      Player var10000;
      if (owner instanceof Player player) {
         var10000 = player;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private @Nullable UUID getOwnerUUID() {
      return (UUID)((Optional)this.entityData.get(DATA_OWNER)).map(EntityReference::getUUID).orElse((Object)null);
   }

   public boolean isOwnedBy(final Player player) {
      return this.getOwner() == player;
   }

   public void setOwner(final @Nullable Player owner) {
      this.entityData.set(DATA_OWNER, Optional.ofNullable(owner).map(EntityReference::of));
      if (owner == null) {
         this.startDespawning();
      } else {
         this.entityData.set(DATA_PLAYER_INTERACTED, true);
      }

   }

   public @Nullable Player getCommander() {
      LivingEntity owner = EntityReference.getLivingEntity((EntityReference)((Optional)this.entityData.get(DATA_COMMANDER)).orElse((Object)null), this.level());
      Player var10000;
      if (owner instanceof Player player) {
         var10000 = player;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public void setCommander(final @Nullable Player owner) {
      this.entityData.set(DATA_COMMANDER, Optional.ofNullable(owner).map(EntityReference::of));
   }

   public boolean canBeControlledBy(final @Nullable Player player) {
      if (this.isAlive() && player != null) {
         UUID ownerUUID = this.getOwnerUUID();
         return ownerUUID == null || ownerUUID.equals(player.getUUID()) || this.level().getPlayerByUUID(ownerUUID) == null;
      } else {
         return false;
      }
   }

   public Leashable.@Nullable LeashData getLeashData() {
      return this.leashData;
   }

   public void setLeashData(final Leashable.@Nullable LeashData leashData) {
      this.leashData = leashData;
   }

   public boolean canBeSeenByAnyone() {
      return this.isAlive();
   }

   public boolean canBeSeenAsEnemy() {
      return !this.isInvulnerable() && this.canBeSeenByAnyone();
   }

   public double getVisibilityPercent(final @Nullable Entity targetingEntity) {
      double visibilityPercent = 1.0;
      if (this.isDiscrete()) {
         visibilityPercent *= 0.8;
      }

      if (this.isInvisible()) {
         float coverPercentage = 0.0F;
         if (coverPercentage < 0.1F) {
            coverPercentage = 0.1F;
         }

         visibilityPercent *= 0.7 * (double)coverPercentage;
      }

      if (targetingEntity != null) {
         ItemStack itemStack = this.getItemStack();
         if (targetingEntity.is(EntityType.SKELETON) && itemStack.is(Items.SKELETON_SKULL) || targetingEntity.is(EntityType.ZOMBIE) && itemStack.is(Items.ZOMBIE_HEAD) || targetingEntity.is(EntityType.PIGLIN) && itemStack.is(Items.PIGLIN_HEAD) || targetingEntity.is(EntityType.PIGLIN_BRUTE) && itemStack.is(Items.PIGLIN_HEAD) || targetingEntity.is(EntityType.CREEPER) && itemStack.is(Items.CREEPER_HEAD)) {
            visibilityPercent *= 0.5;
         }
      }

      return visibilityPercent;
   }

   public boolean canAttack(final Entity target) {
      if (target instanceof Player && this.level().getDifficulty() == Difficulty.PEACEFUL) {
         return false;
      } else if (target instanceof Targetable) {
         Targetable targetable = (Targetable)target;
         return targetable.canBeSeenAsEnemy();
      } else {
         return false;
      }
   }

   public AABB getHitbox() {
      return this.getBoundingBox();
   }

   public @Nullable DamageSource getLastDamageSource() {
      if (this.level().getGameTime() - this.lastDamageStamp > 40L) {
         this.lastDamageSource = null;
      }

      return this.lastDamageSource;
   }

   private static void addEntityTagsFromItemTags(LivingBlock livingBlock, ItemStack itemStack) {
      if (itemStack.is(ItemTags.PIG_FOOD)) {
         livingBlock.addTag(ItemTags.PIG_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.COW_FOOD)) {
         livingBlock.addTag(ItemTags.COW_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.SHEEP_FOOD)) {
         livingBlock.addTag(ItemTags.SHEEP_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.CHICKEN_FOOD)) {
         livingBlock.addTag(ItemTags.CHICKEN_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.HORSE_FOOD)) {
         livingBlock.addTag(ItemTags.HORSE_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.LLAMA_TEMPT_ITEMS)) {
         livingBlock.addTag(ItemTags.LLAMA_TEMPT_ITEMS.location().getPath());
      }

      if (itemStack.is(ItemTags.ZOMBIE_HORSE_FOOD)) {
         livingBlock.addTag(ItemTags.ZOMBIE_HORSE_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.PANDA_FOOD)) {
         livingBlock.addTag(ItemTags.PANDA_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.TURTLE_FOOD)) {
         livingBlock.addTag(ItemTags.TURTLE_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.STRIDER_TEMPT_ITEMS)) {
         livingBlock.addTag(ItemTags.STRIDER_TEMPT_ITEMS.location().getPath());
      }

      if (itemStack.is(ItemTags.CAT_FOOD)) {
         livingBlock.addTag(ItemTags.CAT_FOOD.location().getPath());
      }

      if (itemStack.is(ItemTags.RABBIT_FOOD)) {
         livingBlock.addTag(ItemTags.RABBIT_FOOD.location().getPath());
      }

   }

   public boolean takeDamageOnAttack() {
      return this.takeDamageOnAttack;
   }

   public boolean isNonEmptyChest() {
      for(LivingBlockBehaviorEntry<?> behavior : this.behaviors) {
         LivingBlockBehavior var4 = behavior.instance;
         if (var4 instanceof LivingBlockContainerBehavior container) {
            return !container.isEmptyContainer();
         }
      }

      return false;
   }

   public void setPogoScaleTarget(final Vec3 scale, final int ticks) {
      double length = this.pogoScaleTarget.subtract(scale).lengthSqr();
      if (!(length < 9.999999747378752E-6)) {
         this.lastPogoScaleTarget = this.currentPogoScale;
         this.pogoScaleTarget = scale;
         this.pogoScaleTicks = this.pogoScaleTicksRemaining = ticks;
      }
   }

   public Vector3fc getPogoScale(final float partialTicks) {
      if (this.pogoScaleTicksRemaining == 0) {
         return this.currentPogoScale.toVector3f();
      } else {
         double t = (double)partialTicks * (1.0 / (double)this.pogoScaleTicks);
         return this.currentPogoScale.add(this.pogoScaleTarget.subtract(this.lastPogoScaleTarget).multiply(t, t, t)).toVector3f();
      }
   }

   public void onEquippedItemBroken(final Item item, final EquipmentSlot inSlot) {
   }

   public ItemStack getItemBySlot(final EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND ? this.getItemStack() : ItemStack.EMPTY;
   }

   public @Nullable ServerPlayer getAttributablePlayer() {
      Player var3 = this.getCommander();
      if (var3 instanceof ServerPlayer player) {
         return player;
      } else {
         var3 = this.getOwner();
         if (var3 instanceof ServerPlayer player) {
            return player;
         } else {
            Level player = this.level();
            if (player instanceof ServerLevel level) {
               return (ServerPlayer)level.getNearestPlayer(this, 25.0);
            } else {
               return null;
            }
         }
      }
   }

   public Optional<LivingBlockBehaviorEntry<?>> getBehaviorOfType(final Class<? extends LivingBlockBehavior> type) {
      return this.behaviors.stream().filter((entry) -> type.isAssignableFrom(entry.instance.getClass())).findFirst();
   }

   public boolean fireImmune() {
      return this.fireImmune;
   }

   protected void setFireImmune(final boolean fireImmune) {
      this.fireImmune = fireImmune;
   }

   public String getPlainTextName() {
      return this.hasCustomName() ? super.getPlainTextName() : this.getItemStack().getDisplayName().getString();
   }

   static {
      DATA_ITEM_STACK_ID = SynchedEntityData.<ItemStack>defineId(LivingBlock.class, EntityDataSerializers.ITEM_STACK);
      DATA_HEALTH_ID = SynchedEntityData.<Float>defineId(LivingBlock.class, EntityDataSerializers.FLOAT);
      DATA_MAX_HEALTH_ID = SynchedEntityData.<Float>defineId(LivingBlock.class, EntityDataSerializers.FLOAT);
      DATA_GROUP = SynchedEntityData.<Integer>defineId(LivingBlock.class, EntityDataSerializers.INT);
      DATA_COMMANDER = SynchedEntityData.<Optional<EntityReference<LivingEntity>>>defineId(LivingBlock.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
      DATA_OWNER = SynchedEntityData.<Optional<EntityReference<LivingEntity>>>defineId(LivingBlock.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
      DATA_PLAYER_INTERACTED = SynchedEntityData.<Boolean>defineId(LivingBlock.class, EntityDataSerializers.BOOLEAN);
      DATA_PINNED = SynchedEntityData.<Boolean>defineId(LivingBlock.class, EntityDataSerializers.BOOLEAN);
      DATA_SELECTED = SynchedEntityData.<Boolean>defineId(LivingBlock.class, EntityDataSerializers.BOOLEAN);
      MOVEMENT_DATA = SynchedEntityData.<MovementData>defineId(LivingBlock.class, EntityDataSerializers.MOVEMENT_DATA);
      MOVEMENT_TARGET = SynchedEntityData.<Target>defineId(LivingBlock.class, EntityDataSerializers.TARGET);
      DATA_CLIMBING_DIRECTION = SynchedEntityData.<Direction>defineId(LivingBlock.class, EntityDataSerializers.DIRECTION);
      ballons = List.of(Blocks.WHITE_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.GRAY_WOOL, Blocks.BLACK_WOOL, Blocks.BROWN_WOOL, Blocks.RED_WOOL, Blocks.ORANGE_WOOL, Blocks.YELLOW_WOOL, Blocks.LIME_WOOL, Blocks.GREEN_WOOL, Blocks.CYAN_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.BLUE_WOOL, Blocks.PURPLE_WOOL, Blocks.MAGENTA_WOOL, Blocks.PINK_WOOL);
      BE_MAD_AT = Action.<Targetable>of((e) -> e.beMadAtIntent);
      MOVE_TOWARDS = Action.<Target>of((e) -> e.moveTowardsIntent);
   }
}
