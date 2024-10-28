package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.TracyBootstrap;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
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
      OptionSpecBuilder var2 = var1.accepts("jfrProfile");
      OptionSpecBuilder var3 = var1.accepts("tracy");
      OptionSpecBuilder var4 = var1.accepts("tracyNoImages");
      ArgumentAcceptingOptionSpec var5 = var1.accepts("quickPlayPath").withRequiredArg();
      ArgumentAcceptingOptionSpec var6 = var1.accepts("quickPlaySingleplayer").withRequiredArg();
      ArgumentAcceptingOptionSpec var7 = var1.accepts("quickPlayMultiplayer").withRequiredArg();
      ArgumentAcceptingOptionSpec var8 = var1.accepts("quickPlayRealms").withRequiredArg();
      ArgumentAcceptingOptionSpec var9 = var1.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec var10 = var1.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var11 = var1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var12 = var1.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var13 = var1.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec var14 = var1.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var15 = var1.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var16 = var1.accepts("username").withRequiredArg().defaultsTo("Player" + System.currentTimeMillis() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec var17 = var1.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var18 = var1.accepts("xuid").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var19 = var1.accepts("clientId").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var20 = var1.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var21 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var22 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var23 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var24 = var1.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var25 = var1.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var26 = var1.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var27 = var1.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var28 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var29 = var1.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      ArgumentAcceptingOptionSpec var30 = var1.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var31 = var1.nonOptions();
      OptionSet var32 = var1.parse(var0);
      File var33 = (File)parseArgument(var32, var9);
      String var34 = (String)parseArgument(var32, var21);
      String var37 = "Pre-bootstrap";

      Logger var35;
      GameConfig var36;
      CrashReport var39;
      try {
         if (var32.has(var2)) {
            JvmProfiler.INSTANCE.start(Environment.CLIENT);
         }

         if (var32.has(var3)) {
            TracyBootstrap.setup();
         }

         Stopwatch var38 = Stopwatch.createStarted(Ticker.systemTicker());
         Stopwatch var84 = Stopwatch.createStarted(Ticker.systemTicker());
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS, var38);
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS, var84);
         SharedConstants.tryDetectVersion();
         TracyClient.reportAppInfo("Minecraft Java Edition " + SharedConstants.getCurrentVersion().getName());
         CompletableFuture var86 = DataFixers.optimize(DataFixTypes.TYPES_FOR_LEVEL_LIST);
         CrashReport.preload();
         var35 = LogUtils.getLogger();
         var37 = "Bootstrap";
         Bootstrap.bootStrap();
         GameLoadTimesEvent.INSTANCE.setBootstrapTime(Bootstrap.bootstrapDuration.get());
         Bootstrap.validate();
         var37 = "Argument parsing";
         List var41 = var32.valuesOf(var31);
         if (!var41.isEmpty()) {
            var35.info("Completely ignored arguments: {}", var41);
         }

         String var42 = (String)var29.value(var32);
         User.Type var43 = User.Type.byName(var42);
         if (var43 == null) {
            var35.warn("Unrecognized user type: {}", var42);
         }

         String var44 = (String)parseArgument(var32, var12);
         Proxy var45 = Proxy.NO_PROXY;
         if (var44 != null) {
            try {
               var45 = new Proxy(Type.SOCKS, new InetSocketAddress(var44, (Integer)parseArgument(var32, var13)));
            } catch (Exception var81) {
            }
         }

         final String var46 = (String)parseArgument(var32, var14);
         final String var47 = (String)parseArgument(var32, var15);
         if (!var45.equals(Proxy.NO_PROXY) && stringHasValue(var46) && stringHasValue(var47)) {
            Authenticator.setDefault(new Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(var46, var47.toCharArray());
               }
            });
         }

         int var48 = (Integer)parseArgument(var32, var22);
         int var49 = (Integer)parseArgument(var32, var23);
         OptionalInt var50 = ofNullable((Integer)parseArgument(var32, var24));
         OptionalInt var51 = ofNullable((Integer)parseArgument(var32, var25));
         boolean var52 = var32.has("fullscreen");
         boolean var53 = var32.has("demo");
         boolean var54 = var32.has("disableMultiplayer");
         boolean var55 = var32.has("disableChat");
         boolean var56 = !var32.has(var4);
         Gson var57 = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
         PropertyMap var58 = (PropertyMap)GsonHelper.fromJson(var57, (String)parseArgument(var32, var26), PropertyMap.class);
         PropertyMap var59 = (PropertyMap)GsonHelper.fromJson(var57, (String)parseArgument(var32, var27), PropertyMap.class);
         String var60 = (String)parseArgument(var32, var30);
         File var61 = var32.has(var10) ? (File)parseArgument(var32, var10) : new File(var33, "assets/");
         File var62 = var32.has(var11) ? (File)parseArgument(var32, var11) : new File(var33, "resourcepacks/");
         UUID var63 = hasValidUuid(var17, var32, var35) ? UndashedUuid.fromStringLenient((String)var17.value(var32)) : UUIDUtil.createOfflinePlayerUUID((String)var16.value(var32));
         String var64 = var32.has(var28) ? (String)var28.value(var32) : null;
         String var65 = (String)var32.valueOf(var18);
         String var66 = (String)var32.valueOf(var19);
         String var67 = (String)parseArgument(var32, var5);
         String var68 = unescapeJavaArgument((String)parseArgument(var32, var6));
         String var69 = unescapeJavaArgument((String)parseArgument(var32, var7));
         String var70 = unescapeJavaArgument((String)parseArgument(var32, var8));
         User var71 = new User((String)var16.value(var32), var63, (String)var20.value(var32), emptyStringToEmptyOptional(var65), emptyStringToEmptyOptional(var66), var43);
         var36 = new GameConfig(new GameConfig.UserData(var71, var58, var59, var45), new DisplayData(var48, var49, var50, var51, var52), new GameConfig.FolderData(var33, var62, var61, var64), new GameConfig.GameData(var53, var34, var60, var54, var55, var56), new GameConfig.QuickPlayData(var67, var68, var69, var70));
         Util.startTimerHackThread();
         var86.join();
      } catch (Throwable var82) {
         var39 = CrashReport.forThrowable(var82, var37);
         CrashReportCategory var40 = var39.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var40);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, var34, (Options)null, var39);
         Minecraft.crash((Minecraft)null, var33, var39);
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
      var83.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(var35));
      Runtime.getRuntime().addShutdownHook(var83);
      var39 = null;

      Minecraft var85;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         var85 = new Minecraft(var36);
         RenderSystem.finishInitialization();
      } catch (SilentInitException var79) {
         Util.shutdownExecutors();
         var35.warn("Failed to create window: ", var79);
         return;
      } catch (Throwable var80) {
         CrashReport var88 = CrashReport.forThrowable(var80, "Initializing game");
         CrashReportCategory var89 = var88.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var89);
         Minecraft.fillReport(var39, (LanguageManager)null, var36.game.launchVersion, (Options)null, var88);
         Minecraft.crash(var39, var36.location.gameDirectory, var88);
         return;
      }

      Minecraft var87 = var85;
      var85.run();
      BufferUploader.reset();

      try {
         var87.stop();
      } finally {
         var85.destroy();
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
         return var0.valueOf(var1);
      } catch (Throwable var5) {
         if (var1 instanceof ArgumentAcceptingOptionSpec var3) {
            List var4 = var3.defaultValues();
            if (!var4.isEmpty()) {
               return var4.get(0);
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
