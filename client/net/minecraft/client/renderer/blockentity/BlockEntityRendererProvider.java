package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BlockEntityRendererProvider<T extends BlockEntity> {
   BlockEntityRenderer<T> create(BlockEntityRendererProvider.Context var1);

   public static class Context {
      private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
      private final BlockRenderDispatcher blockRenderDispatcher;
      private final EntityModelSet modelSet;
      private final Font font;

      public Context(BlockEntityRenderDispatcher var1, BlockRenderDispatcher var2, EntityModelSet var3, Font var4) {
         super();
         this.blockEntityRenderDispatcher = var1;
         this.blockRenderDispatcher = var2;
         this.modelSet = var3;
         this.font = var4;
      }

      public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
         return this.blockEntityRenderDispatcher;
      }

      public BlockRenderDispatcher getBlockRenderDispatcher() {
         return this.blockRenderDispatcher;
      }

      public EntityModelSet getModelSet() {
         return this.modelSet;
      }

      public ModelPart getLayer(ModelLayerLocation var1) {
         return this.modelSet.getLayer(var1);
      }

      public Font getFont() {
         return this.font;
      }
   }
}
