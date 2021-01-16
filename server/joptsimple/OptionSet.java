package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public class OptionSet {
   private final List<OptionSpec<?>> detectedSpecs = new ArrayList();
   private final Map<String, AbstractOptionSpec<?>> detectedOptions = new HashMap();
   private final Map<AbstractOptionSpec<?>, List<String>> optionsToArguments = new IdentityHashMap();
   private final Map<String, AbstractOptionSpec<?>> recognizedSpecs;
   private final Map<String, List<?>> defaultValues;

   OptionSet(Map<String, AbstractOptionSpec<?>> var1) {
      super();
      this.defaultValues = defaultValues(var1);
      this.recognizedSpecs = var1;
   }

   public boolean hasOptions() {
      return this.detectedOptions.size() != 1 || !((AbstractOptionSpec)this.detectedOptions.values().iterator().next()).representsNonOptions();
   }

   public boolean has(String var1) {
      return this.detectedOptions.containsKey(var1);
   }

   public boolean has(OptionSpec<?> var1) {
      return this.optionsToArguments.containsKey(var1);
   }

   public boolean hasArgument(String var1) {
      AbstractOptionSpec var2 = (AbstractOptionSpec)this.detectedOptions.get(var1);
      return var2 != null && this.hasArgument((OptionSpec)var2);
   }

   public boolean hasArgument(OptionSpec<?> var1) {
      Objects.requireNonNull(var1);
      List var2 = (List)this.optionsToArguments.get(var1);
      return var2 != null && !var2.isEmpty();
   }

   public Object valueOf(String var1) {
      Objects.requireNonNull(var1);
      AbstractOptionSpec var2 = (AbstractOptionSpec)this.detectedOptions.get(var1);
      if (var2 == null) {
         List var3 = this.defaultValuesFor(var1);
         return var3.isEmpty() ? null : var3.get(0);
      } else {
         return this.valueOf((OptionSpec)var2);
      }
   }

   public <V> V valueOf(OptionSpec<V> var1) {
      Objects.requireNonNull(var1);
      List var2 = this.valuesOf(var1);
      switch(var2.size()) {
      case 0:
         return null;
      case 1:
         return var2.get(0);
      default:
         throw new MultipleArgumentsForOptionException(var1);
      }
   }

   public List<?> valuesOf(String var1) {
      Objects.requireNonNull(var1);
      AbstractOptionSpec var2 = (AbstractOptionSpec)this.detectedOptions.get(var1);
      return var2 == null ? this.defaultValuesFor(var1) : this.valuesOf((OptionSpec)var2);
   }

   public <V> List<V> valuesOf(OptionSpec<V> var1) {
      Objects.requireNonNull(var1);
      List var2 = (List)this.optionsToArguments.get(var1);
      if (var2 != null && !var2.isEmpty()) {
         AbstractOptionSpec var3 = (AbstractOptionSpec)var1;
         ArrayList var4 = new ArrayList();
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            var4.add(var3.convert(var6));
         }

         return Collections.unmodifiableList(var4);
      } else {
         return this.defaultValueFor(var1);
      }
   }

   public List<OptionSpec<?>> specs() {
      List var1 = this.detectedSpecs;
      var1.removeAll(Collections.singletonList(this.detectedOptions.get("[arguments]")));
      return Collections.unmodifiableList(var1);
   }

   public Map<OptionSpec<?>, List<?>> asMap() {
      HashMap var1 = new HashMap();
      Iterator var2 = this.recognizedSpecs.values().iterator();

      while(var2.hasNext()) {
         AbstractOptionSpec var3 = (AbstractOptionSpec)var2.next();
         if (!var3.representsNonOptions()) {
            var1.put(var3, this.valuesOf((OptionSpec)var3));
         }
      }

      return Collections.unmodifiableMap(var1);
   }

   public List<?> nonOptionArguments() {
      AbstractOptionSpec var1 = (AbstractOptionSpec)this.detectedOptions.get("[arguments]");
      return this.valuesOf((OptionSpec)var1);
   }

   void add(AbstractOptionSpec<?> var1) {
      this.addWithArgument(var1, (String)null);
   }

   void addWithArgument(AbstractOptionSpec<?> var1, String var2) {
      this.detectedSpecs.add(var1);
      Iterator var3 = var1.options().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         this.detectedOptions.put(var4, var1);
      }

      Object var5 = (List)this.optionsToArguments.get(var1);
      if (var5 == null) {
         var5 = new ArrayList();
         this.optionsToArguments.put(var1, var5);
      }

      if (var2 != null) {
         ((List)var5).add(var2);
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         OptionSet var2 = (OptionSet)var1;
         HashMap var3 = new HashMap(this.optionsToArguments);
         HashMap var4 = new HashMap(var2.optionsToArguments);
         return this.detectedOptions.equals(var2.detectedOptions) && var3.equals(var4);
      } else {
         return false;
      }
   }

   public int hashCode() {
      HashMap var1 = new HashMap(this.optionsToArguments);
      return this.detectedOptions.hashCode() ^ var1.hashCode();
   }

   private <V> List<V> defaultValuesFor(String var1) {
      return this.defaultValues.containsKey(var1) ? Collections.unmodifiableList((List)this.defaultValues.get(var1)) : Collections.emptyList();
   }

   private <V> List<V> defaultValueFor(OptionSpec<V> var1) {
      return this.defaultValuesFor((String)var1.options().iterator().next());
   }

   private static Map<String, List<?>> defaultValues(Map<String, AbstractOptionSpec<?>> var0) {
      HashMap var1 = new HashMap();
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.put(var3.getKey(), ((AbstractOptionSpec)var3.getValue()).defaultValues());
      }

      return var1;
   }
}
