package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Library extends StructureStrongholdPieces$Stronghold {
   private static final WeightedRandomChestContent[] field_75007_b = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151122_aG, 0, 1, 3, 20),
      new WeightedRandomChestContent(Items.field_151121_aF, 0, 2, 7, 20),
      new WeightedRandomChestContent(Items.field_151148_bJ, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151111_aL, 0, 1, 1, 1)
   };
   private boolean field_75008_c;

   public StructureStrongholdPieces$Library() {
      super();
   }

   public StructureStrongholdPieces$Library(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
      this.field_75008_c = var3.func_78882_c() > 6;
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Tall", this.field_75008_c);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_75008_c = var1.func_74767_n("Tall");
   }

   public static StructureStrongholdPieces$Library func_75006_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -4, -1, 0, 14, 11, 15, var5);
      if (!func_74991_a(var7) || StructureComponent.func_74883_a(var0, var7) != null) {
         var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -4, -1, 0, 14, 6, 15, var5);
         if (!func_74991_a(var7) || StructureComponent.func_74883_a(var0, var7) != null) {
            return null;
         }
      }

      return new StructureStrongholdPieces$Library(var6, var1, var7, var5);
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         byte var4 = 11;
         if (!this.field_75008_c) {
            var4 = 6;
         }

         this.func_74882_a(var1, var3, 0, 0, 0, 13, var4 - 1, 14, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 1, 0);
         this.func_151551_a(var1, var3, var2, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.field_150321_G, Blocks.field_150321_G, false);
         boolean var5 = true;
         boolean var6 = true;

         for(int var7 = 1; var7 <= 13; ++var7) {
            if ((var7 - 1) % 4 == 0) {
               this.func_151549_a(var1, var3, 1, 1, var7, 1, 4, var7, Blocks.field_150344_f, Blocks.field_150344_f, false);
               this.func_151549_a(var1, var3, 12, 1, var7, 12, 4, var7, Blocks.field_150344_f, Blocks.field_150344_f, false);
               this.func_151550_a(var1, Blocks.field_150478_aa, 0, 2, 3, var7, var3);
               this.func_151550_a(var1, Blocks.field_150478_aa, 0, 11, 3, var7, var3);
               if (this.field_75008_c) {
                  this.func_151549_a(var1, var3, 1, 6, var7, 1, 9, var7, Blocks.field_150344_f, Blocks.field_150344_f, false);
                  this.func_151549_a(var1, var3, 12, 6, var7, 12, 9, var7, Blocks.field_150344_f, Blocks.field_150344_f, false);
               }
            } else {
               this.func_151549_a(var1, var3, 1, 1, var7, 1, 4, var7, Blocks.field_150342_X, Blocks.field_150342_X, false);
               this.func_151549_a(var1, var3, 12, 1, var7, 12, 4, var7, Blocks.field_150342_X, Blocks.field_150342_X, false);
               if (this.field_75008_c) {
                  this.func_151549_a(var1, var3, 1, 6, var7, 1, 9, var7, Blocks.field_150342_X, Blocks.field_150342_X, false);
                  this.func_151549_a(var1, var3, 12, 6, var7, 12, 9, var7, Blocks.field_150342_X, Blocks.field_150342_X, false);
               }
            }
         }

         for(int var10 = 3; var10 < 12; var10 += 2) {
            this.func_151549_a(var1, var3, 3, 1, var10, 4, 3, var10, Blocks.field_150342_X, Blocks.field_150342_X, false);
            this.func_151549_a(var1, var3, 6, 1, var10, 7, 3, var10, Blocks.field_150342_X, Blocks.field_150342_X, false);
            this.func_151549_a(var1, var3, 9, 1, var10, 10, 3, var10, Blocks.field_150342_X, Blocks.field_150342_X, false);
         }

         if (this.field_75008_c) {
            this.func_151549_a(var1, var3, 1, 5, 1, 3, 5, 13, Blocks.field_150344_f, Blocks.field_150344_f, false);
            this.func_151549_a(var1, var3, 10, 5, 1, 12, 5, 13, Blocks.field_150344_f, Blocks.field_150344_f, false);
            this.func_151549_a(var1, var3, 4, 5, 1, 9, 5, 2, Blocks.field_150344_f, Blocks.field_150344_f, false);
            this.func_151549_a(var1, var3, 4, 5, 12, 9, 5, 13, Blocks.field_150344_f, Blocks.field_150344_f, false);
            this.func_151550_a(var1, Blocks.field_150344_f, 0, 9, 5, 11, var3);
            this.func_151550_a(var1, Blocks.field_150344_f, 0, 8, 5, 11, var3);
            this.func_151550_a(var1, Blocks.field_150344_f, 0, 9, 5, 10, var3);
            this.func_151549_a(var1, var3, 3, 6, 2, 3, 6, 12, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
            this.func_151549_a(var1, var3, 10, 6, 2, 10, 6, 10, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
            this.func_151549_a(var1, var3, 4, 6, 2, 9, 6, 2, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
            this.func_151549_a(var1, var3, 4, 6, 12, 8, 6, 12, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 9, 6, 11, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 8, 6, 11, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 9, 6, 10, var3);
            int var11 = this.func_151555_a(Blocks.field_150468_ap, 3);
            this.func_151550_a(var1, Blocks.field_150468_ap, var11, 10, 1, 13, var3);
            this.func_151550_a(var1, Blocks.field_150468_ap, var11, 10, 2, 13, var3);
            this.func_151550_a(var1, Blocks.field_150468_ap, var11, 10, 3, 13, var3);
            this.func_151550_a(var1, Blocks.field_150468_ap, var11, 10, 4, 13, var3);
            this.func_151550_a(var1, Blocks.field_150468_ap, var11, 10, 5, 13, var3);
            this.func_151550_a(var1, Blocks.field_150468_ap, var11, 10, 6, 13, var3);
            this.func_151550_a(var1, Blocks.field_150468_ap, var11, 10, 7, 13, var3);
            byte var8 = 7;
            byte var9 = 7;
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8 - 1, 9, var9, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8, 9, var9, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8 - 1, 8, var9, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8, 8, var9, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8 - 1, 7, var9, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8, 7, var9, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8 - 2, 7, var9, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8 + 1, 7, var9, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8 - 1, 7, var9 - 1, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8 - 1, 7, var9 + 1, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8, 7, var9 - 1, var3);
            this.func_151550_a(var1, Blocks.field_150422_aJ, 0, var8, 7, var9 + 1, var3);
            this.func_151550_a(var1, Blocks.field_150478_aa, 0, var8 - 2, 8, var9, var3);
            this.func_151550_a(var1, Blocks.field_150478_aa, 0, var8 + 1, 8, var9, var3);
            this.func_151550_a(var1, Blocks.field_150478_aa, 0, var8 - 1, 8, var9 - 1, var3);
            this.func_151550_a(var1, Blocks.field_150478_aa, 0, var8 - 1, 8, var9 + 1, var3);
            this.func_151550_a(var1, Blocks.field_150478_aa, 0, var8, 8, var9 - 1, var3);
            this.func_151550_a(var1, Blocks.field_150478_aa, 0, var8, 8, var9 + 1, var3);
         }

         this.func_74879_a(
            var1,
            var3,
            var2,
            3,
            3,
            5,
            WeightedRandomChestContent.func_92080_a(field_75007_b, Items.field_151134_bR.func_92112_a(var2, 1, 5, 2)),
            1 + var2.nextInt(4)
         );
         if (this.field_75008_c) {
            this.func_151550_a(var1, Blocks.field_150350_a, 0, 12, 9, 1, var3);
            this.func_74879_a(
               var1,
               var3,
               var2,
               12,
               8,
               1,
               WeightedRandomChestContent.func_92080_a(field_75007_b, Items.field_151134_bR.func_92112_a(var2, 1, 5, 2)),
               1 + var2.nextInt(4)
            );
         }

         return true;
      }
   }
}
