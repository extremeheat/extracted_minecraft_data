package net.minecraft.util.math;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class Rotations {
   protected final float field_179419_a;
   protected final float field_179417_b;
   protected final float field_179418_c;

   public Rotations(float var1, float var2, float var3) {
      super();
      this.field_179419_a = !Float.isInfinite(var1) && !Float.isNaN(var1) ? var1 % 360.0F : 0.0F;
      this.field_179417_b = !Float.isInfinite(var2) && !Float.isNaN(var2) ? var2 % 360.0F : 0.0F;
      this.field_179418_c = !Float.isInfinite(var3) && !Float.isNaN(var3) ? var3 % 360.0F : 0.0F;
   }

   public Rotations(NBTTagList var1) {
      this(var1.func_150308_e(0), var1.func_150308_e(1), var1.func_150308_e(2));
   }

   public NBTTagList func_179414_a() {
      NBTTagList var1 = new NBTTagList();
      var1.add((INBTBase)(new NBTTagFloat(this.field_179419_a)));
      var1.add((INBTBase)(new NBTTagFloat(this.field_179417_b)));
      var1.add((INBTBase)(new NBTTagFloat(this.field_179418_c)));
      return var1;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Rotations)) {
         return false;
      } else {
         Rotations var2 = (Rotations)var1;
         return this.field_179419_a == var2.field_179419_a && this.field_179417_b == var2.field_179417_b && this.field_179418_c == var2.field_179418_c;
      }
   }

   public float func_179415_b() {
      return this.field_179419_a;
   }

   public float func_179416_c() {
      return this.field_179417_b;
   }

   public float func_179413_d() {
      return this.field_179418_c;
   }
}
