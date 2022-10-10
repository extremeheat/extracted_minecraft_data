package net.minecraft.world;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.particles.IParticleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class ServerWorldEventHandler implements IWorldEventListener {
   private final MinecraftServer field_72783_a;
   private final WorldServer field_72782_b;

   public ServerWorldEventHandler(MinecraftServer var1, WorldServer var2) {
      super();
      this.field_72783_a = var1;
      this.field_72782_b = var2;
   }

   public void func_195461_a(IParticleData var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
   }

   public void func_195462_a(IParticleData var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
   }

   public void func_72703_a(Entity var1) {
      this.field_72782_b.func_73039_n().func_72786_a(var1);
      if (var1 instanceof EntityPlayerMP) {
         this.field_72782_b.field_73011_w.func_186061_a((EntityPlayerMP)var1);
      }

   }

   public void func_72709_b(Entity var1) {
      this.field_72782_b.func_73039_n().func_72790_b(var1);
      this.field_72782_b.func_96441_U().func_181140_a(var1);
      if (var1 instanceof EntityPlayerMP) {
         this.field_72782_b.field_73011_w.func_186062_b((EntityPlayerMP)var1);
      }

   }

   public void func_184375_a(@Nullable EntityPlayer var1, SoundEvent var2, SoundCategory var3, double var4, double var6, double var8, float var10, float var11) {
      this.field_72783_a.func_184103_al().func_148543_a(var1, var4, var6, var8, var10 > 1.0F ? (double)(16.0F * var10) : 16.0D, this.field_72782_b.field_73011_w.func_186058_p(), new SPacketSoundEffect(var2, var3, var4, var6, var8, var10, var11));
   }

   public void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   public void func_184376_a(IBlockReader var1, BlockPos var2, IBlockState var3, IBlockState var4, int var5) {
      this.field_72782_b.func_184164_w().func_180244_a(var2);
   }

   public void func_174959_b(BlockPos var1) {
   }

   public void func_184377_a(SoundEvent var1, BlockPos var2) {
   }

   public void func_180439_a(EntityPlayer var1, int var2, BlockPos var3, int var4) {
      this.field_72783_a.func_184103_al().func_148543_a(var1, (double)var3.func_177958_n(), (double)var3.func_177956_o(), (double)var3.func_177952_p(), 64.0D, this.field_72782_b.field_73011_w.func_186058_p(), new SPacketEffect(var2, var3, var4, false));
   }

   public void func_180440_a(int var1, BlockPos var2, int var3) {
      this.field_72783_a.func_184103_al().func_148540_a(new SPacketEffect(var1, var2, var3, true));
   }

   public void func_180441_b(int var1, BlockPos var2, int var3) {
      Iterator var4 = this.field_72783_a.func_184103_al().func_181057_v().iterator();

      while(var4.hasNext()) {
         EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
         if (var5 != null && var5.field_70170_p == this.field_72782_b && var5.func_145782_y() != var1) {
            double var6 = (double)var2.func_177958_n() - var5.field_70165_t;
            double var8 = (double)var2.func_177956_o() - var5.field_70163_u;
            double var10 = (double)var2.func_177952_p() - var5.field_70161_v;
            if (var6 * var6 + var8 * var8 + var10 * var10 < 1024.0D) {
               var5.field_71135_a.func_147359_a(new SPacketBlockBreakAnim(var1, var2, var3));
            }
         }
      }

   }
}
