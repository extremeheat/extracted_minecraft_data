package net.minecraft.block;

import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSourceImpl implements IBlockSource {
   private final World field_82627_a;
   private final int field_82625_b;
   private final int field_82626_c;
   private final int field_82624_d;

   public BlockSourceImpl(World var1, int var2, int var3, int var4) {
      super();
      this.field_82627_a = var1;
      this.field_82625_b = var2;
      this.field_82626_c = var3;
      this.field_82624_d = var4;
   }

   @Override
   public World func_82618_k() {
      return this.field_82627_a;
   }

   @Override
   public double func_82615_a() {
      return (double)this.field_82625_b + 0.5;
   }

   @Override
   public double func_82617_b() {
      return (double)this.field_82626_c + 0.5;
   }

   @Override
   public double func_82616_c() {
      return (double)this.field_82624_d + 0.5;
   }

   @Override
   public int func_82623_d() {
      return this.field_82625_b;
   }

   @Override
   public int func_82622_e() {
      return this.field_82626_c;
   }

   @Override
   public int func_82621_f() {
      return this.field_82624_d;
   }

   @Override
   public int func_82620_h() {
      return this.field_82627_a.func_72805_g(this.field_82625_b, this.field_82626_c, this.field_82624_d);
   }

   @Override
   public TileEntity func_150835_j() {
      return this.field_82627_a.func_147438_o(this.field_82625_b, this.field_82626_c, this.field_82624_d);
   }
}
