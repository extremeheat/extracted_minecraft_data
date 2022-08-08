package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;

public record GuiMessage(int a, Component b, @Nullable MessageSignature c, @Nullable GuiMessageTag d) {
   private final int addedTime;
   private final Component content;
   @Nullable
   private final MessageSignature headerSignature;
   @Nullable
   private final GuiMessageTag tag;

   public GuiMessage(int var1, Component var2, @Nullable MessageSignature var3, @Nullable GuiMessageTag var4) {
      super();
      this.addedTime = var1;
      this.content = var2;
      this.headerSignature = var3;
      this.tag = var4;
   }

   public int addedTime() {
      return this.addedTime;
   }

   public Component content() {
      return this.content;
   }

   @Nullable
   public MessageSignature headerSignature() {
      return this.headerSignature;
   }

   @Nullable
   public GuiMessageTag tag() {
      return this.tag;
   }

   public static record Line(int a, FormattedCharSequence b, @Nullable GuiMessageTag c, boolean d) {
      private final int addedTime;
      private final FormattedCharSequence content;
      @Nullable
      private final GuiMessageTag tag;
      private final boolean endOfEntry;

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
