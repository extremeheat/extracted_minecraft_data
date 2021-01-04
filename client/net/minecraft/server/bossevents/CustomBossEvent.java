package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class CustomBossEvent extends ServerBossEvent {
   private final ResourceLocation id;
   private final Set<UUID> players = Sets.newHashSet();
   private int value;
   private int max = 100;

   public CustomBossEvent(ResourceLocation var1, Component var2) {
      super(var2, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
      this.id = var1;
      this.setPercent(0.0F);
   }

   public ResourceLocation getTextId() {
      return this.id;
   }

   public void addPlayer(ServerPlayer var1) {
      super.addPlayer(var1);
      this.players.add(var1.getUUID());
   }

   public void addOfflinePlayer(UUID var1) {
      this.players.add(var1);
   }

   public void removePlayer(ServerPlayer var1) {
      super.removePlayer(var1);
      this.players.remove(var1.getUUID());
   }

   public void removeAllPlayers() {
      super.removeAllPlayers();
      this.players.clear();
   }

   public int getValue() {
      return this.value;
   }

   public int getMax() {
      return this.max;
   }

   public void setValue(int var1) {
      this.value = var1;
      this.setPercent(Mth.clamp((float)var1 / (float)this.max, 0.0F, 1.0F));
   }

   public void setMax(int var1) {
      this.max = var1;
      this.setPercent(Mth.clamp((float)this.value / (float)var1, 0.0F, 1.0F));
   }

   public final Component getDisplayName() {
      return ComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((var1) -> {
         var1.setColor(this.getColor().getFormatting()).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.getTextId().toString()))).setInsertion(this.getTextId().toString());
      });
   }

   public boolean setPlayers(Collection<ServerPlayer> var1) {
      HashSet var2 = Sets.newHashSet();
      HashSet var3 = Sets.newHashSet();
      Iterator var4 = this.players.iterator();

      UUID var5;
      boolean var6;
      Iterator var7;
      while(var4.hasNext()) {
         var5 = (UUID)var4.next();
         var6 = false;
         var7 = var1.iterator();

         while(var7.hasNext()) {
            ServerPlayer var8 = (ServerPlayer)var7.next();
            if (var8.getUUID().equals(var5)) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            var2.add(var5);
         }
      }

      var4 = var1.iterator();

      ServerPlayer var9;
      while(var4.hasNext()) {
         var9 = (ServerPlayer)var4.next();
         var6 = false;
         var7 = this.players.iterator();

         while(var7.hasNext()) {
            UUID var12 = (UUID)var7.next();
            if (var9.getUUID().equals(var12)) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            var3.add(var9);
         }
      }

      for(var4 = var2.iterator(); var4.hasNext(); this.players.remove(var5)) {
         var5 = (UUID)var4.next();
         Iterator var11 = this.getPlayers().iterator();

         while(var11.hasNext()) {
            ServerPlayer var10 = (ServerPlayer)var11.next();
            if (var10.getUUID().equals(var5)) {
               this.removePlayer(var10);
               break;
            }
         }
      }

      var4 = var3.iterator();

      while(var4.hasNext()) {
         var9 = (ServerPlayer)var4.next();
         this.addPlayer(var9);
      }

      return !var2.isEmpty() || !var3.isEmpty();
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", Component.Serializer.toJson(this.name));
      var1.putBoolean("Visible", this.isVisible());
      var1.putInt("Value", this.value);
      var1.putInt("Max", this.max);
      var1.putString("Color", this.getColor().getName());
      var1.putString("Overlay", this.getOverlay().getName());
      var1.putBoolean("DarkenScreen", this.shouldDarkenScreen());
      var1.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
      var1.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
      ListTag var2 = new ListTag();
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         UUID var4 = (UUID)var3.next();
         var2.add(NbtUtils.createUUIDTag(var4));
      }

      var1.put("Players", var2);
      return var1;
   }

   public static CustomBossEvent load(CompoundTag var0, ResourceLocation var1) {
      CustomBossEvent var2 = new CustomBossEvent(var1, Component.Serializer.fromJson(var0.getString("Name")));
      var2.setVisible(var0.getBoolean("Visible"));
      var2.setValue(var0.getInt("Value"));
      var2.setMax(var0.getInt("Max"));
      var2.setColor(BossEvent.BossBarColor.byName(var0.getString("Color")));
      var2.setOverlay(BossEvent.BossBarOverlay.byName(var0.getString("Overlay")));
      var2.setDarkenScreen(var0.getBoolean("DarkenScreen"));
      var2.setPlayBossMusic(var0.getBoolean("PlayBossMusic"));
      var2.setCreateWorldFog(var0.getBoolean("CreateWorldFog"));
      ListTag var3 = var0.getList("Players", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         var2.addOfflinePlayer(NbtUtils.loadUUIDTag(var3.getCompound(var4)));
      }

      return var2;
   }

   public void onPlayerConnect(ServerPlayer var1) {
      if (this.players.contains(var1.getUUID())) {
         this.addPlayer(var1);
      }

   }

   public void onPlayerDisconnect(ServerPlayer var1) {
      super.removePlayer(var1);
   }
}
