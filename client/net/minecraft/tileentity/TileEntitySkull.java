package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.ITickable;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class TileEntitySkull extends TileEntity implements ITickable {
   private GameProfile field_152110_j;
   private int field_184296_h;
   private boolean field_184297_i;
   private boolean field_195488_h = true;
   private static PlayerProfileCache field_184298_j;
   private static MinecraftSessionService field_184299_k;

   public TileEntitySkull() {
      super(TileEntityType.field_200985_p);
   }

   public static void func_184293_a(PlayerProfileCache var0) {
      field_184298_j = var0;
   }

   public static void func_184294_a(MinecraftSessionService var0) {
      field_184299_k = var0;
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      if (this.field_152110_j != null) {
         NBTTagCompound var2 = new NBTTagCompound();
         NBTUtil.func_180708_a(var2, this.field_152110_j);
         var1.func_74782_a("Owner", var2);
      }

      return var1;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      if (var1.func_150297_b("Owner", 10)) {
         this.func_195485_a(NBTUtil.func_152459_a(var1.func_74775_l("Owner")));
      } else if (var1.func_150297_b("ExtraType", 8)) {
         String var2 = var1.func_74779_i("ExtraType");
         if (!StringUtils.func_151246_b(var2)) {
            this.func_195485_a(new GameProfile((UUID)null, var2));
         }
      }

   }

   public void func_73660_a() {
      Block var1 = this.func_195044_w().func_177230_c();
      if (var1 == Blocks.field_196716_eW || var1 == Blocks.field_196715_eV) {
         if (this.field_145850_b.func_175640_z(this.field_174879_c)) {
            this.field_184297_i = true;
            ++this.field_184296_h;
         } else {
            this.field_184297_i = false;
         }
      }

   }

   public float func_184295_a(float var1) {
      return this.field_184297_i ? (float)this.field_184296_h + var1 : (float)this.field_184296_h;
   }

   @Nullable
   public GameProfile func_152108_a() {
      return this.field_152110_j;
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 4, this.func_189517_E_());
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189515_b(new NBTTagCompound());
   }

   public void func_195485_a(@Nullable GameProfile var1) {
      this.field_152110_j = var1;
      this.func_152109_d();
   }

   private void func_152109_d() {
      this.field_152110_j = func_174884_b(this.field_152110_j);
      this.func_70296_d();
   }

   public static GameProfile func_174884_b(GameProfile var0) {
      if (var0 != null && !StringUtils.func_151246_b(var0.getName())) {
         if (var0.isComplete() && var0.getProperties().containsKey("textures")) {
            return var0;
         } else if (field_184298_j != null && field_184299_k != null) {
            GameProfile var1 = field_184298_j.func_152655_a(var0.getName());
            if (var1 == null) {
               return var0;
            } else {
               Property var2 = (Property)Iterables.getFirst(var1.getProperties().get("textures"), (Object)null);
               if (var2 == null) {
                  var1 = field_184299_k.fillProfileProperties(var1, true);
               }

               return var1;
            }
         } else {
            return var0;
         }
      } else {
         return var0;
      }
   }

   public static void func_195486_a(IBlockReader var0, BlockPos var1) {
      TileEntity var2 = var0.func_175625_s(var1);
      if (var2 instanceof TileEntitySkull) {
         TileEntitySkull var3 = (TileEntitySkull)var2;
         var3.field_195488_h = false;
      }

   }

   public boolean func_195487_d() {
      return this.field_195488_h;
   }
}
