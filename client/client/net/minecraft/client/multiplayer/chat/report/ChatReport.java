package net.minecraft.client.multiplayer.chat.report;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.ChatReportScreen;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageLink;
import org.apache.commons.lang3.StringUtils;

public class ChatReport extends Report {
   final IntSet reportedMessages = new IntOpenHashSet();

   ChatReport(UUID var1, Instant var2, UUID var3) {
      super(var1, var2, var3);
   }

   public void toggleReported(int var1, AbuseReportLimits var2) {
      if (this.reportedMessages.contains(var1)) {
         this.reportedMessages.remove(var1);
      } else if (this.reportedMessages.size() < var2.maxReportedMessageCount()) {
         this.reportedMessages.add(var1);
      }
   }

   public ChatReport copy() {
      ChatReport var1 = new ChatReport(this.reportId, this.createdAt, this.reportedProfileId);
      var1.reportedMessages.addAll(this.reportedMessages);
      var1.comments = this.comments;
      var1.reason = this.reason;
      return var1;
   }

   @Override
   public Screen createScreen(Screen var1, ReportingContext var2) {
      return new ChatReportScreen(var1, var2, this);
   }

   public static class Builder extends Report.Builder<ChatReport> {
      public Builder(ChatReport var1, AbuseReportLimits var2) {
         super(var1, var2);
      }

      public Builder(UUID var1, AbuseReportLimits var2) {
         super(new ChatReport(UUID.randomUUID(), Instant.now(), var1), var2);
      }

      public IntSet reportedMessages() {
         return this.report.reportedMessages;
      }

      public void toggleReported(int var1) {
         this.report.toggleReported(var1, this.limits);
      }

      public boolean isReported(int var1) {
         return this.report.reportedMessages.contains(var1);
      }

      @Override
      public boolean hasContent() {
         return StringUtils.isNotEmpty(this.comments()) || !this.reportedMessages().isEmpty() || this.reason() != null;
      }

      @Nullable
      @Override
      public Report.CannotBuildReason checkBuildable() {
         if (this.report.reportedMessages.isEmpty()) {
            return Report.CannotBuildReason.NO_REPORTED_MESSAGES;
         } else if (this.report.reportedMessages.size() > this.limits.maxReportedMessageCount()) {
            return Report.CannotBuildReason.TOO_MANY_MESSAGES;
         } else if (this.report.reason == null) {
            return Report.CannotBuildReason.NO_REASON;
         } else {
            return this.report.comments.length() > this.limits.maxOpinionCommentsLength() ? Report.CannotBuildReason.COMMENT_TOO_LONG : null;
         }
      }

      @Override
      public Either<Report.Result, Report.CannotBuildReason> build(ReportingContext var1) {
         Report.CannotBuildReason var2 = this.checkBuildable();
         if (var2 != null) {
            return Either.right(var2);
         } else {
            String var3 = Objects.requireNonNull(this.report.reason).backendName();
            ReportEvidence var4 = this.buildEvidence(var1);
            ReportedEntity var5 = new ReportedEntity(this.report.reportedProfileId);
            AbuseReport var6 = AbuseReport.chat(this.report.comments, var3, var4, var5, this.report.createdAt);
            return Either.left(new Report.Result(this.report.reportId, ReportType.CHAT, var6));
         }
      }

      private ReportEvidence buildEvidence(ReportingContext var1) {
         ArrayList var2 = new ArrayList();
         ChatReportContextBuilder var3 = new ChatReportContextBuilder(this.limits.leadingContextMessageCount());
         var3.collectAllContext(
            var1.chatLog(), this.report.reportedMessages, (var2x, var3x) -> var2.add(this.buildReportedChatMessage(var3x, this.isReported(var2x)))
         );
         return new ReportEvidence(Lists.reverse(var2));
      }

      private ReportChatMessage buildReportedChatMessage(LoggedChatMessage.Player var1, boolean var2) {
         SignedMessageLink var3 = var1.message().link();
         SignedMessageBody var4 = var1.message().signedBody();
         List var5 = var4.lastSeen().entries().stream().map(MessageSignature::asByteBuffer).toList();
         ByteBuffer var6 = Optionull.map(var1.message().signature(), MessageSignature::asByteBuffer);
         return new ReportChatMessage(var3.index(), var3.sender(), var3.sessionId(), var4.timeStamp(), var4.salt(), var5, var4.content(), var6, var2);
      }

      public ChatReport.Builder copy() {
         return new ChatReport.Builder(this.report.copy(), this.limits);
      }
   }
}
