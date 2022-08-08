package net.minecraft.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record GuiMessageTag(int a, @Nullable Icon b, @Nullable Component c, @Nullable String d) {
   private final int indicatorColor;
   @Nullable
   private final Icon icon;
   @Nullable
   private final Component text;
   @Nullable
   private final String logTag;
   private static final Component CHAT_NOT_SECURE_TEXT;
   private static final Component CHAT_MODIFIED_TEXT;
   private static final Component CHAT_FILTERED_TEXT;
   private static final int SYSTEM_INDICATOR_COLOR = 10526880;
   private static final int CHAT_NOT_SECURE_INDICATOR_COLOR = 15224664;
   private static final int CHAT_MODIFIED_INDICATOR_COLOR = 15386724;
   private static final GuiMessageTag SYSTEM;
   private static final GuiMessageTag CHAT_NOT_SECURE;
   private static final GuiMessageTag CHAT_FILTERED;
   static final ResourceLocation TEXTURE_LOCATION;

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

   public static GuiMessageTag chatNotSecure() {
      return CHAT_NOT_SECURE;
   }

   public static GuiMessageTag chatModified(String var0) {
      MutableComponent var1 = Component.translatable("chat.tag.modified.original", var0);
      MutableComponent var2 = Component.empty().append(CHAT_MODIFIED_TEXT).append(CommonComponents.NEW_LINE).append((Component)var1);
      return new GuiMessageTag(15386724, GuiMessageTag.Icon.CHAT_MODIFIED, var2, "Modified");
   }

   public static GuiMessageTag chatFiltered() {
      return CHAT_FILTERED;
   }

   public int indicatorColor() {
      return this.indicatorColor;
   }

   @Nullable
   public Icon icon() {
      return this.icon;
   }

   @Nullable
   public Component text() {
      return this.text;
   }

   @Nullable
   public String logTag() {
      return this.logTag;
   }

   static {
      CHAT_NOT_SECURE_TEXT = Component.translatable("chat.tag.not_secure").withStyle(ChatFormatting.UNDERLINE);
      CHAT_MODIFIED_TEXT = Component.translatable("chat.tag.modified").withStyle(ChatFormatting.UNDERLINE);
      CHAT_FILTERED_TEXT = Component.translatable("chat.tag.filtered").withStyle(ChatFormatting.UNDERLINE);
      SYSTEM = new GuiMessageTag(10526880, (Icon)null, (Component)null, "System");
      CHAT_NOT_SECURE = new GuiMessageTag(15224664, GuiMessageTag.Icon.CHAT_NOT_SECURE, CHAT_NOT_SECURE_TEXT, "Not Secure");
      CHAT_FILTERED = new GuiMessageTag(15386724, GuiMessageTag.Icon.CHAT_MODIFIED, CHAT_FILTERED_TEXT, "Filtered");
      TEXTURE_LOCATION = new ResourceLocation("textures/gui/chat_tags.png");
   }

   public static enum Icon {
      CHAT_NOT_SECURE(0, 0, 9, 9),
      CHAT_MODIFIED(9, 0, 9, 9);

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

      public void draw(PoseStack var1, int var2, int var3) {
         RenderSystem.setShaderTexture(0, GuiMessageTag.TEXTURE_LOCATION);
         GuiComponent.blit(var1, var2, var3, (float)this.u, (float)this.v, this.width, this.height, 32, 32);
      }

      // $FF: synthetic method
      private static Icon[] $values() {
         return new Icon[]{CHAT_NOT_SECURE, CHAT_MODIFIED};
      }
   }
}
