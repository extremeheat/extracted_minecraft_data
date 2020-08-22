package net.minecraft.world.phys;

import java.util.Objects;

public class PosAndRot {
   private final Vec3 pos;
   private final float xRot;
   private final float yRot;

   public PosAndRot(Vec3 var1, float var2, float var3) {
      this.pos = var1;
      this.xRot = var2;
      this.yRot = var3;
   }

   public Vec3 pos() {
      return this.pos;
   }

   public float xRot() {
      return this.xRot;
   }

   public float yRot() {
      return this.yRot;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         PosAndRot var2 = (PosAndRot)var1;
         return Float.compare(var2.xRot, this.xRot) == 0 && Float.compare(var2.yRot, this.yRot) == 0 && Objects.equals(this.pos, var2.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.pos, this.xRot, this.yRot});
   }

   public String toString() {
      return "PosAndRot[" + this.pos + " (" + this.xRot + ", " + this.yRot + ")]";
   }
}
