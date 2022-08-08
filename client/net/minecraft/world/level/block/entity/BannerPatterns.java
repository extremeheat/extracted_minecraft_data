package net.minecraft.world.level.block.entity;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BannerPatterns {
   public static final ResourceKey<BannerPattern> BASE = create("base");
   public static final ResourceKey<BannerPattern> SQUARE_BOTTOM_LEFT = create("square_bottom_left");
   public static final ResourceKey<BannerPattern> SQUARE_BOTTOM_RIGHT = create("square_bottom_right");
   public static final ResourceKey<BannerPattern> SQUARE_TOP_LEFT = create("square_top_left");
   public static final ResourceKey<BannerPattern> SQUARE_TOP_RIGHT = create("square_top_right");
   public static final ResourceKey<BannerPattern> STRIPE_BOTTOM = create("stripe_bottom");
   public static final ResourceKey<BannerPattern> STRIPE_TOP = create("stripe_top");
   public static final ResourceKey<BannerPattern> STRIPE_LEFT = create("stripe_left");
   public static final ResourceKey<BannerPattern> STRIPE_RIGHT = create("stripe_right");
   public static final ResourceKey<BannerPattern> STRIPE_CENTER = create("stripe_center");
   public static final ResourceKey<BannerPattern> STRIPE_MIDDLE = create("stripe_middle");
   public static final ResourceKey<BannerPattern> STRIPE_DOWNRIGHT = create("stripe_downright");
   public static final ResourceKey<BannerPattern> STRIPE_DOWNLEFT = create("stripe_downleft");
   public static final ResourceKey<BannerPattern> STRIPE_SMALL = create("small_stripes");
   public static final ResourceKey<BannerPattern> CROSS = create("cross");
   public static final ResourceKey<BannerPattern> STRAIGHT_CROSS = create("straight_cross");
   public static final ResourceKey<BannerPattern> TRIANGLE_BOTTOM = create("triangle_bottom");
   public static final ResourceKey<BannerPattern> TRIANGLE_TOP = create("triangle_top");
   public static final ResourceKey<BannerPattern> TRIANGLES_BOTTOM = create("triangles_bottom");
   public static final ResourceKey<BannerPattern> TRIANGLES_TOP = create("triangles_top");
   public static final ResourceKey<BannerPattern> DIAGONAL_LEFT = create("diagonal_left");
   public static final ResourceKey<BannerPattern> DIAGONAL_RIGHT = create("diagonal_up_right");
   public static final ResourceKey<BannerPattern> DIAGONAL_LEFT_MIRROR = create("diagonal_up_left");
   public static final ResourceKey<BannerPattern> DIAGONAL_RIGHT_MIRROR = create("diagonal_right");
   public static final ResourceKey<BannerPattern> CIRCLE_MIDDLE = create("circle");
   public static final ResourceKey<BannerPattern> RHOMBUS_MIDDLE = create("rhombus");
   public static final ResourceKey<BannerPattern> HALF_VERTICAL = create("half_vertical");
   public static final ResourceKey<BannerPattern> HALF_HORIZONTAL = create("half_horizontal");
   public static final ResourceKey<BannerPattern> HALF_VERTICAL_MIRROR = create("half_vertical_right");
   public static final ResourceKey<BannerPattern> HALF_HORIZONTAL_MIRROR = create("half_horizontal_bottom");
   public static final ResourceKey<BannerPattern> BORDER = create("border");
   public static final ResourceKey<BannerPattern> CURLY_BORDER = create("curly_border");
   public static final ResourceKey<BannerPattern> GRADIENT = create("gradient");
   public static final ResourceKey<BannerPattern> GRADIENT_UP = create("gradient_up");
   public static final ResourceKey<BannerPattern> BRICKS = create("bricks");
   public static final ResourceKey<BannerPattern> GLOBE = create("globe");
   public static final ResourceKey<BannerPattern> CREEPER = create("creeper");
   public static final ResourceKey<BannerPattern> SKULL = create("skull");
   public static final ResourceKey<BannerPattern> FLOWER = create("flower");
   public static final ResourceKey<BannerPattern> MOJANG = create("mojang");
   public static final ResourceKey<BannerPattern> PIGLIN = create("piglin");

   public BannerPatterns() {
      super();
   }

   private static ResourceKey<BannerPattern> create(String var0) {
      return ResourceKey.create(Registry.BANNER_PATTERN_REGISTRY, new ResourceLocation(var0));
   }

   public static BannerPattern bootstrap(Registry<BannerPattern> var0) {
      Registry.register(var0, (ResourceKey)BASE, new BannerPattern("b"));
      Registry.register(var0, (ResourceKey)SQUARE_BOTTOM_LEFT, new BannerPattern("bl"));
      Registry.register(var0, (ResourceKey)SQUARE_BOTTOM_RIGHT, new BannerPattern("br"));
      Registry.register(var0, (ResourceKey)SQUARE_TOP_LEFT, new BannerPattern("tl"));
      Registry.register(var0, (ResourceKey)SQUARE_TOP_RIGHT, new BannerPattern("tr"));
      Registry.register(var0, (ResourceKey)STRIPE_BOTTOM, new BannerPattern("bs"));
      Registry.register(var0, (ResourceKey)STRIPE_TOP, new BannerPattern("ts"));
      Registry.register(var0, (ResourceKey)STRIPE_LEFT, new BannerPattern("ls"));
      Registry.register(var0, (ResourceKey)STRIPE_RIGHT, new BannerPattern("rs"));
      Registry.register(var0, (ResourceKey)STRIPE_CENTER, new BannerPattern("cs"));
      Registry.register(var0, (ResourceKey)STRIPE_MIDDLE, new BannerPattern("ms"));
      Registry.register(var0, (ResourceKey)STRIPE_DOWNRIGHT, new BannerPattern("drs"));
      Registry.register(var0, (ResourceKey)STRIPE_DOWNLEFT, new BannerPattern("dls"));
      Registry.register(var0, (ResourceKey)STRIPE_SMALL, new BannerPattern("ss"));
      Registry.register(var0, (ResourceKey)CROSS, new BannerPattern("cr"));
      Registry.register(var0, (ResourceKey)STRAIGHT_CROSS, new BannerPattern("sc"));
      Registry.register(var0, (ResourceKey)TRIANGLE_BOTTOM, new BannerPattern("bt"));
      Registry.register(var0, (ResourceKey)TRIANGLE_TOP, new BannerPattern("tt"));
      Registry.register(var0, (ResourceKey)TRIANGLES_BOTTOM, new BannerPattern("bts"));
      Registry.register(var0, (ResourceKey)TRIANGLES_TOP, new BannerPattern("tts"));
      Registry.register(var0, (ResourceKey)DIAGONAL_LEFT, new BannerPattern("ld"));
      Registry.register(var0, (ResourceKey)DIAGONAL_RIGHT, new BannerPattern("rd"));
      Registry.register(var0, (ResourceKey)DIAGONAL_LEFT_MIRROR, new BannerPattern("lud"));
      Registry.register(var0, (ResourceKey)DIAGONAL_RIGHT_MIRROR, new BannerPattern("rud"));
      Registry.register(var0, (ResourceKey)CIRCLE_MIDDLE, new BannerPattern("mc"));
      Registry.register(var0, (ResourceKey)RHOMBUS_MIDDLE, new BannerPattern("mr"));
      Registry.register(var0, (ResourceKey)HALF_VERTICAL, new BannerPattern("vh"));
      Registry.register(var0, (ResourceKey)HALF_HORIZONTAL, new BannerPattern("hh"));
      Registry.register(var0, (ResourceKey)HALF_VERTICAL_MIRROR, new BannerPattern("vhr"));
      Registry.register(var0, (ResourceKey)HALF_HORIZONTAL_MIRROR, new BannerPattern("hhb"));
      Registry.register(var0, (ResourceKey)BORDER, new BannerPattern("bo"));
      Registry.register(var0, (ResourceKey)CURLY_BORDER, new BannerPattern("cbo"));
      Registry.register(var0, (ResourceKey)GRADIENT, new BannerPattern("gra"));
      Registry.register(var0, (ResourceKey)GRADIENT_UP, new BannerPattern("gru"));
      Registry.register(var0, (ResourceKey)BRICKS, new BannerPattern("bri"));
      Registry.register(var0, (ResourceKey)GLOBE, new BannerPattern("glb"));
      Registry.register(var0, (ResourceKey)CREEPER, new BannerPattern("cre"));
      Registry.register(var0, (ResourceKey)SKULL, new BannerPattern("sku"));
      Registry.register(var0, (ResourceKey)FLOWER, new BannerPattern("flo"));
      Registry.register(var0, (ResourceKey)MOJANG, new BannerPattern("moj"));
      return (BannerPattern)Registry.register(var0, (ResourceKey)PIGLIN, new BannerPattern("pig"));
   }
}
