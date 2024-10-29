package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
import net.minecraft.client.resources.model.EquipmentModelSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimPattern;

public class EquipmentLayerRenderer {
   private static final int NO_LAYER_COLOR = 0;
   private final EquipmentModelSet equipmentModels;
   private final Function<LayerTextureKey, ResourceLocation> layerTextureLookup;
   private final Function<TrimSpriteKey, TextureAtlasSprite> trimSpriteLookup;

   public EquipmentLayerRenderer(EquipmentModelSet var1, TextureAtlas var2) {
      super();
      this.equipmentModels = var1;
      this.layerTextureLookup = Util.memoize((var0) -> {
         return var0.layer.getTextureLocation(var0.layerType);
      });
      this.trimSpriteLookup = Util.memoize((var1x) -> {
         ResourceLocation var2x = var1x.trim.getTexture(var1x.layerType, var1x.equipmentModelId);
         return var2.getSprite(var2x);
      });
   }

   public void renderLayers(EquipmentModel.LayerType var1, ResourceLocation var2, Model var3, ItemStack var4, PoseStack var5, MultiBufferSource var6, int var7) {
      this.renderLayers(var1, var2, var3, var4, var5, var6, var7, (ResourceLocation)null);
   }

   public void renderLayers(EquipmentModel.LayerType var1, ResourceLocation var2, Model var3, ItemStack var4, PoseStack var5, MultiBufferSource var6, int var7, @Nullable ResourceLocation var8) {
      List var9 = this.equipmentModels.get(var2).getLayers(var1);
      if (!var9.isEmpty()) {
         int var10 = var4.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(var4, 0) : 0;
         boolean var11 = var4.hasFoil();
         Iterator var12 = var9.iterator();

         while(true) {
            EquipmentModel.Layer var13;
            int var14;
            do {
               if (!var12.hasNext()) {
                  ArmorTrim var17 = (ArmorTrim)var4.get(DataComponents.TRIM);
                  if (var17 != null) {
                     TextureAtlasSprite var18 = (TextureAtlasSprite)this.trimSpriteLookup.apply(new TrimSpriteKey(var17, var1, var2));
                     VertexConsumer var19 = var18.wrap(var6.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)var17.pattern().value()).decal())));
                     var3.renderToBuffer(var5, var19, var7, OverlayTexture.NO_OVERLAY);
                  }

                  return;
               }

               var13 = (EquipmentModel.Layer)var12.next();
               var14 = getColorForLayer(var13, var10);
            } while(var14 == 0);

            ResourceLocation var15 = var13.usePlayerTexture() && var8 != null ? var8 : (ResourceLocation)this.layerTextureLookup.apply(new LayerTextureKey(var1, var13));
            VertexConsumer var16 = ItemRenderer.getArmorFoilBuffer(var6, RenderType.armorCutoutNoCull(var15), var11);
            var3.renderToBuffer(var5, var16, var7, OverlayTexture.NO_OVERLAY, var14);
            var11 = false;
         }
      }
   }

   private static int getColorForLayer(EquipmentModel.Layer var0, int var1) {
      Optional var2 = var0.dyeable();
      if (var2.isPresent()) {
         int var3 = (Integer)((EquipmentModel.Dyeable)var2.get()).colorWhenUndyed().map(ARGB::opaque).orElse(0);
         return var1 != 0 ? var1 : var3;
      } else {
         return -1;
      }
   }

   static record LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
      final EquipmentModel.LayerType layerType;
      final EquipmentModel.Layer layer;

      LayerTextureKey(EquipmentModel.LayerType var1, EquipmentModel.Layer var2) {
         super();
         this.layerType = var1;
         this.layer = var2;
      }

      public EquipmentModel.LayerType layerType() {
         return this.layerType;
      }

      public EquipmentModel.Layer layer() {
         return this.layer;
      }
   }

   static record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, ResourceLocation equipmentModelId) {
      final ArmorTrim trim;
      final EquipmentModel.LayerType layerType;
      final ResourceLocation equipmentModelId;

      TrimSpriteKey(ArmorTrim var1, EquipmentModel.LayerType var2, ResourceLocation var3) {
         super();
         this.trim = var1;
         this.layerType = var2;
         this.equipmentModelId = var3;
      }

      public ArmorTrim trim() {
         return this.trim;
      }

      public EquipmentModel.LayerType layerType() {
         return this.layerType;
      }

      public ResourceLocation equipmentModelId() {
         return this.equipmentModelId;
      }
   }
}
