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

   public static record CannotBuildReason(Component message) {
      public static final CannotBuildReason NO_REASON = new CannotBuildReason(Component.translatable("gui.abuseReport.send.no_reason"));
      public static final CannotBuildReason NO_REPORTED_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reported_messages"));
      public static final CannotBuildReason TOO_MANY_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.too_many_messages"));
      public static final CannotBuildReason COMMENT_TOO_LONG = new CannotBuildReason(Component.translatable("gui.abuseReport.send.comment_too_long"));

      public CannotBuildReason(Component message) {
         super();
         this.message = message;
      }

      public Tooltip tooltip() {
         return Tooltip.create(this.message);
      }

      public Component message() {
         return this.message;
      }
   }

   public static record Result(UUID id, ReportType reportType, AbuseReport report) {
      public Result(UUID id, ReportType reportType, AbuseReport report) {
         super();
         this.id = id;
         this.reportType = reportType;
         this.report = report;
      }

      public UUID id() {
         return this.id;
      }

      public ReportType reportType() {
         return this.reportType;
      }

      public AbuseReport report() {
         return this.report;
      }
   }

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

      public abstract boolean hasContent();

      @Nullable
      public abstract CannotBuildReason checkBuildable();

      public abstract Either<Result, CannotBuildReason> build(ReportingContext var1);
   }
}
