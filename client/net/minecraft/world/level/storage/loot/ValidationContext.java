package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class ValidationContext {
   private final ProblemReporter reporter;
   private final LootContextParamSet params;
   private final Optional<HolderGetter.Provider> resolver;
   private final Set<ResourceKey<?>> visitedElements;

   public ValidationContext(ProblemReporter var1, LootContextParamSet var2, HolderGetter.Provider var3) {
      this(var1, var2, Optional.of(var3), Set.of());
   }

   public ValidationContext(ProblemReporter var1, LootContextParamSet var2) {
      this(var1, var2, Optional.empty(), Set.of());
   }

   private ValidationContext(ProblemReporter var1, LootContextParamSet var2, Optional<HolderGetter.Provider> var3, Set<ResourceKey<?>> var4) {
      super();
      this.reporter = var1;
      this.params = var2;
      this.resolver = var3;
      this.visitedElements = var4;
   }

   public ValidationContext forChild(String var1) {
      return new ValidationContext(this.reporter.forChild(var1), this.params, this.resolver, this.visitedElements);
   }

   public ValidationContext enterElement(String var1, ResourceKey<?> var2) {
      ImmutableSet var3 = ImmutableSet.builder().addAll(this.visitedElements).add(var2).build();
      return new ValidationContext(this.reporter.forChild(var1), this.params, this.resolver, var3);
   }

   public boolean hasVisitedElement(ResourceKey<?> var1) {
      return this.visitedElements.contains(var1);
   }

   public void reportProblem(String var1) {
      this.reporter.report(var1);
   }

   public void validateUser(LootContextUser var1) {
      this.params.validateUser(this, var1);
   }

   public HolderGetter.Provider resolver() {
      return (HolderGetter.Provider)this.resolver.orElseThrow(() -> {
         return new UnsupportedOperationException("References not allowed");
      });
   }

   public boolean allowsReferences() {
      return this.resolver.isPresent();
   }

   public ValidationContext setParams(LootContextParamSet var1) {
      return new ValidationContext(this.reporter, var1, this.resolver, this.visitedElements);
   }

   public ProblemReporter reporter() {
      return this.reporter;
   }
}
