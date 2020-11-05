package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.User;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
   private static final Logger LOGGER = LogManager.getLogger();

   public Main() {
      super();
   }

   public static void main(String[] var0) {
      OptionParser var1 = new OptionParser();
      var1.allowsUnrecognizedOptions();
      var1.accepts("demo");
      var1.accepts("disableMultiplayer");
      var1.accepts("disableChat");
      var1.accepts("fullscreen");
      var1.accepts("checkGlErrors");
      ArgumentAcceptingOptionSpec var2 = var1.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec var3 = var1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
      ArgumentAcceptingOptionSpec var4 = var1.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec var5 = var1.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var6 = var1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var7 = var1.accepts("dataPackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var8 = var1.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var9 = var1.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec var10 = var1.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var11 = var1.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var12 = var1.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMillis() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec var13 = var1.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var14 = var1.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var15 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var16 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var17 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var18 = var1.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var19 = var1.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var20 = var1.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var21 = var1.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var22 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var23 = var1.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      ArgumentAcceptingOptionSpec var24 = var1.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var25 = var1.nonOptions();
      OptionSet var26 = var1.parse(var0);
      List var27 = var26.valuesOf(var25);
      if (!var27.isEmpty()) {
         System.out.println("Completely ignored arguments: " + var27);
      }

      String var28 = (String)parseArgument(var26, var8);
      Proxy var29 = Proxy.NO_PROXY;
      if (var28 != null) {
         try {
            var29 = new Proxy(Type.SOCKS, new InetSocketAddress(var28, (Integer)parseArgument(var26, var9)));
         } catch (Exception var71) {
         }
      }

      final String var30 = (String)parseArgument(var26, var10);
      final String var31 = (String)parseArgument(var26, var11);
      if (!var29.equals(Proxy.NO_PROXY) && stringHasValue(var30) && stringHasValue(var31)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(var30, var31.toCharArray());
            }
         });
      }

      int var32 = (Integer)parseArgument(var26, var16);
      int var33 = (Integer)parseArgument(var26, var17);
      OptionalInt var34 = ofNullable((Integer)parseArgument(var26, var18));
      OptionalInt var35 = ofNullable((Integer)parseArgument(var26, var19));
      boolean var36 = var26.has("fullscreen");
      boolean var37 = var26.has("demo");
      boolean var38 = var26.has("disableMultiplayer");
      boolean var39 = var26.has("disableChat");
      String var40 = (String)parseArgument(var26, var15);
      Gson var41 = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap var42 = (PropertyMap)GsonHelper.fromJson(var41, (String)parseArgument(var26, var20), PropertyMap.class);
      PropertyMap var43 = (PropertyMap)GsonHelper.fromJson(var41, (String)parseArgument(var26, var21), PropertyMap.class);
      String var44 = (String)parseArgument(var26, var24);
      File var45 = (File)parseArgument(var26, var4);
      File var46 = var26.has(var5) ? (File)parseArgument(var26, var5) : new File(var45, "assets/");
      File var47 = var26.has(var6) ? (File)parseArgument(var26, var6) : new File(var45, "resourcepacks/");
      String var48 = var26.has(var13) ? (String)var13.value(var26) : Player.createPlayerUUID((String)var12.value(var26)).toString();
      String var49 = var26.has(var22) ? (String)var22.value(var26) : null;
      String var50 = (String)parseArgument(var26, var2);
      Integer var51 = (Integer)parseArgument(var26, var3);
      CrashReport.preload();
      Bootstrap.bootStrap();
      Bootstrap.validate();
      Util.startTimerHackThread();
      User var52 = new User((String)var12.value(var26), var48, (String)var14.value(var26), (String)var23.value(var26));
      GameConfig var53 = new GameConfig(new GameConfig.UserData(var52, var42, var43, var29), new DisplayData(var32, var33, var34, var35, var36), new GameConfig.FolderData(var45, var47, var46, var49), new GameConfig.GameData(var37, var40, var44, var38, var39), new GameConfig.ServerData(var50, var51));
      Thread var54 = new Thread("Client Shutdown Thread") {
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
      var54.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      Runtime.getRuntime().addShutdownHook(var54);
      new RenderPipeline();

      final Minecraft var56;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         var56 = new Minecraft(var53);
         RenderSystem.finishInitialization();
      } catch (SilentInitException var69) {
         LOGGER.warn("Failed to create window: ", var69);
         return;
      } catch (Throwable var70) {
         CrashReport var58 = CrashReport.forThrowable(var70, "Initializing game");
         var58.addCategory("Initialization");
         Minecraft.fillReport((LanguageManager)null, var53.game.launchVersion, (Options)null, var58);
         Minecraft.crash(var58);
         return;
      }

      Thread var57;
      if (var56.renderOnThread()) {
         var57 = new Thread("Game thread") {
            public void run() {
               try {
                  RenderSystem.initGameThread(true);
                  var56.run();
               } catch (Throwable var2) {
                  Main.LOGGER.error("Exception in client thread", var2);
               }

            }
         };
         var57.start();

         while(true) {
            if (var56.isRunning()) {
               continue;
            }
         }
      } else {
         var57 = null;

         try {
            RenderSystem.initGameThread(false);
            var56.run();
         } catch (Throwable var68) {
            LOGGER.error("Unhandled game exception", var68);
         }
      }

      try {
         var56.stop();
         if (var57 != null) {
            var57.join();
         }
      } catch (InterruptedException var66) {
         LOGGER.error("Exception during client thread shutdown", var66);
      } finally {
         var56.destroy();
      }

   }

   private static OptionalInt ofNullable(@Nullable Integer var0) {
      return var0 != null ? OptionalInt.of(var0) : OptionalInt.empty();
   }

   @Nullable
   private static <T> T parseArgument(OptionSet var0, OptionSpec<T> var1) {
      try {
         return var0.valueOf(var1);
      } catch (Throwable var5) {
         if (var1 instanceof ArgumentAcceptingOptionSpec) {
            ArgumentAcceptingOptionSpec var3 = (ArgumentAcceptingOptionSpec)var1;
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
