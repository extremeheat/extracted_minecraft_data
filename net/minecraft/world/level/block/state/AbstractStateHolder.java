package net.minecraft.world.level.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AbstractStateHolder implements StateHolder {
   private static final Function PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function() {
      public String apply(@Nullable Entry var1) {
         if (var1 == null) {
            return "<NULL>";
         } else {
            Property var2 = (Property)var1.getKey();
            return var2.getName() + "=" + this.getName(var2, (Comparable)var1.getValue());
         }
      }

      private String getName(Property var1, Comparable var2) {
         return var1.getName(var2);
      }

      // $FF: synthetic method
      public Object apply(@Nullable Object var1) {
         return this.apply((Entry)var1);
      }
   };
   protected final Object owner;
   private final ImmutableMap values;
   private Table neighbours;

   protected AbstractStateHolder(Object var1, ImmutableMap var2) {
      this.owner = var1;
      this.values = var2;
   }

   public Object cycle(Property var1) {
      return this.setValue(var1, (Comparable)findNextInCollection(var1.getPossibleValues(), this.getValue(var1)));
   }

   protected static Object findNextInCollection(Collection var0, Object var1) {
      Iterator var2 = var0.iterator();

      do {
         if (!var2.hasNext()) {
            return var2.next();
         }
      } while(!var2.next().equals(var1));

      if (var2.hasNext()) {
         return var2.next();
      } else {
         return var0.iterator().next();
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.owner);
      if (!this.getValues().isEmpty()) {
         var1.append('[');
         var1.append((String)this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
         var1.append(']');
      }

      return var1.toString();
   }

   public Collection getProperties() {
      return Collections.unmodifiableCollection(this.values.keySet());
   }

   public boolean hasProperty(Property var1) {
      return this.values.containsKey(var1);
   }

   public Comparable getValue(Property var1) {
      Comparable var2 = (Comparable)this.values.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Cannot get property " + var1 + " as it does not exist in " + this.owner);
      } else {
         return (Comparable)var1.getValueClass().cast(var2);
      }
   }

   public Object setValue(Property var1, Comparable var2) {
      Comparable var3 = (Comparable)this.values.get(var1);
      if (var3 == null) {
         throw new IllegalArgumentException("Cannot set property " + var1 + " as it does not exist in " + this.owner);
      } else if (var3 == var2) {
         return this;
      } else {
         Object var4 = this.neighbours.get(var1, var2);
         if (var4 == null) {
            throw new IllegalArgumentException("Cannot set property " + var1 + " to " + var2 + " on " + this.owner + ", it is not an allowed value");
         } else {
            return var4;
         }
      }
   }

   public void populateNeighbours(Map var1) {
      if (this.neighbours != null) {
         throw new IllegalStateException();
      } else {
         HashBasedTable var2 = HashBasedTable.create();
         UnmodifiableIterator var3 = this.values.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            Property var5 = (Property)var4.getKey();
            Iterator var6 = var5.getPossibleValues().iterator();

            while(var6.hasNext()) {
               Comparable var7 = (Comparable)var6.next();
               if (var7 != var4.getValue()) {
                  var2.put(var5, var7, var1.get(this.makeNeighbourValues(var5, var7)));
               }
            }
         }

         this.neighbours = (Table)(var2.isEmpty() ? var2 : ArrayTable.create(var2));
      }
   }

   private Map makeNeighbourValues(Property var1, Comparable var2) {
      HashMap var3 = Maps.newHashMap(this.values);
      var3.put(var1, var2);
      return var3;
   }

   public ImmutableMap getValues() {
      return this.values;
   }
}
