package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeMap {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<Attribute, AttributeInstance> attributes = Maps.newHashMap();
   private final Set<AttributeInstance> dirtyAttributes = Sets.newHashSet();
   private final AttributeSupplier supplier;

   public AttributeMap(AttributeSupplier var1) {
      super();
      this.supplier = var1;
   }

   private void onAttributeModified(AttributeInstance var1) {
      if (var1.getAttribute().isClientSyncable()) {
         this.dirtyAttributes.add(var1);
      }

   }

   public Set<AttributeInstance> getDirtyAttributes() {
      return this.dirtyAttributes;
   }

   public Collection<AttributeInstance> getSyncableAttributes() {
      return (Collection)this.attributes.values().stream().filter((var0) -> {
         return var0.getAttribute().isClientSyncable();
      }).collect(Collectors.toList());
   }

   @Nullable
   public AttributeInstance getInstance(Attribute var1) {
      return (AttributeInstance)this.attributes.computeIfAbsent(var1, (var1x) -> {
         return this.supplier.createInstance(this::onAttributeModified, var1x);
      });
   }

   public boolean hasAttribute(Attribute var1) {
      return this.attributes.get(var1) != null || this.supplier.hasAttribute(var1);
   }

   public boolean hasModifier(Attribute var1, UUID var2) {
      AttributeInstance var3 = (AttributeInstance)this.attributes.get(var1);
      return var3 != null ? var3.getModifier(var2) != null : this.supplier.hasModifier(var1, var2);
   }

   public double getValue(Attribute var1) {
      AttributeInstance var2 = (AttributeInstance)this.attributes.get(var1);
      return var2 != null ? var2.getValue() : this.supplier.getValue(var1);
   }

   public double getBaseValue(Attribute var1) {
      AttributeInstance var2 = (AttributeInstance)this.attributes.get(var1);
      return var2 != null ? var2.getBaseValue() : this.supplier.getBaseValue(var1);
   }

   public double getModifierValue(Attribute var1, UUID var2) {
      AttributeInstance var3 = (AttributeInstance)this.attributes.get(var1);
      return var3 != null ? var3.getModifier(var2).getAmount() : this.supplier.getModifierValue(var1, var2);
   }

   public void removeAttributeModifiers(Multimap<Attribute, AttributeModifier> var1) {
      var1.asMap().forEach((var1x, var2) -> {
         AttributeInstance var3 = (AttributeInstance)this.attributes.get(var1x);
         if (var3 != null) {
            var2.forEach(var3::removeModifier);
         }

      });
   }

   public void addTransientAttributeModifiers(Multimap<Attribute, AttributeModifier> var1) {
      var1.forEach((var1x, var2) -> {
         AttributeInstance var3 = this.getInstance(var1x);
         if (var3 != null) {
            var3.removeModifier(var2);
            var3.addTransientModifier(var2);
         }

      });
   }

   public void assignValues(AttributeMap var1) {
      var1.attributes.values().forEach((var1x) -> {
         AttributeInstance var2 = this.getInstance(var1x.getAttribute());
         if (var2 != null) {
            var2.replaceFrom(var1x);
         }

      });
   }

   public ListTag save() {
      ListTag var1 = new ListTag();
      Iterator var2 = this.attributes.values().iterator();

      while(var2.hasNext()) {
         AttributeInstance var3 = (AttributeInstance)var2.next();
         var1.add(var3.save());
      }

      return var1;
   }

   public void load(ListTag var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompound(var2);
         String var4 = var3.getString("Name");
         Util.ifElse(Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(var4)), (var2x) -> {
            AttributeInstance var3x = this.getInstance(var2x);
            if (var3x != null) {
               var3x.load(var3);
            }

         }, () -> {
            LOGGER.warn("Ignoring unknown attribute '{}'", var4);
         });
      }

   }
}
