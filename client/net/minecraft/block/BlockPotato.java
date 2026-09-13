package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockPotato extends BlockCrops {
   private IIcon[] field_149869_a;

   public BlockPotato() {
      super();
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 < 7) {
         if (var2 == 6) {
            var2 = 5;
         }

         return this.field_149869_a[var2 >> 1];
      } else {
         return this.field_149869_a[3];
      }
   }

   @Override
   protected Item func_149866_i() {
      return Items.field_151174_bG;
   }

   @Override
   protected Item func_149865_P() {
      return Items.field_151174_bG;
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      super.func_149690_a(var1, var2, var3, var4, var5, var6, var7);
      if (!var1.field_72995_K) {
         if (var5 >= 7 && var1.field_73012_v.nextInt(50) == 0) {
            this.func_149642_a(var1, var2, var3, var4, new ItemStack(Items.field_151170_bI));
         }
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149869_a = new IIcon[4];

      for(int var2 = 0; var2 < this.field_149869_a.length; ++var2) {
         this.field_149869_a[var2] = var1.func_94245_a(this.func_149641_N() + "_stage_" + var2);
      }
   }
}
