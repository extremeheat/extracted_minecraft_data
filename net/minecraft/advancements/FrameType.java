package net.minecraft.advancements;

import net.minecraft.ChatFormatting;

public enum FrameType {
   TASK("task", 0, ChatFormatting.GREEN),
   CHALLENGE("challenge", 26, ChatFormatting.DARK_PURPLE),
   GOAL("goal", 52, ChatFormatting.GREEN);

   private final String name;
   private final int texture;
   private final ChatFormatting chatColor;

   private FrameType(String var3, int var4, ChatFormatting var5) {
      this.name = var3;
      this.texture = var4;
      this.chatColor = var5;
   }

   public String getName() {
      return this.name;
   }

   public int getTexture() {
      return this.texture;
   }

   public static FrameType byName(String var0) {
      FrameType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         FrameType var4 = var1[var3];
         if (var4.name.equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown frame type '" + var0 + "'");
   }

   public ChatFormatting getChatColor() {
      return this.chatColor;
   }
}
