package net.minecraft.world.gen.structure;

final class StructureStrongholdPieces$1 extends StructureStrongholdPieces$PieceWeight {
   StructureStrongholdPieces$1(Class var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean func_75189_a(int var1) {
      return super.func_75189_a(var1) && var1 > 4;
   }
}
