package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public abstract class BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
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

   public static BlockPos getPosFromTag(CompoundTag var0) {
      return new BlockPos(var0.getInt("x"), var0.getInt("y"), var0.getInt("z"));
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

   public void load(CompoundTag var1, HolderLookup.Provider var2) {
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
   }

   public final CompoundTag saveWithFullMetadata(HolderLookup.Provider var1) {
      CompoundTag var2 = this.saveWithoutMetadata(var1);
      this.saveMetadata(var2);
      return var2;
   }

   public final CompoundTag saveWithId(HolderLookup.Provider var1) {
      CompoundTag var2 = this.saveWithoutMetadata(var1);
      this.saveId(var2);
      return var2;
   }

   public final CompoundTag saveWithoutMetadata(HolderLookup.Provider var1) {
      CompoundTag var2 = new CompoundTag();
      this.saveAdditional(var2, var1);
      return var2;
   }

   private void saveId(CompoundTag var1) {
      ResourceLocation var2 = BlockEntityType.getKey(this.getType());
      if (var2 == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         var1.putString("id", var2.toString());
      }
   }

   public static void addEntityType(CompoundTag var0, BlockEntityType<?> var1) {
      var0.putString("id", BlockEntityType.getKey(var1).toString());
   }

   public void saveToItem(ItemStack var1, HolderLookup.Provider var2) {
      CompoundTag var3 = this.saveWithoutMetadata(var2);
      this.removeComponentsFromTag(var3);
      BlockItem.setBlockEntityData(var1, this.getType(), var3);
      var1.applyComponents(this.collectComponents());
   }

   private void saveMetadata(CompoundTag var1) {
      this.saveId(var1);
      var1.putInt("x", this.worldPosition.getX());
      var1.putInt("y", this.worldPosition.getY());
      var1.putInt("z", this.worldPosition.getZ());
   }

   @Nullable
   public static BlockEntity loadStatic(BlockPos var0, BlockState var1, CompoundTag var2, HolderLookup.Provider var3) {
      String var4 = var2.getString("id");
      ResourceLocation var5 = ResourceLocation.tryParse(var4);
      if (var5 == null) {
         LOGGER.error("Block entity has invalid type: {}", var4);
         return null;
      } else {
         return BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(var5).map(var3x -> {
            try {
               return var3x.create(var0, var1);
            } catch (Throwable var5xx) {
               LOGGER.error("Failed to create block entity {}", var4, var5xx);
               return null;
            }
         }).map(var3x -> {
            try {
               var3x.load(var2, var3);
               return var3x;
            } catch (Throwable var5xx) {
               LOGGER.error("Failed to load data for block entity {}", var4, var5xx);
               return null;
            }
         }).orElseGet(() -> {
            LOGGER.warn("Skipping BlockEntity with id {}", var4);
            return null;
         });
      }
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

   public BlockPos getBlockPos() {
      return this.worldPosition;
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return null;
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return new CompoundTag();
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
      var1.setDetail("Name", () -> BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName());
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

   public void applyComponents(DataComponentMap var1) {
   }

   public void collectComponents(DataComponentMap.Builder var1) {
   }

   @Deprecated
   public void removeComponentsFromTag(CompoundTag var1) {
   }

   public final DataComponentMap collectComponents() {
      DataComponentMap.Builder var1 = DataComponentMap.builder();
      this.collectComponents(var1);
      return var1.build();
   }
}
