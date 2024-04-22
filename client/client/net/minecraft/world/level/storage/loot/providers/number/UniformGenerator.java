package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record UniformGenerator(NumberProvider min, NumberProvider max) implements NumberProvider {
   public static final MapCodec<UniformGenerator> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               NumberProviders.CODEC.fieldOf("min").forGetter(UniformGenerator::min), NumberProviders.CODEC.fieldOf("max").forGetter(UniformGenerator::max)
            )
            .apply(var0, UniformGenerator::new)
   );

   public UniformGenerator(NumberProvider min, NumberProvider max) {
      super();
      this.min = min;
      this.max = max;
   }

   @Override
   public LootNumberProviderType getType() {
      return NumberProviders.UNIFORM;
   }

   public static UniformGenerator between(float var0, float var1) {
      return new UniformGenerator(ConstantValue.exactly(var0), ConstantValue.exactly(var1));
   }

   @Override
   public int getInt(LootContext var1) {
      return Mth.nextInt(var1.getRandom(), this.min.getInt(var1), this.max.getInt(var1));
   }

   @Override
   public float getFloat(LootContext var1) {
      return Mth.nextFloat(var1.getRandom(), this.min.getFloat(var1), this.max.getFloat(var1));
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.min.getReferencedContextParams(), this.max.getReferencedContextParams());
   }
}