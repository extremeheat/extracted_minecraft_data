package net.minecraft.client.gui.screens.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;

public class ChatLogSegmenter<T extends LoggedChatMessage> {
   private final Function<ChatLog.Entry<T>, MessageType> typeFunction;
   private final List<ChatLog.Entry<T>> messages = new ArrayList();
   @Nullable
   private MessageType segmentType;

   public ChatLogSegmenter(Function<ChatLog.Entry<T>, MessageType> var1) {
      super();
      this.typeFunction = var1;
   }

   public boolean accept(ChatLog.Entry<T> var1) {
      MessageType var2 = (MessageType)this.typeFunction.apply(var1);
      if (this.segmentType != null && var2 != this.segmentType) {
         return false;
      } else {
         this.segmentType = var2;
         this.messages.add(var1);
         return true;
      }
   }

   @Nullable
   public Results<T> build() {
      return !this.messages.isEmpty() && this.segmentType != null ? new Results(this.messages, this.segmentType) : null;
   }

   public static enum MessageType {
      REPORTABLE,
      CONTEXT;

      private MessageType() {
      }

      public boolean foldable() {
         return this == CONTEXT;
      }

      // $FF: synthetic method
      private static MessageType[] $values() {
         return new MessageType[]{REPORTABLE, CONTEXT};
      }
   }

   public static record Results<T extends LoggedChatMessage>(List<ChatLog.Entry<T>> a, MessageType b) {
      private final List<ChatLog.Entry<T>> messages;
      private final MessageType type;

      public Results(List<ChatLog.Entry<T>> var1, MessageType var2) {
         super();
         this.messages = var1;
         this.type = var2;
      }

      public List<ChatLog.Entry<T>> messages() {
         return this.messages;
      }

      public MessageType type() {
         return this.type;
      }
   }
}
