package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.info.BiomeParametersDumpReport;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.info.WorldgenRegistryDumpReport;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.CatVariantTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FlatLevelGeneratorPresetTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.GameEventTagsProvider;
import net.minecraft.data.tags.InstrumentTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.obfuscate.DontObfuscate;

public class Main {
   public Main() {
      super();
   }

   @DontObfuscate
   public static void main(String[] var0) throws IOException {
      SharedConstants.tryDetectVersion();
      OptionParser var1 = new OptionParser();
      AbstractOptionSpec var2 = var1.accepts("help", "Show the help menu").forHelp();
      OptionSpecBuilder var3 = var1.accepts("server", "Include server generators");
      OptionSpecBuilder var4 = var1.accepts("client", "Include client generators");
      OptionSpecBuilder var5 = var1.accepts("dev", "Include development tools");
      OptionSpecBuilder var6 = var1.accepts("reports", "Include data reports");
      OptionSpecBuilder var7 = var1.accepts("validate", "Validate inputs");
      OptionSpecBuilder var8 = var1.accepts("all", "Include all generators");
      ArgumentAcceptingOptionSpec var9 = var1.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
      ArgumentAcceptingOptionSpec var10 = var1.accepts("input", "Input folder").withRequiredArg();
      OptionSet var11 = var1.parse(var0);
      if (!var11.has(var2) && var11.hasOptions()) {
         Path var12 = Paths.get((String)var9.value(var11));
         boolean var13 = var11.has(var8);
         boolean var14 = var13 || var11.has(var4);
         boolean var15 = var13 || var11.has(var3);
         boolean var16 = var13 || var11.has(var5);
         boolean var17 = var13 || var11.has(var6);
         boolean var18 = var13 || var11.has(var7);
         DataGenerator var19 = createStandardGenerator(
            var12,
            var11.valuesOf(var10).stream().map(var0x -> Paths.get(var0x)).collect(Collectors.toList()),
            var14,
            var15,
            var16,
            var17,
            var18,
            SharedConstants.getCurrentVersion(),
            true
         );
         var19.run();
      } else {
         var1.printHelpOn(System.out);
      }
   }

   public static DataGenerator createStandardGenerator(
      Path var0, Collection<Path> var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, WorldVersion var7, boolean var8
   ) {
      DataGenerator var9 = new DataGenerator(var0, var1, var7, var8);
      var9.addProvider(var2 || var3, new SnbtToNbt(var9).addFilter(new StructureUpdater()));
      var9.addProvider(var2, new ModelProvider(var9));
      var9.addProvider(var3, new AdvancementProvider(var9));
      var9.addProvider(var3, new LootTableProvider(var9));
      var9.addProvider(var3, new RecipeProvider(var9));
      BlockTagsProvider var10 = new BlockTagsProvider(var9);
      var9.addProvider(var3, var10);
      var9.addProvider(var3, new ItemTagsProvider(var9, var10));
      var9.addProvider(var3, new BannerPatternTagsProvider(var9));
      var9.addProvider(var3, new BiomeTagsProvider(var9));
      var9.addProvider(var3, new CatVariantTagsProvider(var9));
      var9.addProvider(var3, new EntityTypeTagsProvider(var9));
      var9.addProvider(var3, new FlatLevelGeneratorPresetTagsProvider(var9));
      var9.addProvider(var3, new FluidTagsProvider(var9));
      var9.addProvider(var3, new GameEventTagsProvider(var9));
      var9.addProvider(var3, new InstrumentTagsProvider(var9));
      var9.addProvider(var3, new PaintingVariantTagsProvider(var9));
      var9.addProvider(var3, new PoiTypeTagsProvider(var9));
      var9.addProvider(var3, new StructureTagsProvider(var9));
      var9.addProvider(var3, new WorldPresetTagsProvider(var9));
      var9.addProvider(var4, new NbtToSnbt(var9));
      var9.addProvider(var5, new BiomeParametersDumpReport(var9));
      var9.addProvider(var5, new BlockListReport(var9));
      var9.addProvider(var5, new CommandsReport(var9));
      var9.addProvider(var5, new RegistryDumpReport(var9));
      var9.addProvider(var5, new WorldgenRegistryDumpReport(var9));
      return var9;
   }
}
