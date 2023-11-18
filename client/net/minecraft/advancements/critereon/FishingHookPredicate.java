package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;

public record FishingHookPredicate(Optional<Boolean> d) implements EntitySubPredicate {
   private final Optional<Boolean> inOpenWater;
   public static final FishingHookPredicate ANY = new FishingHookPredicate(Optional.empty());
   public static final MapCodec<FishingHookPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(ExtraCodecs.strictOptionalField(Codec.BOOL, "in_open_water").forGetter(FishingHookPredicate::inOpenWater))
            .apply(var0, FishingHookPredicate::new)
   );

   public FishingHookPredicate(Optional<Boolean> var1) {
      super();
      this.inOpenWater = var1;
   }

   public static FishingHookPredicate inOpenWater(boolean var0) {
      return new FishingHookPredicate(Optional.of(var0));
   }

   @Override
   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.FISHING_HOOK;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (this.inOpenWater.isEmpty()) {
         return true;
      } else if (var1 instanceof FishingHook var4) {
         return this.inOpenWater.get() == var4.isOpenWaterFishing();
      } else {
         return false;
      }
   }
}
