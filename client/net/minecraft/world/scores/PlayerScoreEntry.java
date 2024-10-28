package net.minecraft.world.scores;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;

public record PlayerScoreEntry(String owner, int value, @Nullable Component display, @Nullable NumberFormat numberFormatOverride) {
   public PlayerScoreEntry(String owner, int value, @Nullable Component display, @Nullable NumberFormat numberFormatOverride) {
      super();
      this.owner = owner;
      this.value = value;
      this.display = display;
      this.numberFormatOverride = numberFormatOverride;
   }

   public boolean isHidden() {
      return this.owner.startsWith("#");
   }

   public Component ownerName() {
      return (Component)(this.display != null ? this.display : Component.literal(this.owner()));
   }

   public MutableComponent formatValue(NumberFormat var1) {
      return ((NumberFormat)Objects.requireNonNullElse(this.numberFormatOverride, var1)).format(this.value);
   }

   public String owner() {
      return this.owner;
   }

   public int value() {
      return this.value;
   }

   @Nullable
   public Component display() {
      return this.display;
   }

   @Nullable
   public NumberFormat numberFormatOverride() {
      return this.numberFormatOverride;
   }
}
