package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.TypedInstance;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.debug.DebugValueSource;
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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class BlockEntity implements DebugValueSource, TypedInstance<BlockEntityType<?>> {
   private static final Codec<BlockEntityType<?>> TYPE_CODEC;
   private static final Logger LOGGER;
   private final BlockEntityType<?> type;
   protected @Nullable Level level;
   protected final BlockPos worldPosition;
   protected boolean remove;
   private BlockState blockState;
   private DataComponentMap components;

   public BlockEntity(final BlockEntityType<?> type, final BlockPos worldPosition, final BlockState blockState) {
      super();
      this.components = DataComponentMap.EMPTY;
      this.type = type;
      this.worldPosition = worldPosition.immutable();
      this.validateBlockState(blockState);
      this.blockState = blockState;
   }

   private void validateBlockState(final BlockState blockState) {
      if (!this.isValidBlockState(blockState)) {
         String var10002 = this.getNameForReporting();
         throw new IllegalStateException("Invalid block entity " + var10002 + " state at " + String.valueOf(this.worldPosition) + ", got " + String.valueOf(blockState));
      }
   }

   public boolean isValidBlockState(final BlockState blockState) {
      return this.type.isValid(blockState);
   }

   public static BlockPos getPosFromTag(final ChunkPos base, final CompoundTag entityTag) {
      int x = entityTag.getIntOr("x", 0);
      int y = entityTag.getIntOr("y", 0);
      int z = entityTag.getIntOr("z", 0);
      int sectionX = SectionPos.blockToSectionCoord(x);
      int sectionZ = SectionPos.blockToSectionCoord(z);
      if (sectionX != base.x() || sectionZ != base.z()) {
         LOGGER.warn("Block entity {} found in a wrong chunk, expected position from chunk {}", entityTag, base);
         x = base.getBlockX(SectionPos.sectionRelative(x));
         z = base.getBlockZ(SectionPos.sectionRelative(z));
      }

      return new BlockPos(x, y, z);
   }

   public @Nullable Level getLevel() {
      return this.level;
   }

   public void setLevel(final Level level) {
      this.level = level;
   }

   public boolean hasLevel() {
      return this.level != null;
   }

   protected void loadAdditional(final ValueInput input) {
   }

   public final void loadWithComponents(final ValueInput input) {
      this.loadAdditional(input);
      this.components = (DataComponentMap)input.read("components", DataComponentMap.CODEC).orElse(DataComponentMap.EMPTY);
   }

   public final void loadCustomOnly(final ValueInput input) {
      this.loadAdditional(input);
   }

   protected void saveAdditional(final ValueOutput output) {
   }

   public final CompoundTag saveWithFullMetadata(final HolderLookup.Provider registries) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
         this.saveWithFullMetadata((ValueOutput)output);
         return output.buildResult();
      }
   }

   public void saveWithFullMetadata(final ValueOutput output) {
      this.saveWithoutMetadata(output);
      this.saveMetadata(output);
   }

   public void saveWithId(final ValueOutput output) {
      this.saveWithoutMetadata(output);
      this.saveId(output);
   }

   public final CompoundTag saveWithoutMetadata(final HolderLookup.Provider registries) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
         this.saveWithoutMetadata((ValueOutput)output);
         return output.buildResult();
      }
   }

   public void saveWithoutMetadata(final ValueOutput output) {
      this.saveAdditional(output);
      output.store("components", DataComponentMap.CODEC, this.components);
   }

   public final CompoundTag saveCustomOnly(final HolderLookup.Provider registries) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
         this.saveCustomOnly((ValueOutput)output);
         return output.buildResult();
      }
   }

   public void saveCustomOnly(final ValueOutput output) {
      this.saveAdditional(output);
   }

   private void saveId(final ValueOutput output) {
      addEntityType(output, this.getType());
   }

   public static void addEntityType(final ValueOutput output, final BlockEntityType<?> type) {
      output.store("id", TYPE_CODEC, type);
   }

   private void saveMetadata(final ValueOutput output) {
      this.saveId(output);
      output.putInt("x", this.worldPosition.getX());
      output.putInt("y", this.worldPosition.getY());
      output.putInt("z", this.worldPosition.getZ());
   }

   public static @Nullable BlockEntity loadStatic(final BlockPos pos, final BlockState state, final CompoundTag tag, final HolderLookup.Provider registries) {
      BlockEntityType<?> type = (BlockEntityType)tag.read("id", TYPE_CODEC).orElse((Object)null);
      if (type == null) {
         LOGGER.error("Skipping block entity with invalid type: {}", tag.get("id"));
         return null;
      } else {
         BlockEntity entity;
         try {
            entity = type.create(pos, state);
         } catch (Throwable t) {
            LOGGER.error("Failed to create block entity {} for block {} at position {} ", new Object[]{type, pos, state, t});
            return null;
         }

         try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
            entity.loadWithComponents(TagValueInput.create(reporter, registries, tag));
            return entity;
         } catch (Throwable t) {
            LOGGER.error("Failed to load data for block entity {} for block {} at position {}", new Object[]{type, pos, state, t});
            return null;
         }
      }
   }

   public void setChanged() {
      if (this.level != null) {
         setChanged(this.level, this.worldPosition, this.blockState);
      }

   }

   protected static void setChanged(final Level level, final BlockPos worldPosition, final BlockState blockState) {
      level.blockEntityChanged(worldPosition);
      if (!blockState.isAir()) {
         level.updateNeighbourForOutputSignal(worldPosition, blockState.getBlock());
      }

   }

   public BlockPos getBlockPos() {
      return this.worldPosition;
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
      return null;
   }

   public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
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

   public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
      if (this instanceof Container container) {
         if (this.level != null) {
            Containers.dropContents(this.level, pos, container);
         }
      }

   }

   public boolean triggerEvent(final int b0, final int b1) {
      return false;
   }

   public void fillCrashReportCategory(final CrashReportCategory category) {
      category.setDetail("Name", this::getNameForReporting);
      BlockState var10002 = this.getBlockState();
      Objects.requireNonNull(var10002);
      category.setDetail("Cached block", var10002::toString);
      if (this.level == null) {
         category.setDetail("Block location", (CrashReportDetail)(() -> String.valueOf(this.worldPosition) + " (world missing)"));
      } else {
         var10002 = this.level.getBlockState(this.worldPosition);
         Objects.requireNonNull(var10002);
         category.setDetail("Actual block", var10002::toString);
         CrashReportCategory.populateBlockLocationDetails(category, this.level, this.worldPosition);
      }

   }

   public String getNameForReporting() {
      String var10000 = this.typeHolder().getRegisteredName();
      return var10000 + " // " + this.getClass().getCanonicalName();
   }

   public BlockEntityType<?> getType() {
      return this.type;
   }

   public Holder<BlockEntityType<?>> typeHolder() {
      return this.type.builtInRegistryHolder();
   }

   /** @deprecated */
   @Deprecated
   public void setBlockState(final BlockState blockState) {
      this.validateBlockState(blockState);
      this.blockState = blockState;
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
   }

   public final void applyComponentsFromItemStack(final ItemStack stack) {
      this.applyComponents(stack.getPrototype(), stack.getComponentsPatch());
   }

   public final void applyComponents(final DataComponentMap prototype, final DataComponentPatch patch) {
      final Set<DataComponentType<?>> implicitComponents = new HashSet();
      implicitComponents.add(DataComponents.BLOCK_ENTITY_DATA);
      implicitComponents.add(DataComponents.BLOCK_STATE);
      final DataComponentMap fullView = PatchedDataComponentMap.fromPatch(prototype, patch);
      this.applyImplicitComponents(new DataComponentGetter() {
         {
            Objects.requireNonNull(BlockEntity.this);
         }

         public <T> @Nullable T get(final DataComponentType<? extends T> type) {
            implicitComponents.add(type);
            return (T)fullView.get(type);
         }

         public <T> T getOrDefault(final DataComponentType<? extends T> type, final T defaultValue) {
            implicitComponents.add(type);
            return (T)fullView.getOrDefault(type, defaultValue);
         }
      });
      Objects.requireNonNull(implicitComponents);
      DataComponentPatch newPatch = patch.forget(implicitComponents::contains);
      this.components = newPatch.split().added();
   }

   protected void collectImplicitComponents(final DataComponentMap.Builder components) {
   }

   /** @deprecated */
   @Deprecated
   public void removeComponentsFromTag(final ValueOutput output) {
   }

   public final DataComponentMap collectComponents() {
      DataComponentMap.Builder result = DataComponentMap.builder();
      result.addAll(this.components);
      this.collectImplicitComponents(result);
      return result.build();
   }

   public DataComponentMap components() {
      return this.components;
   }

   public void setComponents(final DataComponentMap components) {
      this.components = components;
   }

   public static @Nullable Component parseCustomNameSafe(final ValueInput input, final String name) {
      return (Component)input.read(name, ComponentSerialization.CODEC).orElse((Object)null);
   }

   public ProblemReporter.PathElement problemPath() {
      return new BlockEntityPathElement(this);
   }

   public void registerDebugValues(final ServerLevel level, final DebugValueSource.Registration registration) {
   }

   static {
      TYPE_CODEC = BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec();
      LOGGER = LogUtils.getLogger();
   }

   private static record BlockEntityPathElement(BlockEntity blockEntity) implements ProblemReporter.PathElement {
      private BlockEntityPathElement {
         super();
      }

      public String get() {
         String var10000 = this.blockEntity.getNameForReporting();
         return var10000 + "@" + String.valueOf(this.blockEntity.getBlockPos());
      }
   }
}
