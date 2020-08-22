package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsLongRunningMcoTaskScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final int BUTTON_CANCEL_ID = 666;
   private final int BUTTON_BACK_ID = 667;
   private final RealmsScreen lastScreen;
   private final LongRunningTask taskThread;
   private volatile String title = "";
   private volatile boolean error;
   private volatile String errorMessage;
   private volatile boolean aborted;
   private int animTicks;
   private final LongRunningTask task;
   private final int buttonLength = 212;
   public static final String[] symbols = new String[]{"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄", "▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"};

   public RealmsLongRunningMcoTaskScreen(RealmsScreen var1, LongRunningTask var2) {
      this.lastScreen = var1;
      this.task = var2;
      var2.setScreen(this);
      this.taskThread = var2;
   }

   public void start() {
      Thread var1 = new Thread(this.taskThread, "Realms-long-running-task");
      var1.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
      var1.start();
   }

   public void tick() {
      super.tick();
      Realms.narrateRepeatedly(this.title);
      ++this.animTicks;
      this.task.tick();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.cancelOrBackButtonClicked();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void init() {
      this.task.init();
      this.buttonsAdd(new RealmsButton(666, this.width() / 2 - 106, RealmsConstants.row(12), 212, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsLongRunningMcoTaskScreen.this.cancelOrBackButtonClicked();
         }
      });
   }

   private void cancelOrBackButtonClicked() {
      this.aborted = true;
      this.task.abortTask();
      Realms.setScreen(this.lastScreen);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, RealmsConstants.row(3), 16777215);
      if (!this.error) {
         this.drawCenteredString(symbols[this.animTicks % symbols.length], this.width() / 2, RealmsConstants.row(8), 8421504);
      }

      if (this.error) {
         this.drawCenteredString(this.errorMessage, this.width() / 2, RealmsConstants.row(8), 16711680);
      }

      super.render(var1, var2, var3);
   }

   public void error(String var1) {
      this.error = true;
      this.errorMessage = var1;
      Realms.narrateNow(var1);
      this.buttonsClear();
      this.buttonsAdd(new RealmsButton(667, this.width() / 2 - 106, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsLongRunningMcoTaskScreen.this.cancelOrBackButtonClicked();
         }
      });
   }

   public void setTitle(String var1) {
      this.title = var1;
   }

   public boolean aborted() {
      return this.aborted;
   }
}
