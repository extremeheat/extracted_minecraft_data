package net.minecraft.util.random;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.slf4j.Logger;

public class Weight {
   public static final Codec<Weight> CODEC;
   private static final Weight ONE;
   private static final Logger LOGGER;
   private final int value;

   private Weight(int var1) {
      super();
      this.value = var1;
   }

   public static Weight of(int var0) {
      if (var0 == 1) {
         return ONE;
      } else {
         validateWeight(var0);
         return new Weight(var0);
      }
   }

   public int asInt() {
      return this.value;
   }

   private static void validateWeight(int var0) {
      if (var0 < 0) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Weight should be >= 0"));
      } else {
         if (var0 == 0 && SharedConstants.IS_RUNNING_IN_IDE) {
            LOGGER.warn("Found 0 weight, make sure this is intentional!");
         }

      }
   }

   public String toString() {
      return Integer.toString(this.value);
   }

   public int hashCode() {
      return Integer.hashCode(this.value);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof Weight && this.value == ((Weight)var1).value;
      }
   }

   static {
      CODEC = Codec.INT.xmap(Weight::of, Weight::asInt);
      ONE = new Weight(1);
      LOGGER = LogUtils.getLogger();
   }
}
