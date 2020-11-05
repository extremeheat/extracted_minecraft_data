package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.RewindableStream;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements Nameable, EntityAccess, CommandSource {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
   private static final List<ItemStack> EMPTY_LIST = Collections.emptyList();
   private static final AABB INITIAL_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static double viewScale = 1.0D;
   private final EntityType<?> type;
   private int id;
   public boolean blocksBuilding;
   private ImmutableList<Entity> passengers;
   protected int boardingCooldown;
   @Nullable
   private Entity vehicle;
   public boolean forcedLoading;
   public Level level;
   public double xo;
   public double yo;
   public double zo;
   private Vec3 position;
   private BlockPos blockPosition;
   private Vec3 deltaMovement;
   public float yRot;
   public float xRot;
   public float yRotO;
   public float xRotO;
   private AABB bb;
   protected boolean onGround;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean hurtMarked;
   protected Vec3 stuckSpeedMultiplier;
   @Nullable
   private Entity.RemovalReason removalReason;
   public float walkDistO;
   public float walkDist;
   public float moveDist;
   public float fallDistance;
   private float nextStep;
   private float nextFlap;
   public double xOld;
   public double yOld;
   public double zOld;
   public float maxUpStep;
   public boolean noPhysics;
   public float pushthrough;
   protected final Random random;
   public int tickCount;
   private int remainingFireTicks;
   protected boolean wasTouchingWater;
   protected Object2DoubleMap<Tag<Fluid>> fluidHeight;
   protected boolean wasEyeInWater;
   @Nullable
   protected Tag<Fluid> fluidOnEyes;
   public int invulnerableTime;
   protected boolean firstTick;
   protected final SynchedEntityData entityData;
   protected static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID;
   private static final EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID;
   private static final EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME;
   private static final EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE;
   private static final EntityDataAccessor<Boolean> DATA_SILENT;
   private static final EntityDataAccessor<Boolean> DATA_NO_GRAVITY;
   protected static final EntityDataAccessor<Pose> DATA_POSE;
   private EntityInLevelCallback levelCallback;
   private Vec3 packetCoordinates;
   public boolean noCulling;
   public boolean hasImpulse;
   private int portalCooldown;
   protected boolean isInsidePortal;
   protected int portalTime;
   protected BlockPos portalEntrancePos;
   private boolean invulnerable;
   protected UUID uuid;
   protected String stringUUID;
   protected boolean glowing;
   private final Set<String> tags;
   private final double[] pistonDeltas;
   private long pistonDeltasGameTime;
   private EntityDimensions dimensions;
   private float eyeHeight;
   private float crystalSoundIntensity;
   private int lastCrystalSoundPlayTick;

   public Entity(EntityType<?> var1, Level var2) {
      super();
      this.id = ENTITY_COUNTER.incrementAndGet();
      this.passengers = ImmutableList.of();
      this.deltaMovement = Vec3.ZERO;
      this.bb = INITIAL_AABB;
      this.stuckSpeedMultiplier = Vec3.ZERO;
      this.nextStep = 1.0F;
      this.nextFlap = 1.0F;
      this.random = new Random();
      this.remainingFireTicks = -this.getFireImmuneTicks();
      this.fluidHeight = new Object2DoubleArrayMap(2);
      this.firstTick = true;
      this.levelCallback = EntityInLevelCallback.NULL;
      this.uuid = Mth.createInsecureUUID(this.random);
      this.stringUUID = this.uuid.toString();
      this.tags = Sets.newHashSet();
      this.pistonDeltas = new double[]{0.0D, 0.0D, 0.0D};
      this.type = var1;
      this.level = var2;
      this.dimensions = var1.getDimensions();
      this.position = Vec3.ZERO;
      this.blockPosition = BlockPos.ZERO;
      this.packetCoordinates = Vec3.ZERO;
      this.setPos(0.0D, 0.0D, 0.0D);
      this.entityData = new SynchedEntityData(this);
      this.entityData.define(DATA_SHARED_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_AIR_SUPPLY_ID, this.getMaxAirSupply());
      this.entityData.define(DATA_CUSTOM_NAME_VISIBLE, false);
      this.entityData.define(DATA_CUSTOM_NAME, Optional.empty());
      this.entityData.define(DATA_SILENT, false);
      this.entityData.define(DATA_NO_GRAVITY, false);
      this.entityData.define(DATA_POSE, Pose.STANDING);
      this.defineSynchedData();
      this.eyeHeight = this.getEyeHeight(Pose.STANDING, this.dimensions);
   }

   public boolean isColliding(BlockPos var1, BlockState var2) {
      VoxelShape var3 = var2.getCollisionShape(this.level, var1, CollisionContext.of(this));
      VoxelShape var4 = var3.move((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
      return Shapes.joinIsNotEmpty(var4, Shapes.create(this.getBoundingBox()), BooleanOp.AND);
   }

   public int getTeamColor() {
      Team var1 = this.getTeam();
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

   public void setPacketCoordinates(double var1, double var3, double var5) {
      this.setPacketCoordinates(new Vec3(var1, var3, var5));
   }

   public void setPacketCoordinates(Vec3 var1) {
      this.packetCoordinates = var1;
   }

   public Vec3 getPacketCoordinates() {
      return this.packetCoordinates;
   }

   public EntityType<?> getType() {
      return this.type;
   }

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
   }

   public final void discard() {
      this.remove(Entity.RemovalReason.DISCARDED);
   }

   protected abstract void defineSynchedData();

   public SynchedEntityData getEntityData() {
      return this.entityData;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Entity) {
         return ((Entity)var1).id == this.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   protected void resetPos() {
      if (this.level != null) {
         for(double var1 = this.getY(); var1 > (double)this.level.getMinBuildHeight() && var1 < (double)this.level.getMinBuildHeight(); ++var1) {
            this.setPos(this.getX(), var1, this.getZ());
            if (this.level.noCollision(this)) {
               break;
            }
         }

         this.setDeltaMovement(Vec3.ZERO);
         this.xRot = 0.0F;
      }
   }

   public void remove(Entity.RemovalReason var1) {
      this.setRemoved(var1);
   }

   public void setPose(Pose var1) {
      this.entityData.set(DATA_POSE, var1);
   }

   public Pose getPose() {
      return (Pose)this.entityData.get(DATA_POSE);
   }

   public boolean closerThan(Entity var1, double var2) {
      double var4 = var1.position.x - this.position.x;
      double var6 = var1.position.y - this.position.y;
      double var8 = var1.position.z - this.position.z;
      return var4 * var4 + var6 * var6 + var8 * var8 < var2 * var2;
   }

   protected void setRot(float var1, float var2) {
      this.yRot = var1 % 360.0F;
      this.xRot = var2 % 360.0F;
   }

   public void setPos(double var1, double var3, double var5) {
      this.setPosRaw(var1, var3, var5);
      this.setBoundingBox(this.dimensions.makeBoundingBox(var1, var3, var5));
   }

   protected void reapplyPosition() {
      this.setPos(this.position.x, this.position.y, this.position.z);
   }

   public void turn(double var1, double var3) {
      double var5 = var3 * 0.15D;
      double var7 = var1 * 0.15D;
      this.xRot = (float)((double)this.xRot + var5);
      this.yRot = (float)((double)this.yRot + var7);
      this.xRot = Mth.clamp(this.xRot, -90.0F, 90.0F);
      this.xRotO = (float)((double)this.xRotO + var5);
      this.yRotO = (float)((double)this.yRotO + var7);
      this.xRotO = Mth.clamp(this.xRotO, -90.0F, 90.0F);
      if (this.vehicle != null) {
         this.vehicle.onPassengerTurned(this);
      }

   }

   public void tick() {
      if (!this.level.isClientSide) {
         this.setSharedFlag(6, this.isGlowing());
      }

      this.baseTick();
   }

   public void baseTick() {
      this.level.getProfiler().push("entityBaseTick");
      if (this.isPassenger() && this.getVehicle().isRemoved()) {
         this.stopRiding();
      }

      if (this.boardingCooldown > 0) {
         --this.boardingCooldown;
      }

      this.walkDistO = this.walkDist;
      this.xRotO = this.xRot;
      this.yRotO = this.yRot;
      this.handleNetherPortal();
      if (this.canSpawnSprintParticle()) {
         this.spawnSprintParticle();
      }

      this.updateInWaterStateAndDoFluidPushing();
      this.updateFluidOnEyes();
      this.updateSwimming();
      if (this.level.isClientSide) {
         this.clearFire();
      } else if (this.remainingFireTicks > 0) {
         if (this.fireImmune()) {
            this.setRemainingFireTicks(this.remainingFireTicks - 4);
            if (this.remainingFireTicks < 0) {
               this.clearFire();
            }
         } else {
            if (this.remainingFireTicks % 20 == 0 && !this.isInLava()) {
               this.hurt(DamageSource.ON_FIRE, 1.0F);
            }

            this.setRemainingFireTicks(this.remainingFireTicks - 1);
         }
      }

      if (this.isInLava()) {
         this.lavaHurt();
         this.fallDistance *= 0.5F;
      }

      this.checkOutOfWorld();
      if (!this.level.isClientSide) {
         this.setSharedFlag(0, this.remainingFireTicks > 0);
      }

      this.firstTick = false;
      this.level.getProfiler().pop();
   }

   public void checkOutOfWorld() {
      if (this.getY() < (double)(this.level.getMinBuildHeight() - 64)) {
         this.outOfWorld();
      }

   }

   public void setPortalCooldown() {
      this.portalCooldown = this.getDimensionChangingDelay();
   }

   public boolean isOnPortalCooldown() {
      return this.portalCooldown > 0;
   }

   protected void processPortalCooldown() {
      if (this.isOnPortalCooldown()) {
         --this.portalCooldown;
      }

   }

   public int getPortalWaitTime() {
      return 0;
   }

   public void lavaHurt() {
      if (!this.fireImmune()) {
         this.setSecondsOnFire(15);
         this.hurt(DamageSource.LAVA, 4.0F);
      }
   }

   public void setSecondsOnFire(int var1) {
      int var2 = var1 * 20;
      if (this instanceof LivingEntity) {
         var2 = ProtectionEnchantment.getFireAfterDampener((LivingEntity)this, var2);
      }

      if (this.remainingFireTicks < var2) {
         this.setRemainingFireTicks(var2);
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

   protected void outOfWorld() {
      this.discard();
   }

   public boolean isFree(double var1, double var3, double var5) {
      return this.isFree(this.getBoundingBox().move(var1, var3, var5));
   }

   private boolean isFree(AABB var1) {
      return this.level.noCollision(this, var1) && !this.level.containsAnyLiquid(var1);
   }

   public void setOnGround(boolean var1) {
      this.onGround = var1;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void move(MoverType var1, Vec3 var2) {
      if (this.noPhysics) {
         this.setBoundingBox(this.getBoundingBox().move(var2));
         this.setLocationFromBoundingbox();
      } else {
         if (var1 == MoverType.PISTON) {
            var2 = this.limitPistonMovement(var2);
            if (var2.equals(Vec3.ZERO)) {
               return;
            }
         }

         this.level.getProfiler().push("move");
         if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
            var2 = var2.multiply(this.stuckSpeedMultiplier);
            this.stuckSpeedMultiplier = Vec3.ZERO;
            this.setDeltaMovement(Vec3.ZERO);
         }

         var2 = this.maybeBackOffFromEdge(var2, var1);
         Vec3 var3 = this.collide(var2);
         if (var3.lengthSqr() > 1.0E-7D) {
            this.setBoundingBox(this.getBoundingBox().move(var3));
            this.setLocationFromBoundingbox();
         }

         this.level.getProfiler().pop();
         this.level.getProfiler().push("rest");
         this.horizontalCollision = !Mth.equal(var2.x, var3.x) || !Mth.equal(var2.z, var3.z);
         this.verticalCollision = var2.y != var3.y;
         this.onGround = this.verticalCollision && var2.y < 0.0D;
         BlockPos var4 = this.getOnPos();
         BlockState var5 = this.level.getBlockState(var4);
         this.checkFallDamage(var3.y, this.onGround, var5, var4);
         Vec3 var6 = this.getDeltaMovement();
         if (var2.x != var3.x) {
            this.setDeltaMovement(0.0D, var6.y, var6.z);
         }

         if (var2.z != var3.z) {
            this.setDeltaMovement(var6.x, var6.y, 0.0D);
         }

         Block var7 = var5.getBlock();
         if (var2.y != var3.y) {
            var7.updateEntityAfterFallOn(this.level, this);
         }

         if (this.onGround && !this.isSteppingCarefully()) {
            var7.stepOn(this.level, var4, this);
         }

         if (this.isMovementNoisy() && !this.isPassenger()) {
            double var8 = var3.x;
            double var10 = var3.y;
            double var12 = var3.z;
            if (!var5.is(BlockTags.CLIMBABLE)) {
               var10 = 0.0D;
            }

            this.walkDist = (float)((double)this.walkDist + (double)Mth.sqrt(getHorizontalDistanceSqr(var3)) * 0.6D);
            this.moveDist = (float)((double)this.moveDist + (double)Mth.sqrt(var8 * var8 + var10 * var10 + var12 * var12) * 0.6D);
            if (this.moveDist > this.nextStep && !var5.isAir()) {
               this.nextStep = this.nextStep();
               if (this.isInWater()) {
                  Entity var14 = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                  float var15 = var14 == this ? 0.35F : 0.4F;
                  Vec3 var16 = var14.getDeltaMovement();
                  float var17 = Mth.sqrt(var16.x * var16.x * 0.20000000298023224D + var16.y * var16.y + var16.z * var16.z * 0.20000000298023224D) * var15;
                  if (var17 > 1.0F) {
                     var17 = 1.0F;
                  }

                  this.playSwimSound(var17);
               } else {
                  this.playStepSound(var4, var5);
               }
            } else if (this.moveDist > this.nextFlap && this.makeFlySound() && var5.isAir()) {
               this.nextFlap = this.playFlySound(this.moveDist);
            }
         }

         try {
            this.checkInsideBlocks();
         } catch (Throwable var18) {
            CrashReport var9 = CrashReport.forThrowable(var18, "Checking entity block collision");
            CrashReportCategory var20 = var9.addCategory("Entity being checked for collision");
            this.fillCrashReportCategory(var20);
            throw new ReportedException(var9);
         }

         float var19 = this.getBlockSpeedFactor();
         this.setDeltaMovement(this.getDeltaMovement().multiply((double)var19, 1.0D, (double)var19));
         if (this.level.getBlockStatesIfLoaded(this.getBoundingBox().deflate(0.001D)).noneMatch((var0) -> {
            return var0.is(BlockTags.FIRE) || var0.is(Blocks.LAVA);
         }) && this.remainingFireTicks <= 0) {
            this.setRemainingFireTicks(-this.getFireImmuneTicks());
         }

         if (this.isInWaterRainOrBubble() && this.isOnFire()) {
            this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            this.setRemainingFireTicks(-this.getFireImmuneTicks());
         }

         this.level.getProfiler().pop();
      }
   }

   protected BlockPos getOnPos() {
      int var1 = Mth.floor(this.position.x);
      int var2 = Mth.floor(this.position.y - 0.20000000298023224D);
      int var3 = Mth.floor(this.position.z);
      BlockPos var4 = new BlockPos(var1, var2, var3);
      if (this.level.getBlockState(var4).isAir()) {
         BlockPos var5 = var4.below();
         BlockState var6 = this.level.getBlockState(var5);
         if (var6.is(BlockTags.FENCES) || var6.is(BlockTags.WALLS) || var6.getBlock() instanceof FenceGateBlock) {
            return var5;
         }
      }

      return var4;
   }

   protected float getBlockJumpFactor() {
      float var1 = this.level.getBlockState(this.blockPosition()).getBlock().getJumpFactor();
      float var2 = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getJumpFactor();
      return (double)var1 == 1.0D ? var2 : var1;
   }

   protected float getBlockSpeedFactor() {
      BlockState var1 = this.level.getBlockState(this.blockPosition());
      float var2 = var1.getBlock().getSpeedFactor();
      if (!var1.is(Blocks.WATER) && !var1.is(Blocks.BUBBLE_COLUMN)) {
         return (double)var2 == 1.0D ? this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getSpeedFactor() : var2;
      } else {
         return var2;
      }
   }

   protected BlockPos getBlockPosBelowThatAffectsMyMovement() {
      return new BlockPos(this.position.x, this.getBoundingBox().minY - 0.5000001D, this.position.z);
   }

   protected Vec3 maybeBackOffFromEdge(Vec3 var1, MoverType var2) {
      return var1;
   }

   protected Vec3 limitPistonMovement(Vec3 var1) {
      if (var1.lengthSqr() <= 1.0E-7D) {
         return var1;
      } else {
         long var2 = this.level.getGameTime();
         if (var2 != this.pistonDeltasGameTime) {
            Arrays.fill(this.pistonDeltas, 0.0D);
            this.pistonDeltasGameTime = var2;
         }

         double var4;
         if (var1.x != 0.0D) {
            var4 = this.applyPistonMovementRestriction(Direction.Axis.X, var1.x);
            return Math.abs(var4) <= 9.999999747378752E-6D ? Vec3.ZERO : new Vec3(var4, 0.0D, 0.0D);
         } else if (var1.y != 0.0D) {
            var4 = this.applyPistonMovementRestriction(Direction.Axis.Y, var1.y);
            return Math.abs(var4) <= 9.999999747378752E-6D ? Vec3.ZERO : new Vec3(0.0D, var4, 0.0D);
         } else if (var1.z != 0.0D) {
            var4 = this.applyPistonMovementRestriction(Direction.Axis.Z, var1.z);
            return Math.abs(var4) <= 9.999999747378752E-6D ? Vec3.ZERO : new Vec3(0.0D, 0.0D, var4);
         } else {
            return Vec3.ZERO;
         }
      }
   }

   private double applyPistonMovementRestriction(Direction.Axis var1, double var2) {
      int var4 = var1.ordinal();
      double var5 = Mth.clamp(var2 + this.pistonDeltas[var4], -0.51D, 0.51D);
      var2 = var5 - this.pistonDeltas[var4];
      this.pistonDeltas[var4] = var5;
      return var2;
   }

   private Vec3 collide(Vec3 var1) {
      AABB var2 = this.getBoundingBox();
      CollisionContext var3 = CollisionContext.of(this);
      VoxelShape var4 = this.level.getWorldBorder().getCollisionShape();
      Stream var5 = Shapes.joinIsNotEmpty(var4, Shapes.create(var2.deflate(1.0E-7D)), BooleanOp.AND) ? Stream.empty() : Stream.of(var4);
      Stream var6 = this.level.getEntityCollisions(this, var2.expandTowards(var1), (var0) -> {
         return true;
      });
      RewindableStream var7 = new RewindableStream(Stream.concat(var6, var5));
      Vec3 var8 = var1.lengthSqr() == 0.0D ? var1 : collideBoundingBoxHeuristically(this, var1, var2, this.level, var3, var7);
      boolean var9 = var1.x != var8.x;
      boolean var10 = var1.y != var8.y;
      boolean var11 = var1.z != var8.z;
      boolean var12 = this.onGround || var10 && var1.y < 0.0D;
      if (this.maxUpStep > 0.0F && var12 && (var9 || var11)) {
         Vec3 var13 = collideBoundingBoxHeuristically(this, new Vec3(var1.x, (double)this.maxUpStep, var1.z), var2, this.level, var3, var7);
         Vec3 var14 = collideBoundingBoxHeuristically(this, new Vec3(0.0D, (double)this.maxUpStep, 0.0D), var2.expandTowards(var1.x, 0.0D, var1.z), this.level, var3, var7);
         if (var14.y < (double)this.maxUpStep) {
            Vec3 var15 = collideBoundingBoxHeuristically(this, new Vec3(var1.x, 0.0D, var1.z), var2.move(var14), this.level, var3, var7).add(var14);
            if (getHorizontalDistanceSqr(var15) > getHorizontalDistanceSqr(var13)) {
               var13 = var15;
            }
         }

         if (getHorizontalDistanceSqr(var13) > getHorizontalDistanceSqr(var8)) {
            return var13.add(collideBoundingBoxHeuristically(this, new Vec3(0.0D, -var13.y + var1.y, 0.0D), var2.move(var13), this.level, var3, var7));
         }
      }

      return var8;
   }

   public static double getHorizontalDistanceSqr(Vec3 var0) {
      return var0.x * var0.x + var0.z * var0.z;
   }

   public static Vec3 collideBoundingBoxHeuristically(@Nullable Entity var0, Vec3 var1, AABB var2, Level var3, CollisionContext var4, RewindableStream<VoxelShape> var5) {
      boolean var6 = var1.x == 0.0D;
      boolean var7 = var1.y == 0.0D;
      boolean var8 = var1.z == 0.0D;
      if ((!var6 || !var7) && (!var6 || !var8) && (!var7 || !var8)) {
         RewindableStream var9 = new RewindableStream(Stream.concat(var5.getStream(), var3.getBlockCollisions(var0, var2.expandTowards(var1))));
         return collideBoundingBoxLegacy(var1, var2, var9);
      } else {
         return collideBoundingBox(var1, var2, var3, var4, var5);
      }
   }

   public static Vec3 collideBoundingBoxLegacy(Vec3 var0, AABB var1, RewindableStream<VoxelShape> var2) {
      double var3 = var0.x;
      double var5 = var0.y;
      double var7 = var0.z;
      if (var5 != 0.0D) {
         var5 = Shapes.collide(Direction.Axis.Y, var1, var2.getStream(), var5);
         if (var5 != 0.0D) {
            var1 = var1.move(0.0D, var5, 0.0D);
         }
      }

      boolean var9 = Math.abs(var3) < Math.abs(var7);
      if (var9 && var7 != 0.0D) {
         var7 = Shapes.collide(Direction.Axis.Z, var1, var2.getStream(), var7);
         if (var7 != 0.0D) {
            var1 = var1.move(0.0D, 0.0D, var7);
         }
      }

      if (var3 != 0.0D) {
         var3 = Shapes.collide(Direction.Axis.X, var1, var2.getStream(), var3);
         if (!var9 && var3 != 0.0D) {
            var1 = var1.move(var3, 0.0D, 0.0D);
         }
      }

      if (!var9 && var7 != 0.0D) {
         var7 = Shapes.collide(Direction.Axis.Z, var1, var2.getStream(), var7);
      }

      return new Vec3(var3, var5, var7);
   }

   public static Vec3 collideBoundingBox(Vec3 var0, AABB var1, LevelReader var2, CollisionContext var3, RewindableStream<VoxelShape> var4) {
      double var5 = var0.x;
      double var7 = var0.y;
      double var9 = var0.z;
      if (var7 != 0.0D) {
         var7 = Shapes.collide(Direction.Axis.Y, var1, var2, var7, var3, var4.getStream());
         if (var7 != 0.0D) {
            var1 = var1.move(0.0D, var7, 0.0D);
         }
      }

      boolean var11 = Math.abs(var5) < Math.abs(var9);
      if (var11 && var9 != 0.0D) {
         var9 = Shapes.collide(Direction.Axis.Z, var1, var2, var9, var3, var4.getStream());
         if (var9 != 0.0D) {
            var1 = var1.move(0.0D, 0.0D, var9);
         }
      }

      if (var5 != 0.0D) {
         var5 = Shapes.collide(Direction.Axis.X, var1, var2, var5, var3, var4.getStream());
         if (!var11 && var5 != 0.0D) {
            var1 = var1.move(var5, 0.0D, 0.0D);
         }
      }

      if (!var11 && var9 != 0.0D) {
         var9 = Shapes.collide(Direction.Axis.Z, var1, var2, var9, var3, var4.getStream());
      }

      return new Vec3(var5, var7, var9);
   }

   protected float nextStep() {
      return (float)((int)this.moveDist + 1);
   }

   public void setLocationFromBoundingbox() {
      AABB var1 = this.getBoundingBox();
      this.setPosRaw((var1.minX + var1.maxX) / 2.0D, var1.minY, (var1.minZ + var1.maxZ) / 2.0D);
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

   protected void checkInsideBlocks() {
      AABB var1 = this.getBoundingBox();
      BlockPos var2 = new BlockPos(var1.minX + 0.001D, var1.minY + 0.001D, var1.minZ + 0.001D);
      BlockPos var3 = new BlockPos(var1.maxX - 0.001D, var1.maxY - 0.001D, var1.maxZ - 0.001D);
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      if (this.level.hasChunksAt(var2, var3)) {
         for(int var5 = var2.getX(); var5 <= var3.getX(); ++var5) {
            for(int var6 = var2.getY(); var6 <= var3.getY(); ++var6) {
               for(int var7 = var2.getZ(); var7 <= var3.getZ(); ++var7) {
                  var4.set(var5, var6, var7);
                  BlockState var8 = this.level.getBlockState(var4);

                  try {
                     var8.entityInside(this.level, var4, this);
                     this.onInsideBlock(var8);
                  } catch (Throwable var12) {
                     CrashReport var10 = CrashReport.forThrowable(var12, "Colliding entity with block");
                     CrashReportCategory var11 = var10.addCategory("Block being collided with");
                     CrashReportCategory.populateBlockDetails(var11, this.level, var4, var8);
                     throw new ReportedException(var10);
                  }
               }
            }
         }
      }

   }

   protected void onInsideBlock(BlockState var1) {
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      if (!var2.getMaterial().isLiquid()) {
         if (var2.is(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.tickCount >= this.lastCrystalSoundPlayTick + 20) {
            this.crystalSoundIntensity = (float)((double)this.crystalSoundIntensity * Math.pow(0.996999979019165D, (double)(this.tickCount - this.lastCrystalSoundPlayTick)));
            this.crystalSoundIntensity = Math.min(1.0F, this.crystalSoundIntensity + 0.07F);
            float var3 = 0.5F + this.crystalSoundIntensity * this.random.nextFloat() * 1.2F;
            float var4 = 0.1F + this.crystalSoundIntensity * 1.2F;
            this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, var4, var3);
            this.lastCrystalSoundPlayTick = this.tickCount;
         }

         BlockState var5 = this.level.getBlockState(var1.above());
         SoundType var6 = var5.is(Blocks.SNOW) ? var5.getSoundType() : var2.getSoundType();
         this.playSound(var6.getStepSound(), var6.getVolume() * 0.15F, var6.getPitch());
      }
   }

   protected void playSwimSound(float var1) {
      this.playSound(this.getSwimSound(), var1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
   }

   protected float playFlySound(float var1) {
      return 0.0F;
   }

   protected boolean makeFlySound() {
      return false;
   }

   public void playSound(SoundEvent var1, float var2, float var3) {
      if (!this.isSilent()) {
         this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), var1, this.getSoundSource(), var2, var3);
      }

   }

   public boolean isSilent() {
      return (Boolean)this.entityData.get(DATA_SILENT);
   }

   public void setSilent(boolean var1) {
      this.entityData.set(DATA_SILENT, var1);
   }

   public boolean isNoGravity() {
      return (Boolean)this.entityData.get(DATA_NO_GRAVITY);
   }

   public void setNoGravity(boolean var1) {
      this.entityData.set(DATA_NO_GRAVITY, var1);
   }

   protected boolean isMovementNoisy() {
      return true;
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
      if (var3) {
         if (this.fallDistance > 0.0F) {
            var4.getBlock().fallOn(this.level, var5, this, this.fallDistance);
         }

         this.fallDistance = 0.0F;
      } else if (var1 < 0.0D) {
         this.fallDistance = (float)((double)this.fallDistance - var1);
      }

   }

   public boolean fireImmune() {
      return this.getType().fireImmune();
   }

   public boolean causeFallDamage(float var1, float var2) {
      if (this.isVehicle()) {
         Iterator var3 = this.getPassengers().iterator();

         while(var3.hasNext()) {
            Entity var4 = (Entity)var3.next();
            var4.causeFallDamage(var1, var2);
         }
      }

      return false;
   }

   public boolean isInWater() {
      return this.wasTouchingWater;
   }

   private boolean isInRain() {
      BlockPos var1 = this.blockPosition();
      return this.level.isRainingAt(var1) || this.level.isRainingAt(new BlockPos((double)var1.getX(), this.getBoundingBox().maxY, (double)var1.getZ()));
   }

   private boolean isInBubbleColumn() {
      return this.level.getBlockState(this.blockPosition()).is(Blocks.BUBBLE_COLUMN);
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

   public boolean isUnderWater() {
      return this.wasEyeInWater && this.isInWater();
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
      } else {
         this.setSwimming(this.isSprinting() && this.isUnderWater() && !this.isPassenger());
      }

   }

   protected boolean updateInWaterStateAndDoFluidPushing() {
      this.fluidHeight.clear();
      this.updateInWaterStateAndDoWaterCurrentPushing();
      double var1 = this.level.dimensionType().ultraWarm() ? 0.007D : 0.0023333333333333335D;
      boolean var3 = this.updateFluidHeightAndDoFluidPushing(FluidTags.LAVA, var1);
      return this.isInWater() || var3;
   }

   void updateInWaterStateAndDoWaterCurrentPushing() {
      if (this.getVehicle() instanceof Boat) {
         this.wasTouchingWater = false;
      } else if (this.updateFluidHeightAndDoFluidPushing(FluidTags.WATER, 0.014D)) {
         if (!this.wasTouchingWater && !this.firstTick) {
            this.doWaterSplashEffect();
         }

         this.fallDistance = 0.0F;
         this.wasTouchingWater = true;
         this.clearFire();
      } else {
         this.wasTouchingWater = false;
      }

   }

   private void updateFluidOnEyes() {
      this.wasEyeInWater = this.isEyeInFluid(FluidTags.WATER);
      this.fluidOnEyes = null;
      double var1 = this.getEyeY() - 0.1111111119389534D;
      Entity var3 = this.getVehicle();
      if (var3 instanceof Boat) {
         Boat var4 = (Boat)var3;
         if (!var4.isUnderWater() && var4.getBoundingBox().maxY >= var1 && var4.getBoundingBox().minY <= var1) {
            return;
         }
      }

      BlockPos var10 = new BlockPos(this.getX(), var1, this.getZ());
      FluidState var5 = this.level.getFluidState(var10);
      Iterator var6 = FluidTags.getWrappers().iterator();

      Tag var7;
      do {
         if (!var6.hasNext()) {
            return;
         }

         var7 = (Tag)var6.next();
      } while(!var5.is(var7));

      double var8 = (double)((float)var10.getY() + var5.getHeight(this.level, var10));
      if (var8 > var1) {
         this.fluidOnEyes = var7;
      }

   }

   protected void doWaterSplashEffect() {
      Entity var1 = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
      float var2 = var1 == this ? 0.2F : 0.9F;
      Vec3 var3 = var1.getDeltaMovement();
      float var4 = Mth.sqrt(var3.x * var3.x * 0.20000000298023224D + var3.y * var3.y + var3.z * var3.z * 0.20000000298023224D) * var2;
      if (var4 > 1.0F) {
         var4 = 1.0F;
      }

      if ((double)var4 < 0.25D) {
         this.playSound(this.getSwimSplashSound(), var4, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      } else {
         this.playSound(this.getSwimHighSpeedSplashSound(), var4, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      }

      float var5 = (float)Mth.floor(this.getY());

      int var6;
      double var7;
      double var9;
      for(var6 = 0; (float)var6 < 1.0F + this.dimensions.width * 20.0F; ++var6) {
         var7 = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width;
         var9 = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width;
         this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + var7, (double)(var5 + 1.0F), this.getZ() + var9, var3.x, var3.y - this.random.nextDouble() * 0.20000000298023224D, var3.z);
      }

      for(var6 = 0; (float)var6 < 1.0F + this.dimensions.width * 20.0F; ++var6) {
         var7 = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width;
         var9 = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width;
         this.level.addParticle(ParticleTypes.SPLASH, this.getX() + var7, (double)(var5 + 1.0F), this.getZ() + var9, var3.x, var3.y, var3.z);
      }

   }

   protected BlockState getBlockStateOn() {
      return this.level.getBlockState(this.getOnPos());
   }

   public boolean canSpawnSprintParticle() {
      return this.isSprinting() && !this.isInWater() && !this.isSpectator() && !this.isCrouching() && !this.isInLava() && this.isAlive();
   }

   protected void spawnSprintParticle() {
      int var1 = Mth.floor(this.getX());
      int var2 = Mth.floor(this.getY() - 0.20000000298023224D);
      int var3 = Mth.floor(this.getZ());
      BlockPos var4 = new BlockPos(var1, var2, var3);
      BlockState var5 = this.level.getBlockState(var4);
      if (var5.getRenderShape() != RenderShape.INVISIBLE) {
         Vec3 var6 = this.getDeltaMovement();
         this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var5), this.getX() + (this.random.nextDouble() - 0.5D) * (double)this.dimensions.width, this.getY() + 0.1D, this.getZ() + (this.random.nextDouble() - 0.5D) * (double)this.dimensions.width, var6.x * -4.0D, 1.5D, var6.z * -4.0D);
      }

   }

   public boolean isEyeInFluid(Tag<Fluid> var1) {
      return this.fluidOnEyes == var1;
   }

   public boolean isInLava() {
      return !this.firstTick && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0D;
   }

   public void moveRelative(float var1, Vec3 var2) {
      Vec3 var3 = getInputVector(var2, var1, this.yRot);
      this.setDeltaMovement(this.getDeltaMovement().add(var3));
   }

   private static Vec3 getInputVector(Vec3 var0, float var1, float var2) {
      double var3 = var0.lengthSqr();
      if (var3 < 1.0E-7D) {
         return Vec3.ZERO;
      } else {
         Vec3 var5 = (var3 > 1.0D ? var0.normalize() : var0).scale((double)var1);
         float var6 = Mth.sin(var2 * 0.017453292F);
         float var7 = Mth.cos(var2 * 0.017453292F);
         return new Vec3(var5.x * (double)var7 - var5.z * (double)var6, var5.y, var5.z * (double)var7 + var5.x * (double)var6);
      }
   }

   public float getBrightness() {
      BlockPos.MutableBlockPos var1 = new BlockPos.MutableBlockPos(this.getX(), 0.0D, this.getZ());
      if (this.level.hasChunkAt(var1)) {
         var1.setY(Mth.floor(this.getEyeY()));
         return this.level.getBrightness(var1);
      } else {
         return 0.0F;
      }
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   public void absMoveTo(double var1, double var3, double var5, float var7, float var8) {
      this.absMoveTo(var1, var3, var5);
      this.yRot = var7 % 360.0F;
      this.xRot = Mth.clamp(var8, -90.0F, 90.0F) % 360.0F;
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public void absMoveTo(double var1, double var3, double var5) {
      double var7 = Mth.clamp(var1, -3.0E7D, 3.0E7D);
      double var9 = Mth.clamp(var5, -3.0E7D, 3.0E7D);
      this.xo = var7;
      this.yo = var3;
      this.zo = var9;
      this.setPos(var7, var3, var9);
   }

   public void moveTo(Vec3 var1) {
      this.moveTo(var1.x, var1.y, var1.z);
   }

   public void moveTo(double var1, double var3, double var5) {
      this.moveTo(var1, var3, var5, this.yRot, this.xRot);
   }

   public void moveTo(BlockPos var1, float var2, float var3) {
      this.moveTo((double)var1.getX() + 0.5D, (double)var1.getY(), (double)var1.getZ() + 0.5D, var2, var3);
   }

   public void moveTo(double var1, double var3, double var5, float var7, float var8) {
      this.setPosAndOldPos(var1, var3, var5);
      this.yRot = var7;
      this.xRot = var8;
      this.reapplyPosition();
   }

   public void setPosAndOldPos(double var1, double var3, double var5) {
      this.setPosRaw(var1, var3, var5);
      this.xo = var1;
      this.yo = var3;
      this.zo = var5;
      this.xOld = var1;
      this.yOld = var3;
      this.zOld = var5;
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
            if (var6 >= 0.009999999776482582D) {
               var6 = (double)Mth.sqrt(var6);
               var2 /= var6;
               var4 /= var6;
               double var8 = 1.0D / var6;
               if (var8 > 1.0D) {
                  var8 = 1.0D;
               }

               var2 *= var8;
               var4 *= var8;
               var2 *= 0.05000000074505806D;
               var4 *= 0.05000000074505806D;
               var2 *= (double)(1.0F - this.pushthrough);
               var4 *= (double)(1.0F - this.pushthrough);
               if (!this.isVehicle()) {
                  this.push(-var2, 0.0D, -var4);
               }

               if (!var1.isVehicle()) {
                  var1.push(var2, 0.0D, var4);
               }
            }

         }
      }
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

   public float getViewXRot(float var1) {
      return var1 == 1.0F ? this.xRot : Mth.lerp(var1, this.xRotO, this.xRot);
   }

   public float getViewYRot(float var1) {
      return var1 == 1.0F ? this.yRot : Mth.lerp(var1, this.yRotO, this.yRot);
   }

   protected final Vec3 calculateViewVector(float var1, float var2) {
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

   public final Vec3 getEyePosition(float var1) {
      if (var1 == 1.0F) {
         return new Vec3(this.getX(), this.getEyeY(), this.getZ());
      } else {
         double var2 = Mth.lerp((double)var1, this.xo, this.getX());
         double var4 = Mth.lerp((double)var1, this.yo, this.getY()) + (double)this.getEyeHeight();
         double var6 = Mth.lerp((double)var1, this.zo, this.getZ());
         return new Vec3(var2, var4, var6);
      }
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
      return this.level.clip(new ClipContext(var5, var7, ClipContext.Block.OUTLINE, var4 ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
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
         var3 = 1.0D;
      }

      var3 *= 64.0D * viewScale;
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
         var1.put("Rotation", this.newFloatList(this.yRot, this.xRot));
         var1.putFloat("FallDistance", this.fallDistance);
         var1.putShort("Fire", (short)this.remainingFireTicks);
         var1.putShort("Air", (short)this.getAirSupply());
         var1.putBoolean("OnGround", this.onGround);
         var1.putBoolean("Invulnerable", this.invulnerable);
         var1.putInt("PortalCooldown", this.portalCooldown);
         var1.putUUID("UUID", this.getUUID());
         Component var9 = this.getCustomName();
         if (var9 != null) {
            var1.putString("CustomName", Component.Serializer.toJson(var9));
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

         if (this.glowing) {
            var1.putBoolean("Glowing", this.glowing);
         }

         Iterator var5;
         ListTag var10;
         if (!this.tags.isEmpty()) {
            var10 = new ListTag();
            var5 = this.tags.iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               var10.add(StringTag.valueOf(var6));
            }

            var1.put("Tags", var10);
         }

         this.addAdditionalSaveData(var1);
         if (this.isVehicle()) {
            var10 = new ListTag();
            var5 = this.getPassengers().iterator();

            while(var5.hasNext()) {
               Entity var11 = (Entity)var5.next();
               CompoundTag var7 = new CompoundTag();
               if (var11.saveAsPassenger(var7)) {
                  var10.add(var7);
               }
            }

            if (!var10.isEmpty()) {
               var1.put("Passengers", var10);
            }
         }

         return var1;
      } catch (Throwable var8) {
         CrashReport var3 = CrashReport.forThrowable(var8, "Saving entity NBT");
         CrashReportCategory var4 = var3.addCategory("Entity being saved");
         this.fillCrashReportCategory(var4);
         throw new ReportedException(var3);
      }
   }

   public void load(CompoundTag var1) {
      try {
         ListTag var2 = var1.getList("Pos", 6);
         ListTag var16 = var1.getList("Motion", 6);
         ListTag var17 = var1.getList("Rotation", 5);
         double var5 = var16.getDouble(0);
         double var7 = var16.getDouble(1);
         double var9 = var16.getDouble(2);
         this.setDeltaMovement(Math.abs(var5) > 10.0D ? 0.0D : var5, Math.abs(var7) > 10.0D ? 0.0D : var7, Math.abs(var9) > 10.0D ? 0.0D : var9);
         this.setPosAndOldPos(var2.getDouble(0), var2.getDouble(1), var2.getDouble(2));
         this.yRot = var17.getFloat(0);
         this.xRot = var17.getFloat(1);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
         this.setYHeadRot(this.yRot);
         this.setYBodyRot(this.yRot);
         this.fallDistance = var1.getFloat("FallDistance");
         this.remainingFireTicks = var1.getShort("Fire");
         this.setAirSupply(var1.getShort("Air"));
         this.onGround = var1.getBoolean("OnGround");
         this.invulnerable = var1.getBoolean("Invulnerable");
         this.portalCooldown = var1.getInt("PortalCooldown");
         if (var1.hasUUID("UUID")) {
            this.uuid = var1.getUUID("UUID");
            this.stringUUID = this.uuid.toString();
         }

         if (Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ())) {
            if (Double.isFinite((double)this.yRot) && Double.isFinite((double)this.xRot)) {
               this.reapplyPosition();
               this.setRot(this.yRot, this.xRot);
               if (var1.contains("CustomName", 8)) {
                  String var11 = var1.getString("CustomName");

                  try {
                     this.setCustomName(Component.Serializer.fromJson(var11));
                  } catch (Exception var14) {
                     LOGGER.warn("Failed to parse entity custom name {}", var11, var14);
                  }
               }

               this.setCustomNameVisible(var1.getBoolean("CustomNameVisible"));
               this.setSilent(var1.getBoolean("Silent"));
               this.setNoGravity(var1.getBoolean("NoGravity"));
               this.setGlowing(var1.getBoolean("Glowing"));
               if (var1.contains("Tags", 9)) {
                  this.tags.clear();
                  ListTag var18 = var1.getList("Tags", 8);
                  int var12 = Math.min(var18.size(), 1024);

                  for(int var13 = 0; var13 < var12; ++var13) {
                     this.tags.add(var18.getString(var13));
                  }
               }

               this.readAdditionalSaveData(var1);
               if (this.repositionEntityAfterLoad()) {
                  this.reapplyPosition();
               }

            } else {
               throw new IllegalStateException("Entity has invalid rotation");
            }
         } else {
            throw new IllegalStateException("Entity has invalid position");
         }
      } catch (Throwable var15) {
         CrashReport var3 = CrashReport.forThrowable(var15, "Loading entity NBT");
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
      double[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double var6 = var3[var5];
         var2.add(DoubleTag.valueOf(var6));
      }

      return var2;
   }

   protected ListTag newFloatList(float... var1) {
      ListTag var2 = new ListTag();
      float[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         float var6 = var3[var5];
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
      } else if (this.level.isClientSide) {
         return null;
      } else {
         ItemEntity var3 = new ItemEntity(this.level, this.getX(), this.getY() + (double)var2, this.getZ(), var1);
         var3.setDefaultPickUpDelay();
         this.level.addFreshEntity(var3);
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
         float var1 = 0.1F;
         float var2 = this.dimensions.width * 0.8F;
         AABB var3 = AABB.ofSize((double)var2, 0.10000000149011612D, (double)var2).move(this.getX(), this.getEyeY(), this.getZ());
         return this.level.getBlockCollisions(this, var3, (var1x, var2x) -> {
            return var1x.isSuffocating(this.level, var2x);
         }).findAny().isPresent();
      }
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
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

   public void positionRider(Entity var1) {
      this.positionRider(var1, Entity::setPos);
   }

   private void positionRider(Entity var1, Entity.MoveFunction var2) {
      if (this.hasPassenger(var1)) {
         double var3 = this.getY() + this.getPassengersRidingOffset() + var1.getMyRidingOffset();
         var2.accept(var1, this.getX(), var3, this.getZ());
      }
   }

   public void onPassengerTurned(Entity var1) {
   }

   public double getMyRidingOffset() {
      return 0.0D;
   }

   public double getPassengersRidingOffset() {
      return (double)this.dimensions.height * 0.75D;
   }

   public boolean startRiding(Entity var1) {
      return this.startRiding(var1, false);
   }

   public boolean showVehicleHealth() {
      return this instanceof LivingEntity;
   }

   public boolean startRiding(Entity var1, boolean var2) {
      for(Entity var3 = var1; var3.vehicle != null; var3 = var3.vehicle) {
         if (var3.vehicle == this) {
            return false;
         }
      }

      if (!var2 && (!this.canRide(var1) || !var1.canAddPassenger(this))) {
         return false;
      } else {
         if (this.isPassenger()) {
            this.stopRiding();
         }

         this.setPose(Pose.STANDING);
         this.vehicle = var1;
         this.vehicle.addPassenger(this);
         return true;
      }
   }

   protected boolean canRide(Entity var1) {
      return !this.isShiftKeyDown() && this.boardingCooldown <= 0;
   }

   protected boolean canEnterPose(Pose var1) {
      return this.level.noCollision(this, this.getBoundingBoxForPose(var1).deflate(1.0E-7D));
   }

   public void ejectPassengers() {
      for(int var1 = this.passengers.size() - 1; var1 >= 0; --var1) {
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
            if (!this.level.isClientSide && var1 instanceof Player && !(this.getControllingPassenger() instanceof Player)) {
               var2.add(0, var1);
            } else {
               var2.add(var1);
            }

            this.passengers = ImmutableList.copyOf(var2);
         }

      }
   }

   protected void removePassenger(Entity var1) {
      if (var1.getVehicle() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         if (this.passengers.size() == 1 && this.passengers.get(0) == var1) {
            this.passengers = ImmutableList.of();
         } else {
            this.passengers = (ImmutableList)this.passengers.stream().filter((var1x) -> {
               return var1x != var1;
            }).collect(ImmutableList.toImmutableList());
         }

         var1.boardingCooldown = 60;
      }
   }

   protected boolean canAddPassenger(Entity var1) {
      return this.passengers.isEmpty();
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.setPos(var1, var3, var5);
      this.setRot(var7, var8);
   }

   public void lerpHeadTo(float var1, int var2) {
      this.setYHeadRot(var1);
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public Vec3 getLookAngle() {
      return this.calculateViewVector(this.xRot, this.yRot);
   }

   public Vec2 getRotationVector() {
      return new Vec2(this.xRot, this.yRot);
   }

   public Vec3 getForward() {
      return Vec3.directionFromRotation(this.getRotationVector());
   }

   public void handleInsidePortal(BlockPos var1) {
      if (this.isOnPortalCooldown()) {
         this.setPortalCooldown();
      } else {
         if (!this.level.isClientSide && !var1.equals(this.portalEntrancePos)) {
            this.portalEntrancePos = var1.immutable();
         }

         this.isInsidePortal = true;
      }
   }

   protected void handleNetherPortal() {
      if (this.level instanceof ServerLevel) {
         int var1 = this.getPortalWaitTime();
         ServerLevel var2 = (ServerLevel)this.level;
         if (this.isInsidePortal) {
            MinecraftServer var3 = var2.getServer();
            ResourceKey var4 = this.level.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
            ServerLevel var5 = var3.getLevel(var4);
            if (var5 != null && var3.isNetherEnabled() && !this.isPassenger() && this.portalTime++ >= var1) {
               this.level.getProfiler().push("portal");
               this.portalTime = var1;
               this.setPortalCooldown();
               this.changeDimension(var5);
               this.level.getProfiler().pop();
            }

            this.isInsidePortal = false;
         } else {
            if (this.portalTime > 0) {
               this.portalTime -= 4;
            }

            if (this.portalTime < 0) {
               this.portalTime = 0;
            }
         }

         this.processPortalCooldown();
      }
   }

   public int getDimensionChangingDelay() {
      return 300;
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
   }

   public void handleEntityEvent(byte var1) {
      switch(var1) {
      case 53:
         HoneyBlock.showSlideParticles(this);
      default:
      }
   }

   public void animateHurt() {
   }

   public Iterable<ItemStack> getHandSlots() {
      return EMPTY_LIST;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return EMPTY_LIST;
   }

   public Iterable<ItemStack> getAllSlots() {
      return Iterables.concat(this.getHandSlots(), this.getArmorSlots());
   }

   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
   }

   public boolean isOnFire() {
      boolean var1 = this.level != null && this.level.isClientSide;
      return !this.fireImmune() && (this.remainingFireTicks > 0 || var1 && this.getSharedFlag(0));
   }

   public boolean isPassenger() {
      return this.getVehicle() != null;
   }

   public boolean isVehicle() {
      return !this.passengers.isEmpty();
   }

   public boolean rideableUnderWater() {
      return true;
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
      return this.getPose() == Pose.CROUCHING;
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
      return this.getPose() == Pose.SWIMMING;
   }

   public boolean isVisuallyCrawling() {
      return this.isVisuallySwimming() && !this.isInWater();
   }

   public void setSwimming(boolean var1) {
      this.setSharedFlag(4, var1);
   }

   public boolean isGlowing() {
      return this.glowing || this.level.isClientSide && this.getSharedFlag(6);
   }

   public void setGlowing(boolean var1) {
      this.glowing = var1;
      if (!this.level.isClientSide) {
         this.setSharedFlag(6, this.glowing);
      }

   }

   public boolean isInvisible() {
      return this.getSharedFlag(5);
   }

   public boolean isInvisibleTo(Player var1) {
      if (var1.isSpectator()) {
         return false;
      } else {
         Team var2 = this.getTeam();
         return var2 != null && var1 != null && var1.getTeam() == var2 && var2.canSeeFriendlyInvisibles() ? false : this.isInvisible();
      }
   }

   @Nullable
   public Team getTeam() {
      return this.level.getScoreboard().getPlayersTeam(this.getScoreboardName());
   }

   public boolean isAlliedTo(Entity var1) {
      return this.isAlliedTo(var1.getTeam());
   }

   public boolean isAlliedTo(Team var1) {
      return this.getTeam() != null ? this.getTeam().isAlliedTo(var1) : false;
   }

   public void setInvisible(boolean var1) {
      this.setSharedFlag(5, var1);
   }

   protected boolean getSharedFlag(int var1) {
      return ((Byte)this.entityData.get(DATA_SHARED_FLAGS_ID) & 1 << var1) != 0;
   }

   protected void setSharedFlag(int var1, boolean var2) {
      byte var3 = (Byte)this.entityData.get(DATA_SHARED_FLAGS_ID);
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
      return (Integer)this.entityData.get(DATA_AIR_SUPPLY_ID);
   }

   public void setAirSupply(int var1) {
      this.entityData.set(DATA_AIR_SUPPLY_ID, var1);
   }

   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      this.setRemainingFireTicks(this.remainingFireTicks + 1);
      if (this.remainingFireTicks == 0) {
         this.setSecondsOnFire(8);
      }

      this.hurt(DamageSource.LIGHTNING_BOLT, 5.0F);
   }

   public void onAboveBubbleCol(boolean var1) {
      Vec3 var2 = this.getDeltaMovement();
      double var3;
      if (var1) {
         var3 = Math.max(-0.9D, var2.y - 0.03D);
      } else {
         var3 = Math.min(1.8D, var2.y + 0.1D);
      }

      this.setDeltaMovement(var2.x, var3, var2.z);
   }

   public void onInsideBubbleColumn(boolean var1) {
      Vec3 var2 = this.getDeltaMovement();
      double var3;
      if (var1) {
         var3 = Math.max(-0.3D, var2.y - 0.03D);
      } else {
         var3 = Math.min(0.7D, var2.y + 0.06D);
      }

      this.setDeltaMovement(var2.x, var3, var2.z);
      this.fallDistance = 0.0F;
   }

   public void killed(ServerLevel var1, LivingEntity var2) {
   }

   protected void moveTowardsClosestSpace(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      Vec3 var8 = new Vec3(var1 - (double)var7.getX(), var3 - (double)var7.getY(), var5 - (double)var7.getZ());
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
      Direction var10 = Direction.UP;
      double var11 = 1.7976931348623157E308D;
      Direction[] var13 = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};
      int var14 = var13.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         Direction var16 = var13[var15];
         var9.setWithOffset(var7, var16);
         if (!this.level.getBlockState(var9).isCollisionShapeFullBlock(this.level, var9)) {
            double var17 = var8.get(var16.getAxis());
            double var19 = var16.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - var17 : var17;
            if (var19 < var11) {
               var11 = var19;
               var10 = var16;
            }
         }
      }

      float var21 = this.random.nextFloat() * 0.2F + 0.1F;
      float var22 = (float)var10.getAxisDirection().getStep();
      Vec3 var23 = this.getDeltaMovement().scale(0.75D);
      if (var10.getAxis() == Direction.Axis.X) {
         this.setDeltaMovement((double)(var22 * var21), var23.y, var23.z);
      } else if (var10.getAxis() == Direction.Axis.Y) {
         this.setDeltaMovement(var23.x, (double)(var22 * var21), var23.z);
      } else if (var10.getAxis() == Direction.Axis.Z) {
         this.setDeltaMovement(var23.x, var23.y, (double)(var22 * var21));
      }

   }

   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
      this.fallDistance = 0.0F;
      this.stuckSpeedMultiplier = var2;
   }

   private static Component removeAction(Component var0) {
      MutableComponent var1 = var0.plainCopy().setStyle(var0.getStyle().withClickEvent((ClickEvent)null));
      Iterator var2 = var0.getSiblings().iterator();

      while(var2.hasNext()) {
         Component var3 = (Component)var2.next();
         var1.append(removeAction(var3));
      }

      return var1;
   }

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

   public String toString() {
      return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getString(), this.id, this.level == null ? "~NULL~" : this.level.toString(), this.getX(), this.getY(), this.getZ());
   }

   public boolean isInvulnerableTo(DamageSource var1) {
      return this.invulnerable && var1 != DamageSource.OUT_OF_WORLD && !var1.isCreativePlayer();
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean var1) {
      this.invulnerable = var1;
   }

   public void copyPosition(Entity var1) {
      this.moveTo(var1.getX(), var1.getY(), var1.getZ(), var1.yRot, var1.xRot);
   }

   public void restoreFrom(Entity var1) {
      CompoundTag var2 = var1.saveWithoutId(new CompoundTag());
      var2.remove("Dimension");
      this.load(var2);
      this.portalCooldown = var1.portalCooldown;
      this.portalEntrancePos = var1.portalEntrancePos;
   }

   @Nullable
   public Entity changeDimension(ServerLevel var1) {
      if (this.level instanceof ServerLevel && !this.isRemoved()) {
         this.level.getProfiler().push("changeDimension");
         this.unRide();
         this.level.getProfiler().push("reposition");
         PortalInfo var2 = this.findDimensionEntryPoint(var1);
         if (var2 == null) {
            return null;
         } else {
            this.level.getProfiler().popPush("reloading");
            Entity var3 = this.getType().create(var1);
            if (var3 != null) {
               var3.restoreFrom(this);
               var3.moveTo(var2.pos.x, var2.pos.y, var2.pos.z, var2.yRot, var3.xRot);
               var3.setDeltaMovement(var2.speed);
               var1.addAndForceLoad(var3);
               if (var1.dimension() == Level.END) {
                  ServerLevel.makeObsidianPlatform(var1);
               }
            }

            this.removeAfterChangingDimensions();
            this.level.getProfiler().pop();
            ((ServerLevel)this.level).resetEmptyTime();
            var1.resetEmptyTime();
            this.level.getProfiler().pop();
            return var3;
         }
      } else {
         return null;
      }
   }

   protected void removeAfterChangingDimensions() {
      this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
   }

   @Nullable
   protected PortalInfo findDimensionEntryPoint(ServerLevel var1) {
      boolean var2 = this.level.dimension() == Level.END && var1.dimension() == Level.OVERWORLD;
      boolean var3 = var1.dimension() == Level.END;
      if (!var2 && !var3) {
         boolean var17 = var1.dimension() == Level.NETHER;
         if (this.level.dimension() != Level.NETHER && !var17) {
            return null;
         } else {
            WorldBorder var5 = var1.getWorldBorder();
            double var6 = Math.max(-2.9999872E7D, var5.getMinX() + 16.0D);
            double var8 = Math.max(-2.9999872E7D, var5.getMinZ() + 16.0D);
            double var10 = Math.min(2.9999872E7D, var5.getMaxX() - 16.0D);
            double var12 = Math.min(2.9999872E7D, var5.getMaxZ() - 16.0D);
            double var14 = DimensionType.getTeleportationScale(this.level.dimensionType(), var1.dimensionType());
            BlockPos var16 = new BlockPos(Mth.clamp(this.getX() * var14, var6, var10), this.getY(), Mth.clamp(this.getZ() * var14, var8, var12));
            return (PortalInfo)this.getExitPortal(var1, var16, var17).map((var2x) -> {
               BlockState var5 = this.level.getBlockState(this.portalEntrancePos);
               Direction.Axis var3;
               Vec3 var4;
               if (var5.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                  var3 = (Direction.Axis)var5.getValue(BlockStateProperties.HORIZONTAL_AXIS);
                  BlockUtil.FoundRectangle var6 = BlockUtil.getLargestRectangleAround(this.portalEntrancePos, var3, 21, Direction.Axis.Y, 21, (var2) -> {
                     return this.level.getBlockState(var2) == var5;
                  });
                  var4 = this.getRelativePortalPosition(var3, var6);
               } else {
                  var3 = Direction.Axis.X;
                  var4 = new Vec3(0.5D, 0.0D, 0.0D);
               }

               return PortalShape.createPortalInfo(var1, var2x, var3, var4, this.getDimensions(this.getPose()), this.getDeltaMovement(), this.yRot, this.xRot);
            }).orElse((Object)null);
         }
      } else {
         BlockPos var4;
         if (var3) {
            var4 = ServerLevel.END_SPAWN_POINT;
         } else {
            var4 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var1.getSharedSpawnPos());
         }

         return new PortalInfo(new Vec3((double)var4.getX() + 0.5D, (double)var4.getY(), (double)var4.getZ() + 0.5D), this.getDeltaMovement(), this.yRot, this.xRot);
      }
   }

   protected Vec3 getRelativePortalPosition(Direction.Axis var1, BlockUtil.FoundRectangle var2) {
      return PortalShape.getRelativePosition(var2, var1, this.position(), this.getDimensions(this.getPose()));
   }

   protected Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel var1, BlockPos var2, boolean var3) {
      return var1.getPortalForcer().findPortalAround(var2, var3);
   }

   public boolean canChangeDimensions() {
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
      var1.setDetail("Entity Type", () -> {
         return EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")";
      });
      var1.setDetail("Entity ID", (Object)this.id);
      var1.setDetail("Entity Name", () -> {
         return this.getName().getString();
      });
      var1.setDetail("Entity's Exact location", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
      var1.setDetail("Entity's Block location", (Object)CrashReportCategory.formatLocation(this.level, Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ())));
      Vec3 var2 = this.getDeltaMovement();
      var1.setDetail("Entity's Momentum", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", var2.x, var2.y, var2.z));
      var1.setDetail("Entity's Passengers", () -> {
         return this.getPassengers().toString();
      });
      var1.setDetail("Entity's Vehicle", () -> {
         return this.getVehicle().toString();
      });
   }

   public boolean displayFireAnimation() {
      return this.isOnFire() && !this.isSpectator();
   }

   public void setUUID(UUID var1) {
      this.uuid = var1;
      this.stringUUID = this.uuid.toString();
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public String getStringUUID() {
      return this.stringUUID;
   }

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

   public Component getDisplayName() {
      return PlayerTeam.formatNameForTeam(this.getTeam(), this.getName()).withStyle((var1) -> {
         return var1.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID());
      });
   }

   public void setCustomName(@Nullable Component var1) {
      this.entityData.set(DATA_CUSTOM_NAME, Optional.ofNullable(var1));
   }

   @Nullable
   public Component getCustomName() {
      return (Component)((Optional)this.entityData.get(DATA_CUSTOM_NAME)).orElse((Object)null);
   }

   public boolean hasCustomName() {
      return ((Optional)this.entityData.get(DATA_CUSTOM_NAME)).isPresent();
   }

   public void setCustomNameVisible(boolean var1) {
      this.entityData.set(DATA_CUSTOM_NAME_VISIBLE, var1);
   }

   public boolean isCustomNameVisible() {
      return (Boolean)this.entityData.get(DATA_CUSTOM_NAME_VISIBLE);
   }

   public final void teleportToWithTicket(double var1, double var3, double var5) {
      if (this.level instanceof ServerLevel) {
         ChunkPos var7 = new ChunkPos(new BlockPos(var1, var3, var5));
         ((ServerLevel)this.level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, var7, 0, this.getId());
         this.level.getChunk(var7.x, var7.z);
         this.teleportTo(var1, var3, var5);
      }
   }

   public void teleportTo(double var1, double var3, double var5) {
      if (this.level instanceof ServerLevel) {
         ServerLevel var7 = (ServerLevel)this.level;
         this.moveTo(var1, var3, var5, this.yRot, this.xRot);
         this.getSelfAndPassengers().forEach((var0) -> {
            UnmodifiableIterator var1 = var0.passengers.iterator();

            while(var1.hasNext()) {
               Entity var2 = (Entity)var1.next();
               var0.positionRider(var2, Entity::moveTo);
            }

         });
      }
   }

   public boolean shouldShowName() {
      return this.isCustomNameVisible();
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_POSE.equals(var1)) {
         this.refreshDimensions();
      }

   }

   public void refreshDimensions() {
      EntityDimensions var1 = this.dimensions;
      Pose var2 = this.getPose();
      EntityDimensions var3 = this.getDimensions(var2);
      this.dimensions = var3;
      this.eyeHeight = this.getEyeHeight(var2, var3);
      if (var3.width < var1.width) {
         double var6 = (double)var3.width / 2.0D;
         this.setBoundingBox(new AABB(this.getX() - var6, this.getY(), this.getZ() - var6, this.getX() + var6, this.getY() + (double)var3.height, this.getZ() + var6));
      } else {
         AABB var4 = this.getBoundingBox();
         this.setBoundingBox(new AABB(var4.minX, var4.minY, var4.minZ, var4.minX + (double)var3.width, var4.minY + (double)var3.height, var4.minZ + (double)var3.width));
         if (var3.width > var1.width && !this.firstTick && !this.level.isClientSide) {
            float var5 = var1.width - var3.width;
            this.move(MoverType.SELF, new Vec3((double)var5, 0.0D, (double)var5));
         }

      }
   }

   public Direction getDirection() {
      return Direction.fromYRot((double)this.yRot);
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

   public AABB getBoundingBox() {
      return this.bb;
   }

   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox();
   }

   protected AABB getBoundingBoxForPose(Pose var1) {
      EntityDimensions var2 = this.getDimensions(var1);
      float var3 = var2.width / 2.0F;
      Vec3 var4 = new Vec3(this.getX() - (double)var3, this.getY(), this.getZ() - (double)var3);
      Vec3 var5 = new Vec3(this.getX() + (double)var3, this.getY() + (double)var2.height, this.getZ() + (double)var3);
      return new AABB(var4, var5);
   }

   public void setBoundingBox(AABB var1) {
      this.bb = var1;
   }

   protected float getEyeHeight(Pose var1, EntityDimensions var2) {
      return var2.height * 0.85F;
   }

   public float getEyeHeight(Pose var1) {
      return this.getEyeHeight(var1, this.getDimensions(var1));
   }

   public final float getEyeHeight() {
      return this.eyeHeight;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)this.getEyeHeight(), (double)(this.getBbWidth() * 0.4F));
   }

   public boolean setSlot(int var1, ItemStack var2) {
      return false;
   }

   public void sendMessage(Component var1, UUID var2) {
   }

   public Level getCommandSenderWorld() {
      return this.level;
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.level.getServer();
   }

   public InteractionResult interactAt(Player var1, Vec3 var2, InteractionHand var3) {
      return InteractionResult.PASS;
   }

   public boolean ignoreExplosion() {
      return false;
   }

   public void doEnchantDamageEffects(LivingEntity var1, Entity var2) {
      if (var2 instanceof LivingEntity) {
         EnchantmentHelper.doPostHurtEffects((LivingEntity)var2, var1);
      }

      EnchantmentHelper.doPostDamageEffects(var1, var2);
   }

   public void startSeenByPlayer(ServerPlayer var1) {
   }

   public void stopSeenByPlayer(ServerPlayer var1) {
   }

   public float rotate(Rotation var1) {
      float var2 = Mth.wrapDegrees(this.yRot);
      switch(var1) {
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
      float var2 = Mth.wrapDegrees(this.yRot);
      switch(var1) {
      case LEFT_RIGHT:
         return -var2;
      case FRONT_BACK:
         return 180.0F - var2;
      default:
         return var2;
      }
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return null;
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

      Entity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Entity)var2.next();
      } while(!var1.test(var3));

      return true;
   }

   private Stream<Entity> getIndirectPassengersStream() {
      return this.passengers.stream().flatMap(Entity::getSelfAndPassengers);
   }

   public Stream<Entity> getSelfAndPassengers() {
      return Stream.concat(Stream.of(this), this.getIndirectPassengersStream());
   }

   public Stream<Entity> getPassengersAndSelf() {
      return Stream.concat(this.passengers.stream().flatMap(Entity::getPassengersAndSelf), Stream.of(this));
   }

   public Iterable<Entity> getIndirectPassengers() {
      return () -> {
         return this.getIndirectPassengersStream().iterator();
      };
   }

   public boolean hasExactlyOnePlayerPassenger() {
      return this.getIndirectPassengersStream().filter((var0) -> {
         return var0 instanceof Player;
      }).count() == 1L;
   }

   public Entity getRootVehicle() {
      Entity var1;
      for(var1 = this; var1.isPassenger(); var1 = var1.getVehicle()) {
      }

      return var1;
   }

   public boolean isPassengerOfSameVehicle(Entity var1) {
      return this.getRootVehicle() == var1.getRootVehicle();
   }

   public boolean hasIndirectPassenger(Entity var1) {
      return this.getIndirectPassengersStream().anyMatch((var1x) -> {
         return var1x == var1;
      });
   }

   public boolean isControlledByLocalInstance() {
      Entity var1 = this.getControllingPassenger();
      if (var1 instanceof Player) {
         return ((Player)var1).isLocalPlayer();
      } else {
         return !this.level.isClientSide;
      }
   }

   protected static Vec3 getCollisionHorizontalEscapeVector(double var0, double var2, float var4) {
      double var5 = (var0 + var2 + 9.999999747378752E-6D) / 2.0D;
      float var7 = -Mth.sin(var4 * 0.017453292F);
      float var8 = Mth.cos(var4 * 0.017453292F);
      float var9 = Math.max(Math.abs(var7), Math.abs(var8));
      return new Vec3((double)var7 * var5 / (double)var9, 0.0D, (double)var8 * var5 / (double)var9);
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
   }

   @Nullable
   public Entity getVehicle() {
      return this.vehicle;
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
      return new CommandSourceStack(this, this.position(), this.getRotationVector(), this.level instanceof ServerLevel ? (ServerLevel)this.level : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
   }

   protected int getPermissionLevel() {
      return 0;
   }

   public boolean hasPermissions(int var1) {
      return this.getPermissionLevel() >= var1;
   }

   public boolean acceptsSuccess() {
      return this.level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
   }

   public boolean acceptsFailure() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public void lookAt(EntityAnchorArgument.Anchor var1, Vec3 var2) {
      Vec3 var3 = var1.apply(this);
      double var4 = var2.x - var3.x;
      double var6 = var2.y - var3.y;
      double var8 = var2.z - var3.z;
      double var10 = (double)Mth.sqrt(var4 * var4 + var8 * var8);
      this.xRot = Mth.wrapDegrees((float)(-(Mth.atan2(var6, var10) * 57.2957763671875D)));
      this.yRot = Mth.wrapDegrees((float)(Mth.atan2(var8, var4) * 57.2957763671875D) - 90.0F);
      this.setYHeadRot(this.yRot);
      this.xRotO = this.xRot;
      this.yRotO = this.yRot;
   }

   public boolean updateFluidHeightAndDoFluidPushing(Tag<Fluid> var1, double var2) {
      AABB var4 = this.getBoundingBox().deflate(0.001D);
      int var5 = Mth.floor(var4.minX);
      int var6 = Mth.ceil(var4.maxX);
      int var7 = Mth.floor(var4.minY);
      int var8 = Mth.ceil(var4.maxY);
      int var9 = Mth.floor(var4.minZ);
      int var10 = Mth.ceil(var4.maxZ);
      if (!this.level.hasChunksAt(var5, var7, var9, var6, var8, var10)) {
         return false;
      } else {
         double var11 = 0.0D;
         boolean var13 = this.isPushedByFluid();
         boolean var14 = false;
         Vec3 var15 = Vec3.ZERO;
         int var16 = 0;
         BlockPos.MutableBlockPos var17 = new BlockPos.MutableBlockPos();

         for(int var18 = var5; var18 < var6; ++var18) {
            for(int var19 = var7; var19 < var8; ++var19) {
               for(int var20 = var9; var20 < var10; ++var20) {
                  var17.set(var18, var19, var20);
                  FluidState var21 = this.level.getFluidState(var17);
                  if (var21.is(var1)) {
                     double var22 = (double)((float)var19 + var21.getHeight(this.level, var17));
                     if (var22 >= var4.minY) {
                        var14 = true;
                        var11 = Math.max(var22 - var4.minY, var11);
                        if (var13) {
                           Vec3 var24 = var21.getFlow(this.level, var17);
                           if (var11 < 0.4D) {
                              var24 = var24.scale(var11);
                           }

                           var15 = var15.add(var24);
                           ++var16;
                        }
                     }
                  }
               }
            }
         }

         if (var15.length() > 0.0D) {
            if (var16 > 0) {
               var15 = var15.scale(1.0D / (double)var16);
            }

            if (!(this instanceof Player)) {
               var15 = var15.normalize();
            }

            Vec3 var25 = this.getDeltaMovement();
            var15 = var15.scale(var2 * 1.0D);
            double var26 = 0.003D;
            if (Math.abs(var25.x) < 0.003D && Math.abs(var25.z) < 0.003D && var15.length() < 0.0045000000000000005D) {
               var15 = var15.normalize().scale(0.0045000000000000005D);
            }

            this.setDeltaMovement(this.getDeltaMovement().add(var15));
         }

         this.fluidHeight.put(var1, var11);
         return var14;
      }
   }

   public double getFluidHeight(Tag<Fluid> var1) {
      return this.fluidHeight.getDouble(var1);
   }

   public double getFluidJumpThreshold() {
      return (double)this.getEyeHeight() < 0.4D ? 0.0D : 0.4D;
   }

   public final float getBbWidth() {
      return this.dimensions.width;
   }

   public final float getBbHeight() {
      return this.dimensions.height;
   }

   public abstract Packet<?> getAddEntityPacket();

   public EntityDimensions getDimensions(Pose var1) {
      return this.type.getDimensions();
   }

   public Vec3 position() {
      return this.position;
   }

   public BlockPos blockPosition() {
      return this.blockPosition;
   }

   public ChunkPos chunkPosition() {
      return new ChunkPos(this.blockPosition);
   }

   public Vec3 getDeltaMovement() {
      return this.deltaMovement;
   }

   public void setDeltaMovement(Vec3 var1) {
      this.deltaMovement = var1;
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
      return this.getX((2.0D * this.random.nextDouble() - 1.0D) * var1);
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
      return this.getZ((2.0D * this.random.nextDouble() - 1.0D) * var1);
   }

   public void setPosRaw(double var1, double var3, double var5) {
      if (this.position.x != var1 || this.position.y != var3 || this.position.z != var5) {
         this.position = new Vec3(var1, var3, var5);
         int var7 = Mth.floor(var1);
         int var8 = Mth.floor(var3);
         int var9 = Mth.floor(var5);
         if (var7 != this.blockPosition.getX() || var8 != this.blockPosition.getY() || var9 != this.blockPosition.getZ()) {
            this.blockPosition = new BlockPos(var7, var8, var9);
         }

         this.levelCallback.onMove();
      }

   }

   public void checkDespawn() {
   }

   public Vec3 getRopeHoldPosition(float var1) {
      return this.getPosition(var1).add(0.0D, (double)this.eyeHeight * 0.7D, 0.0D);
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      int var2 = var1.getId();
      double var3 = var1.getX();
      double var5 = var1.getY();
      double var7 = var1.getZ();
      this.setPacketCoordinates(var3, var5, var7);
      this.moveTo(var3, var5, var7);
      this.xRot = (float)(var1.getxRot() * 360) / 256.0F;
      this.yRot = (float)(var1.getyRot() * 360) / 256.0F;
      this.setId(var2);
      this.setUUID(var1.getUUID());
   }

   @Nullable
   public ItemStack getPickResult() {
      return null;
   }

   public final boolean isRemoved() {
      return this.removalReason != null;
   }

   public void setRemoved(Entity.RemovalReason var1) {
      if (this.removalReason == null) {
         this.removalReason = var1;
      }

      this.getPassengers().forEach(Entity::stopRiding);
      this.levelCallback.onRemove(var1);
   }

   protected void unsetRemoved() {
      this.removalReason = null;
   }

   public void setLevelCallback(EntityInLevelCallback var1) {
      this.levelCallback = var1;
   }

   public boolean shouldBeSaved() {
      if (this.removalReason != null && !this.removalReason.shouldSave()) {
         return false;
      } else if (this.isPassenger()) {
         return false;
      } else {
         return !this.isVehicle() || !this.hasExactlyOnePlayerPassenger();
      }
   }

   public boolean isAlwaysTicking() {
      return false;
   }

   static {
      DATA_SHARED_FLAGS_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BYTE);
      DATA_AIR_SUPPLY_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
      DATA_CUSTOM_NAME = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
      DATA_CUSTOM_NAME_VISIBLE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
      DATA_SILENT = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
      DATA_NO_GRAVITY = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
      DATA_POSE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.POSE);
   }

   public static enum RemovalReason {
      KILLED(true, false),
      DISCARDED(true, false),
      UNLOADED_TO_CHUNK(false, true),
      UNLOADED_WITH_PLAYER(false, false),
      CHANGED_DIMENSION(false, false);

      private final boolean destroy;
      private final boolean save;

      private RemovalReason(boolean var3, boolean var4) {
         this.destroy = var3;
         this.save = var4;
      }

      public boolean shouldDestroy() {
         return this.destroy;
      }

      public boolean shouldSave() {
         return this.save;
      }
   }

   @FunctionalInterface
   public interface MoveFunction {
      void accept(Entity var1, double var2, double var4, double var6);
   }
}
