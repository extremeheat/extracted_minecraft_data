package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.level.GameType;

public record GameTypePredicate(List<GameType> types) {
   public static final GameTypePredicate ANY = of(GameType.values());
   public static final GameTypePredicate SURVIVAL_LIKE;
   public static final Codec<GameTypePredicate> CODEC;

   public GameTypePredicate {
      super();
   }

   public static GameTypePredicate of(final GameType... types) {
      return new GameTypePredicate(Arrays.stream(types).toList());
   }

   public boolean matches(final GameType type) {
      return this.types.contains(type);
   }

   static {
      SURVIVAL_LIKE = of(GameType.SURVIVAL, GameType.ADVENTURE);
      CODEC = GameType.CODEC.listOf().xmap(GameTypePredicate::new, GameTypePredicate::types);
   }
}
