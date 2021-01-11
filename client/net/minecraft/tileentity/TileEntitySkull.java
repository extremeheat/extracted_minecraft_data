package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TileEntitySkull extends TileEntity {
   private int field_145908_a;
   private int field_145910_i;
   private GameProfile field_152110_j = null;

   public TileEntitySkull() {
      super();
   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74774_a("SkullType", (byte)(this.field_145908_a & 255));
      var1.func_74774_a("Rot", (byte)(this.field_145910_i & 255));
      if (this.field_152110_j != null) {
         NBTTagCompound var2 = new NBTTagCompound();
         NBTUtil.func_180708_a(var2, this.field_152110_j);
         var1.func_74782_a("Owner", var2);
      }

   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145908_a = var1.func_74771_c("SkullType");
      this.field_145910_i = var1.func_74771_c("Rot");
      if (this.field_145908_a == 3) {
         if (var1.func_150297_b("Owner", 10)) {
            this.field_152110_j = NBTUtil.func_152459_a(var1.func_74775_l("Owner"));
         } else if (var1.func_150297_b("ExtraType", 8)) {
            String var2 = var1.func_74779_i("ExtraType");
            if (!StringUtils.func_151246_b(var2)) {
               this.field_152110_j = new GameProfile((UUID)null, var2);
               this.func_152109_d();
            }
         }
      }

   }

   public GameProfile func_152108_a() {
      return this.field_152110_j;
   }

   public Packet func_145844_m() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.func_145841_b(var1);
      return new S35PacketUpdateTileEntity(this.field_174879_c, 4, var1);
   }

   public void func_152107_a(int var1) {
      this.field_145908_a = var1;
      this.field_152110_j = null;
   }

   public void func_152106_a(GameProfile var1) {
      this.field_145908_a = 3;
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
         } else if (MinecraftServer.func_71276_C() == null) {
            return var0;
         } else {
            GameProfile var1 = MinecraftServer.func_71276_C().func_152358_ax().func_152655_a(var0.getName());
            if (var1 == null) {
               return var0;
            } else {
               Property var2 = (Property)Iterables.getFirst(var1.getProperties().get("textures"), (Object)null);
               if (var2 == null) {
                  var1 = MinecraftServer.func_71276_C().func_147130_as().fillProfileProperties(var1, true);
               }

               return var1;
            }
         }
      } else {
         return var0;
      }
   }

   public int func_145904_a() {
      return this.field_145908_a;
   }

   public int func_145906_b() {
      return this.field_145910_i;
   }

   public void func_145903_a(int var1) {
      this.field_145910_i = var1;
   }
}
