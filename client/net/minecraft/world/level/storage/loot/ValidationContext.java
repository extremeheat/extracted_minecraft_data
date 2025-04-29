package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKey;
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

   public ValidationContext forChild(ProblemReporter.PathElement var1) {
      return new ValidationContext(this.reporter.forChild(var1), this.contextKeySet, this.resolver, this.visitedElements);
   }

   public ValidationContext enterElement(ProblemReporter.PathElement var1, ResourceKey<?> var2) {
      ImmutableSet var3 = ImmutableSet.builder().addAll(this.visitedElements).add(var2).build();
      return new ValidationContext(this.reporter.forChild(var1), this.contextKeySet, this.resolver, var3);
   }

   public boolean hasVisitedElement(ResourceKey<?> var1) {
      return this.visitedElements.contains(var1);
   }

   public void reportProblem(ProblemReporter.Problem var1) {
      this.reporter.report(var1);
   }

   public void validateContextUsage(LootContextUser var1) {
      Set var2 = var1.getReferencedContextParams();
      Sets.SetView var3 = Sets.difference(var2, this.contextKeySet.allowed());
      if (!var3.isEmpty()) {
         this.reporter.report(new ParametersNotProvidedProblem(var3));
      }

   }

   public HolderGetter.Provider resolver() {
      return (HolderGetter.Provider)this.resolver.orElseThrow(() -> new UnsupportedOperationException("References not allowed"));
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

   public static record ParametersNotProvidedProblem(Set<ContextKey<?>> notProvided) implements ProblemReporter.Problem {
      public ParametersNotProvidedProblem(Set<ContextKey<?>> var1) {
         super();
         this.notProvided = var1;
      }

      public String description() {
         return "Parameters " + String.valueOf(this.notProvided) + " are not provided in this context";
      }
   }

   public static record ReferenceNotAllowedProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem {
      public ReferenceNotAllowedProblem(ResourceKey<?> var1) {
         super();
         this.referenced = var1;
      }

      public String description() {
         String var10000 = String.valueOf(this.referenced.location());
         return "Reference to " + var10000 + " of type " + String.valueOf(this.referenced.registry()) + " was used, but references are not allowed";
      }
   }

   public static record RecursiveReferenceProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem {
      public RecursiveReferenceProblem(ResourceKey<?> var1) {
         super();
         this.referenced = var1;
      }

      public String description() {
         String var10000 = String.valueOf(this.referenced.location());
         return var10000 + " of type " + String.valueOf(this.referenced.registry()) + " is recursively called";
      }
   }

   public static record MissingReferenceProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem {
      public MissingReferenceProblem(ResourceKey<?> var1) {
         super();
         this.referenced = var1;
      }

      public String description() {
         String var10000 = String.valueOf(this.referenced.location());
         return "Missing element " + var10000 + " of type " + String.valueOf(this.referenced.registry());
      }
   }
}
