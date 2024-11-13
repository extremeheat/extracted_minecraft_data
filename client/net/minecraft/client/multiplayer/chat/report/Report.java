package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class Report {
   protected final UUID reportId;
   protected final Instant createdAt;
   protected final UUID reportedProfileId;
   protected String comments = "";
   @Nullable
   protected ReportReason reason;
   protected boolean attested;

   public Report(UUID var1, Instant var2, UUID var3) {
      super();
      this.reportId = var1;
      this.createdAt = var2;
      this.reportedProfileId = var3;
   }

   public boolean isReportedPlayer(UUID var1) {
      return var1.equals(this.reportedProfileId);
   }

   public abstract Report copy();

   public abstract Screen createScreen(Screen var1, ReportingContext var2);

   public abstract static class Builder<R extends Report> {
      protected final R report;
      protected final AbuseReportLimits limits;

      protected Builder(R var1, AbuseReportLimits var2) {
         super();
         this.report = var1;
         this.limits = var2;
      }

      public R report() {
         return this.report;
      }

      public UUID reportedProfileId() {
         return this.report.reportedProfileId;
      }

      public String comments() {
         return this.report.comments;
      }

      public boolean attested() {
         return this.report().attested;
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

      public void setAttested(boolean var1) {
         this.report.attested = var1;
      }

      public abstract boolean hasContent();

      @Nullable
      public CannotBuildReason checkBuildable() {
         return !this.report().attested ? Report.CannotBuildReason.NOT_ATTESTED : null;
      }

      public abstract Either<Result, CannotBuildReason> build(ReportingContext var1);
   }

   public static record Result(UUID id, ReportType reportType, AbuseReport report) {
      public Result(UUID var1, ReportType var2, AbuseReport var3) {
         super();
         this.id = var1;
         this.reportType = var2;
         this.report = var3;
      }
   }

   public static record CannotBuildReason(Component message) {
      public static final CannotBuildReason NO_REASON = new CannotBuildReason(Component.translatable("gui.abuseReport.send.no_reason"));
      public static final CannotBuildReason NO_REPORTED_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reported_messages"));
      public static final CannotBuildReason TOO_MANY_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.too_many_messages"));
      public static final CannotBuildReason COMMENT_TOO_LONG = new CannotBuildReason(Component.translatable("gui.abuseReport.send.comment_too_long"));
      public static final CannotBuildReason NOT_ATTESTED = new CannotBuildReason(Component.translatable("gui.abuseReport.send.not_attested"));

      public CannotBuildReason(Component var1) {
         super();
         this.message = var1;
      }

      public Tooltip tooltip() {
         return Tooltip.create(this.message);
      }
   }
}
