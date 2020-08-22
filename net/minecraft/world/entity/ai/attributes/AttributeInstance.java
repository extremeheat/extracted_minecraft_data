package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public interface AttributeInstance {
   Attribute getAttribute();

   double getBaseValue();

   void setBaseValue(double var1);

   Set getModifiers(AttributeModifier.Operation var1);

   Set getModifiers();

   boolean hasModifier(AttributeModifier var1);

   @Nullable
   AttributeModifier getModifier(UUID var1);

   void addModifier(AttributeModifier var1);

   void removeModifier(AttributeModifier var1);

   void removeModifier(UUID var1);

   void removeModifiers();

   double getValue();

   default void copyFrom(AttributeInstance var1) {
      this.setBaseValue(var1.getBaseValue());
      Set var2 = var1.getModifiers();
      Set var3 = this.getModifiers();
      ImmutableSet var4 = ImmutableSet.copyOf(Sets.difference(var2, var3));
      ImmutableSet var5 = ImmutableSet.copyOf(Sets.difference(var3, var2));
      var4.forEach(this::addModifier);
      var5.forEach(this::removeModifier);
   }
}
