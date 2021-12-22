package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class SafetyScreen extends Screen {
   private final Screen previous;
   private static final Component TITLE;
   private static final Component CONTENT;
   private static final Component CHECK;
   private static final Component NARRATION;
   private Checkbox stopShowing;
   private MultiLineLabel message;

   public SafetyScreen(Screen var1) {
      super(NarratorChatListener.NO_TITLE);
      this.message = MultiLineLabel.EMPTY;
      this.previous = var1;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, CONTENT, this.width - 50);
      int var10000 = this.message.getLineCount() + 1;
      Objects.requireNonNull(this.font);
      int var1 = var10000 * 9 * 2;
      this.addRenderableWidget(new Button(this.width / 2 - 155, 100 + var1, 150, 20, CommonComponents.GUI_PROCEED, (var1x) -> {
         if (this.stopShowing.selected()) {
            this.minecraft.options.skipMultiplayerWarning = true;
            this.minecraft.options.save();
         }

         this.minecraft.setScreen(new JoinMultiplayerScreen(this.previous));
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, 100 + var1, 150, 20, CommonComponents.GUI_BACK, (var1x) -> {
         this.minecraft.setScreen(this.previous);
      }));
      this.stopShowing = new Checkbox(this.width / 2 - 155 + 80, 76 + var1, 150, 20, CHECK, false);
      this.addRenderableWidget(this.stopShowing);
   }

   public Component getNarrationMessage() {
      return NARRATION;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderDirtBackground(0);
      drawString(var1, this.font, TITLE, 25, 30, 16777215);
      MultiLineLabel var10000 = this.message;
      Objects.requireNonNull(this.font);
      var10000.renderLeftAligned(var1, 25, 70, 9 * 2, 16777215);
      super.render(var1, var2, var3, var4);
   }

   static {
      TITLE = (new TranslatableComponent("multiplayerWarning.header")).withStyle(ChatFormatting.BOLD);
      CONTENT = new TranslatableComponent("multiplayerWarning.message");
      CHECK = new TranslatableComponent("multiplayerWarning.check");
      NARRATION = TITLE.copy().append("\n").append(CONTENT);
   }
}
