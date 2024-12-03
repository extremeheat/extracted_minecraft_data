package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.level.GameType;

public record GameTypePredicate(List<GameType> types) {
   public static final GameTypePredicate ANY = of(GameType.values());
   public static final GameTypePredicate SURVIVAL_LIKE;
   public static final Codec<GameTypePredicate> CODEC;

   public GameTypePredicate(List<GameType> var1) {
      super();
      this.types = var1;
   }

   public static GameTypePredicate of(GameType... var0) {
      return new GameTypePredicate(Arrays.stream(var0).toList());
   }

   public boolean matches(GameType var1) {
      return this.types.contains(var1);
   }

   static {
      SURVIVAL_LIKE = of(GameType.SURVIVAL, GameType.ADVENTURE);
      CODEC = GameType.CODEC.listOf().xmap(GameTypePredicate::new, GameTypePredicate::types);
   }
}
