package net.minecraft.world.entity.animal.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum Variant {
   WHITE(0),
   CREAMY(1),
   CHESTNUT(2),
   BROWN(3),
   BLACK(4),
   GRAY(5),
   DARKBROWN(6);

   private static final Variant[] BY_ID = (Variant[])Arrays.stream(values()).sorted(Comparator.comparingInt(Variant::getId)).toArray((var0) -> {
      return new Variant[var0];
   });
   // $FF: renamed from: id int
   private final int field_227;

   private Variant(int var3) {
      this.field_227 = var3;
   }

   public int getId() {
      return this.field_227;
   }

   public static Variant byId(int var0) {
      return BY_ID[var0 % BY_ID.length];
   }

   // $FF: synthetic method
   private static Variant[] $values() {
      return new Variant[]{WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARKBROWN};
   }
}
