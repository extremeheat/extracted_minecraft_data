package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
   public Main() {
      super();
   }

   public static void main(String[] var0) {
      System.setProperty("java.net.preferIPv4Stack", "true");
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
      ArgumentAcceptingOptionSpec var11 = var1.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.func_71386_F() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec var12 = var1.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var13 = var1.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var14 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var15 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var16 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec var17 = var1.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var18 = var1.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec var19 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var20 = var1.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      NonOptionArgumentSpec var21 = var1.nonOptions();
      OptionSet var22 = var1.parse(var0);
      List var23 = var22.valuesOf(var21);
      if (!var23.isEmpty()) {
         System.out.println("Completely ignored arguments: " + var23);
      }

      String var24 = (String)var22.valueOf(var7);
      Proxy var25 = Proxy.NO_PROXY;
      if (var24 != null) {
         try {
            var25 = new Proxy(Type.SOCKS, new InetSocketAddress(var24, (Integer)var22.valueOf(var8)));
         } catch (Exception var46) {
         }
      }

      final String var26 = (String)var22.valueOf(var9);
      final String var27 = (String)var22.valueOf(var10);
      if (!var25.equals(Proxy.NO_PROXY) && func_110121_a(var26) && func_110121_a(var27)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(var26, var27.toCharArray());
            }
         });
      }

      int var28 = (Integer)var22.valueOf(var15);
      int var29 = (Integer)var22.valueOf(var16);
      boolean var30 = var22.has("fullscreen");
      boolean var31 = var22.has("checkGlErrors");
      boolean var32 = var22.has("demo");
      String var33 = (String)var22.valueOf(var14);
      Gson var34 = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap var35 = (PropertyMap)var34.fromJson((String)var22.valueOf(var17), PropertyMap.class);
      PropertyMap var36 = (PropertyMap)var34.fromJson((String)var22.valueOf(var18), PropertyMap.class);
      File var37 = (File)var22.valueOf(var4);
      File var38 = var22.has(var5) ? (File)var22.valueOf(var5) : new File(var37, "assets/");
      File var39 = var22.has(var6) ? (File)var22.valueOf(var6) : new File(var37, "resourcepacks/");
      String var40 = var22.has(var12) ? (String)var12.value(var22) : (String)var11.value(var22);
      String var41 = var22.has(var19) ? (String)var19.value(var22) : null;
      String var42 = (String)var22.valueOf(var2);
      Integer var43 = (Integer)var22.valueOf(var3);
      Session var44 = new Session((String)var11.value(var22), var40, (String)var13.value(var22), (String)var20.value(var22));
      GameConfiguration var45 = new GameConfiguration(new GameConfiguration.UserInformation(var44, var35, var36, var25), new GameConfiguration.DisplayInformation(var28, var29, var30, var31), new GameConfiguration.FolderInformation(var37, var39, var38, var41), new GameConfiguration.GameInformation(var32, var33), new GameConfiguration.ServerInformation(var42, var43));
      Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
         public void run() {
            Minecraft.func_71363_D();
         }
      });
      Thread.currentThread().setName("Client thread");
      (new Minecraft(var45)).func_99999_d();
   }

   private static boolean func_110121_a(String var0) {
      return var0 != null && !var0.isEmpty();
   }
}
