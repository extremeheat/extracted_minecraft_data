package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.server.packs.PackResources;
import org.slf4j.Logger;

public class ResourceLoadStateTracker {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private ReloadState reloadState;
   private int reloadCount;

   public ResourceLoadStateTracker() {
      super();
   }

   public void startReload(ReloadReason var1, List<PackResources> var2) {
      ++this.reloadCount;
      if (this.reloadState != null && !this.reloadState.finished) {
         LOGGER.warn("Reload already ongoing, replacing");
      }

      this.reloadState = new ReloadState(var1, (List)var2.stream().map(PackResources::getName).collect(ImmutableList.toImmutableList()));
   }

   public void startRecovery(Throwable var1) {
      if (this.reloadState == null) {
         LOGGER.warn("Trying to signal reload recovery, but nothing was started");
         this.reloadState = new ReloadState(ResourceLoadStateTracker.ReloadReason.UNKNOWN, ImmutableList.of());
      }

      this.reloadState.recoveryReloadInfo = new RecoveryInfo(var1);
   }

   public void finishReload() {
      if (this.reloadState == null) {
         LOGGER.warn("Trying to finish reload, but nothing was started");
      } else {
         this.reloadState.finished = true;
      }

   }

   public void fillCrashReport(CrashReport var1) {
      CrashReportCategory var2 = var1.addCategory("Last reload");
      var2.setDetail("Reload number", (Object)this.reloadCount);
      if (this.reloadState != null) {
         this.reloadState.fillCrashInfo(var2);
      }

   }

   static class ReloadState {
      private final ReloadReason reloadReason;
      private final List<String> packs;
      @Nullable
      RecoveryInfo recoveryReloadInfo;
      boolean finished;

      ReloadState(ReloadReason var1, List<String> var2) {
         super();
         this.reloadReason = var1;
         this.packs = var2;
      }

      public void fillCrashInfo(CrashReportCategory var1) {
         var1.setDetail("Reload reason", (Object)this.reloadReason.name);
         var1.setDetail("Finished", (Object)(this.finished ? "Yes" : "No"));
         var1.setDetail("Packs", () -> {
            return String.join(", ", this.packs);
         });
         if (this.recoveryReloadInfo != null) {
            this.recoveryReloadInfo.fillCrashInfo(var1);
         }

      }
   }

   public static enum ReloadReason {
      INITIAL("initial"),
      MANUAL("manual"),
      UNKNOWN("unknown");

      final String name;

      private ReloadReason(String var3) {
         this.name = var3;
      }

      // $FF: synthetic method
      private static ReloadReason[] $values() {
         return new ReloadReason[]{INITIAL, MANUAL, UNKNOWN};
      }
   }

   private static class RecoveryInfo {
      private final Throwable error;

      RecoveryInfo(Throwable var1) {
         super();
         this.error = var1;
      }

      public void fillCrashInfo(CrashReportCategory var1) {
         var1.setDetail("Recovery", (Object)"Yes");
         var1.setDetail("Recovery reason", () -> {
            StringWriter var1 = new StringWriter();
            this.error.printStackTrace(new PrintWriter(var1));
            return var1.toString();
         });
      }
   }
}
