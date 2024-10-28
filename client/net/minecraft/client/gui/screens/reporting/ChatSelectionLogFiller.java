package net.minecraft.client.gui.screens.reporting;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.report.ChatReportContextBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageLink;

public class ChatSelectionLogFiller {
   private final ChatLog log;
   private final ChatReportContextBuilder contextBuilder;
   private final Predicate<LoggedChatMessage.Player> canReport;
   @Nullable
   private SignedMessageLink previousLink = null;
   private int eventId;
   private int missedCount;
   @Nullable
   private PlayerChatMessage lastMessage;

   public ChatSelectionLogFiller(ReportingContext var1, Predicate<LoggedChatMessage.Player> var2) {
      super();
      this.log = var1.chatLog();
      this.contextBuilder = new ChatReportContextBuilder(var1.sender().reportLimits().leadingContextMessageCount());
      this.canReport = var2;
      this.eventId = this.log.end();
   }

   public void fillNextPage(int var1, Output var2) {
      int var3 = 0;

      while(var3 < var1) {
         LoggedChatEvent var4 = this.log.lookup(this.eventId);
         if (var4 == null) {
            break;
         }

         int var5 = this.eventId--;
         if (var4 instanceof LoggedChatMessage.Player var6) {
            if (!var6.message().equals(this.lastMessage)) {
               if (this.acceptMessage(var2, var6)) {
                  if (this.missedCount > 0) {
                     var2.acceptDivider(Component.translatable("gui.chatSelection.fold", this.missedCount));
                     this.missedCount = 0;
                  }

                  var2.acceptMessage(var5, var6);
                  ++var3;
               } else {
                  ++this.missedCount;
               }

               this.lastMessage = var6.message();
            }
         }
      }

   }

   private boolean acceptMessage(Output var1, LoggedChatMessage.Player var2) {
      PlayerChatMessage var3 = var2.message();
      boolean var4 = this.contextBuilder.acceptContext(var3);
      if (this.canReport.test(var2)) {
         this.contextBuilder.trackContext(var3);
         if (this.previousLink != null && !this.previousLink.isDescendantOf(var3.link())) {
            var1.acceptDivider(Component.translatable("gui.chatSelection.join", var2.profile().getName()).withStyle(ChatFormatting.YELLOW));
         }

         this.previousLink = var3.link();
         return true;
      } else {
         return var4;
      }
   }

   public interface Output {
      void acceptMessage(int var1, LoggedChatMessage.Player var2);

      void acceptDivider(Component var1);
   }
}
