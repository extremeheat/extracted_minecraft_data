package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

public class BlockState {
   private static final Joiner field_177628_a = Joiner.on(", ");
   private static final Function<IProperty, String> field_177626_b = new Function<IProperty, String>() {
      public String apply(IProperty var1) {
         return var1 == null ? "<NULL>" : var1.func_177701_a();
      }

      // $FF: synthetic method
      public Object apply(Object var1) {
         return this.apply((IProperty)var1);
      }
   };
   private final Block field_177627_c;
   private final ImmutableList<IProperty> field_177624_d;
   private final ImmutableList<IBlockState> field_177625_e;

   public BlockState(Block var1, IProperty... var2) {
      super();
      this.field_177627_c = var1;
      Arrays.sort(var2, new Comparator<IProperty>() {
         public int compare(IProperty var1, IProperty var2) {
            return var1.func_177701_a().compareTo(var2.func_177701_a());
         }

         // $FF: synthetic method
         public int compare(Object var1, Object var2) {
            return this.compare((IProperty)var1, (IProperty)var2);
         }
      });
      this.field_177624_d = ImmutableList.copyOf(var2);
      LinkedHashMap var3 = Maps.newLinkedHashMap();
      ArrayList var4 = Lists.newArrayList();
      Iterable var5 = Cartesian.func_179321_a(this.func_177620_e());
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         List var7 = (List)var6.next();
         Map var8 = MapPopulator.func_179400_b(this.field_177624_d, var7);
         BlockState.StateImplementation var9 = new BlockState.StateImplementation(var1, ImmutableMap.copyOf(var8));
         var3.put(var8, var9);
         var4.add(var9);
      }

      var6 = var4.iterator();

      while(var6.hasNext()) {
         BlockState.StateImplementation var10 = (BlockState.StateImplementation)var6.next();
         var10.func_177235_a(var3);
      }

      this.field_177625_e = ImmutableList.copyOf(var4);
   }

   public ImmutableList<IBlockState> func_177619_a() {
      return this.field_177625_e;
   }

   private List<Iterable<Comparable>> func_177620_e() {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 < this.field_177624_d.size(); ++var2) {
         var1.add(((IProperty)this.field_177624_d.get(var2)).func_177700_c());
      }

      return var1;
   }

   public IBlockState func_177621_b() {
      return (IBlockState)this.field_177625_e.get(0);
   }

   public Block func_177622_c() {
      return this.field_177627_c;
   }

   public Collection<IProperty> func_177623_d() {
      return this.field_177624_d;
   }

   public String toString() {
      return Objects.toStringHelper(this).add("block", Block.field_149771_c.func_177774_c(this.field_177627_c)).add("properties", Iterables.transform(this.field_177624_d, field_177626_b)).toString();
   }

   static class StateImplementation extends BlockStateBase {
      private final Block field_177239_a;
      private final ImmutableMap<IProperty, Comparable> field_177237_b;
      private ImmutableTable<IProperty, Comparable, IBlockState> field_177238_c;

      private StateImplementation(Block var1, ImmutableMap<IProperty, Comparable> var2) {
         super();
         this.field_177239_a = var1;
         this.field_177237_b = var2;
      }

      public Collection<IProperty> func_177227_a() {
         return Collections.unmodifiableCollection(this.field_177237_b.keySet());
      }

      public <T extends Comparable<T>> T func_177229_b(IProperty<T> var1) {
         if (!this.field_177237_b.containsKey(var1)) {
            throw new IllegalArgumentException("Cannot get property " + var1 + " as it does not exist in " + this.field_177239_a.func_176194_O());
         } else {
            return (Comparable)var1.func_177699_b().cast(this.field_177237_b.get(var1));
         }
      }

      public <T extends Comparable<T>, V extends T> IBlockState func_177226_a(IProperty<T> var1, V var2) {
         if (!this.field_177237_b.containsKey(var1)) {
            throw new IllegalArgumentException("Cannot set property " + var1 + " as it does not exist in " + this.field_177239_a.func_176194_O());
         } else if (!var1.func_177700_c().contains(var2)) {
            throw new IllegalArgumentException("Cannot set property " + var1 + " to " + var2 + " on block " + Block.field_149771_c.func_177774_c(this.field_177239_a) + ", it is not an allowed value");
         } else {
            return (IBlockState)(this.field_177237_b.get(var1) == var2 ? this : (IBlockState)this.field_177238_c.get(var1, var2));
         }
      }

      public ImmutableMap<IProperty, Comparable> func_177228_b() {
         return this.field_177237_b;
      }

      public Block func_177230_c() {
         return this.field_177239_a;
      }

      public boolean equals(Object var1) {
         return this == var1;
      }

      public int hashCode() {
         return this.field_177237_b.hashCode();
      }

      public void func_177235_a(Map<Map<IProperty, Comparable>, BlockState.StateImplementation> var1) {
         if (this.field_177238_c != null) {
            throw new IllegalStateException();
         } else {
            HashBasedTable var2 = HashBasedTable.create();
            Iterator var3 = this.field_177237_b.keySet().iterator();

            while(var3.hasNext()) {
               IProperty var4 = (IProperty)var3.next();
               Iterator var5 = var4.func_177700_c().iterator();

               while(var5.hasNext()) {
                  Comparable var6 = (Comparable)var5.next();
                  if (var6 != this.field_177237_b.get(var4)) {
                     var2.put(var4, var6, var1.get(this.func_177236_b(var4, var6)));
                  }
               }
            }

            this.field_177238_c = ImmutableTable.copyOf(var2);
         }
      }

      private Map<IProperty, Comparable> func_177236_b(IProperty var1, Comparable var2) {
         HashMap var3 = Maps.newHashMap(this.field_177237_b);
         var3.put(var1, var2);
         return var3;
      }

      // $FF: synthetic method
      StateImplementation(Block var1, ImmutableMap var2, Object var3) {
         this(var1, var2);
      }
   }
}
