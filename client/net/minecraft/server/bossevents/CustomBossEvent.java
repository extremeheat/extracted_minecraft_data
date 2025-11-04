package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class CustomBossEvent extends ServerBossEvent {
   private static final int DEFAULT_MAX = 100;
   private final Identifier id;
   private final Set<UUID> players = Sets.newHashSet();
   private int value;
   private int max = 100;

   public CustomBossEvent(Identifier var1, Component var2) {
      super(var2, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
      this.id = var1;
      this.setProgress(0.0F);
   }

   public Identifier getTextId() {
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
      return ComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((UnaryOperator)((var1) -> var1.withColor(this.getColor().getFormatting()).withHoverEvent(new HoverEvent.ShowText(Component.literal(this.getTextId().toString()))).withInsertion(this.getTextId().toString())));
   }

   public boolean setPlayers(Collection<ServerPlayer> var1) {
      HashSet var2 = Sets.newHashSet();
      HashSet var3 = Sets.newHashSet();

      for(UUID var5 : this.players) {
         boolean var6 = false;

         for(ServerPlayer var8 : var1) {
            if (var8.getUUID().equals(var5)) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            var2.add(var5);
         }
      }

      for(ServerPlayer var12 : var1) {
         boolean var15 = false;

         for(UUID var19 : this.players) {
            if (var12.getUUID().equals(var19)) {
               var15 = true;
               break;
            }
         }

         if (!var15) {
            var3.add(var12);
         }
      }

      for(UUID var13 : var2) {
         for(ServerPlayer var18 : this.getPlayers()) {
            if (var18.getUUID().equals(var13)) {
               this.removePlayer(var18);
               break;
            }
         }

         this.players.remove(var13);
      }

      for(ServerPlayer var14 : var3) {
         this.addPlayer(var14);
      }

      return !var2.isEmpty() || !var3.isEmpty();
   }

   public static CustomBossEvent load(Identifier var0, Packed var1) {
      CustomBossEvent var2 = new CustomBossEvent(var0, var1.name);
      var2.setVisible(var1.visible);
      var2.setValue(var1.value);
      var2.setMax(var1.max);
      var2.setColor(var1.color);
      var2.setOverlay(var1.overlay);
      var2.setDarkenScreen(var1.darkenScreen);
      var2.setPlayBossMusic(var1.playBossMusic);
      var2.setCreateWorldFog(var1.createWorldFog);
      Set var10000 = var1.players;
      Objects.requireNonNull(var2);
      var10000.forEach(var2::addOfflinePlayer);
      return var2;
   }

   public Packed pack() {
      return new Packed(this.getName(), this.isVisible(), this.getValue(), this.getMax(), this.getColor(), this.getOverlay(), this.shouldDarkenScreen(), this.shouldPlayBossMusic(), this.shouldCreateWorldFog(), Set.copyOf(this.players));
   }

   public void onPlayerConnect(ServerPlayer var1) {
      if (this.players.contains(var1.getUUID())) {
         this.addPlayer(var1);
      }

   }

   public void onPlayerDisconnect(ServerPlayer var1) {
      super.removePlayer(var1);
   }

   public static record Packed(Component name, boolean visible, int value, int max, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay, boolean darkenScreen, boolean playBossMusic, boolean createWorldFog, Set<UUID> players) {
      final Component name;
      final boolean visible;
      final int value;
      final int max;
      final BossEvent.BossBarColor color;
      final BossEvent.BossBarOverlay overlay;
      final boolean darkenScreen;
      final boolean playBossMusic;
      final boolean createWorldFog;
      final Set<UUID> players;
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ComponentSerialization.CODEC.fieldOf("Name").forGetter(Packed::name), Codec.BOOL.optionalFieldOf("Visible", false).forGetter(Packed::visible), Codec.INT.optionalFieldOf("Value", 0).forGetter(Packed::value), Codec.INT.optionalFieldOf("Max", 100).forGetter(Packed::max), BossEvent.BossBarColor.CODEC.optionalFieldOf("Color", BossEvent.BossBarColor.WHITE).forGetter(Packed::color), BossEvent.BossBarOverlay.CODEC.optionalFieldOf("Overlay", BossEvent.BossBarOverlay.PROGRESS).forGetter(Packed::overlay), Codec.BOOL.optionalFieldOf("DarkenScreen", false).forGetter(Packed::darkenScreen), Codec.BOOL.optionalFieldOf("PlayBossMusic", false).forGetter(Packed::playBossMusic), Codec.BOOL.optionalFieldOf("CreateWorldFog", false).forGetter(Packed::createWorldFog), UUIDUtil.CODEC_SET.optionalFieldOf("Players", Set.of()).forGetter(Packed::players)).apply(var0, Packed::new));

      public Packed(Component var1, boolean var2, int var3, int var4, BossEvent.BossBarColor var5, BossEvent.BossBarOverlay var6, boolean var7, boolean var8, boolean var9, Set<UUID> var10) {
         super();
         this.name = var1;
         this.visible = var2;
         this.value = var3;
         this.max = var4;
         this.color = var5;
         this.overlay = var6;
         this.darkenScreen = var7;
         this.playBossMusic = var8;
         this.createWorldFog = var9;
         this.players = var10;
      }
   }
}
