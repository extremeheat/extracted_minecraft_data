package com.mojang.datafixers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFixerBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final int dataVersion;
   private final Int2ObjectSortedMap<Schema> schemas = new Int2ObjectAVLTreeMap();
   private final List<DataFix> globalList = Lists.newArrayList();
   private final IntSortedSet fixerVersions = new IntAVLTreeSet();

   public DataFixerBuilder(int var1) {
      super();
      this.dataVersion = var1;
   }

   public Schema addSchema(int var1, BiFunction<Integer, Schema, Schema> var2) {
      return this.addSchema(var1, 0, var2);
   }

   public Schema addSchema(int var1, int var2, BiFunction<Integer, Schema, Schema> var3) {
      int var4 = DataFixUtils.makeKey(var1, var2);
      Schema var5 = this.schemas.isEmpty() ? null : (Schema)this.schemas.get(DataFixerUpper.getLowestSchemaSameVersion(this.schemas, var4 - 1));
      Schema var6 = (Schema)var3.apply(DataFixUtils.makeKey(var1, var2), var5);
      this.addSchema(var6);
      return var6;
   }

   public void addSchema(Schema var1) {
      this.schemas.put(var1.getVersionKey(), var1);
   }

   public void addFixer(DataFix var1) {
      int var2 = DataFixUtils.getVersion(var1.getVersionKey());
      if (var2 > this.dataVersion) {
         LOGGER.warn((String)"Ignored fix registered for version: {} as the DataVersion of the game is: {}", (Object)var2, (Object)this.dataVersion);
      } else {
         this.globalList.add(var1);
         this.fixerVersions.add(var1.getVersionKey());
      }
   }

   public DataFixer build(Executor var1) {
      DataFixerUpper var2 = new DataFixerUpper(new Int2ObjectAVLTreeMap(this.schemas), new ArrayList(this.globalList), new IntAVLTreeSet(this.fixerVersions));
      IntBidirectionalIterator var3 = var2.fixerVersions().iterator();

      while(var3.hasNext()) {
         int var4 = var3.nextInt();
         Schema var5 = (Schema)this.schemas.get(var4);
         Iterator var6 = var5.types().iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            CompletableFuture.runAsync(() -> {
               Type var5x = var5.getType(() -> {
                  return var7;
               });
               TypeRewriteRule var6 = var2.getRule(DataFixUtils.getVersion(var4), this.dataVersion);
               var5x.rewrite(var6, DataFixerUpper.OPTIMIZATION_RULE);
            }, var1).exceptionally((var0) -> {
               LOGGER.error("Unable to build datafixers", var0);
               Runtime.getRuntime().exit(1);
               return null;
            });
         }
      }

      return var2;
   }
}
