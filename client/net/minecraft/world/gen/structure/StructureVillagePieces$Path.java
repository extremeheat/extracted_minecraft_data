package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class StructureVillagePieces$Path extends StructureVillagePieces$Road {
   private int field_74934_a;

   public StructureVillagePieces$Path() {
      super();
   }

   public StructureVillagePieces$Path(StructureVillagePieces$Start var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var5;
      this.field_74887_e = var4;
      this.field_74934_a = Math.max(var4.func_78883_b(), var4.func_78880_d());
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74768_a("Length", this.field_74934_a);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74934_a = var1.func_74762_e("Length");
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      boolean var4 = false;

      for(int var5 = var3.nextInt(5); var5 < this.field_74934_a - 8; var5 += 2 + var3.nextInt(5)) {
         StructureComponent var6 = this.func_74891_a((StructureVillagePieces$Start)var1, var2, var3, 0, var5);
         if (var6 != null) {
            var5 += Math.max(var6.field_74887_e.func_78883_b(), var6.field_74887_e.func_78880_d());
            var4 = true;
         }
      }

      for(int var7 = var3.nextInt(5); var7 < this.field_74934_a - 8; var7 += 2 + var3.nextInt(5)) {
         StructureComponent var8 = this.func_74894_b((StructureVillagePieces$Start)var1, var2, var3, 0, var7);
         if (var8 != null) {
            var7 += Math.max(var8.field_74887_e.func_78883_b(), var8.field_74887_e.func_78880_d());
            var4 = true;
         }
      }

      if (var4 && var3.nextInt(3) > 0) {
         switch(this.field_74885_f) {
            case 0:
               StructureVillagePieces.access$100(
                  (StructureVillagePieces$Start)var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a - 1,
                  this.field_74887_e.field_78895_b,
                  this.field_74887_e.field_78892_f - 2,
                  1,
                  this.func_74877_c()
               );
               break;
            case 1:
               StructureVillagePieces.access$100(
                  (StructureVillagePieces$Start)var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a,
                  this.field_74887_e.field_78895_b,
                  this.field_74887_e.field_78896_c - 1,
                  2,
                  this.func_74877_c()
               );
               break;
            case 2:
               StructureVillagePieces.access$100(
                  (StructureVillagePieces$Start)var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a - 1,
                  this.field_74887_e.field_78895_b,
                  this.field_74887_e.field_78896_c,
                  1,
                  this.func_74877_c()
               );
               break;
            case 3:
               StructureVillagePieces.access$100(
                  (StructureVillagePieces$Start)var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d - 2,
                  this.field_74887_e.field_78895_b,
                  this.field_74887_e.field_78896_c - 1,
                  2,
                  this.func_74877_c()
               );
         }
      }

      if (var4 && var3.nextInt(3) > 0) {
         switch(this.field_74885_f) {
            case 0:
               StructureVillagePieces.access$100(
                  (StructureVillagePieces$Start)var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d + 1,
                  this.field_74887_e.field_78895_b,
                  this.field_74887_e.field_78892_f - 2,
                  3,
                  this.func_74877_c()
               );
               break;
            case 1:
               StructureVillagePieces.access$100(
                  (StructureVillagePieces$Start)var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a,
                  this.field_74887_e.field_78895_b,
                  this.field_74887_e.field_78892_f + 1,
                  0,
                  this.func_74877_c()
               );
               break;
            case 2:
               StructureVillagePieces.access$100(
                  (StructureVillagePieces$Start)var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d + 1,
                  this.field_74887_e.field_78895_b,
                  this.field_74887_e.field_78896_c,
                  3,
                  this.func_74877_c()
               );
               break;
            case 3:
               StructureVillagePieces.access$100(
                  (StructureVillagePieces$Start)var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d - 2,
                  this.field_74887_e.field_78895_b,
                  this.field_74887_e.field_78892_f + 1,
                  0,
                  this.func_74877_c()
               );
         }
      }
   }

   public static StructureBoundingBox func_74933_a(StructureVillagePieces$Start var0, List var1, Random var2, int var3, int var4, int var5, int var6) {
      for(int var7 = 7 * MathHelper.func_76136_a(var2, 3, 5); var7 >= 7; var7 -= 7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_78889_a(var3, var4, var5, 0, 0, 0, 3, 3, var7, var6);
         if (StructureComponent.func_74883_a(var1, var8) == null) {
            return var8;
         }
      }

      return null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      Block var4 = this.func_151558_b(Blocks.field_150351_n, 0);

      for(int var5 = this.field_74887_e.field_78897_a; var5 <= this.field_74887_e.field_78893_d; ++var5) {
         for(int var6 = this.field_74887_e.field_78896_c; var6 <= this.field_74887_e.field_78892_f; ++var6) {
            if (var3.func_78890_b(var5, 64, var6)) {
               int var7 = var1.func_72825_h(var5, var6) - 1;
               var1.func_147465_d(var5, var7, var6, var4, 0, 2);
            }
         }
      }

      return true;
   }
}
