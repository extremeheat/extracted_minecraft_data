package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ComponentScatteredFeaturePieces$SwampHut extends ComponentScatteredFeaturePieces$Feature {
   private boolean field_82682_h;

   public ComponentScatteredFeaturePieces$SwampHut() {
      super();
   }

   public ComponentScatteredFeaturePieces$SwampHut(Random var1, int var2, int var3) {
      super(var1, var2, 64, var3, 7, 5, 9);
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Witch", this.field_82682_h);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_82682_h = var1.func_74767_n("Witch");
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (!this.func_74935_a(var1, var3, 0)) {
         return false;
      } else {
         this.func_151556_a(var1, var3, 1, 1, 1, 5, 1, 7, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
         this.func_151556_a(var1, var3, 1, 4, 2, 5, 4, 7, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
         this.func_151556_a(var1, var3, 2, 1, 0, 4, 1, 0, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
         this.func_151556_a(var1, var3, 2, 2, 2, 3, 3, 2, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
         this.func_151556_a(var1, var3, 1, 2, 3, 1, 3, 6, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
         this.func_151556_a(var1, var3, 5, 2, 3, 5, 3, 6, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
         this.func_151556_a(var1, var3, 2, 2, 7, 4, 3, 7, Blocks.field_150344_f, 1, Blocks.field_150344_f, 1, false);
         this.func_151549_a(var1, var3, 1, 0, 2, 1, 3, 2, Blocks.field_150364_r, Blocks.field_150364_r, false);
         this.func_151549_a(var1, var3, 5, 0, 2, 5, 3, 2, Blocks.field_150364_r, Blocks.field_150364_r, false);
         this.func_151549_a(var1, var3, 1, 0, 7, 1, 3, 7, Blocks.field_150364_r, Blocks.field_150364_r, false);
         this.func_151549_a(var1, var3, 5, 0, 7, 5, 3, 7, Blocks.field_150364_r, Blocks.field_150364_r, false);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 2, 3, 2, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 3, 3, 7, var3);
         this.func_151550_a(var1, Blocks.field_150350_a, 0, 1, 3, 4, var3);
         this.func_151550_a(var1, Blocks.field_150350_a, 0, 5, 3, 4, var3);
         this.func_151550_a(var1, Blocks.field_150350_a, 0, 5, 3, 5, var3);
         this.func_151550_a(var1, Blocks.field_150457_bL, 7, 1, 3, 5, var3);
         this.func_151550_a(var1, Blocks.field_150462_ai, 0, 3, 2, 6, var3);
         this.func_151550_a(var1, Blocks.field_150383_bp, 0, 4, 2, 6, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 2, 1, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 5, 2, 1, var3);
         int var4 = this.func_151555_a(Blocks.field_150476_ad, 3);
         int var5 = this.func_151555_a(Blocks.field_150476_ad, 1);
         int var6 = this.func_151555_a(Blocks.field_150476_ad, 0);
         int var7 = this.func_151555_a(Blocks.field_150476_ad, 2);
         this.func_151556_a(var1, var3, 0, 4, 1, 6, 4, 1, Blocks.field_150485_bF, var4, Blocks.field_150485_bF, var4, false);
         this.func_151556_a(var1, var3, 0, 4, 2, 0, 4, 7, Blocks.field_150485_bF, var6, Blocks.field_150485_bF, var6, false);
         this.func_151556_a(var1, var3, 6, 4, 2, 6, 4, 7, Blocks.field_150485_bF, var5, Blocks.field_150485_bF, var5, false);
         this.func_151556_a(var1, var3, 0, 4, 8, 6, 4, 8, Blocks.field_150485_bF, var7, Blocks.field_150485_bF, var7, false);

         for(int var8 = 2; var8 <= 7; var8 += 5) {
            for(int var9 = 1; var9 <= 5; var9 += 4) {
               this.func_151554_b(var1, Blocks.field_150364_r, 0, var9, -1, var8, var3);
            }
         }

         if (!this.field_82682_h) {
            int var12 = this.func_74865_a(2, 5);
            int var13 = this.func_74862_a(2);
            int var10 = this.func_74873_b(2, 5);
            if (var3.func_78890_b(var12, var13, var10)) {
               this.field_82682_h = true;
               EntityWitch var11 = new EntityWitch(var1);
               var11.func_70012_b((double)var12 + 0.5, (double)var13, (double)var10 + 0.5, 0.0F, 0.0F);
               var11.func_110161_a(null);
               var1.func_72838_d(var11);
            }
         }

         return true;
      }
   }
}
