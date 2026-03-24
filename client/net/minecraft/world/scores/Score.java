package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import org.jspecify.annotations.Nullable;

public class Score implements ReadOnlyScoreInfo {
   private int value;
   private boolean locked = true;
   private @Nullable Component display;
   private @Nullable NumberFormat numberFormat;

   public Score() {
      super();
   }

   public Score(final Packed packed) {
      super();
      this.value = packed.value;
      this.locked = packed.locked;
      this.display = (Component)packed.display.orElse((Object)null);
      this.numberFormat = (NumberFormat)packed.numberFormat.orElse((Object)null);
   }

   public Packed pack() {
      return new Packed(this.value, this.locked, Optional.ofNullable(this.display), Optional.ofNullable(this.numberFormat));
   }

   public int value() {
      return this.value;
   }

   public void value(final int score) {
      this.value = score;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(final boolean locked) {
      this.locked = locked;
   }

   public @Nullable Component display() {
      return this.display;
   }

   public void display(final @Nullable Component display) {
      this.display = display;
   }

   public @Nullable NumberFormat numberFormat() {
      return this.numberFormat;
   }

   public void numberFormat(final @Nullable NumberFormat numberFormat) {
      this.numberFormat = numberFormat;
   }

   public static record Packed(int value, boolean locked, Optional<Component> display, Optional<NumberFormat> numberFormat) {
      public static final MapCodec<Packed> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.optionalFieldOf("Score", 0).forGetter(Packed::value), Codec.BOOL.optionalFieldOf("Locked", false).forGetter(Packed::locked), ComponentSerialization.CODEC.optionalFieldOf("display").forGetter(Packed::display), NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter(Packed::numberFormat)).apply(i, Packed::new));

      public Packed {
         super();
      }
   }
}
