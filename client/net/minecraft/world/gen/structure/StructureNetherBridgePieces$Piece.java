package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;

abstract class StructureNetherBridgePieces$Piece extends StructureComponent {
   protected static final WeightedRandomChestContent[] field_111019_a = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 5),
      new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 5),
      new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.field_151010_B, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151171_ah, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151033_d, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151075_bm, 0, 3, 7, 5),
      new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 8),
      new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 3)
   };

   public StructureNetherBridgePieces$Piece() {
      super();
   }

   protected StructureNetherBridgePieces$Piece(int var1) {
      super(var1);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
   }

   private int func_74960_a(List var1) {
      boolean var2 = false;
      int var3 = 0;

      for(StructureNetherBridgePieces$PieceWeight var5 : var1) {
         if (var5.field_78824_d > 0 && var5.field_78827_c < var5.field_78824_d) {
            var2 = true;
         }

         var3 += var5.field_78826_b;
      }

      return var2 ? var3 : -1;
   }

   private StructureNetherBridgePieces$Piece func_74959_a(
      StructureNetherBridgePieces$Start var1, List var2, List var3, Random var4, int var5, int var6, int var7, int var8, int var9
   ) {
      int var10 = this.func_74960_a(var2);
      boolean var11 = var10 > 0 && var9 <= 30;
      int var12 = 0;

      while(var12 < 5 && var11) {
         ++var12;
         int var13 = var4.nextInt(var10);

         for(StructureNetherBridgePieces$PieceWeight var15 : var2) {
            var13 -= var15.field_78826_b;
            if (var13 < 0) {
               if (!var15.func_78822_a(var9) || var15 == var1.field_74970_a && !var15.field_78825_e) {
                  break;
               }

               StructureNetherBridgePieces$Piece var16 = StructureNetherBridgePieces.access$000(var15, var3, var4, var5, var6, var7, var8, var9);
               if (var16 != null) {
                  ++var15.field_78827_c;
                  var1.field_74970_a = var15;
                  if (!var15.func_78823_a()) {
                     var2.remove(var15);
                  }

                  return var16;
               }
            }
         }
      }

      return StructureNetherBridgePieces$End.func_74971_a(var3, var4, var5, var6, var7, var8, var9);
   }

   private StructureComponent func_74962_a(
      StructureNetherBridgePieces$Start var1, List var2, Random var3, int var4, int var5, int var6, int var7, int var8, boolean var9
   ) {
      if (Math.abs(var4 - var1.func_74874_b().field_78897_a) <= 112 && Math.abs(var6 - var1.func_74874_b().field_78896_c) <= 112) {
         List var10 = var1.field_74968_b;
         if (var9) {
            var10 = var1.field_74969_c;
         }

         StructureNetherBridgePieces$Piece var11 = this.func_74959_a(var1, var10, var2, var3, var4, var5, var6, var7, var8 + 1);
         if (var11 != null) {
            var2.add(var11);
            var1.field_74967_d.add(var11);
         }

         return var11;
      } else {
         return StructureNetherBridgePieces$End.func_74971_a(var2, var3, var4, var5, var6, var7, var8);
      }
   }

   protected StructureComponent func_74963_a(StructureNetherBridgePieces$Start var1, List var2, Random var3, int var4, int var5, boolean var6) {
      switch(this.field_74885_f) {
         case 0:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var4,
               this.field_74887_e.field_78895_b + var5,
               this.field_74887_e.field_78892_f + 1,
               this.field_74885_f,
               this.func_74877_c(),
               var6
            );
         case 1:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a - 1,
               this.field_74887_e.field_78895_b + var5,
               this.field_74887_e.field_78896_c + var4,
               this.field_74885_f,
               this.func_74877_c(),
               var6
            );
         case 2:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var4,
               this.field_74887_e.field_78895_b + var5,
               this.field_74887_e.field_78896_c - 1,
               this.field_74885_f,
               this.func_74877_c(),
               var6
            );
         case 3:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78893_d + 1,
               this.field_74887_e.field_78895_b + var5,
               this.field_74887_e.field_78896_c + var4,
               this.field_74885_f,
               this.func_74877_c(),
               var6
            );
         default:
            return null;
      }
   }

   protected StructureComponent func_74961_b(StructureNetherBridgePieces$Start var1, List var2, Random var3, int var4, int var5, boolean var6) {
      switch(this.field_74885_f) {
         case 0:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a - 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               1,
               this.func_74877_c(),
               var6
            );
         case 1:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c - 1,
               2,
               this.func_74877_c(),
               var6
            );
         case 2:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a - 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               1,
               this.func_74877_c(),
               var6
            );
         case 3:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c - 1,
               2,
               this.func_74877_c(),
               var6
            );
         default:
            return null;
      }
   }

   protected StructureComponent func_74965_c(StructureNetherBridgePieces$Start var1, List var2, Random var3, int var4, int var5, boolean var6) {
      switch(this.field_74885_f) {
         case 0:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78893_d + 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               3,
               this.func_74877_c(),
               var6
            );
         case 1:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78892_f + 1,
               0,
               this.func_74877_c(),
               var6
            );
         case 2:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78893_d + 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               3,
               this.func_74877_c(),
               var6
            );
         case 3:
            return this.func_74962_a(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78892_f + 1,
               0,
               this.func_74877_c(),
               var6
            );
         default:
            return null;
      }
   }

   protected static boolean func_74964_a(StructureBoundingBox var0) {
      return var0 != null && var0.field_78895_b > 10;
   }
}
