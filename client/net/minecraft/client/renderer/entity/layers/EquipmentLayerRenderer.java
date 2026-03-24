package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jspecify.annotations.Nullable;

public class EquipmentLayerRenderer {
   private static final int NO_LAYER_COLOR = 0;
   private final EquipmentAssetManager equipmentAssets;
   private final Function<LayerTextureKey, Identifier> layerTextureLookup;
   private final Function<TrimSpriteKey, TextureAtlasSprite> trimSpriteLookup;

   public EquipmentLayerRenderer(final EquipmentAssetManager equipmentAssets, final TextureAtlas armorTrimAtlas) {
      super();
      this.equipmentAssets = equipmentAssets;
      this.layerTextureLookup = Util.memoize((Function)((key) -> key.layer.getTextureLocation(key.layerType)));
      this.trimSpriteLookup = Util.memoize((Function)((key) -> armorTrimAtlas.getSprite(key.spriteId())));
   }

   public <S> void renderLayers(final EquipmentClientInfo.LayerType layerType, final ResourceKey<EquipmentAsset> equipmentAssetId, final Model<? super S> model, final S state, final ItemStack itemStack, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int outlineColor) {
      this.renderLayers(layerType, equipmentAssetId, model, state, itemStack, poseStack, submitNodeCollector, lightCoords, (Identifier)null, outlineColor, 1);
   }

   public <S> void renderLayers(final EquipmentClientInfo.LayerType layerType, final ResourceKey<EquipmentAsset> equipmentAssetId, final Model<? super S> model, final S state, final ItemStack itemStack, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final @Nullable Identifier playerTextureOverride, final int outlineColor, final int order) {
      List<EquipmentClientInfo.Layer> layers = this.equipmentAssets.get(equipmentAssetId).getLayers(layerType);
      if (!layers.isEmpty()) {
         int dyeColor = DyedItemColor.getOrDefault(itemStack, 0);
         boolean renderFoil = itemStack.hasFoil();
         int nextOrder = order;

         for(EquipmentClientInfo.Layer layer : layers) {
            int color = getColorForLayer(layer, dyeColor);
            if (color != 0) {
               Identifier layerTexture = layer.usePlayerTexture() && playerTextureOverride != null ? playerTextureOverride : (Identifier)this.layerTextureLookup.apply(new LayerTextureKey(layerType, layer));
               submitNodeCollector.order(nextOrder++).submitModel(model, state, poseStack, RenderTypes.armorCutoutNoCull(layerTexture), lightCoords, OverlayTexture.NO_OVERLAY, color, (TextureAtlasSprite)null, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
               if (renderFoil) {
                  submitNodeCollector.order(nextOrder++).submitModel(model, state, poseStack, RenderTypes.armorEntityGlint(), lightCoords, OverlayTexture.NO_OVERLAY, color, (TextureAtlasSprite)null, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
               }

               renderFoil = false;
            }
         }

         ArmorTrim trim = (ArmorTrim)itemStack.get(DataComponents.TRIM);
         if (trim != null && layerType != EquipmentClientInfo.LayerType.HUMANOID_BABY) {
            TextureAtlasSprite sprite = (TextureAtlasSprite)this.trimSpriteLookup.apply(new TrimSpriteKey(trim, layerType, equipmentAssetId));
            RenderType renderType = Sheets.armorTrimsSheet(((TrimPattern)trim.pattern().value()).decal());
            submitNodeCollector.order(nextOrder++).submitModel(model, state, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, -1, sprite, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
         }

      }
   }

   private static int getColorForLayer(final EquipmentClientInfo.Layer layer, final int dyeColor) {
      Optional<EquipmentClientInfo.Dyeable> dyeable = layer.dyeable();
      if (dyeable.isPresent()) {
         int colorWhenUndyed = (Integer)((EquipmentClientInfo.Dyeable)dyeable.get()).colorWhenUndyed().map(ARGB::opaque).orElse(0);
         return dyeColor != 0 ? dyeColor : colorWhenUndyed;
      } else {
         return -1;
      }
   }

   private static record LayerTextureKey(EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer) {
      private LayerTextureKey {
         super();
      }
   }

   private static record TrimSpriteKey(ArmorTrim trim, EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> equipmentAssetId) {
      private TrimSpriteKey {
         super();
      }

      public Identifier spriteId() {
         return this.trim.layerAssetId(this.layerType.trimAssetPrefix(), this.equipmentAssetId);
      }
   }
}
