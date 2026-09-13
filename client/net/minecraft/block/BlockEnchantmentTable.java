package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer {
   private IIcon field_149950_a;
   private IIcon field_149949_b;

   protected BlockEnchantmentTable() {
      super(Material.field_151576_e);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
      this.func_149713_g(0);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      super.func_149734_b(var1, var2, var3, var4, var5);

      for(int var6 = var2 - 2; var6 <= var2 + 2; ++var6) {
         for(int var7 = var4 - 2; var7 <= var4 + 2; ++var7) {
            if (var6 > var2 - 2 && var6 < var2 + 2 && var7 == var4 - 1) {
               var7 = var4 + 2;
            }

            if (var5.nextInt(16) == 0) {
               for(int var8 = var3; var8 <= var3 + 1; ++var8) {
                  if (var1.func_147439_a(var6, var8, var7) == Blocks.field_150342_X) {
                     if (!var1.func_147437_c((var6 - var2) / 2 + var2, var8, (var7 - var4) / 2 + var4)) {
                        break;
                     }

                     var1.func_72869_a(
                        "enchantmenttable",
                        (double)var2 + 0.5,
                        (double)var3 + 2.0,
                        (double)var4 + 0.5,
                        (double)((float)(var6 - var2) + var5.nextFloat()) - 0.5,
                        (double)((float)(var8 - var3) - var5.nextFloat() - 1.0F),
                        (double)((float)(var7 - var4) + var5.nextFloat()) - 0.5
                     );
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 0) {
         return this.field_149949_b;
      } else {
         return var1 == 1 ? this.field_149950_a : this.field_149761_L;
      }
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityEnchantmentTable();
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntityEnchantmentTable var10 = (TileEntityEnchantmentTable)var1.func_147438_o(var2, var3, var4);
         var5.func_71002_c(var2, var3, var4, var10.func_145921_b() ? var10.func_145919_a() : null);
         return true;
      }
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      super.func_149689_a(var1, var2, var3, var4, var5, var6);
      if (var6.func_82837_s()) {
         ((TileEntityEnchantmentTable)var1.func_147438_o(var2, var3, var4)).func_145920_a(var6.func_82833_r());
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_" + "side");
      this.field_149950_a = var1.func_94245_a(this.func_149641_N() + "_" + "top");
      this.field_149949_b = var1.func_94245_a(this.func_149641_N() + "_" + "bottom");
   }
}
