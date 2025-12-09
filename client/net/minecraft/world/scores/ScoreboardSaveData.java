package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class ScoreboardSaveData extends SavedData {
   public static final SavedDataType<ScoreboardSaveData> TYPE;
   private Packed data;

   private ScoreboardSaveData() {
      this(ScoreboardSaveData.Packed.EMPTY);
   }

   public ScoreboardSaveData(Packed var1) {
      super();
      this.data = var1;
   }

   public Packed getData() {
      return this.data;
   }

   public void setData(Packed var1) {
      if (!var1.equals(this.data)) {
         this.data = var1;
         this.setDirty();
      }

   }

   static {
      TYPE = new SavedDataType<ScoreboardSaveData>("scoreboard", ScoreboardSaveData::new, ScoreboardSaveData.Packed.CODEC.xmap(ScoreboardSaveData::new, ScoreboardSaveData::getData), DataFixTypes.SAVED_DATA_SCOREBOARD);
   }

   public static record Packed(List<Objective.Packed> objectives, List<Scoreboard.PackedScore> scores, Map<DisplaySlot, String> displaySlots, List<PlayerTeam.Packed> teams) {
      public static final Packed EMPTY = new Packed(List.of(), List.of(), Map.of(), List.of());
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Objective.Packed.CODEC.listOf().optionalFieldOf("Objectives", List.of()).forGetter(Packed::objectives), Scoreboard.PackedScore.CODEC.listOf().optionalFieldOf("PlayerScores", List.of()).forGetter(Packed::scores), Codec.unboundedMap(DisplaySlot.CODEC, Codec.STRING).optionalFieldOf("DisplaySlots", Map.of()).forGetter(Packed::displaySlots), PlayerTeam.Packed.CODEC.listOf().optionalFieldOf("Teams", List.of()).forGetter(Packed::teams)).apply(var0, Packed::new));

      public Packed(List<Objective.Packed> var1, List<Scoreboard.PackedScore> var2, Map<DisplaySlot, String> var3, List<PlayerTeam.Packed> var4) {
         super();
         this.objectives = var1;
         this.scores = var2;
         this.displaySlots = var3;
         this.teams = var4;
      }
   }
}
