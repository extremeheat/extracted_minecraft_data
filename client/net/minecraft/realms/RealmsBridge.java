package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class RealmsBridge extends RealmsScreen {
   private Screen previousScreen;

   public RealmsBridge() {
      super();
   }

   public void switchToRealms(Screen var1) {
      this.previousScreen = var1;
      Realms.setScreen(new RealmsMainScreen(this));
   }

   @Nullable
   public RealmsScreenProxy getNotificationScreen(Screen var1) {
      this.previousScreen = var1;
      return (new RealmsNotificationsScreen(this)).getProxy();
   }

   public void init() {
      Minecraft.getInstance().setScreen(this.previousScreen);
   }
}
