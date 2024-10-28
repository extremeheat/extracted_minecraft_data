package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;
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
         this(HashMultimap.create(), () -> {
            return "";
         });
      }

      private Collector(Multimap<String, String> var1, Supplier<String> var2) {
         super();
         this.problems = var1;
         this.path = var2;
      }

      private String getPath() {
         if (this.pathCache == null) {
            this.pathCache = (String)this.path.get();
         }

         return this.pathCache;
      }

      public ProblemReporter forChild(String var1) {
         return new Collector(this.problems, () -> {
            String var10000 = this.getPath();
            return var10000 + var1;
         });
      }

      public void report(String var1) {
         this.problems.put(this.getPath(), var1);
      }

      public Multimap<String, String> get() {
         return ImmutableMultimap.copyOf(this.problems);
      }
   }
}
