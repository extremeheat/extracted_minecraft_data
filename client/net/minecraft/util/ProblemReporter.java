package net.minecraft.util;

import com.google.common.collect.HashMultimap;
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
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface ProblemReporter {
   ProblemReporter DISCARDING = new ProblemReporter() {
      public ProblemReporter forChild(PathElement var1) {
         return this;
      }

      public void report(Problem var1) {
      }
   };

   ProblemReporter forChild(PathElement var1);

   void report(Problem var1);

   public static record RootFieldPathElement(String name) implements PathElement {
      public RootFieldPathElement(String var1) {
         super();
         this.name = var1;
      }

      public String get() {
         return this.name;
      }
   }

   public static record RootElementPathElement(ResourceKey<?> id) implements PathElement {
      public RootElementPathElement(ResourceKey<?> var1) {
         super();
         this.id = var1;
      }

      public String get() {
         String var10000 = String.valueOf(this.id.location());
         return "{" + var10000 + "@" + String.valueOf(this.id.registry()) + "}";
      }
   }

   public static record FieldPathElement(String name) implements PathElement {
      public FieldPathElement(String var1) {
         super();
         this.name = var1;
      }

      public String get() {
         return "." + this.name;
      }
   }

   public static record IndexedFieldPathElement(String name, int index) implements PathElement {
      public IndexedFieldPathElement(String var1, int var2) {
         super();
         this.name = var1;
         this.index = var2;
      }

      public String get() {
         return "." + this.name + "[" + this.index + "]";
      }
   }

   public static record IndexedPathElement(int index) implements PathElement {
      public IndexedPathElement(int var1) {
         super();
         this.index = var1;
      }

      public String get() {
         return "[" + this.index + "]";
      }
   }

   public static record ElementReferencePathElement(ResourceKey<?> id) implements PathElement {
      public ElementReferencePathElement(ResourceKey<?> var1) {
         super();
         this.id = var1;
      }

      public String get() {
         String var10000 = String.valueOf(this.id.location());
         return "->{" + var10000 + "@" + String.valueOf(this.id.registry()) + "}";
      }
   }

   public static class Collector implements ProblemReporter {
      public static final PathElement EMPTY_ROOT = () -> "";
      @Nullable
      private final Collector parent;
      private final PathElement element;
      private final Set<Entry> problems;

      public Collector() {
         this(EMPTY_ROOT);
      }

      public Collector(PathElement var1) {
         super();
         this.parent = null;
         this.problems = new LinkedHashSet();
         this.element = var1;
      }

      private Collector(Collector var1, PathElement var2) {
         super();
         this.problems = var1.problems;
         this.parent = var1;
         this.element = var2;
      }

      public ProblemReporter forChild(PathElement var1) {
         return new Collector(this, var1);
      }

      public void report(Problem var1) {
         this.problems.add(new Entry(this, var1));
      }

      public boolean isEmpty() {
         return this.problems.isEmpty();
      }

      public void forEach(BiConsumer<String, Problem> var1) {
         ArrayList var2 = new ArrayList();
         StringBuilder var3 = new StringBuilder();

         for(Entry var5 : this.problems) {
            for(Collector var6 = var5.source; var6 != null; var6 = var6.parent) {
               var2.add(var6.element);
            }

            for(int var7 = var2.size() - 1; var7 >= 0; --var7) {
               var3.append(((PathElement)var2.get(var7)).get());
            }

            var1.accept(var3.toString(), var5.problem());
            var3.setLength(0);
            var2.clear();
         }

      }

      public String getReport() {
         HashMultimap var1 = HashMultimap.create();
         Objects.requireNonNull(var1);
         this.forEach(var1::put);
         return (String)var1.asMap().entrySet().stream().map((var0) -> {
            String var10000 = (String)var0.getKey();
            return " at " + var10000 + ": " + (String)((Collection)var0.getValue()).stream().map(Problem::description).collect(Collectors.joining("; "));
         }).collect(Collectors.joining("\n"));
      }

      public String getTreeReport() {
         ArrayList var1 = new ArrayList();
         ProblemTreeNode var2 = new ProblemTreeNode(this.element);

         for(Entry var4 : this.problems) {
            for(Collector var5 = var4.source; var5 != this; var5 = var5.parent) {
               var1.add(var5.element);
            }

            ProblemTreeNode var6 = var2;

            for(int var7 = var1.size() - 1; var7 >= 0; --var7) {
               var6 = var6.child((PathElement)var1.get(var7));
            }

            var1.clear();
            var6.problems.add(var4.problem);
         }

         return String.join("\n", var2.getLines());
      }

      static record Entry(Collector source, Problem problem) {
         final Collector source;
         final Problem problem;

         Entry(Collector var1, Problem var2) {
            super();
            this.source = var1;
            this.problem = var2;
         }
      }

      static record ProblemTreeNode(PathElement element, List<Problem> problems, Map<PathElement, ProblemTreeNode> children) {
         final List<Problem> problems;

         public ProblemTreeNode(PathElement var1) {
            this(var1, new ArrayList(), new LinkedHashMap());
         }

         private ProblemTreeNode(PathElement var1, List<Problem> var2, Map<PathElement, ProblemTreeNode> var3) {
            super();
            this.element = var1;
            this.problems = var2;
            this.children = var3;
         }

         public ProblemTreeNode child(PathElement var1) {
            return (ProblemTreeNode)this.children.computeIfAbsent(var1, ProblemTreeNode::new);
         }

         public List<String> getLines() {
            int var1 = this.problems.size();
            int var2 = this.children.size();
            if (var1 == 0 && var2 == 0) {
               return List.of();
            } else if (var1 == 0 && var2 == 1) {
               ArrayList var6 = new ArrayList();
               this.children.forEach((var1x, var2x) -> var6.addAll(var2x.getLines()));
               String var10002 = this.element.get();
               var6.set(0, var10002 + (String)var6.get(0));
               return var6;
            } else if (var1 == 1 && var2 == 0) {
               String var10000 = this.element.get();
               return List.of(var10000 + ": " + ((Problem)this.problems.getFirst()).description());
            } else {
               ArrayList var3 = new ArrayList();
               this.children.forEach((var1x, var2x) -> var3.addAll(var2x.getLines()));
               var3.replaceAll((var0) -> "  " + var0);

               for(Problem var5 : this.problems) {
                  var3.add("  " + var5.description());
               }

               var3.addFirst(this.element.get() + ":");
               return var3;
            }
         }
      }
   }

   public static class ScopedCollector extends Collector implements AutoCloseable {
      private final Logger logger;

      public ScopedCollector(Logger var1) {
         super();
         this.logger = var1;
      }

      public ScopedCollector(PathElement var1, Logger var2) {
         super(var1);
         this.logger = var2;
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
