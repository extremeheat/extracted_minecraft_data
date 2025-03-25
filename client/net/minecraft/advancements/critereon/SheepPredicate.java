package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.phys.Vec3;

public record SheepPredicate(Optional<Boolean> sheared) implements EntitySubPredicate {
   public static final MapCodec<SheepPredicate> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.optionalFieldOf("sheared").forGetter(SheepPredicate::sheared)).apply(var0, SheepPredicate::new));

   public SheepPredicate(Optional<Boolean> var1) {
      super();
      this.sheared = var1;
   }

   public MapCodec<SheepPredicate> codec() {
      return EntitySubPredicates.SHEEP;
   }

   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (var1 instanceof Sheep var4) {
         return !this.sheared.isPresent() || var4.isSheared() == (Boolean)this.sheared.get();
      } else {
         return false;
      }
   }

   public static SheepPredicate hasWool() {
      return new SheepPredicate(Optional.of(false));
   }
}
