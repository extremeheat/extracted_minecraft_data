package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BlockEntityRendererProvider<T extends BlockEntity> {
   BlockEntityRenderer<T> create(Context var1);

   public static class Context {
      private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
      private final BlockRenderDispatcher blockRenderDispatcher;
      private final ItemModelResolver itemModelResolver;
      private final ItemRenderer itemRenderer;
      private final EntityRenderDispatcher entityRenderer;
      private final EntityModelSet modelSet;
      private final Font font;

      public Context(BlockEntityRenderDispatcher var1, BlockRenderDispatcher var2, ItemModelResolver var3, ItemRenderer var4, EntityRenderDispatcher var5, EntityModelSet var6, Font var7) {
         super();
         this.blockEntityRenderDispatcher = var1;
         this.blockRenderDispatcher = var2;
         this.itemModelResolver = var3;
         this.itemRenderer = var4;
         this.entityRenderer = var5;
         this.modelSet = var6;
         this.font = var7;
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

      public ItemModelResolver getItemModelResolver() {
         return this.itemModelResolver;
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
