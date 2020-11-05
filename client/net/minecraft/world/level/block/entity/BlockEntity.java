package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockEntityType<?> type;
   @Nullable
   protected Level level;
   protected final BlockPos worldPosition;
   protected boolean remove;
   private BlockState blockState;

   public BlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super();
      this.type = var1;
      this.worldPosition = var2.immutable();
      this.blockState = var3;
   }

   @Nullable
   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   public boolean hasLevel() {
      return this.level != null;
   }

   public void load(CompoundTag var1) {
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
   public static BlockEntity loadStatic(BlockPos var0, BlockState var1, CompoundTag var2) {
      String var3 = var2.getString("id");
      return (BlockEntity)Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(var3)).map((var3x) -> {
         try {
            return var3x.create(var0, var1);
         } catch (Throwable var5) {
            LOGGER.error("Failed to create block entity {}", var3, var5);
            return null;
         }
      }).map((var2x) -> {
         try {
            var2x.load(var2);
            return var2x;
         } catch (Throwable var4) {
            LOGGER.error("Failed to load data for block entity {}", var3, var4);
            return null;
         }
      }).orElseGet(() -> {
         LOGGER.warn("Skipping BlockEntity with id {}", var3);
         return null;
      });
   }

   public void setChanged() {
      if (this.level != null) {
         setChanged(this.level, this.worldPosition, this.blockState);
      }

   }

   protected static void setChanged(Level var0, BlockPos var1, BlockState var2) {
      var0.blockEntityChanged(var1);
      if (!var2.isAir()) {
         var0.updateNeighbourForOutputSignal(var1, var2.getBlock());
      }

   }

   public double getViewDistance() {
      return 64.0D;
   }

   public BlockPos getBlockPos() {
      return this.worldPosition;
   }

   public BlockState getBlockState() {
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

   public void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("Name", () -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.level != null) {
         CrashReportCategory.populateBlockDetails(var1, this.level, this.worldPosition, this.getBlockState());
         CrashReportCategory.populateBlockDetails(var1, this.level, this.worldPosition, this.level.getBlockState(this.worldPosition));
      }
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   public BlockEntityType<?> getType() {
      return this.type;
   }

   @Deprecated
   public void setBlockState(BlockState var1) {
      this.blockState = var1;
   }
}
