package net.minecraft.world.scores;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
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

   public ScoreboardSaveData load(CompoundTag var1, HolderLookup.Provider var2) {
      this.loadObjectives(var1.getList("Objectives", 10), var2);
      this.scoreboard.loadPlayerScores(var1.getList("PlayerScores", 10), var2);
      if (var1.contains("DisplaySlots", 10)) {
         this.loadDisplaySlots(var1.getCompound("DisplaySlots"));
      }

      if (var1.contains("Teams", 9)) {
         this.loadTeams(var1.getList("Teams", 10), var2);
      }

      return this;
   }

   private void loadTeams(ListTag var1, HolderLookup.Provider var2) {
      for (int var3 = 0; var3 < var1.size(); var3++) {
         CompoundTag var4 = var1.getCompound(var3);
         String var5 = var4.getString("Name");
         PlayerTeam var6 = this.scoreboard.addPlayerTeam(var5);
         MutableComponent var7 = Component.Serializer.fromJson(var4.getString("DisplayName"), var2);
         if (var7 != null) {
            var6.setDisplayName(var7);
         }

         if (var4.contains("TeamColor", 8)) {
            var6.setColor(ChatFormatting.getByName(var4.getString("TeamColor")));
         }

         if (var4.contains("AllowFriendlyFire", 99)) {
            var6.setAllowFriendlyFire(var4.getBoolean("AllowFriendlyFire"));
         }

         if (var4.contains("SeeFriendlyInvisibles", 99)) {
            var6.setSeeFriendlyInvisibles(var4.getBoolean("SeeFriendlyInvisibles"));
         }

         if (var4.contains("MemberNamePrefix", 8)) {
            MutableComponent var8 = Component.Serializer.fromJson(var4.getString("MemberNamePrefix"), var2);
            if (var8 != null) {
               var6.setPlayerPrefix(var8);
            }
         }

         if (var4.contains("MemberNameSuffix", 8)) {
            MutableComponent var9 = Component.Serializer.fromJson(var4.getString("MemberNameSuffix"), var2);
            if (var9 != null) {
               var6.setPlayerSuffix(var9);
            }
         }

         if (var4.contains("NameTagVisibility", 8)) {
            Team.Visibility var10 = Team.Visibility.byName(var4.getString("NameTagVisibility"));
            if (var10 != null) {
               var6.setNameTagVisibility(var10);
            }
         }

         if (var4.contains("DeathMessageVisibility", 8)) {
            Team.Visibility var11 = Team.Visibility.byName(var4.getString("DeathMessageVisibility"));
            if (var11 != null) {
               var6.setDeathMessageVisibility(var11);
            }
         }

         if (var4.contains("CollisionRule", 8)) {
            Team.CollisionRule var12 = Team.CollisionRule.byName(var4.getString("CollisionRule"));
            if (var12 != null) {
               var6.setCollisionRule(var12);
            }
         }

         this.loadTeamPlayers(var6, var4.getList("Players", 8));
      }
   }

   private void loadTeamPlayers(PlayerTeam var1, ListTag var2) {
      for (int var3 = 0; var3 < var2.size(); var3++) {
         this.scoreboard.addPlayerToTeam(var2.getString(var3), var1);
      }
   }

   private void loadDisplaySlots(CompoundTag var1) {
      for (String var3 : var1.getAllKeys()) {
         DisplaySlot var4 = DisplaySlot.CODEC.byName(var3);
         if (var4 != null) {
            String var5 = var1.getString(var3);
            Objective var6 = this.scoreboard.getObjective(var5);
            this.scoreboard.setDisplayObjective(var4, var6);
         }
      }
   }

   private void loadObjectives(ListTag var1, HolderLookup.Provider var2) {
      for (int var3 = 0; var3 < var1.size(); var3++) {
         CompoundTag var4 = var1.getCompound(var3);
         String var5 = var4.getString("CriteriaName");
         ObjectiveCriteria var6 = ObjectiveCriteria.byName(var5).orElseGet(() -> {
            LOGGER.warn("Unknown scoreboard criteria {}, replacing with {}", var5, ObjectiveCriteria.DUMMY.getName());
            return ObjectiveCriteria.DUMMY;
         });
         String var7 = var4.getString("Name");
         MutableComponent var8 = Component.Serializer.fromJson(var4.getString("DisplayName"), var2);
         ObjectiveCriteria.RenderType var9 = ObjectiveCriteria.RenderType.byId(var4.getString("RenderType"));
         boolean var10 = var4.getBoolean("display_auto_update");
         NumberFormat var11 = (NumberFormat)NumberFormatTypes.CODEC
            .parse(var2.createSerializationContext(NbtOps.INSTANCE), var4.get("format"))
            .result()
            .orElse(null);
         this.scoreboard.addObjective(var7, var6, var8, var9, var10, var11);
      }
   }

   @Override
   public CompoundTag save(CompoundTag var1, HolderLookup.Provider var2) {
      var1.put("Objectives", this.saveObjectives(var2));
      var1.put("PlayerScores", this.scoreboard.savePlayerScores(var2));
      var1.put("Teams", this.saveTeams(var2));
      this.saveDisplaySlots(var1);
      return var1;
   }

   private ListTag saveTeams(HolderLookup.Provider var1) {
      ListTag var2 = new ListTag();

      for (PlayerTeam var5 : this.scoreboard.getPlayerTeams()) {
         CompoundTag var6 = new CompoundTag();
         var6.putString("Name", var5.getName());
         var6.putString("DisplayName", Component.Serializer.toJson(var5.getDisplayName(), var1));
         if (var5.getColor().getId() >= 0) {
            var6.putString("TeamColor", var5.getColor().getName());
         }

         var6.putBoolean("AllowFriendlyFire", var5.isAllowFriendlyFire());
         var6.putBoolean("SeeFriendlyInvisibles", var5.canSeeFriendlyInvisibles());
         var6.putString("MemberNamePrefix", Component.Serializer.toJson(var5.getPlayerPrefix(), var1));
         var6.putString("MemberNameSuffix", Component.Serializer.toJson(var5.getPlayerSuffix(), var1));
         var6.putString("NameTagVisibility", var5.getNameTagVisibility().name);
         var6.putString("DeathMessageVisibility", var5.getDeathMessageVisibility().name);
         var6.putString("CollisionRule", var5.getCollisionRule().name);
         ListTag var7 = new ListTag();

         for (String var9 : var5.getPlayers()) {
            var7.add(StringTag.valueOf(var9));
         }

         var6.put("Players", var7);
         var2.add(var6);
      }

      return var2;
   }

   private void saveDisplaySlots(CompoundTag var1) {
      CompoundTag var2 = new CompoundTag();

      for (DisplaySlot var6 : DisplaySlot.values()) {
         Objective var7 = this.scoreboard.getDisplayObjective(var6);
         if (var7 != null) {
            var2.putString(var6.getSerializedName(), var7.getName());
         }
      }

      if (!var2.isEmpty()) {
         var1.put("DisplaySlots", var2);
      }
   }

   private ListTag saveObjectives(HolderLookup.Provider var1) {
      ListTag var2 = new ListTag();

      for (Objective var5 : this.scoreboard.getObjectives()) {
         CompoundTag var6 = new CompoundTag();
         var6.putString("Name", var5.getName());
         var6.putString("CriteriaName", var5.getCriteria().getName());
         var6.putString("DisplayName", Component.Serializer.toJson(var5.getDisplayName(), var1));
         var6.putString("RenderType", var5.getRenderType().getId());
         var6.putBoolean("display_auto_update", var5.displayAutoUpdate());
         NumberFormat var7 = var5.numberFormat();
         if (var7 != null) {
            NumberFormatTypes.CODEC.encodeStart(var1.createSerializationContext(NbtOps.INSTANCE), var7).ifSuccess(var1x -> var6.put("format", var1x));
         }

         var2.add(var6);
      }

      return var2;
   }
}
