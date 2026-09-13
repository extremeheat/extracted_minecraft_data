package net.minecraft.client.main;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import java.io.File;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
   private static final Type field_152370_a = new Main$1();

   public Main() {
      super();
   }

   public static void main(String[] var0) {
      System.setProperty("java.net.preferIPv4Stack", "true");
      OptionParser var1 = new OptionParser();
      var1.allowsUnrecognizedOptions();
      var1.accepts("demo");
      var1.accepts("fullscreen");
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
      ArgumentAcceptingOptionSpec var17 = var1.accepts("userProperties").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var18 = var1.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var19 = var1.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      NonOptionArgumentSpec var20 = var1.nonOptions();
      OptionSet var21 = var1.parse(var0);
      List var22 = var21.valuesOf(var20);
      String var23 = (String)var21.valueOf(var7);
      Proxy var24 = Proxy.NO_PROXY;
      if (var23 != null) {
         try {
            var24 = new Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress(var23, var21.valueOf(var8)));
         } catch (Exception var41) {
         }
      }

      String var25 = (String)var21.valueOf(var9);
      String var26 = (String)var21.valueOf(var10);
      if (!var24.equals(Proxy.NO_PROXY) && func_110121_a(var25) && func_110121_a(var26)) {
         Authenticator.setDefault(new Main$2(var25, var26));
      }

      int var27 = var21.valueOf(var15);
      int var28 = var21.valueOf(var16);
      boolean var29 = var21.has("fullscreen");
      boolean var30 = var21.has("demo");
      String var31 = (String)var21.valueOf(var14);
      HashMultimap var32 = HashMultimap.create();

      for(Entry var34 : ((Map)new Gson().fromJson((String)var21.valueOf(var17), field_152370_a)).entrySet()) {
         var32.putAll(var34.getKey(), (Iterable)var34.getValue());
      }

      File var42 = (File)var21.valueOf(var4);
      File var43 = var21.has(var5) ? (File)var21.valueOf(var5) : new File(var42, "assets/");
      File var35 = var21.has(var6) ? (File)var21.valueOf(var6) : new File(var42, "resourcepacks/");
      String var36 = var21.has(var12) ? (String)var12.value(var21) : (String)var11.value(var21);
      String var37 = var21.has(var18) ? (String)var18.value(var21) : null;
      Session var38 = new Session((String)var11.value(var21), var36, (String)var13.value(var21), (String)var19.value(var21));
      Minecraft var39 = new Minecraft(var38, var27, var28, var29, var30, var42, var43, var35, var24, var31, var32, var37);
      String var40 = (String)var21.valueOf(var2);
      if (var40 != null) {
         var39.func_71367_a(var40, var21.valueOf(var3));
      }

      Runtime.getRuntime().addShutdownHook(new Main$3("Client Shutdown Thread"));
      if (!var22.isEmpty()) {
         System.out.println("Completely ignored arguments: " + var22);
      }

      Thread.currentThread().setName("Client thread");
      var39.func_99999_d();
   }

   private static boolean func_110121_a(String var0) {
      return var0 != null && !var0.isEmpty();
   }
}
