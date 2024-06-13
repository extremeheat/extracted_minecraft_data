package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

public record DamageSourcePredicate(
   List<TagPredicate<DamageType>> tags, Optional<EntityPredicate> directEntity, Optional<EntityPredicate> sourceEntity, Optional<Boolean> isDirect
) {
   public static final Codec<DamageSourcePredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               TagPredicate.codec(Registries.DAMAGE_TYPE).listOf().optionalFieldOf("tags", List.of()).forGetter(DamageSourcePredicate::tags),
               EntityPredicate.CODEC.optionalFieldOf("direct_entity").forGetter(DamageSourcePredicate::directEntity),
               EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamageSourcePredicate::sourceEntity),
               Codec.BOOL.optionalFieldOf("is_direct").forGetter(DamageSourcePredicate::isDirect)
            )
            .apply(var0, DamageSourcePredicate::new)
   );

   public DamageSourcePredicate(
      List<TagPredicate<DamageType>> tags, Optional<EntityPredicate> directEntity, Optional<EntityPredicate> sourceEntity, Optional<Boolean> isDirect
   ) {
      super();
      this.tags = tags;
      this.directEntity = directEntity;
      this.sourceEntity = sourceEntity;
      this.isDirect = isDirect;
   }

   public boolean matches(ServerPlayer var1, DamageSource var2) {
      return this.matches(var1.serverLevel(), var1.position(), var2);
   }

   public boolean matches(ServerLevel var1, Vec3 var2, DamageSource var3) {
      for (TagPredicate var5 : this.tags) {
         if (!var5.matches(var3.typeHolder())) {
            return false;
         }
      }

      if (this.directEntity.isPresent() && !this.directEntity.get().matches(var1, var2, var3.getDirectEntity())) {
         return false;
      } else {
         return this.sourceEntity.isPresent() && !this.sourceEntity.get().matches(var1, var2, var3.getEntity())
            ? false
            : !this.isDirect.isPresent() || this.isDirect.get() == var3.isDirect();
      }
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableList.Builder<TagPredicate<DamageType>> tags = ImmutableList.builder();
      private Optional<EntityPredicate> directEntity = Optional.empty();
      private Optional<EntityPredicate> sourceEntity = Optional.empty();
      private Optional<Boolean> isDirect = Optional.empty();

      public Builder() {
         super();
      }

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder tag(TagPredicate<DamageType> var1) {
         this.tags.add(var1);
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder var1) {
         this.directEntity = Optional.of(var1.build());
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate.Builder var1) {
         this.sourceEntity = Optional.of(var1.build());
         return this;
      }

      public DamageSourcePredicate.Builder isDirect(boolean var1) {
         this.isDirect = Optional.of(var1);
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.tags.build(), this.directEntity, this.sourceEntity, this.isDirect);
      }
   }
}
