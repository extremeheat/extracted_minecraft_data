package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;

public class BannerPatternFormatFix extends NamedEntityFix {
   private static final Map<String, String> PATTERN_ID_MAP = Map.ofEntries(Map.entry("b", "minecraft:base"), Map.entry("bl", "minecraft:square_bottom_left"), Map.entry("br", "minecraft:square_bottom_right"), Map.entry("tl", "minecraft:square_top_left"), Map.entry("tr", "minecraft:square_top_right"), Map.entry("bs", "minecraft:stripe_bottom"), Map.entry("ts", "minecraft:stripe_top"), Map.entry("ls", "minecraft:stripe_left"), Map.entry("rs", "minecraft:stripe_right"), Map.entry("cs", "minecraft:stripe_center"), Map.entry("ms", "minecraft:stripe_middle"), Map.entry("drs", "minecraft:stripe_downright"), Map.entry("dls", "minecraft:stripe_downleft"), Map.entry("ss", "minecraft:small_stripes"), Map.entry("cr", "minecraft:cross"), Map.entry("sc", "minecraft:straight_cross"), Map.entry("bt", "minecraft:triangle_bottom"), Map.entry("tt", "minecraft:triangle_top"), Map.entry("bts", "minecraft:triangles_bottom"), Map.entry("tts", "minecraft:triangles_top"), Map.entry("ld", "minecraft:diagonal_left"), Map.entry("rd", "minecraft:diagonal_up_right"), Map.entry("lud", "minecraft:diagonal_up_left"), Map.entry("rud", "minecraft:diagonal_right"), Map.entry("mc", "minecraft:circle"), Map.entry("mr", "minecraft:rhombus"), Map.entry("vh", "minecraft:half_vertical"), Map.entry("hh", "minecraft:half_horizontal"), Map.entry("vhr", "minecraft:half_vertical_right"), Map.entry("hhb", "minecraft:half_horizontal_bottom"), Map.entry("bo", "minecraft:border"), Map.entry("cbo", "minecraft:curly_border"), Map.entry("gra", "minecraft:gradient"), Map.entry("gru", "minecraft:gradient_up"), Map.entry("bri", "minecraft:bricks"), Map.entry("glb", "minecraft:globe"), Map.entry("cre", "minecraft:creeper"), Map.entry("sku", "minecraft:skull"), Map.entry("flo", "minecraft:flower"), Map.entry("moj", "minecraft:mojang"), Map.entry("pig", "minecraft:piglin"));

   public BannerPatternFormatFix(Schema var1) {
      super(var1, false, "BannerPatternFormatFix", References.BLOCK_ENTITY, "minecraft:banner");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), BannerPatternFormatFix::fixTag);
   }

   private static Dynamic<?> fixTag(Dynamic<?> var0) {
      return var0.renameAndFixField("Patterns", "patterns", (var0x) -> var0x.createList(var0x.asStream().map(BannerPatternFormatFix::fixLayer)));
   }

   private static Dynamic<?> fixLayer(Dynamic<?> var0) {
      var0 = var0.renameAndFixField("Pattern", "pattern", (var0x) -> {
         DataResult var10000 = var0x.asString().map((var0) -> (String)PATTERN_ID_MAP.getOrDefault(var0, var0));
         Objects.requireNonNull(var0x);
         return (Dynamic)DataFixUtils.orElse(var10000.map(var0x::createString).result(), var0x);
      });
      var0 = var0.set("color", var0.createString(fixColor(var0.get("Color").asInt(0))));
      var0 = var0.remove("Color");
      return var0;
   }

   public static String fixColor(int var0) {
      String var10000;
      switch (var0) {
         case 1 -> var10000 = "orange";
         case 2 -> var10000 = "magenta";
         case 3 -> var10000 = "light_blue";
         case 4 -> var10000 = "yellow";
         case 5 -> var10000 = "lime";
         case 6 -> var10000 = "pink";
         case 7 -> var10000 = "gray";
         case 8 -> var10000 = "light_gray";
         case 9 -> var10000 = "cyan";
         case 10 -> var10000 = "purple";
         case 11 -> var10000 = "blue";
         case 12 -> var10000 = "brown";
         case 13 -> var10000 = "green";
         case 14 -> var10000 = "red";
         case 15 -> var10000 = "black";
         default -> var10000 = "white";
      }

      return var10000;
   }
}
