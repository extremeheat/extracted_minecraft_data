package net.minecraft.client.renderer.chunk;

import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class TranslucencyPointOfView {
   private int x;
   private int y;
   private int z;

   public TranslucencyPointOfView() {
      super();
   }

   public static TranslucencyPointOfView of(Vec3 var0, long var1) {
      return (new TranslucencyPointOfView()).set(var0, var1);
   }

   public TranslucencyPointOfView set(Vec3 var1, long var2) {
      this.x = getCoordinate(var1.x(), SectionPos.x(var2));
      this.y = getCoordinate(var1.y(), SectionPos.y(var2));
      this.z = getCoordinate(var1.z(), SectionPos.z(var2));
      return this;
   }

   private static int getCoordinate(double var0, int var2) {
      int var3 = SectionPos.blockToSectionCoord(var0) - var2;
      return Mth.clamp(var3, -1, 1);
   }

   public boolean isAxisAligned() {
      return this.x == 0 || this.y == 0 || this.z == 0;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TranslucencyPointOfView)) {
         return false;
      } else {
         TranslucencyPointOfView var2 = (TranslucencyPointOfView)var1;
         return this.x == var2.x && this.y == var2.y && this.z == var2.z;
      }
   }
}
