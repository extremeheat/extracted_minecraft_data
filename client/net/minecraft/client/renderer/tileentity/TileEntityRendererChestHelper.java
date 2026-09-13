package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

public class TileEntityRendererChestHelper {
   public static TileEntityRendererChestHelper field_147719_a = new TileEntityRendererChestHelper();
   private TileEntityChest field_147717_b = new TileEntityChest(0);
   private TileEntityChest field_147718_c = new TileEntityChest(1);
   private TileEntityEnderChest field_147716_d = new TileEntityEnderChest();

   public TileEntityRendererChestHelper() {
      super();
   }

   public void func_147715_a(Block var1, int var2, float var3) {
      if (var1 == Blocks.field_150477_bB) {
         TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_147716_d, 0.0, 0.0, 0.0, 0.0F);
      } else if (var1 == Blocks.field_150447_bR) {
         TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_147718_c, 0.0, 0.0, 0.0, 0.0F);
      } else {
         TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_147717_b, 0.0, 0.0, 0.0, 0.0F);
      }
   }
}
