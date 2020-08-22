package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.MapFiller;
import net.minecraft.world.level.block.state.properties.Property;

public class StateDefinition {
   private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
   private final Object owner;
   private final ImmutableSortedMap propertiesByName;
   private final ImmutableList states;

   protected StateDefinition(Object var1, StateDefinition.Factory var2, Map var3) {
      this.owner = var1;
      this.propertiesByName = ImmutableSortedMap.copyOf(var3);
      LinkedHashMap var4 = Maps.newLinkedHashMap();
      ArrayList var5 = Lists.newArrayList();
      Stream var6 = Stream.of(Collections.emptyList());

      Property var8;
      for(UnmodifiableIterator var7 = this.propertiesByName.values().iterator(); var7.hasNext(); var6 = var6.flatMap((var1x) -> {
         return var8.getPossibleValues().stream().map((var1) -> {
            ArrayList var2 = Lists.newArrayList(var1x);
            var2.add(var1);
            return var2;
         });
      })) {
         var8 = (Property)var7.next();
      }

      var6.forEach((var5x) -> {
         Map var6 = MapFiller.linkedHashMapFrom(this.propertiesByName.values(), var5x);
         AbstractStateHolder var7 = var2.create(var1, ImmutableMap.copyOf(var6));
         var4.put(var6, var7);
         var5.add(var7);
      });
      Iterator var9 = var5.iterator();

      while(var9.hasNext()) {
         AbstractStateHolder var10 = (AbstractStateHolder)var9.next();
         var10.populateNeighbours(var4);
      }

      this.states = ImmutableList.copyOf(var5);
   }

   public ImmutableList getPossibleStates() {
      return this.states;
   }

   public StateHolder any() {
      return (StateHolder)this.states.get(0);
   }

   public Object getOwner() {
      return this.owner;
   }

   public Collection getProperties() {
      return this.propertiesByName.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
   }

   @Nullable
   public Property getProperty(String var1) {
      return (Property)this.propertiesByName.get(var1);
   }

   public static class Builder {
      private final Object owner;
      private final Map properties = Maps.newHashMap();

      public Builder(Object var1) {
         this.owner = var1;
      }

      public StateDefinition.Builder add(Property... var1) {
         Property[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Property var5 = var2[var4];
            this.validateProperty(var5);
            this.properties.put(var5.getName(), var5);
         }

         return this;
      }

      private void validateProperty(Property var1) {
         String var2 = var1.getName();
         if (!StateDefinition.NAME_PATTERN.matcher(var2).matches()) {
            throw new IllegalArgumentException(this.owner + " has invalidly named property: " + var2);
         } else {
            Collection var3 = var1.getPossibleValues();
            if (var3.size() <= 1) {
               throw new IllegalArgumentException(this.owner + " attempted use property " + var2 + " with <= 1 possible values");
            } else {
               Iterator var4 = var3.iterator();

               String var6;
               do {
                  if (!var4.hasNext()) {
                     if (this.properties.containsKey(var2)) {
                        throw new IllegalArgumentException(this.owner + " has duplicate property: " + var2);
                     }

                     return;
                  }

                  Comparable var5 = (Comparable)var4.next();
                  var6 = var1.getName(var5);
               } while(StateDefinition.NAME_PATTERN.matcher(var6).matches());

               throw new IllegalArgumentException(this.owner + " has property: " + var2 + " with invalidly named value: " + var6);
            }
         }
      }

      public StateDefinition create(StateDefinition.Factory var1) {
         return new StateDefinition(this.owner, var1, this.properties);
      }
   }

   public interface Factory {
      AbstractStateHolder create(Object var1, ImmutableMap var2);
   }
}
