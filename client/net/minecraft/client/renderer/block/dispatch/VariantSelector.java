package net.minecraft.client.renderer.block.dispatch;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public class VariantSelector {
   private static final Splitter COMMA_SPLITTER = Splitter.on(',');
   private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);

   public VariantSelector() {
      super();
   }

   public static <O, S extends StateHolder<O, S>> Predicate<StateHolder<O, S>> predicate(final StateDefinition<O, S> stateDefinition, final String properties) {
      Map<Property<?>, Comparable<?>> map = new HashMap();

      for(String keyValue : COMMA_SPLITTER.split(properties)) {
         Iterator<String> iterator = EQUAL_SPLITTER.split(keyValue).iterator();
         if (iterator.hasNext()) {
            String propertyName = (String)iterator.next();
            Property<?> property = stateDefinition.getProperty(propertyName);
            if (property != null && iterator.hasNext()) {
               String propertyValue = (String)iterator.next();
               Comparable<?> value = getValueHelper(property, propertyValue);
               if (value == null) {
                  throw new RuntimeException("Unknown value: '" + propertyValue + "' for blockstate property: '" + propertyName + "' " + String.valueOf(property.getPossibleValues()));
               }

               map.put(property, value);
            } else if (!propertyName.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + propertyName + "'");
            }
         }
      }

      return (input) -> {
         for(Map.Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
            if (!Objects.equals(input.getValue((Property)entry.getKey()), entry.getValue())) {
               return false;
            }
         }

         return true;
      };
   }

   private static <T extends Comparable<T>> @Nullable T getValueHelper(final Property<T> property, final String next) {
      return (T)(property.getValue(next).orElse((Object)null));
   }
}
