package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface EntitySubPredicate {
   Codec<EntitySubPredicate> CODEC = BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE.byNameCodec().dispatch(EntitySubPredicate::codec, MapCodec::codec);

   MapCodec<? extends EntitySubPredicate> codec();

   boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3);
}