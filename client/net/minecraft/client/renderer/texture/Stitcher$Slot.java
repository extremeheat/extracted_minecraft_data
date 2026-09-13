package net.minecraft.client.renderer.texture;

import java.util.ArrayList;
import java.util.List;

public class Stitcher$Slot {
   private final int field_94192_a;
   private final int field_94190_b;
   private final int field_94191_c;
   private final int field_94188_d;
   private List field_94189_e;
   private Stitcher$Holder field_94187_f;

   public Stitcher$Slot(int var1, int var2, int var3, int var4) {
      super();
      this.field_94192_a = var1;
      this.field_94190_b = var2;
      this.field_94191_c = var3;
      this.field_94188_d = var4;
   }

   public Stitcher$Holder func_94183_a() {
      return this.field_94187_f;
   }

   public int func_94186_b() {
      return this.field_94192_a;
   }

   public int func_94185_c() {
      return this.field_94190_b;
   }

   public boolean func_94182_a(Stitcher$Holder var1) {
      if (this.field_94187_f != null) {
         return false;
      } else {
         int var2 = var1.func_94197_a();
         int var3 = var1.func_94199_b();
         if (var2 <= this.field_94191_c && var3 <= this.field_94188_d) {
            if (var2 == this.field_94191_c && var3 == this.field_94188_d) {
               this.field_94187_f = var1;
               return true;
            } else {
               if (this.field_94189_e == null) {
                  this.field_94189_e = new ArrayList(1);
                  this.field_94189_e.add(new Stitcher$Slot(this.field_94192_a, this.field_94190_b, var2, var3));
                  int var4 = this.field_94191_c - var2;
                  int var5 = this.field_94188_d - var3;
                  if (var5 > 0 && var4 > 0) {
                     int var6 = Math.max(this.field_94188_d, var4);
                     int var7 = Math.max(this.field_94191_c, var5);
                     if (var6 >= var7) {
                        this.field_94189_e.add(new Stitcher$Slot(this.field_94192_a, this.field_94190_b + var3, var2, var5));
                        this.field_94189_e.add(new Stitcher$Slot(this.field_94192_a + var2, this.field_94190_b, var4, this.field_94188_d));
                     } else {
                        this.field_94189_e.add(new Stitcher$Slot(this.field_94192_a + var2, this.field_94190_b, var4, var3));
                        this.field_94189_e.add(new Stitcher$Slot(this.field_94192_a, this.field_94190_b + var3, this.field_94191_c, var5));
                     }
                  } else if (var4 == 0) {
                     this.field_94189_e.add(new Stitcher$Slot(this.field_94192_a, this.field_94190_b + var3, var2, var5));
                  } else if (var5 == 0) {
                     this.field_94189_e.add(new Stitcher$Slot(this.field_94192_a + var2, this.field_94190_b, var4, var3));
                  }
               }

               for(Stitcher$Slot var9 : this.field_94189_e) {
                  if (var9.func_94182_a(var1)) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      }
   }

   public void func_94184_a(List var1) {
      if (this.field_94187_f != null) {
         var1.add(this);
      } else if (this.field_94189_e != null) {
         for(Stitcher$Slot var3 : this.field_94189_e) {
            var3.func_94184_a(var1);
         }
      }
   }

   @Override
   public String toString() {
      return "Slot{originX="
         + this.field_94192_a
         + ", originY="
         + this.field_94190_b
         + ", width="
         + this.field_94191_c
         + ", height="
         + this.field_94188_d
         + ", texture="
         + this.field_94187_f
         + ", subSlots="
         + this.field_94189_e
         + '}';
   }
}
