package net.minecraft.client;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

public record GuiMessage(int addedTime, Component content, @Nullable MessageSignature signature, @Nullable GuiMessageTag tag) {
   private static final int MESSAGE_TAG_MARGIN_LEFT = 4;

   public GuiMessage(int var1, Component var2, @Nullable MessageSignature var3, @Nullable GuiMessageTag var4) {
      super();
      this.addedTime = var1;
      this.content = var2;
      this.signature = var3;
      this.tag = var4;
   }

   public List<FormattedCharSequence> splitLines(Font var1, int var2) {
      if (this.tag != null && this.tag.icon() != null) {
         var2 -= this.tag.icon().width + 4 + 2;
      }

      return ComponentRenderUtils.wrapComponents(this.content, var2, var1);
   }

   public static record Line(int addedTime, FormattedCharSequence content, @Nullable GuiMessageTag tag, boolean endOfEntry) {
      public Line(int var1, FormattedCharSequence var2, @Nullable GuiMessageTag var3, boolean var4) {
         super();
         this.addedTime = var1;
         this.content = var2;
         this.tag = var3;
         this.endOfEntry = var4;
      }

      public int getTagIconLeft(Font var1) {
         return var1.width(this.content) + 4;
      }
   }
}
