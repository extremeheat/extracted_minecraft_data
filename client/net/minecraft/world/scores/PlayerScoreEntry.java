package net.minecraft.world.scores;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;

public record PlayerScoreEntry(String a, int b, @Nullable Component c, @Nullable NumberFormat d) {
   private final String owner;
   private final int value;
   @Nullable
   private final Component display;
   @Nullable
   private final NumberFormat numberFormatOverride;

   public PlayerScoreEntry(String var1, int var2, @Nullable Component var3, @Nullable NumberFormat var4) {
      super();
      this.owner = var1;
      this.value = var2;
      this.display = var3;
      this.numberFormatOverride = var4;
   }

   public boolean isHidden() {
      return this.owner.startsWith("#");
   }

   public Component ownerName() {
      return (Component)(this.display != null ? this.display : Component.literal(this.owner()));
   }

   public MutableComponent formatValue(NumberFormat var1) {
      return Objects.requireNonNullElse(this.numberFormatOverride, var1).format(this.value);
   }
}
