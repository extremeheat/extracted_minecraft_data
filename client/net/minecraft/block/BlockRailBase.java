package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   protected final boolean field_150053_a;

   public static final boolean func_150049_b_(World var0, int var1, int var2, int var3) {
      return func_150051_a(var0.func_147439_a(var1, var2, var3));
   }

   public static final boolean func_150051_a(Block var0) {
      return var0 == Blocks.field_150448_aq || var0 == Blocks.field_150318_D || var0 == Blocks.field_150319_E || var0 == Blocks.field_150408_cc;
   }

   protected BlockRailBase(boolean var1) {
      super(Material.field_151594_q);
      this.field_150053_a = var1;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.func_149647_a(CreativeTabs.field_78029_e);
   }

   public boolean func_150050_e() {
      return this.field_150053_a;
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public MovingObjectPosition func_149731_a(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149731_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if (var5 >= 2 && var5 <= 5) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      }
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 9;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 1;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return World.func_147466_a(var1, var2, var3 - 1, var4);
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      if (!var1.field_72995_K) {
         this.func_150052_a(var1, var2, var3, var4, true);
         if (this.field_150053_a) {
            this.func_149695_a(var1, var2, var3, var4, this);
         }
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         int var7 = var6;
         if (this.field_150053_a) {
            var7 = var6 & 7;
         }

         boolean var8 = false;
         if (!World.func_147466_a(var1, var2, var3 - 1, var4)) {
            var8 = true;
         }

         if (var7 == 2 && !World.func_147466_a(var1, var2 + 1, var3, var4)) {
            var8 = true;
         }

         if (var7 == 3 && !World.func_147466_a(var1, var2 - 1, var3, var4)) {
            var8 = true;
         }

         if (var7 == 4 && !World.func_147466_a(var1, var2, var3, var4 - 1)) {
            var8 = true;
         }

         if (var7 == 5 && !World.func_147466_a(var1, var2, var3, var4 + 1)) {
            var8 = true;
         }

         if (var8) {
            this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
            var1.func_147468_f(var2, var3, var4);
         } else {
            this.func_150048_a(var1, var2, var3, var4, var6, var7, var5);
         }
      }
   }

   protected void func_150048_a(World var1, int var2, int var3, int var4, int var5, int var6, Block var7) {
   }

   protected void func_150052_a(World var1, int var2, int var3, int var4, boolean var5) {
      if (!var1.field_72995_K) {
         new BlockRailBase$Rail(this, var1, var2, var3, var4).func_150655_a(var1.func_72864_z(var2, var3, var4), var5);
      }
   }

   @Override
   public int func_149656_h() {
      return 0;
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      int var7 = var6;
      if (this.field_150053_a) {
         var7 = var6 & 7;
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
      if (var7 == 2 || var7 == 3 || var7 == 4 || var7 == 5) {
         var1.func_147459_d(var2, var3 + 1, var4, var5);
      }

      if (this.field_150053_a) {
         var1.func_147459_d(var2, var3, var4, var5);
         var1.func_147459_d(var2, var3 - 1, var4, var5);
      }
   }
}
