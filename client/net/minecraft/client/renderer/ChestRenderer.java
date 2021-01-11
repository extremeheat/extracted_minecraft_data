package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class ChestRenderer {
   public ChestRenderer() {
      super();
   }

   public void func_178175_a(Block var1, float var2) {
      GlStateManager.func_179131_c(var2, var2, var2, 1.0F);
      GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
      TileEntityItemStackRenderer.field_147719_a.func_179022_a(new ItemStack(var1));
   }
}
