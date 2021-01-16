package net.minecraft.world.scores;

import java.util.Comparator;
import javax.annotation.Nullable;

public class Score {
   public static final Comparator<Score> SCORE_COMPARATOR = (var0, var1) -> {
      if (var0.getScore() > var1.getScore()) {
         return 1;
      } else {
         return var0.getScore() < var1.getScore() ? -1 : var1.getOwner().compareToIgnoreCase(var0.getOwner());
      }
   };
   private final Scoreboard scoreboard;
   @Nullable
   private final Objective objective;
   private final String owner;
   private int count;
   private boolean locked;
   private boolean forceUpdate;

   public Score(Scoreboard var1, Objective var2, String var3) {
      super();
      this.scoreboard = var1;
      this.objective = var2;
      this.owner = var3;
      this.locked = true;
      this.forceUpdate = true;
   }

   public void add(int var1) {
      if (this.objective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.setScore(this.getScore() + var1);
      }
   }

   public void increment() {
      this.add(1);
   }

   public int getScore() {
      return this.count;
   }

   public void reset() {
      this.setScore(0);
   }

   public void setScore(int var1) {
      int var2 = this.count;
      this.count = var1;
      if (var2 != var1 || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreboard().onScoreChanged(this);
      }

   }

   @Nullable
   public Objective getObjective() {
      return this.objective;
   }

   public String getOwner() {
      return this.owner;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }
}
