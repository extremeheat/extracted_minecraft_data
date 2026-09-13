package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Stairs extends StructureStrongholdPieces$Stronghold {
   private boolean field_75024_a;

   public StructureStrongholdPieces$Stairs() {
      super();
   }

   public StructureStrongholdPieces$Stairs(int var1, Random var2, int var3, int var4) {
      super(var1);
      this.field_75024_a = true;
      this.field_74885_f = var2.nextInt(4);
      this.field_143013_d = StructureStrongholdPieces$Stronghold$Door.OPENING;
      switch(this.field_74885_f) {
         case 0:
         case 2:
            this.field_74887_e = new StructureBoundingBox(var3, 64, var4, var3 + 5 - 1, 74, var4 + 5 - 1);
            break;
         default:
            this.field_74887_e = new StructureBoundingBox(var3, 64, var4, var3 + 5 - 1, 74, var4 + 5 - 1);
      }
   }

   public StructureStrongholdPieces$Stairs(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_75024_a = false;
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Source", this.field_75024_a);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_75024_a = var1.func_74767_n("Source");
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      if (this.field_75024_a) {
         StructureStrongholdPieces.access$102(StructureStrongholdPieces$Crossing.class);
      }

      this.func_74986_a((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
   }

   public static StructureStrongholdPieces$Stairs func_75022_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -7, 0, 5, 11, 5, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces$Stairs(var6, var1, var7, var5) : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 10, 4, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 7, 0);
         this.func_74990_a(var1, var2, var3, StructureStrongholdPieces$Stronghold$Door.OPENING, 1, 1, 4);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 2, 6, 1, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 1, 5, 1, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 0, 1, 6, 1, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 1, 5, 2, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 1, 4, 3, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 0, 1, 5, 3, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 2, 4, 3, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3, 3, 3, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 0, 3, 4, 3, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3, 3, 2, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3, 2, 1, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 0, 3, 3, 1, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 2, 2, 1, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 1, 1, 1, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 0, 1, 2, 1, var3);
         this.func_151550_a(var1, Blocks.field_150417_aV, 0, 1, 1, 2, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 0, 1, 1, 3, var3);
         return true;
      }
   }
}
