package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockMobSpawner extends BlockContainer {
   protected BlockMobSpawner() {
      super(Material.field_151576_e);
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityMobSpawner();
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return null;
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.func_180653_a(var1, var2, var3, var4, var5);
      int var6 = 15 + var1.field_73012_v.nextInt(15) + var1.field_73012_v.nextInt(15);
      this.func_180637_b(var1, var2, var6);
   }

   public boolean func_149662_c() {
      return false;
   }

   public int func_149645_b() {
      return 3;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return null;
   }
}
