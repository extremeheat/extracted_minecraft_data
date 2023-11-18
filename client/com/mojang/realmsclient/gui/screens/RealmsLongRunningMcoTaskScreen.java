package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RepeatedNarrator;
import org.slf4j.Logger;

public class RealmsLongRunningMcoTaskScreen extends RealmsScreen implements ErrorCallback {
   private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Screen lastScreen;
   private volatile Component title = CommonComponents.EMPTY;
   @Nullable
   private volatile Component errorMessage;
   private volatile boolean aborted;
   private int animTicks;
   private final LongRunningTask task;
   private final int buttonLength = 212;
   private Button cancelOrBackButton;
   public static final String[] SYMBOLS = new String[]{
      "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583",
      "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584",
      "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585",
      "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586",
      "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587",
      "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588",
      "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587",
      "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586",
      "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585",
      "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584",
      "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583",
      "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _",
      "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _",
      "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _",
      "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _",
      "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _",
      "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _",
      "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _",
      "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _",
      "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _"
   };

   public RealmsLongRunningMcoTaskScreen(Screen var1, LongRunningTask var2) {
      super(GameNarrator.NO_TITLE);
      this.lastScreen = var1;
      this.task = var2;
      var2.setScreen(this);
      Thread var3 = new Thread(var2, "Realms-long-running-task");
      var3.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
      var3.start();
   }

   @Override
   public void tick() {
      super.tick();
      REPEATED_NARRATOR.narrate(this.minecraft.getNarrator(), this.title);
      ++this.animTicks;
      this.task.tick();
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.cancelOrBackButtonClicked();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public void init() {
      this.task.init();
      this.cancelOrBackButton = this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, var1 -> this.cancelOrBackButtonClicked()).bounds(this.width / 2 - 106, row(12), 212, 20).build()
      );
   }

   private void cancelOrBackButtonClicked() {
      this.aborted = true;
      this.task.abortTask();
      this.minecraft.setScreen(this.lastScreen);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      var1.drawCenteredString(this.font, this.title, this.width / 2, row(3), 16777215);
      Component var5 = this.errorMessage;
      if (var5 == null) {
         var1.drawCenteredString(this.font, SYMBOLS[this.animTicks % SYMBOLS.length], this.width / 2, row(8), 8421504);
      } else {
         var1.drawCenteredString(this.font, var5, this.width / 2, row(8), 16711680);
      }

      super.render(var1, var2, var3, var4);
   }

   @Override
   public void error(Component var1) {
      this.errorMessage = var1;
      this.minecraft.getNarrator().sayNow(var1);
      this.minecraft
         .execute(
            () -> {
               this.removeWidget(this.cancelOrBackButton);
               this.cancelOrBackButton = this.addRenderableWidget(
                  Button.builder(CommonComponents.GUI_BACK, var1x -> this.cancelOrBackButtonClicked())
                     .bounds(this.width / 2 - 106, this.height / 4 + 120 + 12, 200, 20)
                     .build()
               );
            }
         );
   }

   public void setTitle(Component var1) {
      this.title = var1;
   }

   public boolean aborted() {
      return this.aborted;
   }
}
