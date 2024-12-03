package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.entity.player.Input;

public record InputPredicate(Optional<Boolean> forward, Optional<Boolean> backward, Optional<Boolean> left, Optional<Boolean> right, Optional<Boolean> jump, Optional<Boolean> sneak, Optional<Boolean> sprint) {
   public static final Codec<InputPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.optionalFieldOf("forward").forGetter(InputPredicate::forward), Codec.BOOL.optionalFieldOf("backward").forGetter(InputPredicate::backward), Codec.BOOL.optionalFieldOf("left").forGetter(InputPredicate::left), Codec.BOOL.optionalFieldOf("right").forGetter(InputPredicate::right), Codec.BOOL.optionalFieldOf("jump").forGetter(InputPredicate::jump), Codec.BOOL.optionalFieldOf("sneak").forGetter(InputPredicate::sneak), Codec.BOOL.optionalFieldOf("sprint").forGetter(InputPredicate::sprint)).apply(var0, InputPredicate::new));

   public InputPredicate(Optional<Boolean> var1, Optional<Boolean> var2, Optional<Boolean> var3, Optional<Boolean> var4, Optional<Boolean> var5, Optional<Boolean> var6, Optional<Boolean> var7) {
      super();
      this.forward = var1;
      this.backward = var2;
      this.left = var3;
      this.right = var4;
      this.jump = var5;
      this.sneak = var6;
      this.sprint = var7;
   }

   public boolean matches(Input var1) {
      return this.matches(this.forward, var1.forward()) && this.matches(this.backward, var1.backward()) && this.matches(this.left, var1.left()) && this.matches(this.right, var1.right()) && this.matches(this.jump, var1.jump()) && this.matches(this.sneak, var1.shift()) && this.matches(this.sprint, var1.sprint());
   }

   private boolean matches(Optional<Boolean> var1, boolean var2) {
      return (Boolean)var1.map((var1x) -> var1x == var2).orElse(true);
   }
}
