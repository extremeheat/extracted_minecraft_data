package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;

public class StructureNetherBridgePieces$Start extends StructureNetherBridgePieces$Crossing3 {
   public StructureNetherBridgePieces$PieceWeight field_74970_a;
   public List field_74968_b;
   public List field_74969_c;
   public ArrayList field_74967_d = new ArrayList();

   public StructureNetherBridgePieces$Start() {
      super();
   }

   public StructureNetherBridgePieces$Start(Random var1, int var2, int var3) {
      super(var1, var2, var3);
      this.field_74968_b = new ArrayList();

      for(StructureNetherBridgePieces$PieceWeight var7 : StructureNetherBridgePieces.access$100()) {
         var7.field_78827_c = 0;
         this.field_74968_b.add(var7);
      }

      this.field_74969_c = new ArrayList();

      for(StructureNetherBridgePieces$PieceWeight var11 : StructureNetherBridgePieces.access$200()) {
         var11.field_78827_c = 0;
         this.field_74969_c.add(var11);
      }
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
   }
}
