package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;

public class AttributeSupplier {
   private final Map<Attribute, AttributeInstance> instances;

   public AttributeSupplier(Map<Attribute, AttributeInstance> var1) {
      super();
      this.instances = ImmutableMap.copyOf(var1);
   }

   private AttributeInstance getAttributeInstance(Attribute var1) {
      AttributeInstance var2 = (AttributeInstance)this.instances.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Can't find attribute " + Registry.ATTRIBUTE.getKey(var1));
      } else {
         return var2;
      }
   }

   public double getValue(Attribute var1) {
      return this.getAttributeInstance(var1).getValue();
   }

   public double getBaseValue(Attribute var1) {
      return this.getAttributeInstance(var1).getBaseValue();
   }

   public double getModifierValue(Attribute var1, UUID var2) {
      AttributeModifier var3 = this.getAttributeInstance(var1).getModifier(var2);
      if (var3 == null) {
         throw new IllegalArgumentException("Can't find modifier " + var2 + " on attribute " + Registry.ATTRIBUTE.getKey(var1));
      } else {
         return var3.getAmount();
      }
   }

   @Nullable
   public AttributeInstance createInstance(Consumer<AttributeInstance> var1, Attribute var2) {
      AttributeInstance var3 = (AttributeInstance)this.instances.get(var2);
      if (var3 == null) {
         return null;
      } else {
         AttributeInstance var4 = new AttributeInstance(var2, var1);
         var4.replaceFrom(var3);
         return var4;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public boolean hasAttribute(Attribute var1) {
      return this.instances.containsKey(var1);
   }

   public boolean hasModifier(Attribute var1, UUID var2) {
      AttributeInstance var3 = (AttributeInstance)this.instances.get(var1);
      return var3 != null && var3.getModifier(var2) != null;
   }

   public static class Builder {
      private final Map<Attribute, AttributeInstance> builder = Maps.newHashMap();
      private boolean instanceFrozen;

      public Builder() {
         super();
      }

      private AttributeInstance create(Attribute var1) {
         AttributeInstance var2 = new AttributeInstance(var1, (var2x) -> {
            if (this.instanceFrozen) {
               throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getKey(var1));
            }
         });
         this.builder.put(var1, var2);
         return var2;
      }

      public Builder add(Attribute var1) {
         this.create(var1);
         return this;
      }

      public Builder add(Attribute var1, double var2) {
         AttributeInstance var4 = this.create(var1);
         var4.setBaseValue(var2);
         return this;
      }

      public AttributeSupplier build() {
         this.instanceFrozen = true;
         return new AttributeSupplier(this.builder);
      }
   }
}
