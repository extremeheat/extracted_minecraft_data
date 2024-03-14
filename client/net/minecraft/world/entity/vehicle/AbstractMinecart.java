package net.minecraft.world.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMinecart extends VehicleEntity {
   private static final Vec3 LOWERED_PASSENGER_ATTACHMENT = new Vec3(0.0, 0.0, 0.0);
   private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_OFFSET = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_ID_CUSTOM_DISPLAY = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.BOOLEAN);
   private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(
      Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1)
   );
   protected static final float WATER_SLOWDOWN_FACTOR = 0.95F;
   private boolean flipped;
   private boolean onRails;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYRot;
   private double lerpXRot;
   private Vec3 targetDeltaMovement = Vec3.ZERO;
   private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS = Util.make(Maps.newEnumMap(RailShape.class), var0 -> {
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

   protected AbstractMinecart(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.blocksBuilding = true;
   }

   protected AbstractMinecart(EntityType<?> var1, Level var2, double var3, double var5, double var7) {
      this(var1, var2);
      this.setPos(var3, var5, var7);
      this.xo = var3;
      this.yo = var5;
      this.zo = var7;
   }

   public static AbstractMinecart createMinecart(
      ServerLevel var0, double var1, double var3, double var5, AbstractMinecart.Type var7, ItemStack var8, @Nullable Player var9
   ) {
      Object var10 = switch(var7) {
         case CHEST -> new MinecartChest(var0, var1, var3, var5);
         case FURNACE -> new MinecartFurnace(var0, var1, var3, var5);
         case TNT -> new MinecartTNT(var0, var1, var3, var5);
         case SPAWNER -> new MinecartSpawner(var0, var1, var3, var5);
         case HOPPER -> new MinecartHopper(var0, var1, var3, var5);
         case COMMAND_BLOCK -> new MinecartCommandBlock(var0, var1, var3, var5);
         default -> new Minecart(var0, var1, var3, var5);
      };
      EntityType.createDefaultStackConfig(var0, var8, var9).accept(var10);
      return (AbstractMinecart)var10;
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_DISPLAY_BLOCK, Block.getId(Blocks.AIR.defaultBlockState()));
      var1.define(DATA_ID_DISPLAY_OFFSET, 6);
      var1.define(DATA_ID_CUSTOM_DISPLAY, false);
   }

   @Override
   public boolean canCollideWith(Entity var1) {
      return Boat.canVehicleCollide(this, var1);
   }

   @Override
   public boolean isPushable() {
      return true;
   }

   @Override
   protected Vec3 getRelativePortalPosition(Direction.Axis var1, BlockUtil.FoundRectangle var2) {
      return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(var1, var2));
   }

   @Override
   protected Vec3 getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      boolean var4 = var1 instanceof Villager || var1 instanceof WanderingTrader;
      return var4 ? LOWERED_PASSENGER_ATTACHMENT : super.getPassengerAttachmentPoint(var1, var2, var3);
   }

   @Override
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
            float var10 = Math.min(var9.width(), 1.0F) / 2.0F;
            UnmodifiableIterator var11 = ((ImmutableList)POSE_DISMOUNT_HEIGHTS.get(var8)).iterator();

            while(var11.hasNext()) {
               int var12 = var11.next();

               for(int[] var16 : var3) {
                  var5.set(var4.getX() + var16[0], var4.getY() + var12, var4.getZ() + var16[1]);
                  double var17 = this.level()
                     .getBlockFloorHeight(
                        DismountHelper.nonClimbableShape(this.level(), var5), () -> DismountHelper.nonClimbableShape(this.level(), var5.below())
                     );
                  if (DismountHelper.isBlockFloorValid(var17)) {
                     AABB var19 = new AABB((double)(-var10), 0.0, (double)(-var10), (double)var10, (double)var9.height(), (double)var10);
                     Vec3 var20 = Vec3.upFromBottomCenterOf(var5, var17);
                     if (DismountHelper.canDismountTo(this.level(), var1, var19.move(var20))) {
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
            double var24 = (double)var1.getDimensions(var23).height();
            int var25 = Mth.ceil(var21 - (double)var5.getY() + var24);
            double var26 = DismountHelper.findCeilingFrom(var5, var25, var1x -> this.level().getBlockState(var1x).getCollisionShape(this.level(), var1x));
            if (var21 + var24 <= var26) {
               var1.setPose(var23);
               break;
            }
         }

         return super.getDismountLocationForPassenger(var1);
      }
   }

   @Override
   protected float getBlockSpeedFactor() {
      BlockState var1 = this.level().getBlockState(this.blockPosition());
      return var1.is(BlockTags.RAILS) ? 1.0F : super.getBlockSpeedFactor();
   }

   @Override
   public void animateHurt(float var1) {
      this.setHurtDir(-this.getHurtDir());
      this.setHurtTime(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   @Override
   public boolean isPickable() {
      return !this.isRemoved();
   }

   private static Pair<Vec3i, Vec3i> exits(RailShape var0) {
      return (Pair<Vec3i, Vec3i>)EXITS.get(var0);
   }

   @Override
   public Direction getMotionDirection() {
      return this.flipped ? this.getDirection().getOpposite().getClockWise() : this.getDirection().getClockWise();
   }

   @Override
   protected double getDefaultGravity() {
      return this.isInWater() ? 0.005 : 0.04;
   }

   @Override
   public void tick() {
      if (this.getHurtTime() > 0) {
         this.setHurtTime(this.getHurtTime() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      this.checkBelowWorld();
      this.handleNetherPortal();
      if (this.level().isClientSide) {
         if (this.lerpSteps > 0) {
            this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
            --this.lerpSteps;
         } else {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
         }
      } else {
         this.applyGravity();
         int var1 = Mth.floor(this.getX());
         int var2 = Mth.floor(this.getY());
         int var3 = Mth.floor(this.getZ());
         if (this.level().getBlockState(new BlockPos(var1, var2 - 1, var3)).is(BlockTags.RAILS)) {
            --var2;
         }

         BlockPos var4 = new BlockPos(var1, var2, var3);
         BlockState var5 = this.level().getBlockState(var4);
         this.onRails = BaseRailBlock.isRail(var5);
         if (this.onRails) {
            this.moveAlongTrack(var4, var5);
            if (var5.is(Blocks.ACTIVATOR_RAIL)) {
               this.activateMinecart(var1, var2, var3, var5.getValue(PoweredRailBlock.POWERED));
            }
         } else {
            this.comeOffTrack();
         }

         this.checkInsideBlocks();
         this.setXRot(0.0F);
         double var6 = this.xo - this.getX();
         double var8 = this.zo - this.getZ();
         if (var6 * var6 + var8 * var8 > 0.001) {
            this.setYRot((float)(Mth.atan2(var8, var6) * 180.0 / 3.141592653589793));
            if (this.flipped) {
               this.setYRot(this.getYRot() + 180.0F);
            }
         }

         double var10 = (double)Mth.wrapDegrees(this.getYRot() - this.yRotO);
         if (var10 < -170.0 || var10 >= 170.0) {
            this.setYRot(this.getYRot() + 180.0F);
            this.flipped = !this.flipped;
         }

         this.setRot(this.getYRot(), this.getXRot());
         if (this.getMinecartType() == AbstractMinecart.Type.RIDEABLE && this.getDeltaMovement().horizontalDistanceSqr() > 0.01) {
            List var15 = this.level()
               .getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, 0.0, 0.20000000298023224), EntitySelector.pushableBy(this));
            if (!var15.isEmpty()) {
               for(Entity var14 : var15) {
                  if (!(var14 instanceof Player)
                     && !(var14 instanceof IronGolem)
                     && !(var14 instanceof AbstractMinecart)
                     && !this.isVehicle()
                     && !var14.isPassenger()) {
                     var14.startRiding(this);
                  } else {
                     var14.push(this);
                  }
               }
            }
         } else {
            for(Entity var13 : this.level().getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, 0.0, 0.20000000298023224))) {
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
      return (this.isInWater() ? 4.0 : 8.0) / 20.0;
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
   }

   protected void comeOffTrack() {
      double var1 = this.getMaxSpeed();
      Vec3 var3 = this.getDeltaMovement();
      this.setDeltaMovement(Mth.clamp(var3.x, -var1, var1), var3.y, Mth.clamp(var3.z, -var1, var1));
      if (this.onGround()) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      if (!this.onGround()) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.95));
      }
   }

   protected void moveAlongTrack(BlockPos var1, BlockState var2) {
      this.resetFallDistance();
      double var3 = this.getX();
      double var5 = this.getY();
      double var7 = this.getZ();
      Vec3 var9 = this.getPos(var3, var5, var7);
      var5 = (double)var1.getY();
      boolean var10 = false;
      boolean var11 = false;
      if (var2.is(Blocks.POWERED_RAIL)) {
         var10 = var2.getValue(PoweredRailBlock.POWERED);
         var11 = !var10;
      }

      double var12 = 0.0078125;
      if (this.isInWater()) {
         var12 *= 0.2;
      }

      Vec3 var14 = this.getDeltaMovement();
      RailShape var15 = var2.getValue(((BaseRailBlock)var2.getBlock()).getShapeProperty());
      switch(var15) {
         case ASCENDING_EAST:
            this.setDeltaMovement(var14.add(-var12, 0.0, 0.0));
            ++var5;
            break;
         case ASCENDING_WEST:
            this.setDeltaMovement(var14.add(var12, 0.0, 0.0));
            ++var5;
            break;
         case ASCENDING_NORTH:
            this.setDeltaMovement(var14.add(0.0, 0.0, var12));
            ++var5;
            break;
         case ASCENDING_SOUTH:
            this.setDeltaMovement(var14.add(0.0, 0.0, -var12));
            ++var5;
      }

      var14 = this.getDeltaMovement();
      Pair var16 = exits(var15);
      Vec3i var17 = (Vec3i)var16.getFirst();
      Vec3i var18 = (Vec3i)var16.getSecond();
      double var19 = (double)(var18.getX() - var17.getX());
      double var21 = (double)(var18.getZ() - var17.getZ());
      double var23 = Math.sqrt(var19 * var19 + var21 * var21);
      double var25 = var14.x * var19 + var14.z * var21;
      if (var25 < 0.0) {
         var19 = -var19;
         var21 = -var21;
      }

      double var27 = Math.min(2.0, var14.horizontalDistance());
      var14 = new Vec3(var27 * var19 / var23, var14.y, var27 * var21 / var23);
      this.setDeltaMovement(var14);
      Entity var29 = this.getFirstPassenger();
      if (var29 instanceof Player) {
         Vec3 var30 = var29.getDeltaMovement();
         double var31 = var30.horizontalDistanceSqr();
         double var33 = this.getDeltaMovement().horizontalDistanceSqr();
         if (var31 > 1.0E-4 && var33 < 0.01) {
            this.setDeltaMovement(this.getDeltaMovement().add(var30.x * 0.1, 0.0, var30.z * 0.1));
            var11 = false;
         }
      }

      if (var11) {
         double var63 = this.getDeltaMovement().horizontalDistance();
         if (var63 < 0.03) {
            this.setDeltaMovement(Vec3.ZERO);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.0, 0.5));
         }
      }

      double var64 = (double)var1.getX() + 0.5 + (double)var17.getX() * 0.5;
      double var32 = (double)var1.getZ() + 0.5 + (double)var17.getZ() * 0.5;
      double var34 = (double)var1.getX() + 0.5 + (double)var18.getX() * 0.5;
      double var36 = (double)var1.getZ() + 0.5 + (double)var18.getZ() * 0.5;
      var19 = var34 - var64;
      var21 = var36 - var32;
      double var38;
      if (var19 == 0.0) {
         var38 = var7 - (double)var1.getZ();
      } else if (var21 == 0.0) {
         var38 = var3 - (double)var1.getX();
      } else {
         double var40 = var3 - var64;
         double var42 = var7 - var32;
         var38 = (var40 * var19 + var42 * var21) * 2.0;
      }

      var3 = var64 + var19 * var38;
      var7 = var32 + var21 * var38;
      this.setPos(var3, var5, var7);
      double var65 = this.isVehicle() ? 0.75 : 1.0;
      double var66 = this.getMaxSpeed();
      var14 = this.getDeltaMovement();
      this.move(MoverType.SELF, new Vec3(Mth.clamp(var65 * var14.x, -var66, var66), 0.0, Mth.clamp(var65 * var14.z, -var66, var66)));
      if (var17.getY() != 0 && Mth.floor(this.getX()) - var1.getX() == var17.getX() && Mth.floor(this.getZ()) - var1.getZ() == var17.getZ()) {
         this.setPos(this.getX(), this.getY() + (double)var17.getY(), this.getZ());
      } else if (var18.getY() != 0 && Mth.floor(this.getX()) - var1.getX() == var18.getX() && Mth.floor(this.getZ()) - var1.getZ() == var18.getZ()) {
         this.setPos(this.getX(), this.getY() + (double)var18.getY(), this.getZ());
      }

      this.applyNaturalSlowdown();
      Vec3 var44 = this.getPos(this.getX(), this.getY(), this.getZ());
      if (var44 != null && var9 != null) {
         double var45 = (var9.y - var44.y) * 0.05;
         Vec3 var47 = this.getDeltaMovement();
         double var48 = var47.horizontalDistance();
         if (var48 > 0.0) {
            this.setDeltaMovement(var47.multiply((var48 + var45) / var48, 1.0, (var48 + var45) / var48));
         }

         this.setPos(this.getX(), var44.y, this.getZ());
      }

      int var67 = Mth.floor(this.getX());
      int var46 = Mth.floor(this.getZ());
      if (var67 != var1.getX() || var46 != var1.getZ()) {
         Vec3 var68 = this.getDeltaMovement();
         double var70 = var68.horizontalDistance();
         this.setDeltaMovement(var70 * (double)(var67 - var1.getX()), var68.y, var70 * (double)(var46 - var1.getZ()));
      }

      if (var10) {
         Vec3 var69 = this.getDeltaMovement();
         double var71 = var69.horizontalDistance();
         if (var71 > 0.01) {
            double var50 = 0.06;
            this.setDeltaMovement(var69.add(var69.x / var71 * 0.06, 0.0, var69.z / var71 * 0.06));
         } else {
            Vec3 var72 = this.getDeltaMovement();
            double var51 = var72.x;
            double var53 = var72.z;
            if (var15 == RailShape.EAST_WEST) {
               if (this.isRedstoneConductor(var1.west())) {
                  var51 = 0.02;
               } else if (this.isRedstoneConductor(var1.east())) {
                  var51 = -0.02;
               }
            } else {
               if (var15 != RailShape.NORTH_SOUTH) {
                  return;
               }

               if (this.isRedstoneConductor(var1.north())) {
                  var53 = 0.02;
               } else if (this.isRedstoneConductor(var1.south())) {
                  var53 = -0.02;
               }
            }

            this.setDeltaMovement(var51, var72.y, var53);
         }
      }
   }

   @Override
   public boolean isOnRails() {
      return this.onRails;
   }

   private boolean isRedstoneConductor(BlockPos var1) {
      return this.level().getBlockState(var1).isRedstoneConductor(this.level(), var1);
   }

   protected void applyNaturalSlowdown() {
      double var1 = this.isVehicle() ? 0.997 : 0.96;
      Vec3 var3 = this.getDeltaMovement();
      var3 = var3.multiply(var1, 0.0, var1);
      if (this.isInWater()) {
         var3 = var3.scale(0.949999988079071);
      }

      this.setDeltaMovement(var3);
   }

   @Nullable
   public Vec3 getPosOffs(double var1, double var3, double var5, double var7) {
      int var9 = Mth.floor(var1);
      int var10 = Mth.floor(var3);
      int var11 = Mth.floor(var5);
      if (this.level().getBlockState(new BlockPos(var9, var10 - 1, var11)).is(BlockTags.RAILS)) {
         --var10;
      }

      BlockState var12 = this.level().getBlockState(new BlockPos(var9, var10, var11));
      if (BaseRailBlock.isRail(var12)) {
         RailShape var13 = var12.getValue(((BaseRailBlock)var12.getBlock()).getShapeProperty());
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
      if (this.level().getBlockState(new BlockPos(var7, var8 - 1, var9)).is(BlockTags.RAILS)) {
         --var8;
      }

      BlockState var10 = this.level().getBlockState(new BlockPos(var7, var8, var9));
      if (BaseRailBlock.isRail(var10)) {
         RailShape var11 = var10.getValue(((BaseRailBlock)var10.getBlock()).getShapeProperty());
         Pair var12 = exits(var11);
         Vec3i var13 = (Vec3i)var12.getFirst();
         Vec3i var14 = (Vec3i)var12.getSecond();
         double var15 = (double)var7 + 0.5 + (double)var13.getX() * 0.5;
         double var17 = (double)var8 + 0.0625 + (double)var13.getY() * 0.5;
         double var19 = (double)var9 + 0.5 + (double)var13.getZ() * 0.5;
         double var21 = (double)var7 + 0.5 + (double)var14.getX() * 0.5;
         double var23 = (double)var8 + 0.0625 + (double)var14.getY() * 0.5;
         double var25 = (double)var9 + 0.5 + (double)var14.getZ() * 0.5;
         double var27 = var21 - var15;
         double var29 = (var23 - var17) * 2.0;
         double var31 = var25 - var19;
         double var33;
         if (var27 == 0.0) {
            var33 = var5 - (double)var9;
         } else if (var31 == 0.0) {
            var33 = var1 - (double)var7;
         } else {
            double var35 = var1 - var15;
            double var37 = var5 - var19;
            var33 = (var35 * var27 + var37 * var31) * 2.0;
         }

         var1 = var15 + var27 * var33;
         var3 = var17 + var29 * var33;
         var5 = var19 + var31 * var33;
         if (var29 < 0.0) {
            ++var3;
         } else if (var29 > 0.0) {
            var3 += 0.5;
         }

         return new Vec3(var1, var3, var5);
      } else {
         return null;
      }
   }

   @Override
   public AABB getBoundingBoxForCulling() {
      AABB var1 = this.getBoundingBox();
      return this.hasCustomDisplay() ? var1.inflate((double)Math.abs(this.getDisplayOffset()) / 16.0) : var1;
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.getBoolean("CustomDisplayTile")) {
         this.setDisplayBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), var1.getCompound("DisplayState")));
         this.setDisplayOffset(var1.getInt("DisplayOffset"));
      }
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      if (this.hasCustomDisplay()) {
         var1.putBoolean("CustomDisplayTile", true);
         var1.put("DisplayState", NbtUtils.writeBlockState(this.getDisplayBlockState()));
         var1.putInt("DisplayOffset", this.getDisplayOffset());
      }
   }

   @Override
   public void push(Entity var1) {
      if (!this.level().isClientSide) {
         if (!var1.noPhysics && !this.noPhysics) {
            if (!this.hasPassenger(var1)) {
               double var2 = var1.getX() - this.getX();
               double var4 = var1.getZ() - this.getZ();
               double var6 = var2 * var2 + var4 * var4;
               if (var6 >= 9.999999747378752E-5) {
                  var6 = Math.sqrt(var6);
                  var2 /= var6;
                  var4 /= var6;
                  double var8 = 1.0 / var6;
                  if (var8 > 1.0) {
                     var8 = 1.0;
                  }

                  var2 *= var8;
                  var4 *= var8;
                  var2 *= 0.10000000149011612;
                  var4 *= 0.10000000149011612;
                  var2 *= 0.5;
                  var4 *= 0.5;
                  if (var1 instanceof AbstractMinecart) {
                     double var10 = var1.getX() - this.getX();
                     double var12 = var1.getZ() - this.getZ();
                     Vec3 var14 = new Vec3(var10, 0.0, var12).normalize();
                     Vec3 var15 = new Vec3((double)Mth.cos(this.getYRot() * 0.017453292F), 0.0, (double)Mth.sin(this.getYRot() * 0.017453292F)).normalize();
                     double var16 = Math.abs(var14.dot(var15));
                     if (var16 < 0.800000011920929) {
                        return;
                     }

                     Vec3 var18 = this.getDeltaMovement();
                     Vec3 var19 = var1.getDeltaMovement();
                     if (((AbstractMinecart)var1).getMinecartType() == AbstractMinecart.Type.FURNACE && this.getMinecartType() != AbstractMinecart.Type.FURNACE
                        )
                      {
                        this.setDeltaMovement(var18.multiply(0.2, 1.0, 0.2));
                        this.push(var19.x - var2, 0.0, var19.z - var4);
                        var1.setDeltaMovement(var19.multiply(0.95, 1.0, 0.95));
                     } else if (((AbstractMinecart)var1).getMinecartType() != AbstractMinecart.Type.FURNACE
                        && this.getMinecartType() == AbstractMinecart.Type.FURNACE) {
                        var1.setDeltaMovement(var19.multiply(0.2, 1.0, 0.2));
                        var1.push(var18.x + var2, 0.0, var18.z + var4);
                        this.setDeltaMovement(var18.multiply(0.95, 1.0, 0.95));
                     } else {
                        double var20 = (var19.x + var18.x) / 2.0;
                        double var22 = (var19.z + var18.z) / 2.0;
                        this.setDeltaMovement(var18.multiply(0.2, 1.0, 0.2));
                        this.push(var20 - var2, 0.0, var22 - var4);
                        var1.setDeltaMovement(var19.multiply(0.2, 1.0, 0.2));
                        var1.push(var20 + var2, 0.0, var22 + var4);
                     }
                  } else {
                     this.push(-var2, 0.0, -var4);
                     var1.push(var2 / 4.0, 0.0, var4 / 4.0);
                  }
               }
            }
         }
      }
   }

   @Override
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.lerpX = var1;
      this.lerpY = var3;
      this.lerpZ = var5;
      this.lerpYRot = (double)var7;
      this.lerpXRot = (double)var8;
      this.lerpSteps = var9 + 2;
      this.setDeltaMovement(this.targetDeltaMovement);
   }

   @Override
   public double lerpTargetX() {
      return this.lerpSteps > 0 ? this.lerpX : this.getX();
   }

   @Override
   public double lerpTargetY() {
      return this.lerpSteps > 0 ? this.lerpY : this.getY();
   }

   @Override
   public double lerpTargetZ() {
      return this.lerpSteps > 0 ? this.lerpZ : this.getZ();
   }

   @Override
   public float lerpTargetXRot() {
      return this.lerpSteps > 0 ? (float)this.lerpXRot : this.getXRot();
   }

   @Override
   public float lerpTargetYRot() {
      return this.lerpSteps > 0 ? (float)this.lerpYRot : this.getYRot();
   }

   @Override
   public void lerpMotion(double var1, double var3, double var5) {
      this.targetDeltaMovement = new Vec3(var1, var3, var5);
      this.setDeltaMovement(this.targetDeltaMovement);
   }

   public abstract AbstractMinecart.Type getMinecartType();

   public BlockState getDisplayBlockState() {
      return !this.hasCustomDisplay() ? this.getDefaultDisplayBlockState() : Block.stateById(this.getEntityData().get(DATA_ID_DISPLAY_BLOCK));
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.AIR.defaultBlockState();
   }

   public int getDisplayOffset() {
      return !this.hasCustomDisplay() ? this.getDefaultDisplayOffset() : this.getEntityData().get(DATA_ID_DISPLAY_OFFSET);
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
      return this.getEntityData().get(DATA_ID_CUSTOM_DISPLAY);
   }

   public void setCustomDisplay(boolean var1) {
      this.getEntityData().set(DATA_ID_CUSTOM_DISPLAY, var1);
   }

   @Override
   public ItemStack getPickResult() {
      return new ItemStack(switch(this.getMinecartType()) {
         case CHEST -> Items.CHEST_MINECART;
         case FURNACE -> Items.FURNACE_MINECART;
         case TNT -> Items.TNT_MINECART;
         default -> Items.MINECART;
         case HOPPER -> Items.HOPPER_MINECART;
         case COMMAND_BLOCK -> Items.COMMAND_BLOCK_MINECART;
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
