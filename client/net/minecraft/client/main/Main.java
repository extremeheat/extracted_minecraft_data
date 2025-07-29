package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
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
import javax.annotation.Nullable;
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
import net.minecraft.util.GsonHelper;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.apache.commons.lang3.StringEscapeUtils;
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
      ArgumentAcceptingOptionSpec var28 = var1.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var29 = var1.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var30 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var31 = var1.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var32 = var1.nonOptions();
      OptionSet var33 = var1.parse(var0);
      File var34 = (File)parseArgument(var33, var10);
      String var35 = (String)parseArgument(var33, var23);
      String var38 = "Pre-bootstrap";

      Logger var36;
      GameConfig var37;
      try {
         if (var33.has(var3)) {
            JvmProfiler.INSTANCE.start(Environment.CLIENT);
         }

         if (var33.has(var4)) {
            TracyBootstrap.setup();
         }

         Stopwatch var39 = Stopwatch.createStarted(Ticker.systemTicker());
         Stopwatch var84 = Stopwatch.createStarted(Ticker.systemTicker());
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS, var39);
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS, var84);
         SharedConstants.tryDetectVersion();
         TracyClient.reportAppInfo("Minecraft Java Edition " + SharedConstants.getCurrentVersion().name());
         CompletableFuture var87 = DataFixers.optimize(DataFixTypes.TYPES_FOR_LEVEL_LIST);
         CrashReport.preload();
         var36 = LogUtils.getLogger();
         var38 = "Bootstrap";
         Bootstrap.bootStrap();
         ClientBootstrap.bootstrap();
         GameLoadTimesEvent.INSTANCE.setBootstrapTime(Bootstrap.bootstrapDuration.get());
         Bootstrap.validate();
         var38 = "Argument parsing";
         List var42 = var33.valuesOf(var32);
         if (!var42.isEmpty()) {
            var36.info("Completely ignored arguments: {}", var42);
         }

         String var43 = (String)parseArgument(var33, var13);
         Proxy var44 = Proxy.NO_PROXY;
         if (var43 != null) {
            try {
               var44 = new Proxy(Type.SOCKS, new InetSocketAddress(var43, (Integer)parseArgument(var33, var14)));
            } catch (Exception var79) {
            }
         }

         final String var45 = (String)parseArgument(var33, var15);
         final String var46 = (String)parseArgument(var33, var16);
         if (!var44.equals(Proxy.NO_PROXY) && stringHasValue(var45) && stringHasValue(var46)) {
            Authenticator.setDefault(new Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(var45, var46.toCharArray());
               }
            });
         }

         int var47 = (Integer)parseArgument(var33, var24);
         int var48 = (Integer)parseArgument(var33, var25);
         OptionalInt var49 = ofNullable((Integer)parseArgument(var33, var26));
         OptionalInt var50 = ofNullable((Integer)parseArgument(var33, var27));
         boolean var51 = var33.has("fullscreen");
         boolean var52 = var33.has("demo");
         boolean var53 = var33.has("disableMultiplayer");
         boolean var54 = var33.has("disableChat");
         boolean var55 = !var33.has(var5);
         boolean var56 = var33.has(var2);
         Gson var57 = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
         PropertyMap var58 = (PropertyMap)GsonHelper.fromJson(var57, (String)parseArgument(var33, var28), PropertyMap.class);
         PropertyMap var59 = (PropertyMap)GsonHelper.fromJson(var57, (String)parseArgument(var33, var29), PropertyMap.class);
         String var60 = (String)parseArgument(var33, var31);
         File var61 = var33.has(var11) ? (File)parseArgument(var33, var11) : new File(var34, "assets/");
         File var62 = var33.has(var12) ? (File)parseArgument(var33, var12) : new File(var34, "resourcepacks/");
         UUID var63 = hasValidUuid(var19, var33, var36) ? UndashedUuid.fromStringLenient((String)var19.value(var33)) : UUIDUtil.createOfflinePlayerUUID((String)var17.value(var33));
         String var64 = var33.has(var30) ? (String)var30.value(var33) : null;
         String var65 = (String)var33.valueOf(var20);
         String var66 = (String)var33.valueOf(var21);
         String var67 = (String)parseArgument(var33, var6);
         GameConfig.QuickPlayVariant var68 = getQuickPlayVariant(var33, var7, var8, var9);
         User var69 = new User((String)var17.value(var33), var63, (String)var22.value(var33), emptyStringToEmptyOptional(var65), emptyStringToEmptyOptional(var66));
         var37 = new GameConfig(new GameConfig.UserData(var69, var58, var59, var44), new DisplayData(var47, var48, var49, var50, var51), new GameConfig.FolderData(var34, var62, var61, var64), new GameConfig.GameData(var52, var35, var60, var53, var54, var55, var56, var33.has(var18)), new GameConfig.QuickPlayData(var67, var68));
         Util.startTimerHackThread();
         var87.join();
      } catch (Throwable var80) {
         CrashReport var40 = CrashReport.forThrowable(var80, var38);
         CrashReportCategory var41 = var40.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var41);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, var35, (Options)null, var40);
         Minecraft.crash((Minecraft)null, var34, var40);
         return;
      }

      Thread var83 = new Thread("Client Shutdown Thread") {
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
      var83.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(var36));
      Runtime.getRuntime().addShutdownHook(var83);
      Minecraft var85 = null;

      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         var85 = new Minecraft(var37);
      } catch (SilentInitException var77) {
         Util.shutdownExecutors();
         var36.warn("Failed to create window: ", var77);
         return;
      } catch (Throwable var78) {
         CrashReport var89 = CrashReport.forThrowable(var78, "Initializing game");
         CrashReportCategory var90 = var89.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var90);
         Minecraft.fillReport(var85, (LanguageManager)null, var37.game.launchVersion, (Options)null, var89);
         Minecraft.crash(var85, var37.location.gameDirectory, var89);
         return;
      }

      Minecraft var88 = var85;
      var85.run();

      try {
         var88.stop();
      } finally {
         var85.destroy();
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

   @Nullable
   private static String unescapeJavaArgument(@Nullable String var0) {
      return var0 == null ? null : StringEscapeUtils.unescapeJava(var0);
   }

   private static Optional<String> emptyStringToEmptyOptional(String var0) {
      return var0.isEmpty() ? Optional.empty() : Optional.of(var0);
   }

   private static OptionalInt ofNullable(@Nullable Integer var0) {
      return var0 != null ? OptionalInt.of(var0) : OptionalInt.empty();
   }

   @Nullable
   private static <T> T parseArgument(OptionSet var0, OptionSpec<T> var1) {
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
