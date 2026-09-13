package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBlock extends Item {
   protected final Block field_150939_a;
   private IIcon field_150938_b;

   public ItemBlock(Block var1) {
      super();
      this.field_150939_a = var1;
   }

   public ItemBlock func_77655_b(String var1) {
      super.func_77655_b(var1);
      return this;
   }

   @Override
   public int func_94901_k() {
      return this.field_150939_a.func_149702_O() != null ? 1 : 0;
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return this.field_150938_b != null ? this.field_150938_b : this.field_150939_a.func_149733_h(1);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      Block var11 = var3.func_147439_a(var4, var5, var6);
      if (var11 == Blocks.field_150431_aC && (var3.func_72805_g(var4, var5, var6) & 7) < 1) {
         var7 = 1;
      } else if (var11 != Blocks.field_150395_bd && var11 != Blocks.field_150329_H && var11 != Blocks.field_150330_I) {
         if (var7 == 0) {
            --var5;
         }

         if (var7 == 1) {
            ++var5;
         }

         if (var7 == 2) {
            --var6;
         }

         if (var7 == 3) {
            ++var6;
         }

         if (var7 == 4) {
            --var4;
         }

         if (var7 == 5) {
            ++var4;
         }
      }

      if (var1.field_77994_a == 0) {
         return false;
      } else if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
         return false;
      } else if (var5 == 255 && this.field_150939_a.func_149688_o().func_76220_a()) {
         return false;
      } else if (var3.func_147472_a(this.field_150939_a, var4, var5, var6, false, var7, var2, var1)) {
         int var12 = this.func_77647_b(var1.func_77960_j());
         int var13 = this.field_150939_a.func_149660_a(var3, var4, var5, var6, var7, var8, var9, var10, var12);
         if (var3.func_147465_d(var4, var5, var6, this.field_150939_a, var13, 3)) {
            if (var3.func_147439_a(var4, var5, var6) == this.field_150939_a) {
               this.field_150939_a.func_149689_a(var3, var4, var5, var6, var2, var1);
               this.field_150939_a.func_149714_e(var3, var4, var5, var6, var13);
            }

            var3.func_72908_a(
               (double)((float)var4 + 0.5F),
               (double)((float)var5 + 0.5F),
               (double)((float)var6 + 0.5F),
               this.field_150939_a.field_149762_H.func_150496_b(),
               (this.field_150939_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F,
               this.field_150939_a.field_149762_H.func_150494_d() * 0.8F
            );
            --var1.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean func_150936_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6, ItemStack var7) {
      Block var8 = var1.func_147439_a(var2, var3, var4);
      if (var8 == Blocks.field_150431_aC) {
         var5 = 1;
      } else if (var8 != Blocks.field_150395_bd && var8 != Blocks.field_150329_H && var8 != Blocks.field_150330_I) {
         if (var5 == 0) {
            --var3;
         }

         if (var5 == 1) {
            ++var3;
         }

         if (var5 == 2) {
            --var4;
         }

         if (var5 == 3) {
            ++var4;
         }

         if (var5 == 4) {
            --var2;
         }

         if (var5 == 5) {
            ++var2;
         }
      }

      return var1.func_147472_a(this.field_150939_a, var2, var3, var4, false, var5, null, var7);
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      return this.field_150939_a.func_149739_a();
   }

   @Override
   public String func_77658_a() {
      return this.field_150939_a.func_149739_a();
   }

   @Override
   public CreativeTabs func_77640_w() {
      return this.field_150939_a.func_149708_J();
   }

   @Override
   public void func_150895_a(Item var1, CreativeTabs var2, List var3) {
      this.field_150939_a.func_149666_a(var1, var2, var3);
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      String var2 = this.field_150939_a.func_149702_O();
      if (var2 != null) {
         this.field_150938_b = var1.func_94245_a(var2);
      }
   }
}
