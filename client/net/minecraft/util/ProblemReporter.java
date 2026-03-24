package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public interface ProblemReporter {
   ProblemReporter DISCARDING = new ProblemReporter() {
      public ProblemReporter forChild(final PathElement path) {
         return this;
      }

      public void report(final Problem problem) {
      }
   };

   ProblemReporter forChild(PathElement path);

   void report(Problem problem);

   public static record RootFieldPathElement(String name) implements PathElement {
      public RootFieldPathElement {
         super();
      }

      public String get() {
         return this.name;
      }
   }

   public static record RootElementPathElement(ResourceKey<?> id) implements PathElement {
      public RootElementPathElement {
         super();
      }

      public String get() {
         String var10000 = String.valueOf(this.id.identifier());
         return "{" + var10000 + "@" + String.valueOf(this.id.registry()) + "}";
      }
   }

   public static record FieldPathElement(String name) implements PathElement {
      public FieldPathElement {
         super();
      }

      public String get() {
         return "." + this.name;
      }
   }

   public static record IndexedFieldPathElement(String name, int index) implements PathElement {
      public IndexedFieldPathElement {
         super();
      }

      public String get() {
         return "." + this.name + "[" + this.index + "]";
      }
   }

   public static record IndexedPathElement(int index) implements PathElement {
      public IndexedPathElement {
         super();
      }

      public String get() {
         return "[" + this.index + "]";
      }
   }

   public static record MapEntryPathElement(String name, String key) implements PathElement {
      public MapEntryPathElement {
         super();
      }

      public String get() {
         return "." + this.name + "[" + this.key + "]";
      }
   }

   public static record ElementReferencePathElement(ResourceKey<?> id) implements PathElement {
      public ElementReferencePathElement {
         super();
      }

      public String get() {
         String var10000 = String.valueOf(this.id.identifier());
         return "->{" + var10000 + "@" + String.valueOf(this.id.registry()) + "}";
      }
   }

   public static class Collector implements ProblemReporter {
      public static final PathElement EMPTY_ROOT = () -> "";
      private final @Nullable Collector parent;
      private final PathElement element;
      private final Set<Entry> problems;

      public Collector() {
         this(EMPTY_ROOT);
      }

      public Collector(final PathElement root) {
         super();
         this.parent = null;
         this.problems = new LinkedHashSet();
         this.element = root;
      }

      private Collector(final Collector parent, final PathElement path) {
         super();
         this.problems = parent.problems;
         this.parent = parent;
         this.element = path;
      }

      public ProblemReporter forChild(final PathElement path) {
         return new Collector(this, path);
      }

      public void report(final Problem problem) {
         this.problems.add(new Entry(this, problem));
      }

      public boolean isEmpty() {
         return this.problems.isEmpty();
      }

      public void forEach(final BiConsumer<String, Problem> output) {
         List<PathElement> pathElements = new ArrayList();
         StringBuilder pathString = new StringBuilder();

         for(Entry entry : this.problems) {
            for(Collector current = entry.source; current != null; current = current.parent) {
               pathElements.add(current.element);
            }

            for(int i = pathElements.size() - 1; i >= 0; --i) {
               pathString.append(((PathElement)pathElements.get(i)).get());
            }

            output.accept(pathString.toString(), entry.problem());
            pathString.setLength(0);
            pathElements.clear();
         }

      }

      public String getReport() {
         Multimap<String, Problem> groupedProblems = HashMultimap.create();
         Objects.requireNonNull(groupedProblems);
         this.forEach(groupedProblems::put);
         return (String)groupedProblems.asMap().entrySet().stream().map((entry) -> {
            String var10000 = (String)entry.getKey();
            return " at " + var10000 + ": " + (String)((Collection)entry.getValue()).stream().map(Problem::description).collect(Collectors.joining("; "));
         }).collect(Collectors.joining("\n"));
      }

      public String getTreeReport() {
         List<PathElement> pathElements = new ArrayList();
         ProblemTreeNode root = new ProblemTreeNode(this.element);

         for(Entry entry : this.problems) {
            for(Collector current = entry.source; current != this; current = current.parent) {
               pathElements.add(current.element);
            }

            ProblemTreeNode node = root;

            for(int i = pathElements.size() - 1; i >= 0; --i) {
               node = node.child((PathElement)pathElements.get(i));
            }

            pathElements.clear();
            node.problems.add(entry.problem);
         }

         return String.join("\n", root.getLines());
      }

      private static record Entry(Collector source, Problem problem) {
         private Entry {
            super();
         }
      }

      private static record ProblemTreeNode(PathElement element, List<Problem> problems, Map<PathElement, ProblemTreeNode> children) {
         public ProblemTreeNode(final PathElement pathElement) {
            this(pathElement, new ArrayList(), new LinkedHashMap());
         }

         private ProblemTreeNode {
            super();
         }

         public ProblemTreeNode child(final PathElement id) {
            return (ProblemTreeNode)this.children.computeIfAbsent(id, ProblemTreeNode::new);
         }

         public List<String> getLines() {
            int problemCount = this.problems.size();
            int childrenCount = this.children.size();
            if (problemCount == 0 && childrenCount == 0) {
               return List.of();
            } else if (problemCount == 0 && childrenCount == 1) {
               List<String> lines = new ArrayList();
               this.children.forEach((element, child) -> lines.addAll(child.getLines()));
               String var10002 = this.element.get();
               lines.set(0, var10002 + (String)lines.get(0));
               return lines;
            } else if (problemCount == 1 && childrenCount == 0) {
               String var10000 = this.element.get();
               return List.of(var10000 + ": " + ((Problem)this.problems.getFirst()).description());
            } else {
               List<String> lines = new ArrayList();
               this.children.forEach((element, child) -> lines.addAll(child.getLines()));
               lines.replaceAll((s) -> "  " + s);

               for(Problem problem : this.problems) {
                  lines.add("  " + problem.description());
               }

               lines.addFirst(this.element.get() + ":");
               return lines;
            }
         }
      }
   }

   public static class ScopedCollector extends Collector implements AutoCloseable {
      private final Logger logger;

      public ScopedCollector(final Logger logger) {
         super();
         this.logger = logger;
      }

      public ScopedCollector(final PathElement root, final Logger logger) {
         super(root);
         this.logger = logger;
      }

      public void close() {
         if (!this.isEmpty()) {
            this.logger.warn("[{}] Serialization errors:\n{}", this.logger.getName(), this.getTreeReport());
         }

      }
   }

   @FunctionalInterface
   public interface PathElement {
      String get();
   }

   public interface Problem {
      String description();
   }
}
