package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BlockEntityRendererProvider<T extends BlockEntity> {
   BlockEntityRenderer<T> create(Context var1);

   public static class Context {
      private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
      private final BlockRenderDispatcher blockRenderDispatcher;
      private final ItemRenderer itemRenderer;
      private final EntityRenderDispatcher entityRenderer;
      private final EntityModelSet modelSet;
      private final Font font;

      public Context(BlockEntityRenderDispatcher var1, BlockRenderDispatcher var2, ItemRenderer var3, EntityRenderDispatcher var4, EntityModelSet var5, Font var6) {
         super();
         this.blockEntityRenderDispatcher = var1;
         this.blockRenderDispatcher = var2;
         this.itemRenderer = var3;
         this.entityRenderer = var4;
         this.modelSet = var5;
         this.font = var6;
      }

      public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
         return this.blockEntityRenderDispatcher;
      }

      public BlockRenderDispatcher getBlockRenderDispatcher() {
         return this.blockRenderDispatcher;
      }

      public EntityRenderDispatcher getEntityRenderer() {
         return this.entityRenderer;
      }

      public ItemRenderer getItemRenderer() {
         return this.itemRenderer;
      }

      public EntityModelSet getModelSet() {
         return this.modelSet;
      }

      public ModelPart bakeLayer(ModelLayerLocation var1) {
         return this.modelSet.bakeLayer(var1);
      }

      public Font getFont() {
         return this.font;
      }
   }
}
