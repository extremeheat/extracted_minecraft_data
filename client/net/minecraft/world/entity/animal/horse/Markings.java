package net.minecraft.world.entity.animal.horse;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;

public enum Markings {
   NONE(0),
   WHITE(1),
   WHITE_FIELD(2),
   WHITE_DOTS(3),
   BLACK_DOTS(4);

   private static final IntFunction<Markings> BY_ID = ByIdMap.continuous(Markings::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;

   private Markings(int var3) {
      this.id = var3;
   }

   public int getId() {
      return this.id;
   }

   public static Markings byId(int var0) {
      return (Markings)BY_ID.apply(var0);
   }

   // $FF: synthetic method
   private static Markings[] $values() {
      return new Markings[]{NONE, WHITE, WHITE_FIELD, WHITE_DOTS, BLACK_DOTS};
   }
}
