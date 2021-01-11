package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockBarrier extends Block {
   protected BlockBarrier() {
      super(Material.field_175972_I);
      this.func_149722_s();
      this.func_149752_b(6000001.0F);
      this.func_149649_H();
      this.field_149785_s = true;
   }

   public int func_149645_b() {
      return -1;
   }

   public boolean func_149662_c() {
      return false;
   }

   public float func_149685_I() {
      return 1.0F;
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
   }
}
