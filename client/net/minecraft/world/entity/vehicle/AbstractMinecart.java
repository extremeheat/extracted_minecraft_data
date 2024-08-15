package net.minecraft.world.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.flag.FeatureFlags;
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
   private boolean onRails;
   private boolean flipped;
   private Vec3 passengerMoveIntent = Vec3.ZERO;
   private final MinecartBehavior behavior;
   private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS = Util.make(Maps.newEnumMap(RailShape.class), var0 -> {
      Vec3i var1 = Direction.WEST.getUnitVec3i();
      Vec3i var2 = Direction.EAST.getUnitVec3i();
      Vec3i var3 = Direction.NORTH.getUnitVec3i();
      Vec3i var4 = Direction.SOUTH.getUnitVec3i();
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
      if (useExperimentalMovement(var2)) {
         this.behavior = new NewMinecartBehavior(this);
      } else {
         this.behavior = new OldMinecartBehavior(this);
      }
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
      Object var10 = switch (var7) {
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

   public MinecartBehavior getBehavior() {
      return this.behavior;
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
   public Vec3 getRelativePortalPosition(Direction.Axis var1, BlockUtil.FoundRectangle var2) {
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

         while (var7.hasNext()) {
            Pose var8 = (Pose)var7.next();
            EntityDimensions var9 = var1.getDimensions(var8);
            float var10 = Math.min(var9.width(), 1.0F) / 2.0F;
            UnmodifiableIterator var11 = ((ImmutableList)POSE_DISMOUNT_HEIGHTS.get(var8)).iterator();

            while (var11.hasNext()) {
               int var12 = (Integer)var11.next();

               for (int[] var16 : var3) {
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

         while (var22.hasNext()) {
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

   public static Pair<Vec3i, Vec3i> exits(RailShape var0) {
      return EXITS.get(var0);
   }

   @Override
   public Direction getMotionDirection() {
      return this.behavior.getMotionDirection();
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
      this.handlePortal();
      this.behavior.tick();
      this.updateInWaterStateAndDoFluidPushing();
      if (this.isInLava()) {
         this.lavaHurt();
         this.fallDistance *= 0.5F;
      }

      this.firstTick = false;
   }

   public BlockPos getCurrentBlockPos() {
      int var1 = Mth.floor(this.getX());
      int var2 = Mth.floor(this.getY());
      int var3 = Mth.floor(this.getZ());
      if (this.level().getBlockState(new BlockPos(var1, var2 - 1, var3)).is(BlockTags.RAILS)) {
         var2--;
      }

      return new BlockPos(var1, var2, var3);
   }

   public boolean pushOrPickUpEntities(AABB var1, double var2) {
      boolean var4 = false;
      if (this.getMinecartType() == AbstractMinecart.Type.RIDEABLE && this.getDeltaMovement().horizontalDistanceSqr() >= var2) {
         List var8 = this.level().getEntities(this, var1, EntitySelector.pushableBy(this));
         if (!var8.isEmpty()) {
            for (Entity var7 : var8) {
               if (!(var7 instanceof Player) && !(var7 instanceof IronGolem) && !(var7 instanceof AbstractMinecart) && !this.isVehicle() && !var7.isPassenger()
                  )
                {
                  var7.startRiding(this);
                  var4 = true;
               } else {
                  var7.push(this);
               }
            }
         }
      } else {
         for (Entity var6 : this.level().getEntities(this, var1)) {
            if (!this.hasPassenger(var6) && var6.isPushable() && var6 instanceof AbstractMinecart) {
               var6.push(this);
            }
         }
      }

      return var4;
   }

   protected double getMaxSpeed() {
      return this.behavior.getMaxSpeed();
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
   }

   @Override
   public void lerpPositionAndRotationStep(int var1, double var2, double var4, double var6, double var8, double var10) {
      super.lerpPositionAndRotationStep(var1, var2, var4, var6, var8, var10);
   }

   @Override
   public void applyGravity() {
      super.applyGravity();
   }

   @Override
   public void reapplyPosition() {
      super.reapplyPosition();
   }

   @Override
   public boolean updateInWaterStateAndDoFluidPushing() {
      return super.updateInWaterStateAndDoFluidPushing();
   }

   @Override
   public Vec3 getKnownMovement() {
      return this.behavior.getKnownMovement(super.getKnownMovement());
   }

   @Override
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.behavior.lerpTo(var1, var3, var5, var7, var8, var9);
   }

   @Override
   public double lerpTargetX() {
      return this.behavior.lerpTargetX();
   }

   @Override
   public double lerpTargetY() {
      return this.behavior.lerpTargetY();
   }

   @Override
   public double lerpTargetZ() {
      return this.behavior.lerpTargetZ();
   }

   @Override
   public float lerpTargetXRot() {
      return this.behavior.lerpTargetXRot();
   }

   @Override
   public float lerpTargetYRot() {
      return this.behavior.lerpTargetYRot();
   }

   @Override
   public void lerpMotion(double var1, double var3, double var5) {
      this.behavior.lerpMotion(var1, var3, var5);
   }

   protected void moveAlongTrack() {
      this.behavior.moveAlongTrack();
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

   protected double makeStepAlongTrack(BlockPos var1, RailShape var2, double var3) {
      return this.behavior.stepAlongTrack(var1, var2, var3);
   }

   @Override
   public void move(MoverType var1, Vec3 var2) {
      if (useExperimentalMovement(this.level())) {
         Vec3 var3 = this.position().add(var2);
         super.move(var1, var2);
         if (this.horizontalCollision || this.verticalCollision) {
            boolean var4 = this.pushOrPickUpEntities(this.getBoundingBox().inflate(1.0E-7), 0.0);
            if (var4) {
               super.move(var1, var3.subtract(this.position()));
            }
         }
      } else {
         super.move(var1, var2);
      }
   }

   @Override
   public boolean isOnRails() {
      return this.onRails;
   }

   public void setOnRails(boolean var1) {
      this.onRails = var1;
   }

   public boolean isflipped() {
      return this.flipped;
   }

   public void setFlipped(boolean var1) {
      this.flipped = var1;
   }

   public Vec3 getRedstoneDirection(BlockPos var1) {
      BlockState var2 = this.level().getBlockState(var1);
      if (var2.is(Blocks.POWERED_RAIL) && var2.getValue(PoweredRailBlock.POWERED)) {
         RailShape var3 = var2.getValue(((BaseRailBlock)var2.getBlock()).getShapeProperty());
         if (var3 == RailShape.EAST_WEST) {
            if (this.isRedstoneConductor(var1.west())) {
               return new Vec3(1.0, 0.0, 0.0);
            }

            if (this.isRedstoneConductor(var1.east())) {
               return new Vec3(-1.0, 0.0, 0.0);
            }
         } else if (var3 == RailShape.NORTH_SOUTH) {
            if (this.isRedstoneConductor(var1.north())) {
               return new Vec3(0.0, 0.0, 1.0);
            }

            if (this.isRedstoneConductor(var1.south())) {
               return new Vec3(0.0, 0.0, -1.0);
            }
         }

         return Vec3.ZERO;
      } else {
         return Vec3.ZERO;
      }
   }

   public boolean isRedstoneConductor(BlockPos var1) {
      return this.level().getBlockState(var1).isRedstoneConductor(this.level(), var1);
   }

   protected Vec3 applyNaturalSlowdown(Vec3 var1) {
      double var2 = this.behavior.getSlowdownFactor();
      Vec3 var4 = var1.multiply(var2, 0.0, var2);
      if (this.isInWater()) {
         var4 = var4.scale(0.949999988079071);
      }

      return var4;
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.getBoolean("CustomDisplayTile")) {
         this.setDisplayBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), var1.getCompound("DisplayState")));
         this.setDisplayOffset(var1.getInt("DisplayOffset"));
      }

      this.flipped = var1.getBoolean("FlippedRotation");
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      if (this.hasCustomDisplay()) {
         var1.putBoolean("CustomDisplayTile", true);
         var1.put("DisplayState", NbtUtils.writeBlockState(this.getDisplayBlockState()));
         var1.putInt("DisplayOffset", this.getDisplayOffset());
      }

      var1.putBoolean("FlippedRotation", this.flipped);
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
      return new ItemStack(switch (this.getMinecartType()) {
         case CHEST -> Items.CHEST_MINECART;
         case FURNACE -> Items.FURNACE_MINECART;
         case TNT -> Items.TNT_MINECART;
         default -> Items.MINECART;
         case HOPPER -> Items.HOPPER_MINECART;
         case COMMAND_BLOCK -> Items.COMMAND_BLOCK_MINECART;
      });
   }

   public void setPassengerMoveIntentFromInput(LivingEntity var1, Vec3 var2) {
      Vec3 var3 = getInputVector(var2, 1.0F, var1.getYRot());
      this.setPassengerMoveIntent(var3);
   }

   public void setPassengerMoveIntent(Vec3 var1) {
      this.passengerMoveIntent = var1;
   }

   public Vec3 getPassengerMoveIntent() {
      return this.passengerMoveIntent;
   }

   public static boolean useExperimentalMovement(Level var0) {
      return var0.enabledFeatures().contains(FeatureFlags.MINECART_IMPROVEMENTS);
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
