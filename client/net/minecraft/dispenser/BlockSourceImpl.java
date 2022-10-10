package net.minecraft.dispenser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSourceImpl implements IBlockSource {
   private final World field_197525_a;
   private final BlockPos field_179317_b;

   public BlockSourceImpl(World var1, BlockPos var2) {
      super();
      this.field_197525_a = var1;
      this.field_179317_b = var2;
   }

   public World func_197524_h() {
      return this.field_197525_a;
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

   public IBlockState func_189992_e() {
      return this.field_197525_a.func_180495_p(this.field_179317_b);
   }

   public <T extends TileEntity> T func_150835_j() {
      return this.field_197525_a.func_175625_s(this.field_179317_b);
   }
}
