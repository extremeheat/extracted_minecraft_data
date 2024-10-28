package net.minecraft.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.util.StringRepresentable;

public enum FrontAndTop implements StringRepresentable {
   DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
   DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
   DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
   DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
   UP_EAST("up_east", Direction.UP, Direction.EAST),
   UP_NORTH("up_north", Direction.UP, Direction.NORTH),
   UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
   UP_WEST("up_west", Direction.UP, Direction.WEST),
   WEST_UP("west_up", Direction.WEST, Direction.UP),
   EAST_UP("east_up", Direction.EAST, Direction.UP),
   NORTH_UP("north_up", Direction.NORTH, Direction.UP),
   SOUTH_UP("south_up", Direction.SOUTH, Direction.UP);

   private static final Int2ObjectMap<FrontAndTop> LOOKUP_TOP_FRONT = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(values().length), (var0) -> {
      FrontAndTop[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         FrontAndTop var4 = var1[var3];
         var0.put(lookupKey(var4.front, var4.top), var4);
      }

   });
   private final String name;
   private final Direction top;
   private final Direction front;

   private static int lookupKey(Direction var0, Direction var1) {
      return var1.ordinal() << 3 | var0.ordinal();
   }

   private FrontAndTop(String var3, Direction var4, Direction var5) {
      this.name = var3;
      this.front = var4;
      this.top = var5;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static FrontAndTop fromFrontAndTop(Direction var0, Direction var1) {
      int var2 = lookupKey(var0, var1);
      return (FrontAndTop)LOOKUP_TOP_FRONT.get(var2);
   }

   public Direction front() {
      return this.front;
   }

   public Direction top() {
      return this.top;
   }

   // $FF: synthetic method
   private static FrontAndTop[] $values() {
      return new FrontAndTop[]{DOWN_EAST, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, UP_EAST, UP_NORTH, UP_SOUTH, UP_WEST, WEST_UP, EAST_UP, NORTH_UP, SOUTH_UP};
   }
}
