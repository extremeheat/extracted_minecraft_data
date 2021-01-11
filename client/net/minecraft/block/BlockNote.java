package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer {
   private static final List<String> field_176434_a = Lists.newArrayList(new String[]{"harp", "bd", "snare", "hat", "bassattack"});

   public BlockNote() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      boolean var5 = var1.func_175640_z(var2);
      TileEntity var6 = var1.func_175625_s(var2);
      if (var6 instanceof TileEntityNote) {
         TileEntityNote var7 = (TileEntityNote)var6;
         if (var7.field_145880_i != var5) {
            if (var5) {
               var7.func_175108_a(var1, var2);
            }

            var7.field_145880_i = var5;
         }
      }

   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntity var9 = var1.func_175625_s(var2);
         if (var9 instanceof TileEntityNote) {
            TileEntityNote var10 = (TileEntityNote)var9;
            var10.func_145877_a();
            var10.func_175108_a(var1, var2);
            var4.func_71029_a(StatList.field_181735_S);
         }

         return true;
      }
   }

   public void func_180649_a(World var1, BlockPos var2, EntityPlayer var3) {
      if (!var1.field_72995_K) {
         TileEntity var4 = var1.func_175625_s(var2);
         if (var4 instanceof TileEntityNote) {
            ((TileEntityNote)var4).func_175108_a(var1, var2);
            var3.func_71029_a(StatList.field_181734_R);
         }

      }
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityNote();
   }

   private String func_176433_b(int var1) {
      if (var1 < 0 || var1 >= field_176434_a.size()) {
         var1 = 0;
      }

      return (String)field_176434_a.get(var1);
   }

   public boolean func_180648_a(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      float var6 = (float)Math.pow(2.0D, (double)(var5 - 12) / 12.0D);
      var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "note." + this.func_176433_b(var4), 3.0F, var6);
      var1.func_175688_a(EnumParticleTypes.NOTE, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 1.2D, (double)var2.func_177952_p() + 0.5D, (double)var5 / 24.0D, 0.0D, 0.0D);
      return true;
   }

   public int func_149645_b() {
      return 3;
   }
}
