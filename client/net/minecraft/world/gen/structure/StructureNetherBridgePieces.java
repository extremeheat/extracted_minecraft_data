package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;

public class StructureNetherBridgePieces {
   private static final StructureNetherBridgePieces$PieceWeight[] field_78742_a = new StructureNetherBridgePieces$PieceWeight[]{
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Straight.class, 30, 0, true),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Crossing3.class, 10, 4),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Crossing.class, 10, 4),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Stairs.class, 10, 3),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Throne.class, 5, 2),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Entrance.class, 5, 1)
   };
   private static final StructureNetherBridgePieces$PieceWeight[] field_78741_b = new StructureNetherBridgePieces$PieceWeight[]{
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor5.class, 25, 0, true),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Crossing2.class, 15, 5),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor2.class, 5, 10),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor.class, 5, 10),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor3.class, 10, 3, true),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor4.class, 7, 2),
      new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$NetherStalkRoom.class, 5, 2)
   };

   public static void func_143049_a() {
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Crossing3.class, "NeBCr");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$End.class, "NeBEF");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Straight.class, "NeBS");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor3.class, "NeCCS");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor4.class, "NeCTB");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Entrance.class, "NeCE");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Crossing2.class, "NeSCSC");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor.class, "NeSCLT");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor5.class, "NeSC");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor2.class, "NeSCRT");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$NetherStalkRoom.class, "NeCSR");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Throne.class, "NeMT");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Crossing.class, "NeRC");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Stairs.class, "NeSR");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Start.class, "NeStart");
   }

   private static StructureNetherBridgePieces$Piece func_78738_b(
      StructureNetherBridgePieces$PieceWeight var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      Class var8 = var0.field_78828_a;
      Object var9 = null;
      if (var8 == StructureNetherBridgePieces$Straight.class) {
         var9 = StructureNetherBridgePieces$Straight.func_74983_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Crossing3.class) {
         var9 = StructureNetherBridgePieces$Crossing3.func_74966_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Crossing.class) {
         var9 = StructureNetherBridgePieces$Crossing.func_74974_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Stairs.class) {
         var9 = StructureNetherBridgePieces$Stairs.func_74973_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Throne.class) {
         var9 = StructureNetherBridgePieces$Throne.func_74975_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Entrance.class) {
         var9 = StructureNetherBridgePieces$Entrance.func_74984_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Corridor5.class) {
         var9 = StructureNetherBridgePieces$Corridor5.func_74981_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Corridor2.class) {
         var9 = StructureNetherBridgePieces$Corridor2.func_74980_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Corridor.class) {
         var9 = StructureNetherBridgePieces$Corridor.func_74978_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Corridor3.class) {
         var9 = StructureNetherBridgePieces$Corridor3.func_74982_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Corridor4.class) {
         var9 = StructureNetherBridgePieces$Corridor4.func_74985_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$Crossing2.class) {
         var9 = StructureNetherBridgePieces$Crossing2.func_74979_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces$NetherStalkRoom.class) {
         var9 = StructureNetherBridgePieces$NetherStalkRoom.func_74977_a(var1, var2, var3, var4, var5, var6, var7);
      }

      return (StructureNetherBridgePieces$Piece)var9;
   }
}
