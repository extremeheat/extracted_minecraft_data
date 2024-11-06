package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface EntityRendererProvider<T extends Entity> {
   EntityRenderer<T, ?> create(Context var1);

   public static class Context {
      private final EntityRenderDispatcher entityRenderDispatcher;
      private final ItemModelResolver itemModelResolver;
      private final MapRenderer mapRenderer;
      private final BlockRenderDispatcher blockRenderDispatcher;
      private final ResourceManager resourceManager;
      private final EntityModelSet modelSet;
      private final EquipmentAssetManager equipmentAssets;
      private final Font font;
      private final EquipmentLayerRenderer equipmentRenderer;

      public Context(EntityRenderDispatcher var1, ItemModelResolver var2, MapRenderer var3, BlockRenderDispatcher var4, ResourceManager var5, EntityModelSet var6, EquipmentAssetManager var7, Font var8) {
         super();
         this.entityRenderDispatcher = var1;
         this.itemModelResolver = var2;
         this.mapRenderer = var3;
         this.blockRenderDispatcher = var4;
         this.resourceManager = var5;
         this.modelSet = var6;
         this.equipmentAssets = var7;
         this.font = var8;
         this.equipmentRenderer = new EquipmentLayerRenderer(var7, this.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET));
      }

      public EntityRenderDispatcher getEntityRenderDispatcher() {
         return this.entityRenderDispatcher;
      }

      public ItemModelResolver getItemModelResolver() {
         return this.itemModelResolver;
      }

      public MapRenderer getMapRenderer() {
         return this.mapRenderer;
      }

      public BlockRenderDispatcher getBlockRenderDispatcher() {
         return this.blockRenderDispatcher;
      }

      public ResourceManager getResourceManager() {
         return this.resourceManager;
      }

      public EntityModelSet getModelSet() {
         return this.modelSet;
      }

      public EquipmentAssetManager getEquipmentAssets() {
         return this.equipmentAssets;
      }

      public EquipmentLayerRenderer getEquipmentRenderer() {
         return this.equipmentRenderer;
      }

      public ModelManager getModelManager() {
         return this.blockRenderDispatcher.getBlockModelShaper().getModelManager();
      }

      public ModelPart bakeLayer(ModelLayerLocation var1) {
         return this.modelSet.bakeLayer(var1);
      }

      public Font getFont() {
         return this.font;
      }
   }
}
