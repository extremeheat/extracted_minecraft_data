package net.minecraft.world;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class WorldManager implements IWorldAccess {
   private MinecraftServer field_72783_a;
   private WorldServer field_72782_b;

   public WorldManager(MinecraftServer var1, WorldServer var2) {
      super();
      this.field_72783_a = var1;
      this.field_72782_b = var2;
   }

   public void func_180442_a(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
   }

   public void func_72703_a(Entity var1) {
      this.field_72782_b.func_73039_n().func_72786_a(var1);
   }

   public void func_72709_b(Entity var1) {
      this.field_72782_b.func_73039_n().func_72790_b(var1);
      this.field_72782_b.func_96441_U().func_181140_a(var1);
   }

   public void func_72704_a(String var1, double var2, double var4, double var6, float var8, float var9) {
      this.field_72783_a.func_71203_ab().func_148541_a(var2, var4, var6, var8 > 1.0F ? (double)(16.0F * var8) : 16.0D, this.field_72782_b.field_73011_w.func_177502_q(), new S29PacketSoundEffect(var1, var2, var4, var6, var8, var9));
   }

   public void func_85102_a(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10) {
      this.field_72783_a.func_71203_ab().func_148543_a(var1, var3, var5, var7, var9 > 1.0F ? (double)(16.0F * var9) : 16.0D, this.field_72782_b.field_73011_w.func_177502_q(), new S29PacketSoundEffect(var2, var3, var5, var7, var9, var10));
   }

   public void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   public void func_174960_a(BlockPos var1) {
      this.field_72782_b.func_73040_p().func_180244_a(var1);
   }

   public void func_174959_b(BlockPos var1) {
   }

   public void func_174961_a(String var1, BlockPos var2) {
   }

   public void func_180439_a(EntityPlayer var1, int var2, BlockPos var3, int var4) {
      this.field_72783_a.func_71203_ab().func_148543_a(var1, (double)var3.func_177958_n(), (double)var3.func_177956_o(), (double)var3.func_177952_p(), 64.0D, this.field_72782_b.field_73011_w.func_177502_q(), new S28PacketEffect(var2, var3, var4, false));
   }

   public void func_180440_a(int var1, BlockPos var2, int var3) {
      this.field_72783_a.func_71203_ab().func_148540_a(new S28PacketEffect(var1, var2, var3, true));
   }

   public void func_180441_b(int var1, BlockPos var2, int var3) {
      Iterator var4 = this.field_72783_a.func_71203_ab().func_181057_v().iterator();

      while(var4.hasNext()) {
         EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
         if (var5 != null && var5.field_70170_p == this.field_72782_b && var5.func_145782_y() != var1) {
            double var6 = (double)var2.func_177958_n() - var5.field_70165_t;
            double var8 = (double)var2.func_177956_o() - var5.field_70163_u;
            double var10 = (double)var2.func_177952_p() - var5.field_70161_v;
            if (var6 * var6 + var8 * var8 + var10 * var10 < 1024.0D) {
               var5.field_71135_a.func_147359_a(new S25PacketBlockBreakAnim(var1, var2, var3));
            }
         }
      }

   }
}
