package net.minecraft.world.attribute;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttributeSystem implements EnvironmentAttributeReader {
   private final EnvironmentAttributeMap dimensionAttributes;
   private final BiomeManager biomeManager;
   private final Set<EnvironmentAttribute<?>> attributesProvidedByBiomes;

   public EnvironmentAttributeSystem(Holder<DimensionType> var1, RegistryAccess var2, BiomeManager var3) {
      super();
      this.dimensionAttributes = ((DimensionType)var1.value()).attributes();
      this.biomeManager = var3;
      this.attributesProvidedByBiomes = (Set)var2.lookupOrThrow(Registries.BIOME).listElements().flatMap((var0) -> ((Biome)var0.value()).getAttributes().keySet().stream()).collect(Collectors.toCollection(ReferenceOpenHashSet::new));
   }

   public <Value> Value getDimensionValue(EnvironmentAttribute<Value> var1) {
      if (SharedConstants.IS_RUNNING_IN_IDE && var1.isPositional()) {
         throw new IllegalArgumentException("Position must always be provided for positional attribute " + String.valueOf(var1));
      } else {
         return (Value)this.getNotPositionalValue(var1);
      }
   }

   public <Value> Value getValue(EnvironmentAttribute<Value> var1, Vec3 var2, @Nullable SpatialAttributeInterpolator var3) {
      return (Value)(this.attributesProvidedByBiomes.contains(var1) ? this.getPositionalValue(var1, var2, var3) : this.getNotPositionalValue(var1));
   }

   private <Value> Value getPositionalValue(EnvironmentAttribute<Value> var1, Vec3 var2, @Nullable SpatialAttributeInterpolator var3) {
      Object var4 = var1.defaultValue();
      var4 = this.applyDimensionLayer(var1, var4);
      var4 = this.applyBiomeLayer(var1, var2, var3, var4);
      return (Value)var1.sanitizeValue(var4);
   }

   private <Value> Value getNotPositionalValue(EnvironmentAttribute<Value> var1) {
      Object var2 = var1.defaultValue();
      var2 = this.applyDimensionLayer(var1, var2);
      return (Value)var1.sanitizeValue(var2);
   }

   private <Value> Value applyDimensionLayer(EnvironmentAttribute<Value> var1, Value var2) {
      return (Value)this.dimensionAttributes.applyModifier(var1, var2);
   }

   private <Value> Value applyBiomeLayer(EnvironmentAttribute<Value> var1, Vec3 var2, @Nullable SpatialAttributeInterpolator var3, Value var4) {
      if (var3 != null && var1.isSpatiallyInterpolated()) {
         return (Value)var3.applyAttributeLayer(var1, var4);
      } else {
         Holder var5 = this.biomeManager.getNoiseBiomeAtPosition(var2.x, var2.y, var2.z);
         return (Value)((Biome)var5.value()).getAttributes().applyModifier(var1, var4);
      }
   }
}
