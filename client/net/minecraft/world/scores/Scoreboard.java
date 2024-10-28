package net.minecraft.world.scores;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public class Scoreboard {
   public static final String HIDDEN_SCORE_PREFIX = "#";
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Object2ObjectMap<String, Objective> objectivesByName = new Object2ObjectOpenHashMap(16, 0.5F);
   private final Reference2ObjectMap<ObjectiveCriteria, List<Objective>> objectivesByCriteria = new Reference2ObjectOpenHashMap();
   private final Map<String, PlayerScores> playerScores = new Object2ObjectOpenHashMap(16, 0.5F);
   private final Map<DisplaySlot, Objective> displayObjectives = new EnumMap(DisplaySlot.class);
   private final Object2ObjectMap<String, PlayerTeam> teamsByName = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<String, PlayerTeam> teamsByPlayer = new Object2ObjectOpenHashMap();

   public Scoreboard() {
      super();
   }

   @Nullable
   public Objective getObjective(@Nullable String var1) {
      return (Objective)this.objectivesByName.get(var1);
   }

   public Objective addObjective(String var1, ObjectiveCriteria var2, Component var3, ObjectiveCriteria.RenderType var4, boolean var5, @Nullable NumberFormat var6) {
      if (this.objectivesByName.containsKey(var1)) {
         throw new IllegalArgumentException("An objective with the name '" + var1 + "' already exists!");
      } else {
         Objective var7 = new Objective(this, var1, var2, var3, var4, var5, var6);
         ((List)this.objectivesByCriteria.computeIfAbsent(var2, (var0) -> {
            return Lists.newArrayList();
         })).add(var7);
         this.objectivesByName.put(var1, var7);
         this.onObjectiveAdded(var7);
         return var7;
      }
   }

   public final void forAllObjectives(ObjectiveCriteria var1, ScoreHolder var2, Consumer<ScoreAccess> var3) {
      ((List)this.objectivesByCriteria.getOrDefault(var1, Collections.emptyList())).forEach((var3x) -> {
         var3.accept(this.getOrCreatePlayerScore(var2, var3x, true));
      });
   }

   private PlayerScores getOrCreatePlayerInfo(String var1) {
      return (PlayerScores)this.playerScores.computeIfAbsent(var1, (var0) -> {
         return new PlayerScores();
      });
   }

   public ScoreAccess getOrCreatePlayerScore(ScoreHolder var1, Objective var2) {
      return this.getOrCreatePlayerScore(var1, var2, false);
   }

   public ScoreAccess getOrCreatePlayerScore(final ScoreHolder var1, final Objective var2, boolean var3) {
      final boolean var4 = var3 || !var2.getCriteria().isReadOnly();
      PlayerScores var5 = this.getOrCreatePlayerInfo(var1.getScoreboardName());
      final MutableBoolean var6 = new MutableBoolean();
      final Score var7 = var5.getOrCreate(var2, (var1x) -> {
         var6.setTrue();
      });
      return new ScoreAccess() {
         public int get() {
            return var7.value();
         }

         public void set(int var1x) {
            if (!var4) {
               throw new IllegalStateException("Cannot modify read-only score");
            } else {
               boolean var2x = var6.isTrue();
               if (var2.displayAutoUpdate()) {
                  Component var3 = var1.getDisplayName();
                  if (var3 != null && !var3.equals(var7.display())) {
                     var7.display(var3);
                     var2x = true;
                  }
               }

               if (var1x != var7.value()) {
                  var7.value(var1x);
                  var2x = true;
               }

               if (var2x) {
                  this.sendScoreToPlayers();
               }

            }
         }

         @Nullable
         public Component display() {
            return var7.display();
         }

         public void display(@Nullable Component var1x) {
            if (var6.isTrue() || !Objects.equals(var1x, var7.display())) {
               var7.display(var1x);
               this.sendScoreToPlayers();
            }

         }

         public void numberFormatOverride(@Nullable NumberFormat var1x) {
            var7.numberFormat(var1x);
            this.sendScoreToPlayers();
         }

         public boolean locked() {
            return var7.isLocked();
         }

         public void unlock() {
            this.setLocked(false);
         }

         public void lock() {
            this.setLocked(true);
         }

         private void setLocked(boolean var1x) {
            var7.setLocked(var1x);
            if (var6.isTrue()) {
               this.sendScoreToPlayers();
            }

            Scoreboard.this.onScoreLockChanged(var1, var2);
         }

         private void sendScoreToPlayers() {
            Scoreboard.this.onScoreChanged(var1, var2, var7);
            var6.setFalse();
         }
      };
   }

   @Nullable
   public ReadOnlyScoreInfo getPlayerScoreInfo(ScoreHolder var1, Objective var2) {
      PlayerScores var3 = (PlayerScores)this.playerScores.get(var1.getScoreboardName());
      return var3 != null ? var3.get(var2) : null;
   }

   public Collection<PlayerScoreEntry> listPlayerScores(Objective var1) {
      ArrayList var2 = new ArrayList();
      this.playerScores.forEach((var2x, var3) -> {
         Score var4 = var3.get(var1);
         if (var4 != null) {
            var2.add(new PlayerScoreEntry(var2x, var4.value(), var4.display(), var4.numberFormat()));
         }

      });
      return var2;
   }

   public Collection<Objective> getObjectives() {
      return this.objectivesByName.values();
   }

   public Collection<String> getObjectiveNames() {
      return this.objectivesByName.keySet();
   }

   public Collection<ScoreHolder> getTrackedPlayers() {
      return this.playerScores.keySet().stream().map(ScoreHolder::forNameOnly).toList();
   }

   public void resetAllPlayerScores(ScoreHolder var1) {
      PlayerScores var2 = (PlayerScores)this.playerScores.remove(var1.getScoreboardName());
      if (var2 != null) {
         this.onPlayerRemoved(var1);
      }

   }

   public void resetSinglePlayerScore(ScoreHolder var1, Objective var2) {
      PlayerScores var3 = (PlayerScores)this.playerScores.get(var1.getScoreboardName());
      if (var3 != null) {
         boolean var4 = var3.remove(var2);
         if (!var3.hasScores()) {
            PlayerScores var5 = (PlayerScores)this.playerScores.remove(var1.getScoreboardName());
            if (var5 != null) {
               this.onPlayerRemoved(var1);
            }
         } else if (var4) {
            this.onPlayerScoreRemoved(var1, var2);
         }
      }

   }

   public Object2IntMap<Objective> listPlayerScores(ScoreHolder var1) {
      PlayerScores var2 = (PlayerScores)this.playerScores.get(var1.getScoreboardName());
      return var2 != null ? var2.listScores() : Object2IntMaps.emptyMap();
   }

   public void removeObjective(Objective var1) {
      this.objectivesByName.remove(var1.getName());
      DisplaySlot[] var2 = DisplaySlot.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DisplaySlot var5 = var2[var4];
         if (this.getDisplayObjective(var5) == var1) {
            this.setDisplayObjective(var5, (Objective)null);
         }
      }

      List var6 = (List)this.objectivesByCriteria.get(var1.getCriteria());
      if (var6 != null) {
         var6.remove(var1);
      }

      Iterator var7 = this.playerScores.values().iterator();

      while(var7.hasNext()) {
         PlayerScores var8 = (PlayerScores)var7.next();
         var8.remove(var1);
      }

      this.onObjectiveRemoved(var1);
   }

   public void setDisplayObjective(DisplaySlot var1, @Nullable Objective var2) {
      this.displayObjectives.put(var1, var2);
   }

   @Nullable
   public Objective getDisplayObjective(DisplaySlot var1) {
      return (Objective)this.displayObjectives.get(var1);
   }

   @Nullable
   public PlayerTeam getPlayerTeam(String var1) {
      return (PlayerTeam)this.teamsByName.get(var1);
   }

   public PlayerTeam addPlayerTeam(String var1) {
      PlayerTeam var2 = this.getPlayerTeam(var1);
      if (var2 != null) {
         LOGGER.warn("Requested creation of existing team '{}'", var1);
         return var2;
      } else {
         var2 = new PlayerTeam(this, var1);
         this.teamsByName.put(var1, var2);
         this.onTeamAdded(var2);
         return var2;
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
      if (this.getPlayersTeam(var1) != null) {
         this.removePlayerFromTeam(var1);
      }

      this.teamsByPlayer.put(var1, var2);
      return var2.getPlayers().add(var1);
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

   protected void onScoreChanged(ScoreHolder var1, Objective var2, Score var3) {
   }

   protected void onScoreLockChanged(ScoreHolder var1, Objective var2) {
   }

   public void onPlayerRemoved(ScoreHolder var1) {
   }

   public void onPlayerScoreRemoved(ScoreHolder var1, Objective var2) {
   }

   public void onTeamAdded(PlayerTeam var1) {
   }

   public void onTeamChanged(PlayerTeam var1) {
   }

   public void onTeamRemoved(PlayerTeam var1) {
   }

   public void entityRemoved(Entity var1) {
      if (!(var1 instanceof Player) && !var1.isAlive()) {
         this.resetAllPlayerScores(var1);
         this.removePlayerFromTeam(var1.getScoreboardName());
      }
   }

   protected ListTag savePlayerScores(HolderLookup.Provider var1) {
      ListTag var2 = new ListTag();
      this.playerScores.forEach((var2x, var3) -> {
         var3.listRawScores().forEach((var3x, var4) -> {
            CompoundTag var5 = var4.write(var1);
            var5.putString("Name", var2x);
            var5.putString("Objective", var3x.getName());
            var2.add(var5);
         });
      });
      return var2;
   }

   protected void loadPlayerScores(ListTag var1, HolderLookup.Provider var2) {
      for(int var3 = 0; var3 < var1.size(); ++var3) {
         CompoundTag var4 = var1.getCompound(var3);
         Score var5 = Score.read(var4, var2);
         String var6 = var4.getString("Name");
         String var7 = var4.getString("Objective");
         Objective var8 = this.getObjective(var7);
         if (var8 == null) {
            LOGGER.error("Unknown objective {} for name {}, ignoring", var7, var6);
         } else {
            this.getOrCreatePlayerInfo(var6).setScore(var8, var5);
         }
      }

   }
}
