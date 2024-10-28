package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.NameReportScreen;
import org.apache.commons.lang3.StringUtils;

public class NameReport extends Report {
   private final String reportedName;

   NameReport(UUID var1, Instant var2, UUID var3, String var4) {
      super(var1, var2, var3);
      this.reportedName = var4;
   }

   public String getReportedName() {
      return this.reportedName;
   }

   public NameReport copy() {
      NameReport var1 = new NameReport(this.reportId, this.createdAt, this.reportedProfileId, this.reportedName);
      var1.comments = this.comments;
      var1.attested = this.attested;
      return var1;
   }

   public Screen createScreen(Screen var1, ReportingContext var2) {
      return new NameReportScreen(var1, var2, this);
   }

   // $FF: synthetic method
   public Report copy() {
      return this.copy();
   }

   public static class Builder extends Report.Builder<NameReport> {
      public Builder(NameReport var1, AbuseReportLimits var2) {
         super(var1, var2);
      }

      public Builder(UUID var1, String var2, AbuseReportLimits var3) {
         super(new NameReport(UUID.randomUUID(), Instant.now(), var1, var2), var3);
      }

      public boolean hasContent() {
         return StringUtils.isNotEmpty(this.comments());
      }

      @Nullable
      public Report.CannotBuildReason checkBuildable() {
         return ((NameReport)this.report).comments.length() > this.limits.maxOpinionCommentsLength() ? Report.CannotBuildReason.COMMENT_TOO_LONG : super.checkBuildable();
      }

      public Either<Report.Result, Report.CannotBuildReason> build(ReportingContext var1) {
         Report.CannotBuildReason var2 = this.checkBuildable();
         if (var2 != null) {
            return Either.right(var2);
         } else {
            ReportedEntity var3 = new ReportedEntity(((NameReport)this.report).reportedProfileId);
            AbuseReport var4 = AbuseReport.name(((NameReport)this.report).comments, var3, ((NameReport)this.report).createdAt);
            return Either.left(new Report.Result(((NameReport)this.report).reportId, ReportType.USERNAME, var4));
         }
      }
   }
}
