package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class BackupConfirmScreen extends Screen {
   private final Screen lastScreen;
   protected final BackupConfirmScreen.Listener listener;
   private final Component description;
   private final boolean promptForCacheErase;
   private final List<String> lines = Lists.newArrayList();
   private final String eraseCacheText;
   private final String backupButton;
   private final String continueButton;
   private final String cancelButton;
   private Checkbox eraseCache;

   public BackupConfirmScreen(Screen var1, BackupConfirmScreen.Listener var2, Component var3, Component var4, boolean var5) {
      super(var3);
      this.lastScreen = var1;
      this.listener = var2;
      this.description = var4;
      this.promptForCacheErase = var5;
      this.eraseCacheText = I18n.get("selectWorld.backupEraseCache");
      this.backupButton = I18n.get("selectWorld.backupJoinConfirmButton");
      this.continueButton = I18n.get("selectWorld.backupJoinSkipButton");
      this.cancelButton = I18n.get("gui.cancel");
   }

   protected void init() {
      super.init();
      this.lines.clear();
      this.lines.addAll(this.font.split(this.description.getColoredString(), this.width - 50));
      int var10000 = this.lines.size() + 1;
      this.font.getClass();
      int var1 = var10000 * 9;
      this.addButton(new Button(this.width / 2 - 155, 100 + var1, 150, 20, this.backupButton, (var1x) -> {
         this.listener.proceed(true, this.eraseCache.selected());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, 100 + var1, 150, 20, this.continueButton, (var1x) -> {
         this.listener.proceed(false, this.eraseCache.selected());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 80, 124 + var1, 150, 20, this.cancelButton, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.eraseCache = new Checkbox(this.width / 2 - 155 + 80, 76 + var1, 150, 20, this.eraseCacheText, false);
      if (this.promptForCacheErase) {
         this.addButton(this.eraseCache);
      }

   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 50, 16777215);
      int var4 = 70;

      for(Iterator var5 = this.lines.iterator(); var5.hasNext(); var4 += 9) {
         String var6 = (String)var5.next();
         this.drawCenteredString(this.font, var6, this.width / 2, var4, 16777215);
         this.font.getClass();
      }

      super.render(var1, var2, var3);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public interface Listener {
      void proceed(boolean var1, boolean var2);
   }
}
