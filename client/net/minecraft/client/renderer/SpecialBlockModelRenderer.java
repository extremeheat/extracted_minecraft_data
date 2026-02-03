package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;

public class SpecialBlockModelRenderer {
   public static final SpecialBlockModelRenderer EMPTY = new SpecialBlockModelRenderer(Map.of());
   private final Map<Block, SpecialModelRenderer<?>> renderers;

   public SpecialBlockModelRenderer(final Map<Block, SpecialModelRenderer<?>> renderers) {
      super();
      this.renderers = renderers;
   }

   public static SpecialBlockModelRenderer vanilla(final SpecialModelRenderer.BakingContext context) {
      return new SpecialBlockModelRenderer(SpecialModelRenderers.createBlockRenderers(context));
   }

   public void renderByBlock(final Block block, final ItemDisplayContext type, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final int outlineColor) {
      SpecialModelRenderer<?> specialRenderer = (SpecialModelRenderer)this.renderers.get(block);
      if (specialRenderer != null) {
         specialRenderer.submit((Object)null, type, poseStack, submitNodeCollector, lightCoords, overlayCoords, false, outlineColor);
      }

   }
}
