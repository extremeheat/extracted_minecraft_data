package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;

public class ValidationContext {
   private final ProblemReporter reporter;
   private final ContextKeySet contextKeySet;
   private final Set<Object> visitedObjects;

   public ValidationContext(final ProblemReporter reporter, final ContextKeySet contextKeySet) {
      this(reporter, contextKeySet, Set.of());
   }

   private ValidationContext(final ProblemReporter reporter, final ContextKeySet contextKeySet, final Set<Object> visitedObjects) {
      super();
      this.reporter = reporter;
      this.contextKeySet = contextKeySet;
      this.visitedObjects = visitedObjects;
   }

   public ValidationContext forChild(final ProblemReporter.PathElement subContext) {
      return new ValidationContext(this.reporter.forChild(subContext), this.contextKeySet, this.visitedObjects);
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

   private ValidationContext enterObject(final ProblemReporter.PathElement subContext, final Object object) {
      Set<Object> newVisitedElements = ImmutableSet.builder().addAll(this.visitedObjects).add(object).build();
      return new ValidationContext(this.reporter.forChild(subContext), this.contextKeySet, newVisitedElements);
   }

   public ValidationContext enterElement(final ProblemReporter.PathElement subContext, final ResourceKey<?> element) {
      return this.enterObject(subContext, element);
   }

   public boolean hasVisitedElement(final ResourceKey<?> element) {
      return this.visitedObjects.contains(element);
   }

   public ValidationContext enterTag(final ProblemReporter.PathElement subContext, final TagKey<?> tag) {
      return this.enterObject(subContext, tag);
   }

   public boolean hasVisitedTag(final TagKey<?> tag) {
      return this.visitedObjects.contains(tag);
   }

   public void reportProblem(final ProblemReporter.Problem description) {
      this.reporter.report(description);
   }

   public ContextKeySet contextKeySet() {
      return this.contextKeySet;
   }

   public ProblemReporter reporter() {
      return this.reporter;
   }

   public static record ParametersNotProvidedProblem(Set<ContextKey<?>> notProvided) implements ProblemReporter.Error {
      public ParametersNotProvidedProblem {
         super();
      }

      public String description() {
         return "Parameters " + String.valueOf(this.notProvided) + " are not provided in this context";
      }
   }

   public static record RecursiveElementReferenceProblem(ResourceKey<?> referenced) implements ProblemReporter.Error {
      public RecursiveElementReferenceProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.referenced.identifier());
         return var10000 + " of type " + String.valueOf(this.referenced.registry()) + " is recursively called";
      }
   }

   public static record RecursiveTagReferenceProblem(TagKey<?> referenced) implements ProblemReporter.Error {
      public RecursiveTagReferenceProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.referenced.location());
         return "#" + var10000 + " of type " + String.valueOf(this.referenced.registry()) + " is recursively called";
      }
   }

   public static record MinBoundsProblem(int minBounds) implements ProblemReporter.Warning {
      public MinBoundsProblem {
         super();
      }

      public String description() {
         return "List must contain at least " + this.minBounds + " element(s)";
      }
   }
}
