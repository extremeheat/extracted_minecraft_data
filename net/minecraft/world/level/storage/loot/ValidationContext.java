package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ValidationContext {
   private final Multimap problems;
   private final Supplier context;
   private final LootContextParamSet params;
   private final Function conditionResolver;
   private final Set visitedConditions;
   private final Function tableResolver;
   private final Set visitedTables;
   private String contextCache;

   public ValidationContext(LootContextParamSet var1, Function var2, Function var3) {
      this(HashMultimap.create(), () -> {
         return "";
      }, var1, var2, ImmutableSet.of(), var3, ImmutableSet.of());
   }

   public ValidationContext(Multimap var1, Supplier var2, LootContextParamSet var3, Function var4, Set var5, Function var6, Set var7) {
      this.problems = var1;
      this.context = var2;
      this.params = var3;
      this.conditionResolver = var4;
      this.visitedConditions = var5;
      this.tableResolver = var6;
      this.visitedTables = var7;
   }

   private String getContext() {
      if (this.contextCache == null) {
         this.contextCache = (String)this.context.get();
      }

      return this.contextCache;
   }

   public void reportProblem(String var1) {
      this.problems.put(this.getContext(), var1);
   }

   public ValidationContext forChild(String var1) {
      return new ValidationContext(this.problems, () -> {
         return this.getContext() + var1;
      }, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
   }

   public ValidationContext enterTable(String var1, ResourceLocation var2) {
      ImmutableSet var3 = ImmutableSet.builder().addAll(this.visitedTables).add(var2).build();
      return new ValidationContext(this.problems, () -> {
         return this.getContext() + var1;
      }, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, var3);
   }

   public ValidationContext enterCondition(String var1, ResourceLocation var2) {
      ImmutableSet var3 = ImmutableSet.builder().addAll(this.visitedConditions).add(var2).build();
      return new ValidationContext(this.problems, () -> {
         return this.getContext() + var1;
      }, this.params, this.conditionResolver, var3, this.tableResolver, this.visitedTables);
   }

   public boolean hasVisitedTable(ResourceLocation var1) {
      return this.visitedTables.contains(var1);
   }

   public boolean hasVisitedCondition(ResourceLocation var1) {
      return this.visitedConditions.contains(var1);
   }

   public Multimap getProblems() {
      return ImmutableMultimap.copyOf(this.problems);
   }

   public void validateUser(LootContextUser var1) {
      this.params.validateUser(this, var1);
   }

   @Nullable
   public LootTable resolveLootTable(ResourceLocation var1) {
      return (LootTable)this.tableResolver.apply(var1);
   }

   @Nullable
   public LootItemCondition resolveCondition(ResourceLocation var1) {
      return (LootItemCondition)this.conditionResolver.apply(var1);
   }

   public ValidationContext setParams(LootContextParamSet var1) {
      return new ValidationContext(this.problems, this.context, var1, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
   }
}
