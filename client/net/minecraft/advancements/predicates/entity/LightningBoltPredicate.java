package net.minecraft.advancements.predicates.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record LightningBoltPredicate(MinMaxBounds.Ints blocksSetOnFire, Optional<EntityPredicate> entityStruck) implements EntitySubPredicate {
   public static final Codec<LightningBoltPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("blocks_set_on_fire", MinMaxBounds.Ints.ANY).forGetter(LightningBoltPredicate::blocksSetOnFire), EntityPredicate.CODEC.optionalFieldOf("entity_struck").forGetter(LightningBoltPredicate::entityStruck)).apply(i, LightningBoltPredicate::new));

   public LightningBoltPredicate {
      super();
   }

   public static LightningBoltPredicate blockSetOnFire(final MinMaxBounds.Ints count) {
      return new LightningBoltPredicate(count, Optional.empty());
   }

   public boolean matches(final Entity entity, final ServerLevel level, final @Nullable Vec3 position) {
      if (!(entity instanceof LightningBolt bolt)) {
         return false;
      } else {
         return this.blocksSetOnFire.matches(bolt.getBlocksSetOnFire()) && (this.entityStruck.isEmpty() || bolt.getHitEntities().anyMatch((e) -> ((EntityPredicate)this.entityStruck.get()).matches(level, position, e)));
      }
   }
}
