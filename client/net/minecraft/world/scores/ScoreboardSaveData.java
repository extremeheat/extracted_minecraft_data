package net.minecraft.world.scores;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardSaveData extends SavedData {
   public static final String FILE_ID = "scoreboard";
   private final Scoreboard scoreboard;

   public ScoreboardSaveData(Scoreboard var1) {
      super();
      this.scoreboard = var1;
   }

   public ScoreboardSaveData load(CompoundTag var1) {
      this.loadObjectives(var1.getList("Objectives", 10));
      this.scoreboard.loadPlayerScores(var1.getList("PlayerScores", 10));
      if (var1.contains("DisplaySlots", 10)) {
         this.loadDisplaySlots(var1.getCompound("DisplaySlots"));
      }

      if (var1.contains("Teams", 9)) {
         this.loadTeams(var1.getList("Teams", 10));
      }

      return this;
   }

   private void loadTeams(ListTag var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompound(var2);
         String var4 = var3.getString("Name");
         PlayerTeam var5 = this.scoreboard.addPlayerTeam(var4);
         MutableComponent var6 = Component.Serializer.fromJson(var3.getString("DisplayName"));
         if (var6 != null) {
            var5.setDisplayName(var6);
         }

         if (var3.contains("TeamColor", 8)) {
            var5.setColor(ChatFormatting.getByName(var3.getString("TeamColor")));
         }

         if (var3.contains("AllowFriendlyFire", 99)) {
            var5.setAllowFriendlyFire(var3.getBoolean("AllowFriendlyFire"));
         }

         if (var3.contains("SeeFriendlyInvisibles", 99)) {
            var5.setSeeFriendlyInvisibles(var3.getBoolean("SeeFriendlyInvisibles"));
         }

         if (var3.contains("MemberNamePrefix", 8)) {
            MutableComponent var7 = Component.Serializer.fromJson(var3.getString("MemberNamePrefix"));
            if (var7 != null) {
               var5.setPlayerPrefix(var7);
            }
         }

         if (var3.contains("MemberNameSuffix", 8)) {
            MutableComponent var8 = Component.Serializer.fromJson(var3.getString("MemberNameSuffix"));
            if (var8 != null) {
               var5.setPlayerSuffix(var8);
            }
         }

         if (var3.contains("NameTagVisibility", 8)) {
            Team.Visibility var9 = Team.Visibility.byName(var3.getString("NameTagVisibility"));
            if (var9 != null) {
               var5.setNameTagVisibility(var9);
            }
         }

         if (var3.contains("DeathMessageVisibility", 8)) {
            Team.Visibility var10 = Team.Visibility.byName(var3.getString("DeathMessageVisibility"));
            if (var10 != null) {
               var5.setDeathMessageVisibility(var10);
            }
         }

         if (var3.contains("CollisionRule", 8)) {
            Team.CollisionRule var11 = Team.CollisionRule.byName(var3.getString("CollisionRule"));
            if (var11 != null) {
               var5.setCollisionRule(var11);
            }
         }

         this.loadTeamPlayers(var5, var3.getList("Players", 8));
      }
   }

   private void loadTeamPlayers(PlayerTeam var1, ListTag var2) {
      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.scoreboard.addPlayerToTeam(var2.getString(var3), var1);
      }
   }

   private void loadDisplaySlots(CompoundTag var1) {
      for(int var2 = 0; var2 < 19; ++var2) {
         if (var1.contains("slot_" + var2, 8)) {
            String var3 = var1.getString("slot_" + var2);
            Objective var4 = this.scoreboard.getObjective(var3);
            this.scoreboard.setDisplayObjective(var2, var4);
         }
      }
   }

   private void loadObjectives(ListTag var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompound(var2);
         ObjectiveCriteria.byName(var3.getString("CriteriaName")).ifPresent(var2x -> {
            String var3x = var3.getString("Name");
            MutableComponent var4 = Component.Serializer.fromJson(var3.getString("DisplayName"));
            ObjectiveCriteria.RenderType var5 = ObjectiveCriteria.RenderType.byId(var3.getString("RenderType"));
            this.scoreboard.addObjective(var3x, var2x, var4, var5);
         });
      }
   }

   @Override
   public CompoundTag save(CompoundTag var1) {
      var1.put("Objectives", this.saveObjectives());
      var1.put("PlayerScores", this.scoreboard.savePlayerScores());
      var1.put("Teams", this.saveTeams());
      this.saveDisplaySlots(var1);
      return var1;
   }

   private ListTag saveTeams() {
      ListTag var1 = new ListTag();

      for(PlayerTeam var4 : this.scoreboard.getPlayerTeams()) {
         CompoundTag var5 = new CompoundTag();
         var5.putString("Name", var4.getName());
         var5.putString("DisplayName", Component.Serializer.toJson(var4.getDisplayName()));
         if (var4.getColor().getId() >= 0) {
            var5.putString("TeamColor", var4.getColor().getName());
         }

         var5.putBoolean("AllowFriendlyFire", var4.isAllowFriendlyFire());
         var5.putBoolean("SeeFriendlyInvisibles", var4.canSeeFriendlyInvisibles());
         var5.putString("MemberNamePrefix", Component.Serializer.toJson(var4.getPlayerPrefix()));
         var5.putString("MemberNameSuffix", Component.Serializer.toJson(var4.getPlayerSuffix()));
         var5.putString("NameTagVisibility", var4.getNameTagVisibility().name);
         var5.putString("DeathMessageVisibility", var4.getDeathMessageVisibility().name);
         var5.putString("CollisionRule", var4.getCollisionRule().name);
         ListTag var6 = new ListTag();

         for(String var8 : var4.getPlayers()) {
            var6.add(StringTag.valueOf(var8));
         }

         var5.put("Players", var6);
         var1.add(var5);
      }

      return var1;
   }

   private void saveDisplaySlots(CompoundTag var1) {
      CompoundTag var2 = new CompoundTag();
      boolean var3 = false;

      for(int var4 = 0; var4 < 19; ++var4) {
         Objective var5 = this.scoreboard.getDisplayObjective(var4);
         if (var5 != null) {
            var2.putString("slot_" + var4, var5.getName());
            var3 = true;
         }
      }

      if (var3) {
         var1.put("DisplaySlots", var2);
      }
   }

   private ListTag saveObjectives() {
      ListTag var1 = new ListTag();

      for(Objective var4 : this.scoreboard.getObjectives()) {
         if (var4.getCriteria() != null) {
            CompoundTag var5 = new CompoundTag();
            var5.putString("Name", var4.getName());
            var5.putString("CriteriaName", var4.getCriteria().getName());
            var5.putString("DisplayName", Component.Serializer.toJson(var4.getDisplayName()));
            var5.putString("RenderType", var4.getRenderType().getId());
            var1.add(var5);
         }
      }

      return var1;
   }
}
