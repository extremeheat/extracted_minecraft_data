package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class ModifiableAttributeInstance implements AttributeInstance {
   private final BaseAttributeMap attributeMap;
   private final Attribute attribute;
   private final Map modifiers = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map modifiersByName = Maps.newHashMap();
   private final Map modifierById = Maps.newHashMap();
   private double baseValue;
   private boolean dirty = true;
   private double cachedValue;

   public ModifiableAttributeInstance(BaseAttributeMap var1, Attribute var2) {
      this.attributeMap = var1;
      this.attribute = var2;
      this.baseValue = var2.getDefaultValue();
      AttributeModifier.Operation[] var3 = AttributeModifier.Operation.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         AttributeModifier.Operation var6 = var3[var5];
         this.modifiers.put(var6, Sets.newHashSet());
      }

   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double var1) {
      if (var1 != this.getBaseValue()) {
         this.baseValue = var1;
         this.setDirty();
      }
   }

   public Set getModifiers(AttributeModifier.Operation var1) {
      return (Set)this.modifiers.get(var1);
   }

   public Set getModifiers() {
      HashSet var1 = Sets.newHashSet();
      AttributeModifier.Operation[] var2 = AttributeModifier.Operation.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AttributeModifier.Operation var5 = var2[var4];
         var1.addAll(this.getModifiers(var5));
      }

      return var1;
   }

   @Nullable
   public AttributeModifier getModifier(UUID var1) {
      return (AttributeModifier)this.modifierById.get(var1);
   }

   public boolean hasModifier(AttributeModifier var1) {
      return this.modifierById.get(var1.getId()) != null;
   }

   public void addModifier(AttributeModifier var1) {
      if (this.getModifier(var1.getId()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Set var2 = (Set)this.modifiersByName.computeIfAbsent(var1.getName(), (var0) -> {
            return Sets.newHashSet();
         });
         ((Set)this.modifiers.get(var1.getOperation())).add(var1);
         var2.add(var1);
         this.modifierById.put(var1.getId(), var1);
         this.setDirty();
      }
   }

   protected void setDirty() {
      this.dirty = true;
      this.attributeMap.onAttributeModified(this);
   }

   public void removeModifier(AttributeModifier var1) {
      AttributeModifier.Operation[] var2 = AttributeModifier.Operation.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AttributeModifier.Operation var5 = var2[var4];
         ((Set)this.modifiers.get(var5)).remove(var1);
      }

      Set var6 = (Set)this.modifiersByName.get(var1.getName());
      if (var6 != null) {
         var6.remove(var1);
         if (var6.isEmpty()) {
            this.modifiersByName.remove(var1.getName());
         }
      }

      this.modifierById.remove(var1.getId());
      this.setDirty();
   }

   public void removeModifier(UUID var1) {
      AttributeModifier var2 = this.getModifier(var1);
      if (var2 != null) {
         this.removeModifier(var2);
      }

   }

   public void removeModifiers() {
      Set var1 = this.getModifiers();
      if (var1 != null) {
         ArrayList var4 = Lists.newArrayList(var1);
         Iterator var2 = var4.iterator();

         while(var2.hasNext()) {
            AttributeModifier var3 = (AttributeModifier)var2.next();
            this.removeModifier(var3);
         }

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

      AttributeModifier var4;
      for(Iterator var3 = this.getAppliedModifiers(AttributeModifier.Operation.ADDITION).iterator(); var3.hasNext(); var1 += var4.getAmount()) {
         var4 = (AttributeModifier)var3.next();
      }

      double var7 = var1;

      Iterator var5;
      AttributeModifier var6;
      for(var5 = this.getAppliedModifiers(AttributeModifier.Operation.MULTIPLY_BASE).iterator(); var5.hasNext(); var7 += var1 * var6.getAmount()) {
         var6 = (AttributeModifier)var5.next();
      }

      for(var5 = this.getAppliedModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL).iterator(); var5.hasNext(); var7 *= 1.0D + var6.getAmount()) {
         var6 = (AttributeModifier)var5.next();
      }

      return this.attribute.sanitizeValue(var7);
   }

   private Collection getAppliedModifiers(AttributeModifier.Operation var1) {
      HashSet var2 = Sets.newHashSet(this.getModifiers(var1));

      for(Attribute var3 = this.attribute.getParentAttribute(); var3 != null; var3 = var3.getParentAttribute()) {
         AttributeInstance var4 = this.attributeMap.getInstance(var3);
         if (var4 != null) {
            var2.addAll(var4.getModifiers(var1));
         }
      }

      return var2;
   }
}
