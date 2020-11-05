package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SoulSandValleySurfaceBuilder extends NetherCappedSurfaceBuilder {
   private static final BlockState SOUL_SAND;
   private static final BlockState SOUL_SOIL;
   private static final BlockState GRAVEL;
   private static final ImmutableList<BlockState> BLOCK_STATES;

   public SoulSandValleySurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> var1) {
      super(var1);
   }

   protected ImmutableList<BlockState> getFloorBlockStates() {
      return BLOCK_STATES;
   }

   protected ImmutableList<BlockState> getCeilingBlockStates() {
      return BLOCK_STATES;
   }

   protected BlockState getPatchBlockState() {
      return GRAVEL;
   }

   static {
      SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
      SOUL_SOIL = Blocks.SOUL_SOIL.defaultBlockState();
      GRAVEL = Blocks.GRAVEL.defaultBlockState();
      BLOCK_STATES = ImmutableList.of(SOUL_SAND, SOUL_SOIL);
   }
}
