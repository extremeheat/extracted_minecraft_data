package net.minecraft.client.renderer.block;

import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

public class BlockModelShaper {
   private Map<BlockState, BlockStateModel> modelByStateCache = Map.of();
   private final ModelManager modelManager;

   public BlockModelShaper(final ModelManager modelManager) {
      super();
      this.modelManager = modelManager;
   }

   public Material.Baked getParticleMaterial(final BlockState blockState) {
      return this.getBlockModel(blockState).particleMaterial();
   }

   public BlockStateModel getBlockModel(final BlockState state) {
      BlockStateModel model = (BlockStateModel)this.modelByStateCache.get(state);
      if (model == null) {
         model = this.modelManager.getMissingBlockStateModel();
      }

      return model;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void replaceCache(final Map<BlockState, BlockStateModel> modelByStateCache) {
      this.modelByStateCache = modelByStateCache;
   }
}
