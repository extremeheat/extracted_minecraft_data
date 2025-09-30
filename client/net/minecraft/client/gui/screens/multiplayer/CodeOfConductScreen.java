package net.minecraft.client.gui.screens.multiplayer;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class CodeOfConductScreen extends WarningScreen {
   private static final Component TITLE;
   private static final Component CHECK;
   @Nullable
   private final ServerData serverData;
   private final String codeOfConductText;
   private final BooleanConsumer resultConsumer;
   private final Screen parent;

   private CodeOfConductScreen(@Nullable ServerData var1, Screen var2, Component var3, String var4, BooleanConsumer var5) {
      super(TITLE, var3, CHECK, TITLE.copy().append("\n").append(var3));
      this.serverData = var1;
      this.parent = var2;
      this.codeOfConductText = var4;
      this.resultConsumer = var5;
   }

   public CodeOfConductScreen(@Nullable ServerData var1, Screen var2, String var3, BooleanConsumer var4) {
      this(var1, var2, Component.literal(var3), var3, var4);
   }

   protected Layout addFooterButtons() {
      LinearLayout var1 = LinearLayout.horizontal().spacing(8);
      var1.addChild(Button.builder(CommonComponents.GUI_ACKNOWLEDGE, (var1x) -> this.onResult(true)).build());
      var1.addChild(Button.builder(CommonComponents.GUI_DISCONNECT, (var1x) -> this.onResult(false)).build());
      return var1;
   }

   private void onResult(boolean var1) {
      this.resultConsumer.accept(var1);
      if (this.serverData != null) {
         if (var1 && this.stopShowing.selected()) {
            this.serverData.acceptCodeOfConduct(this.codeOfConductText);
         } else {
            this.serverData.clearCodeOfConduct();
         }

         ServerList.saveSingleServer(this.serverData);
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void tick() {
      super.tick();
      if (this.parent instanceof ConnectScreen || this.parent instanceof ServerReconfigScreen) {
         this.parent.tick();
      }

   }

   static {
      TITLE = Component.translatable("multiplayer.codeOfConduct.title").withStyle(ChatFormatting.BOLD);
      CHECK = Component.translatable("multiplayer.codeOfConduct.check");
   }
}
