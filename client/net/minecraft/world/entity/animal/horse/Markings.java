package net.minecraft.world.entity.animal.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum Markings {
   NONE(0),
   WHITE(1),
   WHITE_FIELD(2),
   WHITE_DOTS(3),
   BLACK_DOTS(4);

   private static final Markings[] BY_ID = (Markings[])Arrays.stream(values()).sorted(Comparator.comparingInt(Markings::getId)).toArray((var0) -> {
      return new Markings[var0];
   });
   // $FF: renamed from: id int
   private final int field_153;

   private Markings(int var3) {
      this.field_153 = var3;
   }

   public int getId() {
      return this.field_153;
   }

   public static Markings byId(int var0) {
      return BY_ID[var0 % BY_ID.length];
   }

   // $FF: synthetic method
   private static Markings[] $values() {
      return new Markings[]{NONE, WHITE, WHITE_FIELD, WHITE_DOTS, BLACK_DOTS};
   }
}
