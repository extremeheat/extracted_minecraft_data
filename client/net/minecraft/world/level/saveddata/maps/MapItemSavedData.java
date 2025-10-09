package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.slf4j.Logger;

public class MapItemSavedData extends SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAP_SIZE = 128;
   private static final int HALF_MAP_SIZE = 64;
   public static final int MAX_SCALE = 4;
   public static final int TRACKED_DECORATION_LIMIT = 256;
   private static final String FRAME_PREFIX = "frame-";
   public static final Codec<MapItemSavedData> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter((var0x) -> var0x.dimension), Codec.INT.fieldOf("xCenter").forGetter((var0x) -> var0x.centerX), Codec.INT.fieldOf("zCenter").forGetter((var0x) -> var0x.centerZ), Codec.BYTE.optionalFieldOf("scale", (byte)0).forGetter((var0x) -> var0x.scale), Codec.BYTE_BUFFER.fieldOf("colors").forGetter((var0x) -> ByteBuffer.wrap(var0x.colors)), Codec.BOOL.optionalFieldOf("trackingPosition", true).forGetter((var0x) -> var0x.trackingPosition), Codec.BOOL.optionalFieldOf("unlimitedTracking", false).forGetter((var0x) -> var0x.unlimitedTracking), Codec.BOOL.optionalFieldOf("locked", false).forGetter((var0x) -> var0x.locked), MapBanner.CODEC.listOf().optionalFieldOf("banners", List.of()).forGetter((var0x) -> List.copyOf(var0x.bannerMarkers.values())), MapFrame.CODEC.listOf().optionalFieldOf("frames", List.of()).forGetter((var0x) -> List.copyOf(var0x.frameMarkers.values()))).apply(var0, MapItemSavedData::new));
   public final int centerX;
   public final int centerZ;
   public final ResourceKey<Level> dimension;
   private final boolean trackingPosition;
   private final boolean unlimitedTracking;
   public final byte scale;
   public byte[] colors;
   public final boolean locked;
   private final List<HoldingPlayer> carriedBy;
   private final Map<Player, HoldingPlayer> carriedByPlayers;
   private final Map<String, MapBanner> bannerMarkers;
   final Map<String, MapDecoration> decorations;
   private final Map<String, MapFrame> frameMarkers;
   private int trackedDecorationCount;

   public static SavedDataType<MapItemSavedData> type(MapId var0) {
      return new SavedDataType<MapItemSavedData>(var0.key(), () -> {
         throw new IllegalStateException("Should never create an empty map saved data");
      }, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
   }

   private MapItemSavedData(int var1, int var2, byte var3, boolean var4, boolean var5, boolean var6, ResourceKey<Level> var7) {
      super();
      this.colors = new byte[16384];
      this.carriedBy = Lists.newArrayList();
      this.carriedByPlayers = Maps.newHashMap();
      this.bannerMarkers = Maps.newHashMap();
      this.decorations = Maps.newLinkedHashMap();
      this.frameMarkers = Maps.newHashMap();
      this.scale = var3;
      this.centerX = var1;
      this.centerZ = var2;
      this.dimension = var7;
      this.trackingPosition = var4;
      this.unlimitedTracking = var5;
      this.locked = var6;
   }

   private MapItemSavedData(ResourceKey<Level> var1, int var2, int var3, byte var4, ByteBuffer var5, boolean var6, boolean var7, boolean var8, List<MapBanner> var9, List<MapFrame> var10) {
      this(var2, var3, (byte)Mth.clamp(var4, 0, 4), var6, var7, var8, var1);
      if (var5.array().length == 16384) {
         this.colors = var5.array();
      }

      for(MapBanner var12 : var9) {
         this.bannerMarkers.put(var12.getId(), var12);
         this.addDecoration(var12.getDecoration(), (LevelAccessor)null, var12.getId(), (double)var12.pos().getX(), (double)var12.pos().getZ(), 180.0, (Component)var12.name().orElse((Object)null));
      }

      for(MapFrame var14 : var10) {
         this.frameMarkers.put(var14.getId(), var14);
         this.addDecoration(MapDecorationTypes.FRAME, (LevelAccessor)null, getFrameKey(var14.entityId()), (double)var14.pos().getX(), (double)var14.pos().getZ(), (double)var14.rotation(), (Component)null);
      }

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

   public MapItemSavedData locked() {
      MapItemSavedData var1 = new MapItemSavedData(this.centerX, this.centerZ, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);
      var1.bannerMarkers.putAll(this.bannerMarkers);
      var1.decorations.putAll(this.decorations);
      var1.trackedDecorationCount = this.trackedDecorationCount;
      System.arraycopy(this.colors, 0, var1.colors, 0, this.colors.length);
      return var1;
   }

   public MapItemSavedData scaled() {
      return createFresh((double)this.centerX, (double)this.centerZ, (byte)Mth.clamp(this.scale + 1, 0, 4), this.trackingPosition, this.unlimitedTracking, this.dimension);
   }

   private static Predicate<ItemStack> mapMatcher(ItemStack var0) {
      MapId var1 = (MapId)var0.get(DataComponents.MAP_ID);
      return (var2) -> {
         if (var2 == var0) {
            return true;
         } else {
            return var2.is(var0.getItem()) && Objects.equals(var1, var2.get(DataComponents.MAP_ID));
         }
      };
   }

   public void tickCarriedBy(Player var1, ItemStack var2) {
      if (!this.carriedByPlayers.containsKey(var1)) {
         HoldingPlayer var3 = new HoldingPlayer(var1);
         this.carriedByPlayers.put(var1, var3);
         this.carriedBy.add(var3);
      }

      Predicate var9 = mapMatcher(var2);
      if (!var1.getInventory().contains(var9)) {
         this.removeDecoration(var1.getPlainTextName());
      }

      for(int var4 = 0; var4 < this.carriedBy.size(); ++var4) {
         HoldingPlayer var5 = (HoldingPlayer)this.carriedBy.get(var4);
         Player var6 = var5.player;
         String var7 = var6.getPlainTextName();
         if (!var6.isRemoved() && (var6.getInventory().contains(var9) || var2.isFramed())) {
            if (!var2.isFramed() && var6.level().dimension() == this.dimension && this.trackingPosition) {
               this.addDecoration(MapDecorationTypes.PLAYER, var6.level(), var7, var6.getX(), var6.getZ(), (double)var6.getYRot(), (Component)null);
            }
         } else {
            this.carriedByPlayers.remove(var6);
            this.carriedBy.remove(var5);
            this.removeDecoration(var7);
         }

         if (!var6.equals(var1) && hasMapInvisibilityItemEquipped(var6)) {
            this.removeDecoration(var7);
         }
      }

      if (var2.isFramed() && this.trackingPosition) {
         ItemFrame var10 = var2.getFrame();
         BlockPos var12 = var10.getPos();
         MapFrame var13 = (MapFrame)this.frameMarkers.get(MapFrame.frameId(var12));
         if (var13 != null && var10.getId() != var13.entityId() && this.frameMarkers.containsKey(var13.getId())) {
            this.removeDecoration(getFrameKey(var13.entityId()));
         }

         MapFrame var14 = new MapFrame(var12, var10.getDirection().get2DDataValue() * 90, var10.getId());
         this.addDecoration(MapDecorationTypes.FRAME, var1.level(), getFrameKey(var10.getId()), (double)var12.getX(), (double)var12.getZ(), (double)(var10.getDirection().get2DDataValue() * 90), (Component)null);
         MapFrame var8 = (MapFrame)this.frameMarkers.put(var14.getId(), var14);
         if (!var14.equals(var8)) {
            this.setDirty();
         }
      }

      MapDecorations var11 = (MapDecorations)var2.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY);
      if (!this.decorations.keySet().containsAll(var11.decorations().keySet())) {
         var11.decorations().forEach((var2x, var3x) -> {
            if (!this.decorations.containsKey(var2x)) {
               this.addDecoration(var3x.type(), var1.level(), var2x, var3x.x(), var3x.z(), (double)var3x.rotation(), (Component)null);
            }

         });
      }

   }

   private static boolean hasMapInvisibilityItemEquipped(Player var0) {
      for(EquipmentSlot var4 : EquipmentSlot.values()) {
         if (var4 != EquipmentSlot.MAINHAND && var4 != EquipmentSlot.OFFHAND && var0.getItemBySlot(var4).is(ItemTags.MAP_INVISIBILITY_EQUIPMENT)) {
            return true;
         }
      }

      return false;
   }

   private void removeDecoration(String var1) {
      MapDecoration var2 = (MapDecoration)this.decorations.remove(var1);
      if (var2 != null && ((MapDecorationType)var2.type().value()).trackCount()) {
         --this.trackedDecorationCount;
      }

      this.setDecorationsDirty();
   }

   public static void addTargetDecoration(ItemStack var0, BlockPos var1, String var2, Holder<MapDecorationType> var3) {
      MapDecorations.Entry var4 = new MapDecorations.Entry(var3, (double)var1.getX(), (double)var1.getZ(), 180.0F);
      var0.update(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY, (var2x) -> var2x.withDecoration(var2, var4));
      if (((MapDecorationType)var3.value()).hasMapColor()) {
         var0.set(DataComponents.MAP_COLOR, new MapItemColor(((MapDecorationType)var3.value()).mapColor()));
      }

   }

   private void addDecoration(Holder<MapDecorationType> var1, @Nullable LevelAccessor var2, String var3, double var4, double var6, double var8, @Nullable Component var10) {
      int var12 = 1 << this.scale;
      float var13 = (float)(var4 - (double)this.centerX) / (float)var12;
      float var14 = (float)(var6 - (double)this.centerZ) / (float)var12;
      MapDecorationLocation var11 = this.calculateDecorationLocationAndType(var1, var2, var8, var13, var14);
      if (var11 == null) {
         this.removeDecoration(var3);
      } else {
         MapDecoration var15 = new MapDecoration(var11.type(), var11.x(), var11.y(), var11.rot(), Optional.ofNullable(var10));
         MapDecoration var16 = (MapDecoration)this.decorations.put(var3, var15);
         if (!var15.equals(var16)) {
            if (var16 != null && ((MapDecorationType)var16.type().value()).trackCount()) {
               --this.trackedDecorationCount;
            }

            if (((MapDecorationType)var11.type().value()).trackCount()) {
               ++this.trackedDecorationCount;
            }

            this.setDecorationsDirty();
         }

      }
   }

   @Nullable
   private MapDecorationLocation calculateDecorationLocationAndType(Holder<MapDecorationType> var1, @Nullable LevelAccessor var2, double var3, float var5, float var6) {
      byte var7 = clampMapCoordinate(var5);
      byte var8 = clampMapCoordinate(var6);
      if (var1.is(MapDecorationTypes.PLAYER)) {
         Pair var9 = this.playerDecorationTypeAndRotation(var1, var2, var3, var5, var6);
         return var9 == null ? null : new MapDecorationLocation((Holder)var9.getFirst(), var7, var8, (Byte)var9.getSecond());
      } else {
         return !isInsideMap(var5, var6) && !this.unlimitedTracking ? null : new MapDecorationLocation(var1, var7, var8, this.calculateRotation(var2, var3));
      }
   }

   @Nullable
   private Pair<Holder<MapDecorationType>, Byte> playerDecorationTypeAndRotation(Holder<MapDecorationType> var1, @Nullable LevelAccessor var2, double var3, float var5, float var6) {
      if (isInsideMap(var5, var6)) {
         return Pair.of(var1, this.calculateRotation(var2, var3));
      } else {
         Holder var7 = this.decorationTypeForPlayerOutsideMap(var5, var6);
         return var7 == null ? null : Pair.of(var7, (byte)0);
      }
   }

   private byte calculateRotation(@Nullable LevelAccessor var1, double var2) {
      if (this.dimension == Level.NETHER && var1 != null) {
         int var6 = (int)(var1.getGameTime() / 10L);
         return (byte)(var6 * var6 * 34187121 + var6 * 121 >> 15 & 15);
      } else {
         double var4 = var2 < 0.0 ? var2 - 8.0 : var2 + 8.0;
         return (byte)((int)(var4 * 16.0 / 360.0));
      }
   }

   private static boolean isInsideMap(float var0, float var1) {
      boolean var2 = true;
      return var0 >= -63.0F && var1 >= -63.0F && var0 <= 63.0F && var1 <= 63.0F;
   }

   @Nullable
   private Holder<MapDecorationType> decorationTypeForPlayerOutsideMap(float var1, float var2) {
      boolean var3 = true;
      boolean var4 = Math.abs(var1) < 320.0F && Math.abs(var2) < 320.0F;
      if (var4) {
         return MapDecorationTypes.PLAYER_OFF_MAP;
      } else {
         return this.unlimitedTracking ? MapDecorationTypes.PLAYER_OFF_LIMITS : null;
      }
   }

   private static byte clampMapCoordinate(float var0) {
      boolean var1 = true;
      if (var0 <= -63.0F) {
         return -128;
      } else {
         return var0 >= 63.0F ? 127 : (byte)((int)((double)(var0 * 2.0F) + 0.5));
      }
   }

   @Nullable
   public Packet<?> getUpdatePacket(MapId var1, Player var2) {
      HoldingPlayer var3 = (HoldingPlayer)this.carriedByPlayers.get(var2);
      return var3 == null ? null : var3.nextUpdatePacket(var1);
   }

   private void setColorsDirty(int var1, int var2) {
      this.setDirty();

      for(HoldingPlayer var4 : this.carriedBy) {
         var4.markColorsDirty(var1, var2);
      }

   }

   private void setDecorationsDirty() {
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
      double var8 = (var3 - (double)this.centerX) / (double)var7;
      double var10 = (var5 - (double)this.centerZ) / (double)var7;
      boolean var12 = true;
      if (var8 >= -63.0 && var10 >= -63.0 && var8 <= 63.0 && var10 <= 63.0) {
         MapBanner var13 = MapBanner.fromWorld(var1, var2);
         if (var13 == null) {
            return false;
         }

         if (this.bannerMarkers.remove(var13.getId(), var13)) {
            this.removeDecoration(var13.getId());
            this.setDirty();
            return true;
         }

         if (!this.isTrackedCountOverLimit(256)) {
            this.bannerMarkers.put(var13.getId(), var13);
            this.addDecoration(var13.getDecoration(), var1, var13.getId(), var3, var5, 180.0, (Component)var13.name().orElse((Object)null));
            this.setDirty();
            return true;
         }
      }

      return false;
   }

   public void checkBanners(BlockGetter var1, int var2, int var3) {
      Iterator var4 = this.bannerMarkers.values().iterator();

      while(var4.hasNext()) {
         MapBanner var5 = (MapBanner)var4.next();
         if (var5.pos().getX() == var2 && var5.pos().getZ() == var3) {
            MapBanner var6 = MapBanner.fromWorld(var1, var5.pos());
            if (!var5.equals(var6)) {
               var4.remove();
               this.removeDecoration(var5.getId());
               this.setDirty();
            }
         }
      }

   }

   public Collection<MapBanner> getBanners() {
      return this.bannerMarkers.values();
   }

   public void removedFromFrame(BlockPos var1, int var2) {
      this.removeDecoration(getFrameKey(var2));
      this.frameMarkers.remove(MapFrame.frameId(var1));
      this.setDirty();
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
      for(MapDecoration var2 : this.decorations.values()) {
         if (((MapDecorationType)var2.type().value()).explorationMapElement()) {
            return true;
         }
      }

      return false;
   }

   public void addClientSideDecorations(List<MapDecoration> var1) {
      this.decorations.clear();
      this.trackedDecorationCount = 0;

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         MapDecoration var3 = (MapDecoration)var1.get(var2);
         this.decorations.put("icon-" + var2, var3);
         if (((MapDecorationType)var3.type().value()).trackCount()) {
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

   private static String getFrameKey(int var0) {
      return "frame-" + var0;
   }

   public static record MapPatch(int startX, int startY, int width, int height, byte[] mapColors) {
      public static final StreamCodec<ByteBuf, Optional<MapPatch>> STREAM_CODEC = StreamCodec.<ByteBuf, Optional<MapPatch>>of(MapPatch::write, MapPatch::read);

      public MapPatch(int var1, int var2, int var3, int var4, byte[] var5) {
         super();
         this.startX = var1;
         this.startY = var2;
         this.width = var3;
         this.height = var4;
         this.mapColors = var5;
      }

      private static void write(ByteBuf var0, Optional<MapPatch> var1) {
         if (var1.isPresent()) {
            MapPatch var2 = (MapPatch)var1.get();
            var0.writeByte(var2.width);
            var0.writeByte(var2.height);
            var0.writeByte(var2.startX);
            var0.writeByte(var2.startY);
            FriendlyByteBuf.writeByteArray(var0, var2.mapColors);
         } else {
            var0.writeByte(0);
         }

      }

      private static Optional<MapPatch> read(ByteBuf var0) {
         short var1 = var0.readUnsignedByte();
         if (var1 > 0) {
            short var2 = var0.readUnsignedByte();
            short var3 = var0.readUnsignedByte();
            short var4 = var0.readUnsignedByte();
            byte[] var5 = FriendlyByteBuf.readByteArray(var0);
            return Optional.of(new MapPatch(var3, var4, var1, var2, var5));
         } else {
            return Optional.empty();
         }
      }

      public void applyToMap(MapItemSavedData var1) {
         for(int var2 = 0; var2 < this.width; ++var2) {
            for(int var3 = 0; var3 < this.height; ++var3) {
               var1.setColor(this.startX + var2, this.startY + var3, this.mapColors[var2 + var3 * this.width]);
            }
         }

      }
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

      HoldingPlayer(final Player var2) {
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
      Packet<?> nextUpdatePacket(MapId var1) {
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

   static record MapDecorationLocation(Holder<MapDecorationType> type, byte x, byte y, byte rot) {
      MapDecorationLocation(Holder<MapDecorationType> var1, byte var2, byte var3, byte var4) {
         super();
         this.type = var1;
         this.x = var2;
         this.y = var3;
         this.rot = var4;
      }
   }
}
