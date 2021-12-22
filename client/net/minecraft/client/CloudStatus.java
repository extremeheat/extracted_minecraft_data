package net.minecraft.client;

public enum CloudStatus {
   OFF("options.off"),
   FAST("options.clouds.fast"),
   FANCY("options.clouds.fancy");

   private final String key;

   private CloudStatus(String var3) {
      this.key = var3;
   }

   public String getKey() {
      return this.key;
   }

   // $FF: synthetic method
   private static CloudStatus[] $values() {
      return new CloudStatus[]{OFF, FAST, FANCY};
   }
}
