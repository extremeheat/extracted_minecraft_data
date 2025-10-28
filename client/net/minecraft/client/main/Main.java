package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.blaze3d.TracyBootstrap;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Optionull;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.User;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.core.UUIDUtil;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Main {
   public Main() {
      super();
   }

   @DontObfuscate
   public static void main(String[] var0) {
      OptionParser var1 = new OptionParser();
      var1.allowsUnrecognizedOptions();
      var1.accepts("demo");
      var1.accepts("disableMultiplayer");
      var1.accepts("disableChat");
      var1.accepts("fullscreen");
      var1.accepts("checkGlErrors");
      OptionSpecBuilder var2 = var1.accepts("renderDebugLabels");
      OptionSpecBuilder var3 = var1.accepts("jfrProfile");
      OptionSpecBuilder var4 = var1.accepts("tracy");
      OptionSpecBuilder var5 = var1.accepts("tracyNoImages");
      ArgumentAcceptingOptionSpec var6 = var1.accepts("quickPlayPath").withRequiredArg();
      ArgumentAcceptingOptionSpec var7 = var1.accepts("quickPlaySingleplayer").withOptionalArg();
      ArgumentAcceptingOptionSpec var8 = var1.accepts("quickPlayMultiplayer").withRequiredArg();
      ArgumentAcceptingOptionSpec var9 = var1.accepts("quickPlayRealms").withRequiredArg();
      ArgumentAcceptingOptionSpec var10 = var1.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec var11 = var1.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var12 = var1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var13 = var1.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var14 = var1.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec var15 = var1.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var16 = var1.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var17 = var1.accepts("username").withRequiredArg().defaultsTo("Player" + System.currentTimeMillis() % 1000L, new String[0]);
      OptionSpecBuilder var18 = var1.accepts("offlineDeveloperMode");
      ArgumentAcceptingOptionSpec var19 = var1.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var20 = var1.accepts("xuid").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var21 = var1.accepts("clientId").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var22 = var1.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var23 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var24 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var25 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var26 = var1.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var27 = var1.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var28 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var29 = var1.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var30 = var1.nonOptions();
      OptionSet var31 = var1.parse(var0);
      File var32 = (File)parseArgument(var31, var10);
      String var33 = (String)parseArgument(var31, var23);
      String var36 = "Pre-bootstrap";

      Logger var34;
      GameConfig var35;
      try {
         if (var31.has(var3)) {
            JvmProfiler.INSTANCE.start(Environment.CLIENT);
         }

         if (var31.has(var4)) {
            TracyBootstrap.setup();
         }

         Stopwatch var37 = Stopwatch.createStarted(Ticker.systemTicker());
         Stopwatch var79 = Stopwatch.createStarted(Ticker.systemTicker());
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS, var37);
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS, var79);
         SharedConstants.tryDetectVersion();
         TracyClient.reportAppInfo("Minecraft Java Edition " + SharedConstants.getCurrentVersion().name());
         CompletableFuture var82 = DataFixers.optimize(DataFixTypes.TYPES_FOR_LEVEL_LIST);
         CrashReport.preload();
         var34 = LogUtils.getLogger();
         var36 = "Bootstrap";
         Bootstrap.bootStrap();
         ClientBootstrap.bootstrap();
         GameLoadTimesEvent.INSTANCE.setBootstrapTime(Bootstrap.bootstrapDuration.get());
         Bootstrap.validate();
         var36 = "Argument parsing";
         List var40 = var31.valuesOf(var30);
         if (!var40.isEmpty()) {
            var34.info("Completely ignored arguments: {}", var40);
         }

         String var41 = (String)parseArgument(var31, var13);
         Proxy var42 = Proxy.NO_PROXY;
         if (var41 != null) {
            try {
               var42 = new Proxy(Type.SOCKS, new InetSocketAddress(var41, (Integer)parseArgument(var31, var14)));
            } catch (Exception var74) {
            }
         }

         final String var43 = (String)parseArgument(var31, var15);
         final String var44 = (String)parseArgument(var31, var16);
         if (!var42.equals(Proxy.NO_PROXY) && stringHasValue(var43) && stringHasValue(var44)) {
            Authenticator.setDefault(new Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(var43, var44.toCharArray());
               }
            });
         }

         int var45 = (Integer)parseArgument(var31, var24);
         int var46 = (Integer)parseArgument(var31, var25);
         OptionalInt var47 = ofNullable((Integer)parseArgument(var31, var26));
         OptionalInt var48 = ofNullable((Integer)parseArgument(var31, var27));
         boolean var49 = var31.has("fullscreen");
         boolean var50 = var31.has("demo");
         boolean var51 = var31.has("disableMultiplayer");
         boolean var52 = var31.has("disableChat");
         boolean var53 = !var31.has(var5);
         boolean var54 = var31.has(var2);
         String var55 = (String)parseArgument(var31, var29);
         File var56 = var31.has(var11) ? (File)parseArgument(var31, var11) : new File(var32, "assets/");
         File var57 = var31.has(var12) ? (File)parseArgument(var31, var12) : new File(var32, "resourcepacks/");
         UUID var58 = hasValidUuid(var19, var31, var34) ? UndashedUuid.fromStringLenient((String)var19.value(var31)) : UUIDUtil.createOfflinePlayerUUID((String)var17.value(var31));
         String var59 = var31.has(var28) ? (String)var28.value(var31) : null;
         String var60 = (String)var31.valueOf(var20);
         String var61 = (String)var31.valueOf(var21);
         String var62 = (String)parseArgument(var31, var6);
         GameConfig.QuickPlayVariant var63 = getQuickPlayVariant(var31, var7, var8, var9);
         User var64 = new User((String)var17.value(var31), var58, (String)var22.value(var31), emptyStringToEmptyOptional(var60), emptyStringToEmptyOptional(var61));
         var35 = new GameConfig(new GameConfig.UserData(var64, var42), new DisplayData(var45, var46, var47, var48, var49), new GameConfig.FolderData(var32, var57, var56, var59), new GameConfig.GameData(var50, var33, var55, var51, var52, var53, var54, var31.has(var18)), new GameConfig.QuickPlayData(var62, var63));
         Util.startTimerHackThread();
         var82.join();
      } catch (Throwable var75) {
         CrashReport var38 = CrashReport.forThrowable(var75, var36);
         CrashReportCategory var39 = var38.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var39);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, var33, (Options)null, var38);
         Minecraft.crash((Minecraft)null, var32, var38);
         return;
      }

      Thread var78 = new Thread("Client Shutdown Thread") {
         public void run() {
            Minecraft var1 = Minecraft.getInstance();
            if (var1 != null) {
               IntegratedServer var2 = var1.getSingleplayerServer();
               if (var2 != null) {
                  var2.halt(true);
               }

            }
         }
      };
      var78.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(var34));
      Runtime.getRuntime().addShutdownHook(var78);
      Minecraft var80 = null;

      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         var80 = new Minecraft(var35);
      } catch (SilentInitException var72) {
         Util.shutdownExecutors();
         var34.warn("Failed to create window: ", var72);
         return;
      } catch (Throwable var73) {
         CrashReport var84 = CrashReport.forThrowable(var73, "Initializing game");
         CrashReportCategory var85 = var84.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var85);
         Minecraft.fillReport(var80, (LanguageManager)null, var35.game.launchVersion, (Options)null, var84);
         Minecraft.crash(var80, var35.location.gameDirectory, var84);
         return;
      }

      Minecraft var83 = var80;
      var80.run();

      try {
         var83.stop();
      } finally {
         var80.destroy();
      }

   }

   private static GameConfig.QuickPlayVariant getQuickPlayVariant(OptionSet var0, OptionSpec<String> var1, OptionSpec<String> var2, OptionSpec<String> var3) {
      Stream var10000 = Stream.of(var1, var2, var3);
      Objects.requireNonNull(var0);
      long var4 = var10000.filter(var0::has).count();
      if (var4 == 0L) {
         return GameConfig.QuickPlayVariant.DISABLED;
      } else if (var4 > 1L) {
         throw new IllegalArgumentException("Only one quick play option can be specified");
      } else if (var0.has(var1)) {
         String var8 = unescapeJavaArgument((String)parseArgument(var0, var1));
         return new GameConfig.QuickPlaySinglePlayerData(var8);
      } else if (var0.has(var2)) {
         String var7 = unescapeJavaArgument((String)parseArgument(var0, var2));
         return (GameConfig.QuickPlayVariant)Optionull.mapOrDefault(var7, GameConfig.QuickPlayMultiplayerData::new, GameConfig.QuickPlayVariant.DISABLED);
      } else if (var0.has(var3)) {
         String var6 = unescapeJavaArgument((String)parseArgument(var0, var3));
         return (GameConfig.QuickPlayVariant)Optionull.mapOrDefault(var6, GameConfig.QuickPlayRealmsData::new, GameConfig.QuickPlayVariant.DISABLED);
      } else {
         return GameConfig.QuickPlayVariant.DISABLED;
      }
   }

   private static @Nullable String unescapeJavaArgument(@Nullable String var0) {
      return var0 == null ? null : StringEscapeUtils.unescapeJava(var0);
   }

   private static Optional<String> emptyStringToEmptyOptional(String var0) {
      return var0.isEmpty() ? Optional.empty() : Optional.of(var0);
   }

   private static OptionalInt ofNullable(@Nullable Integer var0) {
      return var0 != null ? OptionalInt.of(var0) : OptionalInt.empty();
   }

   private static <T> @Nullable T parseArgument(OptionSet var0, OptionSpec<T> var1) {
      try {
         return (T)var0.valueOf(var1);
      } catch (Throwable var5) {
         if (var1 instanceof ArgumentAcceptingOptionSpec var3) {
            List var4 = var3.defaultValues();
            if (!var4.isEmpty()) {
               return (T)var4.get(0);
            }
         }

         throw var5;
      }
   }

   private static boolean stringHasValue(@Nullable String var0) {
      return var0 != null && !var0.isEmpty();
   }

   private static boolean hasValidUuid(OptionSpec<String> var0, OptionSet var1, Logger var2) {
      return var1.has(var0) && isUuidValid(var0, var1, var2);
   }

   private static boolean isUuidValid(OptionSpec<String> var0, OptionSet var1, Logger var2) {
      try {
         UndashedUuid.fromStringLenient((String)var0.value(var1));
         return true;
      } catch (IllegalArgumentException var4) {
         var2.warn("Invalid UUID: '{}", var0.value(var1));
         return false;
      }
   }

   static {
      System.setProperty("java.awt.headless", "true");
   }
}
