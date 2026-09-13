package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;

public class WorldManager implements IWorldAccess {
   private MinecraftServer field_72783_a;
   private WorldServer field_72782_b;

   public WorldManager(MinecraftServer var1, WorldServer var2) {
      super();
      this.field_72783_a = var1;
      this.field_72782_b = var2;
   }

   @Override
   public void func_72708_a(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   @Override
   public void func_72703_a(Entity var1) {
      this.field_72782_b.func_73039_n().func_72786_a(var1);
   }

   @Override
   public void func_72709_b(Entity var1) {
      this.field_72782_b.func_73039_n().func_72790_b(var1);
   }

   @Override
   public void func_72704_a(String var1, double var2, double var4, double var6, float var8, float var9) {
      this.field_72783_a
         .func_71203_ab()
         .func_148541_a(
            var2,
            var4,
            var6,
            var8 > 1.0F ? (double)(16.0F * var8) : 16.0,
            this.field_72782_b.field_73011_w.field_76574_g,
            new S29PacketSoundEffect(var1, var2, var4, var6, var8, var9)
         );
   }

   @Override
   public void func_85102_a(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10) {
      this.field_72783_a
         .func_71203_ab()
         .func_148543_a(
            var1,
            var3,
            var5,
            var7,
            var9 > 1.0F ? (double)(16.0F * var9) : 16.0,
            this.field_72782_b.field_73011_w.field_76574_g,
            new S29PacketSoundEffect(var2, var3, var5, var7, var9, var10)
         );
   }

   @Override
   public void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   @Override
   public void func_147586_a(int var1, int var2, int var3) {
      this.field_72782_b.func_73040_p().func_151250_a(var1, var2, var3);
   }

   @Override
   public void func_147588_b(int var1, int var2, int var3) {
   }

   @Override
   public void func_72702_a(String var1, int var2, int var3, int var4) {
   }

   @Override
   public void func_72706_a(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_72783_a
         .func_71203_ab()
         .func_148543_a(
            var1,
            (double)var3,
            (double)var4,
            (double)var5,
            64.0,
            this.field_72782_b.field_73011_w.field_76574_g,
            new S28PacketEffect(var2, var3, var4, var5, var6, false)
         );
   }

   @Override
   public void func_82746_a(int var1, int var2, int var3, int var4, int var5) {
      this.field_72783_a.func_71203_ab().func_148540_a(new S28PacketEffect(var1, var2, var3, var4, var5, true));
   }

   @Override
   public void func_147587_b(int var1, int var2, int var3, int var4, int var5) {
      for(EntityPlayerMP var7 : this.field_72783_a.func_71203_ab().field_72404_b) {
         if (var7 != null && var7.field_70170_p == this.field_72782_b && var7.func_145782_y() != var1) {
            double var8 = (double)var2 - var7.field_70165_t;
            double var10 = (double)var3 - var7.field_70163_u;
            double var12 = (double)var4 - var7.field_70161_v;
            if (var8 * var8 + var10 * var10 + var12 * var12 < 1024.0) {
               var7.field_71135_a.func_147359_a(new S25PacketBlockBreakAnim(var1, var2, var3, var4, var5));
            }
         }
      }
   }

   @Override
   public void func_147584_b() {
   }
}
