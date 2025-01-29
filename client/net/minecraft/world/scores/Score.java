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
   public static final MapCodec<Score> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.INT.optionalFieldOf("Score", 0).forGetter(Score::value), Codec.BOOL.optionalFieldOf("Locked", false).forGetter(Score::isLocked), ComponentSerialization.CODEC.optionalFieldOf("display").forGetter((var0x) -> Optional.ofNullable(var0x.display)), NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter((var0x) -> Optional.ofNullable(var0x.numberFormat))).apply(var0, Score::new));
   private int value;
   private boolean locked = true;
   @Nullable
   private Component display;
   @Nullable
   private NumberFormat numberFormat;

   public Score() {
      super();
   }

   private Score(int var1, boolean var2, Optional<Component> var3, Optional<NumberFormat> var4) {
      super();
      this.value = var1;
      this.locked = var2;
      this.display = (Component)var3.orElse((Object)null);
      this.numberFormat = (NumberFormat)var4.orElse((Object)null);
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
}
