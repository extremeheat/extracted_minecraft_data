package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.platform.DisplayData;
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
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.server.IntegratedServer;
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
      var1.accepts("fullscreen");
      var1.accepts("checkGlErrors");
      ArgumentAcceptingOptionSpec var2 = var1.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec var3 = var1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
      ArgumentAcceptingOptionSpec var4 = var1.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec var5 = var1.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var6 = var1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var7 = var1.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var8 = var1.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec var9 = var1.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var10 = var1.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var11 = var1.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMillis() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec var12 = var1.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var13 = var1.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var14 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var15 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var16 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var17 = var1.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var18 = var1.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      ArgumentAcceptingOptionSpec var19 = var1.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var20 = var1.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var21 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var22 = var1.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      ArgumentAcceptingOptionSpec var23 = var1.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      NonOptionArgumentSpec var24 = var1.nonOptions();
      OptionSet var25 = var1.parse(var0);
      List var26 = var25.valuesOf(var24);
      if (!var26.isEmpty()) {
         System.out.println("Completely ignored arguments: " + var26);
      }

      String var27 = (String)parseArgument(var25, var7);
      Proxy var28 = Proxy.NO_PROXY;
      if (var27 != null) {
         try {
            var28 = new Proxy(Type.SOCKS, new InetSocketAddress(var27, (Integer)parseArgument(var25, var8)));
         } catch (Exception var52) {
         }
      }

      final String var29 = (String)parseArgument(var25, var9);
      final String var30 = (String)parseArgument(var25, var10);
      if (!var28.equals(Proxy.NO_PROXY) && stringHasValue(var29) && stringHasValue(var30)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(var29, var30.toCharArray());
            }
         });
      }

      int var31 = (Integer)parseArgument(var25, var15);
      int var32 = (Integer)parseArgument(var25, var16);
      OptionalInt var33 = ofNullable((Integer)parseArgument(var25, var17));
      OptionalInt var34 = ofNullable((Integer)parseArgument(var25, var18));
      boolean var35 = var25.has("fullscreen");
      boolean var36 = var25.has("demo");
      String var37 = (String)parseArgument(var25, var14);
      Gson var38 = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap var39 = (PropertyMap)GsonHelper.fromJson(var38, (String)parseArgument(var25, var19), PropertyMap.class);
      PropertyMap var40 = (PropertyMap)GsonHelper.fromJson(var38, (String)parseArgument(var25, var20), PropertyMap.class);
      String var41 = (String)parseArgument(var25, var23);
      File var42 = (File)parseArgument(var25, var4);
      File var43 = var25.has(var5) ? (File)parseArgument(var25, var5) : new File(var42, "assets/");
      File var44 = var25.has(var6) ? (File)parseArgument(var25, var6) : new File(var42, "resourcepacks/");
      String var45 = var25.has(var12) ? (String)var12.value(var25) : Player.createPlayerUUID((String)var11.value(var25)).toString();
      String var46 = var25.has(var21) ? (String)var21.value(var25) : null;
      String var47 = (String)parseArgument(var25, var2);
      Integer var48 = (Integer)parseArgument(var25, var3);
      User var49 = new User((String)var11.value(var25), var45, (String)var13.value(var25), (String)var22.value(var25));
      GameConfig var50 = new GameConfig(new GameConfig.UserData(var49, var39, var40, var28), new DisplayData(var31, var32, var33, var34, var35), new GameConfig.FolderData(var42, var44, var43, var46), new GameConfig.GameData(var36, var37, var41), new GameConfig.ServerData(var47, var48));
      Thread var51 = new Thread("Client Shutdown Thread") {
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
      var51.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      Runtime.getRuntime().addShutdownHook(var51);
      Thread.currentThread().setName("Client thread");
      (new Minecraft(var50)).run();
   }

   private static OptionalInt ofNullable(@Nullable Integer var0) {
      return var0 != null ? OptionalInt.of(var0) : OptionalInt.empty();
   }

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

   private static boolean stringHasValue(String var0) {
      return var0 != null && !var0.isEmpty();
   }

   static {
      System.setProperty("java.awt.headless", "true");
   }
}
