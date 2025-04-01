package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlock;

public record UnlockPredicate(List<Holder<PlayerUnlock>> unlocked, List<Holder<PlayerUnlock>> locked) implements Predicate<ServerPlayer> {
   public static final Codec<UnlockPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(PlayerUnlock.CODEC.listOf().fieldOf("unlocked").forGetter(UnlockPredicate::unlocked), PlayerUnlock.CODEC.listOf().fieldOf("locked").forGetter(UnlockPredicate::locked)).apply(var0, UnlockPredicate::new));
   public static final UnlockPredicate ANY = UnlockPredicate.Builder.unlocks().build();

   public UnlockPredicate(List<Holder<PlayerUnlock>> var1, List<Holder<PlayerUnlock>> var2) {
      super();
      this.unlocked = var1;
      this.locked = var2;
   }

   public boolean test(ServerPlayer var1) {
      if (this.unlocked.stream().anyMatch((var1x) -> !var1.isActive(var1x))) {
         return false;
      } else {
         Stream var10000 = this.locked.stream();
         Objects.requireNonNull(var1);
         return !var10000.anyMatch(var1::isActive);
      }
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((ServerPlayer)var1);
   }

   public static class Builder {
      private final List<Holder<PlayerUnlock>> unlocked = new ArrayList();
      private final List<Holder<PlayerUnlock>> locked = new ArrayList();

      public Builder() {
         super();
      }

      public static Builder unlocks() {
         return new Builder();
      }

      @SafeVarargs
      public final Builder isUnlocked(Holder<PlayerUnlock>... var1) {
         this.unlocked.addAll(Arrays.asList(var1));
         return this;
      }

      public Builder isLocked(Holder<PlayerUnlock> var1) {
         this.locked.add(var1);
         return this;
      }

      public UnlockPredicate build() {
         return new UnlockPredicate(List.copyOf(this.unlocked), List.copyOf(this.locked));
      }
   }
}
