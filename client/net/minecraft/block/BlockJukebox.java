package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
   private IIcon field_149927_a;

   protected BlockJukebox() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var1 == 1 ? this.field_149927_a : this.field_149761_L;
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.func_72805_g(var2, var3, var4) == 0) {
         return false;
      } else {
         this.func_149925_e(var1, var2, var3, var4);
         return true;
      }
   }

   public void func_149926_b(World var1, int var2, int var3, int var4, ItemStack var5) {
      if (!var1.field_72995_K) {
         BlockJukebox$TileEntityJukebox var6 = (BlockJukebox$TileEntityJukebox)var1.func_147438_o(var2, var3, var4);
         if (var6 != null) {
            var6.func_145857_a(var5.func_77946_l());
            var1.func_72921_c(var2, var3, var4, 1, 2);
         }
      }
   }

   public void func_149925_e(World var1, int var2, int var3, int var4) {
      if (!var1.field_72995_K) {
         BlockJukebox$TileEntityJukebox var5 = (BlockJukebox$TileEntityJukebox)var1.func_147438_o(var2, var3, var4);
         if (var5 != null) {
            ItemStack var6 = var5.func_145856_a();
            if (var6 != null) {
               var1.func_72926_e(1005, var2, var3, var4, 0);
               var1.func_72934_a(null, var2, var3, var4);
               var5.func_145857_a(null);
               var1.func_72921_c(var2, var3, var4, 0, 2);
               float var7 = 0.7F;
               double var8 = (double)(var1.field_73012_v.nextFloat() * var7) + (double)(1.0F - var7) * 0.5;
               double var10 = (double)(var1.field_73012_v.nextFloat() * var7) + (double)(1.0F - var7) * 0.2 + 0.6;
               double var12 = (double)(var1.field_73012_v.nextFloat() * var7) + (double)(1.0F - var7) * 0.5;
               ItemStack var14 = var6.func_77946_l();
               EntityItem var15 = new EntityItem(var1, (double)var2 + var8, (double)var3 + var10, (double)var4 + var12, var14);
               var15.field_145804_b = 10;
               var1.func_72838_d(var15);
            }
         }
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      this.func_149925_e(var1, var2, var3, var4);
      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      if (!var1.field_72995_K) {
         super.func_149690_a(var1, var2, var3, var4, var5, var6, 0);
      }
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new BlockJukebox$TileEntityJukebox();
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_149927_a = var1.func_94245_a(this.func_149641_N() + "_top");
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      ItemStack var6 = ((BlockJukebox$TileEntityJukebox)var1.func_147438_o(var2, var3, var4)).func_145856_a();
      return var6 == null ? 0 : Item.func_150891_b(var6.func_77973_b()) + 1 - Item.func_150891_b(Items.field_151096_cd);
   }
}
