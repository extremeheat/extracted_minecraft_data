package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
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
      this.setProgress(0.0F);
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
      this.setProgress(Mth.clamp((float)var1 / (float)this.max, 0.0F, 1.0F));
   }

   public void setMax(int var1) {
      this.max = var1;
      this.setProgress(Mth.clamp((float)this.value / (float)var1, 0.0F, 1.0F));
   }

   public final Component getDisplayName() {
      return ComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((var1) -> {
         return var1.withColor(this.getColor().getFormatting()).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(this.getTextId().toString()))).withInsertion(this.getTextId().toString());
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

   public CompoundTag save(HolderLookup.Provider var1) {
      CompoundTag var2 = new CompoundTag();
      var2.putString("Name", Component.Serializer.toJson(this.name, var1));
      var2.putBoolean("Visible", this.isVisible());
      var2.putInt("Value", this.value);
      var2.putInt("Max", this.max);
      var2.putString("Color", this.getColor().getName());
      var2.putString("Overlay", this.getOverlay().getName());
      var2.putBoolean("DarkenScreen", this.shouldDarkenScreen());
      var2.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
      var2.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
      ListTag var3 = new ListTag();
      Iterator var4 = this.players.iterator();

      while(var4.hasNext()) {
         UUID var5 = (UUID)var4.next();
         var3.add(NbtUtils.createUUID(var5));
      }

      var2.put("Players", var3);
      return var2;
   }

   public static CustomBossEvent load(CompoundTag var0, ResourceLocation var1, HolderLookup.Provider var2) {
      CustomBossEvent var3 = new CustomBossEvent(var1, Component.Serializer.fromJson(var0.getString("Name"), var2));
      var3.setVisible(var0.getBoolean("Visible"));
      var3.setValue(var0.getInt("Value"));
      var3.setMax(var0.getInt("Max"));
      var3.setColor(BossEvent.BossBarColor.byName(var0.getString("Color")));
      var3.setOverlay(BossEvent.BossBarOverlay.byName(var0.getString("Overlay")));
      var3.setDarkenScreen(var0.getBoolean("DarkenScreen"));
      var3.setPlayBossMusic(var0.getBoolean("PlayBossMusic"));
      var3.setCreateWorldFog(var0.getBoolean("CreateWorldFog"));
      ListTag var4 = var0.getList("Players", 11);
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         Tag var6 = (Tag)var5.next();
         var3.addOfflinePlayer(NbtUtils.loadUUID(var6));
      }

      return var3;
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
