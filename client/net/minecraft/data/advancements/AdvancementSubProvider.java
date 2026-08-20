package net.minecraft.data.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.predicates.DamageSourcePredicate;
import net.minecraft.advancements.predicates.TagPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

public abstract class AdvancementSubProvider {
   protected final BootstrapContext<Advancement> output;
   protected final HolderGetter<DamageType> damageTypes;

   protected AdvancementSubProvider(final BootstrapContext<Advancement> output) {
      super();
      this.output = output;
      this.damageTypes = output.lookup(Registries.DAMAGE_TYPE);
   }

   public abstract void generate();

   protected DamageSourcePredicate.Builder isProjectile() {
      return DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(this.damageTypes, DamageTypeTags.IS_PROJECTILE));
   }

   @FunctionalInterface
   public interface Factory {
      AdvancementSubProvider create(BootstrapContext<Advancement> output);
   }
}
