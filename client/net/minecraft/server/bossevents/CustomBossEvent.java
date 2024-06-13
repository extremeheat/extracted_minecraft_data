package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
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

   @Override
   public void addPlayer(ServerPlayer var1) {
      super.addPlayer(var1);
      this.players.add(var1.getUUID());
   }

   public void addOfflinePlayer(UUID var1) {
      this.players.add(var1);
   }

   @Override
   public void removePlayer(ServerPlayer var1) {
      super.removePlayer(var1);
      this.players.remove(var1.getUUID());
   }

   @Override
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
      return ComponentUtils.wrapInSquareBrackets(this.getName())
         .withStyle(
            var1 -> var1.withColor(this.getColor().getFormatting())
                  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(this.getTextId().toString())))
                  .withInsertion(this.getTextId().toString())
         );
   }

   public boolean setPlayers(Collection<ServerPlayer> var1) {
      HashSet var2 = Sets.newHashSet();
      HashSet var3 = Sets.newHashSet();

      for (UUID var5 : this.players) {
         boolean var6 = false;

         for (ServerPlayer var8 : var1) {
            if (var8.getUUID().equals(var5)) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            var2.add(var5);
         }
      }

      for (ServerPlayer var12 : var1) {
         boolean var15 = false;

         for (UUID var19 : this.players) {
            if (var12.getUUID().equals(var19)) {
               var15 = true;
               break;
            }
         }

         if (!var15) {
            var3.add(var12);
         }
      }

      for (UUID var13 : var2) {
         for (ServerPlayer var18 : this.getPlayers()) {
            if (var18.getUUID().equals(var13)) {
               this.removePlayer(var18);
               break;
            }
         }

         this.players.remove(var13);
      }

      for (ServerPlayer var14 : var3) {
         this.addPlayer(var14);
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

      for (UUID var5 : this.players) {
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

      for (Tag var6 : var0.getList("Players", 11)) {
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
