package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class ValidationContext {
   private final Multimap<String, String> problems;
   private final Supplier<String> context;
   private final LootContextParamSet params;
   private final LootDataResolver resolver;
   private final Set<LootDataId<?>> visitedElements;
   @Nullable
   private String contextCache;

   public ValidationContext(LootContextParamSet var1, LootDataResolver var2) {
      this(HashMultimap.create(), () -> "", var1, var2, ImmutableSet.of());
   }

   public ValidationContext(Multimap<String, String> var1, Supplier<String> var2, LootContextParamSet var3, LootDataResolver var4, Set<LootDataId<?>> var5) {
      super();
      this.problems = var1;
      this.context = var2;
      this.params = var3;
      this.resolver = var4;
      this.visitedElements = var5;
   }

   private String getContext() {
      if (this.contextCache == null) {
         this.contextCache = this.context.get();
      }

      return this.contextCache;
   }

   public void reportProblem(String var1) {
      this.problems.put(this.getContext(), var1);
   }

   public ValidationContext forChild(String var1) {
      return new ValidationContext(this.problems, () -> this.getContext() + var1, this.params, this.resolver, this.visitedElements);
   }

   public ValidationContext enterElement(String var1, LootDataId<?> var2) {
      ImmutableSet var3 = ImmutableSet.builder().addAll(this.visitedElements).add(var2).build();
      return new ValidationContext(this.problems, () -> this.getContext() + var1, this.params, this.resolver, var3);
   }

   public boolean hasVisitedElement(LootDataId<?> var1) {
      return this.visitedElements.contains(var1);
   }

   public Multimap<String, String> getProblems() {
      return ImmutableMultimap.copyOf(this.problems);
   }

   public void validateUser(LootContextUser var1) {
      this.params.validateUser(this, var1);
   }

   public LootDataResolver resolver() {
      return this.resolver;
   }

   public ValidationContext setParams(LootContextParamSet var1) {
      return new ValidationContext(this.problems, this.context, var1, this.resolver, this.visitedElements);
   }
}
