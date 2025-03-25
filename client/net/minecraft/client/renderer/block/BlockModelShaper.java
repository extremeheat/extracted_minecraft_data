package net.minecraft.client.renderer.block;

import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

public class BlockModelShaper {
   private Map<BlockState, BlockStateModel> modelByStateCache = Map.of();
   private final ModelManager modelManager;

   public BlockModelShaper(ModelManager var1) {
      super();
      this.modelManager = var1;
   }

   public TextureAtlasSprite getParticleIcon(BlockState var1) {
      return this.getBlockModel(var1).particleIcon();
   }

   public BlockStateModel getBlockModel(BlockState var1) {
      BlockStateModel var2 = (BlockStateModel)this.modelByStateCache.get(var1);
      if (var2 == null) {
         var2 = this.modelManager.getMissingBlockStateModel();
      }

      return var2;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void replaceCache(Map<BlockState, BlockStateModel> var1) {
      this.modelByStateCache = var1;
   }
}
