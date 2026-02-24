package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class BlockModelRenderState {
   /** @deprecated */
   @Deprecated
   public @Nullable Block block;
   public @Nullable BlockStateModel model;
   public @Nullable SpecialModelRenderer<?> specialRenderer;
   public final int[] tintLayers = new int[3];

   public BlockModelRenderState() {
      super();
   }

   public void clear() {
      this.block = null;
      this.model = null;
      this.specialRenderer = null;
      Arrays.fill(this.tintLayers, -1);
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final int outlineColor) {
      if (this.model != null) {
         RenderType renderType = this.model.hasTranslucency() ? Sheets.translucentBlockSheet() : Sheets.cutoutBlockSheet();
         submitNodeCollector.submitBlockModel(poseStack, renderType, this.model, this.tintLayers, lightCoords, overlayCoords, outlineColor);
      }

      if (this.specialRenderer != null) {
         this.specialRenderer.submit((Object)null, ItemDisplayContext.NONE, poseStack, submitNodeCollector, lightCoords, overlayCoords, false, outlineColor);
      }

   }

   public void submitOnlyOutline(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final int outlineColor) {
      if (this.model != null) {
         submitNodeCollector.submitBlockModel(poseStack, RenderTypes.outline(TextureAtlas.LOCATION_BLOCKS), this.model, this.tintLayers, lightCoords, overlayCoords, outlineColor);
      }

   }

   public void submitWithZOffset(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final int outlineColor) {
      if (this.model != null) {
         submitNodeCollector.submitBlockModel(poseStack, RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS), this.model, this.tintLayers, lightCoords, overlayCoords, outlineColor);
      }

   }

   public boolean isEmpty() {
      return this.model == null && this.specialRenderer == null;
   }
}
