package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;

public class TileEntityDaylightDetector extends TileEntity implements ITickable {
   public TileEntityDaylightDetector() {
      super(TileEntityType.field_200986_q);
   }

   public void func_73660_a() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K && this.field_145850_b.func_82737_E() % 20L == 0L) {
         IBlockState var1 = this.func_195044_w();
         Block var2 = var1.func_177230_c();
         if (var2 instanceof BlockDaylightDetector) {
            BlockDaylightDetector.func_196319_d(var1, this.field_145850_b, this.field_174879_c);
         }
      }

   }
}
