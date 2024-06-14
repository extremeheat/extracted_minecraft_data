package net.minecraft.world.level.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
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
   public static final ResourceKey<BannerPattern> FLOW = create("flow");
   public static final ResourceKey<BannerPattern> GUSTER = create("guster");

   public BannerPatterns() {
      super();
   }

   private static ResourceKey<BannerPattern> create(String var0) {
      return ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.withDefaultNamespace(var0));
   }

   public static void bootstrap(BootstrapContext<BannerPattern> var0) {
      register(var0, BASE);
      register(var0, SQUARE_BOTTOM_LEFT);
      register(var0, SQUARE_BOTTOM_RIGHT);
      register(var0, SQUARE_TOP_LEFT);
      register(var0, SQUARE_TOP_RIGHT);
      register(var0, STRIPE_BOTTOM);
      register(var0, STRIPE_TOP);
      register(var0, STRIPE_LEFT);
      register(var0, STRIPE_RIGHT);
      register(var0, STRIPE_CENTER);
      register(var0, STRIPE_MIDDLE);
      register(var0, STRIPE_DOWNRIGHT);
      register(var0, STRIPE_DOWNLEFT);
      register(var0, STRIPE_SMALL);
      register(var0, CROSS);
      register(var0, STRAIGHT_CROSS);
      register(var0, TRIANGLE_BOTTOM);
      register(var0, TRIANGLE_TOP);
      register(var0, TRIANGLES_BOTTOM);
      register(var0, TRIANGLES_TOP);
      register(var0, DIAGONAL_LEFT);
      register(var0, DIAGONAL_RIGHT);
      register(var0, DIAGONAL_LEFT_MIRROR);
      register(var0, DIAGONAL_RIGHT_MIRROR);
      register(var0, CIRCLE_MIDDLE);
      register(var0, RHOMBUS_MIDDLE);
      register(var0, HALF_VERTICAL);
      register(var0, HALF_HORIZONTAL);
      register(var0, HALF_VERTICAL_MIRROR);
      register(var0, HALF_HORIZONTAL_MIRROR);
      register(var0, BORDER);
      register(var0, CURLY_BORDER);
      register(var0, GRADIENT);
      register(var0, GRADIENT_UP);
      register(var0, BRICKS);
      register(var0, GLOBE);
      register(var0, CREEPER);
      register(var0, SKULL);
      register(var0, FLOWER);
      register(var0, MOJANG);
      register(var0, PIGLIN);
      register(var0, FLOW);
      register(var0, GUSTER);
   }

   public static void register(BootstrapContext<BannerPattern> var0, ResourceKey<BannerPattern> var1) {
      var0.register(var1, new BannerPattern(var1.location(), "block.minecraft.banner." + var1.location().toShortLanguageKey()));
   }
}
