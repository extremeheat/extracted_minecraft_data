package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

abstract class ComponentScatteredFeaturePieces$Feature extends StructureComponent {
   protected int field_74939_a;
   protected int field_74937_b;
   protected int field_74938_c;
   protected int field_74936_d = -1;

   public ComponentScatteredFeaturePieces$Feature() {
      super();
   }

   protected ComponentScatteredFeaturePieces$Feature(Random var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(0);
      this.field_74939_a = var5;
      this.field_74937_b = var6;
      this.field_74938_c = var7;
      this.field_74885_f = var1.nextInt(4);
      switch(this.field_74885_f) {
         case 0:
         case 2:
            this.field_74887_e = new StructureBoundingBox(var2, var3, var4, var2 + var5 - 1, var3 + var6 - 1, var4 + var7 - 1);
            break;
         default:
            this.field_74887_e = new StructureBoundingBox(var2, var3, var4, var2 + var7 - 1, var3 + var6 - 1, var4 + var5 - 1);
      }
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      var1.func_74768_a("Width", this.field_74939_a);
      var1.func_74768_a("Height", this.field_74937_b);
      var1.func_74768_a("Depth", this.field_74938_c);
      var1.func_74768_a("HPos", this.field_74936_d);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      this.field_74939_a = var1.func_74762_e("Width");
      this.field_74937_b = var1.func_74762_e("Height");
      this.field_74938_c = var1.func_74762_e("Depth");
      this.field_74936_d = var1.func_74762_e("HPos");
   }

   protected boolean func_74935_a(World var1, StructureBoundingBox var2, int var3) {
      if (this.field_74936_d >= 0) {
         return true;
      } else {
         int var4 = 0;
         int var5 = 0;

         for(int var6 = this.field_74887_e.field_78896_c; var6 <= this.field_74887_e.field_78892_f; ++var6) {
            for(int var7 = this.field_74887_e.field_78897_a; var7 <= this.field_74887_e.field_78893_d; ++var7) {
               if (var2.func_78890_b(var7, 64, var6)) {
                  var4 += Math.max(var1.func_72825_h(var7, var6), var1.field_73011_w.func_76557_i());
                  ++var5;
               }
            }
         }

         if (var5 == 0) {
            return false;
         } else {
            this.field_74936_d = var4 / var5;
            this.field_74887_e.func_78886_a(0, this.field_74936_d - this.field_74887_e.field_78895_b + var3, 0);
            return true;
         }
      }
   }
}
