package net.minecraft.data.models.model;

import javax.annotation.Nullable;

public final class TextureSlot {
   public static final TextureSlot ALL = create("all");
   public static final TextureSlot TEXTURE;
   public static final TextureSlot PARTICLE;
   public static final TextureSlot END;
   public static final TextureSlot BOTTOM;
   public static final TextureSlot TOP;
   public static final TextureSlot FRONT;
   public static final TextureSlot BACK;
   public static final TextureSlot SIDE;
   public static final TextureSlot NORTH;
   public static final TextureSlot SOUTH;
   public static final TextureSlot EAST;
   public static final TextureSlot WEST;
   public static final TextureSlot UP;
   public static final TextureSlot DOWN;
   public static final TextureSlot CROSS;
   public static final TextureSlot PLANT;
   public static final TextureSlot WALL;
   public static final TextureSlot RAIL;
   public static final TextureSlot WOOL;
   public static final TextureSlot PATTERN;
   public static final TextureSlot PANE;
   public static final TextureSlot EDGE;
   public static final TextureSlot FAN;
   public static final TextureSlot STEM;
   public static final TextureSlot UPPER_STEM;
   public static final TextureSlot CROP;
   public static final TextureSlot DIRT;
   public static final TextureSlot FIRE;
   public static final TextureSlot LANTERN;
   public static final TextureSlot PLATFORM;
   public static final TextureSlot UNSTICKY;
   public static final TextureSlot TORCH;
   public static final TextureSlot LAYER0;
   public static final TextureSlot LAYER1;
   public static final TextureSlot LAYER2;
   public static final TextureSlot LIT_LOG;
   public static final TextureSlot CANDLE;
   public static final TextureSlot INSIDE;
   public static final TextureSlot CONTENT;
   public static final TextureSlot INNER_TOP;
   public static final TextureSlot FLOWERBED;
   private final String id;
   @Nullable
   private final TextureSlot parent;

   private static TextureSlot create(String var0) {
      return new TextureSlot(var0, (TextureSlot)null);
   }

   private static TextureSlot create(String var0, TextureSlot var1) {
      return new TextureSlot(var0, var1);
   }

   private TextureSlot(String var1, @Nullable TextureSlot var2) {
      super();
      this.id = var1;
      this.parent = var2;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public TextureSlot getParent() {
      return this.parent;
   }

   public String toString() {
      return "#" + this.id;
   }

   static {
      TEXTURE = create("texture", ALL);
      PARTICLE = create("particle", TEXTURE);
      END = create("end", ALL);
      BOTTOM = create("bottom", END);
      TOP = create("top", END);
      FRONT = create("front", ALL);
      BACK = create("back", ALL);
      SIDE = create("side", ALL);
      NORTH = create("north", SIDE);
      SOUTH = create("south", SIDE);
      EAST = create("east", SIDE);
      WEST = create("west", SIDE);
      UP = create("up");
      DOWN = create("down");
      CROSS = create("cross");
      PLANT = create("plant");
      WALL = create("wall", ALL);
      RAIL = create("rail");
      WOOL = create("wool");
      PATTERN = create("pattern");
      PANE = create("pane");
      EDGE = create("edge");
      FAN = create("fan");
      STEM = create("stem");
      UPPER_STEM = create("upperstem");
      CROP = create("crop");
      DIRT = create("dirt");
      FIRE = create("fire");
      LANTERN = create("lantern");
      PLATFORM = create("platform");
      UNSTICKY = create("unsticky");
      TORCH = create("torch");
      LAYER0 = create("layer0");
      LAYER1 = create("layer1");
      LAYER2 = create("layer2");
      LIT_LOG = create("lit_log");
      CANDLE = create("candle");
      INSIDE = create("inside");
      CONTENT = create("content");
      INNER_TOP = create("inner_top");
      FLOWERBED = create("flowerbed");
   }
}
