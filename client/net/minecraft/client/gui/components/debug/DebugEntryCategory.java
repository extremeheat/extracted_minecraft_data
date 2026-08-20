package net.minecraft.client.gui.components.debug;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public record DebugEntryCategory(Component label, float sortKey) {
   public static final DebugEntryCategory SCREEN_TEXT;
   public static final DebugEntryCategory RENDERER;

   public DebugEntryCategory {
      super();
   }

   static {
      SCREEN_TEXT = new DebugEntryCategory(Component.translatable("debug.options.category.text").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 1.0F);
      RENDERER = new DebugEntryCategory(Component.translatable("debug.options.category.renderer").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 2.0F);
   }
}
