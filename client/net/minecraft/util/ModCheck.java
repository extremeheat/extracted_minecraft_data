package net.minecraft.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public record ModCheck(ModCheck.Confidence a, String b) {
   private final ModCheck.Confidence confidence;
   private final String description;

   public ModCheck(ModCheck.Confidence var1, String var2) {
      super();
      this.confidence = var1;
      this.description = var2;
   }

   public static ModCheck identify(String var0, Supplier<String> var1, String var2, Class<?> var3) {
      String var4 = (String)var1.get();
      if (!var0.equals(var4)) {
         return new ModCheck(ModCheck.Confidence.DEFINITELY, var2 + " brand changed to '" + var4 + "'");
      } else {
         return var3.getSigners() == null ? new ModCheck(ModCheck.Confidence.VERY_LIKELY, var2 + " jar signature invalidated") : new ModCheck(ModCheck.Confidence.PROBABLY_NOT, var2 + " jar signature and brand is untouched");
      }
   }

   public boolean shouldReportAsModified() {
      return this.confidence.shouldReportAsModified;
   }

   public ModCheck merge(ModCheck var1) {
      return new ModCheck((ModCheck.Confidence)ObjectUtils.max(new ModCheck.Confidence[]{this.confidence, var1.confidence}), this.description + "; " + var1.description);
   }

   public String fullDescription() {
      return this.confidence.description + " " + this.description;
   }

   public ModCheck.Confidence confidence() {
      return this.confidence;
   }

   public String description() {
      return this.description;
   }

   public static enum Confidence {
      PROBABLY_NOT("Probably not.", false),
      VERY_LIKELY("Very likely;", true),
      DEFINITELY("Definitely;", true);

      final String description;
      final boolean shouldReportAsModified;

      private Confidence(String var3, boolean var4) {
         this.description = var3;
         this.shouldReportAsModified = var4;
      }

      // $FF: synthetic method
      private static ModCheck.Confidence[] $values() {
         return new ModCheck.Confidence[]{PROBABLY_NOT, VERY_LIKELY, DEFINITELY};
      }
   }
}
