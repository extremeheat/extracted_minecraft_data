package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
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
import net.minecraft.core.UUIDUtil;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class Main {
   static final Logger LOGGER = LogUtils.getLogger();

   public Main() {
      super();
   }

   @DontObfuscate
   public static void main(String[] var0) {
      SharedConstants.tryDetectVersion();
      SharedConstants.enableDataFixerOptimizations();
      OptionParser var1 = new OptionParser();
      var1.allowsUnrecognizedOptions();
      var1.accepts("demo");
      var1.accepts("disableMultiplayer");
      var1.accepts("disableChat");
      var1.accepts("fullscreen");
      var1.accepts("checkGlErrors");
      OptionSpecBuilder var2 = var1.accepts("jfrProfile");
      ArgumentAcceptingOptionSpec var3 = var1.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec var4 = var1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
      ArgumentAcceptingOptionSpec var5 = var1.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec var6 = var1.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var7 = var1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var8 = var1.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var9 = var1.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec var10 = var1.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var11 = var1.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var12 = var1.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMillis() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec var13 = var1.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var14 = var1.accepts("xuid").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var15 = var1.accepts("clientId").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var16 = var1.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var17 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var18 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var19 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var20 = var1.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var21 = var1.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var22 = var1.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var23 = var1.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var24 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var25 = var1.accepts("userType").withRequiredArg().defaultsTo(User.Type.LEGACY.getName(), new String[0]);
      ArgumentAcceptingOptionSpec var26 = var1.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var27 = var1.nonOptions();
      OptionSet var28 = var1.parse(var0);
      List var29 = var28.valuesOf(var27);
      if (!var29.isEmpty()) {
         System.out.println("Completely ignored arguments: " + var29);
      }

      String var30 = parseArgument(var28, var8);
      Proxy var31 = Proxy.NO_PROXY;
      if (var30 != null) {
         try {
            var31 = new Proxy(Type.SOCKS, new InetSocketAddress(var30, parseArgument(var28, var9)));
         } catch (Exception var77) {
         }
      }

      final String var32 = parseArgument(var28, var10);
      final String var33 = parseArgument(var28, var11);
      if (!var31.equals(Proxy.NO_PROXY) && stringHasValue(var32) && stringHasValue(var33)) {
         Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(var32, var33.toCharArray());
            }
         });
      }

      int var34 = parseArgument(var28, var18);
      int var35 = parseArgument(var28, var19);
      OptionalInt var36 = ofNullable(parseArgument(var28, var20));
      OptionalInt var37 = ofNullable(parseArgument(var28, var21));
      boolean var38 = var28.has("fullscreen");
      boolean var39 = var28.has("demo");
      boolean var40 = var28.has("disableMultiplayer");
      boolean var41 = var28.has("disableChat");
      String var42 = parseArgument(var28, var17);
      Gson var43 = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap var44 = GsonHelper.fromJson(var43, parseArgument(var28, var22), PropertyMap.class);
      PropertyMap var45 = GsonHelper.fromJson(var43, parseArgument(var28, var23), PropertyMap.class);
      String var46 = parseArgument(var28, var26);
      File var47 = parseArgument(var28, var5);
      File var48 = var28.has(var6) ? parseArgument(var28, var6) : new File(var47, "assets/");
      File var49 = var28.has(var7) ? parseArgument(var28, var7) : new File(var47, "resourcepacks/");
      String var50 = var28.has(var13) ? (String)var13.value(var28) : UUIDUtil.createOfflinePlayerUUID((String)var12.value(var28)).toString();
      String var51 = var28.has(var24) ? (String)var24.value(var28) : null;
      String var52 = (String)var28.valueOf(var14);
      String var53 = (String)var28.valueOf(var15);
      String var54 = parseArgument(var28, var3);
      Integer var55 = parseArgument(var28, var4);
      if (var28.has(var2)) {
         JvmProfiler.INSTANCE.start(Environment.CLIENT);
      }

      CrashReport.preload();
      Bootstrap.bootStrap();
      Bootstrap.validate();
      Util.startTimerHackThread();
      String var56 = (String)var25.value(var28);
      User.Type var57 = User.Type.byName(var56);
      if (var57 == null) {
         LOGGER.warn("Unrecognized user type: {}", var56);
      }

      User var58 = new User(
         (String)var12.value(var28), var50, (String)var16.value(var28), emptyStringToEmptyOptional(var52), emptyStringToEmptyOptional(var53), var57
      );
      GameConfig var59 = new GameConfig(
         new GameConfig.UserData(var58, var44, var45, var31),
         new DisplayData(var34, var35, var36, var37, var38),
         new GameConfig.FolderData(var47, var49, var48, var51),
         new GameConfig.GameData(var39, var42, var46, var40, var41),
         new GameConfig.ServerData(var54, var55)
      );
      Thread var60 = new Thread("Client Shutdown Thread") {
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
      var60.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      Runtime.getRuntime().addShutdownHook(var60);

      final Minecraft var61;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         var61 = new Minecraft(var59);
         RenderSystem.finishInitialization();
      } catch (SilentInitException var75) {
         LOGGER.warn("Failed to create window: ", var75);
         return;
      } catch (Throwable var76) {
         CrashReport var63 = CrashReport.forThrowable(var76, "Initializing game");
         CrashReportCategory var64 = var63.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var64);
         Minecraft.fillReport(null, null, var59.game.launchVersion, null, var63);
         Minecraft.crash(var63);
         return;
      }

      Thread var62;
      if (var61.renderOnThread()) {
         var62 = new Thread("Game thread") {
            @Override
            public void run() {
               try {
                  RenderSystem.initGameThread(true);
                  var61.run();
               } catch (Throwable var2) {
                  Main.LOGGER.error("Exception in client thread", var2);
               }
            }
         };
         var62.start();

         while(var61.isRunning()) {
         }
      } else {
         var62 = null;

         try {
            RenderSystem.initGameThread(false);
            var61.run();
         } catch (Throwable var74) {
            LOGGER.error("Unhandled game exception", var74);
         }
      }

      BufferUploader.reset();

      try {
         var61.stop();
         if (var62 != null) {
            var62.join();
         }
      } catch (InterruptedException var72) {
         LOGGER.error("Exception during client thread shutdown", var72);
      } finally {
         var61.destroy();
      }
   }

   private static Optional<String> emptyStringToEmptyOptional(String var0) {
      return var0.isEmpty() ? Optional.empty() : Optional.of(var0);
   }

   private static OptionalInt ofNullable(@Nullable Integer var0) {
      return var0 != null ? OptionalInt.of(var0) : OptionalInt.empty();
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
