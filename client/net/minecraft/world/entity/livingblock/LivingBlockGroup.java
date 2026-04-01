package net.minecraft.world.entity.livingblock;

import java.util.function.IntFunction;
import net.minecraft.util.ARGB;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum LivingBlockGroup implements StringRepresentable {
   NONE(0, "none", ARGB.color(255, 192, 192, 192)),
   RED(1, "red", ARGB.color(255, 255, 0, 0)),
   BLUE(2, "blue", ARGB.color(255, 0, 0, 255)),
   LIME(3, "lime", ARGB.color(255, 0, 255, 0)),
   YELLOW(4, "yellow", ARGB.color(255, 255, 255, 0)),
   PURPLE(5, "purple", ARGB.color(255, 255, 0, 255)),
   AQUA(6, "aqua", ARGB.color(255, 0, 255, 255)),
   ALL(7, "all", ARGB.color(255, 0, 0, 0));

   private final int id;
   private final String name;
   private final int color;
   public static final StringRepresentable.EnumCodec<LivingBlockGroup> CODEC = StringRepresentable.<LivingBlockGroup>fromEnum(LivingBlockGroup::values);
   public static final IntFunction<LivingBlockGroup> BY_ID = ByIdMap.<LivingBlockGroup>continuous(LivingBlockGroup::id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

   private LivingBlockGroup(final int id, final String name, final int color) {
      this.id = id;
      this.name = name;
      this.color = color;
   }

   public String getSerializedName() {
      return this.name;
   }

   public int id() {
      return this.id;
   }

   public int color() {
      return this.color;
   }

   // $FF: synthetic method
   private static LivingBlockGroup[] $values() {
      return new LivingBlockGroup[]{NONE, RED, BLUE, LIME, YELLOW, PURPLE, AQUA, ALL};
   }
}
