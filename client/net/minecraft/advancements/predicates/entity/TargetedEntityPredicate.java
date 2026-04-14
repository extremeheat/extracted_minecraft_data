package net.minecraft.advancements.predicates.entity;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record TargetedEntityPredicate(EntityPredicate targetedEntity) implements EntitySubPredicate {
   public static final Codec<TargetedEntityPredicate> CODEC;

   public TargetedEntityPredicate {
      super();
   }

   public boolean matches(final Entity entity, final ServerLevel level, final @Nullable Vec3 position) {
      EntityPredicate var10000 = this.targetedEntity;
      LivingEntity var10003;
      if (entity instanceof Mob mob) {
         var10003 = mob.getTarget();
      } else {
         var10003 = null;
      }

      return var10000.matches(level, position, var10003);
   }

   static {
      CODEC = EntityPredicate.CODEC.xmap(TargetedEntityPredicate::new, TargetedEntityPredicate::targetedEntity);
   }
}
