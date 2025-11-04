package net.minecraft.world.entity.ai.attributes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class AttributeInstance {
   private final Holder<Attribute> attribute;
   private final Map<AttributeModifier.Operation, Map<Identifier, AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map<Identifier, AttributeModifier> modifierById = new Object2ObjectArrayMap();
   private final Map<Identifier, AttributeModifier> permanentModifiers = new Object2ObjectArrayMap();
   private double baseValue;
   private boolean dirty = true;
   private double cachedValue;
   private final Consumer<AttributeInstance> onDirty;

   public AttributeInstance(Holder<Attribute> var1, Consumer<AttributeInstance> var2) {
      super();
      this.attribute = var1;
      this.onDirty = var2;
      this.baseValue = ((Attribute)var1.value()).getDefaultValue();
   }

   public Holder<Attribute> getAttribute() {
      return this.attribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double var1) {
      if (var1 != this.baseValue) {
         this.baseValue = var1;
         this.setDirty();
      }
   }

   @VisibleForTesting
   Map<Identifier, AttributeModifier> getModifiers(AttributeModifier.Operation var1) {
      return (Map)this.modifiersByOperation.computeIfAbsent(var1, (var0) -> new Object2ObjectOpenHashMap());
   }

   public Set<AttributeModifier> getModifiers() {
      return ImmutableSet.copyOf(this.modifierById.values());
   }

   public Set<AttributeModifier> getPermanentModifiers() {
      return ImmutableSet.copyOf(this.permanentModifiers.values());
   }

   public @Nullable AttributeModifier getModifier(Identifier var1) {
      return (AttributeModifier)this.modifierById.get(var1);
   }

   public boolean hasModifier(Identifier var1) {
      return this.modifierById.get(var1) != null;
   }

   private void addModifier(AttributeModifier var1) {
      AttributeModifier var2 = (AttributeModifier)this.modifierById.putIfAbsent(var1.id(), var1);
      if (var2 != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         this.getModifiers(var1.operation()).put(var1.id(), var1);
         this.setDirty();
      }
   }

   public void addOrUpdateTransientModifier(AttributeModifier var1) {
      AttributeModifier var2 = (AttributeModifier)this.modifierById.put(var1.id(), var1);
      if (var1 != var2) {
         this.getModifiers(var1.operation()).put(var1.id(), var1);
         this.setDirty();
      }
   }

   public void addTransientModifier(AttributeModifier var1) {
      this.addModifier(var1);
   }

   public void addOrReplacePermanentModifier(AttributeModifier var1) {
      this.removeModifier(var1.id());
      this.addModifier(var1);
      this.permanentModifiers.put(var1.id(), var1);
   }

   public void addPermanentModifier(AttributeModifier var1) {
      this.addModifier(var1);
      this.permanentModifiers.put(var1.id(), var1);
   }

   public void addPermanentModifiers(Collection<AttributeModifier> var1) {
      for(AttributeModifier var3 : var1) {
         this.addPermanentModifier(var3);
      }

   }

   protected void setDirty() {
      this.dirty = true;
      this.onDirty.accept(this);
   }

   public void removeModifier(AttributeModifier var1) {
      this.removeModifier(var1.id());
   }

   public boolean removeModifier(Identifier var1) {
      AttributeModifier var2 = (AttributeModifier)this.modifierById.remove(var1);
      if (var2 == null) {
         return false;
      } else {
         this.getModifiers(var2.operation()).remove(var1);
         this.permanentModifiers.remove(var1);
         this.setDirty();
         return true;
      }
   }

   public void removeModifiers() {
      for(AttributeModifier var2 : this.getModifiers()) {
         this.removeModifier(var2);
      }

   }

   public double getValue() {
      if (this.dirty) {
         this.cachedValue = this.calculateValue();
         this.dirty = false;
      }

      return this.cachedValue;
   }

   private double calculateValue() {
      double var1 = this.getBaseValue();

      for(AttributeModifier var4 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_VALUE)) {
         var1 += var4.amount();
      }

      double var7 = var1;

      for(AttributeModifier var6 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_MULTIPLIED_BASE)) {
         var7 += var1 * var6.amount();
      }

      for(AttributeModifier var9 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) {
         var7 *= 1.0 + var9.amount();
      }

      return ((Attribute)this.attribute.value()).sanitizeValue(var7);
   }

   private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation var1) {
      return ((Map)this.modifiersByOperation.getOrDefault(var1, Map.of())).values();
   }

   public void replaceFrom(AttributeInstance var1) {
      this.baseValue = var1.baseValue;
      this.modifierById.clear();
      this.modifierById.putAll(var1.modifierById);
      this.permanentModifiers.clear();
      this.permanentModifiers.putAll(var1.permanentModifiers);
      this.modifiersByOperation.clear();
      var1.modifiersByOperation.forEach((var1x, var2) -> this.getModifiers(var1x).putAll(var2));
      this.setDirty();
   }

   public Packed pack() {
      return new Packed(this.attribute, this.baseValue, List.copyOf(this.permanentModifiers.values()));
   }

   public void apply(Packed var1) {
      this.baseValue = var1.baseValue;

      for(AttributeModifier var3 : var1.modifiers) {
         this.modifierById.put(var3.id(), var3);
         this.getModifiers(var3.operation()).put(var3.id(), var3);
         this.permanentModifiers.put(var3.id(), var3);
      }

      this.setDirty();
   }

   public static record Packed(Holder<Attribute> attribute, double baseValue, List<AttributeModifier> modifiers) {
      final double baseValue;
      final List<AttributeModifier> modifiers;
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((var0) -> var0.group(BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("id").forGetter(Packed::attribute), Codec.DOUBLE.fieldOf("base").orElse(0.0).forGetter(Packed::baseValue), AttributeModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(Packed::modifiers)).apply(var0, Packed::new));
      public static final Codec<List<Packed>> LIST_CODEC;

      public Packed(Holder<Attribute> var1, double var2, List<AttributeModifier> var4) {
         super();
         this.attribute = var1;
         this.baseValue = var2;
         this.modifiers = var4;
      }

      static {
         LIST_CODEC = CODEC.listOf();
      }
   }
}
