package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityElderGuardian extends EntityGuardian {
   public EntityElderGuardian(World var1) {
      super(EntityType.field_200800_n, var1);
      this.func_70105_a(this.field_70130_N * 2.35F, this.field_70131_O * 2.35F);
      this.func_110163_bv();
      if (this.field_175481_bq != null) {
         this.field_175481_bq.func_179479_b(400);
      }

   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(8.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(80.0D);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186441_w;
   }

   public int func_175464_ck() {
      return 60;
   }

   public void func_190767_di() {
      this.field_175485_bl = 1.0F;
      this.field_175486_bm = this.field_175485_bl;
   }

   protected SoundEvent func_184639_G() {
      return this.func_203005_aq() ? SoundEvents.field_187512_aB : SoundEvents.field_187513_aC;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return this.func_203005_aq() ? SoundEvents.field_187517_aG : SoundEvents.field_187518_aH;
   }

   protected SoundEvent func_184615_bR() {
      return this.func_203005_aq() ? SoundEvents.field_187515_aE : SoundEvents.field_187516_aF;
   }

   protected SoundEvent func_190765_dj() {
      return SoundEvents.field_191240_aK;
   }

   protected void func_70619_bc() {
      super.func_70619_bc();
      boolean var1 = true;
      if ((this.field_70173_aa + this.func_145782_y()) % 1200 == 0) {
         Potion var2 = MobEffects.field_76419_f;
         List var3 = this.field_70170_p.func_175661_b(EntityPlayerMP.class, (var1x) -> {
            return this.func_70068_e(var1x) < 2500.0D && var1x.field_71134_c.func_180239_c();
         });
         boolean var4 = true;
         boolean var5 = true;
         boolean var6 = true;
         Iterator var7 = var3.iterator();

         label28:
         while(true) {
            EntityPlayerMP var8;
            do {
               if (!var7.hasNext()) {
                  break label28;
               }

               var8 = (EntityPlayerMP)var7.next();
            } while(var8.func_70644_a(var2) && var8.func_70660_b(var2).func_76458_c() >= 2 && var8.func_70660_b(var2).func_76459_b() >= 1200);

            var8.field_71135_a.func_147359_a(new SPacketChangeGameState(10, 0.0F));
            var8.func_195064_c(new PotionEffect(var2, 6000, 2));
         }
      }

      if (!this.func_110175_bO()) {
         this.func_175449_a(new BlockPos(this), 16);
      }

   }
}
