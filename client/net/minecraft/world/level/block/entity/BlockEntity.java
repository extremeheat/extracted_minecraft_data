package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

   public static BlockPos getPosFromTag(ChunkPos var0, CompoundTag var1) {
      int var2 = var1.getIntOr("x", 0);
      int var3 = var1.getIntOr("y", 0);
      int var4 = var1.getIntOr("z", 0);
      int var5 = SectionPos.blockToSectionCoord(var2);
      int var6 = SectionPos.blockToSectionCoord(var4);
      if (var5 != var0.x || var6 != var0.z) {
         LOGGER.warn("Block entity {} found in a wrong chunk, expected position from chunk {}", var1, var0);
         var2 = var0.getBlockX(SectionPos.sectionRelative(var2));
         var4 = var0.getBlockZ(SectionPos.sectionRelative(var4));
      }

      return new BlockPos(var2, var3, var4);
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

   protected void loadAdditional(ValueInput var1) {
   }

   public final void loadWithComponents(ValueInput var1) {
      this.loadAdditional(var1);
      this.components = (DataComponentMap)var1.read("components", DataComponentMap.CODEC).orElse(DataComponentMap.EMPTY);
   }

   public final void loadCustomOnly(ValueInput var1) {
      this.loadAdditional(var1);
   }

   protected void saveAdditional(ValueOutput var1) {
   }

   public final CompoundTag saveWithFullMetadata(HolderLookup.Provider var1) {
      try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput var3 = TagValueOutput.createWithContext(var2, var1);
         this.saveWithFullMetadata((ValueOutput)var3);
         return var3.buildResult();
      }
   }

   public void saveWithFullMetadata(ValueOutput var1) {
      this.saveWithoutMetadata(var1);
      this.saveMetadata(var1);
   }

   public void saveWithId(ValueOutput var1) {
      this.saveWithoutMetadata(var1);
      this.saveId(var1);
   }

   public final CompoundTag saveWithoutMetadata(HolderLookup.Provider var1) {
      try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput var3 = TagValueOutput.createWithContext(var2, var1);
         this.saveWithoutMetadata((ValueOutput)var3);
         return var3.buildResult();
      }
   }

   public void saveWithoutMetadata(ValueOutput var1) {
      this.saveAdditional(var1);
      var1.store("components", DataComponentMap.CODEC, this.components);
   }

   public final CompoundTag saveCustomOnly(HolderLookup.Provider var1) {
      try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput var3 = TagValueOutput.createWithContext(var2, var1);
         this.saveCustomOnly((ValueOutput)var3);
         return var3.buildResult();
      }
   }

   public void saveCustomOnly(ValueOutput var1) {
      this.saveAdditional(var1);
   }

   private void saveId(ValueOutput var1) {
      addEntityType(var1, this.getType());
   }

   public static void addEntityType(ValueOutput var0, BlockEntityType<?> var1) {
      var0.store("id", TYPE_CODEC, var1);
   }

   private void saveMetadata(ValueOutput var1) {
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
         } catch (Throwable var12) {
            LOGGER.error("Failed to create block entity {} for block {} at position {} ", new Object[]{var4, var0, var1, var12});
            return null;
         }

         try (ProblemReporter.ScopedCollector var6 = new ProblemReporter.ScopedCollector(var5.problemPath(), LOGGER)) {
            var5.loadWithComponents(TagValueInput.create(var6, var3, var2));
            return var5;
         } catch (Throwable var11) {
            LOGGER.error("Failed to load data for block entity {} for block {} at position {}", new Object[]{var4, var0, var1, var11});
            return null;
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
      BlockState var10002 = this.getBlockState();
      Objects.requireNonNull(var10002);
      var1.setDetail("Cached block", var10002::toString);
      if (this.level == null) {
         var1.setDetail("Block location", (CrashReportDetail)(() -> String.valueOf(this.worldPosition) + " (world missing)"));
      } else {
         var10002 = this.level.getBlockState(this.worldPosition);
         Objects.requireNonNull(var10002);
         var1.setDetail("Actual block", var10002::toString);
         CrashReportCategory.populateBlockLocationDetails(var1, this.level, this.worldPosition);
      }

   }

   public String getNameForReporting() {
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
   public void removeComponentsFromTag(ValueOutput var1) {
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
   public static Component parseCustomNameSafe(ValueInput var0, String var1) {
      return (Component)var0.read(var1, ComponentSerialization.CODEC).orElse((Object)null);
   }

   public ProblemReporter.PathElement problemPath() {
      return new BlockEntityPathElement(this);
   }

   static {
      TYPE_CODEC = BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec();
      LOGGER = LogUtils.getLogger();
   }

   static record BlockEntityPathElement(BlockEntity blockEntity) implements ProblemReporter.PathElement {
      BlockEntityPathElement(BlockEntity var1) {
         super();
         this.blockEntity = var1;
      }

      public String get() {
         String var10000 = this.blockEntity.getNameForReporting();
         return var10000 + "@" + String.valueOf(this.blockEntity.getBlockPos());
      }
   }
}
