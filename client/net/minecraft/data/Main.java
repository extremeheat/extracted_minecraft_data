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

public class Main {
   public Main() {
      super();
   }

   public static void main(String[] var0) throws IOException {
      OptionParser var1 = new OptionParser();
      AbstractOptionSpec var2 = var1.accepts("help", "Show the help menu").forHelp();
      OptionSpecBuilder var3 = var1.accepts("server", "Include server generators");
      OptionSpecBuilder var4 = var1.accepts("client", "Include client generators");
      OptionSpecBuilder var5 = var1.accepts("dev", "Include development tools");
      OptionSpecBuilder var6 = var1.accepts("reports", "Include data reports");
      OptionSpecBuilder var7 = var1.accepts("all", "Include all generators");
      ArgumentAcceptingOptionSpec var8 = var1.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
      ArgumentAcceptingOptionSpec var9 = var1.accepts("input", "Input folder").withRequiredArg();
      OptionSet var10 = var1.parse(var0);
      if (!var10.has(var2) && var10.hasOptions()) {
         Path var11 = Paths.get((String)var8.value(var10));
         boolean var12 = var10.has(var4) || var10.has(var7);
         boolean var13 = var10.has(var3) || var10.has(var7);
         boolean var14 = var10.has(var5) || var10.has(var7);
         boolean var15 = var10.has(var6) || var10.has(var7);
         DataGenerator var16 = func_200264_a(var11, (Collection)var10.valuesOf(var9).stream().map((var0x) -> {
            return Paths.get(var0x);
         }).collect(Collectors.toList()), var12, var13, var14, var15);
         var16.func_200392_c();
      } else {
         var1.printHelpOn(System.out);
      }
   }

   public static DataGenerator func_200264_a(Path var0, Collection<Path> var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      DataGenerator var6 = new DataGenerator(var0, var1);
      if (var2 || var3) {
         var6.func_200390_a(new SNBTToNBTConverter(var6));
      }

      if (var3) {
         var6.func_200390_a(new FluidTagsProvider(var6));
         var6.func_200390_a(new BlockTagsProvider(var6));
         var6.func_200390_a(new ItemTagsProvider(var6));
         var6.func_200390_a(new RecipeProvider(var6));
         var6.func_200390_a(new AdvancementProvider(var6));
      }

      if (var4) {
         var6.func_200390_a(new NBTToSNBTConverter(var6));
      }

      if (var5) {
         var6.func_200390_a(new BlockListReport(var6));
         var6.func_200390_a(new ItemListReport(var6));
         var6.func_200390_a(new CommandsReport(var6));
      }

      return var6;
   }
}
