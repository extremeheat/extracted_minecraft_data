package net.minecraft.world.entity.animal.horse;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum Variant implements StringRepresentable {
   WHITE(0, "white"),
   CREAMY(1, "creamy"),
   CHESTNUT(2, "chestnut"),
   BROWN(3, "brown"),
   BLACK(4, "black"),
   GRAY(5, "gray"),
   DARK_BROWN(6, "dark_brown");

   public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
   private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;
   private final String name;

   private Variant(int var3, String var4) {
      this.id = var3;
      this.name = var4;
   }

   public int getId() {
      return this.id;
   }

   public static Variant byId(int var0) {
      return (Variant)BY_ID.apply(var0);
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static Variant[] $values() {
      return new Variant[]{WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARK_BROWN};
   }
}
