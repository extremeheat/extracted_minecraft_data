package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.HashSet;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public abstract class BlockEntity {
   private static final Codec<BlockEntityType<?>> TYPE_CODEC;
   private static final Logger LOGGER;
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
      return new BlockPos(var0.getIntOr("x", 0), var0.getIntOr("y", 0), var0.getIntOr("z", 0));
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
      this.components = (DataComponentMap)var1.read((MapCodec)BlockEntity.ComponentHelper.COMPONENTS_CODEC, var2.createSerializationContext(NbtOps.INSTANCE)).orElse(DataComponentMap.EMPTY);
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
      var2.store((MapCodec)BlockEntity.ComponentHelper.COMPONENTS_CODEC, var1.createSerializationContext(NbtOps.INSTANCE), this.components);
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
      addEntityType(var1, this.getType());
   }

   public static void addEntityType(CompoundTag var0, BlockEntityType<?> var1) {
      var0.store("id", TYPE_CODEC, var1);
   }

   private void saveMetadata(CompoundTag var1) {
      this.saveId(var1);
      var1.putInt("x", this.worldPosition.getX());
      var1.putInt("y", this.worldPosition.getY());
      var1.putInt("z", this.worldPosition.getZ());
   }

   @Nullable
   public static BlockEntity loadStatic(BlockPos var0, BlockState var1, CompoundTag var2, HolderLookup.Provider var3) {
      BlockEntityType var4 = (BlockEntityType)var2.read("id", TYPE_CODEC).orElse((Object)null);
      if (var4 == null) {
         LOGGER.error("Skipping block entity with invalid type: {}", var2.get("id"));
         return null;
      } else {
         BlockEntity var5;
         try {
            var5 = var4.create(var0, var1);
         } catch (Throwable var8) {
            LOGGER.error("Failed to create block entity {}", var4, var8);
            return null;
         }

         if (var5 == null) {
            return null;
         } else {
            try {
               var5.loadWithComponents(var2, var3);
               return var5;
            } catch (Throwable var7) {
               LOGGER.error("Failed to load data for block entity {}", var4, var7);
               return null;
            }
         }
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

   public void preRemoveSideEffects(BlockPos var1, BlockState var2) {
      if (this instanceof Container var3) {
         if (this.level != null) {
            Containers.dropContents(this.level, var1, var3);
         }
      }

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

   protected void applyImplicitComponents(DataComponentGetter var1) {
   }

   public final void applyComponentsFromItemStack(ItemStack var1) {
      this.applyComponents(var1.getPrototype(), var1.getComponentsPatch());
   }

   public final void applyComponents(DataComponentMap var1, DataComponentPatch var2) {
      final HashSet var3 = new HashSet();
      var3.add(DataComponents.BLOCK_ENTITY_DATA);
      var3.add(DataComponents.BLOCK_STATE);
      final PatchedDataComponentMap var4 = PatchedDataComponentMap.fromPatch(var1, var2);
      this.applyImplicitComponents(new DataComponentGetter() {
         @Nullable
         public <T> T get(DataComponentType<? extends T> var1) {
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
   public static Component parseCustomNameSafe(@Nullable Tag var0, HolderLookup.Provider var1) {
      return var0 == null ? null : (Component)ComponentSerialization.CODEC.parse(var1.createSerializationContext(NbtOps.INSTANCE), var0).resultOrPartial((var0x) -> LOGGER.warn("Failed to parse custom name, discarding: {}", var0x)).orElse((Object)null);
   }

   static {
      TYPE_CODEC = BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec();
      LOGGER = LogUtils.getLogger();
   }

   static class ComponentHelper {
      public static final MapCodec<DataComponentMap> COMPONENTS_CODEC;

      private ComponentHelper() {
         super();
      }

      static {
         COMPONENTS_CODEC = DataComponentMap.CODEC.optionalFieldOf("components", DataComponentMap.EMPTY);
      }
   }
}
