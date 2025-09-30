package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BlockEntityRendererProvider<T extends BlockEntity, S extends BlockEntityRenderState> {
   BlockEntityRenderer<T, S> create(Context var1);

   public static record Context(BlockEntityRenderDispatcher blockEntityRenderDispatcher, BlockRenderDispatcher blockRenderDispatcher, ItemModelResolver itemModelResolver, ItemRenderer itemRenderer, EntityRenderDispatcher entityRenderer, EntityModelSet entityModelSet, Font font, MaterialSet materials, PlayerSkinRenderCache playerSkinRenderCache) {
      public Context(BlockEntityRenderDispatcher var1, BlockRenderDispatcher var2, ItemModelResolver var3, ItemRenderer var4, EntityRenderDispatcher var5, EntityModelSet var6, Font var7, MaterialSet var8, PlayerSkinRenderCache var9) {
         super();
         this.blockEntityRenderDispatcher = var1;
         this.blockRenderDispatcher = var2;
         this.itemModelResolver = var3;
         this.itemRenderer = var4;
         this.entityRenderer = var5;
         this.entityModelSet = var6;
         this.font = var7;
         this.materials = var8;
         this.playerSkinRenderCache = var9;
      }

      public ModelPart bakeLayer(ModelLayerLocation var1) {
         return this.entityModelSet.bakeLayer(var1);
      }
   }
}
