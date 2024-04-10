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

   private static final Int2ObjectMap<FrontAndTop> LOOKUP_TOP_FRONT = Util.make(new Int2ObjectOpenHashMap(values().length), var0 -> {
      for (FrontAndTop var4 : values()) {
         var0.put(lookupKey(var4.front, var4.top), var4);
      }
   });
   private final String name;
   private final Direction top;
   private final Direction front;

   private static int lookupKey(Direction var0, Direction var1) {
      return var1.ordinal() << 3 | var0.ordinal();
   }

   private FrontAndTop(final String param3, final Direction param4, final Direction param5) {
      this.name = nullxx;
      this.front = nullxxx;
      this.top = nullxxxx;
   }

   @Override
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
}
