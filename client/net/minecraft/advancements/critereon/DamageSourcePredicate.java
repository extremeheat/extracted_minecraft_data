package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

public record DamageSourcePredicate(List<TagPredicate<DamageType>> tags, Optional<EntityPredicate> directEntity, Optional<EntityPredicate> sourceEntity, Optional<Boolean> isDirect) {
   public static final Codec<DamageSourcePredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(TagPredicate.codec(Registries.DAMAGE_TYPE).listOf().optionalFieldOf("tags", List.of()).forGetter(DamageSourcePredicate::tags), EntityPredicate.CODEC.optionalFieldOf("direct_entity").forGetter(DamageSourcePredicate::directEntity), EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamageSourcePredicate::sourceEntity), Codec.BOOL.optionalFieldOf("is_direct").forGetter(DamageSourcePredicate::isDirect)).apply(var0, DamageSourcePredicate::new);
   });

   public DamageSourcePredicate(List<TagPredicate<DamageType>> var1, Optional<EntityPredicate> var2, Optional<EntityPredicate> var3, Optional<Boolean> var4) {
      super();
      this.tags = var1;
      this.directEntity = var2;
      this.sourceEntity = var3;
      this.isDirect = var4;
   }

   public boolean matches(ServerPlayer var1, DamageSource var2) {
      return this.matches(var1.serverLevel(), var1.position(), var2);
   }

   public boolean matches(ServerLevel var1, Vec3 var2, DamageSource var3) {
      Iterator var4 = this.tags.iterator();

      TagPredicate var5;
      do {
         if (!var4.hasNext()) {
            if (this.directEntity.isPresent() && !((EntityPredicate)this.directEntity.get()).matches(var1, var2, var3.getDirectEntity())) {
               return false;
            }

            if (this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).matches(var1, var2, var3.getEntity())) {
               return false;
            }

            if (this.isDirect.isPresent() && (Boolean)this.isDirect.get() != var3.isDirect()) {
               return false;
            }

            return true;
         }

         var5 = (TagPredicate)var4.next();
      } while(var5.matches(var3.typeHolder()));

      return false;
   }

   public List<TagPredicate<DamageType>> tags() {
      return this.tags;
   }

   public Optional<EntityPredicate> directEntity() {
      return this.directEntity;
   }

   public Optional<EntityPredicate> sourceEntity() {
      return this.sourceEntity;
   }

   public Optional<Boolean> isDirect() {
      return this.isDirect;
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

      public Builder tag(TagPredicate<DamageType> var1) {
         this.tags.add(var1);
         return this;
      }

      public Builder direct(EntityPredicate.Builder var1) {
         this.directEntity = Optional.of(var1.build());
         return this;
      }

      public Builder source(EntityPredicate.Builder var1) {
         this.sourceEntity = Optional.of(var1.build());
         return this;
      }

      public Builder isDirect(boolean var1) {
         this.isDirect = Optional.of(var1);
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.tags.build(), this.directEntity, this.sourceEntity, this.isDirect);
      }
   }
}
