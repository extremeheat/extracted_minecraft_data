package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class AttributeSupplier {
   private final Map<Holder<Attribute>, AttributeInstance> instances;

   private AttributeSupplier(final Map<Holder<Attribute>, AttributeInstance> instances) {
      super();
      this.instances = instances;
   }

   private AttributeInstance getAttributeInstance(final Holder<Attribute> attribute) {
      AttributeInstance instance = (AttributeInstance)this.instances.get(attribute);
      if (instance == null) {
         throw new IllegalArgumentException("Can't find attribute " + attribute.getRegisteredName());
      } else {
         return instance;
      }
   }

   public double getValue(final Holder<Attribute> attribute) {
      return this.getAttributeInstance(attribute).getValue();
   }

   public double getBaseValue(final Holder<Attribute> attribute) {
      return this.getAttributeInstance(attribute).getBaseValue();
   }

   public double getModifierValue(final Holder<Attribute> attribute, final Identifier id) {
      AttributeModifier modifier = this.getAttributeInstance(attribute).getModifier(id);
      if (modifier == null) {
         String var10002 = String.valueOf(id);
         throw new IllegalArgumentException("Can't find modifier " + var10002 + " on attribute " + attribute.getRegisteredName());
      } else {
         return modifier.amount();
      }
   }

   public @Nullable AttributeInstance createInstance(final Consumer<AttributeInstance> onDirty, final Holder<Attribute> attribute) {
      AttributeInstance template = (AttributeInstance)this.instances.get(attribute);
      if (template == null) {
         return null;
      } else {
         AttributeInstance result = new AttributeInstance(attribute, onDirty);
         result.replaceFrom(template);
         return result;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public boolean hasAttribute(final Holder<Attribute> attribute) {
      return this.instances.containsKey(attribute);
   }

   public boolean hasModifier(final Holder<Attribute> attribute, final Identifier modifier) {
      AttributeInstance attributeInstance = (AttributeInstance)this.instances.get(attribute);
      return attributeInstance != null && attributeInstance.getModifier(modifier) != null;
   }

   public static class Builder {
      private final ImmutableMap.Builder<Holder<Attribute>, AttributeInstance> builder = ImmutableMap.builder();
      private boolean instanceFrozen;

      public Builder() {
         super();
      }

      private AttributeInstance create(final Holder<Attribute> attribute) {
         AttributeInstance result = new AttributeInstance(attribute, (attributeInstance) -> {
            if (this.instanceFrozen) {
               throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + attribute.getRegisteredName());
            }
         });
         this.builder.put(attribute, result);
         return result;
      }

      public Builder add(final Holder<Attribute> attribute) {
         this.create(attribute);
         return this;
      }

      public Builder add(final Holder<Attribute> attribute, final double baseValue) {
         AttributeInstance result = this.create(attribute);
         result.setBaseValue(baseValue);
         return this;
      }

      public AttributeSupplier build() {
         this.instanceFrozen = true;
         return new AttributeSupplier(this.builder.buildKeepingLast());
      }
   }
}
