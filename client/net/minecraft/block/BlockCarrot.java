package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class BlockCarrot extends BlockCrops {
   private IIcon[] field_149868_a;

   public BlockCarrot() {
      super();
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 < 7) {
         if (var2 == 6) {
            var2 = 5;
         }

         return this.field_149868_a[var2 >> 1];
      } else {
         return this.field_149868_a[3];
      }
   }

   @Override
   protected Item func_149866_i() {
      return Items.field_151172_bF;
   }

   @Override
   protected Item func_149865_P() {
      return Items.field_151172_bF;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149868_a = new IIcon[4];

      for(int var2 = 0; var2 < this.field_149868_a.length; ++var2) {
         this.field_149868_a[var2] = var1.func_94245_a(this.func_149641_N() + "_stage_" + var2);
      }
   }
}
