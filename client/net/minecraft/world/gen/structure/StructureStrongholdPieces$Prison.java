package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Prison extends StructureStrongholdPieces$Stronghold {
   public StructureStrongholdPieces$Prison() {
      super();
   }

   public StructureStrongholdPieces$Prison(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74986_a((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
   }

   public static StructureStrongholdPieces$Prison func_75016_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -1, 0, 9, 5, 11, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces$Prison(var6, var1, var7, var5) : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 8, 4, 10, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.func_151549_a(var1, var3, 1, 1, 10, 3, 3, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
         this.func_74882_a(var1, var3, 4, 1, 1, 4, 3, 1, false, var2, StructureStrongholdPieces.access$200());
         this.func_74882_a(var1, var3, 4, 1, 3, 4, 3, 3, false, var2, StructureStrongholdPieces.access$200());
         this.func_74882_a(var1, var3, 4, 1, 7, 4, 3, 7, false, var2, StructureStrongholdPieces.access$200());
         this.func_74882_a(var1, var3, 4, 1, 9, 4, 3, 9, false, var2, StructureStrongholdPieces.access$200());
         this.func_151549_a(var1, var3, 4, 1, 4, 4, 3, 6, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
         this.func_151549_a(var1, var3, 5, 1, 5, 7, 3, 5, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
         this.func_151550_a(var1, Blocks.field_150411_aY, 0, 4, 3, 2, var3);
         this.func_151550_a(var1, Blocks.field_150411_aY, 0, 4, 3, 8, var3);
         this.func_151550_a(var1, Blocks.field_150454_av, this.func_151555_a(Blocks.field_150454_av, 3), 4, 1, 2, var3);
         this.func_151550_a(var1, Blocks.field_150454_av, this.func_151555_a(Blocks.field_150454_av, 3) + 8, 4, 2, 2, var3);
         this.func_151550_a(var1, Blocks.field_150454_av, this.func_151555_a(Blocks.field_150454_av, 3), 4, 1, 8, var3);
         this.func_151550_a(var1, Blocks.field_150454_av, this.func_151555_a(Blocks.field_150454_av, 3) + 8, 4, 2, 8, var3);
         return true;
      }
   }
}
