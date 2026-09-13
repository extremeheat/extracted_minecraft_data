package net.minecraft.world.gen.structure;

public class StructureVillagePieces$PieceWeight {
   public Class field_75090_a;
   public final int field_75088_b;
   public int field_75089_c;
   public int field_75087_d;

   public StructureVillagePieces$PieceWeight(Class var1, int var2, int var3) {
      super();
      this.field_75090_a = var1;
      this.field_75088_b = var2;
      this.field_75087_d = var3;
   }

   public boolean func_75085_a(int var1) {
      return this.field_75087_d == 0 || this.field_75089_c < this.field_75087_d;
   }

   public boolean func_75086_a() {
      return this.field_75087_d == 0 || this.field_75089_c < this.field_75087_d;
   }
}
