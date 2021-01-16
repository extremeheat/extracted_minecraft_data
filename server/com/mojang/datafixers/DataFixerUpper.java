package com.mojang.datafixers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFixerUpper implements DataFixer {
   public static boolean ERRORS_ARE_FATAL = false;
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final PointFreeRule OPTIMIZATION_RULE = (PointFreeRule)DataFixUtils.make(() -> {
      PointFreeRule var0 = PointFreeRule.orElse(PointFreeRule.orElse(PointFreeRule.CataFuseSame.INSTANCE, PointFreeRule.orElse(PointFreeRule.CataFuseDifferent.INSTANCE, PointFreeRule.LensAppId.INSTANCE)), PointFreeRule.orElse(PointFreeRule.LensComp.INSTANCE, PointFreeRule.orElse(PointFreeRule.AppNest.INSTANCE, PointFreeRule.LensCompFunc.INSTANCE)));
      PointFreeRule var1 = PointFreeRule.many(PointFreeRule.once(PointFreeRule.orElse(var0, PointFreeRule.CompAssocLeft.INSTANCE)));
      PointFreeRule var2 = PointFreeRule.many(PointFreeRule.once(PointFreeRule.orElse(PointFreeRule.SortInj.INSTANCE, PointFreeRule.SortProj.INSTANCE)));
      PointFreeRule var3 = PointFreeRule.many(PointFreeRule.once(PointFreeRule.orElse(var0, PointFreeRule.CompAssocRight.INSTANCE)));
      return PointFreeRule.seq(ImmutableList.of(() -> {
         return var1;
      }, () -> {
         return var2;
      }, () -> {
         return var3;
      }, () -> {
         return var1;
      }, () -> {
         return var3;
      }));
   });
   private final Int2ObjectSortedMap<Schema> schemas;
   private final List<DataFix> globalList;
   private final IntSortedSet fixerVersions;
   private final Long2ObjectMap<TypeRewriteRule> rules = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());

   protected DataFixerUpper(Int2ObjectSortedMap<Schema> var1, List<DataFix> var2, IntSortedSet var3) {
      super();
      this.schemas = var1;
      this.globalList = var2;
      this.fixerVersions = var3;
   }

   public <T> Dynamic<T> update(DSL.TypeReference var1, Dynamic<T> var2, int var3, int var4) {
      if (var3 < var4) {
         Type var5 = this.getType(var1, var3);
         DataResult var6 = var5.readAndWrite(var2.getOps(), this.getType(var1, var4), this.getRule(var3, var4), OPTIMIZATION_RULE, var2.getValue());
         Logger var10001 = LOGGER;
         var10001.getClass();
         Object var7 = var6.resultOrPartial(var10001::error).orElse(var2.getValue());
         return new Dynamic(var2.getOps(), var7);
      } else {
         return var2;
      }
   }

   public Schema getSchema(int var1) {
      return (Schema)this.schemas.get(getLowestSchemaSameVersion(this.schemas, var1));
   }

   protected Type<?> getType(DSL.TypeReference var1, int var2) {
      return this.getSchema(DataFixUtils.makeKey(var2)).getType(var1);
   }

   protected static int getLowestSchemaSameVersion(Int2ObjectSortedMap<Schema> var0, int var1) {
      return var1 < var0.firstIntKey() ? var0.firstIntKey() : var0.subMap(0, var1 + 1).lastIntKey();
   }

   private int getLowestFixSameVersion(int var1) {
      return var1 < this.fixerVersions.firstInt() ? this.fixerVersions.firstInt() - 1 : this.fixerVersions.subSet(0, var1 + 1).lastInt();
   }

   protected TypeRewriteRule getRule(int var1, int var2) {
      if (var1 >= var2) {
         return TypeRewriteRule.nop();
      } else {
         int var3 = this.getLowestFixSameVersion(DataFixUtils.makeKey(var1));
         int var4 = DataFixUtils.makeKey(var2);
         long var5 = (long)var3 << 32 | (long)var4;
         return (TypeRewriteRule)this.rules.computeIfAbsent((Object)var5, (var3x) -> {
            ArrayList var4x = Lists.newArrayList();
            Iterator var5 = this.globalList.iterator();

            while(var5.hasNext()) {
               DataFix var6 = (DataFix)var5.next();
               int var7 = var6.getVersionKey();
               if (var7 > var3 && var7 <= var4) {
                  TypeRewriteRule var8 = var6.getRule();
                  if (var8 != TypeRewriteRule.nop()) {
                     var4x.add(var8);
                  }
               }
            }

            return TypeRewriteRule.seq(var4x);
         });
      }
   }

   protected IntSortedSet fixerVersions() {
      return this.fixerVersions;
   }
}
