package net.minecraft.client.gui.screens.reporting;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ChatSelectionLogFiller<T extends LoggedChatMessage> {
   private static final int CONTEXT_FOLDED_SIZE = 4;
   private final ChatLog log;
   private final Predicate<T> canReport;
   private int nextMessageId;
   final Class<T> tClass;

   public ChatSelectionLogFiller(ChatLog var1, Predicate<T> var2, Class<T> var3) {
      super();
      this.log = var1;
      this.canReport = var2;
      this.nextMessageId = var1.newest();
      this.tClass = var3;
   }

   public void fillNextPage(int var1, Output<T> var2) {
      int var3 = 0;

      while(var3 < var1) {
         ChatLogSegmenter.Results var4 = this.nextSegment();
         if (var4 == null) {
            break;
         }

         if (var4.type().foldable()) {
            var3 += this.addFoldedMessagesTo(var4.messages(), var2);
         } else {
            var2.acceptMessages(var4.messages());
            var3 += var4.messages().size();
         }
      }

   }

   private int addFoldedMessagesTo(List<ChatLog.Entry<T>> var1, Output<T> var2) {
      boolean var3 = true;
      if (var1.size() > 8) {
         int var4 = var1.size() - 8;
         var2.acceptMessages(var1.subList(0, 4));
         var2.acceptDivider(Component.translatable("gui.chatSelection.fold", var4));
         var2.acceptMessages(var1.subList(var1.size() - 4, var1.size()));
         return 9;
      } else {
         var2.acceptMessages(var1);
         return var1.size();
      }
   }

   @Nullable
   private ChatLogSegmenter.@Nullable Results<T> nextSegment() {
      ChatLogSegmenter var1 = new ChatLogSegmenter((var1x) -> {
         return this.getMessageType((LoggedChatMessage)var1x.event());
      });
      Stream var10000 = this.log.selectBefore(this.nextMessageId).entries().map((var1x) -> {
         return var1x.tryCast(this.tClass);
      }).filter(Objects::nonNull);
      Objects.requireNonNull(var1);
      OptionalInt var2 = var10000.takeWhile(var1::accept).mapToInt(ChatLog.Entry::id).reduce((var0, var1x) -> {
         return var1x;
      });
      if (var2.isPresent()) {
         this.nextMessageId = this.log.before(var2.getAsInt());
      }

      return var1.build();
   }

   private ChatLogSegmenter.MessageType getMessageType(T var1) {
      return this.canReport.test(var1) ? ChatLogSegmenter.MessageType.REPORTABLE : ChatLogSegmenter.MessageType.CONTEXT;
   }

   public interface Output<T extends LoggedChatMessage> {
      default void acceptMessages(Iterable<ChatLog.Entry<T>> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ChatLog.Entry var3 = (ChatLog.Entry)var2.next();
            this.acceptMessage(var3.id(), (LoggedChatMessage)var3.event());
         }

      }

      void acceptMessage(int var1, T var2);

      void acceptDivider(Component var1);
   }
}
