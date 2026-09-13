package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRedstoneLight extends Block {
   private final boolean field_150171_a;

   public BlockRedstoneLight(boolean var1) {
      super(Material.field_151591_t);
      this.field_150171_a = var1;
      if (var1) {
         this.func_149715_a(1.0F);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      if (!var1.field_72995_K) {
         if (this.field_150171_a && !var1.func_72864_z(var2, var3, var4)) {
            var1.func_147464_a(var2, var3, var4, this, 4);
         } else if (!this.field_150171_a && var1.func_72864_z(var2, var3, var4)) {
            var1.func_147465_d(var2, var3, var4, Blocks.field_150374_bv, 0, 2);
         }
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K) {
         if (this.field_150171_a && !var1.func_72864_z(var2, var3, var4)) {
            var1.func_147464_a(var2, var3, var4, this, 4);
         } else if (!this.field_150171_a && var1.func_72864_z(var2, var3, var4)) {
            var1.func_147465_d(var2, var3, var4, Blocks.field_150374_bv, 0, 2);
         }
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K && this.field_150171_a && !var1.func_72864_z(var2, var3, var4)) {
         var1.func_147465_d(var2, var3, var4, Blocks.field_150379_bu, 0, 2);
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150379_bu);
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150898_a(Blocks.field_150379_bu);
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      return new ItemStack(Blocks.field_150379_bu);
   }
}
