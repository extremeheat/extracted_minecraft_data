package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
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
import net.minecraft.client.Options;
import net.minecraft.client.User;
import net.minecraft.client.resources.language.LanguageManager;
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
      run(var0, true);
   }

   public static void run(String[] var0, boolean var1) {
      SharedConstants.tryDetectVersion();
      if (var1) {
         SharedConstants.enableDataFixerOptimizations();
      }

      OptionParser var2 = new OptionParser();
      var2.allowsUnrecognizedOptions();
      var2.accepts("demo");
      var2.accepts("disableMultiplayer");
      var2.accepts("disableChat");
      var2.accepts("fullscreen");
      var2.accepts("checkGlErrors");
      OptionSpecBuilder var3 = var2.accepts("jfrProfile");
      ArgumentAcceptingOptionSpec var4 = var2.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec var5 = var2.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
      ArgumentAcceptingOptionSpec var6 = var2.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec var7 = var2.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var8 = var2.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var9 = var2.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var10 = var2.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec var11 = var2.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var12 = var2.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var13 = var2.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMillis() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec var14 = var2.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var15 = var2.accepts("xuid").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var16 = var2.accepts("clientId").withOptionalArg().defaultsTo("", new String[0]);
      ArgumentAcceptingOptionSpec var17 = var2.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var18 = var2.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var19 = var2.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var20 = var2.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var21 = var2.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var22 = var2.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var23 = var2.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var24 = var2.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var25 = var2.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var26 = var2.accepts("userType").withRequiredArg().defaultsTo(User.Type.LEGACY.getName(), new String[0]);
      ArgumentAcceptingOptionSpec var27 = var2.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var28 = var2.nonOptions();
      OptionSet var29 = var2.parse(var0);
      List var30 = var29.valuesOf(var28);
      if (!var30.isEmpty()) {
         System.out.println("Completely ignored arguments: " + var30);
      }

      String var31 = (String)parseArgument(var29, var9);
      Proxy var32 = Proxy.NO_PROXY;
      if (var31 != null) {
         try {
            var32 = new Proxy(Type.SOCKS, new InetSocketAddress(var31, (Integer)parseArgument(var29, var10)));
         } catch (Exception var78) {
         }
      }

      final String var33 = (String)parseArgument(var29, var11);
      final String var34 = (String)parseArgument(var29, var12);
      if (!var32.equals(Proxy.NO_PROXY) && stringHasValue(var33) && stringHasValue(var34)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(var33, var34.toCharArray());
            }
         });
      }

      int var35 = (Integer)parseArgument(var29, var19);
      int var36 = (Integer)parseArgument(var29, var20);
      OptionalInt var37 = ofNullable((Integer)parseArgument(var29, var21));
      OptionalInt var38 = ofNullable((Integer)parseArgument(var29, var22));
      boolean var39 = var29.has("fullscreen");
      boolean var40 = var29.has("demo");
      boolean var41 = var29.has("disableMultiplayer");
      boolean var42 = var29.has("disableChat");
      String var43 = (String)parseArgument(var29, var18);
      Gson var44 = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
      PropertyMap var45 = (PropertyMap)GsonHelper.fromJson(var44, (String)parseArgument(var29, var23), PropertyMap.class);
      PropertyMap var46 = (PropertyMap)GsonHelper.fromJson(var44, (String)parseArgument(var29, var24), PropertyMap.class);
      String var47 = (String)parseArgument(var29, var27);
      File var48 = (File)parseArgument(var29, var6);
      File var49 = var29.has(var7) ? (File)parseArgument(var29, var7) : new File(var48, "assets/");
      File var50 = var29.has(var8) ? (File)parseArgument(var29, var8) : new File(var48, "resourcepacks/");
      String var51 = var29.has(var14) ? (String)var14.value(var29) : UUIDUtil.createOfflinePlayerUUID((String)var13.value(var29)).toString();
      String var52 = var29.has(var25) ? (String)var25.value(var29) : null;
      String var53 = (String)var29.valueOf(var15);
      String var54 = (String)var29.valueOf(var16);
      String var55 = (String)parseArgument(var29, var4);
      Integer var56 = (Integer)parseArgument(var29, var5);
      if (var29.has(var3)) {
         JvmProfiler.INSTANCE.start(Environment.CLIENT);
      }

      CrashReport.preload();
      Bootstrap.bootStrap();
      Bootstrap.validate();
      Util.startTimerHackThread();
      String var57 = (String)var26.value(var29);
      User.Type var58 = User.Type.byName(var57);
      if (var58 == null) {
         LOGGER.warn("Unrecognized user type: {}", var57);
      }

      User var59 = new User((String)var13.value(var29), var51, (String)var17.value(var29), emptyStringToEmptyOptional(var53), emptyStringToEmptyOptional(var54), var58);
      GameConfig var60 = new GameConfig(new GameConfig.UserData(var59, var45, var46, var32), new DisplayData(var35, var36, var37, var38, var39), new GameConfig.FolderData(var48, var50, var49, var52), new GameConfig.GameData(var40, var43, var47, var41, var42), new GameConfig.ServerData(var55, var56));
      Thread var61 = new Thread("Client Shutdown Thread") {
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
      var61.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      Runtime.getRuntime().addShutdownHook(var61);

      final Minecraft var62;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         var62 = new Minecraft(var60);
         RenderSystem.finishInitialization();
      } catch (SilentInitException var76) {
         LOGGER.warn("Failed to create window: ", var76);
         return;
      } catch (Throwable var77) {
         CrashReport var64 = CrashReport.forThrowable(var77, "Initializing game");
         CrashReportCategory var65 = var64.addCategory("Initialization");
         NativeModuleLister.addCrashSection(var65);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, var60.game.launchVersion, (Options)null, var64);
         Minecraft.crash(var64);
         return;
      }

      Thread var63;
      if (var62.renderOnThread()) {
         var63 = new Thread("Game thread") {
            public void run() {
               try {
                  RenderSystem.initGameThread(true);
                  var62.run();
               } catch (Throwable var2) {
                  Main.LOGGER.error("Exception in client thread", var2);
               }

            }
         };
         var63.start();

         while(true) {
            if (var62.isRunning()) {
               continue;
            }
         }
      } else {
         var63 = null;

         try {
            RenderSystem.initGameThread(false);
            var62.run();
         } catch (Throwable var75) {
            LOGGER.error("Unhandled game exception", var75);
         }
      }

      BufferUploader.reset();

      try {
         var62.stop();
         if (var63 != null) {
            var63.join();
         }
      } catch (InterruptedException var73) {
         LOGGER.error("Exception during client thread shutdown", var73);
      } finally {
         var62.destroy();
      }

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

   static {
      System.setProperty("java.awt.headless", "true");
   }
}
