package net.minecraft.world.scores;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Scoreboard {
   private final Map<String, Objective> objectivesByName = Maps.newHashMap();
   private final Map<ObjectiveCriteria, List<Objective>> objectivesByCriteria = Maps.newHashMap();
   private final Map<String, Map<Objective, Score>> playerScores = Maps.newHashMap();
   private final Objective[] displayObjectives = new Objective[19];
   private final Map<String, PlayerTeam> teamsByName = Maps.newHashMap();
   private final Map<String, PlayerTeam> teamsByPlayer = Maps.newHashMap();
   private static String[] displaySlotNames;

   public Scoreboard() {
      super();
   }

   public boolean hasObjective(String var1) {
      return this.objectivesByName.containsKey(var1);
   }

   public Objective getOrCreateObjective(String var1) {
      return (Objective)this.objectivesByName.get(var1);
   }

   @Nullable
   public Objective getObjective(@Nullable String var1) {
      return (Objective)this.objectivesByName.get(var1);
   }

   public Objective addObjective(String var1, ObjectiveCriteria var2, Component var3, ObjectiveCriteria.RenderType var4) {
      if (var1.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + var1 + "' is too long!");
      } else if (this.objectivesByName.containsKey(var1)) {
         throw new IllegalArgumentException("An objective with the name '" + var1 + "' already exists!");
      } else {
         Objective var5 = new Objective(this, var1, var2, var3, var4);
         ((List)this.objectivesByCriteria.computeIfAbsent(var2, (var0) -> {
            return Lists.newArrayList();
         })).add(var5);
         this.objectivesByName.put(var1, var5);
         this.onObjectiveAdded(var5);
         return var5;
      }
   }

   public final void forAllObjectives(ObjectiveCriteria var1, String var2, Consumer<Score> var3) {
      ((List)this.objectivesByCriteria.getOrDefault(var1, Collections.emptyList())).forEach((var3x) -> {
         var3.accept(this.getOrCreatePlayerScore(var2, var3x));
      });
   }

   public boolean hasPlayerScore(String var1, Objective var2) {
      Map var3 = (Map)this.playerScores.get(var1);
      if (var3 == null) {
         return false;
      } else {
         Score var4 = (Score)var3.get(var2);
         return var4 != null;
      }
   }

   public Score getOrCreatePlayerScore(String var1, Objective var2) {
      if (var1.length() > 40) {
         throw new IllegalArgumentException("The player name '" + var1 + "' is too long!");
      } else {
         Map var3 = (Map)this.playerScores.computeIfAbsent(var1, (var0) -> {
            return Maps.newHashMap();
         });
         return (Score)var3.computeIfAbsent(var2, (var2x) -> {
            Score var3 = new Score(this, var2x, var1);
            var3.setScore(0);
            return var3;
         });
      }
   }

   public Collection<Score> getPlayerScores(Objective var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.playerScores.values().iterator();

      while(var3.hasNext()) {
         Map var4 = (Map)var3.next();
         Score var5 = (Score)var4.get(var1);
         if (var5 != null) {
            var2.add(var5);
         }
      }

      var2.sort(Score.SCORE_COMPARATOR);
      return var2;
   }

   public Collection<Objective> getObjectives() {
      return this.objectivesByName.values();
   }

   public Collection<String> getObjectiveNames() {
      return this.objectivesByName.keySet();
   }

   public Collection<String> getTrackedPlayers() {
      return Lists.newArrayList(this.playerScores.keySet());
   }

   public void resetPlayerScore(String var1, @Nullable Objective var2) {
      Map var3;
      if (var2 == null) {
         var3 = (Map)this.playerScores.remove(var1);
         if (var3 != null) {
            this.onPlayerRemoved(var1);
         }
      } else {
         var3 = (Map)this.playerScores.get(var1);
         if (var3 != null) {
            Score var4 = (Score)var3.remove(var2);
            if (var3.size() < 1) {
               Map var5 = (Map)this.playerScores.remove(var1);
               if (var5 != null) {
                  this.onPlayerRemoved(var1);
               }
            } else if (var4 != null) {
               this.onPlayerScoreRemoved(var1, var2);
            }
         }
      }

   }

   public Map<Objective, Score> getPlayerScores(String var1) {
      Object var2 = (Map)this.playerScores.get(var1);
      if (var2 == null) {
         var2 = Maps.newHashMap();
      }

      return (Map)var2;
   }

   public void removeObjective(Objective var1) {
      this.objectivesByName.remove(var1.getName());

      for(int var2 = 0; var2 < 19; ++var2) {
         if (this.getDisplayObjective(var2) == var1) {
            this.setDisplayObjective(var2, (Objective)null);
         }
      }

      List var5 = (List)this.objectivesByCriteria.get(var1.getCriteria());
      if (var5 != null) {
         var5.remove(var1);
      }

      Iterator var3 = this.playerScores.values().iterator();

      while(var3.hasNext()) {
         Map var4 = (Map)var3.next();
         var4.remove(var1);
      }

      this.onObjectiveRemoved(var1);
   }

   public void setDisplayObjective(int var1, @Nullable Objective var2) {
      this.displayObjectives[var1] = var2;
   }

   @Nullable
   public Objective getDisplayObjective(int var1) {
      return this.displayObjectives[var1];
   }

   public PlayerTeam getPlayerTeam(String var1) {
      return (PlayerTeam)this.teamsByName.get(var1);
   }

   public PlayerTeam addPlayerTeam(String var1) {
      if (var1.length() > 16) {
         throw new IllegalArgumentException("The team name '" + var1 + "' is too long!");
      } else {
         PlayerTeam var2 = this.getPlayerTeam(var1);
         if (var2 != null) {
            throw new IllegalArgumentException("A team with the name '" + var1 + "' already exists!");
         } else {
            var2 = new PlayerTeam(this, var1);
            this.teamsByName.put(var1, var2);
            this.onTeamAdded(var2);
            return var2;
         }
      }
   }

   public void removePlayerTeam(PlayerTeam var1) {
      this.teamsByName.remove(var1.getName());
      Iterator var2 = var1.getPlayers().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         this.teamsByPlayer.remove(var3);
      }

      this.onTeamRemoved(var1);
   }

   public boolean addPlayerToTeam(String var1, PlayerTeam var2) {
      if (var1.length() > 40) {
         throw new IllegalArgumentException("The player name '" + var1 + "' is too long!");
      } else {
         if (this.getPlayersTeam(var1) != null) {
            this.removePlayerFromTeam(var1);
         }

         this.teamsByPlayer.put(var1, var2);
         return var2.getPlayers().add(var1);
      }
   }

   public boolean removePlayerFromTeam(String var1) {
      PlayerTeam var2 = this.getPlayersTeam(var1);
      if (var2 != null) {
         this.removePlayerFromTeam(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String var1, PlayerTeam var2) {
      if (this.getPlayersTeam(var1) != var2) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + var2.getName() + "'.");
      } else {
         this.teamsByPlayer.remove(var1);
         var2.getPlayers().remove(var1);
      }
   }

   public Collection<String> getTeamNames() {
      return this.teamsByName.keySet();
   }

   public Collection<PlayerTeam> getPlayerTeams() {
      return this.teamsByName.values();
   }

   @Nullable
   public PlayerTeam getPlayersTeam(String var1) {
      return (PlayerTeam)this.teamsByPlayer.get(var1);
   }

   public void onObjectiveAdded(Objective var1) {
   }

   public void onObjectiveChanged(Objective var1) {
   }

   public void onObjectiveRemoved(Objective var1) {
   }

   public void onScoreChanged(Score var1) {
   }

   public void onPlayerRemoved(String var1) {
   }

   public void onPlayerScoreRemoved(String var1, Objective var2) {
   }

   public void onTeamAdded(PlayerTeam var1) {
   }

   public void onTeamChanged(PlayerTeam var1) {
   }

   public void onTeamRemoved(PlayerTeam var1) {
   }

   public static String getDisplaySlotName(int var0) {
      switch(var0) {
      case 0:
         return "list";
      case 1:
         return "sidebar";
      case 2:
         return "belowName";
      default:
         if (var0 >= 3 && var0 <= 18) {
            ChatFormatting var1 = ChatFormatting.getById(var0 - 3);
            if (var1 != null && var1 != ChatFormatting.RESET) {
               return "sidebar.team." + var1.getName();
            }
         }

         return null;
      }
   }

   public static int getDisplaySlotByName(String var0) {
      if ("list".equalsIgnoreCase(var0)) {
         return 0;
      } else if ("sidebar".equalsIgnoreCase(var0)) {
         return 1;
      } else if ("belowName".equalsIgnoreCase(var0)) {
         return 2;
      } else {
         if (var0.startsWith("sidebar.team.")) {
            String var1 = var0.substring("sidebar.team.".length());
            ChatFormatting var2 = ChatFormatting.getByName(var1);
            if (var2 != null && var2.getId() >= 0) {
               return var2.getId() + 3;
            }
         }

         return -1;
      }
   }

   public static String[] getDisplaySlotNames() {
      if (displaySlotNames == null) {
         displaySlotNames = new String[19];

         for(int var0 = 0; var0 < 19; ++var0) {
            displaySlotNames[var0] = getDisplaySlotName(var0);
         }
      }

      return displaySlotNames;
   }

   public void entityRemoved(Entity var1) {
      if (var1 != null && !(var1 instanceof Player) && !var1.isAlive()) {
         String var2 = var1.getStringUUID();
         this.resetPlayerScore(var2, (Objective)null);
         this.removePlayerFromTeam(var2);
      }
   }

   protected ListTag savePlayerScores() {
      ListTag var1 = new ListTag();
      this.playerScores.values().stream().map(Map::values).forEach((var1x) -> {
         var1x.stream().filter((var0) -> {
            return var0.getObjective() != null;
         }).forEach((var1xx) -> {
            CompoundTag var2 = new CompoundTag();
            var2.putString("Name", var1xx.getOwner());
            var2.putString("Objective", var1xx.getObjective().getName());
            var2.putInt("Score", var1xx.getScore());
            var2.putBoolean("Locked", var1xx.isLocked());
            var1.add(var2);
         });
      });
      return var1;
   }

   protected void loadPlayerScores(ListTag var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompound(var2);
         Objective var4 = this.getOrCreateObjective(var3.getString("Objective"));
         String var5 = var3.getString("Name");
         if (var5.length() > 40) {
            var5 = var5.substring(0, 40);
         }

         Score var6 = this.getOrCreatePlayerScore(var5, var4);
         var6.setScore(var3.getInt("Score"));
         if (var3.contains("Locked")) {
            var6.setLocked(var3.getBoolean("Locked"));
         }
      }

   }
}
