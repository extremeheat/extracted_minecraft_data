package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;

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

   public abstract static class Builder<R extends Report> {
      protected final R report;
      protected final AbuseReportLimits limits;

      protected Builder(R var1, AbuseReportLimits var2) {
         super();
         this.report = (R)var1;
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
      public abstract Report.CannotBuildReason checkBuildable();

      public abstract Either<Report.Result, Report.CannotBuildReason> build(ReportingContext var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
