package net.minecraft.client.gui.screens;

import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.network.chat.TranslatableComponent;

public class ChatOptionsScreen extends SimpleOptionsSubScreen {
   private static final Option[] CHAT_OPTIONS;

   public ChatOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, new TranslatableComponent("options.chat.title"), CHAT_OPTIONS);
   }

   static {
      CHAT_OPTIONS = new Option[]{Option.CHAT_VISIBILITY, Option.CHAT_COLOR, Option.CHAT_LINKS, Option.CHAT_LINKS_PROMPT, Option.CHAT_OPACITY, Option.TEXT_BACKGROUND_OPACITY, Option.CHAT_SCALE, Option.CHAT_LINE_SPACING, Option.CHAT_DELAY, Option.CHAT_WIDTH, Option.CHAT_HEIGHT_FOCUSED, Option.CHAT_HEIGHT_UNFOCUSED, Option.NARRATOR, Option.AUTO_SUGGESTIONS, Option.HIDE_MATCHED_NAMES, Option.REDUCED_DEBUG_INFO};
   }
}
