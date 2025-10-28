package net.minecraft.world.scores;

import java.util.Objects;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import org.jspecify.annotations.Nullable;

public interface ReadOnlyScoreInfo {
   int value();

   boolean isLocked();

   @Nullable NumberFormat numberFormat();

   default MutableComponent formatValue(NumberFormat var1) {
      return ((NumberFormat)Objects.requireNonNullElse(this.numberFormat(), var1)).format(this.value());
   }

   static MutableComponent safeFormatValue(@Nullable ReadOnlyScoreInfo var0, NumberFormat var1) {
      return var0 != null ? var0.formatValue(var1) : var1.format(0);
   }
}
