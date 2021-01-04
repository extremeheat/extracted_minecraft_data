package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.EntityBlockRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AnimatedEntityBlockRenderer {
   public AnimatedEntityBlockRenderer() {
      super();
   }

   public void renderSingleBlock(Block var1, float var2) {
      GlStateManager.color4f(var2, var2, var2, 1.0F);
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      EntityBlockRenderer.instance.renderByItem(new ItemStack(var1));
   }
}
