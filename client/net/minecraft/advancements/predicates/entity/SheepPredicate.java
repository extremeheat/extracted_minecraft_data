package net.minecraft.advancements.predicates.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record SheepPredicate(Optional<Boolean> sheared) implements EntitySubPredicate {
   public static final Codec<SheepPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.BOOL.optionalFieldOf("sheared").forGetter(SheepPredicate::sheared)).apply(i, SheepPredicate::new));

   public SheepPredicate {
      super();
   }

   public boolean matches(final Entity entity, final ServerLevel level, final @Nullable Vec3 position) {
      if (entity instanceof Sheep sheep) {
         return !this.sheared.isPresent() || sheep.isSheared() == (Boolean)this.sheared.get();
      } else {
         return false;
      }
   }

   public static SheepPredicate hasWool() {
      return new SheepPredicate(Optional.of(false));
   }
}
