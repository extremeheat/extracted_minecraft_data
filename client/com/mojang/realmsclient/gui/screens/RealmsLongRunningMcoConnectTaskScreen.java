package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsJoinInformation;
import com.mojang.realmsclient.dto.ServiceQuality;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class RealmsLongRunningMcoConnectTaskScreen extends RealmsLongRunningMcoTaskScreen {
   private final LongRunningTask task;
   private final RealmsJoinInformation serverAddress;
   private final LinearLayout footer = LinearLayout.vertical();

   public RealmsLongRunningMcoConnectTaskScreen(Screen var1, RealmsJoinInformation var2, LongRunningTask var3) {
      super(var1, var3);
      this.task = var3;
      this.serverAddress = var2;
   }

   public void init() {
      super.init();
      if (this.serverAddress.regionData() != null && this.serverAddress.regionData().region() != null) {
         LinearLayout var1 = LinearLayout.horizontal().spacing(10);
         StringWidget var2 = new StringWidget(Component.translatable("mco.connect.region", Component.translatable(this.serverAddress.regionData().region().translationKey)), this.font);
         var1.addChild(var2);
         Identifier var3 = this.serverAddress.regionData().serviceQuality() != null ? this.serverAddress.regionData().serviceQuality().getIcon() : ServiceQuality.UNKNOWN.getIcon();
         var1.addChild(ImageWidget.sprite(10, 8, var3), (Consumer)(LayoutSettings::alignVerticallyTop));
         this.footer.addChild(var1, (Consumer)((var0) -> var0.paddingTop(40)));
         this.footer.visitWidgets((var1x) -> {
            AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
         });
         this.repositionElements();
      }
   }

   protected void repositionElements() {
      super.repositionElements();
      int var1 = this.layout.getY() + this.layout.getHeight();
      ScreenRectangle var2 = new ScreenRectangle(0, var1, this.width, this.height - var1);
      this.footer.arrangeElements();
      FrameLayout.alignInRectangle(this.footer, var2, 0.5F, 0.0F);
   }

   public void tick() {
      super.tick();
      this.task.tick();
   }

   protected void cancel() {
      this.task.abortTask();
      super.cancel();
   }
}
