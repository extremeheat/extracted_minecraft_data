package net.minecraft.world.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMinecart extends Entity {
   private static final EntityDataAccessor<Integer> DATA_ID_HURT;
   private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR;
   private static final EntityDataAccessor<Float> DATA_ID_DAMAGE;
   private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_BLOCK;
   private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_OFFSET;
   private static final EntityDataAccessor<Boolean> DATA_ID_CUSTOM_DISPLAY;
   private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS;
   private boolean flipped;
   private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS;
   private int lSteps;
   private double lx;
   private double ly;
   private double lz;
   private double lyr;
   private double lxr;
   private double lxd;
   private double lyd;
   private double lzd;

   protected AbstractMinecart(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.blocksBuilding = true;
   }

   protected AbstractMinecart(EntityType<?> var1, Level var2, double var3, double var5, double var7) {
      this(var1, var2);
      this.setPos(var3, var5, var7);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = var3;
      this.yo = var5;
      this.zo = var7;
   }

   public static AbstractMinecart createMinecart(Level var0, double var1, double var3, double var5, AbstractMinecart.Type var7) {
      if (var7 == AbstractMinecart.Type.CHEST) {
         return new MinecartChest(var0, var1, var3, var5);
      } else if (var7 == AbstractMinecart.Type.FURNACE) {
         return new MinecartFurnace(var0, var1, var3, var5);
      } else if (var7 == AbstractMinecart.Type.TNT) {
         return new MinecartTNT(var0, var1, var3, var5);
      } else if (var7 == AbstractMinecart.Type.SPAWNER) {
         return new MinecartSpawner(var0, var1, var3, var5);
      } else if (var7 == AbstractMinecart.Type.HOPPER) {
         return new MinecartHopper(var0, var1, var3, var5);
      } else {
         return (AbstractMinecart)(var7 == AbstractMinecart.Type.COMMAND_BLOCK ? new MinecartCommandBlock(var0, var1, var3, var5) : new Minecart(var0, var1, var3, var5));
      }
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_ID_HURT, 0);
      this.entityData.define(DATA_ID_HURTDIR, 1);
      this.entityData.define(DATA_ID_DAMAGE, 0.0F);
      this.entityData.define(DATA_ID_DISPLAY_BLOCK, Block.getId(Blocks.AIR.defaultBlockState()));
      this.entityData.define(DATA_ID_DISPLAY_OFFSET, 6);
      this.entityData.define(DATA_ID_CUSTOM_DISPLAY, false);
   }

   public boolean canCollideWith(Entity var1) {
      return Boat.canVehicleCollide(this, var1);
   }

   public boolean isPushable() {
      return true;
   }

   protected Vec3 getRelativePortalPosition(Direction.Axis var1, BlockUtil.FoundRectangle var2) {
      return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(var1, var2));
   }

   public double getPassengersRidingOffset() {
      return 0.0D;
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      Direction var2 = this.getMotionDirection();
      if (var2.getAxis() == Direction.Axis.Y) {
         return super.getDismountLocationForPassenger(var1);
      } else {
         int[][] var3 = DismountHelper.offsetsForDirection(var2);
         BlockPos var4 = this.blockPosition();
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
         ImmutableList var6 = var1.getDismountPoses();
         UnmodifiableIterator var7 = var6.iterator();

         while(var7.hasNext()) {
            Pose var8 = (Pose)var7.next();
            EntityDimensions var9 = var1.getDimensions(var8);
            float var10 = Math.min(var9.width, 1.0F) / 2.0F;
            UnmodifiableIterator var11 = ((ImmutableList)POSE_DISMOUNT_HEIGHTS.get(var8)).iterator();

            while(var11.hasNext()) {
               int var12 = (Integer)var11.next();
               int[][] var13 = var3;
               int var14 = var3.length;

               for(int var15 = 0; var15 < var14; ++var15) {
                  int[] var16 = var13[var15];
                  var5.set(var4.getX() + var16[0], var4.getY() + var12, var4.getZ() + var16[1]);
                  double var17 = this.level.getBlockFloorHeight(DismountHelper.nonClimbableShape(this.level, var5), () -> {
                     return DismountHelper.nonClimbableShape(this.level, var5.below());
                  });
                  if (DismountHelper.isBlockFloorValid(var17)) {
                     AABB var19 = new AABB((double)(-var10), 0.0D, (double)(-var10), (double)var10, (double)var9.height, (double)var10);
                     Vec3 var20 = Vec3.upFromBottomCenterOf(var5, var17);
                     if (DismountHelper.canDismountTo(this.level, var1, var19.move(var20))) {
                        var1.setPose(var8);
                        return var20;
                     }
                  }
               }
            }
         }

         double var21 = this.getBoundingBox().maxY;
         var5.set((double)var4.getX(), var21, (double)var4.getZ());
         UnmodifiableIterator var22 = var6.iterator();

         while(var22.hasNext()) {
            Pose var23 = (Pose)var22.next();
            double var24 = (double)var1.getDimensions(var23).height;
            int var25 = Mth.ceil(var21 - (double)var5.getY() + var24);
            double var26 = DismountHelper.findCeilingFrom(var5, var25, (var1x) -> {
               return this.level.getBlockState(var1x).getCollisionShape(this.level, var1x);
            });
            if (var21 + var24 <= var26) {
               var1.setPose(var23);
               break;
            }
         }

         return super.getDismountLocationForPassenger(var1);
      }
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (!this.level.isClientSide && !this.removed) {
         if (this.isInvulnerableTo(var1)) {
            return false;
         } else {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.markHurt();
            this.setDamage(this.getDamage() + var2 * 10.0F);
            boolean var3 = var1.getEntity() instanceof Player && ((Player)var1.getEntity()).abilities.instabuild;
            if (var3 || this.getDamage() > 40.0F) {
               this.ejectPassengers();
               if (var3 && !this.hasCustomName()) {
                  this.remove();
               } else {
                  this.destroy(var1);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   protected float getBlockSpeedFactor() {
      BlockState var1 = this.level.getBlockState(this.blockPosition());
      return var1.is(BlockTags.RAILS) ? 1.0F : super.getBlockSpeedFactor();
   }

   public void destroy(DamageSource var1) {
      this.remove();
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         ItemStack var2 = new ItemStack(Items.MINECART);
         if (this.hasCustomName()) {
            var2.setHoverName(this.getCustomName());
         }

         this.spawnAtLocation(var2);
      }

   }

   public void animateHurt() {
      this.setHurtDir(-this.getHurtDir());
      this.setHurtTime(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   public boolean isPickable() {
      return !this.removed;
   }

   private static Pair<Vec3i, Vec3i> exits(RailShape var0) {
      return (Pair)EXITS.get(var0);
   }

   public Direction getMotionDirection() {
      return this.flipped ? this.getDirection().getOpposite().getClockWise() : this.getDirection().getClockWise();
   }

   public void tick() {
      if (this.getHurtTime() > 0) {
         this.setHurtTime(this.getHurtTime() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.getY() < -64.0D) {
         this.outOfWorld();
      }

      this.handleNetherPortal();
      if (this.level.isClientSide) {
         if (this.lSteps > 0) {
            double var15 = this.getX() + (this.lx - this.getX()) / (double)this.lSteps;
            double var16 = this.getY() + (this.ly - this.getY()) / (double)this.lSteps;
            double var17 = this.getZ() + (this.lz - this.getZ()) / (double)this.lSteps;
            double var7 = Mth.wrapDegrees(this.lyr - (double)this.yRot);
            this.yRot = (float)((double)this.yRot + var7 / (double)this.lSteps);
            this.xRot = (float)((double)this.xRot + (this.lxr - (double)this.xRot) / (double)this.lSteps);
            --this.lSteps;
            this.setPos(var15, var16, var17);
            this.setRot(this.yRot, this.xRot);
         } else {
            this.reapplyPosition();
            this.setRot(this.yRot, this.xRot);
         }

      } else {
         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         int var1 = Mth.floor(this.getX());
         int var2 = Mth.floor(this.getY());
         int var3 = Mth.floor(this.getZ());
         if (this.level.getBlockState(new BlockPos(var1, var2 - 1, var3)).is(BlockTags.RAILS)) {
            --var2;
         }

         BlockPos var4 = new BlockPos(var1, var2, var3);
         BlockState var5 = this.level.getBlockState(var4);
         if (BaseRailBlock.isRail(var5)) {
            this.moveAlongTrack(var4, var5);
            if (var5.is(Blocks.ACTIVATOR_RAIL)) {
               this.activateMinecart(var1, var2, var3, (Boolean)var5.getValue(PoweredRailBlock.POWERED));
            }
         } else {
            this.comeOffTrack();
         }

         this.checkInsideBlocks();
         this.xRot = 0.0F;
         double var6 = this.xo - this.getX();
         double var8 = this.zo - this.getZ();
         if (var6 * var6 + var8 * var8 > 0.001D) {
            this.yRot = (float)(Mth.atan2(var8, var6) * 180.0D / 3.141592653589793D);
            if (this.flipped) {
               this.yRot += 180.0F;
            }
         }

         double var10 = (double)Mth.wrapDegrees(this.yRot - this.yRotO);
         if (var10 < -170.0D || var10 >= 170.0D) {
            this.yRot += 180.0F;
            this.flipped = !this.flipped;
         }

         this.setRot(this.yRot, this.xRot);
         if (this.getMinecartType() == AbstractMinecart.Type.RIDEABLE && getHorizontalDistanceSqr(this.getDeltaMovement()) > 0.01D) {
            List var18 = this.level.getEntities((Entity)this, this.getBoundingBox().inflate(0.20000000298023224D, 0.0D, 0.20000000298023224D), EntitySelector.pushableBy(this));
            if (!var18.isEmpty()) {
               for(int var19 = 0; var19 < var18.size(); ++var19) {
                  Entity var14 = (Entity)var18.get(var19);
                  if (!(var14 instanceof Player) && !(var14 instanceof IronGolem) && !(var14 instanceof AbstractMinecart) && !this.isVehicle() && !var14.isPassenger()) {
                     var14.startRiding(this);
                  } else {
                     var14.push(this);
                  }
               }
            }
         } else {
            Iterator var12 = this.level.getEntities(this, this.getBoundingBox().inflate(0.20000000298023224D, 0.0D, 0.20000000298023224D)).iterator();

            while(var12.hasNext()) {
               Entity var13 = (Entity)var12.next();
               if (!this.hasPassenger(var13) && var13.isPushable() && var13 instanceof AbstractMinecart) {
                  var13.push(this);
               }
            }
         }

         this.updateInWaterStateAndDoFluidPushing();
         if (this.isInLava()) {
            this.lavaHurt();
            this.fallDistance *= 0.5F;
         }

         this.firstTick = false;
      }
   }

   protected double getMaxSpeed() {
      return 0.4D;
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
   }

   protected void comeOffTrack() {
      double var1 = this.getMaxSpeed();
      Vec3 var3 = this.getDeltaMovement();
      this.setDeltaMovement(Mth.clamp(var3.x, -var1, var1), var3.y, Mth.clamp(var3.z, -var1, var1));
      if (this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      if (!this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.95D));
      }

   }

   protected void moveAlongTrack(BlockPos var1, BlockState var2) {
      this.fallDistance = 0.0F;
      double var3 = this.getX();
      double var5 = this.getY();
      double var7 = this.getZ();
      Vec3 var9 = this.getPos(var3, var5, var7);
      var5 = (double)var1.getY();
      boolean var10 = false;
      boolean var11 = false;
      BaseRailBlock var12 = (BaseRailBlock)var2.getBlock();
      if (var12 == Blocks.POWERED_RAIL) {
         var10 = (Boolean)var2.getValue(PoweredRailBlock.POWERED);
         var11 = !var10;
      }

      double var13 = 0.0078125D;
      Vec3 var15 = this.getDeltaMovement();
      RailShape var16 = (RailShape)var2.getValue(var12.getShapeProperty());
      switch(var16) {
      case ASCENDING_EAST:
         this.setDeltaMovement(var15.add(-0.0078125D, 0.0D, 0.0D));
         ++var5;
         break;
      case ASCENDING_WEST:
         this.setDeltaMovement(var15.add(0.0078125D, 0.0D, 0.0D));
         ++var5;
         break;
      case ASCENDING_NORTH:
         this.setDeltaMovement(var15.add(0.0D, 0.0D, 0.0078125D));
         ++var5;
         break;
      case ASCENDING_SOUTH:
         this.setDeltaMovement(var15.add(0.0D, 0.0D, -0.0078125D));
         ++var5;
      }

      var15 = this.getDeltaMovement();
      Pair var17 = exits(var16);
      Vec3i var18 = (Vec3i)var17.getFirst();
      Vec3i var19 = (Vec3i)var17.getSecond();
      double var20 = (double)(var19.getX() - var18.getX());
      double var22 = (double)(var19.getZ() - var18.getZ());
      double var24 = Math.sqrt(var20 * var20 + var22 * var22);
      double var26 = var15.x * var20 + var15.z * var22;
      if (var26 < 0.0D) {
         var20 = -var20;
         var22 = -var22;
      }

      double var28 = Math.min(2.0D, Math.sqrt(getHorizontalDistanceSqr(var15)));
      var15 = new Vec3(var28 * var20 / var24, var15.y, var28 * var22 / var24);
      this.setDeltaMovement(var15);
      Entity var30 = this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
      if (var30 instanceof Player) {
         Vec3 var31 = var30.getDeltaMovement();
         double var32 = getHorizontalDistanceSqr(var31);
         double var34 = getHorizontalDistanceSqr(this.getDeltaMovement());
         if (var32 > 1.0E-4D && var34 < 0.01D) {
            this.setDeltaMovement(this.getDeltaMovement().add(var31.x * 0.1D, 0.0D, var31.z * 0.1D));
            var11 = false;
         }
      }

      double var58;
      if (var11) {
         var58 = Math.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement()));
         if (var58 < 0.03D) {
            this.setDeltaMovement(Vec3.ZERO);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.0D, 0.5D));
         }
      }

      var58 = (double)var1.getX() + 0.5D + (double)var18.getX() * 0.5D;
      double var33 = (double)var1.getZ() + 0.5D + (double)var18.getZ() * 0.5D;
      double var35 = (double)var1.getX() + 0.5D + (double)var19.getX() * 0.5D;
      double var37 = (double)var1.getZ() + 0.5D + (double)var19.getZ() * 0.5D;
      var20 = var35 - var58;
      var22 = var37 - var33;
      double var39;
      double var41;
      double var43;
      if (var20 == 0.0D) {
         var39 = var7 - (double)var1.getZ();
      } else if (var22 == 0.0D) {
         var39 = var3 - (double)var1.getX();
      } else {
         var41 = var3 - var58;
         var43 = var7 - var33;
         var39 = (var41 * var20 + var43 * var22) * 2.0D;
      }

      var3 = var58 + var20 * var39;
      var7 = var33 + var22 * var39;
      this.setPos(var3, var5, var7);
      var41 = this.isVehicle() ? 0.75D : 1.0D;
      var43 = this.getMaxSpeed();
      var15 = this.getDeltaMovement();
      this.move(MoverType.SELF, new Vec3(Mth.clamp(var41 * var15.x, -var43, var43), 0.0D, Mth.clamp(var41 * var15.z, -var43, var43)));
      if (var18.getY() != 0 && Mth.floor(this.getX()) - var1.getX() == var18.getX() && Mth.floor(this.getZ()) - var1.getZ() == var18.getZ()) {
         this.setPos(this.getX(), this.getY() + (double)var18.getY(), this.getZ());
      } else if (var19.getY() != 0 && Mth.floor(this.getX()) - var1.getX() == var19.getX() && Mth.floor(this.getZ()) - var1.getZ() == var19.getZ()) {
         this.setPos(this.getX(), this.getY() + (double)var19.getY(), this.getZ());
      }

      this.applyNaturalSlowdown();
      Vec3 var45 = this.getPos(this.getX(), this.getY(), this.getZ());
      Vec3 var48;
      double var49;
      if (var45 != null && var9 != null) {
         double var46 = (var9.y - var45.y) * 0.05D;
         var48 = this.getDeltaMovement();
         var49 = Math.sqrt(getHorizontalDistanceSqr(var48));
         if (var49 > 0.0D) {
            this.setDeltaMovement(var48.multiply((var49 + var46) / var49, 1.0D, (var49 + var46) / var49));
         }

         this.setPos(this.getX(), var45.y, this.getZ());
      }

      int var56 = Mth.floor(this.getX());
      int var47 = Mth.floor(this.getZ());
      if (var56 != var1.getX() || var47 != var1.getZ()) {
         var48 = this.getDeltaMovement();
         var49 = Math.sqrt(getHorizontalDistanceSqr(var48));
         this.setDeltaMovement(var49 * (double)(var56 - var1.getX()), var48.y, var49 * (double)(var47 - var1.getZ()));
      }

      if (var10) {
         var48 = this.getDeltaMovement();
         var49 = Math.sqrt(getHorizontalDistanceSqr(var48));
         if (var49 > 0.01D) {
            double var51 = 0.06D;
            this.setDeltaMovement(var48.add(var48.x / var49 * 0.06D, 0.0D, var48.z / var49 * 0.06D));
         } else {
            Vec3 var57 = this.getDeltaMovement();
            double var52 = var57.x;
            double var54 = var57.z;
            if (var16 == RailShape.EAST_WEST) {
               if (this.isRedstoneConductor(var1.west())) {
                  var52 = 0.02D;
               } else if (this.isRedstoneConductor(var1.east())) {
                  var52 = -0.02D;
               }
            } else {
               if (var16 != RailShape.NORTH_SOUTH) {
                  return;
               }

               if (this.isRedstoneConductor(var1.north())) {
                  var54 = 0.02D;
               } else if (this.isRedstoneConductor(var1.south())) {
                  var54 = -0.02D;
               }
            }

            this.setDeltaMovement(var52, var57.y, var54);
         }
      }

   }

   private boolean isRedstoneConductor(BlockPos var1) {
      return this.level.getBlockState(var1).isRedstoneConductor(this.level, var1);
   }

   protected void applyNaturalSlowdown() {
      double var1 = this.isVehicle() ? 0.997D : 0.96D;
      this.setDeltaMovement(this.getDeltaMovement().multiply(var1, 0.0D, var1));
   }

   @Nullable
   public Vec3 getPosOffs(double var1, double var3, double var5, double var7) {
      int var9 = Mth.floor(var1);
      int var10 = Mth.floor(var3);
      int var11 = Mth.floor(var5);
      if (this.level.getBlockState(new BlockPos(var9, var10 - 1, var11)).is(BlockTags.RAILS)) {
         --var10;
      }

      BlockState var12 = this.level.getBlockState(new BlockPos(var9, var10, var11));
      if (BaseRailBlock.isRail(var12)) {
         RailShape var13 = (RailShape)var12.getValue(((BaseRailBlock)var12.getBlock()).getShapeProperty());
         var3 = (double)var10;
         if (var13.isAscending()) {
            var3 = (double)(var10 + 1);
         }

         Pair var14 = exits(var13);
         Vec3i var15 = (Vec3i)var14.getFirst();
         Vec3i var16 = (Vec3i)var14.getSecond();
         double var17 = (double)(var16.getX() - var15.getX());
         double var19 = (double)(var16.getZ() - var15.getZ());
         double var21 = Math.sqrt(var17 * var17 + var19 * var19);
         var17 /= var21;
         var19 /= var21;
         var1 += var17 * var7;
         var5 += var19 * var7;
         if (var15.getY() != 0 && Mth.floor(var1) - var9 == var15.getX() && Mth.floor(var5) - var11 == var15.getZ()) {
            var3 += (double)var15.getY();
         } else if (var16.getY() != 0 && Mth.floor(var1) - var9 == var16.getX() && Mth.floor(var5) - var11 == var16.getZ()) {
            var3 += (double)var16.getY();
         }

         return this.getPos(var1, var3, var5);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3 getPos(double var1, double var3, double var5) {
      int var7 = Mth.floor(var1);
      int var8 = Mth.floor(var3);
      int var9 = Mth.floor(var5);
      if (this.level.getBlockState(new BlockPos(var7, var8 - 1, var9)).is(BlockTags.RAILS)) {
         --var8;
      }

      BlockState var10 = this.level.getBlockState(new BlockPos(var7, var8, var9));
      if (BaseRailBlock.isRail(var10)) {
         RailShape var11 = (RailShape)var10.getValue(((BaseRailBlock)var10.getBlock()).getShapeProperty());
         Pair var12 = exits(var11);
         Vec3i var13 = (Vec3i)var12.getFirst();
         Vec3i var14 = (Vec3i)var12.getSecond();
         double var15 = (double)var7 + 0.5D + (double)var13.getX() * 0.5D;
         double var17 = (double)var8 + 0.0625D + (double)var13.getY() * 0.5D;
         double var19 = (double)var9 + 0.5D + (double)var13.getZ() * 0.5D;
         double var21 = (double)var7 + 0.5D + (double)var14.getX() * 0.5D;
         double var23 = (double)var8 + 0.0625D + (double)var14.getY() * 0.5D;
         double var25 = (double)var9 + 0.5D + (double)var14.getZ() * 0.5D;
         double var27 = var21 - var15;
         double var29 = (var23 - var17) * 2.0D;
         double var31 = var25 - var19;
         double var33;
         if (var27 == 0.0D) {
            var33 = var5 - (double)var9;
         } else if (var31 == 0.0D) {
            var33 = var1 - (double)var7;
         } else {
            double var35 = var1 - var15;
            double var37 = var5 - var19;
            var33 = (var35 * var27 + var37 * var31) * 2.0D;
         }

         var1 = var15 + var27 * var33;
         var3 = var17 + var29 * var33;
         var5 = var19 + var31 * var33;
         if (var29 < 0.0D) {
            ++var3;
         } else if (var29 > 0.0D) {
            var3 += 0.5D;
         }

         return new Vec3(var1, var3, var5);
      } else {
         return null;
      }
   }

   public AABB getBoundingBoxForCulling() {
      AABB var1 = this.getBoundingBox();
      return this.hasCustomDisplay() ? var1.inflate((double)Math.abs(this.getDisplayOffset()) / 16.0D) : var1;
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.getBoolean("CustomDisplayTile")) {
         this.setDisplayBlockState(NbtUtils.readBlockState(var1.getCompound("DisplayState")));
         this.setDisplayOffset(var1.getInt("DisplayOffset"));
      }

   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      if (this.hasCustomDisplay()) {
         var1.putBoolean("CustomDisplayTile", true);
         var1.put("DisplayState", NbtUtils.writeBlockState(this.getDisplayBlockState()));
         var1.putInt("DisplayOffset", this.getDisplayOffset());
      }

   }

   public void push(Entity var1) {
      if (!this.level.isClientSide) {
         if (!var1.noPhysics && !this.noPhysics) {
            if (!this.hasPassenger(var1)) {
               double var2 = var1.getX() - this.getX();
               double var4 = var1.getZ() - this.getZ();
               double var6 = var2 * var2 + var4 * var4;
               if (var6 >= 9.999999747378752E-5D) {
                  var6 = (double)Mth.sqrt(var6);
                  var2 /= var6;
                  var4 /= var6;
                  double var8 = 1.0D / var6;
                  if (var8 > 1.0D) {
                     var8 = 1.0D;
                  }

                  var2 *= var8;
                  var4 *= var8;
                  var2 *= 0.10000000149011612D;
                  var4 *= 0.10000000149011612D;
                  var2 *= (double)(1.0F - this.pushthrough);
                  var4 *= (double)(1.0F - this.pushthrough);
                  var2 *= 0.5D;
                  var4 *= 0.5D;
                  if (var1 instanceof AbstractMinecart) {
                     double var10 = var1.getX() - this.getX();
                     double var12 = var1.getZ() - this.getZ();
                     Vec3 var14 = (new Vec3(var10, 0.0D, var12)).normalize();
                     Vec3 var15 = (new Vec3((double)Mth.cos(this.yRot * 0.017453292F), 0.0D, (double)Mth.sin(this.yRot * 0.017453292F))).normalize();
                     double var16 = Math.abs(var14.dot(var15));
                     if (var16 < 0.800000011920929D) {
                        return;
                     }

                     Vec3 var18 = this.getDeltaMovement();
                     Vec3 var19 = var1.getDeltaMovement();
                     if (((AbstractMinecart)var1).getMinecartType() == AbstractMinecart.Type.FURNACE && this.getMinecartType() != AbstractMinecart.Type.FURNACE) {
                        this.setDeltaMovement(var18.multiply(0.2D, 1.0D, 0.2D));
                        this.push(var19.x - var2, 0.0D, var19.z - var4);
                        var1.setDeltaMovement(var19.multiply(0.95D, 1.0D, 0.95D));
                     } else if (((AbstractMinecart)var1).getMinecartType() != AbstractMinecart.Type.FURNACE && this.getMinecartType() == AbstractMinecart.Type.FURNACE) {
                        var1.setDeltaMovement(var19.multiply(0.2D, 1.0D, 0.2D));
                        var1.push(var18.x + var2, 0.0D, var18.z + var4);
                        this.setDeltaMovement(var18.multiply(0.95D, 1.0D, 0.95D));
                     } else {
                        double var20 = (var19.x + var18.x) / 2.0D;
                        double var22 = (var19.z + var18.z) / 2.0D;
                        this.setDeltaMovement(var18.multiply(0.2D, 1.0D, 0.2D));
                        this.push(var20 - var2, 0.0D, var22 - var4);
                        var1.setDeltaMovement(var19.multiply(0.2D, 1.0D, 0.2D));
                        var1.push(var20 + var2, 0.0D, var22 + var4);
                     }
                  } else {
                     this.push(-var2, 0.0D, -var4);
                     var1.push(var2 / 4.0D, 0.0D, var4 / 4.0D);
                  }
               }

            }
         }
      }
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.lx = var1;
      this.ly = var3;
      this.lz = var5;
      this.lyr = (double)var7;
      this.lxr = (double)var8;
      this.lSteps = var9 + 2;
      this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.lxd = var1;
      this.lyd = var3;
      this.lzd = var5;
      this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
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

   public void setHurtDir(int var1) {
      this.entityData.set(DATA_ID_HURTDIR, var1);
   }

   public int getHurtDir() {
      return (Integer)this.entityData.get(DATA_ID_HURTDIR);
   }

   public abstract AbstractMinecart.Type getMinecartType();

   public BlockState getDisplayBlockState() {
      return !this.hasCustomDisplay() ? this.getDefaultDisplayBlockState() : Block.stateById((Integer)this.getEntityData().get(DATA_ID_DISPLAY_BLOCK));
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.AIR.defaultBlockState();
   }

   public int getDisplayOffset() {
      return !this.hasCustomDisplay() ? this.getDefaultDisplayOffset() : (Integer)this.getEntityData().get(DATA_ID_DISPLAY_OFFSET);
   }

   public int getDefaultDisplayOffset() {
      return 6;
   }

   public void setDisplayBlockState(BlockState var1) {
      this.getEntityData().set(DATA_ID_DISPLAY_BLOCK, Block.getId(var1));
      this.setCustomDisplay(true);
   }

   public void setDisplayOffset(int var1) {
      this.getEntityData().set(DATA_ID_DISPLAY_OFFSET, var1);
      this.setCustomDisplay(true);
   }

   public boolean hasCustomDisplay() {
      return (Boolean)this.getEntityData().get(DATA_ID_CUSTOM_DISPLAY);
   }

   public void setCustomDisplay(boolean var1) {
      this.getEntityData().set(DATA_ID_CUSTOM_DISPLAY, var1);
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   static {
      DATA_ID_HURT = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
      DATA_ID_HURTDIR = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
      DATA_ID_DAMAGE = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.FLOAT);
      DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
      DATA_ID_DISPLAY_OFFSET = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
      DATA_ID_CUSTOM_DISPLAY = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.BOOLEAN);
      POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1));
      EXITS = (Map)Util.make(Maps.newEnumMap(RailShape.class), (var0) -> {
         Vec3i var1 = Direction.WEST.getNormal();
         Vec3i var2 = Direction.EAST.getNormal();
         Vec3i var3 = Direction.NORTH.getNormal();
         Vec3i var4 = Direction.SOUTH.getNormal();
         Vec3i var5 = var1.below();
         Vec3i var6 = var2.below();
         Vec3i var7 = var3.below();
         Vec3i var8 = var4.below();
         var0.put(RailShape.NORTH_SOUTH, Pair.of(var3, var4));
         var0.put(RailShape.EAST_WEST, Pair.of(var1, var2));
         var0.put(RailShape.ASCENDING_EAST, Pair.of(var5, var2));
         var0.put(RailShape.ASCENDING_WEST, Pair.of(var1, var6));
         var0.put(RailShape.ASCENDING_NORTH, Pair.of(var3, var8));
         var0.put(RailShape.ASCENDING_SOUTH, Pair.of(var7, var4));
         var0.put(RailShape.SOUTH_EAST, Pair.of(var4, var2));
         var0.put(RailShape.SOUTH_WEST, Pair.of(var4, var1));
         var0.put(RailShape.NORTH_WEST, Pair.of(var3, var1));
         var0.put(RailShape.NORTH_EAST, Pair.of(var3, var2));
      });
   }

   public static enum Type {
      RIDEABLE,
      CHEST,
      FURNACE,
      TNT,
      SPAWNER,
      HOPPER,
      COMMAND_BLOCK;

      private Type() {
      }
   }
}
