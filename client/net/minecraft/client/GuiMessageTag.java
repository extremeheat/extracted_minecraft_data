package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record GuiMessageTag(int a, @Nullable GuiMessageTag.Icon b, @Nullable Component c, @Nullable String d) {
   private final int indicatorColor;
   @Nullable
   private final GuiMessageTag.Icon icon;
   @Nullable
   private final Component text;
   @Nullable
   private final String logTag;
   private static final Component SYSTEM_TEXT = Component.translatable("chat.tag.system");
   private static final Component SYSTEM_TEXT_SINGLE_PLAYER = Component.translatable("chat.tag.system_single_player");
   private static final Component CHAT_NOT_SECURE_TEXT = Component.translatable("chat.tag.not_secure");
   private static final Component CHAT_MODIFIED_TEXT = Component.translatable("chat.tag.modified");
   private static final int CHAT_NOT_SECURE_INDICATOR_COLOR = 13684944;
   private static final int CHAT_MODIFIED_INDICATOR_COLOR = 6316128;
   private static final GuiMessageTag SYSTEM = new GuiMessageTag(13684944, null, SYSTEM_TEXT, "System");
   private static final GuiMessageTag SYSTEM_SINGLE_PLAYER = new GuiMessageTag(13684944, null, SYSTEM_TEXT_SINGLE_PLAYER, "System");
   private static final GuiMessageTag CHAT_NOT_SECURE = new GuiMessageTag(13684944, null, CHAT_NOT_SECURE_TEXT, "Not Secure");
   static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/chat_tags.png");

   public GuiMessageTag(int var1, @Nullable GuiMessageTag.Icon var2, @Nullable Component var3, @Nullable String var4) {
      super();
      this.indicatorColor = var1;
      this.icon = var2;
      this.text = var3;
      this.logTag = var4;
   }

   public static GuiMessageTag system() {
      return SYSTEM;
   }

   public static GuiMessageTag systemSinglePlayer() {
      return SYSTEM_SINGLE_PLAYER;
   }

   public static GuiMessageTag chatNotSecure() {
      return CHAT_NOT_SECURE;
   }

   public static GuiMessageTag chatModified(String var0) {
      MutableComponent var1 = Component.literal(var0).withStyle(ChatFormatting.GRAY);
      MutableComponent var2 = Component.empty().append(CHAT_MODIFIED_TEXT).append(CommonComponents.NEW_LINE).append(var1);
      return new GuiMessageTag(6316128, GuiMessageTag.Icon.CHAT_MODIFIED, var2, "Modified");
   }

   public static enum Icon {
      CHAT_MODIFIED(0, 0, 9, 9);

      public final int u;
      public final int v;
      public final int width;
      public final int height;

      private Icon(int var3, int var4, int var5, int var6) {
         this.u = var3;
         this.v = var4;
         this.width = var5;
         this.height = var6;
      }

      public void draw(GuiGraphics var1, int var2, int var3) {
         var1.blit(GuiMessageTag.TEXTURE_LOCATION, var2, var3, (float)this.u, (float)this.v, this.width, this.height, 32, 32);
      }
   }
}
