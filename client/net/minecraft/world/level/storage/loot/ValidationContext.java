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

   public ValidationContext(final ProblemReporter reporter, final ContextKeySet contextKeySet, final HolderGetter.Provider resolver) {
      this(reporter, contextKeySet, Optional.of(resolver), Set.of());
   }

   public ValidationContext(final ProblemReporter reporter, final ContextKeySet contextKeySet) {
      this(reporter, contextKeySet, Optional.empty(), Set.of());
   }

   private ValidationContext(final ProblemReporter reporter, final ContextKeySet contextKeySet, final Optional<HolderGetter.Provider> resolver, final Set<ResourceKey<?>> visitedElements) {
      super();
      this.reporter = reporter;
      this.contextKeySet = contextKeySet;
      this.resolver = resolver;
      this.visitedElements = visitedElements;
   }

   public ValidationContext forChild(final ProblemReporter.PathElement subContext) {
      return new ValidationContext(this.reporter.forChild(subContext), this.contextKeySet, this.resolver, this.visitedElements);
   }

   public ValidationContext forField(final String name) {
      return this.forChild(new ProblemReporter.FieldPathElement(name));
   }

   public ValidationContext forIndexedField(final String name, final int index) {
      return this.forChild(new ProblemReporter.IndexedFieldPathElement(name, index));
   }

   public ValidationContext forMapField(final String name, final String key) {
      return this.forChild(new ProblemReporter.MapEntryPathElement(name, key));
   }

   public ValidationContext enterElement(final ProblemReporter.PathElement subContext, final ResourceKey<?> element) {
      Set<ResourceKey<?>> newVisitedElements = ImmutableSet.builder().addAll(this.visitedElements).add(element).build();
      return new ValidationContext(this.reporter.forChild(subContext), this.contextKeySet, this.resolver, newVisitedElements);
   }

   public boolean hasVisitedElement(final ResourceKey<?> element) {
      return this.visitedElements.contains(element);
   }

   public void reportProblem(final ProblemReporter.Problem description) {
      this.reporter.report(description);
   }

   public void validateContextUsage(final LootContextUser lootContextUser) {
      Set<ContextKey<?>> allReferenced = lootContextUser.getReferencedContextParams();
      Set<ContextKey<?>> notProvided = Sets.difference(allReferenced, this.contextKeySet.allowed());
      if (!notProvided.isEmpty()) {
         this.reporter.report(new ParametersNotProvidedProblem(notProvided));
      }

   }

   public HolderGetter.Provider resolver() {
      return (HolderGetter.Provider)this.resolver.orElseThrow(() -> new UnsupportedOperationException("References not allowed"));
   }

   public boolean allowsReferences() {
      return this.resolver.isPresent();
   }

   public ProblemReporter reporter() {
      return this.reporter;
   }

   public static record ParametersNotProvidedProblem(Set<ContextKey<?>> notProvided) implements ProblemReporter.Problem {
      public ParametersNotProvidedProblem {
         super();
      }

      public String description() {
         return "Parameters " + String.valueOf(this.notProvided) + " are not provided in this context";
      }
   }

   public static record ReferenceNotAllowedProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem {
      public ReferenceNotAllowedProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.referenced.identifier());
         return "Reference to " + var10000 + " of type " + String.valueOf(this.referenced.registry()) + " was used, but references are not allowed";
      }
   }

   public static record RecursiveReferenceProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem {
      public RecursiveReferenceProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.referenced.identifier());
         return var10000 + " of type " + String.valueOf(this.referenced.registry()) + " is recursively called";
      }
   }

   public static record MissingReferenceProblem(ResourceKey<?> referenced) implements ProblemReporter.Problem {
      public MissingReferenceProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.referenced.identifier());
         return "Missing element " + var10000 + " of type " + String.valueOf(this.referenced.registry());
      }
   }
}
