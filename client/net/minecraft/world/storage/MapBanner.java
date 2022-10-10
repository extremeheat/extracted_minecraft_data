package net.minecraft.world.storage;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;

public class MapBanner {
   private final BlockPos field_204306_a;
   private final EnumDyeColor field_204307_b;
   @Nullable
   private final ITextComponent field_204308_c;

   public MapBanner(BlockPos var1, EnumDyeColor var2, @Nullable ITextComponent var3) {
      super();
      this.field_204306_a = var1;
      this.field_204307_b = var2;
      this.field_204308_c = var3;
   }

   public static MapBanner func_204300_a(NBTTagCompound var0) {
      BlockPos var1 = NBTUtil.func_186861_c(var0.func_74775_l("Pos"));
      EnumDyeColor var2 = EnumDyeColor.func_204271_a(var0.func_74779_i("Color"));
      ITextComponent var3 = var0.func_74764_b("Name") ? ITextComponent.Serializer.func_150699_a(var0.func_74779_i("Name")) : null;
      return new MapBanner(var1, var2, var3);
   }

   @Nullable
   public static MapBanner func_204301_a(IBlockReader var0, BlockPos var1) {
      TileEntity var2 = var0.func_175625_s(var1);
      if (var2 instanceof TileEntityBanner) {
         TileEntityBanner var3 = (TileEntityBanner)var2;
         EnumDyeColor var4 = var3.func_195533_l(() -> {
            return var0.func_180495_p(var1);
         });
         ITextComponent var5 = var3.func_145818_k_() ? var3.func_200201_e() : null;
         return new MapBanner(var1, var4, var5);
      } else {
         return null;
      }
   }

   public BlockPos func_204304_a() {
      return this.field_204306_a;
   }

   public MapDecoration.Type func_204305_c() {
      switch(this.field_204307_b) {
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
   public ITextComponent func_204302_d() {
      return this.field_204308_c;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MapBanner var2 = (MapBanner)var1;
         return Objects.equals(this.field_204306_a, var2.field_204306_a) && this.field_204307_b == var2.field_204307_b && Objects.equals(this.field_204308_c, var2.field_204308_c);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.field_204306_a, this.field_204307_b, this.field_204308_c});
   }

   public NBTTagCompound func_204303_e() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74782_a("Pos", NBTUtil.func_186859_a(this.field_204306_a));
      var1.func_74778_a("Color", this.field_204307_b.func_176762_d());
      if (this.field_204308_c != null) {
         var1.func_74778_a("Name", ITextComponent.Serializer.func_150696_a(this.field_204308_c));
      }

      return var1;
   }

   public String func_204299_f() {
      return "banner-" + this.field_204306_a.func_177958_n() + "," + this.field_204306_a.func_177956_o() + "," + this.field_204306_a.func_177952_p();
   }
}
