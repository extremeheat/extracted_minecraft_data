package net.minecraft.advancements;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum FrameType {
   TASK("task", ChatFormatting.GREEN),
   CHALLENGE("challenge", ChatFormatting.DARK_PURPLE),
   GOAL("goal", ChatFormatting.GREEN);

   private final String name;
   private final ChatFormatting chatColor;
   private final Component displayName;

   private FrameType(String var3, ChatFormatting var4) {
      this.name = var3;
      this.chatColor = var4;
      this.displayName = Component.translatable("advancements.toast." + var3);
   }

   public String getName() {
      return this.name;
   }

   public static FrameType byName(String var0) {
      for(FrameType var4 : values()) {
         if (var4.name.equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown frame type '" + var0 + "'");
   }

   public ChatFormatting getChatColor() {
      return this.chatColor;
   }

   public Component getDisplayName() {
      return this.displayName;
   }
}
