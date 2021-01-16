package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BasaltDeltasSurfaceBuilder extends NetherCappedSurfaceBuilder {
   private static final BlockState BASALT;
   private static final BlockState BLACKSTONE;
   private static final BlockState GRAVEL;
   private static final ImmutableList<BlockState> FLOOR_BLOCK_STATES;
   private static final ImmutableList<BlockState> CEILING_BLOCK_STATES;

   public BasaltDeltasSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> var1) {
      super(var1);
   }

   protected ImmutableList<BlockState> getFloorBlockStates() {
      return FLOOR_BLOCK_STATES;
   }

   protected ImmutableList<BlockState> getCeilingBlockStates() {
      return CEILING_BLOCK_STATES;
   }

   protected BlockState getPatchBlockState() {
      return GRAVEL;
   }

   static {
      BASALT = Blocks.BASALT.defaultBlockState();
      BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
      GRAVEL = Blocks.GRAVEL.defaultBlockState();
      FLOOR_BLOCK_STATES = ImmutableList.of(BASALT, BLACKSTONE);
      CEILING_BLOCK_STATES = ImmutableList.of(BASALT);
   }
}
