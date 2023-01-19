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
import net.minecraft.Util;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageLink;
import org.apache.commons.lang3.StringUtils;

public class ChatReportBuilder {
   private final ChatReportBuilder.ChatReport report;
   private final AbuseReportLimits limits;

   public ChatReportBuilder(ChatReportBuilder.ChatReport var1, AbuseReportLimits var2) {
      super();
      this.report = var1;
      this.limits = var2;
   }

   public ChatReportBuilder(UUID var1, AbuseReportLimits var2) {
      super();
      this.report = new ChatReportBuilder.ChatReport(UUID.randomUUID(), Instant.now(), var1);
      this.limits = var2;
   }

   public ChatReportBuilder.ChatReport report() {
      return this.report;
   }

   public UUID reportedProfileId() {
      return this.report.reportedProfileId;
   }

   public IntSet reportedMessages() {
      return this.report.reportedMessages;
   }

   public String comments() {
      return this.report.comments;
   }

   public void setComments(String var1) {
      this.report.comments = var1;
   }

   @Nullable
   public ReportReason reason() {
      return this.report.reason;
   }

   public void setReason(ReportReason var1) {
      this.report.reason = var1;
   }

   public void toggleReported(int var1) {
      this.report.toggleReported(var1, this.limits);
   }

   public boolean isReported(int var1) {
      return this.report.reportedMessages.contains(var1);
   }

   public boolean hasContent() {
      return StringUtils.isNotEmpty(this.comments()) || !this.reportedMessages().isEmpty() || this.reason() != null;
   }

   @Nullable
   public ChatReportBuilder.CannotBuildReason checkBuildable() {
      if (this.report.reportedMessages.isEmpty()) {
         return ChatReportBuilder.CannotBuildReason.NO_REPORTED_MESSAGES;
      } else if (this.report.reportedMessages.size() > this.limits.maxReportedMessageCount()) {
         return ChatReportBuilder.CannotBuildReason.TOO_MANY_MESSAGES;
      } else if (this.report.reason == null) {
         return ChatReportBuilder.CannotBuildReason.NO_REASON;
      } else {
         return this.report.comments.length() > this.limits.maxOpinionCommentsLength() ? ChatReportBuilder.CannotBuildReason.COMMENTS_TOO_LONG : null;
      }
   }

   public Either<ChatReportBuilder.Result, ChatReportBuilder.CannotBuildReason> build(ReportingContext var1) {
      ChatReportBuilder.CannotBuildReason var2 = this.checkBuildable();
      if (var2 != null) {
         return Either.right(var2);
      } else {
         String var3 = Objects.requireNonNull(this.report.reason).backendName();
         ReportEvidence var4 = this.buildEvidence(var1.chatLog());
         ReportedEntity var5 = new ReportedEntity(this.report.reportedProfileId);
         AbuseReport var6 = new AbuseReport(this.report.comments, var3, var4, var5, this.report.createdAt);
         return Either.left(new ChatReportBuilder.Result(this.report.reportId, var6));
      }
   }

   private ReportEvidence buildEvidence(ChatLog var1) {
      ArrayList var2 = new ArrayList();
      ChatReportContextBuilder var3 = new ChatReportContextBuilder(this.limits.leadingContextMessageCount());
      var3.collectAllContext(var1, this.report.reportedMessages, (var2x, var3x) -> var2.add(this.buildReportedChatMessage(var3x, this.isReported(var2x))));
      return new ReportEvidence(Lists.reverse(var2));
   }

   private ReportChatMessage buildReportedChatMessage(LoggedChatMessage.Player var1, boolean var2) {
      SignedMessageLink var3 = var1.message().link();
      SignedMessageBody var4 = var1.message().signedBody();
      List var5 = var4.lastSeen().entries().stream().map(MessageSignature::asByteBuffer).toList();
      ByteBuffer var6 = Util.mapNullable(var1.message().signature(), MessageSignature::asByteBuffer);
      return new ReportChatMessage(var3.index(), var3.sender(), var3.sessionId(), var4.timeStamp(), var4.salt(), var5, var4.content(), var6, var2);
   }

   public ChatReportBuilder copy() {
      return new ChatReportBuilder(this.report.copy(), this.limits);
   }

   public static record CannotBuildReason(Component e) {
      private final Component message;
      public static final ChatReportBuilder.CannotBuildReason NO_REASON = new ChatReportBuilder.CannotBuildReason(
         Component.translatable("gui.chatReport.send.no_reason")
      );
      public static final ChatReportBuilder.CannotBuildReason NO_REPORTED_MESSAGES = new ChatReportBuilder.CannotBuildReason(
         Component.translatable("gui.chatReport.send.no_reported_messages")
      );
      public static final ChatReportBuilder.CannotBuildReason TOO_MANY_MESSAGES = new ChatReportBuilder.CannotBuildReason(
         Component.translatable("gui.chatReport.send.too_many_messages")
      );
      public static final ChatReportBuilder.CannotBuildReason COMMENTS_TOO_LONG = new ChatReportBuilder.CannotBuildReason(
         Component.translatable("gui.chatReport.send.comments_too_long")
      );

      public CannotBuildReason(Component var1) {
         super();
         this.message = var1;
      }
   }

   public class ChatReport {
      final UUID reportId;
      final Instant createdAt;
      final UUID reportedProfileId;
      final IntSet reportedMessages = new IntOpenHashSet();
      String comments = "";
      @Nullable
      ReportReason reason;

      ChatReport(UUID var2, Instant var3, UUID var4) {
         super();
         this.reportId = var2;
         this.createdAt = var3;
         this.reportedProfileId = var4;
      }

      public void toggleReported(int var1, AbuseReportLimits var2) {
         if (this.reportedMessages.contains(var1)) {
            this.reportedMessages.remove(var1);
         } else if (this.reportedMessages.size() < var2.maxReportedMessageCount()) {
            this.reportedMessages.add(var1);
         }
      }

      public ChatReportBuilder.ChatReport copy() {
         ChatReportBuilder.ChatReport var1 = ChatReportBuilder.this.new ChatReport(this.reportId, this.createdAt, this.reportedProfileId);
         var1.reportedMessages.addAll(this.reportedMessages);
         var1.comments = this.comments;
         var1.reason = this.reason;
         return var1;
      }

      public boolean isReportedPlayer(UUID var1) {
         return var1.equals(this.reportedProfileId);
      }
   }

   public static record Result(UUID a, AbuseReport b) {
      private final UUID id;
      private final AbuseReport report;

      public Result(UUID var1, AbuseReport var2) {
         super();
         this.id = var1;
         this.report = var2;
      }
   }
}
