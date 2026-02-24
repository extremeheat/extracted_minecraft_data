package net.minecraft.client.renderer.block;

import java.util.Arrays;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.BlockStateDefinitions;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class BlockModelResolver {
   private final ModelManager modelManager;
   private final BlockColors blockColors;

   public BlockModelResolver(final ModelManager modelManager, final BlockColors blockColors) {
      super();
      this.modelManager = modelManager;
      this.blockColors = blockColors;
   }

   public void update(final BlockModelRenderState renderState, final BlockState blockState) {
      renderState.block = blockState.getBlock();
      renderState.specialRenderer = this.modelManager.specialBlockModelRenderer().getSpecialRenderer(blockState.getBlock());
      if (blockState.getRenderShape() == RenderShape.MODEL) {
         renderState.model = this.modelManager.getBlockModelSet().get(blockState);
         this.updateTints(renderState, blockState);
      } else {
         renderState.model = null;
         Arrays.fill(renderState.tintLayers, -1);
      }

   }

   private void updateTints(final BlockModelRenderState renderState, final BlockState blockState) {
      for(int tintIndex = 0; tintIndex <= 2; ++tintIndex) {
         renderState.tintLayers[tintIndex] = this.blockColors.getColor(blockState, (BlockAndTintGetter)null, (BlockPos)null, tintIndex);
      }

   }

   public void updateForItemFrame(final BlockModelRenderState renderState, final boolean isGlowing, final boolean map) {
      BlockState fakeState = BlockStateDefinitions.getItemFrameFakeState(isGlowing, map);
      renderState.block = fakeState.getBlock();
      renderState.model = this.modelManager.getBlockModelSet().get(fakeState);
      renderState.specialRenderer = null;
      Arrays.fill(renderState.tintLayers, -1);
   }
}
