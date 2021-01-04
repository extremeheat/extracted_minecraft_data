package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface StateHolder<C> {
   Logger LOGGER = LogManager.getLogger();

   <T extends Comparable<T>> T getValue(Property<T> var1);

   <T extends Comparable<T>, V extends T> C setValue(Property<T> var1, V var2);

   ImmutableMap<Property<?>, Comparable<?>> getValues();

   static <T extends Comparable<T>> String getName(Property<T> var0, Comparable<?> var1) {
      return var0.getName(var1);
   }

   static <S extends StateHolder<S>, T extends Comparable<T>> S setValueHelper(S var0, Property<T> var1, String var2, String var3, String var4) {
      Optional var5 = var1.getValue(var4);
      if (var5.isPresent()) {
         return (StateHolder)var0.setValue(var1, (Comparable)var5.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for input: {}", var2, var4, var3);
         return var0;
      }
   }
}
