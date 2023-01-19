package net.minecraft.client.gui.screens.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;

public class ChatLogSegmenter<T extends LoggedChatMessage> {
   private final Function<ChatLog.Entry<T>, ChatLogSegmenter.MessageType> typeFunction;
   private final List<ChatLog.Entry<T>> messages = new ArrayList<>();
   @Nullable
   private ChatLogSegmenter.MessageType segmentType;

   public ChatLogSegmenter(Function<ChatLog.Entry<T>, ChatLogSegmenter.MessageType> var1) {
      super();
      this.typeFunction = var1;
   }

   public boolean accept(ChatLog.Entry<T> var1) {
      ChatLogSegmenter.MessageType var2 = this.typeFunction.apply(var1);
      if (this.segmentType != null && var2 != this.segmentType) {
         return false;
      } else {
         this.segmentType = var2;
         this.messages.add(var1);
         return true;
      }
   }

   @Nullable
   public ChatLogSegmenter.Results<T> build() {
      return !this.messages.isEmpty() && this.segmentType != null ? new ChatLogSegmenter.Results<>(this.messages, this.segmentType) : null;
   }

   public static enum MessageType {
      REPORTABLE,
      CONTEXT;

      private MessageType() {
      }

      public boolean foldable() {
         return this == CONTEXT;
      }
   }

   public static record Results<T extends LoggedChatMessage>(List<ChatLog.Entry<T>> a, ChatLogSegmenter.MessageType b) {
      private final List<ChatLog.Entry<T>> messages;
      private final ChatLogSegmenter.MessageType type;

      public Results(List<ChatLog.Entry<T>> var1, ChatLogSegmenter.MessageType var2) {
         super();
         this.messages = var1;
         this.type = var2;
      }
   }
}
