package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;

public class LootContextParamSet {
   private final Set<LootContextParam<?>> required;
   private final Set<LootContextParam<?>> all;

   private LootContextParamSet(Set<LootContextParam<?>> var1, Set<LootContextParam<?>> var2) {
      super();
      this.required = ImmutableSet.copyOf(var1);
      this.all = ImmutableSet.copyOf(Sets.union(var1, var2));
   }

   public Set<LootContextParam<?>> getRequired() {
      return this.required;
   }

   public Set<LootContextParam<?>> getAllowed() {
      return this.all;
   }

   public String toString() {
      return "[" + Joiner.on(", ").join(this.all.stream().map((var1) -> {
         return (this.required.contains(var1) ? "!" : "") + var1.getName();
      }).iterator()) + "]";
   }

   public void validateUser(LootTableProblemCollector var1, LootContextUser var2) {
      Set var3 = var2.getReferencedContextParams();
      SetView var4 = Sets.difference(var3, this.all);
      if (!var4.isEmpty()) {
         var1.reportProblem("Parameters " + var4 + " are not provided in this context");
      }

   }

   // $FF: synthetic method
   LootContextParamSet(Set var1, Set var2, Object var3) {
      this(var1, var2);
   }

   public static class Builder {
      private final Set<LootContextParam<?>> required = Sets.newIdentityHashSet();
      private final Set<LootContextParam<?>> optional = Sets.newIdentityHashSet();

      public Builder() {
         super();
      }

      public LootContextParamSet.Builder required(LootContextParam<?> var1) {
         if (this.optional.contains(var1)) {
            throw new IllegalArgumentException("Parameter " + var1.getName() + " is already optional");
         } else {
            this.required.add(var1);
            return this;
         }
      }

      public LootContextParamSet.Builder optional(LootContextParam<?> var1) {
         if (this.required.contains(var1)) {
            throw new IllegalArgumentException("Parameter " + var1.getName() + " is already required");
         } else {
            this.optional.add(var1);
            return this;
         }
      }

      public LootContextParamSet build() {
         return new LootContextParamSet(this.required, this.optional);
      }
   }
}
