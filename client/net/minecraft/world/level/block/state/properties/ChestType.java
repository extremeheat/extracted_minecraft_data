package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum ChestType implements StringRepresentable {
   SINGLE("single"),
   LEFT("left"),
   RIGHT("right");

   private final String name;

   private ChestType(final String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public ChestType getOpposite() {
      ChestType var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = SINGLE;
         case 1 -> var10000 = RIGHT;
         case 2 -> var10000 = LEFT;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static ChestType[] $values() {
      return new ChestType[]{SINGLE, LEFT, RIGHT};
   }
}
