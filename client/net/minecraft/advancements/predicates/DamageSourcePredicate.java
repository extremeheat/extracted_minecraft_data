package net.minecraft.advancements.predicates;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

public record DamageSourcePredicate(List<TagPredicate<DamageType>> tags, Optional<EntityPredicate> directEntity, Optional<EntityPredicate> sourceEntity, Optional<Boolean> isDirect) {
   public static final Codec<DamageSourcePredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(TagPredicate.codec(Registries.DAMAGE_TYPE).listOf().optionalFieldOf("tags", List.of()).forGetter(DamageSourcePredicate::tags), EntityPredicate.CODEC.optionalFieldOf("direct_entity").forGetter(DamageSourcePredicate::directEntity), EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamageSourcePredicate::sourceEntity), Codec.BOOL.optionalFieldOf("is_direct").forGetter(DamageSourcePredicate::isDirect)).apply(i, DamageSourcePredicate::new));

   public DamageSourcePredicate {
      super();
   }

   public boolean matches(final ServerPlayer player, final DamageSource source) {
      return this.matches(player.level(), player.position(), source);
   }

   public boolean matches(final ServerLevel level, final Vec3 position, final DamageSource source) {
      for(TagPredicate<DamageType> tag : this.tags) {
         if (!tag.matches(source.typeHolder())) {
            return false;
         }
      }

      if (this.directEntity.isPresent() && !((EntityPredicate)this.directEntity.get()).matches(level, position, source.getDirectEntity())) {
         return false;
      } else if (this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).matches(level, position, source.getEntity())) {
         return false;
      } else if (this.isDirect.isPresent() && (Boolean)this.isDirect.get() != source.isDirect()) {
         return false;
      } else {
         return true;
      }
   }

   public static class Builder {
      private final ImmutableList.Builder<TagPredicate<DamageType>> tags = ImmutableList.builder();
      private Optional<EntityPredicate> directEntity = Optional.empty();
      private Optional<EntityPredicate> sourceEntity = Optional.empty();
      private Optional<Boolean> isDirect = Optional.empty();

      public Builder() {
         super();
      }

      public static Builder damageType() {
         return new Builder();
      }

      public Builder tag(final TagPredicate<DamageType> tag) {
         this.tags.add(tag);
         return this;
      }

      public Builder direct(final EntityPredicate.Builder directEntity) {
         this.directEntity = Optional.of(directEntity.build());
         return this;
      }

      public Builder source(final EntityPredicate.Builder sourceEntity) {
         this.sourceEntity = Optional.of(sourceEntity.build());
         return this;
      }

      public Builder isDirect(final boolean direct) {
         this.isDirect = Optional.of(direct);
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.tags.build(), this.directEntity, this.sourceEntity, this.isDirect);
      }
   }
}
