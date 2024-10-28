package com.mojang.realmsclient.gui.screens;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class RealmsPopups {
   private static final int COLOR_INFO = 8226750;
   private static final Component INFO = Component.translatable("mco.info").withColor(8226750);
   private static final Component WARNING = Component.translatable("mco.warning").withColor(-65536);

   public RealmsPopups() {
      super();
   }

   public static PopupScreen infoPopupScreen(Screen var0, Component var1, Consumer<PopupScreen> var2) {
      return (new PopupScreen.Builder(var0, INFO)).setMessage(var1).addButton(CommonComponents.GUI_CONTINUE, var2).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build();
   }

   public static PopupScreen warningPopupScreen(Screen var0, Component var1, Consumer<PopupScreen> var2) {
      return (new PopupScreen.Builder(var0, WARNING)).setMessage(var1).addButton(CommonComponents.GUI_CONTINUE, var2).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build();
   }

   public static PopupScreen warningAcknowledgePopupScreen(Screen var0, Component var1, Consumer<PopupScreen> var2) {
      return (new PopupScreen.Builder(var0, WARNING)).setMessage(var1).addButton(CommonComponents.GUI_OK, var2).build();
   }
}
