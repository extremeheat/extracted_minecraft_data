package net.minecraft.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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

   private static final Int2ObjectMap<FrontAndTop> LOOKUP_TOP_FRONT = new Int2ObjectOpenHashMap(values().length);
   private final String name;
   private final Direction top;
   private final Direction front;

   private static int lookupKey(Direction var0, Direction var1) {
      return var0.ordinal() << 3 | var1.ordinal();
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
      int var2 = lookupKey(var1, var0);
      return (FrontAndTop)LOOKUP_TOP_FRONT.get(var2);
   }

   public Direction front() {
      return this.front;
   }

   public Direction top() {
      return this.top;
   }

   static {
      FrontAndTop[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         FrontAndTop var3 = var0[var2];
         LOOKUP_TOP_FRONT.put(lookupKey(var3.top, var3.front), var3);
      }

   }
}
