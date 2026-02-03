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

   public ScoreboardSaveData(final Packed data) {
      super();
      this.data = data;
   }

   public Packed getData() {
      return this.data;
   }

   public void setData(final Packed data) {
      if (!data.equals(this.data)) {
         this.data = data;
         this.setDirty();
      }

   }

   static {
      TYPE = new SavedDataType<ScoreboardSaveData>("scoreboard", ScoreboardSaveData::new, ScoreboardSaveData.Packed.CODEC.xmap(ScoreboardSaveData::new, ScoreboardSaveData::getData), DataFixTypes.SAVED_DATA_SCOREBOARD);
   }

   public static record Packed(List<Objective.Packed> objectives, List<Scoreboard.PackedScore> scores, Map<DisplaySlot, String> displaySlots, List<PlayerTeam.Packed> teams) {
      public static final Packed EMPTY = new Packed(List.of(), List.of(), Map.of(), List.of());
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((i) -> i.group(Objective.Packed.CODEC.listOf().optionalFieldOf("Objectives", List.of()).forGetter(Packed::objectives), Scoreboard.PackedScore.CODEC.listOf().optionalFieldOf("PlayerScores", List.of()).forGetter(Packed::scores), Codec.unboundedMap(DisplaySlot.CODEC, Codec.STRING).optionalFieldOf("DisplaySlots", Map.of()).forGetter(Packed::displaySlots), PlayerTeam.Packed.CODEC.listOf().optionalFieldOf("Teams", List.of()).forGetter(Packed::teams)).apply(i, Packed::new));

      public Packed {
         super();
      }
   }
}
