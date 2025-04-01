package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class MineData extends SavedData {
   private static final String LEVEL_MINE_FILE_ID = "mine_data";
   public static final Codec<MineData> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.fieldOf("is_mine").forGetter((var0x) -> var0x.isMine), Codec.BOOL.fieldOf("has_placed_start_structures").forGetter((var0x) -> var0x.hasPlacedStartStructures), StringRepresentable.fromEnum(MineState::values).fieldOf("mine_state").forGetter((var0x) -> var0x.mineState), Codec.INT.fieldOf("leave_countdown").forGetter((var0x) -> var0x.leaveCountdown), Codec.INT.fieldOf("experience_to_drop").forGetter((var0x) -> var0x.experienceToDrop), Codec.INT.fieldOf("keys_to_roll").forGetter((var0x) -> var0x.keysToRoll), BlockPos.CODEC.optionalFieldOf("travelling_block_activated").forGetter((var0x) -> var0x.travellingBlockActivated), Codec.list(UUIDUtil.CODEC).fieldOf("dead_players").forGetter((var0x) -> var0x.deadPlayers)).apply(var0, MineData::new));
   public static final SavedDataType<MineData> TYPE;
   public static final int LEAVE_COUNTDOWN = 200;
   private boolean isMine;
   private boolean hasPlacedStartStructures;
   private MineState mineState;
   private int leaveCountdown;
   private int experienceToDrop;
   private int keysToRoll;
   private Optional<BlockPos> travellingBlockActivated;
   private List<UUID> deadPlayers;

   public MineData() {
      super();
      this.mineState = MineData.MineState.ONGOING;
      this.leaveCountdown = 200;
      this.experienceToDrop = 0;
      this.keysToRoll = 0;
      this.travellingBlockActivated = Optional.empty();
      this.deadPlayers = new ArrayList();
      this.setDirty();
   }

   private MineData(boolean var1, boolean var2, MineState var3, int var4, int var5, int var6, Optional<BlockPos> var7, List<UUID> var8) {
      super();
      this.mineState = MineData.MineState.ONGOING;
      this.leaveCountdown = 200;
      this.experienceToDrop = 0;
      this.keysToRoll = 0;
      this.travellingBlockActivated = Optional.empty();
      this.deadPlayers = new ArrayList();
      this.isMine = var1;
      this.hasPlacedStartStructures = var2;
      this.mineState = var3;
      this.leaveCountdown = var4;
      this.keysToRoll = var6;
      this.travellingBlockActivated = var7;
      this.experienceToDrop = var5;
      this.deadPlayers.addAll(var8);
   }

   public boolean isMine() {
      return this.isMine;
   }

   public void setMine(boolean var1) {
      this.isMine = var1;
      this.setDirty();
   }

   public boolean hasPlacedStartStructures() {
      return this.hasPlacedStartStructures;
   }

   public void setHasPlacedStartStructures(boolean var1) {
      this.hasPlacedStartStructures = var1;
      this.setDirty();
   }

   public MineState getMineState() {
      return this.mineState;
   }

   public void setMineState(MineState var1) {
      this.mineState = var1;
      this.setDirty();
   }

   public int getLeaveCountdown() {
      return this.leaveCountdown;
   }

   public void resetCountdown() {
      this.setDirty();
      this.leaveCountdown = 200;
   }

   public boolean countDown() {
      this.setDirty();
      return --this.leaveCountdown <= 0;
   }

   public boolean toggledMineTravellingBlock(BlockPos var1) {
      this.setDirty();
      if (this.travellingBlockActivated.isPresent()) {
         this.travellingBlockActivated = Optional.empty();
         this.resetCountdown();
         return false;
      } else {
         this.travellingBlockActivated = Optional.of(var1);
         return true;
      }
   }

   public void resetMineTravvelingBlock() {
      this.setDirty();
      this.travellingBlockActivated = Optional.empty();
      this.resetCountdown();
   }

   public int getExperienceToDrop() {
      return this.experienceToDrop;
   }

   public void addExperienceToDrop(int var1) {
      this.experienceToDrop += var1;
   }

   public int getKeysToRoll() {
      return this.keysToRoll;
   }

   public void addKeysToRoll(int var1) {
      this.keysToRoll += var1;
   }

   public void resetKeysToRoll() {
      this.keysToRoll = 0;
   }

   public void resetExperienceToDrop() {
      this.experienceToDrop = 0;
   }

   public Optional<BlockPos> getTravellingBlockActivated() {
      return this.travellingBlockActivated;
   }

   public void tick(ServerLevel var1) {
   }

   public void addDeadPlayer(UUID var1) {
      if (!this.deadPlayers.contains(var1)) {
         this.deadPlayers.add(var1);
      }

   }

   public boolean hasPlayerDied(UUID var1) {
      return this.deadPlayers.contains(var1);
   }

   public static MineData load(CompoundTag var0) {
      return (MineData)CODEC.parse(NbtOps.INSTANCE, var0).resultOrPartial().orElseGet(MineData::new);
   }

   static {
      TYPE = new SavedDataType<MineData>("mine_data", MineData::new, CODEC, DataFixTypes.SAVED_DATA_MINE_PROGRESS);
   }

   public static enum MineState implements StringRepresentable {
      ONGOING,
      WON,
      FAILED;

      private MineState() {
      }

      public String getSerializedName() {
         return this.name();
      }

      // $FF: synthetic method
      private static MineState[] $values() {
         return new MineState[]{ONGOING, WON, FAILED};
      }
   }
}
