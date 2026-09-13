package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer {
   public BlockCommandBlock() {
      super(Material.field_151573_f);
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityCommandBlock();
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K) {
         boolean var6 = var1.func_72864_z(var2, var3, var4);
         int var7 = var1.func_72805_g(var2, var3, var4);
         boolean var8 = (var7 & 1) != 0;
         if (var6 && !var8) {
            var1.func_72921_c(var2, var3, var4, var7 | 1, 4);
            var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
         } else if (!var6 && var8) {
            var1.func_72921_c(var2, var3, var4, var7 & -2, 4);
         }
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      TileEntity var6 = var1.func_147438_o(var2, var3, var4);
      if (var6 != null && var6 instanceof TileEntityCommandBlock) {
         CommandBlockLogic var7 = ((TileEntityCommandBlock)var6).func_145993_a();
         var7.func_145755_a(var1);
         var1.func_147453_f(var2, var3, var4, this);
      }
   }

   @Override
   public int func_149738_a(World var1) {
      return 1;
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      TileEntityCommandBlock var10 = (TileEntityCommandBlock)var1.func_147438_o(var2, var3, var4);
      if (var10 != null) {
         var5.func_146100_a(var10);
      }

      return true;
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      TileEntity var6 = var1.func_147438_o(var2, var3, var4);
      return var6 != null && var6 instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)var6).func_145993_a().func_145760_g() : 0;
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      TileEntityCommandBlock var7 = (TileEntityCommandBlock)var1.func_147438_o(var2, var3, var4);
      if (var6.func_82837_s()) {
         var7.func_145993_a().func_145754_b(var6.func_82833_r());
      }
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }
}
