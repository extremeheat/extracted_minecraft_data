package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.TypedInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

public abstract class DebugEntryLookingAt implements DebugScreenEntry {
   private static final int RANGE = 20;
   private static final Identifier BLOCK_GROUP = Identifier.withDefaultNamespace("looking_at_block");
   private static final Identifier FLUID_GROUP = Identifier.withDefaultNamespace("looking_at_fluid");

   public DebugEntryLookingAt() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
      Level clientOrServerLevel = (Level)(SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES ? serverOrClientLevel : Minecraft.getInstance().level);
      if (cameraEntity != null && clientOrServerLevel != null) {
         HitResult block = this.getHitResult(cameraEntity);
         List<String> result = new ArrayList();
         if (block.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult)block).getBlockPos();
            this.extractInfo(result, clientOrServerLevel, pos);
         }

         displayer.addToGroup(this.group(), result);
      }
   }

   public abstract HitResult getHitResult(final Entity cameraEntity);

   public abstract void extractInfo(List<String> result, Level level, BlockPos pos);

   public abstract Identifier group();

   public static void addTagEntries(final List<String> result, final TypedInstance<?> instance) {
      Stream var10000 = instance.tags().map((e) -> "#" + String.valueOf(e.location()));
      Objects.requireNonNull(result);
      var10000.forEach(result::add);
   }

   public abstract static class DebugEntryLookingAtState<OwnerType, StateType extends StateHolder<OwnerType, StateType> & TypedInstance<OwnerType>> extends DebugEntryLookingAt {
      private final String prefix;

      protected DebugEntryLookingAtState(final String prefix) {
         super();
         this.prefix = prefix;
      }

      protected abstract StateType getInstance(Level level, BlockPos pos);

      public void extractInfo(final List<String> result, final Level level, final BlockPos pos) {
         StateType stateInstance = this.getInstance(level, pos);
         String var10001 = String.valueOf(ChatFormatting.UNDERLINE);
         result.add(var10001 + this.prefix + ": " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
         result.add(((TypedInstance)stateInstance).typeHolder().getRegisteredName());
         addStateProperties(result, stateInstance);
      }

      private static void addStateProperties(final List<String> result, final StateHolder<?, ?> stateHolder) {
         for(Map.Entry<Property<?>, Comparable<?>> entry : stateHolder.getValues().entrySet()) {
            result.add(getPropertyValueString(entry));
         }

      }

      private static String getPropertyValueString(final Map.Entry<Property<?>, Comparable<?>> entry) {
         Property<?> property = (Property)entry.getKey();
         Comparable<?> value = (Comparable)entry.getValue();
         String valueString = Util.getPropertyName(property, value);
         if (Boolean.TRUE.equals(value)) {
            String var10000 = String.valueOf(ChatFormatting.GREEN);
            valueString = var10000 + valueString;
         } else if (Boolean.FALSE.equals(value)) {
            String var4 = String.valueOf(ChatFormatting.RED);
            valueString = var4 + valueString;
         }

         String var5 = property.getName();
         return var5 + ": " + valueString;
      }
   }

   public abstract static class DebugEntryLookingAtTags<T extends TypedInstance<?>> extends DebugEntryLookingAt {
      public DebugEntryLookingAtTags() {
         super();
      }

      protected abstract T getInstance(Level level, BlockPos pos);

      public void extractInfo(final List<String> result, final Level level, final BlockPos pos) {
         T instance = this.getInstance(level, pos);
         addTagEntries(result, instance);
      }
   }

   public static class BlockStateInfo extends DebugEntryLookingAtState<Block, BlockState> {
      protected BlockStateInfo() {
         super("Targeted Block");
      }

      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0, 0.0F, false);
      }

      public BlockState getInstance(final Level level, final BlockPos pos) {
         return level.getBlockState(pos);
      }

      public Identifier group() {
         return DebugEntryLookingAt.BLOCK_GROUP;
      }
   }

   public static class BlockTagInfo extends DebugEntryLookingAtTags<BlockState> {
      public BlockTagInfo() {
         super();
      }

      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0, 0.0F, false);
      }

      public BlockState getInstance(final Level level, final BlockPos pos) {
         return level.getBlockState(pos);
      }

      public Identifier group() {
         return DebugEntryLookingAt.BLOCK_GROUP;
      }
   }

   public static class FluidStateInfo extends DebugEntryLookingAtState<Fluid, FluidState> {
      protected FluidStateInfo() {
         super("Targeted Fluid");
      }

      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0, 0.0F, true);
      }

      public FluidState getInstance(final Level level, final BlockPos pos) {
         return level.getFluidState(pos);
      }

      public Identifier group() {
         return DebugEntryLookingAt.FLUID_GROUP;
      }
   }

   public static class FluidTagInfo extends DebugEntryLookingAtTags<FluidState> {
      public FluidTagInfo() {
         super();
      }

      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0, 0.0F, true);
      }

      public FluidState getInstance(final Level level, final BlockPos pos) {
         return level.getFluidState(pos);
      }

      public Identifier group() {
         return DebugEntryLookingAt.FLUID_GROUP;
      }
   }
}
