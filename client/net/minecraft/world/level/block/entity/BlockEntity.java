package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
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
   private DataComponentMap components;

   public BlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super();
      this.components = DataComponentMap.EMPTY;
      this.type = var1;
      this.worldPosition = var2.immutable();
      this.validateBlockState(var3);
      this.blockState = var3;
   }

   private void validateBlockState(BlockState var1) {
      if (!this.isValidBlockState(var1)) {
         String var10002 = this.getNameForReporting();
         throw new IllegalStateException("Invalid block entity " + var10002 + " state at " + String.valueOf(this.worldPosition) + ", got " + String.valueOf(var1));
      }
   }

   public boolean isValidBlockState(BlockState var1) {
      return this.type.isValid(var1);
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

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
   }

   public final void loadWithComponents(CompoundTag var1, HolderLookup.Provider var2) {
      this.loadAdditional(var1, var2);
      BlockEntity.ComponentHelper.COMPONENTS_CODEC.parse(var2.createSerializationContext(NbtOps.INSTANCE), var1).resultOrPartial((var0) -> LOGGER.warn("Failed to load components: {}", var0)).ifPresent((var1x) -> this.components = var1x);
   }

   public final void loadCustomOnly(CompoundTag var1, HolderLookup.Provider var2) {
      this.loadAdditional(var1, var2);
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
      BlockEntity.ComponentHelper.COMPONENTS_CODEC.encodeStart(var1.createSerializationContext(NbtOps.INSTANCE), this.components).resultOrPartial((var0) -> LOGGER.warn("Failed to save components: {}", var0)).ifPresent((var1x) -> var2.merge((CompoundTag)var1x));
      return var2;
   }

   public final CompoundTag saveCustomOnly(HolderLookup.Provider var1) {
      CompoundTag var2 = new CompoundTag();
      this.saveAdditional(var2, var1);
      return var2;
   }

   public final CompoundTag saveCustomAndMetadata(HolderLookup.Provider var1) {
      CompoundTag var2 = this.saveCustomOnly(var1);
      this.saveMetadata(var2);
      return var2;
   }

   private void saveId(CompoundTag var1) {
      ResourceLocation var2 = BlockEntityType.getKey(this.getType());
      if (var2 == null) {
         throw new RuntimeException(String.valueOf(this.getClass()) + " is missing a mapping! This is a bug!");
      } else {
         var1.putString("id", var2.toString());
      }
   }

   public static void addEntityType(CompoundTag var0, BlockEntityType<?> var1) {
      var0.putString("id", BlockEntityType.getKey(var1).toString());
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
         return (BlockEntity)BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(var5).map((var3x) -> {
            try {
               return var3x.create(var0, var1);
            } catch (Throwable var5) {
               LOGGER.error("Failed to create block entity {}", var4, var5);
               return null;
            }
         }).map((var3x) -> {
            try {
               var3x.loadWithComponents(var2, var3);
               return var3x;
            } catch (Throwable var5) {
               LOGGER.error("Failed to load data for block entity {}", var4, var5);
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
      var1.setDetail("Name", this::getNameForReporting);
      if (this.level != null) {
         CrashReportCategory.populateBlockDetails(var1, this.level, this.worldPosition, this.getBlockState());
         CrashReportCategory.populateBlockDetails(var1, this.level, this.worldPosition, this.level.getBlockState(this.worldPosition));
      }
   }

   private String getNameForReporting() {
      String var10000 = String.valueOf(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType()));
      return var10000 + " // " + this.getClass().getCanonicalName();
   }

   public BlockEntityType<?> getType() {
      return this.type;
   }

   /** @deprecated */
   @Deprecated
   public void setBlockState(BlockState var1) {
      this.validateBlockState(var1);
      this.blockState = var1;
   }

   protected void applyImplicitComponents(DataComponentInput var1) {
   }

   public final void applyComponentsFromItemStack(ItemStack var1) {
      this.applyComponents(var1.getPrototype(), var1.getComponentsPatch());
   }

   public final void applyComponents(DataComponentMap var1, DataComponentPatch var2) {
      final HashSet var3 = new HashSet();
      var3.add(DataComponents.BLOCK_ENTITY_DATA);
      var3.add(DataComponents.BLOCK_STATE);
      final PatchedDataComponentMap var4 = PatchedDataComponentMap.fromPatch(var1, var2);
      this.applyImplicitComponents(new DataComponentInput() {
         @Nullable
         public <T> T get(DataComponentType<T> var1) {
            var3.add(var1);
            return (T)var4.get(var1);
         }

         public <T> T getOrDefault(DataComponentType<? extends T> var1, T var2) {
            var3.add(var1);
            return (T)var4.getOrDefault(var1, var2);
         }
      });
      Objects.requireNonNull(var3);
      DataComponentPatch var5 = var2.forget(var3::contains);
      this.components = var5.split().added();
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
   }

   /** @deprecated */
   @Deprecated
   public void removeComponentsFromTag(CompoundTag var1) {
   }

   public final DataComponentMap collectComponents() {
      DataComponentMap.Builder var1 = DataComponentMap.builder();
      var1.addAll(this.components);
      this.collectImplicitComponents(var1);
      return var1.build();
   }

   public DataComponentMap components() {
      return this.components;
   }

   public void setComponents(DataComponentMap var1) {
      this.components = var1;
   }

   @Nullable
   public static Component parseCustomNameSafe(String var0, HolderLookup.Provider var1) {
      try {
         return Component.Serializer.fromJson(var0, var1);
      } catch (Exception var3) {
         LOGGER.warn("Failed to parse custom name from string '{}', discarding", var0, var3);
         return null;
      }
   }

   static class ComponentHelper {
      public static final Codec<DataComponentMap> COMPONENTS_CODEC;

      private ComponentHelper() {
         super();
      }

      static {
         COMPONENTS_CODEC = DataComponentMap.CODEC.optionalFieldOf("components", DataComponentMap.EMPTY).codec();
      }
   }

   protected interface DataComponentInput {
      @Nullable
      <T> T get(DataComponentType<T> var1);

      <T> T getOrDefault(DataComponentType<? extends T> var1, T var2);
   }
}
