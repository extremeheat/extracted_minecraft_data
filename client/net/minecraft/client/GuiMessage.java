package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;

public record GuiMessage(int addedTime, Component content, @Nullable MessageSignature signature, @Nullable GuiMessageTag tag) {
   public GuiMessage(int var1, Component var2, @Nullable MessageSignature var3, @Nullable GuiMessageTag var4) {
      super();
      this.addedTime = var1;
      this.content = var2;
      this.signature = var3;
      this.tag = var4;
   }

   @Nullable
   public GuiMessageTag.Icon icon() {
      return this.tag != null ? this.tag.icon() : null;
   }

   public int addedTime() {
      return this.addedTime;
   }

   public Component content() {
      return this.content;
   }

   @Nullable
   public MessageSignature signature() {
      return this.signature;
   }

   @Nullable
   public GuiMessageTag tag() {
      return this.tag;
   }

   public static record Line(int addedTime, FormattedCharSequence content, @Nullable GuiMessageTag tag, boolean endOfEntry) {
      public Line(int var1, FormattedCharSequence var2, @Nullable GuiMessageTag var3, boolean var4) {
         super();
         this.addedTime = var1;
         this.content = var2;
         this.tag = var3;
         this.endOfEntry = var4;
      }

      public int addedTime() {
         return this.addedTime;
      }

      public FormattedCharSequence content() {
         return this.content;
      }

      @Nullable
      public GuiMessageTag tag() {
         return this.tag;
      }

      public boolean endOfEntry() {
         return this.endOfEntry;
      }
   }
}
