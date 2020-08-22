package net.minecraft.server.level;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class ServerBossEvent extends BossEvent {
   private final Set players = Sets.newHashSet();
   private final Set unmodifiablePlayers;
   private boolean visible;

   public ServerBossEvent(Component var1, BossEvent.BossBarColor var2, BossEvent.BossBarOverlay var3) {
      super(Mth.createInsecureUUID(), var1, var2, var3);
      this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
      this.visible = true;
   }

   public void setPercent(float var1) {
      if (var1 != this.percent) {
         super.setPercent(var1);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_PCT);
      }

   }

   public void setColor(BossEvent.BossBarColor var1) {
      if (var1 != this.color) {
         super.setColor(var1);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_STYLE);
      }

   }

   public void setOverlay(BossEvent.BossBarOverlay var1) {
      if (var1 != this.overlay) {
         super.setOverlay(var1);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_STYLE);
      }

   }

   public BossEvent setDarkenScreen(boolean var1) {
      if (var1 != this.darkenScreen) {
         super.setDarkenScreen(var1);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossEvent setPlayBossMusic(boolean var1) {
      if (var1 != this.playBossMusic) {
         super.setPlayBossMusic(var1);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossEvent setCreateWorldFog(boolean var1) {
      if (var1 != this.createWorldFog) {
         super.setCreateWorldFog(var1);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public void setName(Component var1) {
      if (!Objects.equal(var1, this.name)) {
         super.setName(var1);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_NAME);
      }

   }

   private void broadcast(ClientboundBossEventPacket.Operation var1) {
      if (this.visible) {
         ClientboundBossEventPacket var2 = new ClientboundBossEventPacket(var1, this);
         Iterator var3 = this.players.iterator();

         while(var3.hasNext()) {
            ServerPlayer var4 = (ServerPlayer)var3.next();
            var4.connection.send(var2);
         }
      }

   }

   public void addPlayer(ServerPlayer var1) {
      if (this.players.add(var1) && this.visible) {
         var1.connection.send(new ClientboundBossEventPacket(ClientboundBossEventPacket.Operation.ADD, this));
      }

   }

   public void removePlayer(ServerPlayer var1) {
      if (this.players.remove(var1) && this.visible) {
         var1.connection.send(new ClientboundBossEventPacket(ClientboundBossEventPacket.Operation.REMOVE, this));
      }

   }

   public void removeAllPlayers() {
      if (!this.players.isEmpty()) {
         Iterator var1 = Lists.newArrayList(this.players).iterator();

         while(var1.hasNext()) {
            ServerPlayer var2 = (ServerPlayer)var1.next();
            this.removePlayer(var2);
         }
      }

   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      if (var1 != this.visible) {
         this.visible = var1;
         Iterator var2 = this.players.iterator();

         while(var2.hasNext()) {
            ServerPlayer var3 = (ServerPlayer)var2.next();
            var3.connection.send(new ClientboundBossEventPacket(var1 ? ClientboundBossEventPacket.Operation.ADD : ClientboundBossEventPacket.Operation.REMOVE, this));
         }
      }

   }

   public Collection getPlayers() {
      return this.unmodifiablePlayers;
   }
}
