package net.minecraft.client.gui.screens.multiplayer;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
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

   private CodeOfConductScreen(@Nullable ServerData var1, Component var2, String var3, BooleanConsumer var4) {
      super(TITLE, var2, CHECK, TITLE.copy().append("\n").append(var2));
      this.serverData = var1;
      this.codeOfConductText = var3;
      this.resultConsumer = var4;
   }

   public CodeOfConductScreen(@Nullable ServerData var1, String var2, BooleanConsumer var3) {
      this(var1, Component.literal(var2), var2, var3);
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

   static {
      TITLE = Component.translatable("multiplayer.codeOfConduct.title").withStyle(ChatFormatting.BOLD);
      CHECK = Component.translatable("multiplayer.codeOfConduct.check");
   }
}
