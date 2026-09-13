package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks {
   private static final Logger field_151506_a = LogManager.getLogger();
   private List field_75782_a = new ArrayList();
   private List field_75780_b = new ArrayList();
   private final Profiler field_75781_c;
   private int field_75778_d;
   private int field_75779_e = 3;

   public EntityAITasks(Profiler var1) {
      super();
      this.field_75781_c = var1;
   }

   public void func_75776_a(int var1, EntityAIBase var2) {
      this.field_75782_a.add(new EntityAITasks$EntityAITaskEntry(this, var1, var2));
   }

   public void func_85156_a(EntityAIBase var1) {
      Iterator var2 = this.field_75782_a.iterator();

      while(var2.hasNext()) {
         EntityAITasks$EntityAITaskEntry var3 = (EntityAITasks$EntityAITaskEntry)var2.next();
         EntityAIBase var4 = var3.field_75733_a;
         if (var4 == var1) {
            if (this.field_75780_b.contains(var3)) {
               var4.func_75251_c();
               this.field_75780_b.remove(var3);
            }

            var2.remove();
         }
      }
   }

   public void func_75774_a() {
      ArrayList var1 = new ArrayList();
      if (this.field_75778_d++ % this.field_75779_e == 0) {
         for(EntityAITasks$EntityAITaskEntry var3 : this.field_75782_a) {
            boolean var4 = this.field_75780_b.contains(var3);
            if (var4) {
               if (this.func_75775_b(var3) && this.func_75773_a(var3)) {
                  continue;
               }

               var3.field_75733_a.func_75251_c();
               this.field_75780_b.remove(var3);
            }

            if (this.func_75775_b(var3) && var3.field_75733_a.func_75250_a()) {
               var1.add(var3);
               this.field_75780_b.add(var3);
            }
         }
      } else {
         Iterator var5 = this.field_75780_b.iterator();

         while(var5.hasNext()) {
            EntityAITasks$EntityAITaskEntry var8 = (EntityAITasks$EntityAITaskEntry)var5.next();
            if (!var8.field_75733_a.func_75253_b()) {
               var8.field_75733_a.func_75251_c();
               var5.remove();
            }
         }
      }

      this.field_75781_c.func_76320_a("goalStart");

      for(EntityAITasks$EntityAITaskEntry var9 : var1) {
         this.field_75781_c.func_76320_a(var9.field_75733_a.getClass().getSimpleName());
         var9.field_75733_a.func_75249_e();
         this.field_75781_c.func_76319_b();
      }

      this.field_75781_c.func_76319_b();
      this.field_75781_c.func_76320_a("goalTick");

      for(EntityAITasks$EntityAITaskEntry var10 : this.field_75780_b) {
         var10.field_75733_a.func_75246_d();
      }

      this.field_75781_c.func_76319_b();
   }

   private boolean func_75773_a(EntityAITasks$EntityAITaskEntry var1) {
      this.field_75781_c.func_76320_a("canContinue");
      boolean var2 = var1.field_75733_a.func_75253_b();
      this.field_75781_c.func_76319_b();
      return var2;
   }

   private boolean func_75775_b(EntityAITasks$EntityAITaskEntry var1) {
      this.field_75781_c.func_76320_a("canUse");

      for(EntityAITasks$EntityAITaskEntry var3 : this.field_75782_a) {
         if (var3 != var1) {
            if (var1.field_75731_b >= var3.field_75731_b) {
               if (this.field_75780_b.contains(var3) && !this.func_75777_a(var1, var3)) {
                  this.field_75781_c.func_76319_b();
                  return false;
               }
            } else if (this.field_75780_b.contains(var3) && !var3.field_75733_a.func_75252_g()) {
               this.field_75781_c.func_76319_b();
               return false;
            }
         }
      }

      this.field_75781_c.func_76319_b();
      return true;
   }

   private boolean func_75777_a(EntityAITasks$EntityAITaskEntry var1, EntityAITasks$EntityAITaskEntry var2) {
      return (var1.field_75733_a.func_75247_h() & var2.field_75733_a.func_75247_h()) == 0;
   }
}
