package net.minecraft.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public record ModCheck(Confidence confidence, String description) {
   public ModCheck {
      super();
   }

   public static ModCheck identify(final String expectedBrand, final Supplier<String> actualBrand, final String component, final Class<?> canaryClass) {
      String mod = (String)actualBrand.get();
      if (!expectedBrand.equals(mod)) {
         return new ModCheck(ModCheck.Confidence.DEFINITELY, component + " brand changed to '" + mod + "'");
      } else {
         return canaryClass.getSigners() == null ? new ModCheck(ModCheck.Confidence.VERY_LIKELY, component + " jar signature invalidated") : new ModCheck(ModCheck.Confidence.PROBABLY_NOT, component + " jar signature and brand is untouched");
      }
   }

   public boolean shouldReportAsModified() {
      return this.confidence.shouldReportAsModified;
   }

   public ModCheck merge(final ModCheck other) {
      return new ModCheck((Confidence)ObjectUtils.max(new Confidence[]{this.confidence, other.confidence}), this.description + "; " + other.description);
   }

   public String fullDescription() {
      return this.confidence.description + " " + this.description;
   }

   public static enum Confidence {
      PROBABLY_NOT("Probably not.", false),
      VERY_LIKELY("Very likely;", true),
      DEFINITELY("Definitely;", true);

      private final String description;
      private final boolean shouldReportAsModified;

      private Confidence(final String description, final boolean shouldReportAsModified) {
         this.description = description;
         this.shouldReportAsModified = shouldReportAsModified;
      }

      // $FF: synthetic method
      private static Confidence[] $values() {
         return new Confidence[]{PROBABLY_NOT, VERY_LIKELY, DEFINITELY};
      }
   }
}
