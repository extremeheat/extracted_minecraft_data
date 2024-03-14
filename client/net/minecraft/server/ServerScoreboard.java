package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardSaveData;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer server;
   private final Set<Objective> trackedObjectives = Sets.newHashSet();
   private final List<Runnable> dirtyListeners = Lists.newArrayList();

   public ServerScoreboard(MinecraftServer var1) {
      super();
      this.server = var1;
   }

   @Override
   protected void onScoreChanged(ScoreHolder var1, Objective var2, Score var3) {
      super.onScoreChanged(var1, var2, var3);
      if (this.trackedObjectives.contains(var2)) {
         this.server
            .getPlayerList()
            .broadcastAll(
               new ClientboundSetScorePacket(
                  var1.getScoreboardName(), var2.getName(), var3.value(), Optional.ofNullable(var3.display()), Optional.ofNullable(var3.numberFormat())
               )
            );
      }

      this.setDirty();
   }

   @Override
   protected void onScoreLockChanged(ScoreHolder var1, Objective var2) {
      super.onScoreLockChanged(var1, var2);
      this.setDirty();
   }

   @Override
   public void onPlayerRemoved(ScoreHolder var1) {
      super.onPlayerRemoved(var1);
      this.server.getPlayerList().broadcastAll(new ClientboundResetScorePacket(var1.getScoreboardName(), null));
      this.setDirty();
   }

   @Override
   public void onPlayerScoreRemoved(ScoreHolder var1, Objective var2) {
      super.onPlayerScoreRemoved(var1, var2);
      if (this.trackedObjectives.contains(var2)) {
         this.server.getPlayerList().broadcastAll(new ClientboundResetScorePacket(var1.getScoreboardName(), var2.getName()));
      }

      this.setDirty();
   }

   @Override
   public void setDisplayObjective(DisplaySlot var1, @Nullable Objective var2) {
      Objective var3 = this.getDisplayObjective(var1);
      super.setDisplayObjective(var1, var2);
      if (var3 != var2 && var3 != null) {
         if (this.getObjectiveDisplaySlotCount(var3) > 0) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(var1, var2));
         } else {
            this.stopTrackingObjective(var3);
         }
      }

      if (var2 != null) {
         if (this.trackedObjectives.contains(var2)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(var1, var2));
         } else {
            this.startTrackingObjective(var2);
         }
      }

      this.setDirty();
   }

   @Override
   public boolean addPlayerToTeam(String var1, PlayerTeam var2) {
      if (super.addPlayerToTeam(var1, var2)) {
         this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket(var2, var1, ClientboundSetPlayerTeamPacket.Action.ADD));
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removePlayerFromTeam(String var1, PlayerTeam var2) {
      super.removePlayerFromTeam(var1, var2);
      this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket(var2, var1, ClientboundSetPlayerTeamPacket.Action.REMOVE));
      this.setDirty();
   }

   @Override
   public void onObjectiveAdded(Objective var1) {
      super.onObjectiveAdded(var1);
      this.setDirty();
   }

   @Override
   public void onObjectiveChanged(Objective var1) {
      super.onObjectiveChanged(var1);
      if (this.trackedObjectives.contains(var1)) {
         this.server.getPlayerList().broadcastAll(new ClientboundSetObjectivePacket(var1, 2));
      }

      this.setDirty();
   }

   @Override
   public void onObjectiveRemoved(Objective var1) {
      super.onObjectiveRemoved(var1);
      if (this.trackedObjectives.contains(var1)) {
         this.stopTrackingObjective(var1);
      }

      this.setDirty();
   }

   @Override
   public void onTeamAdded(PlayerTeam var1) {
      super.onTeamAdded(var1);
      this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(var1, true));
      this.setDirty();
   }

   @Override
   public void onTeamChanged(PlayerTeam var1) {
      super.onTeamChanged(var1);
      this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(var1, false));
      this.setDirty();
   }

   @Override
   public void onTeamRemoved(PlayerTeam var1) {
      super.onTeamRemoved(var1);
      this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createRemovePacket(var1));
      this.setDirty();
   }

   public void addDirtyListener(Runnable var1) {
      this.dirtyListeners.add(var1);
   }

   protected void setDirty() {
      for(Runnable var2 : this.dirtyListeners) {
         var2.run();
      }
   }

   public List<Packet<?>> getStartTrackingPackets(Objective var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new ClientboundSetObjectivePacket(var1, 0));

      for(DisplaySlot var6 : DisplaySlot.values()) {
         if (this.getDisplayObjective(var6) == var1) {
            var2.add(new ClientboundSetDisplayObjectivePacket(var6, var1));
         }
      }

      for(PlayerScoreEntry var8 : this.listPlayerScores(var1)) {
         var2.add(
            new ClientboundSetScorePacket(
               var8.owner(), var1.getName(), var8.value(), Optional.ofNullable(var8.display()), Optional.ofNullable(var8.numberFormatOverride())
            )
         );
      }

      return var2;
   }

   public void startTrackingObjective(Objective var1) {
      List var2 = this.getStartTrackingPackets(var1);

      for(ServerPlayer var4 : this.server.getPlayerList().getPlayers()) {
         for(Packet var6 : var2) {
            var4.connection.send(var6);
         }
      }

      this.trackedObjectives.add(var1);
   }

   public List<Packet<?>> getStopTrackingPackets(Objective var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new ClientboundSetObjectivePacket(var1, 1));

      for(DisplaySlot var6 : DisplaySlot.values()) {
         if (this.getDisplayObjective(var6) == var1) {
            var2.add(new ClientboundSetDisplayObjectivePacket(var6, var1));
         }
      }

      return var2;
   }

   public void stopTrackingObjective(Objective var1) {
      List var2 = this.getStopTrackingPackets(var1);

      for(ServerPlayer var4 : this.server.getPlayerList().getPlayers()) {
         for(Packet var6 : var2) {
            var4.connection.send(var6);
         }
      }

      this.trackedObjectives.remove(var1);
   }

   public int getObjectiveDisplaySlotCount(Objective var1) {
      int var2 = 0;

      for(DisplaySlot var6 : DisplaySlot.values()) {
         if (this.getDisplayObjective(var6) == var1) {
            ++var2;
         }
      }

      return var2;
   }

   public SavedData.Factory<ScoreboardSaveData> dataFactory() {
      return new SavedData.Factory<>(this::createData, this::createData, DataFixTypes.SAVED_DATA_SCOREBOARD);
   }

   private ScoreboardSaveData createData() {
      ScoreboardSaveData var1 = new ScoreboardSaveData(this);
      this.addDirtyListener(var1::setDirty);
      return var1;
   }

   private ScoreboardSaveData createData(CompoundTag var1, HolderLookup.Provider var2) {
      return this.createData().load(var1, var2);
   }

   public static enum Method {
      CHANGE,
      REMOVE;

      private Method() {
      }
   }
}
