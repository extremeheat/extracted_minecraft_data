package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public class AttributeMap {
   private final Map<Holder<Attribute>, AttributeInstance> attributes = new Object2ObjectOpenHashMap();
   private final Set<AttributeInstance> attributesToSync = new ObjectOpenHashSet();
   private final Set<AttributeInstance> attributesToUpdate = new ObjectOpenHashSet();
   private final AttributeSupplier supplier;

   public AttributeMap(AttributeSupplier var1) {
      super();
      this.supplier = var1;
   }

   private void onAttributeModified(AttributeInstance var1) {
      this.attributesToUpdate.add(var1);
      if (((Attribute)var1.getAttribute().value()).isClientSyncable()) {
         this.attributesToSync.add(var1);
      }

   }

   public Set<AttributeInstance> getAttributesToSync() {
      return this.attributesToSync;
   }

   public Set<AttributeInstance> getAttributesToUpdate() {
      return this.attributesToUpdate;
   }

   public Collection<AttributeInstance> getSyncableAttributes() {
      return (Collection)this.attributes.values().stream().filter((var0) -> ((Attribute)var0.getAttribute().value()).isClientSyncable()).collect(Collectors.toList());
   }

   @Nullable
   public AttributeInstance getInstance(Holder<Attribute> var1) {
      return (AttributeInstance)this.attributes.computeIfAbsent(var1, (var1x) -> this.supplier.createInstance(this::onAttributeModified, var1x));
   }

   public boolean hasAttribute(Holder<Attribute> var1) {
      return this.attributes.get(var1) != null || this.supplier.hasAttribute(var1);
   }

   public boolean hasModifier(Holder<Attribute> var1, ResourceLocation var2) {
      AttributeInstance var3 = (AttributeInstance)this.attributes.get(var1);
      return var3 != null ? var3.getModifier(var2) != null : this.supplier.hasModifier(var1, var2);
   }

   public double getValue(Holder<Attribute> var1) {
      AttributeInstance var2 = (AttributeInstance)this.attributes.get(var1);
      return var2 != null ? var2.getValue() : this.supplier.getValue(var1);
   }

   public double getBaseValue(Holder<Attribute> var1) {
      AttributeInstance var2 = (AttributeInstance)this.attributes.get(var1);
      return var2 != null ? var2.getBaseValue() : this.supplier.getBaseValue(var1);
   }

   public double getModifierValue(Holder<Attribute> var1, ResourceLocation var2) {
      AttributeInstance var3 = (AttributeInstance)this.attributes.get(var1);
      return var3 != null ? var3.getModifier(var2).amount() : this.supplier.getModifierValue(var1, var2);
   }

   public void addTransientAttributeModifiers(Multimap<Holder<Attribute>, AttributeModifier> var1) {
      var1.forEach((var1x, var2) -> {
         AttributeInstance var3 = this.getInstance(var1x);
         if (var3 != null) {
            var3.removeModifier(var2.id());
            var3.addTransientModifier(var2);
         }

      });
   }

   public void removeAttributeModifiers(Multimap<Holder<Attribute>, AttributeModifier> var1) {
      var1.asMap().forEach((var1x, var2) -> {
         AttributeInstance var3 = (AttributeInstance)this.attributes.get(var1x);
         if (var3 != null) {
            var2.forEach((var1) -> var3.removeModifier(var1.id()));
         }

      });
   }

   public void assignAllValues(AttributeMap var1) {
      var1.attributes.values().forEach((var1x) -> {
         AttributeInstance var2 = this.getInstance(var1x.getAttribute());
         if (var2 != null) {
            var2.replaceFrom(var1x);
         }

      });
   }

   public void assignBaseValues(AttributeMap var1) {
      var1.attributes.values().forEach((var1x) -> {
         AttributeInstance var2 = this.getInstance(var1x.getAttribute());
         if (var2 != null) {
            var2.setBaseValue(var1x.getBaseValue());
         }

      });
   }

   public void assignPermanentModifiers(AttributeMap var1) {
      var1.attributes.values().forEach((var1x) -> {
         AttributeInstance var2 = this.getInstance(var1x.getAttribute());
         if (var2 != null) {
            var2.addPermanentModifiers(var1x.getPermanentModifiers());
         }

      });
   }

   public boolean resetBaseValue(Holder<Attribute> var1) {
      if (!this.supplier.hasAttribute(var1)) {
         return false;
      } else {
         AttributeInstance var2 = (AttributeInstance)this.attributes.get(var1);
         if (var2 != null) {
            var2.setBaseValue(this.supplier.getBaseValue(var1));
         }

         return true;
      }
   }

   public List<AttributeInstance.Packed> pack() {
      ArrayList var1 = new ArrayList(this.attributes.values().size());

      for(AttributeInstance var3 : this.attributes.values()) {
         var1.add(var3.pack());
      }

      return var1;
   }

   public void apply(List<AttributeInstance.Packed> var1) {
      for(AttributeInstance.Packed var3 : var1) {
         AttributeInstance var4 = this.getInstance(var3.attribute());
         if (var4 != null) {
            var4.apply(var3);
         }
      }

   }
}
