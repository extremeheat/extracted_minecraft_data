package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public abstract class BlockEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockEntityType type;
   @Nullable
   protected Level level;
   protected BlockPos worldPosition;
   protected boolean remove;
   @Nullable
   private BlockState blockState;
   private boolean hasLoggedInvalidStateBefore;

   public BlockEntity(BlockEntityType var1) {
      this.worldPosition = BlockPos.ZERO;
      this.type = var1;
   }

   @Nullable
   public Level getLevel() {
      return this.level;
   }

   public void setLevelAndPosition(Level var1, BlockPos var2) {
      this.level = var1;
      this.worldPosition = var2.immutable();
   }

   public boolean hasLevel() {
      return this.level != null;
   }

   public void load(CompoundTag var1) {
      this.worldPosition = new BlockPos(var1.getInt("x"), var1.getInt("y"), var1.getInt("z"));
   }

   public CompoundTag save(CompoundTag var1) {
      return this.saveMetadata(var1);
   }

   private CompoundTag saveMetadata(CompoundTag var1) {
      ResourceLocation var2 = BlockEntityType.getKey(this.getType());
      if (var2 == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         var1.putString("id", var2.toString());
         var1.putInt("x", this.worldPosition.getX());
         var1.putInt("y", this.worldPosition.getY());
         var1.putInt("z", this.worldPosition.getZ());
         return var1;
      }
   }

   @Nullable
   public static BlockEntity loadStatic(CompoundTag var0) {
      String var1 = var0.getString("id");
      return (BlockEntity)Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(var1)).map((var1x) -> {
         try {
            return var1x.create();
         } catch (Throwable var3) {
            LOGGER.error("Failed to create block entity {}", var1, var3);
            return null;
         }
      }).map((var2) -> {
         try {
            var2.load(var0);
            return var2;
         } catch (Throwable var4) {
            LOGGER.error("Failed to load data for block entity {}", var1, var4);
            return null;
         }
      }).orElseGet(() -> {
         LOGGER.warn("Skipping BlockEntity with id {}", var1);
         return null;
      });
   }

   public void setChanged() {
      if (this.level != null) {
         this.blockState = this.level.getBlockState(this.worldPosition);
         this.level.blockEntityChanged(this.worldPosition, this);
         if (!this.blockState.isAir()) {
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.blockState.getBlock());
         }
      }

   }

   public double distanceToSqr(double var1, double var3, double var5) {
      double var7 = (double)this.worldPosition.getX() + 0.5D - var1;
      double var9 = (double)this.worldPosition.getY() + 0.5D - var3;
      double var11 = (double)this.worldPosition.getZ() + 0.5D - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double getViewDistance() {
      return 4096.0D;
   }

   public BlockPos getBlockPos() {
      return this.worldPosition;
   }

   public BlockState getBlockState() {
      if (this.blockState == null) {
         this.blockState = this.level.getBlockState(this.worldPosition);
      }

      return this.blockState;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return null;
   }

   public CompoundTag getUpdateTag() {
      return this.saveMetadata(new CompoundTag());
   }

   public boolean isRemoved() {
      return this.remove;
   }

   public void setRemoved() {
      this.remove = true;
   }

   public void clearRemoved() {
      this.remove = false;
   }

   public boolean triggerEvent(int var1, int var2) {
      return false;
   }

   public void clearCache() {
      this.blockState = null;
   }

   public void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("Name", () -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.level != null) {
         CrashReportCategory.populateBlockDetails(var1, this.worldPosition, this.getBlockState());
         CrashReportCategory.populateBlockDetails(var1, this.worldPosition, this.level.getBlockState(this.worldPosition));
      }
   }

   public void setPosition(BlockPos var1) {
      this.worldPosition = var1.immutable();
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   public void rotate(Rotation var1) {
   }

   public void mirror(Mirror var1) {
   }

   public BlockEntityType getType() {
      return this.type;
   }

   public void logInvalidState() {
      if (!this.hasLoggedInvalidStateBefore) {
         this.hasLoggedInvalidStateBefore = true;
         LOGGER.warn("Block entity invalid: {} @ {}", new Supplier[]{() -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType());
         }, this::getBlockPos});
      }
   }
}
