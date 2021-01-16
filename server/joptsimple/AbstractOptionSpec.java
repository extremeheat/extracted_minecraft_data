package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import joptsimple.internal.Reflection;
import joptsimple.internal.ReflectionException;

public abstract class AbstractOptionSpec<V> implements OptionSpec<V>, OptionDescriptor {
   private final List<String> options;
   private final String description;
   private boolean forHelp;

   AbstractOptionSpec(String var1) {
      this(Collections.singletonList(var1), "");
   }

   AbstractOptionSpec(List<String> var1, String var2) {
      super();
      this.options = new ArrayList();
      this.arrangeOptions(var1);
      this.description = var2;
   }

   public final List<String> options() {
      return Collections.unmodifiableList(this.options);
   }

   public final List<V> values(OptionSet var1) {
      return var1.valuesOf((OptionSpec)this);
   }

   public final V value(OptionSet var1) {
      return var1.valueOf((OptionSpec)this);
   }

   public String description() {
      return this.description;
   }

   public final AbstractOptionSpec<V> forHelp() {
      this.forHelp = true;
      return this;
   }

   public final boolean isForHelp() {
      return this.forHelp;
   }

   public boolean representsNonOptions() {
      return false;
   }

   protected abstract V convert(String var1);

   protected V convertWith(ValueConverter<V> var1, String var2) {
      try {
         return Reflection.convertWith(var1, var2);
      } catch (ValueConversionException | ReflectionException var4) {
         throw new OptionArgumentConversionException(this, var2, var4);
      }
   }

   protected String argumentTypeIndicatorFrom(ValueConverter<V> var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.valuePattern();
         return var2 == null ? var1.valueType().getName() : var2;
      }
   }

   abstract void handleOption(OptionParser var1, ArgumentList var2, OptionSet var3, String var4);

   private void arrangeOptions(List<String> var1) {
      if (var1.size() == 1) {
         this.options.addAll(var1);
      } else {
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            if (var5.length() == 1) {
               var2.add(var5);
            } else {
               var3.add(var5);
            }
         }

         Collections.sort(var2);
         Collections.sort(var3);
         this.options.addAll(var2);
         this.options.addAll(var3);
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof AbstractOptionSpec)) {
         return false;
      } else {
         AbstractOptionSpec var2 = (AbstractOptionSpec)var1;
         return this.options.equals(var2.options);
      }
   }

   public int hashCode() {
      return this.options.hashCode();
   }

   public String toString() {
      return this.options.toString();
   }
}
