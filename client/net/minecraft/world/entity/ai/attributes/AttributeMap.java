package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AttributeMap {
   private static final Logger LOGGER = LogUtils.getLogger();
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
      return (Collection)this.attributes.values().stream().filter((var0) -> {
         return ((Attribute)var0.getAttribute().value()).isClientSyncable();
      }).collect(Collectors.toList());
   }

   @Nullable
   public AttributeInstance getInstance(Holder<Attribute> var1) {
      return (AttributeInstance)this.attributes.computeIfAbsent(var1, (var1x) -> {
         return this.supplier.createInstance(this::onAttributeModified, var1x);
      });
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
            var2.forEach((var1) -> {
               var3.removeModifier(var1.id());
            });
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
         String var4 = var3.getString("id");
         ResourceLocation var5 = ResourceLocation.tryParse(var4);
         if (var5 != null) {
            Util.ifElse(BuiltInRegistries.ATTRIBUTE.getHolder(var5), (var2x) -> {
               AttributeInstance var3x = this.getInstance(var2x);
               if (var3x != null) {
                  var3x.load(var3);
               }

            }, () -> {
               LOGGER.warn("Ignoring unknown attribute '{}'", var5);
            });
         } else {
            LOGGER.warn("Ignoring malformed attribute '{}'", var4);
         }
      }

   }
}
