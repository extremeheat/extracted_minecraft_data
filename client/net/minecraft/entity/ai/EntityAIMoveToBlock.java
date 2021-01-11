package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityAIMoveToBlock extends EntityAIBase {
   private final EntityCreature field_179495_c;
   private final double field_179492_d;
   protected int field_179496_a;
   private int field_179493_e;
   private int field_179490_f;
   protected BlockPos field_179494_b;
   private boolean field_179491_g;
   private int field_179497_h;

   public EntityAIMoveToBlock(EntityCreature var1, double var2, int var4) {
      super();
      this.field_179494_b = BlockPos.field_177992_a;
      this.field_179495_c = var1;
      this.field_179492_d = var2;
      this.field_179497_h = var4;
      this.func_75248_a(5);
   }

   public boolean func_75250_a() {
      if (this.field_179496_a > 0) {
         --this.field_179496_a;
         return false;
      } else {
         this.field_179496_a = 200 + this.field_179495_c.func_70681_au().nextInt(200);
         return this.func_179489_g();
      }
   }

   public boolean func_75253_b() {
      return this.field_179493_e >= -this.field_179490_f && this.field_179493_e <= 1200 && this.func_179488_a(this.field_179495_c.field_70170_p, this.field_179494_b);
   }

   public void func_75249_e() {
      this.field_179495_c.func_70661_as().func_75492_a((double)((float)this.field_179494_b.func_177958_n()) + 0.5D, (double)(this.field_179494_b.func_177956_o() + 1), (double)((float)this.field_179494_b.func_177952_p()) + 0.5D, this.field_179492_d);
      this.field_179493_e = 0;
      this.field_179490_f = this.field_179495_c.func_70681_au().nextInt(this.field_179495_c.func_70681_au().nextInt(1200) + 1200) + 1200;
   }

   public void func_75251_c() {
   }

   public void func_75246_d() {
      if (this.field_179495_c.func_174831_c(this.field_179494_b.func_177984_a()) > 1.0D) {
         this.field_179491_g = false;
         ++this.field_179493_e;
         if (this.field_179493_e % 40 == 0) {
            this.field_179495_c.func_70661_as().func_75492_a((double)((float)this.field_179494_b.func_177958_n()) + 0.5D, (double)(this.field_179494_b.func_177956_o() + 1), (double)((float)this.field_179494_b.func_177952_p()) + 0.5D, this.field_179492_d);
         }
      } else {
         this.field_179491_g = true;
         --this.field_179493_e;
      }

   }

   protected boolean func_179487_f() {
      return this.field_179491_g;
   }

   private boolean func_179489_g() {
      int var1 = this.field_179497_h;
      boolean var2 = true;
      BlockPos var3 = new BlockPos(this.field_179495_c);

      for(int var4 = 0; var4 <= 1; var4 = var4 > 0 ? -var4 : 1 - var4) {
         for(int var5 = 0; var5 < var1; ++var5) {
            for(int var6 = 0; var6 <= var5; var6 = var6 > 0 ? -var6 : 1 - var6) {
               for(int var7 = var6 < var5 && var6 > -var5 ? var5 : 0; var7 <= var5; var7 = var7 > 0 ? -var7 : 1 - var7) {
                  BlockPos var8 = var3.func_177982_a(var6, var4 - 1, var7);
                  if (this.field_179495_c.func_180485_d(var8) && this.func_179488_a(this.field_179495_c.field_70170_p, var8)) {
                     this.field_179494_b = var8;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected abstract boolean func_179488_a(World var1, BlockPos var2);
}
