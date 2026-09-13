package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;

public class StructureMineshaftPieces {
   private static final WeightedRandomChestContent[] field_78818_a = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10),
      new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5),
      new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5),
      new WeightedRandomChestContent(Items.field_151100_aR, 4, 4, 9, 5),
      new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 2, 3),
      new WeightedRandomChestContent(Items.field_151044_h, 0, 3, 8, 10),
      new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 1),
      new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150448_aq), 0, 4, 8, 1),
      new WeightedRandomChestContent(Items.field_151081_bc, 0, 2, 4, 10),
      new WeightedRandomChestContent(Items.field_151080_bb, 0, 2, 4, 10),
      new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3),
      new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1)
   };

   public static void func_143048_a() {
      MapGenStructureIO.func_143031_a(StructureMineshaftPieces$Corridor.class, "MSCorridor");
      MapGenStructureIO.func_143031_a(StructureMineshaftPieces$Cross.class, "MSCrossing");
      MapGenStructureIO.func_143031_a(StructureMineshaftPieces$Room.class, "MSRoom");
      MapGenStructureIO.func_143031_a(StructureMineshaftPieces$Stairs.class, "MSStairs");
   }

   private static StructureComponent func_78815_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.nextInt(100);
      if (var7 >= 80) {
         StructureBoundingBox var8 = StructureMineshaftPieces$Cross.func_74951_a(var0, var1, var2, var3, var4, var5);
         if (var8 != null) {
            return new StructureMineshaftPieces$Cross(var6, var1, var8, var5);
         }
      } else if (var7 >= 70) {
         StructureBoundingBox var9 = StructureMineshaftPieces$Stairs.func_74950_a(var0, var1, var2, var3, var4, var5);
         if (var9 != null) {
            return new StructureMineshaftPieces$Stairs(var6, var1, var9, var5);
         }
      } else {
         StructureBoundingBox var10 = StructureMineshaftPieces$Corridor.func_74954_a(var0, var1, var2, var3, var4, var5);
         if (var10 != null) {
            return new StructureMineshaftPieces$Corridor(var6, var1, var10, var5);
         }
      }

      return null;
   }

   private static StructureComponent func_78817_b(StructureComponent var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
      if (var7 > 8) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 80 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 80) {
         StructureComponent var8 = func_78815_a(var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            var1.add(var8);
            var8.func_74861_a(var0, var1, var2);
         }

         return var8;
      } else {
         return null;
      }
   }
}
