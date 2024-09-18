package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class Entity implements SyncedDataHolder, Nameable, EntityAccess, CommandSource, ScoreHolder {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String ID_TAG = "id";
   public static final String PASSENGERS_TAG = "Passengers";
   private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
   public static final int CONTENTS_SLOT_INDEX = 0;
   public static final int BOARDING_COOLDOWN = 60;
   public static final int TOTAL_AIR_SUPPLY = 300;
   public static final int MAX_ENTITY_TAG_COUNT = 1024;
   public static final float DELTA_AFFECTED_BY_BLOCKS_BELOW_0_2 = 0.2F;
   public static final double DELTA_AFFECTED_BY_BLOCKS_BELOW_0_5 = 0.500001;
   public static final double DELTA_AFFECTED_BY_BLOCKS_BELOW_1_0 = 0.999999;
   public static final int BASE_TICKS_REQUIRED_TO_FREEZE = 140;
   public static final int FREEZE_HURT_FREQUENCY = 40;
   public static final int BASE_SAFE_FALL_DISTANCE = 3;
   private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private static final double WATER_FLOW_SCALE = 0.014;
   private static final double LAVA_FAST_FLOW_SCALE = 0.007;
   private static final double LAVA_SLOW_FLOW_SCALE = 0.0023333333333333335;
   public static final String UUID_TAG = "UUID";
   private static double viewScale = 1.0;
   private final EntityType<?> type;
   private int id = ENTITY_COUNTER.incrementAndGet();
   public boolean blocksBuilding;
   private ImmutableList<Entity> passengers = ImmutableList.of();
   protected int boardingCooldown;
   @Nullable
   private Entity vehicle;
   private Level level;
   public double xo;
   public double yo;
   public double zo;
   private Vec3 position;
   private BlockPos blockPosition;
   private ChunkPos chunkPosition;
   private Vec3 deltaMovement = Vec3.ZERO;
   private float yRot;
   private float xRot;
   public float yRotO;
   public float xRotO;
   private AABB bb = INITIAL_AABB;
   private boolean onGround;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean verticalCollisionBelow;
   public boolean minorHorizontalCollision;
   public boolean hurtMarked;
   protected Vec3 stuckSpeedMultiplier = Vec3.ZERO;
   @Nullable
   private Entity.RemovalReason removalReason;
   public static final float DEFAULT_BB_WIDTH = 0.6F;
   public static final float DEFAULT_BB_HEIGHT = 1.8F;
   public float moveDist;
   public float flyDist;
   public float fallDistance;
   private float nextStep = 1.0F;
   public double xOld;
   public double yOld;
   public double zOld;
   public boolean noPhysics;
   protected final RandomSource random = RandomSource.create();
   public int tickCount;
   private int remainingFireTicks = -this.getFireImmuneTicks();
   protected boolean wasTouchingWater;
   protected Object2DoubleMap<TagKey<Fluid>> fluidHeight = new Object2DoubleArrayMap(2);
   protected boolean wasEyeInWater;
   private final Set<TagKey<Fluid>> fluidOnEyes = new HashSet<>();
   public int invulnerableTime;
   protected boolean firstTick = true;
   protected final SynchedEntityData entityData;
   protected static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BYTE);
   protected static final int FLAG_ONFIRE = 0;
   private static final int FLAG_SHIFT_KEY_DOWN = 1;
   private static final int FLAG_SPRINTING = 3;
   private static final int FLAG_SWIMMING = 4;
   private static final int FLAG_INVISIBLE = 5;
   protected static final int FLAG_GLOWING = 6;
   protected static final int FLAG_FALL_FLYING = 7;
   private static final EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME = SynchedEntityData.defineId(
      Entity.class, EntityDataSerializers.OPTIONAL_COMPONENT
   );
   private static final EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_SILENT = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_NO_GRAVITY = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Pose> DATA_POSE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.POSE);
   private static final EntityDataAccessor<Integer> DATA_TICKS_FROZEN = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
   private EntityInLevelCallback levelCallback = EntityInLevelCallback.NULL;
   private final VecDeltaCodec packetPositionCodec = new VecDeltaCodec();
   public boolean hasImpulse;
   @Nullable
   public PortalProcessor portalProcess;
   private int portalCooldown;
   private boolean invulnerable;
   protected UUID uuid = Mth.createInsecureUUID(this.random);
   protected String stringUUID = this.uuid.toString();
   private boolean hasGlowingTag;
   private final Set<String> tags = Sets.newHashSet();
   private final double[] pistonDeltas = new double[]{0.0, 0.0, 0.0};
   private long pistonDeltasGameTime;
   private EntityDimensions dimensions;
   private float eyeHeight;
   public boolean isInPowderSnow;
   public boolean wasInPowderSnow;
   public Optional<BlockPos> mainSupportingBlockPos = Optional.empty();
   private boolean onGroundNoBlocks = false;
   private float crystalSoundIntensity;
   private int lastCrystalSoundPlayTick;
   private boolean hasVisualFire;
   @Nullable
   private BlockState inBlockState = null;
   private final Map<BlockPos, BlockState> blocksInside = new HashMap<>();

   public Entity(EntityType<?> var1, Level var2) {
      super();
      this.type = var1;
      this.level = var2;
      this.dimensions = var1.getDimensions();
      this.position = Vec3.ZERO;
      this.blockPosition = BlockPos.ZERO;
      this.chunkPosition = ChunkPos.ZERO;
      SynchedEntityData.Builder var3 = new SynchedEntityData.Builder(this);
      var3.define(DATA_SHARED_FLAGS_ID, (byte)0);
      var3.define(DATA_AIR_SUPPLY_ID, this.getMaxAirSupply());
      var3.define(DATA_CUSTOM_NAME_VISIBLE, false);
      var3.define(DATA_CUSTOM_NAME, Optional.empty());
      var3.define(DATA_SILENT, false);
      var3.define(DATA_NO_GRAVITY, false);
      var3.define(DATA_POSE, Pose.STANDING);
      var3.define(DATA_TICKS_FROZEN, 0);
      this.defineSynchedData(var3);
      this.entityData = var3.build();
      this.setPos(0.0, 0.0, 0.0);
      this.eyeHeight = this.dimensions.eyeHeight();
   }

   public boolean isColliding(BlockPos var1, BlockState var2) {
      VoxelShape var3 = var2.getCollisionShape(this.level(), var1, CollisionContext.of(this));
      VoxelShape var4 = var3.move((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
      return Shapes.joinIsNotEmpty(var4, Shapes.create(this.getBoundingBox()), BooleanOp.AND);
   }

   public int getTeamColor() {
      PlayerTeam var1 = this.getTeam();
      return var1 != null && var1.getColor().getColor() != null ? var1.getColor().getColor() : 16777215;
   }

   public boolean isSpectator() {
      return false;
   }

   public final void unRide() {
      if (this.isVehicle()) {
         this.ejectPassengers();
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }
   }

   public void syncPacketPositionCodec(double var1, double var3, double var5) {
      this.packetPositionCodec.setBase(new Vec3(var1, var3, var5));
   }

   public VecDeltaCodec getPositionCodec() {
      return this.packetPositionCodec;
   }

   public EntityType<?> getType() {
      return this.type;
   }

   @Override
   public int getId() {
      return this.id;
   }

   public void setId(int var1) {
      this.id = var1;
   }

   public Set<String> getTags() {
      return this.tags;
   }

   public boolean addTag(String var1) {
      return this.tags.size() >= 1024 ? false : this.tags.add(var1);
   }

   public boolean removeTag(String var1) {
      return this.tags.remove(var1);
   }

   public void kill() {
      this.remove(Entity.RemovalReason.KILLED);
      this.gameEvent(GameEvent.ENTITY_DIE);
   }

   public final void discard() {
      this.remove(Entity.RemovalReason.DISCARDED);
   }

   protected abstract void defineSynchedData(SynchedEntityData.Builder var1);

   public SynchedEntityData getEntityData() {
      return this.entityData;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Entity ? ((Entity)var1).id == this.id : false;
   }

   @Override
   public int hashCode() {
      return this.id;
   }

   public void remove(Entity.RemovalReason var1) {
      this.setRemoved(var1);
   }

   public void onClientRemoval() {
   }

   public void setPose(Pose var1) {
      this.entityData.set(DATA_POSE, var1);
   }

   public Pose getPose() {
      return this.entityData.get(DATA_POSE);
   }

   public boolean hasPose(Pose var1) {
      return this.getPose() == var1;
   }

   public boolean closerThan(Entity var1, double var2) {
      return this.position().closerThan(var1.position(), var2);
   }

   public boolean closerThan(Entity var1, double var2, double var4) {
      double var6 = var1.getX() - this.getX();
      double var8 = var1.getY() - this.getY();
      double var10 = var1.getZ() - this.getZ();
      return Mth.lengthSquared(var6, var10) < Mth.square(var2) && Mth.square(var8) < Mth.square(var4);
   }

   protected void setRot(float var1, float var2) {
      this.setYRot(var1 % 360.0F);
      this.setXRot(var2 % 360.0F);
   }

   public final void setPos(Vec3 var1) {
      this.setPos(var1.x(), var1.y(), var1.z());
   }

   public void setPos(double var1, double var3, double var5) {
      this.setPosRaw(var1, var3, var5);
      this.setBoundingBox(this.makeBoundingBox());
   }

   protected AABB makeBoundingBox() {
      return this.dimensions.makeBoundingBox(this.position);
   }

   protected void reapplyPosition() {
      this.setPos(this.position.x, this.position.y, this.position.z);
   }

   public void turn(double var1, double var3) {
      float var5 = (float)var3 * 0.15F;
      float var6 = (float)var1 * 0.15F;
      this.setXRot(this.getXRot() + var5);
      this.setYRot(this.getYRot() + var6);
      this.setXRot(Mth.clamp(this.getXRot(), -90.0F, 90.0F));
      this.xRotO += var5;
      this.yRotO += var6;
      this.xRotO = Mth.clamp(this.xRotO, -90.0F, 90.0F);
      if (this.vehicle != null) {
         this.vehicle.onPassengerTurned(this);
      }
   }

   public void tick() {
      this.baseTick();
   }

   public void baseTick() {
      ProfilerFiller var1 = Profiler.get();
      var1.push("entityBaseTick");
      this.inBlockState = null;
      if (this.isPassenger() && this.getVehicle().isRemoved()) {
         this.stopRiding();
      }

      if (this.boardingCooldown > 0) {
         this.boardingCooldown--;
      }

      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
      this.handlePortal();
      if (this.canSpawnSprintParticle()) {
         this.spawnSprintParticle();
      }

      this.wasInPowderSnow = this.isInPowderSnow;
      this.isInPowderSnow = false;
      this.updateInWaterStateAndDoFluidPushing();
      this.updateFluidOnEyes();
      this.updateSwimming();
      if (this.level().isClientSide) {
         this.clearFire();
      } else if (this.remainingFireTicks > 0) {
         if (this.fireImmune()) {
            this.setRemainingFireTicks(this.remainingFireTicks - 4);
            if (this.remainingFireTicks < 0) {
               this.clearFire();
            }
         } else {
            if (this.remainingFireTicks % 20 == 0 && !this.isInLava()) {
               this.hurt(this.damageSources().onFire(), 1.0F);
            }

            this.setRemainingFireTicks(this.remainingFireTicks - 1);
         }

         if (this.getTicksFrozen() > 0) {
            this.setTicksFrozen(0);
            this.level().levelEvent(null, 1009, this.blockPosition, 1);
         }
      }

      if (this.isInLava()) {
         this.lavaHurt();
         this.fallDistance *= 0.5F;
      }

      this.checkBelowWorld();
      if (!this.level().isClientSide) {
         this.setSharedFlagOnFire(this.remainingFireTicks > 0);
      }

      this.firstTick = false;
      if (!this.level().isClientSide && this instanceof Leashable) {
         Leashable.tickLeash((Entity)((Leashable)this));
      }

      var1.pop();
   }

   public void setSharedFlagOnFire(boolean var1) {
      this.setSharedFlag(0, var1 || this.hasVisualFire);
   }

   public void checkBelowWorld() {
      if (this.getY() < (double)(this.level().getMinY() - 64)) {
         this.onBelowWorld();
      }
   }

   public void setPortalCooldown() {
      this.portalCooldown = this.getDimensionChangingDelay();
   }

   public void setPortalCooldown(int var1) {
      this.portalCooldown = var1;
   }

   public int getPortalCooldown() {
      return this.portalCooldown;
   }

   public boolean isOnPortalCooldown() {
      return this.portalCooldown > 0;
   }

   protected void processPortalCooldown() {
      if (this.isOnPortalCooldown()) {
         this.portalCooldown--;
      }
   }

   public void lavaHurt() {
      if (!this.fireImmune()) {
         this.igniteForSeconds(15.0F);
         if (this.hurt(this.damageSources().lava(), 4.0F) && this.shouldPlayLavaHurtSound()) {
            this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
         }
      }
   }

   protected boolean shouldPlayLavaHurtSound() {
      return true;
   }

   public final void igniteForSeconds(float var1) {
      this.igniteForTicks(Mth.floor(var1 * 20.0F));
   }

   public void igniteForTicks(int var1) {
      if (this.remainingFireTicks < var1) {
         this.setRemainingFireTicks(var1);
      }
   }

   public void setRemainingFireTicks(int var1) {
      this.remainingFireTicks = var1;
   }

   public int getRemainingFireTicks() {
      return this.remainingFireTicks;
   }

   public void clearFire() {
      this.setRemainingFireTicks(0);
   }

   protected void onBelowWorld() {
      this.discard();
   }

   public boolean isFree(double var1, double var3, double var5) {
      return this.isFree(this.getBoundingBox().move(var1, var3, var5));
   }

   private boolean isFree(AABB var1) {
      return this.level().noCollision(this, var1) && !this.level().containsAnyLiquid(var1);
   }

   public void setOnGround(boolean var1) {
      this.onGround = var1;
      this.checkSupportingBlock(var1, null);
   }

   public void setOnGroundWithMovement(boolean var1, boolean var2, Vec3 var3) {
      this.onGround = var1;
      this.horizontalCollision = var2;
      this.checkSupportingBlock(var1, var3);
   }

   public boolean isSupportedBy(BlockPos var1) {
      return this.mainSupportingBlockPos.isPresent() && this.mainSupportingBlockPos.get().equals(var1);
   }

   protected void checkSupportingBlock(boolean var1, @Nullable Vec3 var2) {
      if (var1) {
         AABB var3 = this.getBoundingBox();
         AABB var4 = new AABB(var3.minX, var3.minY - 1.0E-6, var3.minZ, var3.maxX, var3.minY, var3.maxZ);
         Optional var5 = this.level.findSupportingBlock(this, var4);
         if (var5.isPresent() || this.onGroundNoBlocks) {
            this.mainSupportingBlockPos = var5;
         } else if (var2 != null) {
            AABB var6 = var4.move(-var2.x, 0.0, -var2.z);
            var5 = this.level.findSupportingBlock(this, var6);
            this.mainSupportingBlockPos = var5;
         }

         this.onGroundNoBlocks = var5.isEmpty();
      } else {
         this.onGroundNoBlocks = false;
         if (this.mainSupportingBlockPos.isPresent()) {
            this.mainSupportingBlockPos = Optional.empty();
         }
      }
   }

   public boolean onGround() {
      return this.onGround;
   }

   public void move(MoverType var1, Vec3 var2) {
      if (this.noPhysics) {
         this.setPos(this.getX() + var2.x, this.getY() + var2.y, this.getZ() + var2.z);
      } else {
         if (var1 == MoverType.PISTON) {
            var2 = this.limitPistonMovement(var2);
            if (var2.equals(Vec3.ZERO)) {
               return;
            }
         }

         ProfilerFiller var3 = Profiler.get();
         var3.push("move");
         if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
            var2 = var2.multiply(this.stuckSpeedMultiplier);
            this.stuckSpeedMultiplier = Vec3.ZERO;
            this.setDeltaMovement(Vec3.ZERO);
         }

         var2 = this.maybeBackOffFromEdge(var2, var1);
         Vec3 var4 = this.collide(var2);
         double var5 = var4.lengthSqr();
         if (var5 > 1.0E-7 || var2.lengthSqr() - var5 < 1.0E-7) {
            if (this.fallDistance != 0.0F && var5 >= 1.0) {
               BlockHitResult var7 = this.level()
                  .clip(new ClipContext(this.position(), this.position().add(var4), ClipContext.Block.FALLDAMAGE_RESETTING, ClipContext.Fluid.WATER, this));
               if (var7.getType() != HitResult.Type.MISS) {
                  this.resetFallDistance();
               }
            }

            this.setPos(this.getX() + var4.x, this.getY() + var4.y, this.getZ() + var4.z);
         }

         var3.pop();
         var3.push("rest");
         boolean var13 = !Mth.equal(var2.x, var4.x);
         boolean var8 = !Mth.equal(var2.z, var4.z);
         this.horizontalCollision = var13 || var8;
         if (Math.abs(var2.y) > 0.0 || this.isControlledByOrIsLocalPlayer()) {
            this.verticalCollision = var2.y != var4.y;
            this.verticalCollisionBelow = this.verticalCollision && var2.y < 0.0;
            this.setOnGroundWithMovement(this.verticalCollisionBelow, this.horizontalCollision, var4);
         }

         if (this.horizontalCollision) {
            this.minorHorizontalCollision = this.isHorizontalCollisionMinor(var4);
         } else {
            this.minorHorizontalCollision = false;
         }

         BlockPos var9 = this.getOnPosLegacy();
         BlockState var10 = this.level().getBlockState(var9);
         if (!this.level().isClientSide() || this.isControlledByLocalInstance()) {
            this.checkFallDamage(var4.y, this.onGround(), var10, var9);
         }

         if (this.isRemoved()) {
            var3.pop();
         } else {
            if (this.horizontalCollision) {
               Vec3 var11 = this.getDeltaMovement();
               this.setDeltaMovement(var13 ? 0.0 : var11.x, var11.y, var8 ? 0.0 : var11.z);
            }

            if (this.isControlledByLocalInstance()) {
               Block var14 = var10.getBlock();
               if (var2.y != var4.y) {
                  var14.updateEntityMovementAfterFallOn(this.level(), this);
               }
            }

            if (!this.level().isClientSide() || this.isControlledByLocalInstance()) {
               Entity.MovementEmission var15 = this.getMovementEmission();
               if (var15.emitsAnything() && !this.isPassenger()) {
                  this.applyMovementEmissionAndPlaySound(var15, var4, var9, var10);
               }
            }

            float var16 = this.getBlockSpeedFactor();
            this.setDeltaMovement(this.getDeltaMovement().multiply((double)var16, 1.0, (double)var16));
            var3.pop();
         }
      }
   }

   private void applyMovementEmissionAndPlaySound(Entity.MovementEmission var1, Vec3 var2, BlockPos var3, BlockState var4) {
      float var5 = 0.6F;
      float var6 = (float)(var2.length() * 0.6000000238418579);
      float var7 = (float)(var2.horizontalDistance() * 0.6000000238418579);
      BlockPos var8 = this.getOnPos();
      BlockState var9 = this.level().getBlockState(var8);
      boolean var10 = this.isStateClimbable(var9);
      this.moveDist += var10 ? var6 : var7;
      this.flyDist += var6;
      if (this.moveDist > this.nextStep && !var9.isAir()) {
         boolean var11 = var8.equals(var3);
         boolean var12 = this.vibrationAndSoundEffectsFromBlock(var3, var4, var1.emitsSounds(), var11, var2);
         if (!var11) {
            var12 |= this.vibrationAndSoundEffectsFromBlock(var8, var9, false, var1.emitsEvents(), var2);
         }

         if (var12) {
            this.nextStep = this.nextStep();
         } else if (this.isInWater()) {
            this.nextStep = this.nextStep();
            if (var1.emitsSounds()) {
               this.waterSwimSound();
            }

            if (var1.emitsEvents()) {
               this.gameEvent(GameEvent.SWIM);
            }
         }
      } else if (var9.isAir()) {
         this.processFlappingMovement();
      }
   }

   public void applyEffectsFromBlocks() {
      this.applyEffectsFromBlocks(this.oldPosition(), this.position);
   }

   public void applyEffectsFromBlocks(Vec3 var1, Vec3 var2) {
      if (this.isAffectedByBlocks()) {
         boolean var3 = this.isOnFire();
         if (this.onGround()) {
            BlockPos var4 = this.getOnPosLegacy();
            BlockState var5 = this.level().getBlockState(var4);
            var5.getBlock().stepOn(this.level(), var4, var5, this);
         }

         this.collectBlockCollidedWith(this.blocksInside, var1, var2);
         boolean var7 = false;

         for (Entry var6 : this.blocksInside.entrySet()) {
            ((BlockState)var6.getValue()).entityInside(this.level(), (BlockPos)var6.getKey(), this);
            this.onInsideBlock((BlockState)var6.getValue());
            if (((BlockState)var6.getValue()).is(BlockTags.FIRE) || ((BlockState)var6.getValue()).is(Blocks.LAVA)) {
               var7 = true;
            }
         }

         this.blocksInside.clear();
         if (!var7) {
            if (this.remainingFireTicks <= 0) {
               this.setRemainingFireTicks(-this.getFireImmuneTicks());
            }

            if (var3 && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
               this.playEntityOnFireExtinguishedSound();
            }
         }

         if (this.isOnFire() && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
            this.setRemainingFireTicks(-this.getFireImmuneTicks());
         }
      }
   }

   protected boolean isAffectedByBlocks() {
      return !this.isRemoved() && !this.noPhysics;
   }

   private boolean isStateClimbable(BlockState var1) {
      return var1.is(BlockTags.CLIMBABLE) || var1.is(Blocks.POWDER_SNOW);
   }

   private boolean vibrationAndSoundEffectsFromBlock(BlockPos var1, BlockState var2, boolean var3, boolean var4, Vec3 var5) {
      if (var2.isAir()) {
         return false;
      } else {
         boolean var6 = this.isStateClimbable(var2);
         if ((this.onGround() || var6 || this.isCrouching() && var5.y == 0.0 || this.isOnRails()) && !this.isSwimming()) {
            if (var3) {
               this.walkingStepSound(var1, var2);
            }

            if (var4) {
               this.level().gameEvent(GameEvent.STEP, this.position(), GameEvent.Context.of(this, var2));
            }

            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean isHorizontalCollisionMinor(Vec3 var1) {
      return false;
   }

   protected void playEntityOnFireExtinguishedSound() {
      this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
   }

   public void extinguishFire() {
      if (!this.level().isClientSide && this.isOnFire()) {
         this.playEntityOnFireExtinguishedSound();
      }

      this.clearFire();
   }

   protected void processFlappingMovement() {
      if (this.isFlapping()) {
         this.onFlap();
         if (this.getMovementEmission().emitsEvents()) {
            this.gameEvent(GameEvent.FLAP);
         }
      }
   }

   @Deprecated
   public BlockPos getOnPosLegacy() {
      return this.getOnPos(0.2F);
   }

   public BlockPos getBlockPosBelowThatAffectsMyMovement() {
      return this.getOnPos(0.500001F);
   }

   public BlockPos getOnPos() {
      return this.getOnPos(1.0E-5F);
   }

   protected BlockPos getOnPos(float var1) {
      if (this.mainSupportingBlockPos.isPresent()) {
         BlockPos var5 = this.mainSupportingBlockPos.get();
         if (!(var1 > 1.0E-5F)) {
            return var5;
         } else {
            BlockState var6 = this.level().getBlockState(var5);
            return (!((double)var1 <= 0.5) || !var6.is(BlockTags.FENCES)) && !var6.is(BlockTags.WALLS) && !(var6.getBlock() instanceof FenceGateBlock)
               ? var5.atY(Mth.floor(this.position.y - (double)var1))
               : var5;
         }
      } else {
         int var2 = Mth.floor(this.position.x);
         int var3 = Mth.floor(this.position.y - (double)var1);
         int var4 = Mth.floor(this.position.z);
         return new BlockPos(var2, var3, var4);
      }
   }

   protected float getBlockJumpFactor() {
      float var1 = this.level().getBlockState(this.blockPosition()).getBlock().getJumpFactor();
      float var2 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getJumpFactor();
      return (double)var1 == 1.0 ? var2 : var1;
   }

   protected float getBlockSpeedFactor() {
      BlockState var1 = this.level().getBlockState(this.blockPosition());
      float var2 = var1.getBlock().getSpeedFactor();
      if (!var1.is(Blocks.WATER) && !var1.is(Blocks.BUBBLE_COLUMN)) {
         return (double)var2 == 1.0 ? this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getSpeedFactor() : var2;
      } else {
         return var2;
      }
   }

   protected Vec3 maybeBackOffFromEdge(Vec3 var1, MoverType var2) {
      return var1;
   }

   protected Vec3 limitPistonMovement(Vec3 var1) {
      if (var1.lengthSqr() <= 1.0E-7) {
         return var1;
      } else {
         long var2 = this.level().getGameTime();
         if (var2 != this.pistonDeltasGameTime) {
            Arrays.fill(this.pistonDeltas, 0.0);
            this.pistonDeltasGameTime = var2;
         }

         if (var1.x != 0.0) {
            double var7 = this.applyPistonMovementRestriction(Direction.Axis.X, var1.x);
            return Math.abs(var7) <= 9.999999747378752E-6 ? Vec3.ZERO : new Vec3(var7, 0.0, 0.0);
         } else if (var1.y != 0.0) {
            double var6 = this.applyPistonMovementRestriction(Direction.Axis.Y, var1.y);
            return Math.abs(var6) <= 9.999999747378752E-6 ? Vec3.ZERO : new Vec3(0.0, var6, 0.0);
         } else if (var1.z != 0.0) {
            double var4 = this.applyPistonMovementRestriction(Direction.Axis.Z, var1.z);
            return Math.abs(var4) <= 9.999999747378752E-6 ? Vec3.ZERO : new Vec3(0.0, 0.0, var4);
         } else {
            return Vec3.ZERO;
         }
      }
   }

   private double applyPistonMovementRestriction(Direction.Axis var1, double var2) {
      int var4 = var1.ordinal();
      double var5 = Mth.clamp(var2 + this.pistonDeltas[var4], -0.51, 0.51);
      var2 = var5 - this.pistonDeltas[var4];
      this.pistonDeltas[var4] = var5;
      return var2;
   }

   private Vec3 collide(Vec3 var1) {
      AABB var2 = this.getBoundingBox();
      List var3 = this.level().getEntityCollisions(this, var2.expandTowards(var1));
      Vec3 var4 = var1.lengthSqr() == 0.0 ? var1 : collideBoundingBox(this, var1, var2, this.level(), var3);
      boolean var5 = var1.x != var4.x;
      boolean var6 = var1.y != var4.y;
      boolean var7 = var1.z != var4.z;
      boolean var8 = var6 && var1.y < 0.0;
      if (this.maxUpStep() > 0.0F && (var8 || this.onGround()) && (var5 || var7)) {
         AABB var9 = var8 ? var2.move(0.0, var4.y, 0.0) : var2;
         AABB var10 = var9.expandTowards(var1.x, (double)this.maxUpStep(), var1.z);
         if (!var8) {
            var10 = var10.expandTowards(0.0, -9.999999747378752E-6, 0.0);
         }

         List var11 = collectColliders(this, this.level, var3, var10);
         float var12 = (float)var4.y;
         float[] var13 = collectCandidateStepUpHeights(var9, var11, this.maxUpStep(), var12);

         for (float var17 : var13) {
            Vec3 var18 = collideWithShapes(new Vec3(var1.x, (double)var17, var1.z), var9, var11);
            if (var18.horizontalDistanceSqr() > var4.horizontalDistanceSqr()) {
               double var19 = var2.minY - var9.minY;
               return var18.add(0.0, -var19, 0.0);
            }
         }
      }

      return var4;
   }

   private static float[] collectCandidateStepUpHeights(AABB var0, List<VoxelShape> var1, float var2, float var3) {
      FloatArraySet var4 = new FloatArraySet(4);

      for (VoxelShape var6 : var1) {
         DoubleList var7 = var6.getCoords(Direction.Axis.Y);
         DoubleListIterator var8 = var7.iterator();

         while (var8.hasNext()) {
            double var9 = (Double)var8.next();
            float var11 = (float)(var9 - var0.minY);
            if (!(var11 < 0.0F) && var11 != var3) {
               if (var11 > var2) {
                  break;
               }

               var4.add(var11);
            }
         }
      }

      float[] var12 = var4.toFloatArray();
      FloatArrays.unstableSort(var12);
      return var12;
   }

   public static Vec3 collideBoundingBox(@Nullable Entity var0, Vec3 var1, AABB var2, Level var3, List<VoxelShape> var4) {
      List var5 = collectColliders(var0, var3, var4, var2.expandTowards(var1));
      return collideWithShapes(var1, var2, var5);
   }

   private static List<VoxelShape> collectColliders(@Nullable Entity var0, Level var1, List<VoxelShape> var2, AABB var3) {
      Builder var4 = ImmutableList.builderWithExpectedSize(var2.size() + 1);
      if (!var2.isEmpty()) {
         var4.addAll(var2);
      }

      WorldBorder var5 = var1.getWorldBorder();
      boolean var6 = var0 != null && var5.isInsideCloseToBorder(var0, var3);
      if (var6) {
         var4.add(var5.getCollisionShape());
      }

      var4.addAll(var1.getBlockCollisions(var0, var3));
      return var4.build();
   }

   private static Vec3 collideWithShapes(Vec3 var0, AABB var1, List<VoxelShape> var2) {
      if (var2.isEmpty()) {
         return var0;
      } else {
         double var3 = var0.x;
         double var5 = var0.y;
         double var7 = var0.z;
         if (var5 != 0.0) {
            var5 = Shapes.collide(Direction.Axis.Y, var1, var2, var5);
            if (var5 != 0.0) {
               var1 = var1.move(0.0, var5, 0.0);
            }
         }

         boolean var9 = Math.abs(var3) < Math.abs(var7);
         if (var9 && var7 != 0.0) {
            var7 = Shapes.collide(Direction.Axis.Z, var1, var2, var7);
            if (var7 != 0.0) {
               var1 = var1.move(0.0, 0.0, var7);
            }
         }

         if (var3 != 0.0) {
            var3 = Shapes.collide(Direction.Axis.X, var1, var2, var3);
            if (!var9 && var3 != 0.0) {
               var1 = var1.move(var3, 0.0, 0.0);
            }
         }

         if (!var9 && var7 != 0.0) {
            var7 = Shapes.collide(Direction.Axis.Z, var1, var2, var7);
         }

         return new Vec3(var3, var5, var7);
      }
   }

   protected float nextStep() {
      return (float)((int)this.moveDist + 1);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.GENERIC_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.GENERIC_SPLASH;
   }

   protected SoundEvent getSwimHighSpeedSplashSound() {
      return SoundEvents.GENERIC_SPLASH;
   }

   public void recordMovementThroughBlocks(Vec3 var1, Vec3 var2) {
      this.collectBlockCollidedWith(this.blocksInside, var1, var2);
   }

   private void collectBlockCollidedWith(Map<BlockPos, BlockState> var1, Vec3 var2, Vec3 var3) {
      AABB var4 = this.getBoundingBox().deflate(9.999999747378752E-6);

      for (BlockPos var6 : BlockGetter.boxTraverseBlocks(var2, var3, var4)) {
         if (!this.isAlive()) {
            return;
         }

         BlockState var7 = this.level().getBlockState(var6);
         if (!var7.isAir() && !var1.containsKey(var6)) {
            try {
               VoxelShape var8 = var7.getEntityInsideCollisionShape(this.level(), var6);
               if (var8 == Shapes.block() || this.collidedWithShapeMovingFrom(var2, var3, var6, var8)) {
                  var1.put(var6.immutable(), var7);
               }
            } catch (Throwable var12) {
               CrashReport var9 = CrashReport.forThrowable(var12, "Colliding entity with block");
               CrashReportCategory var10 = var9.addCategory("Block being collided with");
               CrashReportCategory.populateBlockDetails(var10, this.level(), var6, var7);
               CrashReportCategory var11 = var9.addCategory("Entity being checked for collision");
               this.fillCrashReportCategory(var11);
               throw new ReportedException(var9);
            }
         }
      }
   }

   private boolean collidedWithShapeMovingFrom(Vec3 var1, Vec3 var2, BlockPos var3, VoxelShape var4) {
      AABB var5 = this.getBoundingBox().move(this.getBoundingBox().getCenter().scale(-1.0)).move(var2);
      Vec3 var6 = var1.subtract(var5.getBottomCenter());
      return this.getBoundingBox().collidedAlongVector(var6, var4.move(new Vec3(var3)).toAabbs());
   }

   protected void onInsideBlock(BlockState var1) {
   }

   public BlockPos adjustSpawnLocation(ServerLevel var1, BlockPos var2) {
      BlockPos var3 = var1.getSharedSpawnPos();
      Vec3 var4 = var3.getCenter();
      int var5 = var1.getChunkAt(var3).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var3.getX(), var3.getZ()) + 1;
      return BlockPos.containing(var4.x, (double)var5, var4.z);
   }

   public void gameEvent(Holder<GameEvent> var1, @Nullable Entity var2) {
      this.level().gameEvent(var2, var1, this.position);
   }

   public void gameEvent(Holder<GameEvent> var1) {
      this.gameEvent(var1, this);
   }

   private void walkingStepSound(BlockPos var1, BlockState var2) {
      this.playStepSound(var1, var2);
      if (this.shouldPlayAmethystStepSound(var2)) {
         this.playAmethystStepSound();
      }
   }

   protected void waterSwimSound() {
      Entity var1 = Objects.requireNonNullElse(this.getControllingPassenger(), this);
      float var2 = var1 == this ? 0.35F : 0.4F;
      Vec3 var3 = var1.getDeltaMovement();
      float var4 = Math.min(1.0F, (float)Math.sqrt(var3.x * var3.x * 0.20000000298023224 + var3.y * var3.y + var3.z * var3.z * 0.20000000298023224) * var2);
      this.playSwimSound(var4);
   }

   protected BlockPos getPrimaryStepSoundBlockPos(BlockPos var1) {
      BlockPos var2 = var1.above();
      BlockState var3 = this.level().getBlockState(var2);
      return !var3.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) && !var3.is(BlockTags.COMBINATION_STEP_SOUND_BLOCKS) ? var1 : var2;
   }

   protected void playCombinationStepSounds(BlockState var1, BlockState var2) {
      SoundType var3 = var1.getSoundType();
      this.playSound(var3.getStepSound(), var3.getVolume() * 0.15F, var3.getPitch());
      this.playMuffledStepSound(var2);
   }

   protected void playMuffledStepSound(BlockState var1) {
      SoundType var2 = var1.getSoundType();
      this.playSound(var2.getStepSound(), var2.getVolume() * 0.05F, var2.getPitch() * 0.8F);
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      SoundType var3 = var2.getSoundType();
      this.playSound(var3.getStepSound(), var3.getVolume() * 0.15F, var3.getPitch());
   }

   private boolean shouldPlayAmethystStepSound(BlockState var1) {
      return var1.is(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.tickCount >= this.lastCrystalSoundPlayTick + 20;
   }

   private void playAmethystStepSound() {
      this.crystalSoundIntensity = this.crystalSoundIntensity * (float)Math.pow(0.997, (double)(this.tickCount - this.lastCrystalSoundPlayTick));
      this.crystalSoundIntensity = Math.min(1.0F, this.crystalSoundIntensity + 0.07F);
      float var1 = 0.5F + this.crystalSoundIntensity * this.random.nextFloat() * 1.2F;
      float var2 = 0.1F + this.crystalSoundIntensity * 1.2F;
      this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, var2, var1);
      this.lastCrystalSoundPlayTick = this.tickCount;
   }

   protected void playSwimSound(float var1) {
      this.playSound(this.getSwimSound(), var1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
   }

   protected void onFlap() {
   }

   protected boolean isFlapping() {
      return false;
   }

   public void playSound(SoundEvent var1, float var2, float var3) {
      if (!this.isSilent()) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), var1, this.getSoundSource(), var2, var3);
      }
   }

   public void playSound(SoundEvent var1) {
      if (!this.isSilent()) {
         this.playSound(var1, 1.0F, 1.0F);
      }
   }

   public boolean isSilent() {
      return this.entityData.get(DATA_SILENT);
   }

   public void setSilent(boolean var1) {
      this.entityData.set(DATA_SILENT, var1);
   }

   public boolean isNoGravity() {
      return this.entityData.get(DATA_NO_GRAVITY);
   }

   public void setNoGravity(boolean var1) {
      this.entityData.set(DATA_NO_GRAVITY, var1);
   }

   protected double getDefaultGravity() {
      return 0.0;
   }

   public final double getGravity() {
      return this.isNoGravity() ? 0.0 : this.getDefaultGravity();
   }

   protected void applyGravity() {
      double var1 = this.getGravity();
      if (var1 != 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -var1, 0.0));
      }
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.ALL;
   }

   public boolean dampensVibrations() {
      return false;
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
      if (var3) {
         if (this.fallDistance > 0.0F) {
            var4.getBlock().fallOn(this.level(), var4, var5, this, this.fallDistance);
            this.level()
               .gameEvent(
                  GameEvent.HIT_GROUND,
                  this.position,
                  GameEvent.Context.of(this, this.mainSupportingBlockPos.<BlockState>map(var1x -> this.level().getBlockState(var1x)).orElse(var4))
               );
         }

         this.resetFallDistance();
      } else if (var1 < 0.0) {
         this.fallDistance -= (float)var1;
      }
   }

   public boolean fireImmune() {
      return this.getType().fireImmune();
   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      if (this.type.is(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
         return false;
      } else {
         if (this.isVehicle()) {
            for (Entity var5 : this.getPassengers()) {
               var5.causeFallDamage(var1, var2, var3);
            }
         }

         return false;
      }
   }

   public boolean isInWater() {
      return this.wasTouchingWater;
   }

   private boolean isInRain() {
      BlockPos var1 = this.blockPosition();
      return this.level().isRainingAt(var1)
         || this.level().isRainingAt(BlockPos.containing((double)var1.getX(), this.getBoundingBox().maxY, (double)var1.getZ()));
   }

   private boolean isInBubbleColumn() {
      return this.getInBlockState().is(Blocks.BUBBLE_COLUMN);
   }

   public boolean isInWaterOrRain() {
      return this.isInWater() || this.isInRain();
   }

   public boolean isInWaterRainOrBubble() {
      return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
   }

   public boolean isInWaterOrBubble() {
      return this.isInWater() || this.isInBubbleColumn();
   }

   public boolean isInLiquid() {
      return this.isInWaterOrBubble() || this.isInLava();
   }

   public boolean isUnderWater() {
      return this.wasEyeInWater && this.isInWater();
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
      } else {
         this.setSwimming(
            this.isSprinting() && this.isUnderWater() && !this.isPassenger() && this.level().getFluidState(this.blockPosition).is(FluidTags.WATER)
         );
      }
   }

   protected boolean updateInWaterStateAndDoFluidPushing() {
      this.fluidHeight.clear();
      this.updateInWaterStateAndDoWaterCurrentPushing();
      double var1 = this.level().dimensionType().ultraWarm() ? 0.007 : 0.0023333333333333335;
      boolean var3 = this.updateFluidHeightAndDoFluidPushing(FluidTags.LAVA, var1);
      return this.isInWater() || var3;
   }

   void updateInWaterStateAndDoWaterCurrentPushing() {
      if (this.getVehicle() instanceof Boat var1 && !var1.isUnderWater()) {
         this.wasTouchingWater = false;
         return;
      }

      if (this.updateFluidHeightAndDoFluidPushing(FluidTags.WATER, 0.014)) {
         if (!this.wasTouchingWater && !this.firstTick) {
            this.doWaterSplashEffect();
         }

         this.resetFallDistance();
         this.wasTouchingWater = true;
         this.clearFire();
      } else {
         this.wasTouchingWater = false;
      }
   }

   private void updateFluidOnEyes() {
      this.wasEyeInWater = this.isEyeInFluid(FluidTags.WATER);
      this.fluidOnEyes.clear();
      double var1 = this.getEyeY();
      if (this.getVehicle() instanceof Boat var4 && !var4.isUnderWater() && var4.getBoundingBox().maxY >= var1 && var4.getBoundingBox().minY <= var1) {
         return;
      }

      BlockPos var8 = BlockPos.containing(this.getX(), var1, this.getZ());
      FluidState var5 = this.level().getFluidState(var8);
      double var6 = (double)((float)var8.getY() + var5.getHeight(this.level(), var8));
      if (var6 > var1) {
         var5.getTags().forEach(this.fluidOnEyes::add);
      }
   }

   protected void doWaterSplashEffect() {
      Entity var1 = Objects.requireNonNullElse(this.getControllingPassenger(), this);
      float var2 = var1 == this ? 0.2F : 0.9F;
      Vec3 var3 = var1.getDeltaMovement();
      float var4 = Math.min(1.0F, (float)Math.sqrt(var3.x * var3.x * 0.20000000298023224 + var3.y * var3.y + var3.z * var3.z * 0.20000000298023224) * var2);
      if (var4 < 0.25F) {
         this.playSound(this.getSwimSplashSound(), var4, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      } else {
         this.playSound(this.getSwimHighSpeedSplashSound(), var4, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      }

      float var5 = (float)Mth.floor(this.getY());

      for (int var6 = 0; (float)var6 < 1.0F + this.dimensions.width() * 20.0F; var6++) {
         double var7 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
         double var9 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
         this.level()
            .addParticle(
               ParticleTypes.BUBBLE,
               this.getX() + var7,
               (double)(var5 + 1.0F),
               this.getZ() + var9,
               var3.x,
               var3.y - this.random.nextDouble() * 0.20000000298023224,
               var3.z
            );
      }

      for (int var11 = 0; (float)var11 < 1.0F + this.dimensions.width() * 20.0F; var11++) {
         double var12 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
         double var13 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
         this.level().addParticle(ParticleTypes.SPLASH, this.getX() + var12, (double)(var5 + 1.0F), this.getZ() + var13, var3.x, var3.y, var3.z);
      }

      this.gameEvent(GameEvent.SPLASH);
   }

   @Deprecated
   protected BlockState getBlockStateOnLegacy() {
      return this.level().getBlockState(this.getOnPosLegacy());
   }

   public BlockState getBlockStateOn() {
      return this.level().getBlockState(this.getOnPos());
   }

   public boolean canSpawnSprintParticle() {
      return this.isSprinting() && !this.isInWater() && !this.isSpectator() && !this.isCrouching() && !this.isInLava() && this.isAlive();
   }

   protected void spawnSprintParticle() {
      BlockPos var1 = this.getOnPosLegacy();
      BlockState var2 = this.level().getBlockState(var1);
      if (var2.getRenderShape() != RenderShape.INVISIBLE) {
         Vec3 var3 = this.getDeltaMovement();
         BlockPos var4 = this.blockPosition();
         double var5 = this.getX() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width();
         double var7 = this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width();
         if (var4.getX() != var1.getX()) {
            var5 = Mth.clamp(var5, (double)var1.getX(), (double)var1.getX() + 1.0);
         }

         if (var4.getZ() != var1.getZ()) {
            var7 = Mth.clamp(var7, (double)var1.getZ(), (double)var1.getZ() + 1.0);
         }

         this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var2), var5, this.getY() + 0.1, var7, var3.x * -4.0, 1.5, var3.z * -4.0);
      }
   }

   public boolean isEyeInFluid(TagKey<Fluid> var1) {
      return this.fluidOnEyes.contains(var1);
   }

   public boolean isInLava() {
      return !this.firstTick && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0;
   }

   public void moveRelative(float var1, Vec3 var2) {
      Vec3 var3 = getInputVector(var2, var1, this.getYRot());
      this.setDeltaMovement(this.getDeltaMovement().add(var3));
   }

   protected static Vec3 getInputVector(Vec3 var0, float var1, float var2) {
      double var3 = var0.lengthSqr();
      if (var3 < 1.0E-7) {
         return Vec3.ZERO;
      } else {
         Vec3 var5 = (var3 > 1.0 ? var0.normalize() : var0).scale((double)var1);
         float var6 = Mth.sin(var2 * 0.017453292F);
         float var7 = Mth.cos(var2 * 0.017453292F);
         return new Vec3(var5.x * (double)var7 - var5.z * (double)var6, var5.y, var5.z * (double)var7 + var5.x * (double)var6);
      }
   }

   @Deprecated
   public float getLightLevelDependentMagicValue() {
      return this.level().hasChunkAt(this.getBlockX(), this.getBlockZ())
         ? this.level().getLightLevelDependentMagicValue(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ()))
         : 0.0F;
   }

   public void absMoveTo(double var1, double var3, double var5, float var7, float var8) {
      this.absMoveTo(var1, var3, var5);
      this.absRotateTo(var7, var8);
   }

   public void absRotateTo(float var1, float var2) {
      this.setYRot(var1 % 360.0F);
      this.setXRot(Mth.clamp(var2, -90.0F, 90.0F) % 360.0F);
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   public void absMoveTo(double var1, double var3, double var5) {
      double var7 = Mth.clamp(var1, -3.0E7, 3.0E7);
      double var9 = Mth.clamp(var5, -3.0E7, 3.0E7);
      this.xo = var7;
      this.yo = var3;
      this.zo = var9;
      this.setPos(var7, var3, var9);
   }

   public void moveTo(Vec3 var1) {
      this.moveTo(var1.x, var1.y, var1.z);
   }

   public void moveTo(double var1, double var3, double var5) {
      this.moveTo(var1, var3, var5, this.getYRot(), this.getXRot());
   }

   public void moveTo(BlockPos var1, float var2, float var3) {
      this.moveTo(var1.getBottomCenter(), var2, var3);
   }

   public void moveTo(Vec3 var1, float var2, float var3) {
      this.moveTo(var1.x, var1.y, var1.z, var2, var3);
   }

   public void moveTo(double var1, double var3, double var5, float var7, float var8) {
      this.setPosRaw(var1, var3, var5);
      this.setYRot(var7);
      this.setXRot(var8);
      this.setOldPosAndRot();
      this.reapplyPosition();
   }

   public final void setOldPosAndRot() {
      this.setOldPos();
      this.setOldRot();
   }

   public final void setOldPosAndRot(Vec3 var1, float var2, float var3) {
      this.setOldPos(var1);
      this.setOldRot(var2, var3);
   }

   protected void setOldPos() {
      this.setOldPos(this.position);
   }

   protected void setOldRot() {
      this.setOldRot(this.getYRot(), this.getXRot());
   }

   private void setOldPos(Vec3 var1) {
      this.xo = this.xOld = var1.x;
      this.yo = this.yOld = var1.y;
      this.zo = this.zOld = var1.z;
   }

   private void setOldRot(float var1, float var2) {
      this.yRotO = var1;
      this.xRotO = var2;
   }

   public final Vec3 oldPosition() {
      return new Vec3(this.xOld, this.yOld, this.zOld);
   }

   public float distanceTo(Entity var1) {
      float var2 = (float)(this.getX() - var1.getX());
      float var3 = (float)(this.getY() - var1.getY());
      float var4 = (float)(this.getZ() - var1.getZ());
      return Mth.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public double distanceToSqr(double var1, double var3, double var5) {
      double var7 = this.getX() - var1;
      double var9 = this.getY() - var3;
      double var11 = this.getZ() - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double distanceToSqr(Entity var1) {
      return this.distanceToSqr(var1.position());
   }

   public double distanceToSqr(Vec3 var1) {
      double var2 = this.getX() - var1.x;
      double var4 = this.getY() - var1.y;
      double var6 = this.getZ() - var1.z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public void playerTouch(Player var1) {
   }

   public void push(Entity var1) {
      if (!this.isPassengerOfSameVehicle(var1)) {
         if (!var1.noPhysics && !this.noPhysics) {
            double var2 = var1.getX() - this.getX();
            double var4 = var1.getZ() - this.getZ();
            double var6 = Mth.absMax(var2, var4);
            if (var6 >= 0.009999999776482582) {
               var6 = Math.sqrt(var6);
               var2 /= var6;
               var4 /= var6;
               double var8 = 1.0 / var6;
               if (var8 > 1.0) {
                  var8 = 1.0;
               }

               var2 *= var8;
               var4 *= var8;
               var2 *= 0.05000000074505806;
               var4 *= 0.05000000074505806;
               if (!this.isVehicle() && this.isPushable()) {
                  this.push(-var2, 0.0, -var4);
               }

               if (!var1.isVehicle() && var1.isPushable()) {
                  var1.push(var2, 0.0, var4);
               }
            }
         }
      }
   }

   public void push(Vec3 var1) {
      this.push(var1.x, var1.y, var1.z);
   }

   public void push(double var1, double var3, double var5) {
      this.setDeltaMovement(this.getDeltaMovement().add(var1, var3, var5));
      this.hasImpulse = true;
   }

   protected void markHurt() {
      this.hurtMarked = true;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         this.markHurt();
         return false;
      }
   }

   public final Vec3 getViewVector(float var1) {
      return this.calculateViewVector(this.getViewXRot(var1), this.getViewYRot(var1));
   }

   public Direction getNearestViewDirection() {
      return Direction.getApproximateNearest(this.getViewVector(1.0F));
   }

   public float getViewXRot(float var1) {
      return this.getXRot(var1);
   }

   public float getViewYRot(float var1) {
      return this.getYRot(var1);
   }

   public float getXRot(float var1) {
      return var1 == 1.0F ? this.getXRot() : Mth.lerp(var1, this.xRotO, this.getXRot());
   }

   public float getYRot(float var1) {
      return var1 == 1.0F ? this.getYRot() : Mth.rotLerp(var1, this.yRotO, this.getYRot());
   }

   public final Vec3 calculateViewVector(float var1, float var2) {
      float var3 = var1 * 0.017453292F;
      float var4 = -var2 * 0.017453292F;
      float var5 = Mth.cos(var4);
      float var6 = Mth.sin(var4);
      float var7 = Mth.cos(var3);
      float var8 = Mth.sin(var3);
      return new Vec3((double)(var6 * var7), (double)(-var8), (double)(var5 * var7));
   }

   public final Vec3 getUpVector(float var1) {
      return this.calculateUpVector(this.getViewXRot(var1), this.getViewYRot(var1));
   }

   protected final Vec3 calculateUpVector(float var1, float var2) {
      return this.calculateViewVector(var1 - 90.0F, var2);
   }

   public final Vec3 getEyePosition() {
      return new Vec3(this.getX(), this.getEyeY(), this.getZ());
   }

   public final Vec3 getEyePosition(float var1) {
      double var2 = Mth.lerp((double)var1, this.xo, this.getX());
      double var4 = Mth.lerp((double)var1, this.yo, this.getY()) + (double)this.getEyeHeight();
      double var6 = Mth.lerp((double)var1, this.zo, this.getZ());
      return new Vec3(var2, var4, var6);
   }

   public Vec3 getLightProbePosition(float var1) {
      return this.getEyePosition(var1);
   }

   public final Vec3 getPosition(float var1) {
      double var2 = Mth.lerp((double)var1, this.xo, this.getX());
      double var4 = Mth.lerp((double)var1, this.yo, this.getY());
      double var6 = Mth.lerp((double)var1, this.zo, this.getZ());
      return new Vec3(var2, var4, var6);
   }

   public HitResult pick(double var1, float var3, boolean var4) {
      Vec3 var5 = this.getEyePosition(var3);
      Vec3 var6 = this.getViewVector(var3);
      Vec3 var7 = var5.add(var6.x * var1, var6.y * var1, var6.z * var1);
      return this.level().clip(new ClipContext(var5, var7, ClipContext.Block.OUTLINE, var4 ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
   }

   public boolean canBeHitByProjectile() {
      return this.isAlive() && this.isPickable();
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public void awardKillScore(Entity var1, int var2, DamageSource var3) {
      if (var1 instanceof ServerPlayer) {
         CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayer)var1, this, var3);
      }
   }

   public boolean shouldRender(double var1, double var3, double var5) {
      double var7 = this.getX() - var1;
      double var9 = this.getY() - var3;
      double var11 = this.getZ() - var5;
      double var13 = var7 * var7 + var9 * var9 + var11 * var11;
      return this.shouldRenderAtSqrDistance(var13);
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize();
      if (Double.isNaN(var3)) {
         var3 = 1.0;
      }

      var3 *= 64.0 * viewScale;
      return var1 < var3 * var3;
   }

   public boolean saveAsPassenger(CompoundTag var1) {
      if (this.removalReason != null && !this.removalReason.shouldSave()) {
         return false;
      } else {
         String var2 = this.getEncodeId();
         if (var2 == null) {
            return false;
         } else {
            var1.putString("id", var2);
            this.saveWithoutId(var1);
            return true;
         }
      }
   }

   public boolean save(CompoundTag var1) {
      return this.isPassenger() ? false : this.saveAsPassenger(var1);
   }

   public CompoundTag saveWithoutId(CompoundTag var1) {
      try {
         if (this.vehicle != null) {
            var1.put("Pos", this.newDoubleList(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
         } else {
            var1.put("Pos", this.newDoubleList(this.getX(), this.getY(), this.getZ()));
         }

         Vec3 var2 = this.getDeltaMovement();
         var1.put("Motion", this.newDoubleList(var2.x, var2.y, var2.z));
         var1.put("Rotation", this.newFloatList(this.getYRot(), this.getXRot()));
         var1.putFloat("FallDistance", this.fallDistance);
         var1.putShort("Fire", (short)this.remainingFireTicks);
         var1.putShort("Air", (short)this.getAirSupply());
         var1.putBoolean("OnGround", this.onGround());
         var1.putBoolean("Invulnerable", this.invulnerable);
         var1.putInt("PortalCooldown", this.portalCooldown);
         var1.putUUID("UUID", this.getUUID());
         Component var10 = this.getCustomName();
         if (var10 != null) {
            var1.putString("CustomName", Component.Serializer.toJson(var10, this.registryAccess()));
         }

         if (this.isCustomNameVisible()) {
            var1.putBoolean("CustomNameVisible", this.isCustomNameVisible());
         }

         if (this.isSilent()) {
            var1.putBoolean("Silent", this.isSilent());
         }

         if (this.isNoGravity()) {
            var1.putBoolean("NoGravity", this.isNoGravity());
         }

         if (this.hasGlowingTag) {
            var1.putBoolean("Glowing", true);
         }

         int var11 = this.getTicksFrozen();
         if (var11 > 0) {
            var1.putInt("TicksFrozen", this.getTicksFrozen());
         }

         if (this.hasVisualFire) {
            var1.putBoolean("HasVisualFire", this.hasVisualFire);
         }

         if (!this.tags.isEmpty()) {
            ListTag var5 = new ListTag();

            for (String var7 : this.tags) {
               var5.add(StringTag.valueOf(var7));
            }

            var1.put("Tags", var5);
         }

         this.addAdditionalSaveData(var1);
         if (this.isVehicle()) {
            ListTag var12 = new ListTag();

            for (Entity var14 : this.getPassengers()) {
               CompoundTag var8 = new CompoundTag();
               if (var14.saveAsPassenger(var8)) {
                  var12.add(var8);
               }
            }

            if (!var12.isEmpty()) {
               var1.put("Passengers", var12);
            }
         }

         return var1;
      } catch (Throwable var9) {
         CrashReport var3 = CrashReport.forThrowable(var9, "Saving entity NBT");
         CrashReportCategory var4 = var3.addCategory("Entity being saved");
         this.fillCrashReportCategory(var4);
         throw new ReportedException(var3);
      }
   }

   public void load(CompoundTag var1) {
      try {
         ListTag var2 = var1.getList("Pos", 6);
         ListTag var18 = var1.getList("Motion", 6);
         ListTag var19 = var1.getList("Rotation", 5);
         double var5 = var18.getDouble(0);
         double var7 = var18.getDouble(1);
         double var9 = var18.getDouble(2);
         this.setDeltaMovement(Math.abs(var5) > 10.0 ? 0.0 : var5, Math.abs(var7) > 10.0 ? 0.0 : var7, Math.abs(var9) > 10.0 ? 0.0 : var9);
         this.hasImpulse = true;
         double var11 = 3.0000512E7;
         this.setPosRaw(
            Mth.clamp(var2.getDouble(0), -3.0000512E7, 3.0000512E7),
            Mth.clamp(var2.getDouble(1), -2.0E7, 2.0E7),
            Mth.clamp(var2.getDouble(2), -3.0000512E7, 3.0000512E7)
         );
         this.setYRot(var19.getFloat(0));
         this.setXRot(var19.getFloat(1));
         this.setOldPosAndRot();
         this.setYHeadRot(this.getYRot());
         this.setYBodyRot(this.getYRot());
         this.fallDistance = var1.getFloat("FallDistance");
         this.remainingFireTicks = var1.getShort("Fire");
         if (var1.contains("Air")) {
            this.setAirSupply(var1.getShort("Air"));
         }

         this.onGround = var1.getBoolean("OnGround");
         this.invulnerable = var1.getBoolean("Invulnerable");
         this.portalCooldown = var1.getInt("PortalCooldown");
         if (var1.hasUUID("UUID")) {
            this.uuid = var1.getUUID("UUID");
            this.stringUUID = this.uuid.toString();
         }

         if (!Double.isFinite(this.getX()) || !Double.isFinite(this.getY()) || !Double.isFinite(this.getZ())) {
            throw new IllegalStateException("Entity has invalid position");
         } else if (Double.isFinite((double)this.getYRot()) && Double.isFinite((double)this.getXRot())) {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
            if (var1.contains("CustomName", 8)) {
               String var13 = var1.getString("CustomName");

               try {
                  this.setCustomName(Component.Serializer.fromJson(var13, this.registryAccess()));
               } catch (Exception var16) {
                  LOGGER.warn("Failed to parse entity custom name {}", var13, var16);
               }
            }

            this.setCustomNameVisible(var1.getBoolean("CustomNameVisible"));
            this.setSilent(var1.getBoolean("Silent"));
            this.setNoGravity(var1.getBoolean("NoGravity"));
            this.setGlowingTag(var1.getBoolean("Glowing"));
            this.setTicksFrozen(var1.getInt("TicksFrozen"));
            this.hasVisualFire = var1.getBoolean("HasVisualFire");
            if (var1.contains("Tags", 9)) {
               this.tags.clear();
               ListTag var20 = var1.getList("Tags", 8);
               int var14 = Math.min(var20.size(), 1024);

               for (int var15 = 0; var15 < var14; var15++) {
                  this.tags.add(var20.getString(var15));
               }
            }

            this.readAdditionalSaveData(var1);
            if (this.repositionEntityAfterLoad()) {
               this.reapplyPosition();
            }
         } else {
            throw new IllegalStateException("Entity has invalid rotation");
         }
      } catch (Throwable var17) {
         CrashReport var3 = CrashReport.forThrowable(var17, "Loading entity NBT");
         CrashReportCategory var4 = var3.addCategory("Entity being loaded");
         this.fillCrashReportCategory(var4);
         throw new ReportedException(var3);
      }
   }

   protected boolean repositionEntityAfterLoad() {
      return true;
   }

   @Nullable
   protected final String getEncodeId() {
      EntityType var1 = this.getType();
      ResourceLocation var2 = EntityType.getKey(var1);
      return var1.canSerialize() && var2 != null ? var2.toString() : null;
   }

   protected abstract void readAdditionalSaveData(CompoundTag var1);

   protected abstract void addAdditionalSaveData(CompoundTag var1);

   protected ListTag newDoubleList(double... var1) {
      ListTag var2 = new ListTag();

      for (double var6 : var1) {
         var2.add(DoubleTag.valueOf(var6));
      }

      return var2;
   }

   protected ListTag newFloatList(float... var1) {
      ListTag var2 = new ListTag();

      for (float var6 : var1) {
         var2.add(FloatTag.valueOf(var6));
      }

      return var2;
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemLike var1) {
      return this.spawnAtLocation(var1, 0);
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemLike var1, int var2) {
      return this.spawnAtLocation(new ItemStack(var1), (float)var2);
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemStack var1) {
      return this.spawnAtLocation(var1, 0.0F);
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemStack var1, float var2) {
      if (var1.isEmpty()) {
         return null;
      } else if (this.level().isClientSide) {
         return null;
      } else {
         ItemEntity var3 = new ItemEntity(this.level(), this.getX(), this.getY() + (double)var2, this.getZ(), var1);
         var3.setDefaultPickUpDelay();
         this.level().addFreshEntity(var3);
         return var3;
      }
   }

   public boolean isAlive() {
      return !this.isRemoved();
   }

   public boolean isInWall() {
      if (this.noPhysics) {
         return false;
      } else {
         float var1 = this.dimensions.width() * 0.8F;
         AABB var2 = AABB.ofSize(this.getEyePosition(), (double)var1, 1.0E-6, (double)var1);
         return BlockPos.betweenClosedStream(var2)
            .anyMatch(
               var2x -> {
                  BlockState var3 = this.level().getBlockState(var2x);
                  return !var3.isAir()
                     && var3.isSuffocating(this.level(), var2x)
                     && Shapes.joinIsNotEmpty(
                        var3.getCollisionShape(this.level(), var2x).move((double)var2x.getX(), (double)var2x.getY(), (double)var2x.getZ()),
                        Shapes.create(var2),
                        BooleanOp.AND
                     );
               }
            );
      }
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (this.isAlive() && this instanceof Leashable var3) {
         if (var3.getLeashHolder() == var1) {
            if (!this.level().isClientSide()) {
               var3.dropLeash(true, !var1.hasInfiniteMaterials());
               this.gameEvent(GameEvent.ENTITY_INTERACT, var1);
            }

            return InteractionResult.SUCCESS;
         }

         ItemStack var4 = var1.getItemInHand(var2);
         if (var4.is(Items.LEAD) && var3.canHaveALeashAttachedToIt()) {
            if (!this.level().isClientSide()) {
               var3.setLeashedTo(var1, true);
            }

            var4.shrink(1);
            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.PASS;
   }

   public boolean canCollideWith(Entity var1) {
      return var1.canBeCollidedWith() && !this.isPassengerOfSameVehicle(var1);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public void rideTick() {
      this.setDeltaMovement(Vec3.ZERO);
      this.tick();
      if (this.isPassenger()) {
         this.getVehicle().positionRider(this);
      }
   }

   public final void positionRider(Entity var1) {
      if (this.hasPassenger(var1)) {
         this.positionRider(var1, Entity::setPos);
      }
   }

   protected void positionRider(Entity var1, Entity.MoveFunction var2) {
      Vec3 var3 = this.getPassengerRidingPosition(var1);
      Vec3 var4 = var1.getVehicleAttachmentPoint(this);
      var2.accept(var1, var3.x - var4.x, var3.y - var4.y, var3.z - var4.z);
   }

   public void onPassengerTurned(Entity var1) {
   }

   public Vec3 getVehicleAttachmentPoint(Entity var1) {
      return this.getAttachments().get(EntityAttachment.VEHICLE, 0, this.yRot);
   }

   public Vec3 getPassengerRidingPosition(Entity var1) {
      return this.position().add(this.getPassengerAttachmentPoint(var1, this.dimensions, 1.0F));
   }

   protected Vec3 getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      return getDefaultPassengerAttachmentPoint(this, var1, var2.attachments());
   }

   protected static Vec3 getDefaultPassengerAttachmentPoint(Entity var0, Entity var1, EntityAttachments var2) {
      int var3 = var0.getPassengers().indexOf(var1);
      return var2.getClamped(EntityAttachment.PASSENGER, var3, var0.yRot);
   }

   public boolean startRiding(Entity var1) {
      return this.startRiding(var1, false);
   }

   public boolean showVehicleHealth() {
      return this instanceof LivingEntity;
   }

   public boolean startRiding(Entity var1, boolean var2) {
      if (var1 == this.vehicle) {
         return false;
      } else if (!var1.couldAcceptPassenger()) {
         return false;
      } else {
         for (Entity var3 = var1; var3.vehicle != null; var3 = var3.vehicle) {
            if (var3.vehicle == this) {
               return false;
            }
         }

         if (var2 || this.canRide(var1) && var1.canAddPassenger(this)) {
            if (this.isPassenger()) {
               this.stopRiding();
            }

            this.setPose(Pose.STANDING);
            this.vehicle = var1;
            this.vehicle.addPassenger(this);
            var1.getIndirectPassengersStream()
               .filter(var0 -> var0 instanceof ServerPlayer)
               .forEach(var0 -> CriteriaTriggers.START_RIDING_TRIGGER.trigger((ServerPlayer)var0));
            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean canRide(Entity var1) {
      return !this.isShiftKeyDown() && this.boardingCooldown <= 0;
   }

   public void ejectPassengers() {
      for (int var1 = this.passengers.size() - 1; var1 >= 0; var1--) {
         ((Entity)this.passengers.get(var1)).stopRiding();
      }
   }

   public void removeVehicle() {
      if (this.vehicle != null) {
         Entity var1 = this.vehicle;
         this.vehicle = null;
         var1.removePassenger(this);
      }
   }

   public void stopRiding() {
      this.removeVehicle();
   }

   protected void addPassenger(Entity var1) {
      if (var1.getVehicle() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (this.passengers.isEmpty()) {
            this.passengers = ImmutableList.of(var1);
         } else {
            ArrayList var2 = Lists.newArrayList(this.passengers);
            if (!this.level().isClientSide && var1 instanceof Player && !(this.getFirstPassenger() instanceof Player)) {
               var2.add(0, var1);
            } else {
               var2.add(var1);
            }

            this.passengers = ImmutableList.copyOf(var2);
         }

         this.gameEvent(GameEvent.ENTITY_MOUNT, var1);
      }
   }

   protected void removePassenger(Entity var1) {
      if (var1.getVehicle() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         if (this.passengers.size() == 1 && this.passengers.get(0) == var1) {
            this.passengers = ImmutableList.of();
         } else {
            this.passengers = this.passengers.stream().filter(var1x -> var1x != var1).collect(ImmutableList.toImmutableList());
         }

         var1.boardingCooldown = 60;
         this.gameEvent(GameEvent.ENTITY_DISMOUNT, var1);
      }
   }

   protected boolean canAddPassenger(Entity var1) {
      return this.passengers.isEmpty();
   }

   protected boolean couldAcceptPassenger() {
      return true;
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.setPos(var1, var3, var5);
      this.setRot(var7, var8);
   }

   public double lerpTargetX() {
      return this.getX();
   }

   public double lerpTargetY() {
      return this.getY();
   }

   public double lerpTargetZ() {
      return this.getZ();
   }

   public float lerpTargetXRot() {
      return this.getXRot();
   }

   public float lerpTargetYRot() {
      return this.getYRot();
   }

   public void lerpHeadTo(float var1, int var2) {
      this.setYHeadRot(var1);
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public Vec3 getLookAngle() {
      return this.calculateViewVector(this.getXRot(), this.getYRot());
   }

   public Vec3 getHandHoldingItemAngle(Item var1) {
      if (!(this instanceof Player var2)) {
         return Vec3.ZERO;
      } else {
         boolean var3 = var2.getOffhandItem().is(var1) && !var2.getMainHandItem().is(var1);
         HumanoidArm var4 = var3 ? var2.getMainArm().getOpposite() : var2.getMainArm();
         return this.calculateViewVector(0.0F, this.getYRot() + (float)(var4 == HumanoidArm.RIGHT ? 80 : -80)).scale(0.5);
      }
   }

   public Vec2 getRotationVector() {
      return new Vec2(this.getXRot(), this.getYRot());
   }

   public Vec3 getForward() {
      return Vec3.directionFromRotation(this.getRotationVector());
   }

   public void setAsInsidePortal(Portal var1, BlockPos var2) {
      if (this.isOnPortalCooldown()) {
         this.setPortalCooldown();
      } else {
         if (this.portalProcess == null || !this.portalProcess.isSamePortal(var1)) {
            this.portalProcess = new PortalProcessor(var1, var2.immutable());
         } else if (!this.portalProcess.isInsidePortalThisTick()) {
            this.portalProcess.updateEntryPosition(var2.immutable());
            this.portalProcess.setAsInsidePortalThisTick(true);
         }
      }
   }

   protected void handlePortal() {
      if (this.level() instanceof ServerLevel var1) {
         this.processPortalCooldown();
         if (this.portalProcess != null) {
            if (this.portalProcess.processPortalTeleportation(var1, this, this.canUsePortal(false))) {
               ProfilerFiller var5 = Profiler.get();
               var5.push("portal");
               this.setPortalCooldown();
               DimensionTransition var3 = this.portalProcess.getPortalDestination(var1, this);
               if (var3 != null) {
                  ServerLevel var4 = var3.newLevel();
                  if (var1.getServer().isLevelEnabled(var4) && (var4.dimension() == var1.dimension() || this.canChangeDimensions(var1, var4))) {
                     this.changeDimension(var3);
                  }
               }

               var5.pop();
            } else if (this.portalProcess.hasExpired()) {
               this.portalProcess = null;
            }
         }
      }
   }

   public int getDimensionChangingDelay() {
      Entity var1 = this.getFirstPassenger();
      return var1 instanceof ServerPlayer ? var1.getDimensionChangingDelay() : 300;
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
   }

   public void handleDamageEvent(DamageSource var1) {
   }

   public void handleEntityEvent(byte var1) {
      switch (var1) {
         case 53:
            HoneyBlock.showSlideParticles(this);
      }
   }

   public void animateHurt(float var1) {
   }

   public boolean isOnFire() {
      boolean var1 = this.level() != null && this.level().isClientSide;
      return !this.fireImmune() && (this.remainingFireTicks > 0 || var1 && this.getSharedFlag(0));
   }

   public boolean isPassenger() {
      return this.getVehicle() != null;
   }

   public boolean isVehicle() {
      return !this.passengers.isEmpty();
   }

   public boolean dismountsUnderwater() {
      return this.getType().is(EntityTypeTags.DISMOUNTS_UNDERWATER);
   }

   public boolean canControlVehicle() {
      return !this.getType().is(EntityTypeTags.NON_CONTROLLING_RIDER);
   }

   public void setShiftKeyDown(boolean var1) {
      this.setSharedFlag(1, var1);
   }

   public boolean isShiftKeyDown() {
      return this.getSharedFlag(1);
   }

   public boolean isSteppingCarefully() {
      return this.isShiftKeyDown();
   }

   public boolean isSuppressingBounce() {
      return this.isShiftKeyDown();
   }

   public boolean isDiscrete() {
      return this.isShiftKeyDown();
   }

   public boolean isDescending() {
      return this.isShiftKeyDown();
   }

   public boolean isCrouching() {
      return this.hasPose(Pose.CROUCHING);
   }

   public boolean isSprinting() {
      return this.getSharedFlag(3);
   }

   public void setSprinting(boolean var1) {
      this.setSharedFlag(3, var1);
   }

   public boolean isSwimming() {
      return this.getSharedFlag(4);
   }

   public boolean isVisuallySwimming() {
      return this.hasPose(Pose.SWIMMING);
   }

   public boolean isVisuallyCrawling() {
      return this.isVisuallySwimming() && !this.isInWater();
   }

   public void setSwimming(boolean var1) {
      this.setSharedFlag(4, var1);
   }

   public final boolean hasGlowingTag() {
      return this.hasGlowingTag;
   }

   public final void setGlowingTag(boolean var1) {
      this.hasGlowingTag = var1;
      this.setSharedFlag(6, this.isCurrentlyGlowing());
   }

   public boolean isCurrentlyGlowing() {
      return this.level().isClientSide() ? this.getSharedFlag(6) : this.hasGlowingTag;
   }

   public boolean isInvisible() {
      return this.getSharedFlag(5);
   }

   public boolean isInvisibleTo(Player var1) {
      if (var1.isSpectator()) {
         return false;
      } else {
         PlayerTeam var2 = this.getTeam();
         return var2 != null && var1 != null && var1.getTeam() == var2 && var2.canSeeFriendlyInvisibles() ? false : this.isInvisible();
      }
   }

   public boolean isOnRails() {
      return false;
   }

   public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> var1) {
   }

   @Nullable
   public PlayerTeam getTeam() {
      return this.level().getScoreboard().getPlayersTeam(this.getScoreboardName());
   }

   public final boolean isAlliedTo(@Nullable Entity var1) {
      return var1 == null ? false : this == var1 || this.considersEntityAsAlly(var1) || var1.considersEntityAsAlly(this);
   }

   protected boolean considersEntityAsAlly(Entity var1) {
      return this.isAlliedTo(var1.getTeam());
   }

   public boolean isAlliedTo(@Nullable Team var1) {
      return this.getTeam() != null ? this.getTeam().isAlliedTo(var1) : false;
   }

   public void setInvisible(boolean var1) {
      this.setSharedFlag(5, var1);
   }

   protected boolean getSharedFlag(int var1) {
      return (this.entityData.get(DATA_SHARED_FLAGS_ID) & 1 << var1) != 0;
   }

   protected void setSharedFlag(int var1, boolean var2) {
      byte var3 = this.entityData.get(DATA_SHARED_FLAGS_ID);
      if (var2) {
         this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(var3 | 1 << var1));
      } else {
         this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(var3 & ~(1 << var1)));
      }
   }

   public int getMaxAirSupply() {
      return 300;
   }

   public int getAirSupply() {
      return this.entityData.get(DATA_AIR_SUPPLY_ID);
   }

   public void setAirSupply(int var1) {
      this.entityData.set(DATA_AIR_SUPPLY_ID, var1);
   }

   public int getTicksFrozen() {
      return this.entityData.get(DATA_TICKS_FROZEN);
   }

   public void setTicksFrozen(int var1) {
      this.entityData.set(DATA_TICKS_FROZEN, var1);
   }

   public float getPercentFrozen() {
      int var1 = this.getTicksRequiredToFreeze();
      return (float)Math.min(this.getTicksFrozen(), var1) / (float)var1;
   }

   public boolean isFullyFrozen() {
      return this.getTicksFrozen() >= this.getTicksRequiredToFreeze();
   }

   public int getTicksRequiredToFreeze() {
      return 140;
   }

   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      this.setRemainingFireTicks(this.remainingFireTicks + 1);
      if (this.remainingFireTicks == 0) {
         this.igniteForSeconds(8.0F);
      }

      this.hurt(this.damageSources().lightningBolt(), 5.0F);
   }

   public void onAboveBubbleCol(boolean var1) {
      Vec3 var2 = this.getDeltaMovement();
      double var3;
      if (var1) {
         var3 = Math.max(-0.9, var2.y - 0.03);
      } else {
         var3 = Math.min(1.8, var2.y + 0.1);
      }

      this.setDeltaMovement(var2.x, var3, var2.z);
   }

   public void onInsideBubbleColumn(boolean var1) {
      Vec3 var2 = this.getDeltaMovement();
      double var3;
      if (var1) {
         var3 = Math.max(-0.3, var2.y - 0.03);
      } else {
         var3 = Math.min(0.7, var2.y + 0.06);
      }

      this.setDeltaMovement(var2.x, var3, var2.z);
      this.resetFallDistance();
   }

   public boolean killedEntity(ServerLevel var1, LivingEntity var2) {
      return true;
   }

   public void checkSlowFallDistance() {
      if (this.getDeltaMovement().y() > -0.5 && this.fallDistance > 1.0F) {
         this.fallDistance = 1.0F;
      }
   }

   public void resetFallDistance() {
      this.fallDistance = 0.0F;
   }

   protected void moveTowardsClosestSpace(double var1, double var3, double var5) {
      BlockPos var7 = BlockPos.containing(var1, var3, var5);
      Vec3 var8 = new Vec3(var1 - (double)var7.getX(), var3 - (double)var7.getY(), var5 - (double)var7.getZ());
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
      Direction var10 = Direction.UP;
      double var11 = 1.7976931348623157E308;

      for (Direction var16 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
         var9.setWithOffset(var7, var16);
         if (!this.level().getBlockState(var9).isCollisionShapeFullBlock(this.level(), var9)) {
            double var17 = var8.get(var16.getAxis());
            double var19 = var16.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - var17 : var17;
            if (var19 < var11) {
               var11 = var19;
               var10 = var16;
            }
         }
      }

      float var21 = this.random.nextFloat() * 0.2F + 0.1F;
      float var22 = (float)var10.getAxisDirection().getStep();
      Vec3 var23 = this.getDeltaMovement().scale(0.75);
      if (var10.getAxis() == Direction.Axis.X) {
         this.setDeltaMovement((double)(var22 * var21), var23.y, var23.z);
      } else if (var10.getAxis() == Direction.Axis.Y) {
         this.setDeltaMovement(var23.x, (double)(var22 * var21), var23.z);
      } else if (var10.getAxis() == Direction.Axis.Z) {
         this.setDeltaMovement(var23.x, var23.y, (double)(var22 * var21));
      }
   }

   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
      this.resetFallDistance();
      this.stuckSpeedMultiplier = var2;
   }

   private static Component removeAction(Component var0) {
      MutableComponent var1 = var0.plainCopy().setStyle(var0.getStyle().withClickEvent(null));

      for (Component var3 : var0.getSiblings()) {
         var1.append(removeAction(var3));
      }

      return var1;
   }

   @Override
   public Component getName() {
      Component var1 = this.getCustomName();
      return var1 != null ? removeAction(var1) : this.getTypeName();
   }

   protected Component getTypeName() {
      return this.type.getDescription();
   }

   public boolean is(Entity var1) {
      return this == var1;
   }

   public float getYHeadRot() {
      return 0.0F;
   }

   public void setYHeadRot(float var1) {
   }

   public void setYBodyRot(float var1) {
   }

   public boolean isAttackable() {
      return true;
   }

   public boolean skipAttackInteraction(Entity var1) {
      return false;
   }

   @Override
   public String toString() {
      String var1 = this.level() == null ? "~NULL~" : this.level().toString();
      return this.removalReason != null
         ? String.format(
            Locale.ROOT,
            "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, removed=%s]",
            this.getClass().getSimpleName(),
            this.getName().getString(),
            this.id,
            var1,
            this.getX(),
            this.getY(),
            this.getZ(),
            this.removalReason
         )
         : String.format(
            Locale.ROOT,
            "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
            this.getClass().getSimpleName(),
            this.getName().getString(),
            this.id,
            var1,
            this.getX(),
            this.getY(),
            this.getZ()
         );
   }

   public boolean isInvulnerableTo(DamageSource var1) {
      return this.isRemoved()
         || this.invulnerable && !var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !var1.isCreativePlayer()
         || var1.is(DamageTypeTags.IS_FIRE) && this.fireImmune()
         || var1.is(DamageTypeTags.IS_FALL) && this.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean var1) {
      this.invulnerable = var1;
   }

   public void copyPosition(Entity var1) {
      this.moveTo(var1.getX(), var1.getY(), var1.getZ(), var1.getYRot(), var1.getXRot());
   }

   public void restoreFrom(Entity var1) {
      CompoundTag var2 = var1.saveWithoutId(new CompoundTag());
      var2.remove("Dimension");
      this.load(var2);
      this.portalCooldown = var1.portalCooldown;
      this.portalProcess = var1.portalProcess;
   }

   @Nullable
   public Entity changeDimension(DimensionTransition var1) {
      if (this.level() instanceof ServerLevel var2 && !this.isRemoved()) {
         ServerLevel var13 = var1.newLevel();
         List var4 = this.getPassengers();
         this.unRide();
         ArrayList var5 = new ArrayList();

         for (Entity var7 : var4) {
            float var8 = var7.getYRot() - this.getYRot();
            float var9 = var7.getXRot() - this.getXRot();
            float var10 = var1.yRot() + (var1.relatives().contains(Relative.Y_ROT) ? 0.0F : var8);
            float var11 = var1.xRot() + (var1.relatives().contains(Relative.X_ROT) ? 0.0F : var9);
            Entity var12 = var7.changeDimension(var1.withRotation(var10, var11));
            if (var12 != null) {
               var5.add(var12);
            }
         }

         ProfilerFiller var14 = Profiler.get();
         var14.push("changeDimension");
         Entity var15 = var13.dimension() == var2.dimension() ? this : this.getType().create(var13, EntitySpawnReason.DIMENSION_TRAVEL);
         if (var15 != null) {
            if (this != var15) {
               var15.restoreFrom(this);
               this.removeAfterChangingDimensions();
            }

            var15.teleportSetPosition(var1);
            if (this != var15) {
               var13.addDuringTeleport(var15);
            }

            for (Entity var17 : var5) {
               var17.startRiding(var15, true);
            }

            var2.resetEmptyTime();
            var13.resetEmptyTime();
            var1.postDimensionTransition().onTransition(var15);
         }

         var14.pop();
         return var15;
      }

      return null;
   }

   protected void teleportSetPosition(DimensionTransition var1) {
      PositionMoveRotation var2 = PositionMoveRotation.of(var1);
      PositionMoveRotation var3 = PositionMoveRotation.calculateAbsolute(PositionMoveRotation.of(this), var2, var1.relatives());
      this.setPosRaw(var3.position().x, var3.position().y, var3.position().z);
      this.setYRot(var3.yRot());
      this.setYHeadRot(var3.yRot());
      this.setXRot(var3.xRot());
      this.reapplyPosition();
      this.setOldPosAndRot();
      this.setDeltaMovement(var3.deltaMovement());
      this.blocksInside.clear();
   }

   public void placePortalTicket(BlockPos var1) {
      if (this.level() instanceof ServerLevel var2) {
         var2.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(var1), 3, var1);
      }
   }

   protected void removeAfterChangingDimensions() {
      this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
      if (this instanceof Leashable var1) {
         var1.dropLeash(true, false);
      }
   }

   public Vec3 getRelativePortalPosition(Direction.Axis var1, BlockUtil.FoundRectangle var2) {
      return PortalShape.getRelativePosition(var2, var1, this.position(), this.getDimensions(this.getPose()));
   }

   public boolean canUsePortal(boolean var1) {
      return (var1 || !this.isPassenger()) && this.isAlive();
   }

   public boolean canChangeDimensions(Level var1, Level var2) {
      if (var1.dimension() == Level.END && var2.dimension() == Level.OVERWORLD) {
         for (Entity var4 : this.getPassengers()) {
            if (var4 instanceof ServerPlayer var5 && !var5.seenCredits) {
               return false;
            }
         }
      }

      return true;
   }

   public float getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5, float var6) {
      return var6;
   }

   public boolean shouldBlockExplode(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, float var5) {
      return true;
   }

   public int getMaxFallDistance() {
      return 3;
   }

   public boolean isIgnoringBlockTriggers() {
      return false;
   }

   public void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("Entity Type", () -> EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
      var1.setDetail("Entity ID", this.id);
      var1.setDetail("Entity Name", () -> this.getName().getString());
      var1.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
      var1.setDetail(
         "Entity's Block location", CrashReportCategory.formatLocation(this.level(), Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()))
      );
      Vec3 var2 = this.getDeltaMovement();
      var1.setDetail("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", var2.x, var2.y, var2.z));
      var1.setDetail("Entity's Passengers", () -> this.getPassengers().toString());
      var1.setDetail("Entity's Vehicle", () -> String.valueOf(this.getVehicle()));
   }

   public boolean displayFireAnimation() {
      return this.isOnFire() && !this.isSpectator();
   }

   public void setUUID(UUID var1) {
      this.uuid = var1;
      this.stringUUID = this.uuid.toString();
   }

   @Override
   public UUID getUUID() {
      return this.uuid;
   }

   public String getStringUUID() {
      return this.stringUUID;
   }

   @Override
   public String getScoreboardName() {
      return this.stringUUID;
   }

   public boolean isPushedByFluid() {
      return true;
   }

   public static double getViewScale() {
      return viewScale;
   }

   public static void setViewScale(double var0) {
      viewScale = var0;
   }

   @Override
   public Component getDisplayName() {
      return PlayerTeam.formatNameForTeam(this.getTeam(), this.getName())
         .withStyle(var1 -> var1.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID()));
   }

   public void setCustomName(@Nullable Component var1) {
      this.entityData.set(DATA_CUSTOM_NAME, Optional.ofNullable(var1));
   }

   @Nullable
   @Override
   public Component getCustomName() {
      return this.entityData.get(DATA_CUSTOM_NAME).orElse(null);
   }

   @Override
   public boolean hasCustomName() {
      return this.entityData.get(DATA_CUSTOM_NAME).isPresent();
   }

   public void setCustomNameVisible(boolean var1) {
      this.entityData.set(DATA_CUSTOM_NAME_VISIBLE, var1);
   }

   public boolean isCustomNameVisible() {
      return this.entityData.get(DATA_CUSTOM_NAME_VISIBLE);
   }

   public boolean teleportTo(ServerLevel var1, double var2, double var4, double var6, Set<Relative> var8, float var9, float var10, boolean var11) {
      float var12 = Mth.clamp(var10, -90.0F, 90.0F);
      Entity var13 = this.changeDimension(
         new DimensionTransition(var1, new Vec3(var2, var4, var6), Vec3.ZERO, var9, var12, var8, DimensionTransition.DO_NOTHING)
      );
      return var13 != null;
   }

   public void dismountTo(double var1, double var3, double var5) {
      this.teleportTo(var1, var3, var5);
   }

   public void teleportTo(double var1, double var3, double var5) {
      if (this.level() instanceof ServerLevel) {
         this.moveTo(var1, var3, var5, this.getYRot(), this.getXRot());
         this.teleportPassengers();
      }
   }

   private void teleportPassengers() {
      this.getSelfAndPassengers().forEach(var0 -> {
         UnmodifiableIterator var1 = var0.passengers.iterator();

         while (var1.hasNext()) {
            Entity var2 = (Entity)var1.next();
            var0.positionRider(var2, Entity::moveTo);
         }
      });
   }

   public void teleportRelative(double var1, double var3, double var5) {
      this.teleportTo(this.getX() + var1, this.getY() + var3, this.getZ() + var5);
   }

   public boolean shouldShowName() {
      return this.isCustomNameVisible();
   }

   @Override
   public void onSyncedDataUpdated(List<SynchedEntityData.DataValue<?>> var1) {
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_POSE.equals(var1)) {
         this.refreshDimensions();
      }
   }

   @Deprecated
   protected void fixupDimensions() {
      Pose var1 = this.getPose();
      EntityDimensions var2 = this.getDimensions(var1);
      this.dimensions = var2;
      this.eyeHeight = var2.eyeHeight();
   }

   public void refreshDimensions() {
      EntityDimensions var1 = this.dimensions;
      Pose var2 = this.getPose();
      EntityDimensions var3 = this.getDimensions(var2);
      this.dimensions = var3;
      this.eyeHeight = var3.eyeHeight();
      this.reapplyPosition();
      boolean var4 = var3.width() <= 4.0F && var3.height() <= 4.0F;
      if (!this.level.isClientSide
         && !this.firstTick
         && !this.noPhysics
         && var4
         && (var3.width() > var1.width() || var3.height() > var1.height())
         && !(this instanceof Player)) {
         this.fudgePositionAfterSizeChange(var1);
      }
   }

   public boolean fudgePositionAfterSizeChange(EntityDimensions var1) {
      EntityDimensions var2 = this.getDimensions(this.getPose());
      Vec3 var3 = this.position().add(0.0, (double)var1.height() / 2.0, 0.0);
      double var4 = (double)Math.max(0.0F, var2.width() - var1.width()) + 1.0E-6;
      double var6 = (double)Math.max(0.0F, var2.height() - var1.height()) + 1.0E-6;
      VoxelShape var8 = Shapes.create(AABB.ofSize(var3, var4, var6, var4));
      Optional var9 = this.level.findFreePosition(this, var8, var3, (double)var2.width(), (double)var2.height(), (double)var2.width());
      if (var9.isPresent()) {
         this.setPos(((Vec3)var9.get()).add(0.0, (double)(-var2.height()) / 2.0, 0.0));
         return true;
      } else {
         if (var2.width() > var1.width() && var2.height() > var1.height()) {
            VoxelShape var10 = Shapes.create(AABB.ofSize(var3, var4, 1.0E-6, var4));
            Optional var11 = this.level.findFreePosition(this, var10, var3, (double)var2.width(), (double)var1.height(), (double)var2.width());
            if (var11.isPresent()) {
               this.setPos(((Vec3)var11.get()).add(0.0, (double)(-var1.height()) / 2.0 + 1.0E-6, 0.0));
               return true;
            }
         }

         return false;
      }
   }

   public Direction getDirection() {
      return Direction.fromYRot((double)this.getYRot());
   }

   public Direction getMotionDirection() {
      return this.getDirection();
   }

   protected HoverEvent createHoverEvent() {
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(this.getType(), this.getUUID(), this.getName()));
   }

   public boolean broadcastToPlayer(ServerPlayer var1) {
      return true;
   }

   @Override
   public final AABB getBoundingBox() {
      return this.bb;
   }

   public final void setBoundingBox(AABB var1) {
      this.bb = var1;
   }

   public final float getEyeHeight(Pose var1) {
      return this.getDimensions(var1).eyeHeight();
   }

   public final float getEyeHeight() {
      return this.eyeHeight;
   }

   public Vec3 getLeashOffset(float var1) {
      return this.getLeashOffset();
   }

   protected Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)this.getEyeHeight(), (double)(this.getBbWidth() * 0.4F));
   }

   public SlotAccess getSlot(int var1) {
      return SlotAccess.NULL;
   }

   @Override
   public void sendSystemMessage(Component var1) {
   }

   public Level getCommandSenderWorld() {
      return this.level();
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.level().getServer();
   }

   public InteractionResult interactAt(Player var1, Vec3 var2, InteractionHand var3) {
      return InteractionResult.PASS;
   }

   public boolean ignoreExplosion(Explosion var1) {
      return false;
   }

   public void startSeenByPlayer(ServerPlayer var1) {
   }

   public void stopSeenByPlayer(ServerPlayer var1) {
   }

   public float rotate(Rotation var1) {
      float var2 = Mth.wrapDegrees(this.getYRot());
      switch (var1) {
         case CLOCKWISE_180:
            return var2 + 180.0F;
         case COUNTERCLOCKWISE_90:
            return var2 + 270.0F;
         case CLOCKWISE_90:
            return var2 + 90.0F;
         default:
            return var2;
      }
   }

   public float mirror(Mirror var1) {
      float var2 = Mth.wrapDegrees(this.getYRot());
      switch (var1) {
         case FRONT_BACK:
            return -var2;
         case LEFT_RIGHT:
            return 180.0F - var2;
         default:
            return var2;
      }
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   public ProjectileDeflection deflection(Projectile var1) {
      return this.getType().is(EntityTypeTags.DEFLECTS_PROJECTILES) ? ProjectileDeflection.REVERSE : ProjectileDeflection.NONE;
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return null;
   }

   public final boolean hasControllingPassenger() {
      return this.getControllingPassenger() != null;
   }

   public final List<Entity> getPassengers() {
      return this.passengers;
   }

   @Nullable
   public Entity getFirstPassenger() {
      return this.passengers.isEmpty() ? null : (Entity)this.passengers.get(0);
   }

   public boolean hasPassenger(Entity var1) {
      return this.passengers.contains(var1);
   }

   public boolean hasPassenger(Predicate<Entity> var1) {
      UnmodifiableIterator var2 = this.passengers.iterator();

      while (var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         if (var1.test(var3)) {
            return true;
         }
      }

      return false;
   }

   private Stream<Entity> getIndirectPassengersStream() {
      return this.passengers.stream().flatMap(Entity::getSelfAndPassengers);
   }

   @Override
   public Stream<Entity> getSelfAndPassengers() {
      return Stream.concat(Stream.of(this), this.getIndirectPassengersStream());
   }

   @Override
   public Stream<Entity> getPassengersAndSelf() {
      return Stream.concat(this.passengers.stream().flatMap(Entity::getPassengersAndSelf), Stream.of(this));
   }

   public Iterable<Entity> getIndirectPassengers() {
      return () -> this.getIndirectPassengersStream().iterator();
   }

   public int countPlayerPassengers() {
      return (int)this.getIndirectPassengersStream().filter(var0 -> var0 instanceof Player).count();
   }

   public boolean hasExactlyOnePlayerPassenger() {
      return this.countPlayerPassengers() == 1;
   }

   public Entity getRootVehicle() {
      Entity var1 = this;

      while (var1.isPassenger()) {
         var1 = var1.getVehicle();
      }

      return var1;
   }

   public boolean isPassengerOfSameVehicle(Entity var1) {
      return this.getRootVehicle() == var1.getRootVehicle();
   }

   public boolean hasIndirectPassenger(Entity var1) {
      if (!var1.isPassenger()) {
         return false;
      } else {
         Entity var2 = var1.getVehicle();
         return var2 == this ? true : this.hasIndirectPassenger(var2);
      }
   }

   public boolean isControlledByOrIsLocalPlayer() {
      return this instanceof Player var1 ? var1.isLocalPlayer() : this.isControlledByLocalInstance();
   }

   public boolean isControlledByLocalInstance() {
      return this.getControllingPassenger() instanceof Player var1 ? var1.isLocalPlayer() : this.isEffectiveAi();
   }

   public boolean isEffectiveAi() {
      return !this.level().isClientSide;
   }

   protected static Vec3 getCollisionHorizontalEscapeVector(double var0, double var2, float var4) {
      double var5 = (var0 + var2 + 9.999999747378752E-6) / 2.0;
      float var7 = -Mth.sin(var4 * 0.017453292F);
      float var8 = Mth.cos(var4 * 0.017453292F);
      float var9 = Math.max(Math.abs(var7), Math.abs(var8));
      return new Vec3((double)var7 * var5 / (double)var9, 0.0, (double)var8 * var5 / (double)var9);
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
   }

   @Nullable
   public Entity getVehicle() {
      return this.vehicle;
   }

   @Nullable
   public Entity getControlledVehicle() {
      return this.vehicle != null && this.vehicle.getControllingPassenger() == this ? this.vehicle : null;
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.NORMAL;
   }

   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   protected int getFireImmuneTicks() {
      return 1;
   }

   public CommandSourceStack createCommandSourceStack() {
      return new CommandSourceStack(
         this,
         this.position(),
         this.getRotationVector(),
         this.level() instanceof ServerLevel ? (ServerLevel)this.level() : null,
         this.getPermissionLevel(),
         this.getName().getString(),
         this.getDisplayName(),
         this.level().getServer(),
         this
      );
   }

   protected int getPermissionLevel() {
      return 0;
   }

   public boolean hasPermissions(int var1) {
      return this.getPermissionLevel() >= var1;
   }

   @Override
   public boolean acceptsSuccess() {
      return this.level().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
   }

   @Override
   public boolean acceptsFailure() {
      return true;
   }

   @Override
   public boolean shouldInformAdmins() {
      return true;
   }

   public void lookAt(EntityAnchorArgument.Anchor var1, Vec3 var2) {
      Vec3 var3 = var1.apply(this);
      double var4 = var2.x - var3.x;
      double var6 = var2.y - var3.y;
      double var8 = var2.z - var3.z;
      double var10 = Math.sqrt(var4 * var4 + var8 * var8);
      this.setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(var6, var10) * 57.2957763671875))));
      this.setYRot(Mth.wrapDegrees((float)(Mth.atan2(var8, var4) * 57.2957763671875) - 90.0F));
      this.setYHeadRot(this.getYRot());
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
   }

   public float getPreciseBodyRotation(float var1) {
      return Mth.lerp(var1, this.yRotO, this.yRot);
   }

   public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> var1, double var2) {
      if (this.touchingUnloadedChunk()) {
         return false;
      } else {
         AABB var4 = this.getBoundingBox().deflate(0.001);
         int var5 = Mth.floor(var4.minX);
         int var6 = Mth.ceil(var4.maxX);
         int var7 = Mth.floor(var4.minY);
         int var8 = Mth.ceil(var4.maxY);
         int var9 = Mth.floor(var4.minZ);
         int var10 = Mth.ceil(var4.maxZ);
         double var11 = 0.0;
         boolean var13 = this.isPushedByFluid();
         boolean var14 = false;
         Vec3 var15 = Vec3.ZERO;
         int var16 = 0;
         BlockPos.MutableBlockPos var17 = new BlockPos.MutableBlockPos();

         for (int var18 = var5; var18 < var6; var18++) {
            for (int var19 = var7; var19 < var8; var19++) {
               for (int var20 = var9; var20 < var10; var20++) {
                  var17.set(var18, var19, var20);
                  FluidState var21 = this.level().getFluidState(var17);
                  if (var21.is(var1)) {
                     double var22 = (double)((float)var19 + var21.getHeight(this.level(), var17));
                     if (var22 >= var4.minY) {
                        var14 = true;
                        var11 = Math.max(var22 - var4.minY, var11);
                        if (var13) {
                           Vec3 var24 = var21.getFlow(this.level(), var17);
                           if (var11 < 0.4) {
                              var24 = var24.scale(var11);
                           }

                           var15 = var15.add(var24);
                           var16++;
                        }
                     }
                  }
               }
            }
         }

         if (var15.length() > 0.0) {
            if (var16 > 0) {
               var15 = var15.scale(1.0 / (double)var16);
            }

            if (!(this instanceof Player)) {
               var15 = var15.normalize();
            }

            Vec3 var26 = this.getDeltaMovement();
            var15 = var15.scale(var2);
            double var27 = 0.003;
            if (Math.abs(var26.x) < 0.003 && Math.abs(var26.z) < 0.003 && var15.length() < 0.0045000000000000005) {
               var15 = var15.normalize().scale(0.0045000000000000005);
            }

            this.setDeltaMovement(this.getDeltaMovement().add(var15));
         }

         this.fluidHeight.put(var1, var11);
         return var14;
      }
   }

   public boolean touchingUnloadedChunk() {
      AABB var1 = this.getBoundingBox().inflate(1.0);
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.minZ);
      int var5 = Mth.ceil(var1.maxZ);
      return !this.level().hasChunksAt(var2, var4, var3, var5);
   }

   public double getFluidHeight(TagKey<Fluid> var1) {
      return this.fluidHeight.getDouble(var1);
   }

   public double getFluidJumpThreshold() {
      return (double)this.getEyeHeight() < 0.4 ? 0.0 : 0.4;
   }

   public final float getBbWidth() {
      return this.dimensions.width();
   }

   public final float getBbHeight() {
      return this.dimensions.height();
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity var1) {
      return new ClientboundAddEntityPacket(this, var1);
   }

   public EntityDimensions getDimensions(Pose var1) {
      return this.type.getDimensions();
   }

   public final EntityAttachments getAttachments() {
      return this.dimensions.attachments();
   }

   public Vec3 position() {
      return this.position;
   }

   public Vec3 trackingPosition() {
      return this.position();
   }

   @Override
   public BlockPos blockPosition() {
      return this.blockPosition;
   }

   public BlockState getInBlockState() {
      if (this.inBlockState == null) {
         this.inBlockState = this.level().getBlockState(this.blockPosition());
      }

      return this.inBlockState;
   }

   public ChunkPos chunkPosition() {
      return this.chunkPosition;
   }

   public Vec3 getDeltaMovement() {
      return this.deltaMovement;
   }

   public void setDeltaMovement(Vec3 var1) {
      this.deltaMovement = var1;
   }

   public void addDeltaMovement(Vec3 var1) {
      this.setDeltaMovement(this.getDeltaMovement().add(var1));
   }

   public void setDeltaMovement(double var1, double var3, double var5) {
      this.setDeltaMovement(new Vec3(var1, var3, var5));
   }

   public final int getBlockX() {
      return this.blockPosition.getX();
   }

   public final double getX() {
      return this.position.x;
   }

   public double getX(double var1) {
      return this.position.x + (double)this.getBbWidth() * var1;
   }

   public double getRandomX(double var1) {
      return this.getX((2.0 * this.random.nextDouble() - 1.0) * var1);
   }

   public final int getBlockY() {
      return this.blockPosition.getY();
   }

   public final double getY() {
      return this.position.y;
   }

   public double getY(double var1) {
      return this.position.y + (double)this.getBbHeight() * var1;
   }

   public double getRandomY() {
      return this.getY(this.random.nextDouble());
   }

   public double getEyeY() {
      return this.position.y + (double)this.eyeHeight;
   }

   public final int getBlockZ() {
      return this.blockPosition.getZ();
   }

   public final double getZ() {
      return this.position.z;
   }

   public double getZ(double var1) {
      return this.position.z + (double)this.getBbWidth() * var1;
   }

   public double getRandomZ(double var1) {
      return this.getZ((2.0 * this.random.nextDouble() - 1.0) * var1);
   }

   public final void setPosRaw(double var1, double var3, double var5) {
      if (this.position.x != var1 || this.position.y != var3 || this.position.z != var5) {
         this.position = new Vec3(var1, var3, var5);
         int var7 = Mth.floor(var1);
         int var8 = Mth.floor(var3);
         int var9 = Mth.floor(var5);
         if (var7 != this.blockPosition.getX() || var8 != this.blockPosition.getY() || var9 != this.blockPosition.getZ()) {
            this.blockPosition = new BlockPos(var7, var8, var9);
            this.inBlockState = null;
            if (SectionPos.blockToSectionCoord(var7) != this.chunkPosition.x || SectionPos.blockToSectionCoord(var9) != this.chunkPosition.z) {
               this.chunkPosition = new ChunkPos(this.blockPosition);
            }
         }

         this.levelCallback.onMove();
      }
   }

   public void checkDespawn() {
   }

   public Vec3 getRopeHoldPosition(float var1) {
      return this.getPosition(var1).add(0.0, (double)this.eyeHeight * 0.7, 0.0);
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      int var2 = var1.getId();
      double var3 = var1.getX();
      double var5 = var1.getY();
      double var7 = var1.getZ();
      this.syncPacketPositionCodec(var3, var5, var7);
      this.moveTo(var3, var5, var7);
      this.setXRot(var1.getXRot());
      this.setYRot(var1.getYRot());
      this.setId(var2);
      this.setUUID(var1.getUUID());
   }

   @Nullable
   public ItemStack getPickResult() {
      return null;
   }

   public void setIsInPowderSnow(boolean var1) {
      this.isInPowderSnow = var1;
   }

   public boolean canFreeze() {
      return !this.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
   }

   public boolean isFreezing() {
      return (this.isInPowderSnow || this.wasInPowderSnow) && this.canFreeze();
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getVisualRotationYInDegrees() {
      return this.getYRot();
   }

   public void setYRot(float var1) {
      if (!Float.isFinite(var1)) {
         Util.logAndPauseIfInIde("Invalid entity rotation: " + var1 + ", discarding.");
      } else {
         this.yRot = var1;
      }
   }

   public float getXRot() {
      return this.xRot;
   }

   public void setXRot(float var1) {
      if (!Float.isFinite(var1)) {
         Util.logAndPauseIfInIde("Invalid entity rotation: " + var1 + ", discarding.");
      } else {
         this.xRot = Math.clamp(var1 % 360.0F, -90.0F, 90.0F);
      }
   }

   public boolean canSprint() {
      return false;
   }

   public float maxUpStep() {
      return 0.0F;
   }

   public void onExplosionHit(@Nullable Entity var1) {
   }

   public final boolean isRemoved() {
      return this.removalReason != null;
   }

   @Nullable
   public Entity.RemovalReason getRemovalReason() {
      return this.removalReason;
   }

   @Override
   public final void setRemoved(Entity.RemovalReason var1) {
      if (this.removalReason == null) {
         this.removalReason = var1;
      }

      if (this.removalReason.shouldDestroy()) {
         this.stopRiding();
      }

      this.getPassengers().forEach(Entity::stopRiding);
      this.levelCallback.onRemove(var1);
   }

   protected void unsetRemoved() {
      this.removalReason = null;
   }

   @Override
   public void setLevelCallback(EntityInLevelCallback var1) {
      this.levelCallback = var1;
   }

   @Override
   public boolean shouldBeSaved() {
      if (this.removalReason != null && !this.removalReason.shouldSave()) {
         return false;
      } else {
         return this.isPassenger() ? false : !this.isVehicle() || !this.hasExactlyOnePlayerPassenger();
      }
   }

   @Override
   public boolean isAlwaysTicking() {
      return false;
   }

   public boolean mayInteract(Level var1, BlockPos var2) {
      return true;
   }

   public Level level() {
      return this.level;
   }

   protected void setLevel(Level var1) {
      this.level = var1;
   }

   public DamageSources damageSources() {
      return this.level().damageSources();
   }

   public RegistryAccess registryAccess() {
      return this.level().registryAccess();
   }

   protected void lerpPositionAndRotationStep(int var1, double var2, double var4, double var6, double var8, double var10) {
      double var12 = 1.0 / (double)var1;
      double var14 = Mth.lerp(var12, this.getX(), var2);
      double var16 = Mth.lerp(var12, this.getY(), var4);
      double var18 = Mth.lerp(var12, this.getZ(), var6);
      float var20 = (float)Mth.rotLerp(var12, (double)this.getYRot(), var8);
      float var21 = (float)Mth.lerp(var12, (double)this.getXRot(), var10);
      this.setPos(var14, var16, var18);
      this.setRot(var20, var21);
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public Vec3 getKnownMovement() {
      if (this.getControllingPassenger() instanceof Player var1 && this.isAlive()) {
         return var1.getKnownMovement();
      }

      return this.getDeltaMovement();
   }

   @Nullable
   public ItemStack getWeaponItem() {
      return null;
   }

   public Optional<ResourceKey<LootTable>> getLootTable() {
      return this.type.getDefaultLootTable();
   }

   @FunctionalInterface
   public interface MoveFunction {
      void accept(Entity var1, double var2, double var4, double var6);
   }

   public static enum MovementEmission {
      NONE(false, false),
      SOUNDS(true, false),
      EVENTS(false, true),
      ALL(true, true);

      final boolean sounds;
      final boolean events;

      private MovementEmission(final boolean nullxx, final boolean nullxxx) {
         this.sounds = nullxx;
         this.events = nullxxx;
      }

      public boolean emitsAnything() {
         return this.events || this.sounds;
      }

      public boolean emitsEvents() {
         return this.events;
      }

      public boolean emitsSounds() {
         return this.sounds;
      }
   }

   public static enum RemovalReason {
      KILLED(true, false),
      DISCARDED(true, false),
      UNLOADED_TO_CHUNK(false, true),
      UNLOADED_WITH_PLAYER(false, false),
      CHANGED_DIMENSION(false, false);

      private final boolean destroy;
      private final boolean save;

      private RemovalReason(final boolean nullxx, final boolean nullxxx) {
         this.destroy = nullxx;
         this.save = nullxxx;
      }

      public boolean shouldDestroy() {
         return this.destroy;
      }

      public boolean shouldSave() {
         return this.save;
      }
   }
}
