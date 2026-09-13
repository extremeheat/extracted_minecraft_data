package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Straight extends StructureStrongholdPieces$Stronghold {
   private boolean field_75019_b;
   private boolean field_75020_c;

   public StructureStrongholdPieces$Straight() {
      super();
   }

   public StructureStrongholdPieces$Straight(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
      this.field_75019_b = var2.nextInt(2) == 0;
      this.field_75020_c = var2.nextInt(2) == 0;
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Left", this.field_75019_b);
      var1.func_74757_a("Right", this.field_75020_c);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_75019_b = var1.func_74767_n("Left");
      this.field_75020_c = var1.func_74767_n("Right");
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74986_a((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
      if (this.field_75019_b) {
         this.func_74989_b((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 2);
      }

      if (this.field_75020_c) {
         this.func_74987_c((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 2);
      }
   }

   public static StructureStrongholdPieces$Straight func_75018_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces$Straight(var6, var1, var7, var5) : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 6, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.func_74990_a(var1, var2, var3, StructureStrongholdPieces$Stronghold$Door.OPENING, 1, 1, 6);
         this.func_151552_a(var1, var3, var2, 0.1F, 1, 2, 1, Blocks.field_150478_aa, 0);
         this.func_151552_a(var1, var3, var2, 0.1F, 3, 2, 1, Blocks.field_150478_aa, 0);
         this.func_151552_a(var1, var3, var2, 0.1F, 1, 2, 5, Blocks.field_150478_aa, 0);
         this.func_151552_a(var1, var3, var2, 0.1F, 3, 2, 5, Blocks.field_150478_aa, 0);
         if (this.field_75019_b) {
            this.func_151549_a(var1, var3, 0, 1, 2, 0, 3, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }

         if (this.field_75020_c) {
            this.func_151549_a(var1, var3, 4, 1, 2, 4, 3, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }

         return true;
      }
   }
}
