package net.minecraft.data.worldgen;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class Carvers {
   public static final ConfiguredWorldCarver<CaveCarverConfiguration> CAVE;
   public static final ConfiguredWorldCarver<CaveCarverConfiguration> CAVE_EXTRA_UNDERGROUND;
   public static final ConfiguredWorldCarver<CanyonCarverConfiguration> CANYON;
   public static final ConfiguredWorldCarver<CaveCarverConfiguration> NETHER_CAVE;

   public Carvers() {
      super();
   }

   private static <WC extends CarverConfiguration> ConfiguredWorldCarver<WC> register(String var0, ConfiguredWorldCarver<WC> var1) {
      return (ConfiguredWorldCarver)BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_CARVER, (String)var0, var1);
   }

   static {
      CAVE = register("cave", WorldCarver.CAVE.configured(new CaveCarverConfiguration(0.15F, UniformHeight.method_24(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)), UniformFloat.method_18(0.1F, 0.9F), VerticalAnchor.aboveBottom(8), CarverDebugSettings.method_135(false, Blocks.CRIMSON_BUTTON.defaultBlockState()), UniformFloat.method_18(0.7F, 1.4F), UniformFloat.method_18(0.8F, 1.3F), UniformFloat.method_18(-1.0F, -0.4F))));
      CAVE_EXTRA_UNDERGROUND = register("cave_extra_underground", WorldCarver.CAVE.configured(new CaveCarverConfiguration(0.07F, UniformHeight.method_24(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(47)), UniformFloat.method_18(0.1F, 0.9F), VerticalAnchor.aboveBottom(8), CarverDebugSettings.method_135(false, Blocks.OAK_BUTTON.defaultBlockState()), UniformFloat.method_18(0.7F, 1.4F), UniformFloat.method_18(0.8F, 1.3F), UniformFloat.method_18(-1.0F, -0.4F))));
      CANYON = register("canyon", WorldCarver.CANYON.configured(new CanyonCarverConfiguration(0.01F, UniformHeight.method_24(VerticalAnchor.absolute(10), VerticalAnchor.absolute(67)), ConstantFloat.method_19(3.0F), VerticalAnchor.aboveBottom(8), CarverDebugSettings.method_135(false, Blocks.WARPED_BUTTON.defaultBlockState()), UniformFloat.method_18(-0.125F, 0.125F), new CanyonCarverConfiguration.CanyonShapeConfiguration(UniformFloat.method_18(0.75F, 1.0F), TrapezoidFloat.method_17(0.0F, 6.0F, 2.0F), 3, UniformFloat.method_18(0.75F, 1.0F), 1.0F, 0.0F))));
      NETHER_CAVE = register("nether_cave", WorldCarver.NETHER_CAVE.configured(new CaveCarverConfiguration(0.2F, UniformHeight.method_24(VerticalAnchor.absolute(0), VerticalAnchor.belowTop(1)), ConstantFloat.method_19(0.5F), VerticalAnchor.aboveBottom(10), false, ConstantFloat.method_19(1.0F), ConstantFloat.method_19(1.0F), ConstantFloat.method_19(-0.7F))));
   }
}
