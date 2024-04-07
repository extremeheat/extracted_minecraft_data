package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

public class ChatOptionsScreen extends SimpleOptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.chat.title");

   public ChatOptionsScreen(Screen var1, Options var2) {
      super(
         var1,
         var2,
         TITLE,
         new OptionInstance[]{
            var2.chatVisibility(),
            var2.chatColors(),
            var2.chatLinks(),
            var2.chatLinksPrompt(),
            var2.chatOpacity(),
            var2.textBackgroundOpacity(),
            var2.chatScale(),
            var2.chatLineSpacing(),
            var2.chatDelay(),
            var2.chatWidth(),
            var2.chatHeightFocused(),
            var2.chatHeightUnfocused(),
            var2.narrator(),
            var2.autoSuggestions(),
            var2.hideMatchedNames(),
            var2.reducedDebugInfo(),
            var2.onlyShowSecureChat()
         }
      );
   }
}
