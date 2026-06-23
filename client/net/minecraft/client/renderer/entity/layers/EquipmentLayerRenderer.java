package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.resources.palette.PalettedTextureManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jspecify.annotations.Nullable;

public class EquipmentLayerRenderer {
   private static final int NO_LAYER_COLOR = 0;
   private final EquipmentAssetManager equipmentAssets;
   private final Function<LayerTextureKey, Identifier> layerTextureLookup;
   private final Function<TrimTextureKey, PalettedTextureManager.Handle> trimTextureLookup;

   public EquipmentLayerRenderer(final EquipmentAssetManager equipmentAssets, final PalettedTextureManager palettedTextures) {
      super();
      this.equipmentAssets = equipmentAssets;
      this.layerTextureLookup = Util.memoize((Function)((key) -> key.layer.getTextureLocation(key.layerType)));
      this.trimTextureLookup = Util.memoize((Function)((key) -> palettedTextures.getOrPrepare(key.baseTexture(), key.paletteId())));
   }

   public <S> void renderLayers(final EquipmentClientInfo.LayerType layerType, final ResourceKey<EquipmentAsset> equipmentAssetId, final Model<? super S> model, final S state, final ItemStack itemStack, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int outlineColor) {
      this.renderLayers(layerType, equipmentAssetId, model, state, itemStack, poseStack, submitNodeCollector, lightCoords, (Identifier)null, outlineColor, 1);
   }

   public <S> void renderLayers(final EquipmentClientInfo.LayerType layerType, final ResourceKey<EquipmentAsset> equipmentAssetId, final Model<? super S> model, final S state, final ItemStack itemStack, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final @Nullable Identifier playerTextureOverride, final int outlineColor, final int order) {
      EquipmentClientInfo equipmentInfo = this.equipmentAssets.get(equipmentAssetId);
      List<EquipmentClientInfo.Layer> layers = equipmentInfo.getLayers(layerType);
      if (!layers.isEmpty()) {
         int dyeColor = DyedItemColor.getOrDefault(itemStack, 0);
         boolean renderFoil = itemStack.hasFoil();
         int nextOrder = order;

         for(EquipmentClientInfo.Layer layer : layers) {
            int color = getColorForLayer(layer, dyeColor);
            if (color != 0) {
               Identifier layerTexture = layer.usePlayerTexture() && playerTextureOverride != null ? playerTextureOverride : (Identifier)this.layerTextureLookup.apply(new LayerTextureKey(layerType, layer));
               submitNodeCollector.order(nextOrder++).submitModel(model, state, poseStack, RenderTypes.armorCutoutNoCull(layerTexture), lightCoords, OverlayTexture.NO_OVERLAY, color, (UvMapping)null, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
               if (renderFoil) {
                  submitNodeCollector.order(nextOrder++).submitModel(model, state, poseStack, RenderTypes.armorEntityGlint(), lightCoords, OverlayTexture.NO_OVERLAY, color, (UvMapping)null, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
               }

               renderFoil = false;
            }
         }

         ArmorTrim trim = (ArmorTrim)itemStack.get(DataComponents.TRIM);
         if (trim != null && layerType != EquipmentClientInfo.LayerType.HUMANOID_BABY) {
            PalettedTextureManager.Handle textureHandle = (PalettedTextureManager.Handle)this.trimTextureLookup.apply(new TrimTextureKey(trim, layerType, equipmentInfo));
            RenderType renderType = RenderTypes.armorTrim(textureHandle.textureLocation(), ((TrimPattern)trim.pattern().value()).decal());
            submitNodeCollector.order(nextOrder++).submitModel(model, state, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, -1, textureHandle, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
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

   private static record TrimTextureKey(ArmorTrim trim, EquipmentClientInfo.LayerType layerType, EquipmentClientInfo equipmentInfo) {
      private TrimTextureKey {
         super();
      }

      public Identifier baseTexture() {
         return ((TrimPattern)this.trim.pattern().value()).assetId().withPath((UnaryOperator)((path) -> {
            String var10000 = this.layerType.trimAssetPrefix();
            return var10000 + "/" + path;
         }));
      }

      public Identifier paletteId() {
         Identifier paletteId = ((TrimMaterial)this.trim.material().value()).paletteId();
         return (Identifier)this.equipmentInfo.trimPaletteReplacements().getOrDefault(paletteId, paletteId);
      }
   }
}
