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
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;

public class Main {
   public static void main(String[] var0) throws IOException {
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
         DataGenerator var19 = createStandardGenerator(var12, (Collection)var11.valuesOf(var10).stream().map((var0x) -> {
            return Paths.get(var0x);
         }).collect(Collectors.toList()), var14, var15, var16, var17, var18);
         var19.run();
      } else {
         var1.printHelpOn(System.out);
      }
   }

   public static DataGenerator createStandardGenerator(Path var0, Collection var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      DataGenerator var7 = new DataGenerator(var0, var1);
      if (var2 || var3) {
         var7.addProvider((new SnbtToNbt(var7)).addFilter(new StructureUpdater()));
      }

      if (var3) {
         var7.addProvider(new FluidTagsProvider(var7));
         var7.addProvider(new BlockTagsProvider(var7));
         var7.addProvider(new ItemTagsProvider(var7));
         var7.addProvider(new EntityTypeTagsProvider(var7));
         var7.addProvider(new RecipeProvider(var7));
         var7.addProvider(new AdvancementProvider(var7));
         var7.addProvider(new LootTableProvider(var7));
      }

      if (var4) {
         var7.addProvider(new NbtToSnbt(var7));
      }

      if (var5) {
         var7.addProvider(new BlockListReport(var7));
         var7.addProvider(new RegistryDumpReport(var7));
         var7.addProvider(new CommandsReport(var7));
      }

      return var7;
   }
}
