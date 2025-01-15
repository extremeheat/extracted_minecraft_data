package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.SuppressForbidden;
import net.minecraft.Util;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class GameTestMainUtil {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String DEFAULT_UNIVERSE_DIR = "gametestserver";
   private static final String LEVEL_NAME = "gametestworld";
   private static final OptionParser parser = new OptionParser();
   private static final OptionSpec<String> universe;
   private static final OptionSpec<File> report;
   private static final OptionSpec<String> tests;
   private static final OptionSpec<Boolean> verify;
   private static final OptionSpec<String> packs;
   private static final OptionSpec<Void> help;

   public GameTestMainUtil() {
      super();
   }

   @SuppressForbidden(
      a = "Using System.err due to no bootstrap"
   )
   public static void runGameTestServer(String[] var0, Consumer<String> var1) throws Exception {
      parser.allowsUnrecognizedOptions();
      OptionSet var2 = parser.parse(var0);
      if (var2.has(help)) {
         parser.printHelpOn(System.err);
      } else {
         if ((Boolean)var2.valueOf(verify) && !var2.has(tests)) {
            LOGGER.error("Please specify a test selection to run the verify option. For example: --verify --tests example:test_something_*");
            System.exit(-1);
         }

         LOGGER.info("Running GameTestMain with cwd '{}', universe path '{}'", System.getProperty("user.dir"), var2.valueOf(universe));
         if (var2.has(report)) {
            GlobalTestReporter.replaceWith(new JUnitLikeTestReporter((File)report.value(var2)));
         }

         Bootstrap.bootStrap();
         Util.startTimerHackThread();
         String var3 = (String)var2.valueOf(universe);
         createOrResetDir(var3);
         var1.accept(var3);
         if (var2.has(packs)) {
            String var4 = (String)var2.valueOf(packs);
            copyPacks(var3, var4);
         }

         LevelStorageSource.LevelStorageAccess var6 = LevelStorageSource.createDefault(Paths.get("gametestserver")).createAccess("gametestworld");
         PackRepository var5 = ServerPacksSource.createVanillaTrustedRepository();
         MinecraftServer.spin((var3x) -> GameTestServer.create(var3x, var6, var5, optionalFromOption(var2, tests), var2.has(verify)));
      }
   }

   private static Optional<String> optionalFromOption(OptionSet var0, OptionSpec<String> var1) {
      return var0.has(var1) ? Optional.of((String)var0.valueOf(var1)) : Optional.empty();
   }

   private static void createOrResetDir(String var0) throws IOException {
      Path var1 = Paths.get(var0);
      if (Files.exists(var1, new LinkOption[0])) {
         FileUtils.deleteDirectory(var1.toFile());
      }

      Files.createDirectories(var1);
   }

   private static void copyPacks(String var0, String var1) throws IOException {
      Path var2 = Paths.get(var0).resolve("gametestworld").resolve("datapacks");
      if (!Files.exists(var2, new LinkOption[0])) {
         Files.createDirectories(var2);
      }

      Path var3 = Paths.get(var1);
      if (Files.exists(var3, new LinkOption[0])) {
         for(Path var5 : Files.list(var3).toList()) {
            Path var6 = var2.resolve(var5.getFileName());
            if (Files.isDirectory(var5, new LinkOption[0])) {
               if (Files.isRegularFile(var5.resolve("pack.mcmeta"), new LinkOption[0])) {
                  FileUtils.copyDirectory(var5.toFile(), var6.toFile());
                  LOGGER.info("Included folder pack " + String.valueOf(var5.getFileName()));
               }
            } else if (var5.endsWith(".zip")) {
               Files.copy(var5, var6);
               LOGGER.info("Included zip pack  " + String.valueOf(var5.getFileName()));
            }
         }
      }

   }

   static {
      universe = parser.accepts("universe", "The path to where the test server world will be created. Any existing folder will be replaced.").withRequiredArg().defaultsTo("gametestserver", new String[0]);
      report = parser.accepts("report", "Exports results in a junit-like XML report at the given path.").withRequiredArg().ofType(File.class);
      tests = parser.accepts("tests", "Which test(s) to run (namespaced ID selector using wildcards). Empty means run all.").withRequiredArg();
      verify = parser.accepts("verify", "Runs the tests specified with `test` or `testNamespace` 100 times for each 90 degree rotation step").withRequiredArg().ofType(Boolean.class).defaultsTo(false, new Boolean[0]);
      packs = parser.accepts("packs", "A folder of datapacks to include in the world").withRequiredArg();
      help = parser.accepts("help").forHelp();
   }
}
