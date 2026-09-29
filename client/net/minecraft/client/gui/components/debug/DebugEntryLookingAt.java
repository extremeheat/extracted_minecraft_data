package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.TypedInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

public abstract class DebugEntryLookingAt implements DebugScreenEntry {
   private static final int RANGE = 20;

   public DebugEntryLookingAt() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
      Level clientOrServerLevel = (Level)(SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES ? serverOrClientLevel : Minecraft.getInstance().level);
      if (cameraEntity != null && clientOrServerLevel != null) {
         HitResult block = this.getHitResult(cameraEntity);
         List<String> tags = new ArrayList();
         if (block.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult)block).getBlockPos();
            this.extractInfo(displayer, tags, clientOrServerLevel, pos);
         }

         if (!tags.isEmpty()) {
            displayer.addToGroup(this.group(), tags);
         }

      }
   }

   public abstract HitResult getHitResult(final Entity cameraEntity);

   public abstract void extractInfo(final DebugScreenDisplayer displayer, List<String> result, Level level, BlockPos pos);

   public abstract DebugGroup group();

   public static void addTagEntries(final List<String> result, final TypedInstance<?> instance) {
      Stream var10000 = instance.tags().map((e) -> "#" + String.valueOf(e.location()));
      Objects.requireNonNull(result);
      var10000.forEach(result::add);
   }

   public abstract static class DebugEntryLookingAtState<OwnerType, StateType extends StateHolder<OwnerType, StateType> & TypedInstance<OwnerType>> extends DebugEntryLookingAt {
      protected DebugEntryLookingAtState() {
         super();
      }

      protected abstract StateType getInstance(Level level, BlockPos pos);

      public void extractInfo(final DebugScreenDisplayer displayer, final List<String> result, final Level level, final BlockPos pos) {
         StateType stateInstance = this.getInstance(level, pos);
         displayer.addFactToGroup(this.group(), "Coordinates", (fact) -> fact.value(pos.getX()).text(", ").value(pos.getY()).text(", ").value(pos.getZ()));
         displayer.addFactToGroup(this.group(), "Type", (fact) -> fact.value(((TypedInstance)stateInstance).typeHolder().getRegisteredName()));
         this.addStateProperties(displayer, stateInstance);
      }

      private void addStateProperties(final DebugScreenDisplayer displayer, final StateHolder<?, ?> stateHolder) {
         stateHolder.getValues().forEach((entry) -> displayer.addFactToGroup(this.group(), entry.property().getName(), (fact) -> {
               if (Boolean.TRUE.equals(entry.value())) {
                  fact.text((Component)Component.literal("true").withColor(-16711936));
               } else if (Boolean.FALSE.equals(entry.value())) {
                  fact.text((Component)Component.literal("false").withColor(-65536));
               } else {
                  fact.value(entry.valueName());
               }

            }));
      }
   }

   public abstract static class DebugEntryLookingAtTags<T extends TypedInstance<?>> extends DebugEntryLookingAt {
      public DebugEntryLookingAtTags() {
         super();
      }

      protected abstract T getInstance(Level level, BlockPos pos);

      public void extractInfo(final DebugScreenDisplayer displayer, final List<String> tags, final Level level, final BlockPos pos) {
         T instance = this.getInstance(level, pos);
         addTagEntries(tags, instance);
      }
   }

   public static class BlockStateInfo extends DebugEntryLookingAtState<Block, BlockState> {
      protected BlockStateInfo() {
         super();
      }

      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0, 0.0F, false);
      }

      public BlockState getInstance(final Level level, final BlockPos pos) {
         return level.getBlockState(pos);
      }

      public DebugGroup group() {
         return DebugGroups.LOOKING_AT_BLOCK;
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

      public DebugGroup group() {
         return DebugGroups.LOOKING_AT_BLOCK;
      }
   }

   public static class FluidStateInfo extends DebugEntryLookingAtState<Fluid, FluidState> {
      protected FluidStateInfo() {
         super();
      }

      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0, 0.0F, true);
      }

      public FluidState getInstance(final Level level, final BlockPos pos) {
         return level.getFluidState(pos);
      }

      public DebugGroup group() {
         return DebugGroups.LOOKING_AT_FLUID;
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

      public DebugGroup group() {
         return DebugGroups.LOOKING_AT_FLUID;
      }
   }
}
