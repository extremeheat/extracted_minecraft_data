package net.minecraft.client.resources.server;

import java.util.UUID;

public interface PackLoadFeedback {
   void reportUpdate(UUID var1, Update var2);

   void reportFinalResult(UUID var1, FinalResult var2);

   public static enum FinalResult {
      DECLINED,
      APPLIED,
      DISCARDED,
      DOWNLOAD_FAILED,
      ACTIVATION_FAILED;

      private FinalResult() {
      }

      // $FF: synthetic method
      private static FinalResult[] $values() {
         return new FinalResult[]{DECLINED, APPLIED, DISCARDED, DOWNLOAD_FAILED, ACTIVATION_FAILED};
      }
   }

   public static enum Update {
      ACCEPTED,
      DOWNLOADED;

      private Update() {
      }

      // $FF: synthetic method
      private static Update[] $values() {
         return new Update[]{ACCEPTED, DOWNLOADED};
      }
   }
}
