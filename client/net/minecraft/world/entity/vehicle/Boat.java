package net.minecraft.world.entity.vehicle;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Boat extends Entity {
   private static final EntityDataAccessor<Integer> DATA_ID_HURT;
   private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR;
   private static final EntityDataAccessor<Float> DATA_ID_DAMAGE;
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE;
   private static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_LEFT;
   private static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_RIGHT;
   private static final EntityDataAccessor<Integer> DATA_ID_BUBBLE_TIME;
   private final float[] paddlePositions;
   private float invFriction;
   private float outOfControlTicks;
   private float deltaRotation;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYRot;
   private double lerpXRot;
   private boolean inputLeft;
   private boolean inputRight;
   private boolean inputUp;
   private boolean inputDown;
   private double waterLevel;
   private float landFriction;
   private Boat.Status status;
   private Boat.Status oldStatus;
   private double lastYd;
   private boolean isAboveBubbleColumn;
   private boolean bubbleColumnDirectionIsDown;
   private float bubbleMultiplier;
   private float bubbleAngle;
   private float bubbleAngleO;

   public Boat(EntityType<? extends Boat> var1, Level var2) {
      super(var1, var2);
      this.paddlePositions = new float[2];
      this.blocksBuilding = true;
   }

   public Boat(Level var1, double var2, double var4, double var6) {
      this(EntityType.BOAT, var1);
      this.setPos(var2, var4, var6);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = var2;
      this.yo = var4;
      this.zo = var6;
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_ID_HURT, 0);
      this.entityData.define(DATA_ID_HURTDIR, 1);
      this.entityData.define(DATA_ID_DAMAGE, 0.0F);
      this.entityData.define(DATA_ID_TYPE, Boat.Type.OAK.ordinal());
      this.entityData.define(DATA_ID_PADDLE_LEFT, false);
      this.entityData.define(DATA_ID_PADDLE_RIGHT, false);
      this.entityData.define(DATA_ID_BUBBLE_TIME, 0);
   }

   @Nullable
   public AABB getCollideAgainstBox(Entity var1) {
      return var1.isPushable() ? var1.getBoundingBox() : null;
   }

   @Nullable
   public AABB getCollideBox() {
      return this.getBoundingBox();
   }

   public boolean isPushable() {
      return true;
   }

   public double getRideHeight() {
      return -0.1D;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (!this.level.isClientSide && !this.removed) {
         if (var1 instanceof IndirectEntityDamageSource && var1.getEntity() != null && this.hasPassenger(var1.getEntity())) {
            return false;
         } else {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + var2 * 10.0F);
            this.markHurt();
            boolean var3 = var1.getEntity() instanceof Player && ((Player)var1.getEntity()).abilities.instabuild;
            if (var3 || this.getDamage() > 40.0F) {
               if (!var3 && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                  this.spawnAtLocation(this.getDropItem());
               }

               this.remove();
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void onAboveBubbleCol(boolean var1) {
      if (!this.level.isClientSide) {
         this.isAboveBubbleColumn = true;
         this.bubbleColumnDirectionIsDown = var1;
         if (this.getBubbleTime() == 0) {
            this.setBubbleTime(60);
         }
      }

      this.level.addParticle(ParticleTypes.SPLASH, this.x + (double)this.random.nextFloat(), this.y + 0.7D, this.z + (double)this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
      if (this.random.nextInt(20) == 0) {
         this.level.playLocalSound(this.x, this.y, this.z, this.getSwimSplashSound(), this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
      }

   }

   public void push(Entity var1) {
      if (var1 instanceof Boat) {
         if (var1.getBoundingBox().minY < this.getBoundingBox().maxY) {
            super.push(var1);
         }
      } else if (var1.getBoundingBox().minY <= this.getBoundingBox().minY) {
         super.push(var1);
      }

   }

   public Item getDropItem() {
      switch(this.getBoatType()) {
      case OAK:
      default:
         return Items.OAK_BOAT;
      case SPRUCE:
         return Items.SPRUCE_BOAT;
      case BIRCH:
         return Items.BIRCH_BOAT;
      case JUNGLE:
         return Items.JUNGLE_BOAT;
      case ACACIA:
         return Items.ACACIA_BOAT;
      case DARK_OAK:
         return Items.DARK_OAK_BOAT;
      }
   }

   public void animateHurt() {
      this.setHurtDir(-this.getHurtDir());
      this.setHurtTime(10);
      this.setDamage(this.getDamage() * 11.0F);
   }

   public boolean isPickable() {
      return !this.removed;
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.lerpX = var1;
      this.lerpY = var3;
      this.lerpZ = var5;
      this.lerpYRot = (double)var7;
      this.lerpXRot = (double)var8;
      this.lerpSteps = 10;
   }

   public Direction getMotionDirection() {
      return this.getDirection().getClockWise();
   }

   public void tick() {
      this.oldStatus = this.status;
      this.status = this.getStatus();
      if (this.status != Boat.Status.UNDER_WATER && this.status != Boat.Status.UNDER_FLOWING_WATER) {
         this.outOfControlTicks = 0.0F;
      } else {
         ++this.outOfControlTicks;
      }

      if (!this.level.isClientSide && this.outOfControlTicks >= 60.0F) {
         this.ejectPassengers();
      }

      if (this.getHurtTime() > 0) {
         this.setHurtTime(this.getHurtTime() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      super.tick();
      this.tickLerp();
      if (this.isControlledByLocalInstance()) {
         if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof Player)) {
            this.setPaddleState(false, false);
         }

         this.floatBoat();
         if (this.level.isClientSide) {
            this.controlBoat();
            this.level.sendPacketToServer(new ServerboundPaddleBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
      } else {
         this.setDeltaMovement(Vec3.ZERO);
      }

      this.tickBubbleColumn();

      for(int var1 = 0; var1 <= 1; ++var1) {
         if (this.getPaddleState(var1)) {
            if (!this.isSilent() && (double)(this.paddlePositions[var1] % 6.2831855F) <= 0.7853981852531433D && ((double)this.paddlePositions[var1] + 0.39269909262657166D) % 6.2831854820251465D >= 0.7853981852531433D) {
               SoundEvent var2 = this.getPaddleSound();
               if (var2 != null) {
                  Vec3 var3 = this.getViewVector(1.0F);
                  double var4 = var1 == 1 ? -var3.z : var3.z;
                  double var6 = var1 == 1 ? var3.x : -var3.x;
                  this.level.playSound((Player)null, this.x + var4, this.y, this.z + var6, var2, this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
               }
            }

            float[] var10000 = this.paddlePositions;
            var10000[var1] = (float)((double)var10000[var1] + 0.39269909262657166D);
         } else {
            this.paddlePositions[var1] = 0.0F;
         }
      }

      this.checkInsideBlocks();
      List var8 = this.level.getEntities((Entity)this, this.getBoundingBox().inflate(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelector.pushableBy(this));
      if (!var8.isEmpty()) {
         boolean var9 = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);

         for(int var10 = 0; var10 < var8.size(); ++var10) {
            Entity var11 = (Entity)var8.get(var10);
            if (!var11.hasPassenger((Entity)this)) {
               if (var9 && this.getPassengers().size() < 2 && !var11.isPassenger() && var11.getBbWidth() < this.getBbWidth() && var11 instanceof LivingEntity && !(var11 instanceof WaterAnimal) && !(var11 instanceof Player)) {
                  var11.startRiding(this);
               } else {
                  this.push(var11);
               }
            }
         }
      }

   }

   private void tickBubbleColumn() {
      int var1;
      if (this.level.isClientSide) {
         var1 = this.getBubbleTime();
         if (var1 > 0) {
            this.bubbleMultiplier += 0.05F;
         } else {
            this.bubbleMultiplier -= 0.1F;
         }

         this.bubbleMultiplier = Mth.clamp(this.bubbleMultiplier, 0.0F, 1.0F);
         this.bubbleAngleO = this.bubbleAngle;
         this.bubbleAngle = 10.0F * (float)Math.sin((double)(0.5F * (float)this.level.getGameTime())) * this.bubbleMultiplier;
      } else {
         if (!this.isAboveBubbleColumn) {
            this.setBubbleTime(0);
         }

         var1 = this.getBubbleTime();
         if (var1 > 0) {
            --var1;
            this.setBubbleTime(var1);
            int var2 = 60 - var1 - 1;
            if (var2 > 0 && var1 == 0) {
               this.setBubbleTime(0);
               Vec3 var3 = this.getDeltaMovement();
               if (this.bubbleColumnDirectionIsDown) {
                  this.setDeltaMovement(var3.add(0.0D, -0.7D, 0.0D));
                  this.ejectPassengers();
               } else {
                  this.setDeltaMovement(var3.x, this.hasPassenger(Player.class) ? 2.7D : 0.6D, var3.z);
               }
            }

            this.isAboveBubbleColumn = false;
         }
      }

   }

   @Nullable
   protected SoundEvent getPaddleSound() {
      switch(this.getStatus()) {
      case IN_WATER:
      case UNDER_WATER:
      case UNDER_FLOWING_WATER:
         return SoundEvents.BOAT_PADDLE_WATER;
      case ON_LAND:
         return SoundEvents.BOAT_PADDLE_LAND;
      case IN_AIR:
      default:
         return null;
      }
   }

   private void tickLerp() {
      if (this.lerpSteps > 0 && !this.isControlledByLocalInstance()) {
         double var1 = this.x + (this.lerpX - this.x) / (double)this.lerpSteps;
         double var3 = this.y + (this.lerpY - this.y) / (double)this.lerpSteps;
         double var5 = this.z + (this.lerpZ - this.z) / (double)this.lerpSteps;
         double var7 = Mth.wrapDegrees(this.lerpYRot - (double)this.yRot);
         this.yRot = (float)((double)this.yRot + var7 / (double)this.lerpSteps);
         this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
         --this.lerpSteps;
         this.setPos(var1, var3, var5);
         this.setRot(this.yRot, this.xRot);
      }
   }

   public void setPaddleState(boolean var1, boolean var2) {
      this.entityData.set(DATA_ID_PADDLE_LEFT, var1);
      this.entityData.set(DATA_ID_PADDLE_RIGHT, var2);
   }

   public float getRowingTime(int var1, float var2) {
      return this.getPaddleState(var1) ? (float)Mth.clampedLerp((double)this.paddlePositions[var1] - 0.39269909262657166D, (double)this.paddlePositions[var1], (double)var2) : 0.0F;
   }

   private Boat.Status getStatus() {
      Boat.Status var1 = this.isUnderwater();
      if (var1 != null) {
         this.waterLevel = this.getBoundingBox().maxY;
         return var1;
      } else if (this.checkInWater()) {
         return Boat.Status.IN_WATER;
      } else {
         float var2 = this.getGroundFriction();
         if (var2 > 0.0F) {
            this.landFriction = var2;
            return Boat.Status.ON_LAND;
         } else {
            return Boat.Status.IN_AIR;
         }
      }
   }

   public float getWaterLevelAbove() {
      AABB var1 = this.getBoundingBox();
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.maxY);
      int var5 = Mth.ceil(var1.maxY - this.lastYd);
      int var6 = Mth.floor(var1.minZ);
      int var7 = Mth.ceil(var1.maxZ);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var9 = null;

      try {
         label160:
         for(int var10 = var4; var10 < var5; ++var10) {
            float var11 = 0.0F;

            for(int var12 = var2; var12 < var3; ++var12) {
               for(int var13 = var6; var13 < var7; ++var13) {
                  var8.set(var12, var10, var13);
                  FluidState var14 = this.level.getFluidState(var8);
                  if (var14.is(FluidTags.WATER)) {
                     var11 = Math.max(var11, var14.getHeight(this.level, var8));
                  }

                  if (var11 >= 1.0F) {
                     continue label160;
                  }
               }
            }

            if (var11 < 1.0F) {
               float var26 = (float)var8.getY() + var11;
               return var26;
            }
         }

         float var25 = (float)(var5 + 1);
         return var25;
      } catch (Throwable var23) {
         var9 = var23;
         throw var23;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var22) {
                  var9.addSuppressed(var22);
               }
            } else {
               var8.close();
            }
         }

      }
   }

   public float getGroundFriction() {
      AABB var1 = this.getBoundingBox();
      AABB var2 = new AABB(var1.minX, var1.minY - 0.001D, var1.minZ, var1.maxX, var1.minY, var1.maxZ);
      int var3 = Mth.floor(var2.minX) - 1;
      int var4 = Mth.ceil(var2.maxX) + 1;
      int var5 = Mth.floor(var2.minY) - 1;
      int var6 = Mth.ceil(var2.maxY) + 1;
      int var7 = Mth.floor(var2.minZ) - 1;
      int var8 = Mth.ceil(var2.maxZ) + 1;
      VoxelShape var9 = Shapes.create(var2);
      float var10 = 0.0F;
      int var11 = 0;
      BlockPos.PooledMutableBlockPos var12 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var13 = null;

      try {
         for(int var14 = var3; var14 < var4; ++var14) {
            for(int var15 = var7; var15 < var8; ++var15) {
               int var16 = (var14 != var3 && var14 != var4 - 1 ? 0 : 1) + (var15 != var7 && var15 != var8 - 1 ? 0 : 1);
               if (var16 != 2) {
                  for(int var17 = var5; var17 < var6; ++var17) {
                     if (var16 <= 0 || var17 != var5 && var17 != var6 - 1) {
                        var12.set(var14, var17, var15);
                        BlockState var18 = this.level.getBlockState(var12);
                        if (!(var18.getBlock() instanceof WaterlilyBlock) && Shapes.joinIsNotEmpty(var18.getCollisionShape(this.level, var12).move((double)var14, (double)var17, (double)var15), var9, BooleanOp.AND)) {
                           var10 += var18.getBlock().getFriction();
                           ++var11;
                        }
                     }
                  }
               }
            }
         }
      } catch (Throwable var26) {
         var13 = var26;
         throw var26;
      } finally {
         if (var12 != null) {
            if (var13 != null) {
               try {
                  var12.close();
               } catch (Throwable var25) {
                  var13.addSuppressed(var25);
               }
            } else {
               var12.close();
            }
         }

      }

      return var10 / (float)var11;
   }

   private boolean checkInWater() {
      AABB var1 = this.getBoundingBox();
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.minY);
      int var5 = Mth.ceil(var1.minY + 0.001D);
      int var6 = Mth.floor(var1.minZ);
      int var7 = Mth.ceil(var1.maxZ);
      boolean var8 = false;
      this.waterLevel = 4.9E-324D;
      BlockPos.PooledMutableBlockPos var9 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var10 = null;

      try {
         for(int var11 = var2; var11 < var3; ++var11) {
            for(int var12 = var4; var12 < var5; ++var12) {
               for(int var13 = var6; var13 < var7; ++var13) {
                  var9.set(var11, var12, var13);
                  FluidState var14 = this.level.getFluidState(var9);
                  if (var14.is(FluidTags.WATER)) {
                     float var15 = (float)var12 + var14.getHeight(this.level, var9);
                     this.waterLevel = Math.max((double)var15, this.waterLevel);
                     var8 |= var1.minY < (double)var15;
                  }
               }
            }
         }
      } catch (Throwable var23) {
         var10 = var23;
         throw var23;
      } finally {
         if (var9 != null) {
            if (var10 != null) {
               try {
                  var9.close();
               } catch (Throwable var22) {
                  var10.addSuppressed(var22);
               }
            } else {
               var9.close();
            }
         }

      }

      return var8;
   }

   @Nullable
   private Boat.Status isUnderwater() {
      AABB var1 = this.getBoundingBox();
      double var2 = var1.maxY + 0.001D;
      int var4 = Mth.floor(var1.minX);
      int var5 = Mth.ceil(var1.maxX);
      int var6 = Mth.floor(var1.maxY);
      int var7 = Mth.ceil(var2);
      int var8 = Mth.floor(var1.minZ);
      int var9 = Mth.ceil(var1.maxZ);
      boolean var10 = false;
      BlockPos.PooledMutableBlockPos var11 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var12 = null;

      try {
         for(int var13 = var4; var13 < var5; ++var13) {
            for(int var14 = var6; var14 < var7; ++var14) {
               for(int var15 = var8; var15 < var9; ++var15) {
                  var11.set(var13, var14, var15);
                  FluidState var16 = this.level.getFluidState(var11);
                  if (var16.is(FluidTags.WATER) && var2 < (double)((float)var11.getY() + var16.getHeight(this.level, var11))) {
                     if (!var16.isSource()) {
                        Boat.Status var17 = Boat.Status.UNDER_FLOWING_WATER;
                        return var17;
                     }

                     var10 = true;
                  }
               }
            }
         }
      } catch (Throwable var27) {
         var12 = var27;
         throw var27;
      } finally {
         if (var11 != null) {
            if (var12 != null) {
               try {
                  var11.close();
               } catch (Throwable var26) {
                  var12.addSuppressed(var26);
               }
            } else {
               var11.close();
            }
         }

      }

      return var10 ? Boat.Status.UNDER_WATER : null;
   }

   private void floatBoat() {
      double var1 = -0.03999999910593033D;
      double var3 = this.isNoGravity() ? 0.0D : -0.03999999910593033D;
      double var5 = 0.0D;
      this.invFriction = 0.05F;
      if (this.oldStatus == Boat.Status.IN_AIR && this.status != Boat.Status.IN_AIR && this.status != Boat.Status.ON_LAND) {
         this.waterLevel = this.getBoundingBox().minY + (double)this.getBbHeight();
         this.setPos(this.x, (double)(this.getWaterLevelAbove() - this.getBbHeight()) + 0.101D, this.z);
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
         this.lastYd = 0.0D;
         this.status = Boat.Status.IN_WATER;
      } else {
         if (this.status == Boat.Status.IN_WATER) {
            var5 = (this.waterLevel - this.getBoundingBox().minY) / (double)this.getBbHeight();
            this.invFriction = 0.9F;
         } else if (this.status == Boat.Status.UNDER_FLOWING_WATER) {
            var3 = -7.0E-4D;
            this.invFriction = 0.9F;
         } else if (this.status == Boat.Status.UNDER_WATER) {
            var5 = 0.009999999776482582D;
            this.invFriction = 0.45F;
         } else if (this.status == Boat.Status.IN_AIR) {
            this.invFriction = 0.9F;
         } else if (this.status == Boat.Status.ON_LAND) {
            this.invFriction = this.landFriction;
            if (this.getControllingPassenger() instanceof Player) {
               this.landFriction /= 2.0F;
            }
         }

         Vec3 var7 = this.getDeltaMovement();
         this.setDeltaMovement(var7.x * (double)this.invFriction, var7.y + var3, var7.z * (double)this.invFriction);
         this.deltaRotation *= this.invFriction;
         if (var5 > 0.0D) {
            Vec3 var8 = this.getDeltaMovement();
            this.setDeltaMovement(var8.x, (var8.y + var5 * 0.06153846016296973D) * 0.75D, var8.z);
         }
      }

   }

   private void controlBoat() {
      if (this.isVehicle()) {
         float var1 = 0.0F;
         if (this.inputLeft) {
            --this.deltaRotation;
         }

         if (this.inputRight) {
            ++this.deltaRotation;
         }

         if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
            var1 += 0.005F;
         }

         this.yRot += this.deltaRotation;
         if (this.inputUp) {
            var1 += 0.04F;
         }

         if (this.inputDown) {
            var1 -= 0.005F;
         }

         this.setDeltaMovement(this.getDeltaMovement().add((double)(Mth.sin(-this.yRot * 0.017453292F) * var1), 0.0D, (double)(Mth.cos(this.yRot * 0.017453292F) * var1)));
         this.setPaddleState(this.inputRight && !this.inputLeft || this.inputUp, this.inputLeft && !this.inputRight || this.inputUp);
      }
   }

   public void positionRider(Entity var1) {
      if (this.hasPassenger(var1)) {
         float var2 = 0.0F;
         float var3 = (float)((this.removed ? 0.009999999776482582D : this.getRideHeight()) + var1.getRidingHeight());
         if (this.getPassengers().size() > 1) {
            int var4 = this.getPassengers().indexOf(var1);
            if (var4 == 0) {
               var2 = 0.2F;
            } else {
               var2 = -0.6F;
            }

            if (var1 instanceof Animal) {
               var2 = (float)((double)var2 + 0.2D);
            }
         }

         Vec3 var6 = (new Vec3((double)var2, 0.0D, 0.0D)).yRot(-this.yRot * 0.017453292F - 1.5707964F);
         var1.setPos(this.x + var6.x, this.y + (double)var3, this.z + var6.z);
         var1.yRot += this.deltaRotation;
         var1.setYHeadRot(var1.getYHeadRot() + this.deltaRotation);
         this.clampRotation(var1);
         if (var1 instanceof Animal && this.getPassengers().size() > 1) {
            int var5 = var1.getId() % 2 == 0 ? 90 : 270;
            var1.setYBodyRot(((Animal)var1).yBodyRot + (float)var5);
            var1.setYHeadRot(var1.getYHeadRot() + (float)var5);
         }

      }
   }

   protected void clampRotation(Entity var1) {
      var1.setYBodyRot(this.yRot);
      float var2 = Mth.wrapDegrees(var1.yRot - this.yRot);
      float var3 = Mth.clamp(var2, -105.0F, 105.0F);
      var1.yRotO += var3 - var2;
      var1.yRot += var3 - var2;
      var1.setYHeadRot(var1.yRot);
   }

   public void onPassengerTurned(Entity var1) {
      this.clampRotation(var1);
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putString("Type", this.getBoatType().getName());
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.contains("Type", 8)) {
         this.setType(Boat.Type.byName(var1.getString("Type")));
      }

   }

   public boolean interact(Player var1, InteractionHand var2) {
      if (var1.isSneaking()) {
         return false;
      } else {
         if (!this.level.isClientSide && this.outOfControlTicks < 60.0F) {
            var1.startRiding(this);
         }

         return true;
      }
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
      this.lastYd = this.getDeltaMovement().y;
      if (!this.isPassenger()) {
         if (var3) {
            if (this.fallDistance > 3.0F) {
               if (this.status != Boat.Status.ON_LAND) {
                  this.fallDistance = 0.0F;
                  return;
               }

               this.causeFallDamage(this.fallDistance, 1.0F);
               if (!this.level.isClientSide && !this.removed) {
                  this.remove();
                  if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                     int var6;
                     for(var6 = 0; var6 < 3; ++var6) {
                        this.spawnAtLocation(this.getBoatType().getPlanks());
                     }

                     for(var6 = 0; var6 < 2; ++var6) {
                        this.spawnAtLocation(Items.STICK);
                     }
                  }
               }
            }

            this.fallDistance = 0.0F;
         } else if (!this.level.getFluidState((new BlockPos(this)).below()).is(FluidTags.WATER) && var1 < 0.0D) {
            this.fallDistance = (float)((double)this.fallDistance - var1);
         }

      }
   }

   public boolean getPaddleState(int var1) {
      return (Boolean)this.entityData.get(var1 == 0 ? DATA_ID_PADDLE_LEFT : DATA_ID_PADDLE_RIGHT) && this.getControllingPassenger() != null;
   }

   public void setDamage(float var1) {
      this.entityData.set(DATA_ID_DAMAGE, var1);
   }

   public float getDamage() {
      return (Float)this.entityData.get(DATA_ID_DAMAGE);
   }

   public void setHurtTime(int var1) {
      this.entityData.set(DATA_ID_HURT, var1);
   }

   public int getHurtTime() {
      return (Integer)this.entityData.get(DATA_ID_HURT);
   }

   private void setBubbleTime(int var1) {
      this.entityData.set(DATA_ID_BUBBLE_TIME, var1);
   }

   private int getBubbleTime() {
      return (Integer)this.entityData.get(DATA_ID_BUBBLE_TIME);
   }

   public float getBubbleAngle(float var1) {
      return Mth.lerp(var1, this.bubbleAngleO, this.bubbleAngle);
   }

   public void setHurtDir(int var1) {
      this.entityData.set(DATA_ID_HURTDIR, var1);
   }

   public int getHurtDir() {
      return (Integer)this.entityData.get(DATA_ID_HURTDIR);
   }

   public void setType(Boat.Type var1) {
      this.entityData.set(DATA_ID_TYPE, var1.ordinal());
   }

   public Boat.Type getBoatType() {
      return Boat.Type.byId((Integer)this.entityData.get(DATA_ID_TYPE));
   }

   protected boolean canAddPassenger(Entity var1) {
      return this.getPassengers().size() < 2 && !this.isUnderLiquid(FluidTags.WATER);
   }

   @Nullable
   public Entity getControllingPassenger() {
      List var1 = this.getPassengers();
      return var1.isEmpty() ? null : (Entity)var1.get(0);
   }

   public void setInput(boolean var1, boolean var2, boolean var3, boolean var4) {
      this.inputLeft = var1;
      this.inputRight = var2;
      this.inputUp = var3;
      this.inputDown = var4;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   static {
      DATA_ID_HURT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
      DATA_ID_HURTDIR = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
      DATA_ID_DAMAGE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.FLOAT);
      DATA_ID_TYPE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
      DATA_ID_PADDLE_LEFT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BOOLEAN);
      DATA_ID_PADDLE_RIGHT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BOOLEAN);
      DATA_ID_BUBBLE_TIME = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
   }

   public static enum Type {
      OAK(Blocks.OAK_PLANKS, "oak"),
      SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
      BIRCH(Blocks.BIRCH_PLANKS, "birch"),
      JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
      ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
      DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

      private final String name;
      private final Block planks;

      private Type(Block var3, String var4) {
         this.name = var4;
         this.planks = var3;
      }

      public String getName() {
         return this.name;
      }

      public Block getPlanks() {
         return this.planks;
      }

      public String toString() {
         return this.name;
      }

      public static Boat.Type byId(int var0) {
         Boat.Type[] var1 = values();
         if (var0 < 0 || var0 >= var1.length) {
            var0 = 0;
         }

         return var1[var0];
      }

      public static Boat.Type byName(String var0) {
         Boat.Type[] var1 = values();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].getName().equals(var0)) {
               return var1[var2];
            }
         }

         return var1[0];
      }
   }

   public static enum Status {
      IN_WATER,
      UNDER_WATER,
      UNDER_FLOWING_WATER,
      ON_LAND,
      IN_AIR;

      private Status() {
      }
   }
}
