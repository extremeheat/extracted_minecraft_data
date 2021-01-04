package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;

public class LootTableProblemCollector {
   private final Multimap<String, String> problems;
   private final Supplier<String> context;
   private String contextCache;

   public LootTableProblemCollector() {
      this(HashMultimap.create(), () -> {
         return "";
      });
   }

   public LootTableProblemCollector(Multimap<String, String> var1, Supplier<String> var2) {
      super();
      this.problems = var1;
      this.context = var2;
   }

   private String getContext() {
      if (this.contextCache == null) {
         this.contextCache = (String)this.context.get();
      }

      return this.contextCache;
   }

   public void reportProblem(String var1) {
      this.problems.put(this.getContext(), var1);
   }

   public LootTableProblemCollector forChild(String var1) {
      return new LootTableProblemCollector(this.problems, () -> {
         return this.getContext() + var1;
      });
   }

   public Multimap<String, String> getProblems() {
      return ImmutableMultimap.copyOf(this.problems);
   }
}
