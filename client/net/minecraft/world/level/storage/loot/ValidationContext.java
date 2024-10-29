package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;

public class ValidationContext {
   private final ProblemReporter reporter;
   private final ContextKeySet contextKeySet;
   private final Optional<HolderGetter.Provider> resolver;
   private final Set<ResourceKey<?>> visitedElements;

   public ValidationContext(ProblemReporter var1, ContextKeySet var2, HolderGetter.Provider var3) {
      this(var1, var2, Optional.of(var3), Set.of());
   }

   public ValidationContext(ProblemReporter var1, ContextKeySet var2) {
      this(var1, var2, Optional.empty(), Set.of());
   }

   private ValidationContext(ProblemReporter var1, ContextKeySet var2, Optional<HolderGetter.Provider> var3, Set<ResourceKey<?>> var4) {
      super();
      this.reporter = var1;
      this.contextKeySet = var2;
      this.resolver = var3;
      this.visitedElements = var4;
   }

   public ValidationContext forChild(String var1) {
      return new ValidationContext(this.reporter.forChild(var1), this.contextKeySet, this.resolver, this.visitedElements);
   }

   public ValidationContext enterElement(String var1, ResourceKey<?> var2) {
      ImmutableSet var3 = ImmutableSet.builder().addAll(this.visitedElements).add(var2).build();
      return new ValidationContext(this.reporter.forChild(var1), this.contextKeySet, this.resolver, var3);
   }

   public boolean hasVisitedElement(ResourceKey<?> var1) {
      return this.visitedElements.contains(var1);
   }

   public void reportProblem(String var1) {
      this.reporter.report(var1);
   }

   public void validateContextUsage(LootContextUser var1) {
      Set var2 = var1.getReferencedContextParams();
      Sets.SetView var3 = Sets.difference(var2, this.contextKeySet.allowed());
      if (!var3.isEmpty()) {
         this.reporter.report("Parameters " + String.valueOf(var3) + " are not provided in this context");
      }

   }

   public HolderGetter.Provider resolver() {
      return (HolderGetter.Provider)this.resolver.orElseThrow(() -> {
         return new UnsupportedOperationException("References not allowed");
      });
   }

   public boolean allowsReferences() {
      return this.resolver.isPresent();
   }

   public ValidationContext setContextKeySet(ContextKeySet var1) {
      return new ValidationContext(this.reporter, var1, this.resolver, this.visitedElements);
   }

   public ProblemReporter reporter() {
      return this.reporter;
   }
}
