package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks {
   private static final Logger field_151506_a = LogManager.getLogger();
   private List<EntityAITasks.EntityAITaskEntry> field_75782_a = Lists.newArrayList();
   private List<EntityAITasks.EntityAITaskEntry> field_75780_b = Lists.newArrayList();
   private final Profiler field_75781_c;
   private int field_75778_d;
   private int field_75779_e = 3;

   public EntityAITasks(Profiler var1) {
      super();
      this.field_75781_c = var1;
   }

   public void func_75776_a(int var1, EntityAIBase var2) {
      this.field_75782_a.add(new EntityAITasks.EntityAITaskEntry(var1, var2));
   }

   public void func_85156_a(EntityAIBase var1) {
      Iterator var2 = this.field_75782_a.iterator();

      while(var2.hasNext()) {
         EntityAITasks.EntityAITaskEntry var3 = (EntityAITasks.EntityAITaskEntry)var2.next();
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
      this.field_75781_c.func_76320_a("goalSetup");
      Iterator var1;
      EntityAITasks.EntityAITaskEntry var2;
      if (this.field_75778_d++ % this.field_75779_e == 0) {
         var1 = this.field_75782_a.iterator();

         label50:
         while(true) {
            while(true) {
               if (!var1.hasNext()) {
                  break label50;
               }

               var2 = (EntityAITasks.EntityAITaskEntry)var1.next();
               boolean var3 = this.field_75780_b.contains(var2);
               if (!var3) {
                  break;
               }

               if (!this.func_75775_b(var2) || !this.func_75773_a(var2)) {
                  var2.field_75733_a.func_75251_c();
                  this.field_75780_b.remove(var2);
                  break;
               }
            }

            if (this.func_75775_b(var2) && var2.field_75733_a.func_75250_a()) {
               var2.field_75733_a.func_75249_e();
               this.field_75780_b.add(var2);
            }
         }
      } else {
         var1 = this.field_75780_b.iterator();

         while(var1.hasNext()) {
            var2 = (EntityAITasks.EntityAITaskEntry)var1.next();
            if (!this.func_75773_a(var2)) {
               var2.field_75733_a.func_75251_c();
               var1.remove();
            }
         }
      }

      this.field_75781_c.func_76319_b();
      this.field_75781_c.func_76320_a("goalTick");
      var1 = this.field_75780_b.iterator();

      while(var1.hasNext()) {
         var2 = (EntityAITasks.EntityAITaskEntry)var1.next();
         var2.field_75733_a.func_75246_d();
      }

      this.field_75781_c.func_76319_b();
   }

   private boolean func_75773_a(EntityAITasks.EntityAITaskEntry var1) {
      boolean var2 = var1.field_75733_a.func_75253_b();
      return var2;
   }

   private boolean func_75775_b(EntityAITasks.EntityAITaskEntry var1) {
      Iterator var2 = this.field_75782_a.iterator();

      while(var2.hasNext()) {
         EntityAITasks.EntityAITaskEntry var3 = (EntityAITasks.EntityAITaskEntry)var2.next();
         if (var3 != var1) {
            if (var1.field_75731_b >= var3.field_75731_b) {
               if (!this.func_75777_a(var1, var3) && this.field_75780_b.contains(var3)) {
                  return false;
               }
            } else if (!var3.field_75733_a.func_75252_g() && this.field_75780_b.contains(var3)) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean func_75777_a(EntityAITasks.EntityAITaskEntry var1, EntityAITasks.EntityAITaskEntry var2) {
      return (var1.field_75733_a.func_75247_h() & var2.field_75733_a.func_75247_h()) == 0;
   }

   class EntityAITaskEntry {
      public EntityAIBase field_75733_a;
      public int field_75731_b;

      public EntityAITaskEntry(int var2, EntityAIBase var3) {
         super();
         this.field_75731_b = var2;
         this.field_75733_a = var3;
      }
   }
}
