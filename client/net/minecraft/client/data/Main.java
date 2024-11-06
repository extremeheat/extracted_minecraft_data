package net.minecraft.client.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.SuppressForbidden;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;

public class Main {
   public Main() {
      super();
   }

   @DontObfuscate
   @SuppressForbidden(
      a = "System.out needed before bootstrap"
   )
   public static void main(String[] var0) throws IOException {
      SharedConstants.tryDetectVersion();
      OptionParser var1 = new OptionParser();
      AbstractOptionSpec var2 = var1.accepts("help", "Show the help menu").forHelp();
      OptionSpecBuilder var3 = var1.accepts("client", "Include client generators");
      OptionSpecBuilder var4 = var1.accepts("all", "Include all generators");
      ArgumentAcceptingOptionSpec var5 = var1.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
      OptionSet var6 = var1.parse(var0);
      if (!var6.has(var2) && var6.hasOptions()) {
         Path var7 = Paths.get((String)var5.value(var6));
         boolean var8 = var6.has(var4);
         boolean var9 = var8 || var6.has(var3);
         Bootstrap.bootStrap();
         ClientBootstrap.bootstrap();
         DataGenerator var10 = new DataGenerator(var7, SharedConstants.getCurrentVersion(), true);
         addClientProviders(var10, var9);
         var10.run();
      } else {
         var1.printHelpOn(System.out);
      }
   }

   public static void addClientProviders(DataGenerator var0, boolean var1) {
      DataGenerator.PackGenerator var2 = var0.getVanillaPack(var1);
      var2.addProvider(ModelProvider::new);
      var2.addProvider(EquipmentAssetProvider::new);
   }
}
