package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.SkinReportScreen;
import net.minecraft.client.resources.PlayerSkin;
import org.apache.commons.lang3.StringUtils;

public class SkinReport extends Report {
   final Supplier<PlayerSkin> skinGetter;

   SkinReport(UUID var1, Instant var2, UUID var3, Supplier<PlayerSkin> var4) {
      super(var1, var2, var3);
      this.skinGetter = var4;
   }

   public Supplier<PlayerSkin> getSkinGetter() {
      return this.skinGetter;
   }

   public SkinReport copy() {
      SkinReport var1 = new SkinReport(this.reportId, this.createdAt, this.reportedProfileId, this.skinGetter);
      var1.comments = this.comments;
      var1.reason = this.reason;
      var1.attested = this.attested;
      return var1;
   }

   public Screen createScreen(Screen var1, ReportingContext var2) {
      return new SkinReportScreen(var1, var2, this);
   }

   // $FF: synthetic method
   public Report copy() {
      return this.copy();
   }

   public static class Builder extends Report.Builder<SkinReport> {
      public Builder(SkinReport var1, AbuseReportLimits var2) {
         super(var1, var2);
      }

      public Builder(UUID var1, Supplier<PlayerSkin> var2, AbuseReportLimits var3) {
         super(new SkinReport(UUID.randomUUID(), Instant.now(), var1, var2), var3);
      }

      public boolean hasContent() {
         return StringUtils.isNotEmpty(this.comments()) || this.reason() != null;
      }

      @Nullable
      public Report.CannotBuildReason checkBuildable() {
         if (((SkinReport)this.report).reason == null) {
            return Report.CannotBuildReason.NO_REASON;
         } else {
            return ((SkinReport)this.report).comments.length() > this.limits.maxOpinionCommentsLength() ? Report.CannotBuildReason.COMMENT_TOO_LONG : super.checkBuildable();
         }
      }

      public Either<Report.Result, Report.CannotBuildReason> build(ReportingContext var1) {
         Report.CannotBuildReason var2 = this.checkBuildable();
         if (var2 != null) {
            return Either.right(var2);
         } else {
            String var3 = ((ReportReason)Objects.requireNonNull(((SkinReport)this.report).reason)).backendName();
            ReportedEntity var4 = new ReportedEntity(((SkinReport)this.report).reportedProfileId);
            PlayerSkin var5 = (PlayerSkin)((SkinReport)this.report).skinGetter.get();
            String var6 = var5.textureUrl();
            AbuseReport var7 = AbuseReport.skin(((SkinReport)this.report).comments, var3, var6, var4, ((SkinReport)this.report).createdAt);
            return Either.left(new Report.Result(((SkinReport)this.report).reportId, ReportType.SKIN, var7));
         }
      }
   }
}
