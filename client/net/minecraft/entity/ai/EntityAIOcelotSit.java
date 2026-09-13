package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class EntityAIOcelotSit extends EntityAIBase {
   private final EntityOcelot field_151493_a;
   private final double field_151491_b;
   private int field_151492_c;
   private int field_151489_d;
   private int field_151490_e;
   private int field_151487_f;
   private int field_151488_g;
   private int field_151494_h;

   public EntityAIOcelotSit(EntityOcelot var1, double var2) {
      super();
      this.field_151493_a = var1;
      this.field_151491_b = var2;
      this.func_75248_a(5);
   }

   @Override
   public boolean func_75250_a() {
      return this.field_151493_a.func_70909_n()
         && !this.field_151493_a.func_70906_o()
         && this.field_151493_a.func_70681_au().nextDouble() <= 0.006500000134110451
         && this.func_151485_f();
   }

   @Override
   public boolean func_75253_b() {
      return this.field_151492_c <= this.field_151490_e
         && this.field_151489_d <= 60
         && this.func_151486_a(this.field_151493_a.field_70170_p, this.field_151487_f, this.field_151488_g, this.field_151494_h);
   }

   @Override
   public void func_75249_e() {
      this.field_151493_a
         .func_70661_as()
         .func_75492_a(
            (double)((float)this.field_151487_f) + 0.5, (double)(this.field_151488_g + 1), (double)((float)this.field_151494_h) + 0.5, this.field_151491_b
         );
      this.field_151492_c = 0;
      this.field_151489_d = 0;
      this.field_151490_e = this.field_151493_a.func_70681_au().nextInt(this.field_151493_a.func_70681_au().nextInt(1200) + 1200) + 1200;
      this.field_151493_a.func_70907_r().func_75270_a(false);
   }

   @Override
   public void func_75251_c() {
      this.field_151493_a.func_70904_g(false);
   }

   @Override
   public void func_75246_d() {
      ++this.field_151492_c;
      this.field_151493_a.func_70907_r().func_75270_a(false);
      if (this.field_151493_a.func_70092_e((double)this.field_151487_f, (double)(this.field_151488_g + 1), (double)this.field_151494_h) > 1.0) {
         this.field_151493_a.func_70904_g(false);
         this.field_151493_a
            .func_70661_as()
            .func_75492_a(
               (double)((float)this.field_151487_f) + 0.5, (double)(this.field_151488_g + 1), (double)((float)this.field_151494_h) + 0.5, this.field_151491_b
            );
         ++this.field_151489_d;
      } else if (!this.field_151493_a.func_70906_o()) {
         this.field_151493_a.func_70904_g(true);
      } else {
         --this.field_151489_d;
      }
   }

   private boolean func_151485_f() {
      int var1 = (int)this.field_151493_a.field_70163_u;
      double var2 = 2.147483647E9;

      for(int var4 = (int)this.field_151493_a.field_70165_t - 8; (double)var4 < this.field_151493_a.field_70165_t + 8.0; ++var4) {
         for(int var5 = (int)this.field_151493_a.field_70161_v - 8; (double)var5 < this.field_151493_a.field_70161_v + 8.0; ++var5) {
            if (this.func_151486_a(this.field_151493_a.field_70170_p, var4, var1, var5)
               && this.field_151493_a.field_70170_p.func_147437_c(var4, var1 + 1, var5)) {
               double var6 = this.field_151493_a.func_70092_e((double)var4, (double)var1, (double)var5);
               if (var6 < var2) {
                  this.field_151487_f = var4;
                  this.field_151488_g = var1;
                  this.field_151494_h = var5;
                  var2 = var6;
               }
            }
         }
      }

      return var2 < 2.147483647E9;
   }

   private boolean func_151486_a(World var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3, var4);
      int var6 = var1.func_72805_g(var2, var3, var4);
      if (var5 == Blocks.field_150486_ae) {
         TileEntityChest var7 = (TileEntityChest)var1.func_147438_o(var2, var3, var4);
         if (var7.field_145987_o < 1) {
            return true;
         }
      } else {
         if (var5 == Blocks.field_150470_am) {
            return true;
         }

         if (var5 == Blocks.field_150324_C && !BlockBed.func_149975_b(var6)) {
            return true;
         }
      }

      return false;
   }
}
