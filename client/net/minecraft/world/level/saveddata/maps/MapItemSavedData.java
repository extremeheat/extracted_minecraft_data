package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapItemColor;
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
   public final int centerX;
   public final int centerZ;
   public final ResourceKey<Level> dimension;
   private final boolean trackingPosition;
   private final boolean unlimitedTracking;
   public final byte scale;
   public byte[] colors = new byte[16384];
   public final boolean locked;
   private final List<MapItemSavedData.HoldingPlayer> carriedBy = Lists.newArrayList();
   private final Map<Player, MapItemSavedData.HoldingPlayer> carriedByPlayers = Maps.newHashMap();
   private final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
   final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
   private final Map<String, MapFrame> frameMarkers = Maps.newHashMap();
   private int trackedDecorationCount;

   public static SavedData.Factory<MapItemSavedData> factory() {
      return new SavedData.Factory<>(() -> {
         throw new IllegalStateException("Should never create an empty map saved data");
      }, MapItemSavedData::load, DataFixTypes.SAVED_DATA_MAP_DATA);
   }

   private MapItemSavedData(int var1, int var2, byte var3, boolean var4, boolean var5, boolean var6, ResourceKey<Level> var7) {
      super();
      this.scale = var3;
      this.centerX = var1;
      this.centerZ = var2;
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

   public static MapItemSavedData load(CompoundTag var0, HolderLookup.Provider var1) {
      ResourceKey var2 = (ResourceKey)DimensionType.parseLegacy(new Dynamic(NbtOps.INSTANCE, var0.get("dimension")))
         .resultOrPartial(LOGGER::error)
         .orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + var0.get("dimension")));
      int var3 = var0.getInt("xCenter");
      int var4 = var0.getInt("zCenter");
      byte var5 = (byte)Mth.clamp(var0.getByte("scale"), 0, 4);
      boolean var6 = !var0.contains("trackingPosition", 1) || var0.getBoolean("trackingPosition");
      boolean var7 = var0.getBoolean("unlimitedTracking");
      boolean var8 = var0.getBoolean("locked");
      MapItemSavedData var9 = new MapItemSavedData(var3, var4, var5, var6, var7, var8, var2);
      byte[] var10 = var0.getByteArray("colors");
      if (var10.length == 16384) {
         var9.colors = var10;
      }

      RegistryOps var11 = var1.createSerializationContext(NbtOps.INSTANCE);

      for (MapBanner var14 : MapBanner.LIST_CODEC
         .parse(var11, var0.get("banners"))
         .resultOrPartial(var0x -> LOGGER.warn("Failed to parse map banner: '{}'", var0x))
         .orElse(List.of())) {
         var9.bannerMarkers.put(var14.getId(), var14);
         var9.addDecoration(
            var14.getDecoration(), null, var14.getId(), (double)var14.pos().getX(), (double)var14.pos().getZ(), 180.0, var14.name().orElse(null)
         );
      }

      ListTag var16 = var0.getList("frames", 10);

      for (int var17 = 0; var17 < var16.size(); var17++) {
         MapFrame var15 = MapFrame.load(var16.getCompound(var17));
         if (var15 != null) {
            var9.frameMarkers.put(var15.getId(), var15);
            var9.addDecoration(
               MapDecorationTypes.FRAME,
               null,
               "frame-" + var15.getEntityId(),
               (double)var15.getPos().getX(),
               (double)var15.getPos().getZ(),
               (double)var15.getRotation(),
               null
            );
         }
      }

      return var9;
   }

   @Override
   public CompoundTag save(CompoundTag var1, HolderLookup.Provider var2) {
      ResourceLocation.CODEC
         .encodeStart(NbtOps.INSTANCE, this.dimension.location())
         .resultOrPartial(LOGGER::error)
         .ifPresent(var1x -> var1.put("dimension", var1x));
      var1.putInt("xCenter", this.centerX);
      var1.putInt("zCenter", this.centerZ);
      var1.putByte("scale", this.scale);
      var1.putByteArray("colors", this.colors);
      var1.putBoolean("trackingPosition", this.trackingPosition);
      var1.putBoolean("unlimitedTracking", this.unlimitedTracking);
      var1.putBoolean("locked", this.locked);
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      var1.put("banners", (Tag)MapBanner.LIST_CODEC.encodeStart(var3, List.copyOf(this.bannerMarkers.values())).getOrThrow());
      ListTag var4 = new ListTag();

      for (MapFrame var6 : this.frameMarkers.values()) {
         var4.add(var6.save());
      }

      var1.put("frames", var4);
      return var1;
   }

   public MapItemSavedData locked() {
      MapItemSavedData var1 = new MapItemSavedData(this.centerX, this.centerZ, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);
      var1.bannerMarkers.putAll(this.bannerMarkers);
      var1.decorations.putAll(this.decorations);
      var1.trackedDecorationCount = this.trackedDecorationCount;
      System.arraycopy(this.colors, 0, var1.colors, 0, this.colors.length);
      var1.setDirty();
      return var1;
   }

   public MapItemSavedData scaled() {
      return createFresh(
         (double)this.centerX, (double)this.centerZ, (byte)Mth.clamp(this.scale + 1, 0, 4), this.trackingPosition, this.unlimitedTracking, this.dimension
      );
   }

   private static Predicate<ItemStack> mapMatcher(ItemStack var0) {
      MapId var1 = var0.get(DataComponents.MAP_ID);
      return var2 -> var2 == var0 ? true : var2.is(var0.getItem()) && Objects.equals(var1, var2.get(DataComponents.MAP_ID));
   }

   public void tickCarriedBy(Player var1, ItemStack var2) {
      if (!this.carriedByPlayers.containsKey(var1)) {
         MapItemSavedData.HoldingPlayer var3 = new MapItemSavedData.HoldingPlayer(var1);
         this.carriedByPlayers.put(var1, var3);
         this.carriedBy.add(var3);
      }

      Predicate var8 = mapMatcher(var2);
      if (!var1.getInventory().contains(var8)) {
         this.removeDecoration(var1.getName().getString());
      }

      for (int var4 = 0; var4 < this.carriedBy.size(); var4++) {
         MapItemSavedData.HoldingPlayer var5 = this.carriedBy.get(var4);
         String var6 = var5.player.getName().getString();
         if (!var5.player.isRemoved() && (var5.player.getInventory().contains(var8) || var2.isFramed())) {
            if (!var2.isFramed() && var5.player.level().dimension() == this.dimension && this.trackingPosition) {
               this.addDecoration(
                  MapDecorationTypes.PLAYER, var5.player.level(), var6, var5.player.getX(), var5.player.getZ(), (double)var5.player.getYRot(), null
               );
            }
         } else {
            this.carriedByPlayers.remove(var5.player);
            this.carriedBy.remove(var5);
            this.removeDecoration(var6);
         }
      }

      if (var2.isFramed() && this.trackingPosition) {
         ItemFrame var9 = var2.getFrame();
         BlockPos var11 = var9.getPos();
         MapFrame var12 = this.frameMarkers.get(MapFrame.frameId(var11));
         if (var12 != null && var9.getId() != var12.getEntityId() && this.frameMarkers.containsKey(var12.getId())) {
            this.removeDecoration("frame-" + var12.getEntityId());
         }

         MapFrame var7 = new MapFrame(var11, var9.getDirection().get2DDataValue() * 90, var9.getId());
         this.addDecoration(
            MapDecorationTypes.FRAME,
            var1.level(),
            "frame-" + var9.getId(),
            (double)var11.getX(),
            (double)var11.getZ(),
            (double)(var9.getDirection().get2DDataValue() * 90),
            null
         );
         this.frameMarkers.put(var7.getId(), var7);
      }

      MapDecorations var10 = var2.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY);
      if (!this.decorations.keySet().containsAll(var10.decorations().keySet())) {
         var10.decorations().forEach((var2x, var3x) -> {
            if (!this.decorations.containsKey(var2x)) {
               this.addDecoration(var3x.type(), var1.level(), var2x, var3x.x(), var3x.z(), (double)var3x.rotation(), null);
            }
         });
      }
   }

   private void removeDecoration(String var1) {
      MapDecoration var2 = this.decorations.remove(var1);
      if (var2 != null && var2.type().value().trackCount()) {
         this.trackedDecorationCount--;
      }

      this.setDecorationsDirty();
   }

   public static void addTargetDecoration(ItemStack var0, BlockPos var1, String var2, Holder<MapDecorationType> var3) {
      MapDecorations.Entry var4 = new MapDecorations.Entry(var3, (double)var1.getX(), (double)var1.getZ(), 180.0F);
      var0.update(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY, var2x -> var2x.withDecoration(var2, var4));
      if (((MapDecorationType)var3.value()).hasMapColor()) {
         var0.set(DataComponents.MAP_COLOR, new MapItemColor(((MapDecorationType)var3.value()).mapColor()));
      }
   }

   private void addDecoration(
      Holder<MapDecorationType> var1, @Nullable LevelAccessor var2, String var3, double var4, double var6, double var8, @Nullable Component var10
   ) {
      int var11 = 1 << this.scale;
      float var12 = (float)(var4 - (double)this.centerX) / (float)var11;
      float var13 = (float)(var6 - (double)this.centerZ) / (float)var11;
      byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5));
      byte var15 = (byte)((int)((double)(var13 * 2.0F) + 0.5));
      byte var17 = 63;
      byte var16;
      if (var12 >= -63.0F && var13 >= -63.0F && var12 <= 63.0F && var13 <= 63.0F) {
         var8 += var8 < 0.0 ? -8.0 : 8.0;
         var16 = (byte)((int)(var8 * 16.0 / 360.0));
         if (this.dimension == Level.NETHER && var2 != null) {
            int var21 = (int)(var2.getLevelData().getDayTime() / 10L);
            var16 = (byte)(var21 * var21 * 34187121 + var21 * 121 >> 15 & 15);
         }
      } else {
         if (!var1.is(MapDecorationTypes.PLAYER)) {
            this.removeDecoration(var3);
            return;
         }

         short var18 = 320;
         if (Math.abs(var12) < 320.0F && Math.abs(var13) < 320.0F) {
            var1 = MapDecorationTypes.PLAYER_OFF_MAP;
         } else {
            if (!this.unlimitedTracking) {
               this.removeDecoration(var3);
               return;
            }

            var1 = MapDecorationTypes.PLAYER_OFF_LIMITS;
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

      MapDecoration var22 = new MapDecoration(var1, var14, var15, var16, Optional.ofNullable(var10));
      MapDecoration var19 = this.decorations.put(var3, var22);
      if (!var22.equals(var19)) {
         if (var19 != null && var19.type().value().trackCount()) {
            this.trackedDecorationCount--;
         }

         if (((MapDecorationType)var1.value()).trackCount()) {
            this.trackedDecorationCount++;
         }

         this.setDecorationsDirty();
      }
   }

   @Nullable
   public Packet<?> getUpdatePacket(MapId var1, Player var2) {
      MapItemSavedData.HoldingPlayer var3 = this.carriedByPlayers.get(var2);
      return var3 == null ? null : var3.nextUpdatePacket(var1);
   }

   private void setColorsDirty(int var1, int var2) {
      this.setDirty();

      for (MapItemSavedData.HoldingPlayer var4 : this.carriedBy) {
         var4.markColorsDirty(var1, var2);
      }
   }

   private void setDecorationsDirty() {
      this.setDirty();
      this.carriedBy.forEach(MapItemSavedData.HoldingPlayer::markDecorationsDirty);
   }

   public MapItemSavedData.HoldingPlayer getHoldingPlayer(Player var1) {
      MapItemSavedData.HoldingPlayer var2 = this.carriedByPlayers.get(var1);
      if (var2 == null) {
         var2 = new MapItemSavedData.HoldingPlayer(var1);
         this.carriedByPlayers.put(var1, var2);
         this.carriedBy.add(var2);
      }

      return var2;
   }

   public boolean toggleBanner(LevelAccessor var1, BlockPos var2) {
      double var3 = (double)var2.getX() + 0.5;
      double var5 = (double)var2.getZ() + 0.5;
      int var7 = 1 << this.scale;
      double var8 = (var3 - (double)this.centerX) / (double)var7;
      double var10 = (var5 - (double)this.centerZ) / (double)var7;
      byte var12 = 63;
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
            this.addDecoration(var13.getDecoration(), var1, var13.getId(), var3, var5, 180.0, var13.name().orElse(null));
            return true;
         }
      }

      return false;
   }

   public void checkBanners(BlockGetter var1, int var2, int var3) {
      Iterator var4 = this.bannerMarkers.values().iterator();

      while (var4.hasNext()) {
         MapBanner var5 = (MapBanner)var4.next();
         if (var5.pos().getX() == var2 && var5.pos().getZ() == var3) {
            MapBanner var6 = MapBanner.fromWorld(var1, var5.pos());
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
      for (MapDecoration var2 : this.decorations.values()) {
         if (var2.type().value().explorationMapElement()) {
            return true;
         }
      }

      return false;
   }

   public void addClientSideDecorations(List<MapDecoration> var1) {
      this.decorations.clear();
      this.trackedDecorationCount = 0;

      for (int var2 = 0; var2 < var1.size(); var2++) {
         MapDecoration var3 = (MapDecoration)var1.get(var2);
         this.decorations.put("icon-" + var2, var3);
         if (var3.type().value().trackCount()) {
            this.trackedDecorationCount++;
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

      HoldingPlayer(final Player nullx) {
         super();
         this.player = nullx;
      }

      private MapItemSavedData.MapPatch createPatch() {
         int var1 = this.minDirtyX;
         int var2 = this.minDirtyY;
         int var3 = this.maxDirtyX + 1 - this.minDirtyX;
         int var4 = this.maxDirtyY + 1 - this.minDirtyY;
         byte[] var5 = new byte[var3 * var4];

         for (int var6 = 0; var6 < var3; var6++) {
            for (int var7 = 0; var7 < var4; var7++) {
               var5[var6 + var7 * var3] = MapItemSavedData.this.colors[var1 + var6 + (var2 + var7) * 128];
            }
         }

         return new MapItemSavedData.MapPatch(var1, var2, var3, var4, var5);
      }

      @Nullable
      Packet<?> nextUpdatePacket(MapId var1) {
         MapItemSavedData.MapPatch var2;
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

         return var3 == null && var2 == null
            ? null
            : new ClientboundMapItemDataPacket(var1, MapItemSavedData.this.scale, MapItemSavedData.this.locked, var3, var2);
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
