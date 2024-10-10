package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

public class EquipmentLayerRenderer {
   private static final int NO_LAYER_COLOR = 0;
   private final EquipmentModelSet equipmentModels;
   private final Function<EquipmentLayerRenderer.LayerTextureKey, ResourceLocation> layerTextureLookup;
   private final Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> trimSpriteLookup;

   public EquipmentLayerRenderer(EquipmentModelSet var1, TextureAtlas var2) {
      super();
      this.equipmentModels = var1;
      this.layerTextureLookup = Util.memoize(var0 -> var0.layer.getTextureLocation(var0.layerType));
      this.trimSpriteLookup = Util.memoize(var1x -> {
         ResourceLocation var2x = var1x.trim.getTexture(var1x.layerType, var1x.equipmentModelId);
         return var2.getSprite(var2x);
      });
   }

   public void renderLayers(EquipmentModel.LayerType var1, ResourceLocation var2, Model var3, ItemStack var4, PoseStack var5, MultiBufferSource var6, int var7) {
      this.renderLayers(var1, var2, var3, var4, var5, var6, var7, null);
   }

   public void renderLayers(
      EquipmentModel.LayerType var1,
      ResourceLocation var2,
      Model var3,
      ItemStack var4,
      PoseStack var5,
      MultiBufferSource var6,
      int var7,
      @Nullable ResourceLocation var8
   ) {
      List var9 = this.equipmentModels.get(var2).getLayers(var1);
      if (!var9.isEmpty()) {
         int var10 = var4.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(var4, 0) : 0;
         boolean var11 = var4.hasFoil();

         for (EquipmentModel.Layer var13 : var9) {
            int var14 = getColorForLayer(var13, var10);
            if (var14 != 0) {
               ResourceLocation var15 = var13.usePlayerTexture() && var8 != null
                  ? var8
                  : this.layerTextureLookup.apply(new EquipmentLayerRenderer.LayerTextureKey(var1, var13));
               VertexConsumer var16 = ItemRenderer.getArmorFoilBuffer(var6, RenderType.armorCutoutNoCull(var15), var11);
               var3.renderToBuffer(var5, var16, var7, OverlayTexture.NO_OVERLAY, var14);
               var11 = false;
            }
         }

         ArmorTrim var17 = var4.get(DataComponents.TRIM);
         if (var17 != null) {
            TextureAtlasSprite var18 = this.trimSpriteLookup.apply(new EquipmentLayerRenderer.TrimSpriteKey(var17, var1, var2));
            VertexConsumer var19 = var18.wrap(var6.getBuffer(Sheets.armorTrimsSheet(var17.pattern().value().decal())));
            var3.renderToBuffer(var5, var19, var7, OverlayTexture.NO_OVERLAY);
         }
      }
   }

   private static int getColorForLayer(EquipmentModel.Layer var0, int var1) {
      Optional var2 = var0.dyeable();
      if (var2.isPresent()) {
         int var3 = ((EquipmentModel.Dyeable)var2.get()).colorWhenUndyed().map(ARGB::opaque).orElse(0);
         return var1 != 0 ? var1 : var3;
      } else {
         return -1;
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
