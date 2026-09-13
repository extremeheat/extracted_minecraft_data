package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAnvil extends BlockFalling {
   public static final String[] field_149834_a = new String[]{"intact", "slightlyDamaged", "veryDamaged"};
   private static final String[] field_149835_N = new String[]{"anvil_top_damaged_0", "anvil_top_damaged_1", "anvil_top_damaged_2"};
   public int field_149833_b;
   private IIcon[] field_149836_O;

   protected BlockAnvil() {
      super(Material.field_151574_g);
      this.func_149713_g(0);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (this.field_149833_b == 3 && var1 == 1) {
         int var3 = (var2 >> 2) % this.field_149836_O.length;
         return this.field_149836_O[var3];
      } else {
         return this.field_149761_L;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("anvil_base");
      this.field_149836_O = new IIcon[field_149835_N.length];

      for(int var2 = 0; var2 < this.field_149836_O.length; ++var2) {
         this.field_149836_O[var2] = var1.func_94245_a(field_149835_N[var2]);
      }
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3;
      int var8 = var1.func_72805_g(var2, var3, var4) >> 2;
      var7 = ++var7 % 4;
      if (var7 == 0) {
         var1.func_72921_c(var2, var3, var4, 2 | var8 << 2, 2);
      }

      if (var7 == 1) {
         var1.func_72921_c(var2, var3, var4, 3 | var8 << 2, 2);
      }

      if (var7 == 2) {
         var1.func_72921_c(var2, var3, var4, 0 | var8 << 2, 2);
      }

      if (var7 == 3) {
         var1.func_72921_c(var2, var3, var4, 1 | var8 << 2, 2);
      }
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         var5.func_82244_d(var2, var3, var4);
         return true;
      }
   }

   @Override
   public int func_149645_b() {
      return 35;
   }

   @Override
   public int func_149692_a(int var1) {
      return var1 >> 2;
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4) & 3;
      if (var5 != 3 && var5 != 1) {
         this.func_149676_a(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
      }
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
      var3.add(new ItemStack(var1, 1, 2));
   }

   @Override
   protected void func_149829_a(EntityFallingBlock var1) {
      var1.func_145806_a(true);
   }

   @Override
   public void func_149828_a(World var1, int var2, int var3, int var4, int var5) {
      var1.func_72926_e(1022, var2, var3, var4, 0);
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return true;
   }
}
