package net.minecraft.world.scores;

import java.util.Objects;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import org.jspecify.annotations.Nullable;

public interface ReadOnlyScoreInfo {
   int value();

   boolean isLocked();

   @Nullable NumberFormat numberFormat();

   default MutableComponent formatValue(final NumberFormat defaultFormat) {
      return ((NumberFormat)Objects.requireNonNullElse(this.numberFormat(), defaultFormat)).format(this.value());
   }

   static MutableComponent safeFormatValue(final @Nullable ReadOnlyScoreInfo scoreInfo, final NumberFormat defaultFormat) {
      return scoreInfo != null ? scoreInfo.formatValue(defaultFormat) : defaultFormat.format(0);
   }
}
