package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;

public record FishingHookPredicate(Optional<Boolean> inOpenWater) implements EntitySubPredicate {
   public static final FishingHookPredicate ANY = new FishingHookPredicate(Optional.empty());
   public static final MapCodec<FishingHookPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.BOOL.optionalFieldOf("in_open_water").forGetter(FishingHookPredicate::inOpenWater)).apply(var0, FishingHookPredicate::new)
   );

   public FishingHookPredicate(Optional<Boolean> inOpenWater) {
      super();
      this.inOpenWater = inOpenWater;
   }

   public static FishingHookPredicate inOpenWater(boolean var0) {
      return new FishingHookPredicate(Optional.of(var0));
   }

   @Override
   public MapCodec<FishingHookPredicate> codec() {
      return EntitySubPredicates.FISHING_HOOK;
   }

   @Override
   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (this.inOpenWater.isEmpty()) {
         return true;
      } else {
         return var1 instanceof FishingHook var4 ? this.inOpenWater.get() == var4.isOpenWaterFishing() : false;
      }
   }
}
