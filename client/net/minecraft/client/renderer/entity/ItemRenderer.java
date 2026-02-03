package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemRenderer {
   public static final Identifier ENCHANTED_GLINT_ARMOR = Identifier.withDefaultNamespace("textures/misc/enchanted_glint_armor.png");
   public static final Identifier ENCHANTED_GLINT_ITEM = Identifier.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
   public static final float SPECIAL_FOIL_UI_SCALE = 0.5F;
   public static final float SPECIAL_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public static final float SPECIAL_FOIL_TEXTURE_SCALE = 0.0078125F;
   public static final int NO_TINT = -1;

   public ItemRenderer() {
      super();
   }

   public static void renderItem(final ItemDisplayContext type, final PoseStack poseStack, final MultiBufferSource bufferSource, final int lightCoords, final int overlayCoords, final int[] tintLayers, final List<BakedQuad> quads, final RenderType renderType, final ItemStackRenderState.FoilType foilType) {
      VertexConsumer builder;
      if (foilType == ItemStackRenderState.FoilType.SPECIAL) {
         PoseStack.Pose cameraPose = poseStack.last().copy();
         if (type == ItemDisplayContext.GUI) {
            MatrixUtil.mulComponentWise(cameraPose.pose(), 0.5F);
         } else if (type.firstPerson()) {
            MatrixUtil.mulComponentWise(cameraPose.pose(), 0.75F);
         }

         builder = getSpecialFoilBuffer(bufferSource, renderType, cameraPose);
      } else {
         builder = getFoilBuffer(bufferSource, renderType, true, foilType != ItemStackRenderState.FoilType.NONE);
      }

      renderQuadList(poseStack, builder, quads, tintLayers, lightCoords, overlayCoords);
   }

   private static VertexConsumer getSpecialFoilBuffer(final MultiBufferSource bufferSource, final RenderType renderType, final PoseStack.Pose cameraPose) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(bufferSource.getBuffer(useTransparentGlint(renderType) ? RenderTypes.glintTranslucent() : RenderTypes.glint()), cameraPose, 0.0078125F), bufferSource.getBuffer(renderType));
   }

   public static VertexConsumer getFoilBuffer(final MultiBufferSource bufferSource, final RenderType renderType, final boolean sheeted, final boolean hasFoil) {
      if (hasFoil) {
         return useTransparentGlint(renderType) ? VertexMultiConsumer.create(bufferSource.getBuffer(RenderTypes.glintTranslucent()), bufferSource.getBuffer(renderType)) : VertexMultiConsumer.create(bufferSource.getBuffer(sheeted ? RenderTypes.glint() : RenderTypes.entityGlint()), bufferSource.getBuffer(renderType));
      } else {
         return bufferSource.getBuffer(renderType);
      }
   }

   public static List<RenderType> getFoilRenderTypes(final RenderType baseRenderType, final boolean sheeted, final boolean hasFoil) {
      if (hasFoil) {
         return useTransparentGlint(baseRenderType) ? List.of(baseRenderType, RenderTypes.glintTranslucent()) : List.of(baseRenderType, sheeted ? RenderTypes.glint() : RenderTypes.entityGlint());
      } else {
         return List.of(baseRenderType);
      }
   }

   private static boolean useTransparentGlint(final RenderType renderType) {
      return Minecraft.useShaderTransparency() && (renderType == Sheets.translucentItemSheet() || renderType == Sheets.translucentBlockItemSheet());
   }

   private static int getLayerColorSafe(final int[] layers, final int layer) {
      return layer >= 0 && layer < layers.length ? layers[layer] : -1;
   }

   private static void renderQuadList(final PoseStack poseStack, final VertexConsumer builder, final List<BakedQuad> quads, final int[] tintLayers, final int lightCoords, final int overlayCoords) {
      PoseStack.Pose pose = poseStack.last();

      for(BakedQuad quad : quads) {
         float alpha;
         float red;
         float green;
         float blue;
         if (quad.isTinted()) {
            int color = getLayerColorSafe(tintLayers, quad.tintIndex());
            alpha = (float)ARGB.alpha(color) / 255.0F;
            red = (float)ARGB.red(color) / 255.0F;
            green = (float)ARGB.green(color) / 255.0F;
            blue = (float)ARGB.blue(color) / 255.0F;
         } else {
            alpha = 1.0F;
            red = 1.0F;
            green = 1.0F;
            blue = 1.0F;
         }

         builder.putBulkData(pose, quad, red, green, blue, alpha, lightCoords, overlayCoords);
      }

   }
}
