package net.minecraft.world.scores;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.resources.RegistryOps;
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
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         CompoundTag var5 = var1.getCompound(var4);
         String var6 = var5.getString("Name");
         PlayerTeam var7 = this.scoreboard.addPlayerTeam(var6);
         if (var5.contains("DisplayName")) {
            DataResult var10000 = ComponentSerialization.CODEC.parse(var3, var5.get("DisplayName"));
            Objects.requireNonNull(var7);
            var10000.ifSuccess(var7::setDisplayName);
         }

         if (var5.contains("TeamColor", 8)) {
            var7.setColor(ChatFormatting.getByName(var5.getString("TeamColor")));
         }

         if (var5.contains("AllowFriendlyFire", 99)) {
            var7.setAllowFriendlyFire(var5.getBoolean("AllowFriendlyFire"));
         }

         if (var5.contains("SeeFriendlyInvisibles", 99)) {
            var7.setSeeFriendlyInvisibles(var5.getBoolean("SeeFriendlyInvisibles"));
         }

         if (var5.contains("MemberNamePrefix")) {
            DataResult var11 = ComponentSerialization.CODEC.parse(var3, var5.get("MemberNamePrefix"));
            Objects.requireNonNull(var7);
            var11.ifSuccess(var7::setPlayerPrefix);
         }

         if (var5.contains("MemberNameSuffix")) {
            DataResult var12 = ComponentSerialization.CODEC.parse(var3, var5.get("MemberNameSuffix"));
            Objects.requireNonNull(var7);
            var12.ifSuccess(var7::setPlayerSuffix);
         }

         if (var5.contains("NameTagVisibility", 8)) {
            Team.Visibility var8 = Team.Visibility.byName(var5.getString("NameTagVisibility"));
            if (var8 != null) {
               var7.setNameTagVisibility(var8);
            }
         }

         if (var5.contains("DeathMessageVisibility", 8)) {
            Team.Visibility var9 = Team.Visibility.byName(var5.getString("DeathMessageVisibility"));
            if (var9 != null) {
               var7.setDeathMessageVisibility(var9);
            }
         }

         if (var5.contains("CollisionRule", 8)) {
            Team.CollisionRule var10 = Team.CollisionRule.byName(var5.getString("CollisionRule"));
            if (var10 != null) {
               var7.setCollisionRule(var10);
            }
         }

         this.loadTeamPlayers(var7, var5.getList("Players", 8));
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

   private void loadObjectives(ListTag var1, HolderLookup.Provider var2) {
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         CompoundTag var5 = var1.getCompound(var4);
         String var6 = var5.getString("CriteriaName");
         ObjectiveCriteria var7 = (ObjectiveCriteria)ObjectiveCriteria.byName(var6).orElseGet(() -> {
            LOGGER.warn("Unknown scoreboard criteria {}, replacing with {}", var6, ObjectiveCriteria.DUMMY.getName());
            return ObjectiveCriteria.DUMMY;
         });
         String var8 = var5.getString("Name");
         Component var9 = (Component)ComponentSerialization.CODEC.parse(var3, var5.get("DisplayName")).ifError((var1x) -> LOGGER.warn("Malformed display name for scoreboard criteria '{}', ignoring: {}", var6, var1x)).result().orElse((Object)null);
         if (var9 != null) {
            ObjectiveCriteria.RenderType var10 = ObjectiveCriteria.RenderType.byId(var5.getString("RenderType"));
            boolean var11 = var5.getBoolean("display_auto_update");
            NumberFormat var12 = (NumberFormat)NumberFormatTypes.CODEC.parse(var3, var5.get("format")).result().orElse((Object)null);
            this.scoreboard.addObjective(var8, var7, var9, var10, var11, var12);
         }
      }

   }

   public CompoundTag save(CompoundTag var1, HolderLookup.Provider var2) {
      var1.put("Objectives", this.saveObjectives(var2));
      var1.put("PlayerScores", this.scoreboard.savePlayerScores(var2));
      var1.put("Teams", this.saveTeams(var2));
      this.saveDisplaySlots(var1);
      return var1;
   }

   private ListTag saveTeams(HolderLookup.Provider var1) {
      ListTag var2 = new ListTag();
      Collection var3 = this.scoreboard.getPlayerTeams();
      RegistryOps var4 = var1.createSerializationContext(NbtOps.INSTANCE);

      for(PlayerTeam var6 : var3) {
         CompoundTag var7 = new CompoundTag();
         var7.putString("Name", var6.getName());
         var7.put("DisplayName", (Tag)ComponentSerialization.CODEC.encodeStart(var4, var6.getDisplayName()).getOrThrow());
         if (var6.getColor().getId() >= 0) {
            var7.putString("TeamColor", var6.getColor().getName());
         }

         var7.putBoolean("AllowFriendlyFire", var6.isAllowFriendlyFire());
         var7.putBoolean("SeeFriendlyInvisibles", var6.canSeeFriendlyInvisibles());
         var7.put("MemberNamePrefix", (Tag)ComponentSerialization.CODEC.encodeStart(var4, var6.getPlayerPrefix()).getOrThrow());
         var7.put("MemberNameSuffix", (Tag)ComponentSerialization.CODEC.encodeStart(var4, var6.getPlayerSuffix()).getOrThrow());
         var7.putString("NameTagVisibility", var6.getNameTagVisibility().name);
         var7.putString("DeathMessageVisibility", var6.getDeathMessageVisibility().name);
         var7.putString("CollisionRule", var6.getCollisionRule().name);
         ListTag var8 = new ListTag();

         for(String var10 : var6.getPlayers()) {
            var8.add(StringTag.valueOf(var10));
         }

         var7.put("Players", var8);
         var2.add(var7);
      }

      return var2;
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

   private ListTag saveObjectives(HolderLookup.Provider var1) {
      ListTag var2 = new ListTag();
      Collection var3 = this.scoreboard.getObjectives();
      RegistryOps var4 = var1.createSerializationContext(NbtOps.INSTANCE);

      for(Objective var6 : var3) {
         CompoundTag var7 = new CompoundTag();
         var7.putString("Name", var6.getName());
         var7.putString("CriteriaName", var6.getCriteria().getName());
         var7.put("DisplayName", (Tag)ComponentSerialization.CODEC.encodeStart(var4, var6.getDisplayName()).getOrThrow());
         var7.putString("RenderType", var6.getRenderType().getId());
         var7.putBoolean("display_auto_update", var6.displayAutoUpdate());
         NumberFormat var8 = var6.numberFormat();
         if (var8 != null) {
            NumberFormatTypes.CODEC.encodeStart(var4, var8).ifSuccess((var1x) -> var7.put("format", var1x));
         }

         var2.add(var7);
      }

      return var2;
   }
}
