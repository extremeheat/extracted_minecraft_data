package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public interface ProblemReporter {
   ProblemReporter forChild(String var1);

   void report(String var1);

   public static class Collector implements ProblemReporter {
      private final Multimap<String, String> problems;
      private final Supplier<String> path;
      @Nullable
      private String pathCache;

      public Collector() {
         this(HashMultimap.create(), () -> "");
      }

      private Collector(Multimap<String, String> var1, Supplier<String> var2) {
         super();
         this.problems = var1;
         this.path = var2;
      }

      private String getPath() {
         if (this.pathCache == null) {
            this.pathCache = this.path.get();
         }

         return this.pathCache;
      }

      @Override
      public ProblemReporter forChild(String var1) {
         return new ProblemReporter.Collector(this.problems, () -> this.getPath() + var1);
      }

      @Override
      public void report(String var1) {
         this.problems.put(this.getPath(), var1);
      }

      public Multimap<String, String> get() {
         return ImmutableMultimap.copyOf(this.problems);
      }

      public Optional<String> getReport() {
         Multimap var1 = this.get();
         if (!var1.isEmpty()) {
            String var2 = var1.asMap()
               .entrySet()
               .stream()
               .map(var0 -> " at " + (String)var0.getKey() + ": " + String.join("; ", (Iterable<? extends CharSequence>)var0.getValue()))
               .collect(Collectors.joining("\n"));
            return Optional.of(var2);
         } else {
            return Optional.empty();
         }
      }
   }
}
