package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class AttributeInstance {
   private final Attribute attribute;
   private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map<UUID, AttributeModifier> modifierById = new Object2ObjectArrayMap();
   private final Set<AttributeModifier> permanentModifiers = new ObjectArraySet();
   private double baseValue;
   private boolean dirty = true;
   private double cachedValue;
   private final Consumer<AttributeInstance> onDirty;

   public AttributeInstance(Attribute var1, Consumer<AttributeInstance> var2) {
      super();
      this.attribute = var1;
      this.onDirty = var2;
      this.baseValue = var1.getDefaultValue();
   }

   public Attribute getAttribute() {
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

   public Set<AttributeModifier> getModifiers(AttributeModifier.Operation var1) {
      return this.modifiersByOperation.computeIfAbsent(var1, var0 -> Sets.newHashSet());
   }

   public Set<AttributeModifier> getModifiers() {
      return ImmutableSet.copyOf(this.modifierById.values());
   }

   @Nullable
   public AttributeModifier getModifier(UUID var1) {
      return this.modifierById.get(var1);
   }

   public boolean hasModifier(AttributeModifier var1) {
      return this.modifierById.get(var1.getId()) != null;
   }

   private void addModifier(AttributeModifier var1) {
      AttributeModifier var2 = this.modifierById.putIfAbsent(var1.getId(), var1);
      if (var2 != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         this.getModifiers(var1.getOperation()).add(var1);
         this.setDirty();
      }
   }

   public void addTransientModifier(AttributeModifier var1) {
      this.addModifier(var1);
   }

   public void addPermanentModifier(AttributeModifier var1) {
      this.addModifier(var1);
      this.permanentModifiers.add(var1);
   }

   protected void setDirty() {
      this.dirty = true;
      this.onDirty.accept(this);
   }

   private void removeModifier(AttributeModifier var1) {
      this.getModifiers(var1.getOperation()).remove(var1);
      this.modifierById.remove(var1.getId());
      this.permanentModifiers.remove(var1);
      this.setDirty();
   }

   public void removeModifier(UUID var1) {
      AttributeModifier var2 = this.getModifier(var1);
      if (var2 != null) {
         this.removeModifier(var2);
      }
   }

   public boolean removePermanentModifier(UUID var1) {
      AttributeModifier var2 = this.getModifier(var1);
      if (var2 != null && this.permanentModifiers.contains(var2)) {
         this.removeModifier(var2);
         return true;
      } else {
         return false;
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

      for(AttributeModifier var4 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
         var1 += var4.getAmount();
      }

      double var7 = var1;

      for(AttributeModifier var6 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
         var7 += var1 * var6.getAmount();
      }

      for(AttributeModifier var9 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
         var7 *= 1.0 + var9.getAmount();
      }

      return this.attribute.sanitizeValue(var7);
   }

   private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation var1) {
      return this.modifiersByOperation.getOrDefault(var1, Collections.emptySet());
   }

   public void replaceFrom(AttributeInstance var1) {
      this.baseValue = var1.baseValue;
      this.modifierById.clear();
      this.modifierById.putAll(var1.modifierById);
      this.permanentModifiers.clear();
      this.permanentModifiers.addAll(var1.permanentModifiers);
      this.modifiersByOperation.clear();
      var1.modifiersByOperation.forEach((var1x, var2) -> this.getModifiers(var1x).addAll(var2));
      this.setDirty();
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
      var1.putDouble("Base", this.baseValue);
      if (!this.permanentModifiers.isEmpty()) {
         ListTag var2 = new ListTag();

         for(AttributeModifier var4 : this.permanentModifiers) {
            var2.add(var4.save());
         }

         var1.put("Modifiers", var2);
      }

      return var1;
   }

   public void load(CompoundTag var1) {
      this.baseValue = var1.getDouble("Base");
      if (var1.contains("Modifiers", 9)) {
         ListTag var2 = var1.getList("Modifiers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            AttributeModifier var4 = AttributeModifier.load(var2.getCompound(var3));
            if (var4 != null) {
               this.modifierById.put(var4.getId(), var4);
               this.getModifiers(var4.getOperation()).add(var4);
               this.permanentModifiers.add(var4);
            }
         }
      }

      this.setDirty();
   }
}
