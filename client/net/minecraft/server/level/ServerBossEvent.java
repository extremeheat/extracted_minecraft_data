package net.minecraft.server.level;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class ServerBossEvent extends BossEvent {
   private final Set<ServerPlayer> players = Sets.newHashSet();
   private final Set<ServerPlayer> unmodifiablePlayers;
   private boolean visible;

   public ServerBossEvent(Component var1, BossEvent.BossBarColor var2, BossEvent.BossBarOverlay var3) {
      super(Mth.createInsecureUUID(), var1, var2, var3);
      this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
      this.visible = true;
   }

   public void setProgress(float var1) {
      if (var1 != this.progress) {
         super.setProgress(var1);
         this.broadcast(ClientboundBossEventPacket::createUpdateProgressPacket);
      }

   }

   public void setColor(BossEvent.BossBarColor var1) {
      if (var1 != this.color) {
         super.setColor(var1);
         this.broadcast(ClientboundBossEventPacket::createUpdateStylePacket);
      }

   }

   public void setOverlay(BossEvent.BossBarOverlay var1) {
      if (var1 != this.overlay) {
         super.setOverlay(var1);
         this.broadcast(ClientboundBossEventPacket::createUpdateStylePacket);
      }

   }

   public BossEvent setDarkenScreen(boolean var1) {
      if (var1 != this.darkenScreen) {
         super.setDarkenScreen(var1);
         this.broadcast(ClientboundBossEventPacket::createUpdatePropertiesPacket);
      }

      return this;
   }

   public BossEvent setPlayBossMusic(boolean var1) {
      if (var1 != this.playBossMusic) {
         super.setPlayBossMusic(var1);
         this.broadcast(ClientboundBossEventPacket::createUpdatePropertiesPacket);
      }

      return this;
   }

   public BossEvent setCreateWorldFog(boolean var1) {
      if (var1 != this.createWorldFog) {
         super.setCreateWorldFog(var1);
         this.broadcast(ClientboundBossEventPacket::createUpdatePropertiesPacket);
      }

      return this;
   }

   public void setName(Component var1) {
      if (!Objects.equal(var1, this.name)) {
         super.setName(var1);
         this.broadcast(ClientboundBossEventPacket::createUpdateNamePacket);
      }

   }

   private void broadcast(Function<BossEvent, ClientboundBossEventPacket> var1) {
      if (this.visible) {
         ClientboundBossEventPacket var2 = (ClientboundBossEventPacket)var1.apply(this);
         Iterator var3 = this.players.iterator();

         while(var3.hasNext()) {
            ServerPlayer var4 = (ServerPlayer)var3.next();
            var4.connection.send(var2);
         }
      }

   }

   public void addPlayer(ServerPlayer var1) {
      if (this.players.add(var1) && this.visible) {
         var1.connection.send(ClientboundBossEventPacket.createAddPacket(this));
      }

   }

   public void removePlayer(ServerPlayer var1) {
      if (this.players.remove(var1) && this.visible) {
         var1.connection.send(ClientboundBossEventPacket.createRemovePacket(this.getId()));
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
            var3.connection.send(var1 ? ClientboundBossEventPacket.createAddPacket(this) : ClientboundBossEventPacket.createRemovePacket(this.getId()));
         }
      }

   }

   public Collection<ServerPlayer> getPlayers() {
      return this.unmodifiablePlayers;
   }
}
