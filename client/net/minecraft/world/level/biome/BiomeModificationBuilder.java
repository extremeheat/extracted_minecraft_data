package net.minecraft.world.level.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.Holder;

public class BiomeModificationBuilder {
   private final List<Predicate<Holder.Reference<Biome>>> conditions = new ArrayList();
   private final List<Consumer<BiomeBuilder>> modifiers = new ArrayList();

   public BiomeModificationBuilder() {
      super();
   }

   public BiomeModificationBuilder thatMatch(Predicate<Holder.Reference<Biome>> var1) {
      this.conditions.add(var1);
      return this;
   }

   public BiomeModificationBuilder modifyClimate(Consumer<ClimateSettings.Builder> var1) {
      this.modifiers.add((Consumer)(var1x) -> var1.accept(var1x.climateSettings()));
      return this;
   }

   public BiomeModificationBuilder modifySpecialEffects(Consumer<BiomeSpecialEffects.Builder> var1) {
      this.modifiers.add((Consumer)(var1x) -> var1.accept(var1x.specialEffects()));
      return this;
   }

   public BiomeModificationBuilder modifyMobSpawns(Consumer<MobSpawnSettings.Builder> var1) {
      this.modifiers.add((Consumer)(var1x) -> var1.accept(var1x.mobSpawnSettings()));
      return this;
   }

   public BiomeModificationBuilder modifyGenerationSettings(Consumer<BiomeGenerationSettings.PlainBuilder> var1) {
      this.modifiers.add((Consumer)(var1x) -> var1.accept(var1x.generationSettings()));
      return this;
   }

   public void apply(BiomeBuilder var1) {
      this.modifiers.forEach((var1x) -> var1x.accept(var1));
   }
}
