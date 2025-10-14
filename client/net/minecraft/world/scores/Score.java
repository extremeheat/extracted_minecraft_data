package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;

public class Score implements ReadOnlyScoreInfo {
   private int value;
   private boolean locked = true;
   @Nullable
   private Component display;
   @Nullable
   private NumberFormat numberFormat;

   public Score() {
      super();
   }

   public Score(Packed var1) {
      super();
      this.value = var1.value;
      this.locked = var1.locked;
      this.display = (Component)var1.display.orElse((Object)null);
      this.numberFormat = (NumberFormat)var1.numberFormat.orElse((Object)null);
   }

   public Packed pack() {
      return new Packed(this.value, this.locked, Optional.ofNullable(this.display), Optional.ofNullable(this.numberFormat));
   }

   public int value() {
      return this.value;
   }

   public void value(int var1) {
      this.value = var1;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }

   @Nullable
   public Component display() {
      return this.display;
   }

   public void display(@Nullable Component var1) {
      this.display = var1;
   }

   @Nullable
   public NumberFormat numberFormat() {
      return this.numberFormat;
   }

   public void numberFormat(@Nullable NumberFormat var1) {
      this.numberFormat = var1;
   }

   public static record Packed(int value, boolean locked, Optional<Component> display, Optional<NumberFormat> numberFormat) {
      final int value;
      final boolean locked;
      final Optional<Component> display;
      final Optional<NumberFormat> numberFormat;
      public static final MapCodec<Packed> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.INT.optionalFieldOf("Score", 0).forGetter(Packed::value), Codec.BOOL.optionalFieldOf("Locked", false).forGetter(Packed::locked), ComponentSerialization.CODEC.optionalFieldOf("display").forGetter(Packed::display), NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter(Packed::numberFormat)).apply(var0, Packed::new));

      public Packed(int var1, boolean var2, Optional<Component> var3, Optional<NumberFormat> var4) {
         super();
         this.value = var1;
         this.locked = var2;
         this.display = var3;
         this.numberFormat = var4;
      }
   }
}
