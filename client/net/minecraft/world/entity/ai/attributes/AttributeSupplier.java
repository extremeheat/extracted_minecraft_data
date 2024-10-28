package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public class AttributeSupplier {
   private final Map<Holder<Attribute>, AttributeInstance> instances;

   AttributeSupplier(Map<Holder<Attribute>, AttributeInstance> var1) {
      super();
      this.instances = var1;
   }

   private AttributeInstance getAttributeInstance(Holder<Attribute> var1) {
      AttributeInstance var2 = (AttributeInstance)this.instances.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Can't find attribute " + var1.getRegisteredName());
      } else {
         return var2;
      }
   }

   public double getValue(Holder<Attribute> var1) {
      return this.getAttributeInstance(var1).getValue();
   }

   public double getBaseValue(Holder<Attribute> var1) {
      return this.getAttributeInstance(var1).getBaseValue();
   }

   public double getModifierValue(Holder<Attribute> var1, ResourceLocation var2) {
      AttributeModifier var3 = this.getAttributeInstance(var1).getModifier(var2);
      if (var3 == null) {
         String var10002 = String.valueOf(var2);
         throw new IllegalArgumentException("Can't find modifier " + var10002 + " on attribute " + var1.getRegisteredName());
      } else {
         return var3.amount();
      }
   }

   @Nullable
   public AttributeInstance createInstance(Consumer<AttributeInstance> var1, Holder<Attribute> var2) {
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

   public boolean hasAttribute(Holder<Attribute> var1) {
      return this.instances.containsKey(var1);
   }

   public boolean hasModifier(Holder<Attribute> var1, ResourceLocation var2) {
      AttributeInstance var3 = (AttributeInstance)this.instances.get(var1);
      return var3 != null && var3.getModifier(var2) != null;
   }

   public static class Builder {
      private final ImmutableMap.Builder<Holder<Attribute>, AttributeInstance> builder = ImmutableMap.builder();
      private boolean instanceFrozen;

      public Builder() {
         super();
      }

      private AttributeInstance create(Holder<Attribute> var1) {
         AttributeInstance var2 = new AttributeInstance(var1, (var2x) -> {
            if (this.instanceFrozen) {
               throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + var1.getRegisteredName());
            }
         });
         this.builder.put(var1, var2);
         return var2;
      }

      public Builder add(Holder<Attribute> var1) {
         this.create(var1);
         return this;
      }

      public Builder add(Holder<Attribute> var1, double var2) {
         AttributeInstance var4 = this.create(var1);
         var4.setBaseValue(var2);
         return this;
      }

      public AttributeSupplier build() {
         this.instanceFrozen = true;
         return new AttributeSupplier(this.builder.buildKeepingLast());
      }
   }
}
