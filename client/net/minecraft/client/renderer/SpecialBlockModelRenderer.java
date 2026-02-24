package net.minecraft.client.renderer;

import java.util.Map;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

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

   public @Nullable SpecialModelRenderer<?> getSpecialRenderer(final Block block) {
      return (SpecialModelRenderer)this.renderers.get(block);
   }
}
