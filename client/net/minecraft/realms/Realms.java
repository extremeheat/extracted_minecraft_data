package net.minecraft.realms;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.Session;
import net.minecraft.world.WorldSettings;

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
      return Minecraft.func_71386_F();
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

   public static void setScreen(RealmsScreen var0) {
      Minecraft.func_71410_x().func_147108_a(var0.getProxy());
   }

   public static String getGameDirectoryPath() {
      return Minecraft.func_71410_x().field_71412_D.getAbsolutePath();
   }

   public static int survivalId() {
      return WorldSettings.GameType.SURVIVAL.func_77148_a();
   }

   public static int creativeId() {
      return WorldSettings.GameType.CREATIVE.func_77148_a();
   }

   public static int adventureId() {
      return WorldSettings.GameType.ADVENTURE.func_77148_a();
   }

   public static int spectatorId() {
      return WorldSettings.GameType.SPECTATOR.func_77148_a();
   }

   public static void setConnectedToRealms(boolean var0) {
      Minecraft.func_71410_x().func_181537_a(var0);
   }

   public static ListenableFuture<Object> downloadResourcePack(String var0, String var1) {
      ListenableFuture var2 = Minecraft.func_71410_x().func_110438_M().func_180601_a(var0, var1);
      return var2;
   }

   public static void clearResourcePack() {
      Minecraft.func_71410_x().func_110438_M().func_148529_f();
   }

   public static boolean getRealmsNotificationsEnabled() {
      return Minecraft.func_71410_x().field_71474_y.func_74308_b(GameSettings.Options.REALMS_NOTIFICATIONS);
   }

   public static boolean inTitleScreen() {
      return Minecraft.func_71410_x().field_71462_r != null && Minecraft.func_71410_x().field_71462_r instanceof GuiMainMenu;
   }
}
