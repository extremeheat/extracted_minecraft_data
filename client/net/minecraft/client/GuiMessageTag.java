package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record GuiMessageTag(int indicatorColor, @Nullable Icon icon, @Nullable Component text, @Nullable String logTag) {
   private static final Component SYSTEM_TEXT = Component.translatable("chat.tag.system");
   private static final Component SYSTEM_TEXT_SINGLE_PLAYER = Component.translatable("chat.tag.system_single_player");
   private static final Component CHAT_NOT_SECURE_TEXT = Component.translatable("chat.tag.not_secure");
   private static final Component CHAT_MODIFIED_TEXT = Component.translatable("chat.tag.modified");
   private static final Component CHAT_ERROR_TEXT = Component.translatable("chat.tag.error");
   private static final int CHAT_NOT_SECURE_INDICATOR_COLOR = 13684944;
   private static final int CHAT_MODIFIED_INDICATOR_COLOR = 6316128;
   private static final GuiMessageTag SYSTEM;
   private static final GuiMessageTag SYSTEM_SINGLE_PLAYER;
   private static final GuiMessageTag CHAT_NOT_SECURE;
   private static final GuiMessageTag CHAT_ERROR;

   public GuiMessageTag(int var1, @Nullable Icon var2, @Nullable Component var3, @Nullable String var4) {
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
      MutableComponent var2 = Component.empty().append(CHAT_MODIFIED_TEXT).append(CommonComponents.NEW_LINE).append((Component)var1);
      return new GuiMessageTag(6316128, GuiMessageTag.Icon.CHAT_MODIFIED, var2, "Modified");
   }

   public static GuiMessageTag chatError() {
      return CHAT_ERROR;
   }

   static {
      SYSTEM = new GuiMessageTag(13684944, (Icon)null, SYSTEM_TEXT, "System");
      SYSTEM_SINGLE_PLAYER = new GuiMessageTag(13684944, (Icon)null, SYSTEM_TEXT_SINGLE_PLAYER, "System");
      CHAT_NOT_SECURE = new GuiMessageTag(13684944, (Icon)null, CHAT_NOT_SECURE_TEXT, "Not Secure");
      CHAT_ERROR = new GuiMessageTag(16733525, (Icon)null, CHAT_ERROR_TEXT, "Chat Error");
   }

   public static enum Icon {
      CHAT_MODIFIED(ResourceLocation.withDefaultNamespace("icon/chat_modified"), 9, 9);

      public final ResourceLocation sprite;
      public final int width;
      public final int height;

      private Icon(final ResourceLocation var3, final int var4, final int var5) {
         this.sprite = var3;
         this.width = var4;
         this.height = var5;
      }

      public void draw(GuiGraphics var1, int var2, int var3) {
         var1.blitSprite(RenderType::guiTextured, this.sprite, var2, var3, this.width, this.height);
      }

      // $FF: synthetic method
      private static Icon[] $values() {
         return new Icon[]{CHAT_MODIFIED};
      }
   }
}
