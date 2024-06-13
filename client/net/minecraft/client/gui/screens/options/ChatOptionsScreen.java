package net.minecraft.client.gui.screens.options;

import javax.annotation.Nullable;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ChatOptionsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.chat.title");
   @Nullable
   private AbstractWidget narratorButton;

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{
         var0.chatVisibility(),
         var0.chatColors(),
         var0.chatLinks(),
         var0.chatLinksPrompt(),
         var0.chatOpacity(),
         var0.textBackgroundOpacity(),
         var0.chatScale(),
         var0.chatLineSpacing(),
         var0.chatDelay(),
         var0.chatWidth(),
         var0.chatHeightFocused(),
         var0.chatHeightUnfocused(),
         var0.narrator(),
         var0.autoSuggestions(),
         var0.hideMatchedNames(),
         var0.reducedDebugInfo(),
         var0.onlyShowSecureChat()
      };
   }

   public ChatOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
   }

   @Override
   public void init() {
      super.init();
      this.narratorButton = this.list.findOption(this.options.narrator());
      if (this.narratorButton != null) {
         this.narratorButton.active = this.minecraft.getNarrator().isActive();
      }
   }

   @Override
   protected void addOptions() {
      this.list.addSmall(options(this.options));
   }

   public void updateNarratorButton() {
      if (this.narratorButton instanceof CycleButton) {
         ((CycleButton)this.narratorButton).setValue(this.options.narrator().get());
      }
   }
}
