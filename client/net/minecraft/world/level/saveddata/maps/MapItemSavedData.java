package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;

public class MapItemSavedData extends SavedData {
   public int x;
   public int z;
   public DimensionType dimension;
   public boolean trackingPosition;
   public boolean unlimitedTracking;
   public byte scale;
   public byte[] colors = new byte[16384];
   public boolean locked;
   public final List<MapItemSavedData.HoldingPlayer> carriedBy = Lists.newArrayList();
   private final Map<Player, MapItemSavedData.HoldingPlayer> carriedByPlayers = Maps.newHashMap();
   private final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
   public final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
   private final Map<String, MapFrame> frameMarkers = Maps.newHashMap();

   public MapItemSavedData(String var1) {
      super(var1);
   }

   public void setProperties(int var1, int var2, int var3, boolean var4, boolean var5, DimensionType var6) {
      this.scale = (byte)var3;
      this.setOrigin((double)var1, (double)var2, this.scale);
      this.dimension = var6;
      this.trackingPosition = var4;
      this.unlimitedTracking = var5;
      this.setDirty();
   }

   public void setOrigin(double var1, double var3, int var5) {
      int var6 = 128 * (1 << var5);
      int var7 = Mth.floor((var1 + 64.0D) / (double)var6);
      int var8 = Mth.floor((var3 + 64.0D) / (double)var6);
      this.x = var7 * var6 + var6 / 2 - 64;
      this.z = var8 * var6 + var6 / 2 - 64;
   }

   public void load(CompoundTag var1) {
      int var2 = var1.getInt("dimension");
      DimensionType var3 = DimensionType.getById(var2);
      if (var3 == null) {
         throw new IllegalArgumentException("Invalid map dimension: " + var2);
      } else {
         this.dimension = var3;
         this.x = var1.getInt("xCenter");
         this.z = var1.getInt("zCenter");
         this.scale = (byte)Mth.clamp(var1.getByte("scale"), 0, 4);
         this.trackingPosition = !var1.contains("trackingPosition", 1) || var1.getBoolean("trackingPosition");
         this.unlimitedTracking = var1.getBoolean("unlimitedTracking");
         this.locked = var1.getBoolean("locked");
         this.colors = var1.getByteArray("colors");
         if (this.colors.length != 16384) {
            this.colors = new byte[16384];
         }

         ListTag var4 = var1.getList("banners", 10);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            MapBanner var6 = MapBanner.load(var4.getCompound(var5));
            this.bannerMarkers.put(var6.getId(), var6);
            this.addDecoration(var6.getDecoration(), (LevelAccessor)null, var6.getId(), (double)var6.getPos().getX(), (double)var6.getPos().getZ(), 180.0D, var6.getName());
         }

         ListTag var8 = var1.getList("frames", 10);

         for(int var9 = 0; var9 < var8.size(); ++var9) {
            MapFrame var7 = MapFrame.load(var8.getCompound(var9));
            this.frameMarkers.put(var7.getId(), var7);
            this.addDecoration(MapDecoration.Type.FRAME, (LevelAccessor)null, "frame-" + var7.getEntityId(), (double)var7.getPos().getX(), (double)var7.getPos().getZ(), (double)var7.getRotation(), (Component)null);
         }

      }
   }

   public CompoundTag save(CompoundTag var1) {
      var1.putInt("dimension", this.dimension.getId());
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

   public void lockData(MapItemSavedData var1) {
      this.locked = true;
      this.x = var1.x;
      this.z = var1.z;
      this.bannerMarkers.putAll(var1.bannerMarkers);
      this.decorations.putAll(var1.decorations);
      System.arraycopy(var1.colors, 0, this.colors, 0, var1.colors.length);
      this.setDirty();
   }

   public void tickCarriedBy(Player var1, ItemStack var2) {
      if (!this.carriedByPlayers.containsKey(var1)) {
         MapItemSavedData.HoldingPlayer var3 = new MapItemSavedData.HoldingPlayer(var1);
         this.carriedByPlayers.put(var1, var3);
         this.carriedBy.add(var3);
      }

      if (!var1.inventory.contains(var2)) {
         this.decorations.remove(var1.getName().getString());
      }

      for(int var7 = 0; var7 < this.carriedBy.size(); ++var7) {
         MapItemSavedData.HoldingPlayer var4 = (MapItemSavedData.HoldingPlayer)this.carriedBy.get(var7);
         String var5 = var4.player.getName().getString();
         if (!var4.player.removed && (var4.player.inventory.contains(var2) || var2.isFramed())) {
            if (!var2.isFramed() && var4.player.dimension == this.dimension && this.trackingPosition) {
               this.addDecoration(MapDecoration.Type.PLAYER, var4.player.level, var5, var4.player.x, var4.player.z, (double)var4.player.yRot, (Component)null);
            }
         } else {
            this.carriedByPlayers.remove(var4.player);
            this.carriedBy.remove(var4);
            this.decorations.remove(var5);
         }
      }

      if (var2.isFramed() && this.trackingPosition) {
         ItemFrame var8 = var2.getFrame();
         BlockPos var9 = var8.getPos();
         MapFrame var12 = (MapFrame)this.frameMarkers.get(MapFrame.frameId(var9));
         if (var12 != null && var8.getId() != var12.getEntityId() && this.frameMarkers.containsKey(var12.getId())) {
            this.decorations.remove("frame-" + var12.getEntityId());
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
      var5.putDouble("rot", 180.0D);
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
      byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5D));
      byte var15 = (byte)((int)((double)(var13 * 2.0F) + 0.5D));
      boolean var17 = true;
      byte var16;
      if (var12 >= -63.0F && var13 >= -63.0F && var12 <= 63.0F && var13 <= 63.0F) {
         var8 += var8 < 0.0D ? -8.0D : 8.0D;
         var16 = (byte)((int)(var8 * 16.0D / 360.0D));
         if (this.dimension == DimensionType.NETHER && var2 != null) {
            int var19 = (int)(var2.getLevelData().getDayTime() / 10L);
            var16 = (byte)(var19 * var19 * 34187121 + var19 * 121 >> 15 & 15);
         }
      } else {
         if (var1 != MapDecoration.Type.PLAYER) {
            this.decorations.remove(var3);
            return;
         }

         boolean var18 = true;
         if (Math.abs(var12) < 320.0F && Math.abs(var13) < 320.0F) {
            var1 = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if (!this.unlimitedTracking) {
               this.decorations.remove(var3);
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

      this.decorations.put(var3, new MapDecoration(var1, var14, var15, var16, var10));
   }

   @Nullable
   public Packet<?> getUpdatePacket(ItemStack var1, BlockGetter var2, Player var3) {
      MapItemSavedData.HoldingPlayer var4 = (MapItemSavedData.HoldingPlayer)this.carriedByPlayers.get(var3);
      return var4 == null ? null : var4.nextUpdatePacket(var1);
   }

   public void setDirty(int var1, int var2) {
      this.setDirty();
      Iterator var3 = this.carriedBy.iterator();

      while(var3.hasNext()) {
         MapItemSavedData.HoldingPlayer var4 = (MapItemSavedData.HoldingPlayer)var3.next();
         var4.markDirty(var1, var2);
      }

   }

   public MapItemSavedData.HoldingPlayer getHoldingPlayer(Player var1) {
      MapItemSavedData.HoldingPlayer var2 = (MapItemSavedData.HoldingPlayer)this.carriedByPlayers.get(var1);
      if (var2 == null) {
         var2 = new MapItemSavedData.HoldingPlayer(var1);
         this.carriedByPlayers.put(var1, var2);
         this.carriedBy.add(var2);
      }

      return var2;
   }

   public void toggleBanner(LevelAccessor var1, BlockPos var2) {
      float var3 = (float)var2.getX() + 0.5F;
      float var4 = (float)var2.getZ() + 0.5F;
      int var5 = 1 << this.scale;
      float var6 = (var3 - (float)this.x) / (float)var5;
      float var7 = (var4 - (float)this.z) / (float)var5;
      boolean var8 = true;
      boolean var9 = false;
      if (var6 >= -63.0F && var7 >= -63.0F && var6 <= 63.0F && var7 <= 63.0F) {
         MapBanner var10 = MapBanner.fromWorld(var1, var2);
         if (var10 == null) {
            return;
         }

         boolean var11 = true;
         if (this.bannerMarkers.containsKey(var10.getId()) && ((MapBanner)this.bannerMarkers.get(var10.getId())).equals(var10)) {
            this.bannerMarkers.remove(var10.getId());
            this.decorations.remove(var10.getId());
            var11 = false;
            var9 = true;
         }

         if (var11) {
            this.bannerMarkers.put(var10.getId(), var10);
            this.addDecoration(var10.getDecoration(), var1, var10.getId(), (double)var3, (double)var4, 180.0D, var10.getName());
            var9 = true;
         }

         if (var9) {
            this.setDirty();
         }
      }

   }

   public void checkBanners(BlockGetter var1, int var2, int var3) {
      Iterator var4 = this.bannerMarkers.values().iterator();

      while(var4.hasNext()) {
         MapBanner var5 = (MapBanner)var4.next();
         if (var5.getPos().getX() == var2 && var5.getPos().getZ() == var3) {
            MapBanner var6 = MapBanner.fromWorld(var1, var5.getPos());
            if (!var5.equals(var6)) {
               var4.remove();
               this.decorations.remove(var5.getId());
            }
         }
      }

   }

   public void removedFromFrame(BlockPos var1, int var2) {
      this.decorations.remove("frame-" + var2);
      this.frameMarkers.remove(MapFrame.frameId(var1));
   }

   public class HoldingPlayer {
      public final Player player;
      private boolean dirtyData = true;
      private int minDirtyX;
      private int minDirtyY;
      private int maxDirtyX = 127;
      private int maxDirtyY = 127;
      private int tick;
      public int step;

      public HoldingPlayer(Player var2) {
         super();
         this.player = var2;
      }

      @Nullable
      public Packet<?> nextUpdatePacket(ItemStack var1) {
         if (this.dirtyData) {
            this.dirtyData = false;
            return new ClientboundMapItemDataPacket(MapItem.getMapId(var1), MapItemSavedData.this.scale, MapItemSavedData.this.trackingPosition, MapItemSavedData.this.locked, MapItemSavedData.this.decorations.values(), MapItemSavedData.this.colors, this.minDirtyX, this.minDirtyY, this.maxDirtyX + 1 - this.minDirtyX, this.maxDirtyY + 1 - this.minDirtyY);
         } else {
            return this.tick++ % 5 == 0 ? new ClientboundMapItemDataPacket(MapItem.getMapId(var1), MapItemSavedData.this.scale, MapItemSavedData.this.trackingPosition, MapItemSavedData.this.locked, MapItemSavedData.this.decorations.values(), MapItemSavedData.this.colors, 0, 0, 0, 0) : null;
         }
      }

      public void markDirty(int var1, int var2) {
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
   }
}
