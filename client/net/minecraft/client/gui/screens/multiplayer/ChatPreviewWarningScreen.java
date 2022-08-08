package net.minecraft.client.gui.screens.multiplayer;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.chat.ChatPreviewStatus;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ChatPreviewWarningScreen extends WarningScreen {
   private static final Component TITLE;
   private static final Component CHECK;
   private final ServerData serverData;
   @Nullable
   private final Screen lastScreen;

   private static Component content() {
      ChatPreviewStatus var0 = (ChatPreviewStatus)Minecraft.getInstance().options.chatPreview().get();
      return Component.translatable("chatPreview.warning.content", var0.getCaption());
   }

   public ChatPreviewWarningScreen(@Nullable Screen var1, ServerData var2) {
      super(TITLE, content(), CHECK, CommonComponents.joinForNarration(TITLE, content()));
      this.serverData = var2;
      this.lastScreen = var1;
   }

   protected void initButtons(int var1) {
      this.addRenderableWidget(new Button(this.width / 2 - 155, 100 + var1, 150, 20, Component.translatable("menu.disconnect"), (var1x) -> {
         this.minecraft.level.disconnect();
         this.minecraft.clearLevel();
         this.minecraft.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, 100 + var1, 150, 20, CommonComponents.GUI_PROCEED, (var1x) -> {
         this.updateOptions();
         this.onClose();
      }));
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void updateOptions() {
      if (this.stopShowing != null && this.stopShowing.selected()) {
         ServerData.ChatPreview var1 = this.serverData.getChatPreview();
         if (var1 != null) {
            var1.acknowledge();
            ServerList.saveSingleServer(this.serverData);
         }
      }

   }

   protected int getLineHeight() {
      Objects.requireNonNull(this.font);
      return 9 * 3 / 2;
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   static {
      TITLE = Component.translatable("chatPreview.warning.title").withStyle(ChatFormatting.BOLD);
      CHECK = Component.translatable("chatPreview.warning.check");
   }
}
