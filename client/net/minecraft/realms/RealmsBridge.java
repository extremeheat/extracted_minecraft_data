package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenAlert;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBridge extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private GuiScreen previousScreen;

   public RealmsBridge() {
      super();
   }

   public void switchToRealms(GuiScreen var1) {
      this.previousScreen = var1;

      try {
         Class var2 = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
         Constructor var3 = var2.getDeclaredConstructor(RealmsScreen.class);
         var3.setAccessible(true);
         Object var4 = var3.newInstance(this);
         Minecraft.func_71410_x().func_147108_a(((RealmsScreen)var4).getProxy());
      } catch (ClassNotFoundException var5) {
         LOGGER.error("Realms module missing");
         this.showMissingRealmsErrorScreen();
      } catch (Exception var6) {
         LOGGER.error("Failed to load Realms module", var6);
         this.showMissingRealmsErrorScreen();
      }

   }

   public GuiScreenRealmsProxy getNotificationScreen(GuiScreen var1) {
      try {
         this.previousScreen = var1;
         Class var2 = Class.forName("com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen");
         Constructor var3 = var2.getDeclaredConstructor(RealmsScreen.class);
         var3.setAccessible(true);
         Object var4 = var3.newInstance(this);
         return ((RealmsScreen)var4).getProxy();
      } catch (ClassNotFoundException var5) {
         LOGGER.error("Realms module missing");
      } catch (Exception var6) {
         LOGGER.error("Failed to load Realms module", var6);
      }

      return null;
   }

   public void init() {
      Minecraft.func_71410_x().func_147108_a(this.previousScreen);
   }

   public static void openUri(String var0) {
      Util.func_110647_a().func_195640_a(var0);
   }

   public static void setClipboard(String var0) {
      Minecraft.func_71410_x().field_195559_v.func_197960_a(var0);
   }

   private void showMissingRealmsErrorScreen() {
      Minecraft.func_71410_x().func_147108_a(new GuiScreenAlert(() -> {
         Minecraft.func_71410_x().func_147108_a(this.previousScreen);
      }, new TextComponentString(""), new TextComponentTranslation("realms.missing.module.error.text", new Object[0])));
   }
}
