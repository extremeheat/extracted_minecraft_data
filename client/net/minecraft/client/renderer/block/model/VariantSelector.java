package net.minecraft.client.renderer.block.model;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class VariantSelector {
   private static final Splitter COMMA_SPLITTER = Splitter.on(',');
   private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);

   public VariantSelector() {
      super();
   }

   public static <O, S extends StateHolder<O, S>> Predicate<StateHolder<O, S>> predicate(StateDefinition<O, S> var0, String var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = COMMA_SPLITTER.split(var1).iterator();

      while(true) {
         while(true) {
            Iterator var5;
            do {
               if (!var3.hasNext()) {
                  return (var1x) -> {
                     Iterator var2x = var2.entrySet().iterator();

                     Map.Entry var3;
                     do {
                        if (!var2x.hasNext()) {
                           return true;
                        }

                        var3 = (Map.Entry)var2x.next();
                     } while(Objects.equals(var1x.getValue((Property)var3.getKey()), var3.getValue()));

                     return false;
                  };
               }

               String var4 = (String)var3.next();
               var5 = EQUAL_SPLITTER.split(var4).iterator();
            } while(!var5.hasNext());

            String var6 = (String)var5.next();
            Property var7 = var0.getProperty(var6);
            if (var7 != null && var5.hasNext()) {
               String var8 = (String)var5.next();
               Comparable var9 = getValueHelper(var7, var8);
               if (var9 == null) {
                  throw new RuntimeException("Unknown value: '" + var8 + "' for blockstate property: '" + var6 + "' " + String.valueOf(var7.getPossibleValues()));
               }

               var2.put(var7, var9);
            } else if (!var6.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + var6 + "'");
            }
         }
      }
   }

   @Nullable
   private static <T extends Comparable<T>> T getValueHelper(Property<T> var0, String var1) {
      return (Comparable)var0.getValue(var1).orElse((Object)null);
   }
}
