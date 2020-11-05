package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MapBanner {
   private final BlockPos pos;
   private final DyeColor color;
   @Nullable
   private final Component name;

   public MapBanner(BlockPos var1, DyeColor var2, @Nullable Component var3) {
      super();
      this.pos = var1;
      this.color = var2;
      this.name = var3;
   }

   public static MapBanner load(CompoundTag var0) {
      BlockPos var1 = NbtUtils.readBlockPos(var0.getCompound("Pos"));
      DyeColor var2 = DyeColor.byName(var0.getString("Color"), DyeColor.WHITE);
      MutableComponent var3 = var0.contains("Name") ? Component.Serializer.fromJson(var0.getString("Name")) : null;
      return new MapBanner(var1, var2, var3);
   }

   @Nullable
   public static MapBanner fromWorld(BlockGetter var0, BlockPos var1) {
      BlockEntity var2 = var0.getBlockEntity(var1);
      if (var2 instanceof BannerBlockEntity) {
         BannerBlockEntity var3 = (BannerBlockEntity)var2;
         DyeColor var4 = var3.getBaseColor();
         Component var5 = var3.hasCustomName() ? var3.getCustomName() : null;
         return new MapBanner(var1, var4, var5);
      } else {
         return null;
      }
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public MapDecoration.Type getDecoration() {
      switch(this.color) {
      case WHITE:
         return MapDecoration.Type.BANNER_WHITE;
      case ORANGE:
         return MapDecoration.Type.BANNER_ORANGE;
      case MAGENTA:
         return MapDecoration.Type.BANNER_MAGENTA;
      case LIGHT_BLUE:
         return MapDecoration.Type.BANNER_LIGHT_BLUE;
      case YELLOW:
         return MapDecoration.Type.BANNER_YELLOW;
      case LIME:
         return MapDecoration.Type.BANNER_LIME;
      case PINK:
         return MapDecoration.Type.BANNER_PINK;
      case GRAY:
         return MapDecoration.Type.BANNER_GRAY;
      case LIGHT_GRAY:
         return MapDecoration.Type.BANNER_LIGHT_GRAY;
      case CYAN:
         return MapDecoration.Type.BANNER_CYAN;
      case PURPLE:
         return MapDecoration.Type.BANNER_PURPLE;
      case BLUE:
         return MapDecoration.Type.BANNER_BLUE;
      case BROWN:
         return MapDecoration.Type.BANNER_BROWN;
      case GREEN:
         return MapDecoration.Type.BANNER_GREEN;
      case RED:
         return MapDecoration.Type.BANNER_RED;
      case BLACK:
      default:
         return MapDecoration.Type.BANNER_BLACK;
      }
   }

   @Nullable
   public Component getName() {
      return this.name;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MapBanner var2 = (MapBanner)var1;
         return Objects.equals(this.pos, var2.pos) && this.color == var2.color && Objects.equals(this.name, var2.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.pos, this.color, this.name});
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.put("Pos", NbtUtils.writeBlockPos(this.pos));
      var1.putString("Color", this.color.getName());
      if (this.name != null) {
         var1.putString("Name", Component.Serializer.toJson(this.name));
      }

      return var1;
   }

   public String getId() {
      return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
   }
}
