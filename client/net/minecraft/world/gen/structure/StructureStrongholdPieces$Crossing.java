package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Crossing extends StructureStrongholdPieces$Stronghold {
   private boolean field_74996_b;
   private boolean field_74997_c;
   private boolean field_74995_d;
   private boolean field_74999_h;

   public StructureStrongholdPieces$Crossing() {
      super();
   }

   public StructureStrongholdPieces$Crossing(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
      this.field_74996_b = var2.nextBoolean();
      this.field_74997_c = var2.nextBoolean();
      this.field_74995_d = var2.nextBoolean();
      this.field_74999_h = var2.nextInt(3) > 0;
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("leftLow", this.field_74996_b);
      var1.func_74757_a("leftHigh", this.field_74997_c);
      var1.func_74757_a("rightLow", this.field_74995_d);
      var1.func_74757_a("rightHigh", this.field_74999_h);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74996_b = var1.func_74767_n("leftLow");
      this.field_74997_c = var1.func_74767_n("leftHigh");
      this.field_74995_d = var1.func_74767_n("rightLow");
      this.field_74999_h = var1.func_74767_n("rightHigh");
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      int var4 = 3;
      int var5 = 5;
      if (this.field_74885_f == 1 || this.field_74885_f == 2) {
         var4 = 8 - var4;
         var5 = 8 - var5;
      }

      this.func_74986_a((StructureStrongholdPieces$Stairs2)var1, var2, var3, 5, 1);
      if (this.field_74996_b) {
         this.func_74989_b((StructureStrongholdPieces$Stairs2)var1, var2, var3, var4, 1);
      }

      if (this.field_74997_c) {
         this.func_74989_b((StructureStrongholdPieces$Stairs2)var1, var2, var3, var5, 7);
      }

      if (this.field_74995_d) {
         this.func_74987_c((StructureStrongholdPieces$Stairs2)var1, var2, var3, var4, 1);
      }

      if (this.field_74999_h) {
         this.func_74987_c((StructureStrongholdPieces$Stairs2)var1, var2, var3, var5, 7);
      }
   }

   public static StructureStrongholdPieces$Crossing func_74994_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -4, -3, 0, 10, 9, 11, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces$Crossing(var6, var1, var7, var5) : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 9, 8, 10, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 3, 0);
         if (this.field_74996_b) {
            this.func_151549_a(var1, var3, 0, 3, 1, 0, 5, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }

         if (this.field_74995_d) {
            this.func_151549_a(var1, var3, 9, 3, 1, 9, 5, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }

         if (this.field_74997_c) {
            this.func_151549_a(var1, var3, 0, 5, 7, 0, 7, 9, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }

         if (this.field_74999_h) {
            this.func_151549_a(var1, var3, 9, 5, 7, 9, 7, 9, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }

         this.func_151549_a(var1, var3, 5, 1, 10, 7, 3, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
         this.func_74882_a(var1, var3, 1, 2, 1, 8, 2, 6, false, var2, StructureStrongholdPieces.access$200());
         this.func_74882_a(var1, var3, 4, 1, 5, 4, 4, 9, false, var2, StructureStrongholdPieces.access$200());
         this.func_74882_a(var1, var3, 8, 1, 5, 8, 4, 9, false, var2, StructureStrongholdPieces.access$200());
         this.func_74882_a(var1, var3, 1, 4, 7, 3, 4, 9, false, var2, StructureStrongholdPieces.access$200());
         this.func_74882_a(var1, var3, 1, 3, 5, 3, 3, 6, false, var2, StructureStrongholdPieces.access$200());
         this.func_151549_a(var1, var3, 1, 3, 4, 3, 3, 4, Blocks.field_150333_U, Blocks.field_150333_U, false);
         this.func_151549_a(var1, var3, 1, 4, 6, 3, 4, 6, Blocks.field_150333_U, Blocks.field_150333_U, false);
         this.func_74882_a(var1, var3, 5, 1, 7, 7, 1, 8, false, var2, StructureStrongholdPieces.access$200());
         this.func_151549_a(var1, var3, 5, 1, 9, 7, 1, 9, Blocks.field_150333_U, Blocks.field_150333_U, false);
         this.func_151549_a(var1, var3, 5, 2, 7, 7, 2, 7, Blocks.field_150333_U, Blocks.field_150333_U, false);
         this.func_151549_a(var1, var3, 4, 5, 7, 4, 5, 9, Blocks.field_150333_U, Blocks.field_150333_U, false);
         this.func_151549_a(var1, var3, 8, 5, 7, 8, 5, 9, Blocks.field_150333_U, Blocks.field_150333_U, false);
         this.func_151549_a(var1, var3, 5, 5, 7, 7, 5, 9, Blocks.field_150334_T, Blocks.field_150334_T, false);
         this.func_151550_a(var1, Blocks.field_150478_aa, 0, 6, 5, 6, var3);
         return true;
      }
   }
}
