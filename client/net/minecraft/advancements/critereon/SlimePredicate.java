package net.minecraft.advancements.critereon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;

public record SlimePredicate(MinMaxBounds.Ints c) implements EntitySubPredicate {
   private final MinMaxBounds.Ints size;
   public static final MapCodec<SlimePredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "size", MinMaxBounds.Ints.ANY).forGetter(SlimePredicate::size))
            .apply(var0, SlimePredicate::new)
   );

   public SlimePredicate(MinMaxBounds.Ints var1) {
      super();
      this.size = var1;
   }

   public static SlimePredicate sized(MinMaxBounds.Ints var0) {
      return new SlimePredicate(var0);
   }

   @Override
   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      return var1 instanceof Slime var4 ? this.size.matches(var4.getSize()) : false;
   }

   @Override
   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.SLIME;
   }
}
