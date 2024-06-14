package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
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
import net.minecraft.client.User;
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
      ArgumentAcceptingOptionSpec var3 = var1.accepts("quickPlayPath").withRequiredArg();
      ArgumentAcceptingOptionSpec var4 = var1.accepts("quickPlaySingleplayer").withRequiredArg();
      ArgumentAcceptingOptionSpec var5 = var1.accepts("quickPlayMultiplayer").withRequiredArg();
      ArgumentAcceptingOptionSpec var6 = var1.accepts("quickPlayRealms").withRequiredArg();
      ArgumentAcceptingOptionSpec var7 = var1.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec var8 = var1.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var9 = var1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var10 = var1.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var11 = var1.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec var12 = var1.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var13 = var1.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var14 = var1.accepts("username").withRequiredArg().defaultsTo("Player" + System.currentTimeMillis() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec var15 = var1.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var16 = var1.accepts("xuid").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var17 = var1.accepts("clientId").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var18 = var1.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var19 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var20 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var21 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var22 = var1.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var23 = var1.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var24 = var1.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var25 = var1.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var26 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var27 = var1.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      ArgumentAcceptingOptionSpec var28 = var1.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var29 = var1.nonOptions();
      OptionSet var30 = var1.parse(var0);
      File var31 = parseArgument(var30, var7);
      String var32 = parseArgument(var30, var19);
      String var35 = "Pre-bootstrap";

      Logger var33;
      GameConfig var34;
      try {
         if (var30.has(var2)) {
            JvmProfiler.INSTANCE.start(Environment.CLIENT);
         }

         Stopwatch var36 = Stopwatch.createStarted(Ticker.systemTicker());
         Stopwatch var81 = Stopwatch.createStarted(Ticker.systemTicker());
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS, var36);
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS, var81);
         SharedConstants.tryDetectVersion();
         CompletableFuture var83 = DataFixers.optimize(DataFixTypes.TYPES_FOR_LEVEL_LIST);
         CrashReport.preload();
         var33 = LogUtils.getLogger();
         var35 = "Bootstrap";
         Bootstrap.bootStrap();
         GameLoadTimesEvent.INSTANCE.setBootstrapTime(Bootstrap.bootstrapDuration.get());
         Bootstrap.validate();
         var35 = "Argument parsing";
         List var39 = var30.valuesOf(var29);
         if (!var39.isEmpty()) {
            var33.info("Completely ignored arguments: {}", var39);
         }

         String var40 = (String)var27.value(var30);
         User.Type var41 = User.Type.byName(var40);
         if (var41 == null) {
            var33.warn("Unrecognized user type: {}", var40);
         }

         String var42 = parseArgument(var30, var10);
         Proxy var43 = Proxy.NO_PROXY;
         if (var42 != null) {
            try {
               var43 = new Proxy(Type.SOCKS, new InetSocketAddress(var42, parseArgument(var30, var11)));
            } catch (Exception var78) {
            }
         }

         final String var44 = parseArgument(var30, var12);
         final String var45 = parseArgument(var30, var13);
         if (!var43.equals(Proxy.NO_PROXY) && stringHasValue(var44) && stringHasValue(var45)) {
            Authenticator.setDefault(new Authenticator() {
               @Override
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(var44, var45.toCharArray());
               }
            });
         }

         int var46 = parseArgument(var30, var20);
         int var47 = parseArgument(var30, var21);
         OptionalInt var48 = ofNullable(parseArgument(var30, var22));
         OptionalInt var49 = ofNullable(parseArgument(var30, var23));
         boolean var50 = var30.has("fullscreen");
         boolean var51 = var30.has("demo");
         boolean var52 = var30.has("disableMultiplayer");
         boolean var53 = var30.has("disableChat");
         Gson var54 = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
         PropertyMap var55 = GsonHelper.fromJson(var54, parseArgument(var30, var24), PropertyMap.class);
         PropertyMap var56 = GsonHelper.fromJson(var54, parseArgument(var30, var25), PropertyMap.class);
         String var57 = parseArgument(var30, var28);
         File var58 = var30.has(var8) ? parseArgument(var30, var8) : new File(var31, "assets/");
         File var59 = var30.has(var9) ? parseArgument(var30, var9) : new File(var31, "resourcepacks/");
         UUID var60 = var30.has(var15)
            ? UndashedUuid.fromStringLenient((String)var15.value(var30))
            : UUIDUtil.createOfflinePlayerUUID((String)var14.value(var30));
         String var61 = var30.has(var26) ? (String)var26.value(var30) : null;
         String var62 = (String)var30.valueOf(var16);
         String var63 = (String)var30.valueOf(var17);
         String var64 = parseArgument(var30, var3);
         String var65 = unescapeJavaArgument(parseArgument(var30, var4));
         String var66 = unescapeJavaArgument(parseArgument(var30, var5));
         String var67 = unescapeJavaArgument(parseArgument(var30, var6));
         User var68 = new User(
            (String)var14.value(var30), var60, (String)var18.value(var30), emptyStringToEmptyOptional(var62), emptyStringToEmptyOptional(var63), var41
         );
         var34 = new GameConfig(
            new GameConfig.UserData(var68, var55, var56, var43),
            new DisplayData(var46, var47, var48, var49, var50),
            new GameConfig.FolderData(var31, var59, var58, var61),
            new GameConfig.GameData(var51, var32, var57, var52, var53),
            new GameConfig.QuickPlayData(var64, var65, var66, var67)
         );
         Util.startTimerHackThread();
         var83.join();
      } catch (Throwable var79) {
         CrashReport var37 = CrashReport.forThrowable(var79, var35);
         CrashReportCategory var38 = var37.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var38);
         Minecraft.fillReport(null, null, var32, null, var37);
         Minecraft.crash(null, var31, var37);
         return;
      }

      Thread var80 = new Thread("Client Shutdown Thread") {
         @Override
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
      var80.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(var33));
      Runtime.getRuntime().addShutdownHook(var80);
      Minecraft var82 = null;

      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         var82 = new Minecraft(var34);
         RenderSystem.finishInitialization();
      } catch (SilentInitException var76) {
         Util.shutdownExecutors();
         var33.warn("Failed to create window: ", var76);
         return;
      } catch (Throwable var77) {
         CrashReport var85 = CrashReport.forThrowable(var77, "Initializing game");
         CrashReportCategory var86 = var85.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var86);
         Minecraft.fillReport(var82, null, var34.game.launchVersion, null, var85);
         Minecraft.crash(var82, var34.location.gameDirectory, var85);
         return;
      }

      Minecraft var84 = var82;
      var82.run();
      BufferUploader.reset();

      try {
         var84.stop();
      } finally {
         var82.destroy();
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

   static {
      System.setProperty("java.awt.headless", "true");
   }
}
