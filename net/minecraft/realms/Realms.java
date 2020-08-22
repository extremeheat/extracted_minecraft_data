package net.minecraft.realms;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

public class Realms {
   private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

   public static boolean isTouchScreen() {
      return Minecraft.getInstance().options.touchscreen;
   }

   public static Proxy getProxy() {
      return Minecraft.getInstance().getProxy();
   }

   public static String sessionId() {
      User var0 = Minecraft.getInstance().getUser();
      return var0 == null ? null : var0.getSessionId();
   }

   public static String userName() {
      User var0 = Minecraft.getInstance().getUser();
      return var0 == null ? null : var0.getName();
   }

   public static long currentTimeMillis() {
      return Util.getMillis();
   }

   public static String getSessionId() {
      return Minecraft.getInstance().getUser().getSessionId();
   }

   public static String getUUID() {
      return Minecraft.getInstance().getUser().getUuid();
   }

   public static String getName() {
      return Minecraft.getInstance().getUser().getName();
   }

   public static String uuidToName(String var0) {
      return Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(var0), (String)null), false).getName();
   }

   public static CompletableFuture execute(Supplier var0) {
      return Minecraft.getInstance().submit(var0);
   }

   public static void execute(Runnable var0) {
      Minecraft.getInstance().execute(var0);
   }

   public static void setScreen(RealmsScreen var0) {
      execute(() -> {
         setScreenDirect(var0);
         return null;
      });
   }

   public static void setScreenDirect(RealmsScreen var0) {
      Minecraft.getInstance().setScreen(var0.getProxy());
   }

   public static String getGameDirectoryPath() {
      return Minecraft.getInstance().gameDirectory.getAbsolutePath();
   }

   public static int survivalId() {
      return GameType.SURVIVAL.getId();
   }

   public static int creativeId() {
      return GameType.CREATIVE.getId();
   }

   public static int adventureId() {
      return GameType.ADVENTURE.getId();
   }

   public static int spectatorId() {
      return GameType.SPECTATOR.getId();
   }

   public static void setConnectedToRealms(boolean var0) {
      Minecraft.getInstance().setConnectedToRealms(var0);
   }

   public static CompletableFuture downloadResourcePack(String var0, String var1) {
      return Minecraft.getInstance().getClientPackSource().downloadAndSelectResourcePack(var0, var1);
   }

   public static void clearResourcePack() {
      Minecraft.getInstance().getClientPackSource().clearServerPack();
   }

   public static boolean getRealmsNotificationsEnabled() {
      return Minecraft.getInstance().options.realmsNotifications;
   }

   public static boolean inTitleScreen() {
      return Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof TitleScreen;
   }

   public static void deletePlayerTag(File var0) {
      if (var0.exists()) {
         try {
            CompoundTag var1 = NbtIo.readCompressed(new FileInputStream(var0));
            CompoundTag var2 = var1.getCompound("Data");
            var2.remove("Player");
            NbtIo.writeCompressed(var1, new FileOutputStream(var0));
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }

   public static void openUri(String var0) {
      Util.getPlatform().openUri(var0);
   }

   public static void setClipboard(String var0) {
      Minecraft.getInstance().keyboardHandler.setClipboard(var0);
   }

   public static String getMinecraftVersionString() {
      return SharedConstants.getCurrentVersion().getName();
   }

   public static ResourceLocation resourceLocation(String var0) {
      return new ResourceLocation(var0);
   }

   public static String getLocalizedString(String var0, Object... var1) {
      return I18n.get(var0, var1);
   }

   public static void bind(String var0) {
      ResourceLocation var1 = new ResourceLocation(var0);
      Minecraft.getInstance().getTextureManager().bind(var1);
   }

   public static void narrateNow(String var0) {
      NarratorChatListener var1 = NarratorChatListener.INSTANCE;
      var1.clear();
      var1.handle(ChatType.SYSTEM, new TextComponent(fixNarrationNewlines(var0)));
   }

   private static String fixNarrationNewlines(String var0) {
      return var0.replace("\\n", System.lineSeparator());
   }

   public static void narrateNow(String... var0) {
      narrateNow((Iterable)Arrays.asList(var0));
   }

   public static void narrateNow(Iterable var0) {
      narrateNow(joinNarrations(var0));
   }

   public static String joinNarrations(Iterable var0) {
      return String.join(System.lineSeparator(), var0);
   }

   public static void narrateRepeatedly(String var0) {
      REPEATED_NARRATOR.narrate(fixNarrationNewlines(var0));
   }
}
