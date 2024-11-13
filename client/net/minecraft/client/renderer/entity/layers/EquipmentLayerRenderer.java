package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

public class EquipmentLayerRenderer {
   private static final int NO_LAYER_COLOR = 0;
   private final EquipmentAssetManager equipmentAssets;
   private final Function<LayerTextureKey, ResourceLocation> layerTextureLookup;
   private final Function<TrimSpriteKey, TextureAtlasSprite> trimSpriteLookup;

   public EquipmentLayerRenderer(EquipmentAssetManager var1, TextureAtlas var2) {
      super();
      this.equipmentAssets = var1;
      this.layerTextureLookup = Util.memoize((Function)((var0) -> var0.layer.getTextureLocation(var0.layerType)));
      this.trimSpriteLookup = Util.memoize((Function)((var1x) -> var2.getSprite(var1x.textureId())));
   }

   public void renderLayers(EquipmentClientInfo.LayerType var1, ResourceKey<EquipmentAsset> var2, Model var3, ItemStack var4, PoseStack var5, MultiBufferSource var6, int var7) {
      this.renderLayers(var1, var2, var3, var4, var5, var6, var7, (ResourceLocation)null);
   }

   public void renderLayers(EquipmentClientInfo.LayerType var1, ResourceKey<EquipmentAsset> var2, Model var3, ItemStack var4, PoseStack var5, MultiBufferSource var6, int var7, @Nullable ResourceLocation var8) {
      List var9 = this.equipmentAssets.get(var2).getLayers(var1);
      if (!var9.isEmpty()) {
         int var10 = var4.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(var4, 0) : 0;
         boolean var11 = var4.hasFoil();

         for(EquipmentClientInfo.Layer var13 : var9) {
            int var14 = getColorForLayer(var13, var10);
            if (var14 != 0) {
               ResourceLocation var15 = var13.usePlayerTexture() && var8 != null ? var8 : (ResourceLocation)this.layerTextureLookup.apply(new LayerTextureKey(var1, var13));
               VertexConsumer var16 = ItemRenderer.getArmorFoilBuffer(var6, RenderType.armorCutoutNoCull(var15), var11);
               var3.renderToBuffer(var5, var16, var7, OverlayTexture.NO_OVERLAY, var14);
               var11 = false;
            }
         }

         ArmorTrim var17 = (ArmorTrim)var4.get(DataComponents.TRIM);
         if (var17 != null) {
            TextureAtlasSprite var18 = (TextureAtlasSprite)this.trimSpriteLookup.apply(new TrimSpriteKey(var17, var1, var2));
            VertexConsumer var19 = var18.wrap(var6.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)var17.pattern().value()).decal())));
            var3.renderToBuffer(var5, var19, var7, OverlayTexture.NO_OVERLAY);
         }

      }
   }

   private static int getColorForLayer(EquipmentClientInfo.Layer var0, int var1) {
      Optional var2 = var0.dyeable();
      if (var2.isPresent()) {
         int var3 = (Integer)((EquipmentClientInfo.Dyeable)var2.get()).colorWhenUndyed().map(ARGB::opaque).orElse(0);
         return var1 != 0 ? var1 : var3;
      } else {
         return -1;
      }
   }

   static record LayerTextureKey(EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer) {
      final EquipmentClientInfo.LayerType layerType;
      final EquipmentClientInfo.Layer layer;

      LayerTextureKey(EquipmentClientInfo.LayerType var1, EquipmentClientInfo.Layer var2) {
         super();
         this.layerType = var1;
         this.layer = var2;
      }
   }

   static record TrimSpriteKey(ArmorTrim trim, EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> equipmentAssetId) {
      TrimSpriteKey(ArmorTrim var1, EquipmentClientInfo.LayerType var2, ResourceKey<EquipmentAsset> var3) {
         super();
         this.trim = var1;
         this.layerType = var2;
         this.equipmentAssetId = var3;
      }

      private static String getColorPaletteSuffix(Holder<TrimMaterial> var0, ResourceKey<EquipmentAsset> var1) {
         String var2 = (String)((TrimMaterial)var0.value()).overrideArmorAssets().get(var1);
         return var2 != null ? var2 : ((TrimMaterial)var0.value()).assetName();
      }

      public ResourceLocation textureId() {
         ResourceLocation var1 = ((TrimPattern)this.trim.pattern().value()).assetId();
         String var2 = getColorPaletteSuffix(this.trim.material(), this.equipmentAssetId);
         return var1.withPath((UnaryOperator)((var2x) -> "trims/entity/" + this.layerType.getSerializedName() + "/" + var2x + "_" + var2));
      }
   }
}
