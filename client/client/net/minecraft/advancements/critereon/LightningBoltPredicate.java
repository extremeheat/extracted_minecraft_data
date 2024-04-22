package net.minecraft.advancements.critereon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public record LightningBoltPredicate(MinMaxBounds.Ints blocksSetOnFire, Optional<EntityPredicate> entityStruck) implements EntitySubPredicate {
   public static final MapCodec<LightningBoltPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               MinMaxBounds.Ints.CODEC.optionalFieldOf("blocks_set_on_fire", MinMaxBounds.Ints.ANY).forGetter(LightningBoltPredicate::blocksSetOnFire),
               EntityPredicate.CODEC.optionalFieldOf("entity_struck").forGetter(LightningBoltPredicate::entityStruck)
            )
            .apply(var0, LightningBoltPredicate::new)
   );

   public LightningBoltPredicate(MinMaxBounds.Ints blocksSetOnFire, Optional<EntityPredicate> entityStruck) {
      super();
      this.blocksSetOnFire = blocksSetOnFire;
      this.entityStruck = entityStruck;
   }

   public static LightningBoltPredicate blockSetOnFire(MinMaxBounds.Ints var0) {
      return new LightningBoltPredicate(var0, Optional.empty());
   }

   @Override
   public MapCodec<LightningBoltPredicate> codec() {
      return EntitySubPredicates.LIGHTNING;
   }

   @Override
   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      return !(var1 instanceof LightningBolt var4)
         ? false
         : this.blocksSetOnFire.matches(var4.getBlocksSetOnFire())
            && (this.entityStruck.isEmpty() || var4.getHitEntities().anyMatch(var3x -> this.entityStruck.get().matches(var2, var3, var3x)));
   }
}