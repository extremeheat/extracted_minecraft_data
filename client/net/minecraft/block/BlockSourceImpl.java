package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockSourceImpl implements IBlockSource {
   private final World field_82627_a;
   private final BlockPos field_179317_b;

   public BlockSourceImpl(World var1, BlockPos var2) {
      super();
      this.field_82627_a = var1;
      this.field_179317_b = var2;
   }

   public World func_82618_k() {
      return this.field_82627_a;
   }

   public double func_82615_a() {
      return (double)this.field_179317_b.func_177958_n() + 0.5D;
   }

   public double func_82617_b() {
      return (double)this.field_179317_b.func_177956_o() + 0.5D;
   }

   public double func_82616_c() {
      return (double)this.field_179317_b.func_177952_p() + 0.5D;
   }

   public BlockPos func_180699_d() {
      return this.field_179317_b;
   }

   public int func_82620_h() {
      IBlockState var1 = this.field_82627_a.func_180495_p(this.field_179317_b);
      return var1.func_177230_c().func_176201_c(var1);
   }

   public <T extends TileEntity> T func_150835_j() {
      return this.field_82627_a.func_175625_s(this.field_179317_b);
   }
}
