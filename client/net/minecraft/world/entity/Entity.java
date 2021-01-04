package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.global.LightningBolt;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
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

public abstract class Entity implements Nameable, CommandSource {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
   private static final List<ItemStack> EMPTY_LIST = Collections.emptyList();
   private static final AABB INITIAL_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static double viewScale = 1.0D;
   private final EntityType<?> type;
   private int id;
   public boolean blocksBuilding;
   private final List<Entity> passengers;
   protected int boardingCooldown;
   private Entity vehicle;
   public boolean forcedLoading;
   public Level level;
   public double xo;
   public double yo;
   public double zo;
   public double x;
   public double y;
   public double z;
   private Vec3 deltaMovement;
   public float yRot;
   public float xRot;
   public float yRotO;
   public float xRotO;
   private AABB bb;
   public boolean onGround;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean collision;
   public boolean hurtMarked;
   protected Vec3 stuckSpeedMultiplier;
   public boolean removed;
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
   protected boolean wasInWater;
   protected double waterHeight;
   protected boolean wasUnderWater;
   protected boolean isInLava;
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
   public boolean inChunk;
   public int xChunk;
   public int yChunk;
   public int zChunk;
   public long xp;
   public long yp;
   public long zp;
   public boolean noCulling;
   public boolean hasImpulse;
   public int changingDimensionDelay;
   protected boolean isInsidePortal;
   protected int portalTime;
   public DimensionType dimension;
   protected BlockPos portalEntranceBlock;
   protected Vec3 portalEntranceOffset;
   protected Direction portalEntranceForwards;
   private boolean invulnerable;
   protected UUID uuid;
   protected String stringUUID;
   protected boolean glowing;
   private final Set<String> tags;
   private boolean teleported;
   private final double[] pistonDeltas;
   private long pistonDeltasGameTime;
   private EntityDimensions dimensions;
   private float eyeHeight;

   public Entity(EntityType<?> var1, Level var2) {
      super();
      this.id = ENTITY_COUNTER.incrementAndGet();
      this.passengers = Lists.newArrayList();
      this.deltaMovement = Vec3.ZERO;
      this.bb = INITIAL_AABB;
      this.stuckSpeedMultiplier = Vec3.ZERO;
      this.nextStep = 1.0F;
      this.nextFlap = 1.0F;
      this.random = new Random();
      this.remainingFireTicks = -this.getFireImmuneTicks();
      this.firstTick = true;
      this.uuid = Mth.createInsecureUUID(this.random);
      this.stringUUID = this.uuid.toString();
      this.tags = Sets.newHashSet();
      this.pistonDeltas = new double[]{0.0D, 0.0D, 0.0D};
      this.type = var1;
      this.level = var2;
      this.dimensions = var1.getDimensions();
      this.setPos(0.0D, 0.0D, 0.0D);
      if (var2 != null) {
         this.dimension = var2.dimension.getType();
      }

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
      this.xp = ClientboundMoveEntityPacket.entityToPacket(var1);
      this.yp = ClientboundMoveEntityPacket.entityToPacket(var3);
      this.zp = ClientboundMoveEntityPacket.entityToPacket(var5);
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
      this.remove();
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
         while(this.y > 0.0D && this.y < 256.0D) {
            this.setPos(this.x, this.y, this.z);
            if (this.level.noCollision(this)) {
               break;
            }

            ++this.y;
         }

         this.setDeltaMovement(Vec3.ZERO);
         this.xRot = 0.0F;
      }
   }

   public void remove() {
      this.removed = true;
   }

   protected void setPose(Pose var1) {
      this.entityData.set(DATA_POSE, var1);
   }

   public Pose getPose() {
      return (Pose)this.entityData.get(DATA_POSE);
   }

   protected void setRot(float var1, float var2) {
      this.yRot = var1 % 360.0F;
      this.xRot = var2 % 360.0F;
   }

   public void setPos(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      float var7 = this.dimensions.width / 2.0F;
      float var8 = this.dimensions.height;
      this.setBoundingBox(new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
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
      if (this.isPassenger() && this.getVehicle().removed) {
         this.stopRiding();
      }

      if (this.boardingCooldown > 0) {
         --this.boardingCooldown;
      }

      this.walkDistO = this.walkDist;
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.xRotO = this.xRot;
      this.yRotO = this.yRot;
      this.handleNetherPortal();
      this.updateSprintingState();
      this.updateWaterState();
      if (this.level.isClientSide) {
         this.clearFire();
      } else if (this.remainingFireTicks > 0) {
         if (this.fireImmune()) {
            this.remainingFireTicks -= 4;
            if (this.remainingFireTicks < 0) {
               this.clearFire();
            }
         } else {
            if (this.remainingFireTicks % 20 == 0) {
               this.hurt(DamageSource.ON_FIRE, 1.0F);
            }

            --this.remainingFireTicks;
         }
      }

      if (this.isInLava()) {
         this.lavaHurt();
         this.fallDistance *= 0.5F;
      }

      if (this.y < -64.0D) {
         this.outOfWorld();
      }

      if (!this.level.isClientSide) {
         this.setSharedFlag(0, this.remainingFireTicks > 0);
      }

      this.firstTick = false;
      this.level.getProfiler().pop();
   }

   protected void processDimensionDelay() {
      if (this.changingDimensionDelay > 0) {
         --this.changingDimensionDelay;
      }

   }

   public int getPortalWaitTime() {
      return 1;
   }

   protected void lavaHurt() {
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
         this.remainingFireTicks = var2;
      }

   }

   public void setRemainingFireTicks(int var1) {
      this.remainingFireTicks = var1;
   }

   public int getRemainingFireTicks() {
      return this.remainingFireTicks;
   }

   public void clearFire() {
      this.remainingFireTicks = 0;
   }

   protected void outOfWorld() {
      this.remove();
   }

   public boolean isFree(double var1, double var3, double var5) {
      return this.isFree(this.getBoundingBox().move(var1, var3, var5));
   }

   private boolean isFree(AABB var1) {
      return this.level.noCollision(this, var1) && !this.level.containsAnyLiquid(var1);
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

         var2 = this.applySneaking(var2, var1);
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
         this.collision = this.horizontalCollision || this.verticalCollision;
         int var4 = Mth.floor(this.x);
         int var5 = Mth.floor(this.y - 0.20000000298023224D);
         int var6 = Mth.floor(this.z);
         BlockPos var7 = new BlockPos(var4, var5, var6);
         BlockState var8 = this.level.getBlockState(var7);
         if (var8.isAir()) {
            BlockPos var9 = var7.below();
            BlockState var10 = this.level.getBlockState(var9);
            Block var11 = var10.getBlock();
            if (var11.is(BlockTags.FENCES) || var11.is(BlockTags.WALLS) || var11 instanceof FenceGateBlock) {
               var8 = var10;
               var7 = var9;
            }
         }

         this.checkFallDamage(var3.y, this.onGround, var8, var7);
         Vec3 var22 = this.getDeltaMovement();
         if (var2.x != var3.x) {
            this.setDeltaMovement(0.0D, var22.y, var22.z);
         }

         if (var2.z != var3.z) {
            this.setDeltaMovement(var22.x, var22.y, 0.0D);
         }

         Block var23 = var8.getBlock();
         if (var2.y != var3.y) {
            var23.updateEntityAfterFallOn(this.level, this);
         }

         if (this.makeStepSound() && (!this.onGround || !this.isSneaking() || !(this instanceof Player)) && !this.isPassenger()) {
            double var24 = var3.x;
            double var13 = var3.y;
            double var15 = var3.z;
            if (var23 != Blocks.LADDER && var23 != Blocks.SCAFFOLDING) {
               var13 = 0.0D;
            }

            if (this.onGround) {
               var23.stepOn(this.level, var7, this);
            }

            this.walkDist = (float)((double)this.walkDist + (double)Mth.sqrt(getHorizontalDistanceSqr(var3)) * 0.6D);
            this.moveDist = (float)((double)this.moveDist + (double)Mth.sqrt(var24 * var24 + var13 * var13 + var15 * var15) * 0.6D);
            if (this.moveDist > this.nextStep && !var8.isAir()) {
               this.nextStep = this.nextStep();
               if (this.isInWater()) {
                  Entity var17 = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                  float var18 = var17 == this ? 0.35F : 0.4F;
                  Vec3 var19 = var17.getDeltaMovement();
                  float var20 = Mth.sqrt(var19.x * var19.x * 0.20000000298023224D + var19.y * var19.y + var19.z * var19.z * 0.20000000298023224D) * var18;
                  if (var20 > 1.0F) {
                     var20 = 1.0F;
                  }

                  this.playSwimSound(var20);
               } else {
                  this.playStepSound(var7, var8);
               }
            } else if (this.moveDist > this.nextFlap && this.makeFlySound() && var8.isAir()) {
               this.nextFlap = this.playFlySound(this.moveDist);
            }
         }

         try {
            this.isInLava = false;
            this.checkInsideBlocks();
         } catch (Throwable var21) {
            CrashReport var12 = CrashReport.forThrowable(var21, "Checking entity block collision");
            CrashReportCategory var26 = var12.addCategory("Entity being checked for collision");
            this.fillCrashReportCategory(var26);
            throw new ReportedException(var12);
         }

         boolean var25 = this.isInWaterRainOrBubble();
         if (this.level.containsFireBlock(this.getBoundingBox().deflate(0.001D))) {
            if (!var25) {
               ++this.remainingFireTicks;
               if (this.remainingFireTicks == 0) {
                  this.setSecondsOnFire(8);
               }
            }

            this.burn(1);
         } else if (this.remainingFireTicks <= 0) {
            this.remainingFireTicks = -this.getFireImmuneTicks();
         }

         if (var25 && this.isOnFire()) {
            this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            this.remainingFireTicks = -this.getFireImmuneTicks();
         }

         this.level.getProfiler().pop();
      }
   }

   protected Vec3 applySneaking(Vec3 var1, MoverType var2) {
      if (this instanceof Player && (var2 == MoverType.SELF || var2 == MoverType.PLAYER) && this.onGround && this.isSneaking()) {
         double var3 = var1.x;
         double var5 = var1.z;
         double var7 = 0.05D;

         while(true) {
            while(var3 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(var3, (double)(-this.maxUpStep), 0.0D))) {
               if (var3 < 0.05D && var3 >= -0.05D) {
                  var3 = 0.0D;
               } else if (var3 > 0.0D) {
                  var3 -= 0.05D;
               } else {
                  var3 += 0.05D;
               }
            }

            while(true) {
               while(var5 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(0.0D, (double)(-this.maxUpStep), var5))) {
                  if (var5 < 0.05D && var5 >= -0.05D) {
                     var5 = 0.0D;
                  } else if (var5 > 0.0D) {
                     var5 -= 0.05D;
                  } else {
                     var5 += 0.05D;
                  }
               }

               while(true) {
                  while(var3 != 0.0D && var5 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(var3, (double)(-this.maxUpStep), var5))) {
                     if (var3 < 0.05D && var3 >= -0.05D) {
                        var3 = 0.0D;
                     } else if (var3 > 0.0D) {
                        var3 -= 0.05D;
                     } else {
                        var3 += 0.05D;
                     }

                     if (var5 < 0.05D && var5 >= -0.05D) {
                        var5 = 0.0D;
                     } else if (var5 > 0.0D) {
                        var5 -= 0.05D;
                     } else {
                        var5 += 0.05D;
                     }
                  }

                  var1 = new Vec3(var3, var1.y, var5);
                  return var1;
               }
            }
         }
      } else {
         return var1;
      }
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
      Stream var6 = this.level.getEntityCollisions(this, var2.expandTowards(var1), ImmutableSet.of());
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
      this.x = (var1.minX + var1.maxX) / 2.0D;
      this.y = var1.minY;
      this.z = (var1.minZ + var1.maxZ) / 2.0D;
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
      BlockPos.PooledMutableBlockPos var2 = BlockPos.PooledMutableBlockPos.acquire(var1.minX + 0.001D, var1.minY + 0.001D, var1.minZ + 0.001D);
      Throwable var3 = null;

      try {
         BlockPos.PooledMutableBlockPos var4 = BlockPos.PooledMutableBlockPos.acquire(var1.maxX - 0.001D, var1.maxY - 0.001D, var1.maxZ - 0.001D);
         Throwable var5 = null;

         try {
            BlockPos.PooledMutableBlockPos var6 = BlockPos.PooledMutableBlockPos.acquire();
            Throwable var7 = null;

            try {
               if (this.level.hasChunksAt(var2, var4)) {
                  for(int var8 = var2.getX(); var8 <= var4.getX(); ++var8) {
                     for(int var9 = var2.getY(); var9 <= var4.getY(); ++var9) {
                        for(int var10 = var2.getZ(); var10 <= var4.getZ(); ++var10) {
                           var6.set(var8, var9, var10);
                           BlockState var11 = this.level.getBlockState(var6);

                           try {
                              var11.entityInside(this.level, var6, this);
                              this.onInsideBlock(var11);
                           } catch (Throwable var60) {
                              CrashReport var13 = CrashReport.forThrowable(var60, "Colliding entity with block");
                              CrashReportCategory var14 = var13.addCategory("Block being collided with");
                              CrashReportCategory.populateBlockDetails(var14, var6, var11);
                              throw new ReportedException(var13);
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var61) {
               var7 = var61;
               throw var61;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var59) {
                        var7.addSuppressed(var59);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var63) {
            var5 = var63;
            throw var63;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var58) {
                     var5.addSuppressed(var58);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var65) {
         var3 = var65;
         throw var65;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var57) {
                  var3.addSuppressed(var57);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   protected void onInsideBlock(BlockState var1) {
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      if (!var2.getMaterial().isLiquid()) {
         BlockState var3 = this.level.getBlockState(var1.above());
         SoundType var4 = var3.getBlock() == Blocks.SNOW ? var3.getSoundType() : var2.getSoundType();
         this.playSound(var4.getStepSound(), var4.getVolume() * 0.15F, var4.getPitch());
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
         this.level.playSound((Player)null, this.x, this.y, this.z, var1, this.getSoundSource(), var2, var3);
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

   protected boolean makeStepSound() {
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

   @Nullable
   public AABB getCollideBox() {
      return null;
   }

   protected void burn(int var1) {
      if (!this.fireImmune()) {
         this.hurt(DamageSource.IN_FIRE, (float)var1);
      }

   }

   public final boolean fireImmune() {
      return this.getType().fireImmune();
   }

   public void causeFallDamage(float var1, float var2) {
      if (this.isVehicle()) {
         Iterator var3 = this.getPassengers().iterator();

         while(var3.hasNext()) {
            Entity var4 = (Entity)var3.next();
            var4.causeFallDamage(var1, var2);
         }
      }

   }

   public boolean isInWater() {
      return this.wasInWater;
   }

   private boolean isInRain() {
      BlockPos.PooledMutableBlockPos var1 = BlockPos.PooledMutableBlockPos.acquire(this);
      Throwable var2 = null;

      boolean var3;
      try {
         var3 = this.level.isRainingAt(var1) || this.level.isRainingAt(var1.set(this.x, this.y + (double)this.dimensions.height, this.z));
      } catch (Throwable var12) {
         var2 = var12;
         throw var12;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               var1.close();
            }
         }

      }

      return var3;
   }

   private boolean isInBubbleColumn() {
      return this.level.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
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
      return this.wasUnderWater && this.isInWater();
   }

   private void updateWaterState() {
      this.updateInWaterState();
      this.updateUnderWaterState();
      this.updateSwimming();
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
      } else {
         this.setSwimming(this.isSprinting() && this.isUnderWater() && !this.isPassenger());
      }

   }

   public boolean updateInWaterState() {
      if (this.getVehicle() instanceof Boat) {
         this.wasInWater = false;
      } else if (this.checkAndHandleWater(FluidTags.WATER)) {
         if (!this.wasInWater && !this.firstTick) {
            this.doWaterSplashEffect();
         }

         this.fallDistance = 0.0F;
         this.wasInWater = true;
         this.clearFire();
      } else {
         this.wasInWater = false;
      }

      return this.wasInWater;
   }

   private void updateUnderWaterState() {
      this.wasUnderWater = this.isUnderLiquid(FluidTags.WATER, true);
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

      float var5 = (float)Mth.floor(this.getBoundingBox().minY);

      int var6;
      float var7;
      float var8;
      for(var6 = 0; (float)var6 < 1.0F + this.dimensions.width * 20.0F; ++var6) {
         var7 = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
         var8 = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
         this.level.addParticle(ParticleTypes.BUBBLE, this.x + (double)var7, (double)(var5 + 1.0F), this.z + (double)var8, var3.x, var3.y - (double)(this.random.nextFloat() * 0.2F), var3.z);
      }

      for(var6 = 0; (float)var6 < 1.0F + this.dimensions.width * 20.0F; ++var6) {
         var7 = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
         var8 = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
         this.level.addParticle(ParticleTypes.SPLASH, this.x + (double)var7, (double)(var5 + 1.0F), this.z + (double)var8, var3.x, var3.y, var3.z);
      }

   }

   public void updateSprintingState() {
      if (this.isSprinting() && !this.isInWater()) {
         this.doSprintParticleEffect();
      }

   }

   protected void doSprintParticleEffect() {
      int var1 = Mth.floor(this.x);
      int var2 = Mth.floor(this.y - 0.20000000298023224D);
      int var3 = Mth.floor(this.z);
      BlockPos var4 = new BlockPos(var1, var2, var3);
      BlockState var5 = this.level.getBlockState(var4);
      if (var5.getRenderShape() != RenderShape.INVISIBLE) {
         Vec3 var6 = this.getDeltaMovement();
         this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var5), this.x + ((double)this.random.nextFloat() - 0.5D) * (double)this.dimensions.width, this.y + 0.1D, this.z + ((double)this.random.nextFloat() - 0.5D) * (double)this.dimensions.width, var6.x * -4.0D, 1.5D, var6.z * -4.0D);
      }

   }

   public boolean isUnderLiquid(Tag<Fluid> var1) {
      return this.isUnderLiquid(var1, false);
   }

   public boolean isUnderLiquid(Tag<Fluid> var1, boolean var2) {
      if (this.getVehicle() instanceof Boat) {
         return false;
      } else {
         double var3 = this.y + (double)this.getEyeHeight();
         BlockPos var5 = new BlockPos(this.x, var3, this.z);
         if (var2 && !this.level.hasChunk(var5.getX() >> 4, var5.getZ() >> 4)) {
            return false;
         } else {
            FluidState var6 = this.level.getFluidState(var5);
            return var6.is(var1) && var3 < (double)((float)var5.getY() + var6.getHeight(this.level, var5) + 0.11111111F);
         }
      }
   }

   public void setInLava() {
      this.isInLava = true;
   }

   public boolean isInLava() {
      return this.isInLava;
   }

   public void moveRelative(float var1, Vec3 var2) {
      Vec3 var3 = getInputVector(var2, var1, this.yRot);
      this.setDeltaMovement(this.getDeltaMovement().add(var3));
   }

   protected static Vec3 getInputVector(Vec3 var0, float var1, float var2) {
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

   public int getLightColor() {
      BlockPos var1 = new BlockPos(this.x, this.y + (double)this.getEyeHeight(), this.z);
      return this.level.hasChunkAt(var1) ? this.level.getLightColor(var1, 0) : 0;
   }

   public float getBrightness() {
      BlockPos.MutableBlockPos var1 = new BlockPos.MutableBlockPos(this.x, 0.0D, this.z);
      if (this.level.hasChunkAt(var1)) {
         var1.setY(Mth.floor(this.y + (double)this.getEyeHeight()));
         return this.level.getBrightness(var1);
      } else {
         return 0.0F;
      }
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   public void absMoveTo(double var1, double var3, double var5, float var7, float var8) {
      this.x = Mth.clamp(var1, -3.0E7D, 3.0E7D);
      this.y = var3;
      this.z = Mth.clamp(var5, -3.0E7D, 3.0E7D);
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      var8 = Mth.clamp(var8, -90.0F, 90.0F);
      this.yRot = var7;
      this.xRot = var8;
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
      double var9 = (double)(this.yRotO - var7);
      if (var9 < -180.0D) {
         this.yRotO += 360.0F;
      }

      if (var9 >= 180.0D) {
         this.yRotO -= 360.0F;
      }

      this.setPos(this.x, this.y, this.z);
      this.setRot(var7, var8);
   }

   public void moveTo(BlockPos var1, float var2, float var3) {
      this.moveTo((double)var1.getX() + 0.5D, (double)var1.getY(), (double)var1.getZ() + 0.5D, var2, var3);
   }

   public void moveTo(double var1, double var3, double var5, float var7, float var8) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.xOld = this.x;
      this.yOld = this.y;
      this.zOld = this.z;
      this.yRot = var7;
      this.xRot = var8;
      this.setPos(this.x, this.y, this.z);
   }

   public float distanceTo(Entity var1) {
      float var2 = (float)(this.x - var1.x);
      float var3 = (float)(this.y - var1.y);
      float var4 = (float)(this.z - var1.z);
      return Mth.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public double distanceToSqr(double var1, double var3, double var5) {
      double var7 = this.x - var1;
      double var9 = this.y - var3;
      double var11 = this.z - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double distanceToSqr(Entity var1) {
      return this.distanceToSqr(var1.position());
   }

   public double distanceToSqr(Vec3 var1) {
      double var2 = this.x - var1.x;
      double var4 = this.y - var1.y;
      double var6 = this.z - var1.z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public void playerTouch(Player var1) {
   }

   public void push(Entity var1) {
      if (!this.isPassengerOfSameVehicle(var1)) {
         if (!var1.noPhysics && !this.noPhysics) {
            double var2 = var1.x - this.x;
            double var4 = var1.z - this.z;
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

   public Vec3 getEyePosition(float var1) {
      if (var1 == 1.0F) {
         return new Vec3(this.x, this.y + (double)this.getEyeHeight(), this.z);
      } else {
         double var2 = Mth.lerp((double)var1, this.xo, this.x);
         double var4 = Mth.lerp((double)var1, this.yo, this.y) + (double)this.getEyeHeight();
         double var6 = Mth.lerp((double)var1, this.zo, this.z);
         return new Vec3(var2, var4, var6);
      }
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
      double var7 = this.x - var1;
      double var9 = this.y - var3;
      double var11 = this.z - var5;
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
      String var2 = this.getEncodeId();
      if (!this.removed && var2 != null) {
         var1.putString("id", var2);
         this.saveWithoutId(var1);
         return true;
      } else {
         return false;
      }
   }

   public boolean save(CompoundTag var1) {
      return this.isPassenger() ? false : this.saveAsPassenger(var1);
   }

   public CompoundTag saveWithoutId(CompoundTag var1) {
      try {
         var1.put("Pos", this.newDoubleList(this.x, this.y, this.z));
         Vec3 var2 = this.getDeltaMovement();
         var1.put("Motion", this.newDoubleList(var2.x, var2.y, var2.z));
         var1.put("Rotation", this.newFloatList(this.yRot, this.xRot));
         var1.putFloat("FallDistance", this.fallDistance);
         var1.putShort("Fire", (short)this.remainingFireTicks);
         var1.putShort("Air", (short)this.getAirSupply());
         var1.putBoolean("OnGround", this.onGround);
         var1.putInt("Dimension", this.dimension.getId());
         var1.putBoolean("Invulnerable", this.invulnerable);
         var1.putInt("PortalCooldown", this.changingDimensionDelay);
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
               var10.add(new StringTag(var6));
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
         ListTag var15 = var1.getList("Motion", 6);
         ListTag var16 = var1.getList("Rotation", 5);
         double var5 = var15.getDouble(0);
         double var7 = var15.getDouble(1);
         double var9 = var15.getDouble(2);
         this.setDeltaMovement(Math.abs(var5) > 10.0D ? 0.0D : var5, Math.abs(var7) > 10.0D ? 0.0D : var7, Math.abs(var9) > 10.0D ? 0.0D : var9);
         this.x = var2.getDouble(0);
         this.y = var2.getDouble(1);
         this.z = var2.getDouble(2);
         this.xOld = this.x;
         this.yOld = this.y;
         this.zOld = this.z;
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         this.yRot = var16.getFloat(0);
         this.xRot = var16.getFloat(1);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
         this.setYHeadRot(this.yRot);
         this.setYBodyRot(this.yRot);
         this.fallDistance = var1.getFloat("FallDistance");
         this.remainingFireTicks = var1.getShort("Fire");
         this.setAirSupply(var1.getShort("Air"));
         this.onGround = var1.getBoolean("OnGround");
         if (var1.contains("Dimension")) {
            this.dimension = DimensionType.getById(var1.getInt("Dimension"));
         }

         this.invulnerable = var1.getBoolean("Invulnerable");
         this.changingDimensionDelay = var1.getInt("PortalCooldown");
         if (var1.hasUUID("UUID")) {
            this.uuid = var1.getUUID("UUID");
            this.stringUUID = this.uuid.toString();
         }

         if (Double.isFinite(this.x) && Double.isFinite(this.y) && Double.isFinite(this.z)) {
            if (Double.isFinite((double)this.yRot) && Double.isFinite((double)this.xRot)) {
               this.setPos(this.x, this.y, this.z);
               this.setRot(this.yRot, this.xRot);
               if (var1.contains("CustomName", 8)) {
                  this.setCustomName(Component.Serializer.fromJson(var1.getString("CustomName")));
               }

               this.setCustomNameVisible(var1.getBoolean("CustomNameVisible"));
               this.setSilent(var1.getBoolean("Silent"));
               this.setNoGravity(var1.getBoolean("NoGravity"));
               this.setGlowing(var1.getBoolean("Glowing"));
               if (var1.contains("Tags", 9)) {
                  this.tags.clear();
                  ListTag var11 = var1.getList("Tags", 8);
                  int var12 = Math.min(var11.size(), 1024);

                  for(int var13 = 0; var13 < var12; ++var13) {
                     this.tags.add(var11.getString(var13));
                  }
               }

               this.readAdditionalSaveData(var1);
               if (this.repositionEntityAfterLoad()) {
                  this.setPos(this.x, this.y, this.z);
               }

            } else {
               throw new IllegalStateException("Entity has invalid rotation");
            }
         } else {
            throw new IllegalStateException("Entity has invalid position");
         }
      } catch (Throwable var14) {
         CrashReport var3 = CrashReport.forThrowable(var14, "Loading entity NBT");
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
         var2.add(new DoubleTag(var6));
      }

      return var2;
   }

   protected ListTag newFloatList(float... var1) {
      ListTag var2 = new ListTag();
      float[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         float var6 = var3[var5];
         var2.add(new FloatTag(var6));
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
         ItemEntity var3 = new ItemEntity(this.level, this.x, this.y + (double)var2, this.z, var1);
         var3.setDefaultPickUpDelay();
         this.level.addFreshEntity(var3);
         return var3;
      }
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public boolean isInWall() {
      if (this.noPhysics) {
         return false;
      } else {
         BlockPos.PooledMutableBlockPos var1 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var2 = null;

         try {
            for(int var3 = 0; var3 < 8; ++var3) {
               int var4 = Mth.floor(this.y + (double)(((float)((var3 >> 0) % 2) - 0.5F) * 0.1F) + (double)this.eyeHeight);
               int var5 = Mth.floor(this.x + (double)(((float)((var3 >> 1) % 2) - 0.5F) * this.dimensions.width * 0.8F));
               int var6 = Mth.floor(this.z + (double)(((float)((var3 >> 2) % 2) - 0.5F) * this.dimensions.width * 0.8F));
               if (var1.getX() != var5 || var1.getY() != var4 || var1.getZ() != var6) {
                  var1.set(var5, var4, var6);
                  if (this.level.getBlockState(var1).isViewBlocking(this.level, var1)) {
                     boolean var7 = true;
                     return var7;
                  }
               }
            }
         } catch (Throwable var17) {
            var2 = var17;
            throw var17;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var16) {
                     var2.addSuppressed(var16);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return false;
      }
   }

   public boolean interact(Player var1, InteractionHand var2) {
      return false;
   }

   @Nullable
   public AABB getCollideAgainstBox(Entity var1) {
      return null;
   }

   public void rideTick() {
      this.setDeltaMovement(Vec3.ZERO);
      this.tick();
      if (this.isPassenger()) {
         this.getVehicle().positionRider(this);
      }
   }

   public void positionRider(Entity var1) {
      if (this.hasPassenger(var1)) {
         var1.setPos(this.x, this.y + this.getRideHeight() + var1.getRidingHeight(), this.z);
      }
   }

   public void onPassengerTurned(Entity var1) {
   }

   public double getRidingHeight() {
      return 0.0D;
   }

   public double getRideHeight() {
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

         this.vehicle = var1;
         this.vehicle.addPassenger(this);
         return true;
      }
   }

   protected boolean canRide(Entity var1) {
      return this.boardingCooldown <= 0;
   }

   protected boolean canEnterPose(Pose var1) {
      return this.level.noCollision(this, this.getBoundingBoxForPose(var1));
   }

   public void ejectPassengers() {
      for(int var1 = this.passengers.size() - 1; var1 >= 0; --var1) {
         ((Entity)this.passengers.get(var1)).stopRiding();
      }

   }

   public void stopRiding() {
      if (this.vehicle != null) {
         Entity var1 = this.vehicle;
         this.vehicle = null;
         var1.removePassenger(this);
      }

   }

   protected void addPassenger(Entity var1) {
      if (var1.getVehicle() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (!this.level.isClientSide && var1 instanceof Player && !(this.getControllingPassenger() instanceof Player)) {
            this.passengers.add(0, var1);
         } else {
            this.passengers.add(var1);
         }

      }
   }

   protected void removePassenger(Entity var1) {
      if (var1.getVehicle() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         this.passengers.remove(var1);
         var1.boardingCooldown = 60;
      }
   }

   protected boolean canAddPassenger(Entity var1) {
      return this.getPassengers().size() < 1;
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
      if (this.changingDimensionDelay > 0) {
         this.changingDimensionDelay = this.getDimensionChangingDelay();
      } else {
         if (!this.level.isClientSide && !var1.equals(this.portalEntranceBlock)) {
            this.portalEntranceBlock = new BlockPos(var1);
            BlockPattern.BlockPatternMatch var2 = ((NetherPortalBlock)Blocks.NETHER_PORTAL).getPortalShape(this.level, this.portalEntranceBlock);
            double var3 = var2.getForwards().getAxis() == Direction.Axis.X ? (double)var2.getFrontTopLeft().getZ() : (double)var2.getFrontTopLeft().getX();
            double var5 = Math.abs(Mth.pct((var2.getForwards().getAxis() == Direction.Axis.X ? this.z : this.x) - (double)(var2.getForwards().getClockWise().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), var3, var3 - (double)var2.getWidth()));
            double var7 = Mth.pct(this.y - 1.0D, (double)var2.getFrontTopLeft().getY(), (double)(var2.getFrontTopLeft().getY() - var2.getHeight()));
            this.portalEntranceOffset = new Vec3(var5, var7, 0.0D);
            this.portalEntranceForwards = var2.getForwards();
         }

         this.isInsidePortal = true;
      }
   }

   protected void handleNetherPortal() {
      if (this.level instanceof ServerLevel) {
         int var1 = this.getPortalWaitTime();
         if (this.isInsidePortal) {
            if (this.level.getServer().isNetherEnabled() && !this.isPassenger() && this.portalTime++ >= var1) {
               this.level.getProfiler().push("portal");
               this.portalTime = var1;
               this.changingDimensionDelay = this.getDimensionChangingDelay();
               this.changeDimension(this.level.dimension.getType() == DimensionType.NETHER ? DimensionType.OVERWORLD : DimensionType.NETHER);
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

         this.processDimensionDelay();
      }
   }

   public int getDimensionChangingDelay() {
      return 300;
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
   }

   public void handleEntityEvent(byte var1) {
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
      return !this.getPassengers().isEmpty();
   }

   public boolean rideableUnderWater() {
      return true;
   }

   public boolean isSneaking() {
      return this.getSharedFlag(1);
   }

   public boolean isVisuallySneaking() {
      return this.getPose() == Pose.SNEAKING;
   }

   public void setSneaking(boolean var1) {
      this.setSharedFlag(1, var1);
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

   public void thunderHit(LightningBolt var1) {
      ++this.remainingFireTicks;
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

   public void killed(LivingEntity var1) {
   }

   protected void checkInBlock(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      Vec3 var8 = new Vec3(var1 - (double)var7.getX(), var3 - (double)var7.getY(), var5 - (double)var7.getZ());
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
      Direction var10 = Direction.UP;
      double var11 = 1.7976931348623157E308D;
      Direction[] var13 = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};
      int var14 = var13.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         Direction var16 = var13[var15];
         var9.set((Vec3i)var7).move(var16);
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

   private static void removeAction(Component var0) {
      var0.withStyle((var0x) -> {
         var0x.setClickEvent((ClickEvent)null);
      }).getSiblings().forEach(Entity::removeAction);
   }

   public Component getName() {
      Component var1 = this.getCustomName();
      if (var1 != null) {
         Component var2 = var1.deepCopy();
         removeAction(var2);
         return var2;
      } else {
         return this.type.getDescription();
      }
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
      return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getContents(), this.id, this.level == null ? "~NULL~" : this.level.getLevelData().getLevelName(), this.x, this.y, this.z);
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
      this.moveTo(var1.x, var1.y, var1.z, var1.yRot, var1.xRot);
   }

   public void restoreFrom(Entity var1) {
      CompoundTag var2 = var1.saveWithoutId(new CompoundTag());
      var2.remove("Dimension");
      this.load(var2);
      this.changingDimensionDelay = var1.changingDimensionDelay;
      this.portalEntranceBlock = var1.portalEntranceBlock;
      this.portalEntranceOffset = var1.portalEntranceOffset;
      this.portalEntranceForwards = var1.portalEntranceForwards;
   }

   @Nullable
   public Entity changeDimension(DimensionType var1) {
      if (!this.level.isClientSide && !this.removed) {
         this.level.getProfiler().push("changeDimension");
         MinecraftServer var2 = this.getServer();
         DimensionType var3 = this.dimension;
         ServerLevel var4 = var2.getLevel(var3);
         ServerLevel var5 = var2.getLevel(var1);
         this.dimension = var1;
         this.unRide();
         this.level.getProfiler().push("reposition");
         Vec3 var7 = this.getDeltaMovement();
         float var8 = 0.0F;
         BlockPos var6;
         if (var3 == DimensionType.THE_END && var1 == DimensionType.OVERWORLD) {
            var6 = var5.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var5.getSharedSpawnPos());
         } else if (var1 == DimensionType.THE_END) {
            var6 = var5.getDimensionSpecificSpawn();
         } else {
            double var9 = this.x;
            double var11 = this.z;
            double var13 = 8.0D;
            if (var3 == DimensionType.OVERWORLD && var1 == DimensionType.NETHER) {
               var9 /= 8.0D;
               var11 /= 8.0D;
            } else if (var3 == DimensionType.NETHER && var1 == DimensionType.OVERWORLD) {
               var9 *= 8.0D;
               var11 *= 8.0D;
            }

            double var15 = Math.min(-2.9999872E7D, var5.getWorldBorder().getMinX() + 16.0D);
            double var17 = Math.min(-2.9999872E7D, var5.getWorldBorder().getMinZ() + 16.0D);
            double var19 = Math.min(2.9999872E7D, var5.getWorldBorder().getMaxX() - 16.0D);
            double var21 = Math.min(2.9999872E7D, var5.getWorldBorder().getMaxZ() - 16.0D);
            var9 = Mth.clamp(var9, var15, var19);
            var11 = Mth.clamp(var11, var17, var21);
            Vec3 var23 = this.getPortalEntranceOffset();
            var6 = new BlockPos(var9, this.y, var11);
            BlockPattern.PortalInfo var24 = var5.getPortalForcer().findPortal(var6, var7, this.getPortalEntranceForwards(), var23.x, var23.y, this instanceof Player);
            if (var24 == null) {
               return null;
            }

            var6 = new BlockPos(var24.pos);
            var7 = var24.speed;
            var8 = (float)var24.angle;
         }

         this.level.getProfiler().popPush("reloading");
         Entity var25 = this.getType().create(var5);
         if (var25 != null) {
            var25.restoreFrom(this);
            var25.moveTo(var6, var25.yRot + var8, var25.xRot);
            var25.setDeltaMovement(var7);
            var5.addFromAnotherDimension(var25);
         }

         this.removed = true;
         this.level.getProfiler().pop();
         var4.resetEmptyTime();
         var5.resetEmptyTime();
         this.level.getProfiler().pop();
         return var25;
      } else {
         return null;
      }
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

   public Vec3 getPortalEntranceOffset() {
      return this.portalEntranceOffset;
   }

   public Direction getPortalEntranceForwards() {
      return this.portalEntranceForwards;
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
      var1.setDetail("Entity's Exact location", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
      var1.setDetail("Entity's Block location", (Object)CrashReportCategory.formatLocation(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)));
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
      return this.isOnFire();
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

   public boolean isPushedByWater() {
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
         var1.setHoverEvent(this.createHoverEvent()).setInsertion(this.getStringUUID());
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
         this.teleported = true;
         this.moveTo(var1, var3, var5, this.yRot, this.xRot);
         ((ServerLevel)this.level).updateChunkPos(this);
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
         this.setBoundingBox(new AABB(this.x - var6, this.y, this.z - var6, this.x + var6, this.y + (double)var3.height, this.z + var6));
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
      CompoundTag var1 = new CompoundTag();
      ResourceLocation var2 = EntityType.getKey(this.getType());
      var1.putString("id", this.getStringUUID());
      if (var2 != null) {
         var1.putString("type", var2.toString());
      }

      var1.putString("name", Component.Serializer.toJson(this.getName()));
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponent(var1.toString()));
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
      Vec3 var4 = new Vec3(this.x - (double)var3, this.y, this.z - (double)var3);
      Vec3 var5 = new Vec3(this.x + (double)var3, this.y + (double)var2.height, this.z + (double)var3);
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

   public boolean setSlot(int var1, ItemStack var2) {
      return false;
   }

   public void sendMessage(Component var1) {
   }

   public BlockPos getCommandSenderBlockPosition() {
      return new BlockPos(this);
   }

   public Vec3 getCommandSenderWorldPosition() {
      return new Vec3(this.x, this.y, this.z);
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

   protected void doEnchantDamageEffects(LivingEntity var1, Entity var2) {
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

   public boolean checkAndResetTeleportedFlag() {
      boolean var1 = this.teleported;
      this.teleported = false;
      return var1;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return null;
   }

   public List<Entity> getPassengers() {
      return (List)(this.passengers.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengers));
   }

   public boolean hasPassenger(Entity var1) {
      Iterator var2 = this.getPassengers().iterator();

      Entity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Entity)var2.next();
      } while(!var3.equals(var1));

      return true;
   }

   public boolean hasPassenger(Class<? extends Entity> var1) {
      Iterator var2 = this.getPassengers().iterator();

      Entity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Entity)var2.next();
      } while(!var1.isAssignableFrom(var3.getClass()));

      return true;
   }

   public Collection<Entity> getIndirectPassengers() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.getPassengers().iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         var1.add(var3);
         var3.fillIndirectPassengers(false, var1);
      }

      return var1;
   }

   public boolean hasOnePlayerPassenger() {
      HashSet var1 = Sets.newHashSet();
      this.fillIndirectPassengers(true, var1);
      return var1.size() == 1;
   }

   private void fillIndirectPassengers(boolean var1, Set<Entity> var2) {
      Entity var4;
      for(Iterator var3 = this.getPassengers().iterator(); var3.hasNext(); var4.fillIndirectPassengers(var1, var2)) {
         var4 = (Entity)var3.next();
         if (!var1 || ServerPlayer.class.isAssignableFrom(var4.getClass())) {
            var2.add(var4);
         }
      }

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
      Iterator var2 = this.getPassengers().iterator();

      Entity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Entity)var2.next();
         if (var3.equals(var1)) {
            return true;
         }
      } while(!var3.hasIndirectPassenger(var1));

      return true;
   }

   public boolean isControlledByLocalInstance() {
      Entity var1 = this.getControllingPassenger();
      if (var1 instanceof Player) {
         return ((Player)var1).isLocalPlayer();
      } else {
         return !this.level.isClientSide;
      }
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
      return new CommandSourceStack(this, new Vec3(this.x, this.y, this.z), this.getRotationVector(), this.level instanceof ServerLevel ? (ServerLevel)this.level : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
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

   public boolean checkAndHandleWater(Tag<Fluid> var1) {
      AABB var2 = this.getBoundingBox().deflate(0.001D);
      int var3 = Mth.floor(var2.minX);
      int var4 = Mth.ceil(var2.maxX);
      int var5 = Mth.floor(var2.minY);
      int var6 = Mth.ceil(var2.maxY);
      int var7 = Mth.floor(var2.minZ);
      int var8 = Mth.ceil(var2.maxZ);
      if (!this.level.hasChunksAt(var3, var5, var7, var4, var6, var8)) {
         return false;
      } else {
         double var9 = 0.0D;
         boolean var11 = this.isPushedByWater();
         boolean var12 = false;
         Vec3 var13 = Vec3.ZERO;
         int var14 = 0;
         BlockPos.PooledMutableBlockPos var15 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var16 = null;

         try {
            for(int var17 = var3; var17 < var4; ++var17) {
               for(int var18 = var5; var18 < var6; ++var18) {
                  for(int var19 = var7; var19 < var8; ++var19) {
                     var15.set(var17, var18, var19);
                     FluidState var20 = this.level.getFluidState(var15);
                     if (var20.is(var1)) {
                        double var21 = (double)((float)var18 + var20.getHeight(this.level, var15));
                        if (var21 >= var2.minY) {
                           var12 = true;
                           var9 = Math.max(var21 - var2.minY, var9);
                           if (var11) {
                              Vec3 var23 = var20.getFlow(this.level, var15);
                              if (var9 < 0.4D) {
                                 var23 = var23.scale(var9);
                              }

                              var13 = var13.add(var23);
                              ++var14;
                           }
                        }
                     }
                  }
               }
            }
         } catch (Throwable var31) {
            var16 = var31;
            throw var31;
         } finally {
            if (var15 != null) {
               if (var16 != null) {
                  try {
                     var15.close();
                  } catch (Throwable var30) {
                     var16.addSuppressed(var30);
                  }
               } else {
                  var15.close();
               }
            }

         }

         if (var13.length() > 0.0D) {
            if (var14 > 0) {
               var13 = var13.scale(1.0D / (double)var14);
            }

            if (!(this instanceof Player)) {
               var13 = var13.normalize();
            }

            this.setDeltaMovement(this.getDeltaMovement().add(var13.scale(0.014D)));
         }

         this.waterHeight = var9;
         return var12;
      }
   }

   public double getWaterHeight() {
      return this.waterHeight;
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
      return new Vec3(this.x, this.y, this.z);
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

   static {
      DATA_SHARED_FLAGS_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BYTE);
      DATA_AIR_SUPPLY_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
      DATA_CUSTOM_NAME = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
      DATA_CUSTOM_NAME_VISIBLE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
      DATA_SILENT = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
      DATA_NO_GRAVITY = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
      DATA_POSE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.POSE);
   }
}
