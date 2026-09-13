package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public BlockRedstoneComparator(boolean var1) {
      super(var1);
      this.field_149758_A = true;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151132_bS;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151132_bS;
   }

   @Override
   protected int func_149901_b(int var1) {
      return 2;
   }

   @Override
   protected BlockRedstoneDiode func_149906_e() {
      return Blocks.field_150455_bV;
   }

   @Override
   protected BlockRedstoneDiode func_149898_i() {
      return Blocks.field_150441_bU;
   }

   @Override
   public int func_149645_b() {
      return 37;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      boolean var3 = this.field_149914_a || (var2 & 8) != 0;
      if (var1 == 0) {
         return var3 ? Blocks.field_150429_aA.func_149733_h(var1) : Blocks.field_150437_az.func_149733_h(var1);
      } else if (var1 == 1) {
         return var3 ? Blocks.field_150455_bV.field_149761_L : this.field_149761_L;
      } else {
         return Blocks.field_150334_T.func_149733_h(1);
      }
   }

   @Override
   protected boolean func_149905_c(int var1) {
      return this.field_149914_a || (var1 & 8) != 0;
   }

   @Override
   protected int func_149904_f(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return this.func_149971_e(var1, var2, var3, var4).func_145996_a();
   }

   private int func_149970_j(World var1, int var2, int var3, int var4, int var5) {
      return !this.func_149969_d(var5)
         ? this.func_149903_h(var1, var2, var3, var4, var5)
         : Math.max(this.func_149903_h(var1, var2, var3, var4, var5) - this.func_149902_h(var1, var2, var3, var4, var5), 0);
   }

   public boolean func_149969_d(int var1) {
      return (var1 & 4) == 4;
   }

   @Override
   protected boolean func_149900_a(World var1, int var2, int var3, int var4, int var5) {
      int var6 = this.func_149903_h(var1, var2, var3, var4, var5);
      if (var6 >= 15) {
         return true;
      } else if (var6 == 0) {
         return false;
      } else {
         int var7 = this.func_149902_h(var1, var2, var3, var4, var5);
         if (var7 == 0) {
            return true;
         } else {
            return var6 >= var7;
         }
      }
   }

   @Override
   protected int func_149903_h(World var1, int var2, int var3, int var4, int var5) {
      int var6 = super.func_149903_h(var1, var2, var3, var4, var5);
      int var7 = func_149895_l(var5);
      int var8 = var2 + Direction.field_71583_a[var7];
      int var9 = var4 + Direction.field_71581_b[var7];
      Block var10 = var1.func_147439_a(var8, var3, var9);
      if (var10.func_149740_M()) {
         var6 = var10.func_149736_g(var1, var8, var3, var9, Direction.field_71580_e[var7]);
      } else if (var6 < 15 && var10.func_149721_r()) {
         var8 += Direction.field_71583_a[var7];
         var9 += Direction.field_71581_b[var7];
         var10 = var1.func_147439_a(var8, var3, var9);
         if (var10.func_149740_M()) {
            var6 = var10.func_149736_g(var1, var8, var3, var9, Direction.field_71580_e[var7]);
         }
      }

      return var6;
   }

   public TileEntityComparator func_149971_e(IBlockAccess var1, int var2, int var3, int var4) {
      return (TileEntityComparator)var1.func_147438_o(var2, var3, var4);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      int var10 = var1.func_72805_g(var2, var3, var4);
      boolean var11 = this.field_149914_a | (var10 & 8) != 0;
      boolean var12 = !this.func_149969_d(var10);
      int var13 = var12 ? 4 : 0;
      var13 |= var11 ? 8 : 0;
      var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "random.click", 0.3F, var12 ? 0.55F : 0.5F);
      var1.func_72921_c(var2, var3, var4, var13 | var10 & 3, 2);
      this.func_149972_c(var1, var2, var3, var4, var1.field_73012_v);
      return true;
   }

   @Override
   protected void func_149897_b(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.func_147477_a(var2, var3, var4, this)) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         int var7 = this.func_149970_j(var1, var2, var3, var4, var6);
         int var8 = this.func_149971_e(var1, var2, var3, var4).func_145996_a();
         if (var7 != var8 || this.func_149905_c(var6) != this.func_149900_a(var1, var2, var3, var4, var6)) {
            if (this.func_149912_i(var1, var2, var3, var4, var6)) {
               var1.func_147454_a(var2, var3, var4, this, this.func_149901_b(0), -1);
            } else {
               var1.func_147454_a(var2, var3, var4, this, this.func_149901_b(0), 0);
            }
         }
      }
   }

   private void func_149972_c(World var1, int var2, int var3, int var4, Random var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      int var7 = this.func_149970_j(var1, var2, var3, var4, var6);
      int var8 = this.func_149971_e(var1, var2, var3, var4).func_145996_a();
      this.func_149971_e(var1, var2, var3, var4).func_145995_a(var7);
      if (var8 != var7 || !this.func_149969_d(var6)) {
         boolean var9 = this.func_149900_a(var1, var2, var3, var4, var6);
         boolean var10 = this.field_149914_a || (var6 & 8) != 0;
         if (var10 && !var9) {
            var1.func_72921_c(var2, var3, var4, var6 & -9, 2);
         } else if (!var10 && var9) {
            var1.func_72921_c(var2, var3, var4, var6 | 8, 2);
         }

         this.func_149911_e(var1, var2, var3, var4);
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (this.field_149914_a) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         var1.func_147465_d(var2, var3, var4, this.func_149898_i(), var6 | 8, 4);
      }

      this.func_149972_c(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      var1.func_147455_a(var2, var3, var4, this.func_149915_a(var1, 0));
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      super.func_149749_a(var1, var2, var3, var4, var5, var6);
      var1.func_147475_p(var2, var3, var4);
      this.func_149911_e(var1, var2, var3, var4);
   }

   @Override
   public boolean func_149696_a(World var1, int var2, int var3, int var4, int var5, int var6) {
      super.func_149696_a(var1, var2, var3, var4, var5, var6);
      TileEntity var7 = var1.func_147438_o(var2, var3, var4);
      return var7 != null ? var7.func_145842_c(var5, var6) : false;
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityComparator();
   }
}
