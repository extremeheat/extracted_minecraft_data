package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record RaiderPredicate(boolean hasRaid, boolean isCaptain) implements EntitySubPredicate {
   public static final MapCodec<RaiderPredicate> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.optionalFieldOf("has_raid", false).forGetter(RaiderPredicate::hasRaid), Codec.BOOL.optionalFieldOf("is_captain", false).forGetter(RaiderPredicate::isCaptain)).apply(i, RaiderPredicate::new));
   public static final RaiderPredicate CAPTAIN_WITHOUT_RAID = new RaiderPredicate(false, true);

   public RaiderPredicate {
      super();
   }

   public MapCodec<RaiderPredicate> codec() {
      return EntitySubPredicates.RAIDER;
   }

   public boolean matches(final Entity entity, final ServerLevel level, final @Nullable Vec3 position) {
      if (!(entity instanceof Raider raider)) {
         return false;
      } else {
         return raider.hasRaid() == this.hasRaid && raider.isCaptain() == this.isCaptain;
      }
   }
}
