package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record PotatoPredicate(boolean c) implements EntitySubPredicate {
   private final boolean isPotato;
   public static final MapCodec<PotatoPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.BOOL.fieldOf("is_potato").forGetter(PotatoPredicate::isPotato)).apply(var0, PotatoPredicate::new)
   );

   public PotatoPredicate(boolean var1) {
      super();
      this.isPotato = var1;
   }

   @Override
   public MapCodec<PotatoPredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      return var1.isPotato() == this.isPotato;
   }
}
