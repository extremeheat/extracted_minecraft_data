package net.minecraft.advancements.critereon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public record LightningBoltPredicate(MinMaxBounds.Ints c, Optional<EntityPredicate> d) implements EntitySubPredicate {
   private final MinMaxBounds.Ints blocksSetOnFire;
   private final Optional<EntityPredicate> entityStruck;
   public static final MapCodec<LightningBoltPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "blocks_set_on_fire", MinMaxBounds.Ints.ANY)
                  .forGetter(LightningBoltPredicate::blocksSetOnFire),
               ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "entity_struck").forGetter(LightningBoltPredicate::entityStruck)
            )
            .apply(var0, LightningBoltPredicate::new)
   );

   public LightningBoltPredicate(MinMaxBounds.Ints var1, Optional<EntityPredicate> var2) {
      super();
      this.blocksSetOnFire = var1;
      this.entityStruck = var2;
   }

   public static LightningBoltPredicate blockSetOnFire(MinMaxBounds.Ints var0) {
      return new LightningBoltPredicate(var0, Optional.empty());
   }

   @Override
   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.LIGHTNING;
   }

   @Override
   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (!(var1 instanceof LightningBolt)) {
         return false;
      } else {
         LightningBolt var4 = (LightningBolt)var1;
         return this.blocksSetOnFire.matches(var4.getBlocksSetOnFire())
            && (this.entityStruck.isEmpty() || var4.getHitEntities().anyMatch(var3x -> this.entityStruck.get().matches(var2, var3, var3x)));
      }
   }
}
