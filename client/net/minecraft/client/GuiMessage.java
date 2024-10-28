package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;

public record GuiMessage(int addedTime, Component content, @Nullable MessageSignature signature, @Nullable GuiMessageTag tag) {
   public GuiMessage(int addedTime, Component content, @Nullable MessageSignature signature, @Nullable GuiMessageTag tag) {
      super();
      this.addedTime = addedTime;
      this.content = content;
      this.signature = signature;
      this.tag = tag;
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
      public Line(int addedTime, FormattedCharSequence content, @Nullable GuiMessageTag tag, boolean endOfEntry) {
         super();
         this.addedTime = addedTime;
         this.content = content;
         this.tag = tag;
         this.endOfEntry = endOfEntry;
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
