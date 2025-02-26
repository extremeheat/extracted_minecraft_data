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
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
      ArgumentAcceptingOptionSpec var7 = var1.accepts("quickPlaySingleplayer").withRequiredArg();
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
      ArgumentAcceptingOptionSpec var18 = var1.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var19 = var1.accepts("xuid").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var20 = var1.accepts("clientId").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var21 = var1.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var22 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var23 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var24 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var25 = var1.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var26 = var1.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var27 = var1.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var28 = var1.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var29 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var30 = var1.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      ArgumentAcceptingOptionSpec var31 = var1.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var32 = var1.nonOptions();
      OptionSet var33 = var1.parse(var0);
      File var34 = (File)parseArgument(var33, var10);
      String var35 = (String)parseArgument(var33, var22);
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
         Stopwatch var88 = Stopwatch.createStarted(Ticker.systemTicker());
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS, var39);
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS, var88);
         SharedConstants.tryDetectVersion();
         TracyClient.reportAppInfo("Minecraft Java Edition " + SharedConstants.getCurrentVersion().getName());
         CompletableFuture var91 = DataFixers.optimize(DataFixTypes.TYPES_FOR_LEVEL_LIST);
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

         String var43 = (String)var30.value(var33);
         User.Type var44 = User.Type.byName(var43);
         if (var44 == null) {
            var36.warn("Unrecognized user type: {}", var43);
         }

         String var45 = (String)parseArgument(var33, var13);
         Proxy var46 = Proxy.NO_PROXY;
         if (var45 != null) {
            try {
               var46 = new Proxy(Type.SOCKS, new InetSocketAddress(var45, (Integer)parseArgument(var33, var14)));
            } catch (Exception var83) {
            }
         }

         final String var47 = (String)parseArgument(var33, var15);
         final String var48 = (String)parseArgument(var33, var16);
         if (!var46.equals(Proxy.NO_PROXY) && stringHasValue(var47) && stringHasValue(var48)) {
            Authenticator.setDefault(new Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(var47, var48.toCharArray());
               }
            });
         }

         int var49 = (Integer)parseArgument(var33, var23);
         int var50 = (Integer)parseArgument(var33, var24);
         OptionalInt var51 = ofNullable((Integer)parseArgument(var33, var25));
         OptionalInt var52 = ofNullable((Integer)parseArgument(var33, var26));
         boolean var53 = var33.has("fullscreen");
         boolean var54 = var33.has("demo");
         boolean var55 = var33.has("disableMultiplayer");
         boolean var56 = var33.has("disableChat");
         boolean var57 = !var33.has(var5);
         boolean var58 = var33.has(var2);
         Gson var59 = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
         PropertyMap var60 = (PropertyMap)GsonHelper.fromJson(var59, (String)parseArgument(var33, var27), PropertyMap.class);
         PropertyMap var61 = (PropertyMap)GsonHelper.fromJson(var59, (String)parseArgument(var33, var28), PropertyMap.class);
         String var62 = (String)parseArgument(var33, var31);
         File var63 = var33.has(var11) ? (File)parseArgument(var33, var11) : new File(var34, "assets/");
         File var64 = var33.has(var12) ? (File)parseArgument(var33, var12) : new File(var34, "resourcepacks/");
         UUID var65 = hasValidUuid(var18, var33, var36) ? UndashedUuid.fromStringLenient((String)var18.value(var33)) : UUIDUtil.createOfflinePlayerUUID((String)var17.value(var33));
         String var66 = var33.has(var29) ? (String)var29.value(var33) : null;
         String var67 = (String)var33.valueOf(var19);
         String var68 = (String)var33.valueOf(var20);
         String var69 = (String)parseArgument(var33, var6);
         String var70 = unescapeJavaArgument((String)parseArgument(var33, var7));
         String var71 = unescapeJavaArgument((String)parseArgument(var33, var8));
         String var72 = unescapeJavaArgument((String)parseArgument(var33, var9));
         User var73 = new User((String)var17.value(var33), var65, (String)var21.value(var33), emptyStringToEmptyOptional(var67), emptyStringToEmptyOptional(var68), var44);
         var37 = new GameConfig(new GameConfig.UserData(var73, var60, var61, var46), new DisplayData(var49, var50, var51, var52, var53), new GameConfig.FolderData(var34, var64, var63, var66), new GameConfig.GameData(var54, var35, var62, var55, var56, var57, var58), new GameConfig.QuickPlayData(var69, var70, var71, var72));
         Util.startTimerHackThread();
         var91.join();
      } catch (Throwable var84) {
         CrashReport var40 = CrashReport.forThrowable(var84, var38);
         CrashReportCategory var41 = var40.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var41);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, var35, (Options)null, var40);
         Minecraft.crash((Minecraft)null, var34, var40);
         return;
      }

      Thread var87 = new Thread("Client Shutdown Thread") {
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
      var87.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(var36));
      Runtime.getRuntime().addShutdownHook(var87);
      Minecraft var89 = null;

      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         var89 = new Minecraft(var37);
      } catch (SilentInitException var81) {
         Util.shutdownExecutors();
         var36.warn("Failed to create window: ", var81);
         return;
      } catch (Throwable var82) {
         CrashReport var93 = CrashReport.forThrowable(var82, "Initializing game");
         CrashReportCategory var94 = var93.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var94);
         Minecraft.fillReport(var89, (LanguageManager)null, var37.game.launchVersion, (Options)null, var93);
         Minecraft.crash(var89, var37.location.gameDirectory, var93);
         return;
      }

      Minecraft var92 = var89;
      var89.run();

      try {
         var92.stop();
      } finally {
         var89.destroy();
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
