package net.minecraft.realms;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.util.concurrent.Callable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraft.world.GameType;

public class Realms {
   public Realms() {
      super();
   }

   public static boolean isTouchScreen() {
      return Minecraft.func_71410_x().field_71474_y.field_85185_A;
   }

   public static Proxy getProxy() {
      return Minecraft.func_71410_x().func_110437_J();
   }

   public static String sessionId() {
      Session var0 = Minecraft.func_71410_x().func_110432_I();
      return var0 == null ? null : var0.func_111286_b();
   }

   public static String userName() {
      Session var0 = Minecraft.func_71410_x().func_110432_I();
      return var0 == null ? null : var0.func_111285_a();
   }

   public static long currentTimeMillis() {
      return Util.func_211177_b();
   }

   public static String getSessionId() {
      return Minecraft.func_71410_x().func_110432_I().func_111286_b();
   }

   public static String getUUID() {
      return Minecraft.func_71410_x().func_110432_I().func_148255_b();
   }

   public static String getName() {
      return Minecraft.func_71410_x().func_110432_I().func_111285_a();
   }

   public static String uuidToName(String var0) {
      return Minecraft.func_71410_x().func_152347_ac().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(var0), (String)null), false).getName();
   }

   public static <V> ListenableFuture<V> execute(Callable<V> var0) {
      return Minecraft.func_71410_x().func_152343_a(var0);
   }

   public static void execute(Runnable var0) {
      Minecraft.func_71410_x().func_152344_a(var0);
   }

   public static void setScreen(RealmsScreen var0) {
      execute(() -> {
         setScreenDirect(var0);
         return null;
      });
   }

   public static void setScreenDirect(RealmsScreen var0) {
      Minecraft.func_71410_x().func_147108_a(var0.getProxy());
   }

   public static String getGameDirectoryPath() {
      return Minecraft.func_71410_x().field_71412_D.getAbsolutePath();
   }

   public static int survivalId() {
      return GameType.SURVIVAL.func_77148_a();
   }

   public static int creativeId() {
      return GameType.CREATIVE.func_77148_a();
   }

   public static int adventureId() {
      return GameType.ADVENTURE.func_77148_a();
   }

   public static int spectatorId() {
      return GameType.SPECTATOR.func_77148_a();
   }

   public static void setConnectedToRealms(boolean var0) {
      Minecraft.func_71410_x().func_181537_a(var0);
   }

   public static ListenableFuture downloadResourcePack(String var0, String var1) {
      return Minecraft.func_71410_x().func_195541_I().func_195744_a(var0, var1);
   }

   public static void clearResourcePack() {
      Minecraft.func_71410_x().func_195541_I().func_195749_c();
   }

   public static boolean getRealmsNotificationsEnabled() {
      return Minecraft.func_71410_x().field_71474_y.func_74308_b(GameSettings.Options.REALMS_NOTIFICATIONS);
   }

   public static boolean inTitleScreen() {
      return Minecraft.func_71410_x().field_71462_r != null && Minecraft.func_71410_x().field_71462_r instanceof GuiMainMenu;
   }

   public static void deletePlayerTag(File var0) {
      if (var0.exists()) {
         try {
            NBTTagCompound var1 = CompressedStreamTools.func_74796_a(new FileInputStream(var0));
            NBTTagCompound var2 = var1.func_74775_l("Data");
            var2.func_82580_o("Player");
            CompressedStreamTools.func_74799_a(var1, new FileOutputStream(var0));
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }
}
