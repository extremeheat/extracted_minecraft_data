package net.minecraft.world.item.crafting;

import net.minecraft.util.StringRepresentable;

public enum CookingBookCategory implements StringRepresentable {
   FOOD("food"),
   BLOCKS("blocks"),
   MISC("misc");

   public static final StringRepresentable.EnumCodec<CookingBookCategory> CODEC = StringRepresentable.fromEnum(CookingBookCategory::values);
   private final String name;

   private CookingBookCategory(String var3) {
      this.name = var3;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static CookingBookCategory[] $values() {
      return new CookingBookCategory[]{FOOD, BLOCKS, MISC};
   }
}
