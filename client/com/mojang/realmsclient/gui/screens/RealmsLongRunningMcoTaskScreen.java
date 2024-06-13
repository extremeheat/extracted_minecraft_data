package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.time.Duration;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RepeatedNarrator;
import org.slf4j.Logger;

public class RealmsLongRunningMcoTaskScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
   private final List<LongRunningTask> queuedTasks;
   private final Screen lastScreen;
   private final LinearLayout layout = LinearLayout.vertical();
   private volatile Component title;
   @Nullable
   private LoadingDotsWidget loadingDotsWidget;

   public RealmsLongRunningMcoTaskScreen(Screen var1, LongRunningTask... var2) {
      super(GameNarrator.NO_TITLE);
      this.lastScreen = var1;
      this.queuedTasks = List.of(var2);
      if (this.queuedTasks.isEmpty()) {
         throw new IllegalArgumentException("No tasks added");
      } else {
         this.title = this.queuedTasks.get(0).getTitle();
         Runnable var3 = () -> {
            for (LongRunningTask var5 : var2) {
               this.setTitle(var5.getTitle());
               if (var5.aborted()) {
                  break;
               }

               var5.run();
               if (var5.aborted()) {
                  return;
               }
            }
         };
         Thread var4 = new Thread(var3, "Realms-long-running-task");
         var4.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
         var4.start();
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.loadingDotsWidget != null) {
         REPEATED_NARRATOR.narrate(this.minecraft.getNarrator(), this.loadingDotsWidget.getMessage());
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.cancel();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.loadingDotsWidget = new LoadingDotsWidget(this.font, this.title);
      this.layout.addChild(this.loadingDotsWidget, var0 -> var0.paddingBottom(30));
      this.layout.addChild(Button.builder(CommonComponents.GUI_CANCEL, var1 -> this.cancel()).build());
      this.layout.visitWidgets(var1 -> {
         AbstractWidget var10000 = this.addRenderableWidget(var1);
      });
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   protected void cancel() {
      for (LongRunningTask var2 : this.queuedTasks) {
         var2.abortTask();
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   public void setTitle(Component var1) {
      if (this.loadingDotsWidget != null) {
         this.loadingDotsWidget.setMessage(var1);
      }

      this.title = var1;
   }
}
