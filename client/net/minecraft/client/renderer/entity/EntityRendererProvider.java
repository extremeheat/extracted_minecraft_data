package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface EntityRendererProvider<T extends Entity> {
   EntityRenderer<T, ?> create(Context context);

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
      private final AtlasManager atlasManager;
      private final PlayerSkinRenderCache playerSkinRenderCache;

      public Context(final EntityRenderDispatcher entityRenderDispatcher, final ItemModelResolver itemModelResolver, final MapRenderer mapRenderer, final BlockRenderDispatcher blockRenderDispatcher, final ResourceManager resourceManager, final EntityModelSet modelSet, final EquipmentAssetManager equipmentAssets, final AtlasManager atlasManager, final Font font, final PlayerSkinRenderCache playerSkinRenderCache) {
         super();
         this.entityRenderDispatcher = entityRenderDispatcher;
         this.itemModelResolver = itemModelResolver;
         this.mapRenderer = mapRenderer;
         this.blockRenderDispatcher = blockRenderDispatcher;
         this.resourceManager = resourceManager;
         this.modelSet = modelSet;
         this.equipmentAssets = equipmentAssets;
         this.font = font;
         this.atlasManager = atlasManager;
         this.playerSkinRenderCache = playerSkinRenderCache;
         this.equipmentRenderer = new EquipmentLayerRenderer(equipmentAssets, atlasManager.getAtlasOrThrow(AtlasIds.ARMOR_TRIMS));
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

      public MaterialSet getMaterials() {
         return this.atlasManager;
      }

      public TextureAtlas getAtlas(final Identifier sheet) {
         return this.atlasManager.getAtlasOrThrow(sheet);
      }

      public ModelPart bakeLayer(final ModelLayerLocation id) {
         return this.modelSet.bakeLayer(id);
      }

      public Font getFont() {
         return this.font;
      }

      public PlayerSkinRenderCache getPlayerSkinRenderCache() {
         return this.playerSkinRenderCache;
      }
   }
}
