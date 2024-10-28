package net.minecraft.world.scores;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

class PlayerScores {
   private final Reference2ObjectOpenHashMap<Objective, Score> scores = new Reference2ObjectOpenHashMap(16, 0.5F);

   PlayerScores() {
      super();
   }

   @Nullable
   public Score get(Objective var1) {
      return (Score)this.scores.get(var1);
   }

   public Score getOrCreate(Objective var1, Consumer<Score> var2) {
      return (Score)this.scores.computeIfAbsent(var1, (var1x) -> {
         Score var2x = new Score();
         var2.accept(var2x);
         return var2x;
      });
   }

   public boolean remove(Objective var1) {
      return this.scores.remove(var1) != null;
   }

   public boolean hasScores() {
      return !this.scores.isEmpty();
   }

   public Object2IntMap<Objective> listScores() {
      Object2IntOpenHashMap var1 = new Object2IntOpenHashMap();
      this.scores.forEach((var1x, var2) -> {
         var1.put(var1x, var2.value());
      });
      return var1;
   }

   void setScore(Objective var1, Score var2) {
      this.scores.put(var1, var2);
   }

   Map<Objective, Score> listRawScores() {
      return Collections.unmodifiableMap(this.scores);
   }
}
