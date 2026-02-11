package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadBrightness;
import com.mojang.blaze3d.vertex.QuadLightmapCoords;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

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

   public static void renderItem(final ItemDisplayContext type, final PoseStack poseStack, final MultiBufferSource bufferSource, final int lightCoords, final int overlayCoords, final int[] tintLayers, final List<BakedQuad> quads, final ItemStackRenderState.FoilType foilType) {
      PoseStack.Pose pose = poseStack.last();
      PoseStack.Pose foilDecalPose = foilType == ItemStackRenderState.FoilType.SPECIAL ? computeFoilDecalPose(type, pose) : null;
      QuadLightmapCoords wrappedLightmapCoords = QuadLightmapCoords.create(lightCoords);

      for(BakedQuad quad : quads) {
         RenderType renderType = quad.spriteInfo().itemRenderType();
         int tintColor = getLayerColorSafe(tintLayers, quad);
         if (foilType != ItemStackRenderState.FoilType.NONE) {
            VertexConsumer foilBuffer = getFoilBuffer(bufferSource, renderType, foilDecalPose);
            foilBuffer.putBulkData(pose, quad, QuadBrightness.ALL_BRIGHT, tintColor, wrappedLightmapCoords, overlayCoords);
         }

         bufferSource.getBuffer(renderType).putBulkData(pose, quad, QuadBrightness.ALL_BRIGHT, tintColor, wrappedLightmapCoords, overlayCoords);
      }

   }

   private static VertexConsumer getFoilBuffer(final MultiBufferSource bufferSource, final RenderType renderType, final PoseStack.@Nullable Pose foilDecalPose) {
      VertexConsumer foilBuffer = bufferSource.getBuffer(getFoilRenderType(renderType, true));
      if (foilDecalPose != null) {
         foilBuffer = new SheetedDecalTextureGenerator(foilBuffer, foilDecalPose, 0.0078125F);
      }

      return foilBuffer;
   }

   private static PoseStack.Pose computeFoilDecalPose(final ItemDisplayContext type, final PoseStack.Pose pose) {
      PoseStack.Pose foilDecalPose = pose.copy();
      if (type == ItemDisplayContext.GUI) {
         MatrixUtil.mulComponentWise(foilDecalPose.pose(), 0.5F);
      } else if (type.firstPerson()) {
         MatrixUtil.mulComponentWise(foilDecalPose.pose(), 0.75F);
      }

      return foilDecalPose;
   }

   public static VertexConsumer getFoilBuffer(final MultiBufferSource bufferSource, final RenderType renderType, final boolean sheeted, final boolean hasFoil) {
      return hasFoil ? VertexMultiConsumer.create(bufferSource.getBuffer(getFoilRenderType(renderType, sheeted)), bufferSource.getBuffer(renderType)) : bufferSource.getBuffer(renderType);
   }

   private static RenderType getFoilRenderType(final RenderType baseRenderType, final boolean sheeted) {
      if (useTransparentGlint(baseRenderType)) {
         return RenderTypes.glintTranslucent();
      } else {
         return sheeted ? RenderTypes.glint() : RenderTypes.entityGlint();
      }
   }

   public static List<RenderType> getFoilRenderTypes(final RenderType baseRenderType, final boolean sheeted, final boolean hasFoil) {
      return hasFoil ? List.of(baseRenderType, getFoilRenderType(baseRenderType, sheeted)) : List.of(baseRenderType);
   }

   private static boolean useTransparentGlint(final RenderType renderType) {
      return Minecraft.useShaderTransparency() && renderType.outputTarget() == OutputTarget.ITEM_ENTITY_TARGET;
   }

   private static int getLayerColorSafe(final int[] layers, final int layer) {
      return layer >= 0 && layer < layers.length ? layers[layer] : -1;
   }

   private static int getLayerColorSafe(final int[] tintLayers, final BakedQuad quad) {
      return quad.isTinted() ? getLayerColorSafe(tintLayers, quad.tintIndex()) : -1;
   }
}
