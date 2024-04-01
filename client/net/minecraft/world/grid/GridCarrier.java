package net.minecraft.world.grid;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddSubGridPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GridCarrier extends Entity {
   public static final int LERP_STEPS = 2;
   private static final EntityDataAccessor<Direction> MOVEMENT_DIRECTION = SynchedEntityData.defineId(GridCarrier.class, EntityDataSerializers.DIRECTION);
   private static final EntityDataAccessor<Float> MOVEMENT_SPEED = SynchedEntityData.defineId(GridCarrier.class, EntityDataSerializers.FLOAT);
   private final SubGrid grid;
   @Nullable
   private SubGridMovementCollider movementCollider;
   @Nullable
   private GridCarrier.PosInterpolationTarget posInterpolationTarget;
   private int placeInTicks;

   public GridCarrier(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.grid = var2.createSubGrid(this);
      this.noCulling = true;
   }

   public void setMovement(Direction var1, float var2) {
      this.getEntityData().set(MOVEMENT_DIRECTION, var1);
      this.getEntityData().set(MOVEMENT_SPEED, var2);
      this.movementCollider = SubGridMovementCollider.generate(this.grid.getBlocks(), var1);
   }

   public void clearMovement() {
      this.getEntityData().set(MOVEMENT_SPEED, 0.0F);
      this.movementCollider = null;
   }

   public SubGrid grid() {
      return this.grid;
   }

   @Override
   public void setPos(double var1, double var3, double var5) {
      super.setPos(var1, var3, var5);
      if (this.grid != null) {
         this.grid.updatePosition(var1, var3, var5);
      }
   }

   @Override
   public void tick() {
      super.tick();
      Direction var1 = this.getMovementDirection();
      this.grid.getBlocks().tick(this.level(), this.position(), var1);
      if (this.level().isClientSide()) {
         this.tickClient();
      } else {
         this.tickServer();
      }
   }

   private void tickClient() {
      if (this.posInterpolationTarget != null) {
         this.posInterpolationTarget.applyLerpStep(this);
         if (--this.posInterpolationTarget.steps == 0) {
            this.posInterpolationTarget = null;
         }
      }
   }

   private void tickServer() {
      Direction var1 = this.getMovementDirection();
      float var2 = this.getMovementSpeed();
      if (this.placeInTicks == 0 && var2 == 0.0F) {
         this.placeInTicks = 2;
      }

      if (this.placeInTicks > 0) {
         --this.placeInTicks;
         if (this.placeInTicks == 1) {
            this.grid.getBlocks().place(this.blockPosition(), this.level());
         } else if (this.placeInTicks == 0) {
            this.discard();
         }
      } else if (this.movementCollider != null) {
         this.tickMovement(this.movementCollider, var1, var2);
      }
   }

   private void tickMovement(SubGridMovementCollider var1, Direction var2, float var3) {
      Vec3 var4 = this.position();
      Vec3 var5 = var4.add((double)((float)var2.getStepX() * var3), (double)((float)var2.getStepY() * var3), (double)((float)var2.getStepZ() * var3));
      BlockPos var6 = this.getCollidingPos(var4, var2);
      BlockPos var7 = this.getCollidingPos(var5, var2);
      BlockPos.MutableBlockPos var8 = var6.mutable();

      while(!var8.equals(var7)) {
         var8.move(var2);
         if (var1.checkCollision(this.level(), var8)) {
            BlockPos var9 = var8.relative(var2, -1);
            this.setPos(Vec3.atLowerCornerOf(var9));
            this.clearMovement();
            this.placeInTicks = 5;
            return;
         }
      }

      this.setPos(var5);
   }

   private BlockPos getCollidingPos(Vec3 var1, Direction var2) {
      BlockPos var3 = BlockPos.containing(var1);
      return var2.getAxisDirection() == Direction.AxisDirection.POSITIVE ? var3.relative(var2) : var3;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(MOVEMENT_DIRECTION, Direction.NORTH);
      var1.define(MOVEMENT_SPEED, 0.0F);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      this.grid.setBlocks(SubGridBlocks.decode(this.registryAccess().lookupOrThrow(Registries.BLOCK), var1.getCompound("blocks")));
      if (var1.contains("biome", 8)) {
         this.registryAccess().registryOrThrow(Registries.BIOME).getHolder(new ResourceLocation(var1.getString("biome"))).ifPresent(this.grid::setBiome);
      }

      if (var1.contains("movement_direction", 8)) {
         Direction var2 = Direction.byName(var1.getString("movement_direction"));
         if (var2 != null) {
            this.setMovement(var2, var1.getFloat("movement_speed"));
         }
      } else {
         this.clearMovement();
      }
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.put("blocks", this.grid.getBlocks().encode());
      this.grid.getBiome().unwrapKey().ifPresent(var1x -> var1.putString("biome", var1x.location().toString()));
      var1.putString("movement_direction", this.getMovementDirection().getSerializedName());
      var1.putFloat("movement_speed", this.getMovementSpeed());
   }

   private float getMovementSpeed() {
      return this.getEntityData().get(MOVEMENT_SPEED);
   }

   private Direction getMovementDirection() {
      return this.getEntityData().get(MOVEMENT_DIRECTION);
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddSubGridPacket(this);
   }

   @Override
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.posInterpolationTarget = new GridCarrier.PosInterpolationTarget(2, var1, var3, var5);
   }

   @Override
   public double lerpTargetX() {
      return this.posInterpolationTarget != null ? this.posInterpolationTarget.targetX : this.getX();
   }

   @Override
   public double lerpTargetY() {
      return this.posInterpolationTarget != null ? this.posInterpolationTarget.targetY : this.getY();
   }

   @Override
   public double lerpTargetZ() {
      return this.posInterpolationTarget != null ? this.posInterpolationTarget.targetZ : this.getZ();
   }

   static class PosInterpolationTarget {
      int steps;
      final double targetX;
      final double targetY;
      final double targetZ;

      PosInterpolationTarget(int var1, double var2, double var4, double var6) {
         super();
         this.steps = var1;
         this.targetX = var2;
         this.targetY = var4;
         this.targetZ = var6;
      }

      void applyLerpStep(Entity var1) {
         var1.lerpPositionAndRotationStep(this.steps, this.targetX, this.targetY, this.targetZ, 0.0, 0.0);
      }
   }
}
