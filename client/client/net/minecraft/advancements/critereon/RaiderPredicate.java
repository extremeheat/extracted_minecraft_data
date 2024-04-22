package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;

public record RaiderPredicate(boolean hasRaid, boolean isCaptain) implements EntitySubPredicate {
   public static final MapCodec<RaiderPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Codec.BOOL.optionalFieldOf("has_raid", false).forGetter(RaiderPredicate::hasRaid),
               Codec.BOOL.optionalFieldOf("is_captain", false).forGetter(RaiderPredicate::isCaptain)
            )
            .apply(var0, RaiderPredicate::new)
   );
   public static final RaiderPredicate CAPTAIN_WITHOUT_RAID = new RaiderPredicate(false, true);

   public RaiderPredicate(boolean hasRaid, boolean isCaptain) {
      super();
      this.hasRaid = hasRaid;
      this.isCaptain = isCaptain;
   }

   @Override
   public MapCodec<RaiderPredicate> codec() {
      return EntitySubPredicates.RAIDER;
   }

   @Override
   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      return !(var1 instanceof Raider var4) ? false : var4.hasRaid() == this.hasRaid && var4.isCaptain() == this.isCaptain;
   }
}