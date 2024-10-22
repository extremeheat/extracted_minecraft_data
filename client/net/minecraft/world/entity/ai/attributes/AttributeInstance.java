package net.minecraft.world.entity.ai.attributes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class AttributeInstance {
   private static final String BASE_FIELD = "base";
   private static final String MODIFIERS_FIELD = "modifiers";
   public static final String ID_FIELD = "id";
   private final Holder<Attribute> attribute;
   private final Map<AttributeModifier.Operation, Map<ResourceLocation, AttributeModifier>> modifiersByOperation = Maps.newEnumMap(
      AttributeModifier.Operation.class
   );
   private final Map<ResourceLocation, AttributeModifier> modifierById = new Object2ObjectArrayMap();
   private final Map<ResourceLocation, AttributeModifier> permanentModifiers = new Object2ObjectArrayMap();
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
   Map<ResourceLocation, AttributeModifier> getModifiers(AttributeModifier.Operation var1) {
      return this.modifiersByOperation.computeIfAbsent(var1, var0 -> new Object2ObjectOpenHashMap());
   }

   public Set<AttributeModifier> getModifiers() {
      return ImmutableSet.copyOf(this.modifierById.values());
   }

   public Set<AttributeModifier> getPermanentModifiers() {
      return ImmutableSet.copyOf(this.permanentModifiers.values());
   }

   @Nullable
   public AttributeModifier getModifier(ResourceLocation var1) {
      return this.modifierById.get(var1);
   }

   public boolean hasModifier(ResourceLocation var1) {
      return this.modifierById.get(var1) != null;
   }

   private void addModifier(AttributeModifier var1) {
      AttributeModifier var2 = this.modifierById.putIfAbsent(var1.id(), var1);
      if (var2 != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         this.getModifiers(var1.operation()).put(var1.id(), var1);
         this.setDirty();
      }
   }

   public void addOrUpdateTransientModifier(AttributeModifier var1) {
      AttributeModifier var2 = this.modifierById.put(var1.id(), var1);
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
      for (AttributeModifier var3 : var1) {
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

   public boolean removeModifier(ResourceLocation var1) {
      AttributeModifier var2 = this.modifierById.remove(var1);
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
      for (AttributeModifier var2 : this.getModifiers()) {
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

      for (AttributeModifier var4 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_VALUE)) {
         var1 += var4.amount();
      }

      double var7 = var1;

      for (AttributeModifier var6 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_MULTIPLIED_BASE)) {
         var7 += var1 * var6.amount();
      }

      for (AttributeModifier var9 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) {
         var7 *= 1.0 + var9.amount();
      }

      return this.attribute.value().sanitizeValue(var7);
   }

   private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation var1) {
      return this.modifiersByOperation.getOrDefault(var1, Map.of()).values();
   }

   public void replaceFrom(AttributeInstance var1) {
      this.baseValue = var1.baseValue;
      this.modifierById.clear();
      this.modifierById.putAll(var1.modifierById);
      this.permanentModifiers.clear();
      this.permanentModifiers.putAll(var1.permanentModifiers);
      this.modifiersByOperation.clear();
      var1.modifiersByOperation.forEach((var1x, var2) -> this.getModifiers(var1x).putAll((Map<? extends ResourceLocation, ? extends AttributeModifier>)var2));
      this.setDirty();
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      ResourceKey var2 = this.attribute.unwrapKey().orElseThrow(() -> new IllegalStateException("Tried to serialize unregistered attribute"));
      var1.putString("id", var2.location().toString());
      var1.putDouble("base", this.baseValue);
      if (!this.permanentModifiers.isEmpty()) {
         ListTag var3 = new ListTag();

         for (AttributeModifier var5 : this.permanentModifiers.values()) {
            var3.add(var5.save());
         }

         var1.put("modifiers", var3);
      }

      return var1;
   }

   public void load(CompoundTag var1) {
      this.baseValue = var1.getDouble("base");
      if (var1.contains("modifiers", 9)) {
         ListTag var2 = var1.getList("modifiers", 10);

         for (int var3 = 0; var3 < var2.size(); var3++) {
            AttributeModifier var4 = AttributeModifier.load(var2.getCompound(var3));
            if (var4 != null) {
               this.modifierById.put(var4.id(), var4);
               this.getModifiers(var4.operation()).put(var4.id(), var4);
               this.permanentModifiers.put(var4.id(), var4);
            }
         }
      }

      this.setDirty();
   }
}
