package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class MapItemSavedData extends SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAP_SIZE = 128;
   private static final int HALF_MAP_SIZE = 64;
   public static final int MAX_SCALE = 4;
   public static final int TRACKED_DECORATION_LIMIT = 256;
   public final int x;
   public final int z;
   public final ResourceKey<Level> dimension;
   private final boolean trackingPosition;
   private final boolean unlimitedTracking;
   public final byte scale;
   public byte[] colors = new byte[16384];
   public final boolean locked;
   private final List<HoldingPlayer> carriedBy = Lists.newArrayList();
   private final Map<Player, HoldingPlayer> carriedByPlayers = Maps.newHashMap();
   private final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
   final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
   private final Map<String, MapFrame> frameMarkers = Maps.newHashMap();
   private int trackedDecorationCount;

   private MapItemSavedData(int var1, int var2, byte var3, boolean var4, boolean var5, boolean var6, ResourceKey<Level> var7) {
      super();
      this.scale = var3;
      this.x = var1;
      this.z = var2;
      this.dimension = var7;
      this.trackingPosition = var4;
      this.unlimitedTracking = var5;
      this.locked = var6;
      this.setDirty();
   }

   public static MapItemSavedData createFresh(double var0, double var2, byte var4, boolean var5, boolean var6, ResourceKey<Level> var7) {
      int var8 = 128 * (1 << var4);
      int var9 = Mth.floor((var0 + 64.0) / (double)var8);
      int var10 = Mth.floor((var2 + 64.0) / (double)var8);
      int var11 = var9 * var8 + var8 / 2 - 64;
      int var12 = var10 * var8 + var8 / 2 - 64;
      return new MapItemSavedData(var11, var12, var4, var5, var6, false, var7);
   }

   public static MapItemSavedData createForClient(byte var0, boolean var1, ResourceKey<Level> var2) {
      return new MapItemSavedData(0, 0, var0, false, false, var1, var2);
   }

   public static MapItemSavedData load(CompoundTag var0) {
      DataResult var10000 = DimensionType.parseLegacy(new Dynamic(NbtOps.INSTANCE, var0.get("dimension")));
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      ResourceKey var1 = (ResourceKey)var10000.resultOrPartial(var10001::error).orElseThrow(() -> {
         return new IllegalArgumentException("Invalid map dimension: " + var0.get("dimension"));
      });
      int var2 = var0.getInt("xCenter");
      int var3 = var0.getInt("zCenter");
      byte var4 = (byte)Mth.clamp((int)var0.getByte("scale"), (int)0, (int)4);
      boolean var5 = !var0.contains("trackingPosition", 1) || var0.getBoolean("trackingPosition");
      boolean var6 = var0.getBoolean("unlimitedTracking");
      boolean var7 = var0.getBoolean("locked");
      MapItemSavedData var8 = new MapItemSavedData(var2, var3, var4, var5, var6, var7, var1);
      byte[] var9 = var0.getByteArray("colors");
      if (var9.length == 16384) {
         var8.colors = var9;
      }

      ListTag var10 = var0.getList("banners", 10);

      for(int var11 = 0; var11 < var10.size(); ++var11) {
         MapBanner var12 = MapBanner.load(var10.getCompound(var11));
         var8.bannerMarkers.put(var12.getId(), var12);
         var8.addDecoration(var12.getDecoration(), (LevelAccessor)null, var12.getId(), (double)var12.getPos().getX(), (double)var12.getPos().getZ(), 180.0, var12.getName());
      }

      ListTag var14 = var0.getList("frames", 10);

      for(int var15 = 0; var15 < var14.size(); ++var15) {
         MapFrame var13 = MapFrame.load(var14.getCompound(var15));
         var8.frameMarkers.put(var13.getId(), var13);
         var8.addDecoration(MapDecoration.Type.FRAME, (LevelAccessor)null, "frame-" + var13.getEntityId(), (double)var13.getPos().getX(), (double)var13.getPos().getZ(), (double)var13.getRotation(), (Component)null);
      }

      return var8;
   }

   public CompoundTag save(CompoundTag var1) {
      DataResult var10000 = ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.dimension.location());
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
         var1.put("dimension", var1x);
      });
      var1.putInt("xCenter", this.x);
      var1.putInt("zCenter", this.z);
      var1.putByte("scale", this.scale);
      var1.putByteArray("colors", this.colors);
      var1.putBoolean("trackingPosition", this.trackingPosition);
      var1.putBoolean("unlimitedTracking", this.unlimitedTracking);
      var1.putBoolean("locked", this.locked);
      ListTag var2 = new ListTag();
      Iterator var3 = this.bannerMarkers.values().iterator();

      while(var3.hasNext()) {
         MapBanner var4 = (MapBanner)var3.next();
         var2.add(var4.save());
      }

      var1.put("banners", var2);
      ListTag var6 = new ListTag();
      Iterator var7 = this.frameMarkers.values().iterator();

      while(var7.hasNext()) {
         MapFrame var5 = (MapFrame)var7.next();
         var6.add(var5.save());
      }

      var1.put("frames", var6);
      return var1;
   }

   public MapItemSavedData locked() {
      MapItemSavedData var1 = new MapItemSavedData(this.x, this.z, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);
      var1.bannerMarkers.putAll(this.bannerMarkers);
      var1.decorations.putAll(this.decorations);
      var1.trackedDecorationCount = this.trackedDecorationCount;
      System.arraycopy(this.colors, 0, var1.colors, 0, this.colors.length);
      var1.setDirty();
      return var1;
   }

   public MapItemSavedData scaled(int var1) {
      return createFresh((double)this.x, (double)this.z, (byte)Mth.clamp((int)(this.scale + var1), (int)0, (int)4), this.trackingPosition, this.unlimitedTracking, this.dimension);
   }

   public void tickCarriedBy(Player var1, ItemStack var2) {
      if (!this.carriedByPlayers.containsKey(var1)) {
         HoldingPlayer var3 = new HoldingPlayer(var1);
         this.carriedByPlayers.put(var1, var3);
         this.carriedBy.add(var3);
      }

      if (!var1.getInventory().contains(var2)) {
         this.removeDecoration(var1.getName().getString());
      }

      for(int var7 = 0; var7 < this.carriedBy.size(); ++var7) {
         HoldingPlayer var4 = (HoldingPlayer)this.carriedBy.get(var7);
         String var5 = var4.player.getName().getString();
         if (!var4.player.isRemoved() && (var4.player.getInventory().contains(var2) || var2.isFramed())) {
            if (!var2.isFramed() && var4.player.level.dimension() == this.dimension && this.trackingPosition) {
               this.addDecoration(MapDecoration.Type.PLAYER, var4.player.level, var5, var4.player.getX(), var4.player.getZ(), (double)var4.player.getYRot(), (Component)null);
            }
         } else {
            this.carriedByPlayers.remove(var4.player);
            this.carriedBy.remove(var4);
            this.removeDecoration(var5);
         }
      }

      if (var2.isFramed() && this.trackingPosition) {
         ItemFrame var8 = var2.getFrame();
         BlockPos var9 = var8.getPos();
         MapFrame var12 = (MapFrame)this.frameMarkers.get(MapFrame.frameId(var9));
         if (var12 != null && var8.getId() != var12.getEntityId() && this.frameMarkers.containsKey(var12.getId())) {
            this.removeDecoration("frame-" + var12.getEntityId());
         }

         MapFrame var6 = new MapFrame(var9, var8.getDirection().get2DDataValue() * 90, var8.getId());
         this.addDecoration(MapDecoration.Type.FRAME, var1.level, "frame-" + var8.getId(), (double)var9.getX(), (double)var9.getZ(), (double)(var8.getDirection().get2DDataValue() * 90), (Component)null);
         this.frameMarkers.put(var6.getId(), var6);
      }

      CompoundTag var10 = var2.getTag();
      if (var10 != null && var10.contains("Decorations", 9)) {
         ListTag var11 = var10.getList("Decorations", 10);

         for(int var13 = 0; var13 < var11.size(); ++var13) {
            CompoundTag var14 = var11.getCompound(var13);
            if (!this.decorations.containsKey(var14.getString("id"))) {
               this.addDecoration(MapDecoration.Type.byIcon(var14.getByte("type")), var1.level, var14.getString("id"), var14.getDouble("x"), var14.getDouble("z"), var14.getDouble("rot"), (Component)null);
            }
         }
      }

   }

   private void removeDecoration(String var1) {
      MapDecoration var2 = (MapDecoration)this.decorations.remove(var1);
      if (var2 != null && var2.getType().shouldTrackCount()) {
         --this.trackedDecorationCount;
      }

      this.setDecorationsDirty();
   }

   public static void addTargetDecoration(ItemStack var0, BlockPos var1, String var2, MapDecoration.Type var3) {
      ListTag var4;
      if (var0.hasTag() && var0.getTag().contains("Decorations", 9)) {
         var4 = var0.getTag().getList("Decorations", 10);
      } else {
         var4 = new ListTag();
         var0.addTagElement("Decorations", var4);
      }

      CompoundTag var5 = new CompoundTag();
      var5.putByte("type", var3.getIcon());
      var5.putString("id", var2);
      var5.putDouble("x", (double)var1.getX());
      var5.putDouble("z", (double)var1.getZ());
      var5.putDouble("rot", 180.0);
      var4.add(var5);
      if (var3.hasMapColor()) {
         CompoundTag var6 = var0.getOrCreateTagElement("display");
         var6.putInt("MapColor", var3.getMapColor());
      }

   }

   private void addDecoration(MapDecoration.Type var1, @Nullable LevelAccessor var2, String var3, double var4, double var6, double var8, @Nullable Component var10) {
      int var11 = 1 << this.scale;
      float var12 = (float)(var4 - (double)this.x) / (float)var11;
      float var13 = (float)(var6 - (double)this.z) / (float)var11;
      byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5));
      byte var15 = (byte)((int)((double)(var13 * 2.0F) + 0.5));
      boolean var17 = true;
      byte var16;
      if (var12 >= -63.0F && var13 >= -63.0F && var12 <= 63.0F && var13 <= 63.0F) {
         var8 += var8 < 0.0 ? -8.0 : 8.0;
         var16 = (byte)((int)(var8 * 16.0 / 360.0));
         if (this.dimension == Level.NETHER && var2 != null) {
            int var20 = (int)(var2.getLevelData().getDayTime() / 10L);
            var16 = (byte)(var20 * var20 * 34187121 + var20 * 121 >> 15 & 15);
         }
      } else {
         if (var1 != MapDecoration.Type.PLAYER) {
            this.removeDecoration(var3);
            return;
         }

         boolean var18 = true;
         if (Math.abs(var12) < 320.0F && Math.abs(var13) < 320.0F) {
            var1 = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if (!this.unlimitedTracking) {
               this.removeDecoration(var3);
               return;
            }

            var1 = MapDecoration.Type.PLAYER_OFF_LIMITS;
         }

         var16 = 0;
         if (var12 <= -63.0F) {
            var14 = -128;
         }

         if (var13 <= -63.0F) {
            var15 = -128;
         }

         if (var12 >= 63.0F) {
            var14 = 127;
         }

         if (var13 >= 63.0F) {
            var15 = 127;
         }
      }

      MapDecoration var21 = new MapDecoration(var1, var14, var15, var16, var10);
      MapDecoration var19 = (MapDecoration)this.decorations.put(var3, var21);
      if (!var21.equals(var19)) {
         if (var19 != null && var19.getType().shouldTrackCount()) {
            --this.trackedDecorationCount;
         }

         if (var1.shouldTrackCount()) {
            ++this.trackedDecorationCount;
         }

         this.setDecorationsDirty();
      }

   }

   @Nullable
   public Packet<?> getUpdatePacket(int var1, Player var2) {
      HoldingPlayer var3 = (HoldingPlayer)this.carriedByPlayers.get(var2);
      return var3 == null ? null : var3.nextUpdatePacket(var1);
   }

   private void setColorsDirty(int var1, int var2) {
      this.setDirty();
      Iterator var3 = this.carriedBy.iterator();

      while(var3.hasNext()) {
         HoldingPlayer var4 = (HoldingPlayer)var3.next();
         var4.markColorsDirty(var1, var2);
      }

   }

   private void setDecorationsDirty() {
      this.setDirty();
      this.carriedBy.forEach(HoldingPlayer::markDecorationsDirty);
   }

   public HoldingPlayer getHoldingPlayer(Player var1) {
      HoldingPlayer var2 = (HoldingPlayer)this.carriedByPlayers.get(var1);
      if (var2 == null) {
         var2 = new HoldingPlayer(var1);
         this.carriedByPlayers.put(var1, var2);
         this.carriedBy.add(var2);
      }

      return var2;
   }

   public boolean toggleBanner(LevelAccessor var1, BlockPos var2) {
      double var3 = (double)var2.getX() + 0.5;
      double var5 = (double)var2.getZ() + 0.5;
      int var7 = 1 << this.scale;
      double var8 = (var3 - (double)this.x) / (double)var7;
      double var10 = (var5 - (double)this.z) / (double)var7;
      boolean var12 = true;
      if (var8 >= -63.0 && var10 >= -63.0 && var8 <= 63.0 && var10 <= 63.0) {
         MapBanner var13 = MapBanner.fromWorld(var1, var2);
         if (var13 == null) {
            return false;
         }

         if (this.bannerMarkers.remove(var13.getId(), var13)) {
            this.removeDecoration(var13.getId());
            return true;
         }

         if (!this.isTrackedCountOverLimit(256)) {
            this.bannerMarkers.put(var13.getId(), var13);
            this.addDecoration(var13.getDecoration(), var1, var13.getId(), var3, var5, 180.0, var13.getName());
            return true;
         }
      }

      return false;
   }

   public void checkBanners(BlockGetter var1, int var2, int var3) {
      Iterator var4 = this.bannerMarkers.values().iterator();

      while(var4.hasNext()) {
         MapBanner var5 = (MapBanner)var4.next();
         if (var5.getPos().getX() == var2 && var5.getPos().getZ() == var3) {
            MapBanner var6 = MapBanner.fromWorld(var1, var5.getPos());
            if (!var5.equals(var6)) {
               var4.remove();
               this.removeDecoration(var5.getId());
            }
         }
      }

   }

   public Collection<MapBanner> getBanners() {
      return this.bannerMarkers.values();
   }

   public void removedFromFrame(BlockPos var1, int var2) {
      this.removeDecoration("frame-" + var2);
      this.frameMarkers.remove(MapFrame.frameId(var1));
   }

   public boolean updateColor(int var1, int var2, byte var3) {
      byte var4 = this.colors[var1 + var2 * 128];
      if (var4 != var3) {
         this.setColor(var1, var2, var3);
         return true;
      } else {
         return false;
      }
   }

   public void setColor(int var1, int var2, byte var3) {
      this.colors[var1 + var2 * 128] = var3;
      this.setColorsDirty(var1, var2);
   }

   public boolean isExplorationMap() {
      Iterator var1 = this.decorations.values().iterator();

      MapDecoration var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (MapDecoration)var1.next();
      } while(var2.getType() != MapDecoration.Type.MANSION && var2.getType() != MapDecoration.Type.MONUMENT);

      return true;
   }

   public void addClientSideDecorations(List<MapDecoration> var1) {
      this.decorations.clear();
      this.trackedDecorationCount = 0;

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         MapDecoration var3 = (MapDecoration)var1.get(var2);
         this.decorations.put("icon-" + var2, var3);
         if (var3.getType().shouldTrackCount()) {
            ++this.trackedDecorationCount;
         }
      }

   }

   public Iterable<MapDecoration> getDecorations() {
      return this.decorations.values();
   }

   public boolean isTrackedCountOverLimit(int var1) {
      return this.trackedDecorationCount >= var1;
   }

   public class HoldingPlayer {
      public final Player player;
      private boolean dirtyData = true;
      private int minDirtyX;
      private int minDirtyY;
      private int maxDirtyX = 127;
      private int maxDirtyY = 127;
      private boolean dirtyDecorations = true;
      private int tick;
      public int step;

      HoldingPlayer(Player var2) {
         super();
         this.player = var2;
      }

      private MapPatch createPatch() {
         int var1 = this.minDirtyX;
         int var2 = this.minDirtyY;
         int var3 = this.maxDirtyX + 1 - this.minDirtyX;
         int var4 = this.maxDirtyY + 1 - this.minDirtyY;
         byte[] var5 = new byte[var3 * var4];

         for(int var6 = 0; var6 < var3; ++var6) {
            for(int var7 = 0; var7 < var4; ++var7) {
               var5[var6 + var7 * var3] = MapItemSavedData.this.colors[var1 + var6 + (var2 + var7) * 128];
            }
         }

         return new MapPatch(var1, var2, var3, var4, var5);
      }

      @Nullable
      Packet<?> nextUpdatePacket(int var1) {
         MapPatch var2;
         if (this.dirtyData) {
            this.dirtyData = false;
            var2 = this.createPatch();
         } else {
            var2 = null;
         }

         Collection var3;
         if (this.dirtyDecorations && this.tick++ % 5 == 0) {
            this.dirtyDecorations = false;
            var3 = MapItemSavedData.this.decorations.values();
         } else {
            var3 = null;
         }

         return var3 == null && var2 == null ? null : new ClientboundMapItemDataPacket(var1, MapItemSavedData.this.scale, MapItemSavedData.this.locked, var3, var2);
      }

      void markColorsDirty(int var1, int var2) {
         if (this.dirtyData) {
            this.minDirtyX = Math.min(this.minDirtyX, var1);
            this.minDirtyY = Math.min(this.minDirtyY, var2);
            this.maxDirtyX = Math.max(this.maxDirtyX, var1);
            this.maxDirtyY = Math.max(this.maxDirtyY, var2);
         } else {
            this.dirtyData = true;
            this.minDirtyX = var1;
            this.minDirtyY = var2;
            this.maxDirtyX = var1;
            this.maxDirtyY = var2;
         }

      }

      private void markDecorationsDirty() {
         this.dirtyDecorations = true;
      }
   }

   public static class MapPatch {
      public final int startX;
      public final int startY;
      public final int width;
      public final int height;
      public final byte[] mapColors;

      public MapPatch(int var1, int var2, int var3, int var4, byte[] var5) {
         super();
         this.startX = var1;
         this.startY = var2;
         this.width = var3;
         this.height = var4;
         this.mapColors = var5;
      }

      public void applyToMap(MapItemSavedData var1) {
         for(int var2 = 0; var2 < this.width; ++var2) {
            for(int var3 = 0; var3 < this.height; ++var3) {
               var1.setColor(this.startX + var2, this.startY + var3, this.mapColors[var2 + var3 * this.width]);
            }
         }

      }
   }
}
