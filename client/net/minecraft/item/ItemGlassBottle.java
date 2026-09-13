package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item {
   public ItemGlassBottle() {
      super();
      this.func_77637_a(CreativeTabs.field_78038_k);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return Items.field_151068_bn.func_77617_a(0);
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var4 = this.func_77621_a(var2, var3, true);
      if (var4 == null) {
         return var1;
      } else {
         if (var4.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
            int var5 = var4.field_72311_b;
            int var6 = var4.field_72312_c;
            int var7 = var4.field_72309_d;
            if (!var2.func_72962_a(var3, var5, var6, var7)) {
               return var1;
            }

            if (!var3.func_82247_a(var5, var6, var7, var4.field_72310_e, var1)) {
               return var1;
            }

            if (var2.func_147439_a(var5, var6, var7).func_149688_o() == Material.field_151586_h) {
               --var1.field_77994_a;
               if (var1.field_77994_a <= 0) {
                  return new ItemStack(Items.field_151068_bn);
               }

               if (!var3.field_71071_by.func_70441_a(new ItemStack(Items.field_151068_bn))) {
                  var3.func_71019_a(new ItemStack(Items.field_151068_bn, 1, 0), false);
               }
            }
         }

         return var1;
      }
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
   }
}
