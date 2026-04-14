package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.MatrixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

public class ItemFeatureRenderer {
   public static final Identifier ENCHANTED_GLINT_ARMOR = Identifier.withDefaultNamespace("textures/misc/enchanted_glint_armor.png");
   public static final Identifier ENCHANTED_GLINT_ITEM = Identifier.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
   private static final float SPECIAL_FOIL_UI_SCALE = 0.5F;
   private static final float SPECIAL_FOIL_FIRST_PERSON_SCALE = 0.75F;
   private static final float SPECIAL_FOIL_TEXTURE_SCALE = 0.0078125F;
   public static final int NO_TINT = -1;
   private final QuadInstance quadInstance = new QuadInstance();

   public ItemFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource) {
      for(SubmitNodeStorage.ItemSubmit submit : nodeCollection.getItemSubmits()) {
         if (!hasTranslucency(submit)) {
            this.renderItem(bufferSource, outlineBufferSource, submit);
         }
      }

   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource) {
      for(SubmitNodeStorage.ItemSubmit submit : nodeCollection.getItemSubmits()) {
         if (hasTranslucency(submit)) {
            this.renderItem(bufferSource, outlineBufferSource, submit);
         }
      }

   }

   private static boolean hasTranslucency(final SubmitNodeStorage.ItemSubmit submit) {
      for(BakedQuad quad : submit.quads()) {
         if (quad.materialInfo().itemRenderType().hasBlending()) {
            return true;
         }
      }

      return false;
   }

   private void renderItem(final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final SubmitNodeStorage.ItemSubmit submit) {
      PoseStack.Pose pose = submit.pose();
      ItemStackRenderState.FoilType foilType = submit.foilType();
      PoseStack.Pose foilDecalPose = foilType == ItemStackRenderState.FoilType.SPECIAL ? computeFoilDecalPose(submit.displayContext(), pose) : null;
      this.quadInstance.setLightCoords(submit.lightCoords());
      this.quadInstance.setOverlayCoords(submit.overlayCoords());
      if (submit.outlineColor() != 0) {
         outlineBufferSource.setColor(submit.outlineColor());
      }

      for(BakedQuad quad : submit.quads()) {
         BakedQuad.MaterialInfo material = quad.materialInfo();
         RenderType renderType = material.itemRenderType();
         this.quadInstance.setColor(getLayerColorSafe(submit.tintLayers(), material));
         if (foilType != ItemStackRenderState.FoilType.NONE) {
            VertexConsumer foilBuffer = getFoilBuffer(bufferSource, renderType, foilDecalPose);
            foilBuffer.putBakedQuad(pose, quad, this.quadInstance);
         }

         if (submit.outlineColor() != 0) {
            outlineBufferSource.getBuffer(renderType).putBakedQuad(pose, quad, this.quadInstance);
         }

         bufferSource.getBuffer(renderType).putBakedQuad(pose, quad, this.quadInstance);
      }

   }

   private static VertexConsumer getFoilBuffer(final MultiBufferSource bufferSource, final RenderType renderType, final PoseStack.@Nullable Pose foilDecalPose) {
      RenderType foilRenderType = useTransparentGlint(renderType) ? RenderTypes.glintTranslucent() : RenderTypes.glint();
      VertexConsumer foilBuffer = bufferSource.getBuffer(foilRenderType);
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

   private static boolean useTransparentGlint(final RenderType renderType) {
      return Minecraft.getInstance().gameRenderer.gameRenderState().useShaderTransparency() && renderType.outputTarget() == OutputTarget.ITEM_ENTITY_TARGET;
   }

   private static int getLayerColorSafe(final int[] layers, final int layer) {
      return layer >= 0 && layer < layers.length ? layers[layer] : -1;
   }

   private static int getLayerColorSafe(final int[] tintLayers, final BakedQuad.MaterialInfo material) {
      return material.isTinted() ? getLayerColorSafe(tintLayers, material.tintIndex()) : -1;
   }
}
