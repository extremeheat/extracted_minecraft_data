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

   public SpecialBlockModelRenderer(Map<Block, SpecialModelRenderer<?>> var1) {
      super();
      this.renderers = var1;
   }

   public static SpecialBlockModelRenderer vanilla(SpecialModelRenderer.BakingContext var0) {
      return new SpecialBlockModelRenderer(SpecialModelRenderers.createBlockRenderers(var0));
   }

   public void renderByBlock(Block var1, ItemDisplayContext var2, PoseStack var3, SubmitNodeCollector var4, int var5, int var6, int var7) {
      SpecialModelRenderer var8 = (SpecialModelRenderer)this.renderers.get(var1);
      if (var8 != null) {
         var8.submit((Object)null, var2, var3, var4, var5, var6, false, var7);
      }

   }
}
