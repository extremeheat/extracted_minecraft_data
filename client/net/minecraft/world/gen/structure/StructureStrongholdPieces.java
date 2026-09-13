package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureStrongholdPieces {
   private static final StructureStrongholdPieces$PieceWeight[] field_75205_b = new StructureStrongholdPieces$PieceWeight[]{
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$Straight.class, 40, 0),
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$Prison.class, 5, 5),
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$LeftTurn.class, 20, 0),
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$RightTurn.class, 20, 0),
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$RoomCrossing.class, 10, 6),
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$StairsStraight.class, 5, 5),
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$Stairs.class, 5, 5),
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$Crossing.class, 5, 4),
      new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$ChestCorridor.class, 5, 4),
      new StructureStrongholdPieces$1(StructureStrongholdPieces$Library.class, 10, 2),
      new StructureStrongholdPieces$2(StructureStrongholdPieces$PortalRoom.class, 20, 1)
   };
   private static List field_75206_c;
   private static Class field_75203_d;
   static int field_75207_a;
   private static final StructureStrongholdPieces$Stones field_75204_e = new StructureStrongholdPieces$Stones(null);

   public static void func_143046_a() {
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$ChestCorridor.class, "SHCC");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Corridor.class, "SHFC");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Crossing.class, "SH5C");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$LeftTurn.class, "SHLT");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Library.class, "SHLi");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$PortalRoom.class, "SHPR");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Prison.class, "SHPH");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$RightTurn.class, "SHRT");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$RoomCrossing.class, "SHRC");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Stairs.class, "SHSD");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Stairs2.class, "SHStart");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Straight.class, "SHS");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces$StairsStraight.class, "SHSSD");
   }

   public static void func_75198_a() {
      field_75206_c = new ArrayList();

      for(StructureStrongholdPieces$PieceWeight var3 : field_75205_b) {
         var3.field_75193_c = 0;
         field_75206_c.add(var3);
      }

      field_75203_d = null;
   }

   private static boolean func_75202_c() {
      boolean var0 = false;
      field_75207_a = 0;

      for(StructureStrongholdPieces$PieceWeight var2 : field_75206_c) {
         if (var2.field_75191_d > 0 && var2.field_75193_c < var2.field_75191_d) {
            var0 = true;
         }

         field_75207_a += var2.field_75192_b;
      }

      return var0;
   }

   private static StructureStrongholdPieces$Stronghold func_75200_a(Class var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
      Object var8 = null;
      if (var0 == StructureStrongholdPieces$Straight.class) {
         var8 = StructureStrongholdPieces$Straight.func_75018_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$Prison.class) {
         var8 = StructureStrongholdPieces$Prison.func_75016_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$LeftTurn.class) {
         var8 = StructureStrongholdPieces$LeftTurn.func_75010_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$RightTurn.class) {
         var8 = StructureStrongholdPieces$RightTurn.func_75010_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$RoomCrossing.class) {
         var8 = StructureStrongholdPieces$RoomCrossing.func_75012_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$StairsStraight.class) {
         var8 = StructureStrongholdPieces$StairsStraight.func_75028_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$Stairs.class) {
         var8 = StructureStrongholdPieces$Stairs.func_75022_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$Crossing.class) {
         var8 = StructureStrongholdPieces$Crossing.func_74994_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$ChestCorridor.class) {
         var8 = StructureStrongholdPieces$ChestCorridor.func_75000_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$Library.class) {
         var8 = StructureStrongholdPieces$Library.func_75006_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces$PortalRoom.class) {
         var8 = StructureStrongholdPieces$PortalRoom.func_75004_a(var1, var2, var3, var4, var5, var6, var7);
      }

      return (StructureStrongholdPieces$Stronghold)var8;
   }

   private static StructureStrongholdPieces$Stronghold func_75201_b(
      StructureStrongholdPieces$Stairs2 var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      if (!func_75202_c()) {
         return null;
      } else {
         if (field_75203_d != null) {
            StructureStrongholdPieces$Stronghold var8 = func_75200_a(field_75203_d, var1, var2, var3, var4, var5, var6, var7);
            field_75203_d = null;
            if (var8 != null) {
               return var8;
            }
         }

         int var13 = 0;

         while(var13 < 5) {
            ++var13;
            int var9 = var2.nextInt(field_75207_a);

            for(StructureStrongholdPieces$PieceWeight var11 : field_75206_c) {
               var9 -= var11.field_75192_b;
               if (var9 < 0) {
                  if (!var11.func_75189_a(var7) || var11 == var0.field_75027_a) {
                     break;
                  }

                  StructureStrongholdPieces$Stronghold var12 = func_75200_a(var11.field_75194_a, var1, var2, var3, var4, var5, var6, var7);
                  if (var12 != null) {
                     ++var11.field_75193_c;
                     var0.field_75027_a = var11;
                     if (!var11.func_75190_a()) {
                        field_75206_c.remove(var11);
                     }

                     return var12;
                  }
               }
            }
         }

         StructureBoundingBox var14 = StructureStrongholdPieces$Corridor.func_74992_a(var1, var2, var3, var4, var5, var6);
         return var14 != null && var14.field_78895_b > 1 ? new StructureStrongholdPieces$Corridor(var7, var2, var14, var6) : null;
      }
   }

   private static StructureComponent func_75196_c(
      StructureStrongholdPieces$Stairs2 var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      if (var7 > 50) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 112 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 112) {
         StructureStrongholdPieces$Stronghold var8 = func_75201_b(var0, var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            var1.add(var8);
            var0.field_75026_c.add(var8);
         }

         return var8;
      } else {
         return null;
      }
   }
}
