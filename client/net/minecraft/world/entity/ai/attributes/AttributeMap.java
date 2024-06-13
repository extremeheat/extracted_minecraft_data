package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
   private final Set<AttributeInstance> dirtyAttributes = new ObjectOpenHashSet();
   private final AttributeSupplier supplier;

   public AttributeMap(AttributeSupplier var1) {
      super();
      this.supplier = var1;
   }

   private void onAttributeModified(AttributeInstance var1) {
      if (var1.getAttribute().value().isClientSyncable()) {
         this.dirtyAttributes.add(var1);
      }
   }

   public Set<AttributeInstance> getDirtyAttributes() {
      return this.dirtyAttributes;
   }

   public Collection<AttributeInstance> getSyncableAttributes() {
      return this.attributes.values().stream().filter(var0 -> var0.getAttribute().value().isClientSyncable()).collect(Collectors.toList());
   }

   @Nullable
   public AttributeInstance getInstance(Holder<Attribute> var1) {
      return this.attributes.computeIfAbsent(var1, var1x -> this.supplier.createInstance(this::onAttributeModified, (Holder<Attribute>)var1x));
   }

   public boolean hasAttribute(Holder<Attribute> var1) {
      return this.attributes.get(var1) != null || this.supplier.hasAttribute(var1);
   }

   public boolean hasModifier(Holder<Attribute> var1, UUID var2) {
      AttributeInstance var3 = this.attributes.get(var1);
      return var3 != null ? var3.getModifier(var2) != null : this.supplier.hasModifier(var1, var2);
   }

   public double getValue(Holder<Attribute> var1) {
      AttributeInstance var2 = this.attributes.get(var1);
      return var2 != null ? var2.getValue() : this.supplier.getValue(var1);
   }

   public double getBaseValue(Holder<Attribute> var1) {
      AttributeInstance var2 = this.attributes.get(var1);
      return var2 != null ? var2.getBaseValue() : this.supplier.getBaseValue(var1);
   }

   public double getModifierValue(Holder<Attribute> var1, UUID var2) {
      AttributeInstance var3 = this.attributes.get(var1);
      return var3 != null ? var3.getModifier(var2).amount() : this.supplier.getModifierValue(var1, var2);
   }

   public void assignValues(AttributeMap var1) {
      var1.attributes.values().forEach(var1x -> {
         AttributeInstance var2 = this.getInstance(var1x.getAttribute());
         if (var2 != null) {
            var2.replaceFrom(var1x);
         }
      });
   }

   public ListTag save() {
      ListTag var1 = new ListTag();

      for (AttributeInstance var3 : this.attributes.values()) {
         var1.add(var3.save());
      }

      return var1;
   }

   public void load(ListTag var1) {
      for (int var2 = 0; var2 < var1.size(); var2++) {
         CompoundTag var3 = var1.getCompound(var2);
         String var4 = var3.getString("Name");
         ResourceLocation var5 = ResourceLocation.tryParse(var4);
         if (var5 != null) {
            Util.ifElse(BuiltInRegistries.ATTRIBUTE.getHolder(var5), var2x -> {
               AttributeInstance var3x = this.getInstance(var2x);
               if (var3x != null) {
                  var3x.load(var3);
               }
            }, () -> LOGGER.warn("Ignoring unknown attribute '{}'", var5));
         } else {
            LOGGER.warn("Ignoring malformed attribute '{}'", var4);
         }
      }
   }
}
