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
   private final BlockEntityType<?> type;
   @Nullable
   protected Level level;
   protected BlockPos worldPosition;
   protected boolean remove;
   @Nullable
   private BlockState blockState;
   private boolean hasLoggedInvalidStateBefore;

   public BlockEntity(BlockEntityType<?> var1) {
      super();
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

   public void load(BlockState var1, CompoundTag var2) {
      this.worldPosition = new BlockPos(var2.getInt("x"), var2.getInt("y"), var2.getInt("z"));
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
   public static BlockEntity loadStatic(BlockState var0, CompoundTag var1) {
      String var2 = var1.getString("id");
      return (BlockEntity)Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(var2)).map((var1x) -> {
         try {
            return var1x.create();
         } catch (Throwable var3) {
            LOGGER.error("Failed to create block entity {}", var2, var3);
            return null;
         }
      }).map((var3) -> {
         try {
            var3.load(var0, var1);
            return var3;
         } catch (Throwable var5) {
            LOGGER.error("Failed to load data for block entity {}", var2, var5);
            return null;
         }
      }).orElseGet(() -> {
         LOGGER.warn("Skipping BlockEntity with id {}", var2);
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

   public double getViewDistance() {
      return 64.0D;
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

   public BlockEntityType<?> getType() {
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
