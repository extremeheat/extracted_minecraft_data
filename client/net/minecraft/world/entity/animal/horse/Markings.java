package net.minecraft.world.entity.animal.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum Markings {
   NONE(0),
   WHITE(1),
   WHITE_FIELD(2),
   WHITE_DOTS(3),
   BLACK_DOTS(4);

   private static final Markings[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Markings::getId)).toArray(var0 -> new Markings[var0]);
   private final int id;

   private Markings(int var3) {
      this.id = var3;
   }

   public int getId() {
      return this.id;
   }

   public static Markings byId(int var0) {
      return BY_ID[var0 % BY_ID.length];
   }
}