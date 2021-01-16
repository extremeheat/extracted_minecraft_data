package com.mojang.datafixers.schemas;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class Schema {
   protected final Object2IntMap<String> RECURSIVE_TYPES = new Object2IntOpenHashMap();
   private final Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES = Maps.newHashMap();
   private final Map<String, Type<?>> TYPES;
   private final int versionKey;
   private final String name;
   protected final Schema parent;

   public Schema(int var1, Schema var2) {
      super();
      this.versionKey = var1;
      int var3 = DataFixUtils.getSubVersion(var1);
      this.name = "V" + DataFixUtils.getVersion(var1) + (var3 == 0 ? "" : "." + var3);
      this.parent = var2;
      this.registerTypes(this, this.registerEntities(this), this.registerBlockEntities(this));
      this.TYPES = this.buildTypes();
   }

   protected Map<String, Type<?>> buildTypes() {
      HashMap var1 = Maps.newHashMap();
      ArrayList var2 = Lists.newArrayList();
      ObjectIterator var3 = this.RECURSIVE_TYPES.object2IntEntrySet().iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry var4 = (Object2IntMap.Entry)var3.next();
         var2.add(DSL.check((String)var4.getKey(), var4.getIntValue(), this.getTemplate((String)var4.getKey())));
      }

      TypeTemplate var9 = (TypeTemplate)var2.stream().reduce(DSL::or).get();
      RecursiveTypeFamily var10 = new RecursiveTypeFamily(this.name, var9);

      String var6;
      Type var7;
      for(Iterator var5 = this.TYPE_TEMPLATES.keySet().iterator(); var5.hasNext(); var1.put(var6, var7)) {
         var6 = (String)var5.next();
         int var8 = (Integer)this.RECURSIVE_TYPES.getOrDefault(var6, (int)-1);
         if (var8 != -1) {
            var7 = var10.apply(var8);
         } else {
            var7 = this.getTemplate(var6).apply(var10).apply(-1);
         }
      }

      return var1;
   }

   public Set<String> types() {
      return this.TYPES.keySet();
   }

   public Type<?> getTypeRaw(DSL.TypeReference var1) {
      String var2 = var1.typeName();
      return (Type)this.TYPES.computeIfAbsent(var2, (var1x) -> {
         throw new IllegalArgumentException("Unknown type: " + var2);
      });
   }

   public Type<?> getType(DSL.TypeReference var1) {
      String var2 = var1.typeName();
      Type var3 = (Type)this.TYPES.computeIfAbsent(var2, (var1x) -> {
         throw new IllegalArgumentException("Unknown type: " + var2);
      });
      return var3 instanceof RecursivePoint.RecursivePointType ? (Type)var3.findCheckedType(-1).orElseThrow(() -> {
         return new IllegalStateException("Could not find choice type in the recursive type");
      }) : var3;
   }

   public TypeTemplate resolveTemplate(String var1) {
      return (TypeTemplate)((Supplier)this.TYPE_TEMPLATES.getOrDefault(var1, () -> {
         throw new IllegalArgumentException("Unknown type: " + var1);
      })).get();
   }

   public TypeTemplate id(String var1) {
      int var2 = (Integer)this.RECURSIVE_TYPES.getOrDefault(var1, (int)-1);
      return var2 != -1 ? DSL.id(var2) : this.getTemplate(var1);
   }

   protected TypeTemplate getTemplate(String var1) {
      return DSL.named(var1, this.resolveTemplate(var1));
   }

   public Type<?> getChoiceType(DSL.TypeReference var1, String var2) {
      TaggedChoice.TaggedChoiceType var3 = this.findChoiceType(var1);
      if (!var3.types().containsKey(var2)) {
         throw new IllegalArgumentException("Data fixer not registered for: " + var2 + " in " + var1.typeName());
      } else {
         return (Type)var3.types().get(var2);
      }
   }

   public TaggedChoice.TaggedChoiceType<?> findChoiceType(DSL.TypeReference var1) {
      return (TaggedChoice.TaggedChoiceType)this.getType(var1).findChoiceType("id", -1).orElseThrow(() -> {
         return new IllegalArgumentException("Not a choice type");
      });
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      this.parent.registerTypes(var1, var2, var3);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      return this.parent.registerEntities(var1);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      return this.parent.registerBlockEntities(var1);
   }

   public void registerSimple(Map<String, Supplier<TypeTemplate>> var1, String var2) {
      this.register(var1, var2, DSL::remainder);
   }

   public void register(Map<String, Supplier<TypeTemplate>> var1, String var2, Function<String, TypeTemplate> var3) {
      this.register(var1, var2, () -> {
         return (TypeTemplate)var3.apply(var2);
      });
   }

   public void register(Map<String, Supplier<TypeTemplate>> var1, String var2, Supplier<TypeTemplate> var3) {
      var1.put(var2, var3);
   }

   public void registerType(boolean var1, DSL.TypeReference var2, Supplier<TypeTemplate> var3) {
      this.TYPE_TEMPLATES.put(var2.typeName(), var3);
      if (var1 && !this.RECURSIVE_TYPES.containsKey(var2.typeName())) {
         this.RECURSIVE_TYPES.put(var2.typeName(), this.RECURSIVE_TYPES.size());
      }

   }

   public int getVersionKey() {
      return this.versionKey;
   }

   public Schema getParent() {
      return this.parent;
   }
}
