package net.minecraft.world.entity;

import java.util.EnumSet;
import java.util.Set;

public enum RelativeMovement {
   X(0),
   Y(1),
   Z(2),
   Y_ROT(3),
   X_ROT(4);

   public static final Set<RelativeMovement> ALL = Set.of(values());
   public static final Set<RelativeMovement> ROTATION = Set.of(X_ROT, Y_ROT);
   private final int bit;

   private RelativeMovement(int var3) {
      this.bit = var3;
   }

   private int getMask() {
      return 1 << this.bit;
   }

   private boolean isSet(int var1) {
      return (var1 & this.getMask()) == this.getMask();
   }

   public static Set<RelativeMovement> unpack(int var0) {
      EnumSet var1 = EnumSet.noneOf(RelativeMovement.class);

      for (RelativeMovement var5 : values()) {
         if (var5.isSet(var0)) {
            var1.add(var5);
         }
      }

      return var1;
   }

   public static int pack(Set<RelativeMovement> var0) {
      int var1 = 0;

      for (RelativeMovement var3 : var0) {
         var1 |= var3.getMask();
      }

      return var1;
   }
}
