package net.minecraft.world.scores;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ScoreboardSaveData extends SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
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
      for(String var3 : var1.getAllKeys()) {
         DisplaySlot var4 = DisplaySlot.CODEC.byName(var3);
         if (var4 != null) {
            String var5 = var1.getString(var3);
            Objective var6 = this.scoreboard.getObjective(var5);
            this.scoreboard.setDisplayObjective(var4, var6);
         }
      }
   }

   private void loadObjectives(ListTag var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompound(var2);
         String var4 = var3.getString("CriteriaName");
         ObjectiveCriteria var5 = ObjectiveCriteria.byName(var4).orElseGet(() -> {
            LOGGER.warn("Unknown scoreboard criteria {}, replacing with {}", var4, ObjectiveCriteria.DUMMY.getName());
            return ObjectiveCriteria.DUMMY;
         });
         String var6 = var3.getString("Name");
         MutableComponent var7 = Component.Serializer.fromJson(var3.getString("DisplayName"));
         ObjectiveCriteria.RenderType var8 = ObjectiveCriteria.RenderType.byId(var3.getString("RenderType"));
         boolean var9 = var3.getBoolean("display_auto_update");
         NumberFormat var10 = (NumberFormat)NumberFormatTypes.CODEC.parse(NbtOps.INSTANCE, var3.get("format")).result().orElse(null);
         this.scoreboard.addObjective(var6, var5, var7, var8, var9, var10);
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

      for(DisplaySlot var6 : DisplaySlot.values()) {
         Objective var7 = this.scoreboard.getDisplayObjective(var6);
         if (var7 != null) {
            var2.putString(var6.getSerializedName(), var7.getName());
         }
      }

      if (!var2.isEmpty()) {
         var1.put("DisplaySlots", var2);
      }
   }

   private ListTag saveObjectives() {
      ListTag var1 = new ListTag();

      for(Objective var4 : this.scoreboard.getObjectives()) {
         CompoundTag var5 = new CompoundTag();
         var5.putString("Name", var4.getName());
         var5.putString("CriteriaName", var4.getCriteria().getName());
         var5.putString("DisplayName", Component.Serializer.toJson(var4.getDisplayName()));
         var5.putString("RenderType", var4.getRenderType().getId());
         var5.putBoolean("display_auto_update", var4.displayAutoUpdate());
         NumberFormat var6 = var4.numberFormat();
         if (var6 != null) {
            NumberFormatTypes.CODEC.encodeStart(NbtOps.INSTANCE, var6).result().ifPresent(var1x -> var5.put("format", var1x));
         }

         var1.add(var5);
      }

      return var1;
   }
}
