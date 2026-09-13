package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.ChunkPosition;

public class StructureStrongholdPieces$Stairs2 extends StructureStrongholdPieces$Stairs {
   public StructureStrongholdPieces$PieceWeight field_75027_a;
   public StructureStrongholdPieces$PortalRoom field_75025_b;
   public List field_75026_c = new ArrayList();

   public StructureStrongholdPieces$Stairs2() {
      super();
   }

   public StructureStrongholdPieces$Stairs2(int var1, Random var2, int var3, int var4) {
      super(0, var2, var3, var4);
   }

   @Override
   public ChunkPosition func_151553_a() {
      return this.field_75025_b != null ? this.field_75025_b.func_151553_a() : super.func_151553_a();
   }
}
