package net.minecraft.world.gen.structure;

class StructureStrongholdPieces$PieceWeight {
   public Class field_75194_a;
   public final int field_75192_b;
   public int field_75193_c;
   public int field_75191_d;

   public StructureStrongholdPieces$PieceWeight(Class var1, int var2, int var3) {
      super();
      this.field_75194_a = var1;
      this.field_75192_b = var2;
      this.field_75191_d = var3;
   }

   public boolean func_75189_a(int var1) {
      return this.field_75191_d == 0 || this.field_75193_c < this.field_75191_d;
   }

   public boolean func_75190_a() {
      return this.field_75191_d == 0 || this.field_75193_c < this.field_75191_d;
   }
}
