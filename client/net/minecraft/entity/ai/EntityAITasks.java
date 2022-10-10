package net.minecraft.entity.ai;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks {
   private static final Logger field_151506_a = LogManager.getLogger();
   private final Set<EntityAITasks.EntityAITaskEntry> field_75782_a = Sets.newLinkedHashSet();
   private final Set<EntityAITasks.EntityAITaskEntry> field_75780_b = Sets.newLinkedHashSet();
   private final Profiler field_75781_c;
   private int field_75778_d;
   private int field_75779_e = 3;
   private int field_188529_g;

   public EntityAITasks(Profiler var1) {
      super();
      this.field_75781_c = var1;
   }

   public void func_75776_a(int var1, EntityAIBase var2) {
      this.field_75782_a.add(new EntityAITasks.EntityAITaskEntry(var1, var2));
   }

   public void func_85156_a(EntityAIBase var1) {
      Iterator var2 = this.field_75782_a.iterator();

      EntityAITasks.EntityAITaskEntry var3;
      EntityAIBase var4;
      do {
         if (!var2.hasNext()) {
            return;
         }

         var3 = (EntityAITasks.EntityAITaskEntry)var2.next();
         var4 = var3.field_75733_a;
      } while(var4 != var1);

      if (var3.field_188524_c) {
         var3.field_188524_c = false;
         var3.field_75733_a.func_75251_c();
         this.field_75780_b.remove(var3);
      }

      var2.remove();
   }

   public void func_75774_a() {
      this.field_75781_c.func_76320_a("goalSetup");
      Iterator var1;
      EntityAITasks.EntityAITaskEntry var2;
      if (this.field_75778_d++ % this.field_75779_e == 0) {
         var1 = this.field_75782_a.iterator();

         label57:
         while(true) {
            do {
               while(true) {
                  if (!var1.hasNext()) {
                     break label57;
                  }

                  var2 = (EntityAITasks.EntityAITaskEntry)var1.next();
                  if (var2.field_188524_c) {
                     break;
                  }

                  if (this.func_75775_b(var2) && var2.field_75733_a.func_75250_a()) {
                     var2.field_188524_c = true;
                     var2.field_75733_a.func_75249_e();
                     this.field_75780_b.add(var2);
                  }
               }
            } while(this.func_75775_b(var2) && this.func_75773_a(var2));

            var2.field_188524_c = false;
            var2.field_75733_a.func_75251_c();
            this.field_75780_b.remove(var2);
         }
      } else {
         var1 = this.field_75780_b.iterator();

         while(var1.hasNext()) {
            var2 = (EntityAITasks.EntityAITaskEntry)var1.next();
            if (!this.func_75773_a(var2)) {
               var2.field_188524_c = false;
               var2.field_75733_a.func_75251_c();
               var1.remove();
            }
         }
      }

      this.field_75781_c.func_76319_b();
      if (!this.field_75780_b.isEmpty()) {
         this.field_75781_c.func_76320_a("goalTick");
         var1 = this.field_75780_b.iterator();

         while(var1.hasNext()) {
            var2 = (EntityAITasks.EntityAITaskEntry)var1.next();
            var2.field_75733_a.func_75246_d();
         }

         this.field_75781_c.func_76319_b();
      }

   }

   private boolean func_75773_a(EntityAITasks.EntityAITaskEntry var1) {
      return var1.field_75733_a.func_75253_b();
   }

   private boolean func_75775_b(EntityAITasks.EntityAITaskEntry var1) {
      if (this.field_75780_b.isEmpty()) {
         return true;
      } else if (this.func_188528_b(var1.field_75733_a.func_75247_h())) {
         return false;
      } else {
         Iterator var2 = this.field_75780_b.iterator();

         while(var2.hasNext()) {
            EntityAITasks.EntityAITaskEntry var3 = (EntityAITasks.EntityAITaskEntry)var2.next();
            if (var3 != var1) {
               if (var1.field_75731_b >= var3.field_75731_b) {
                  if (!this.func_75777_a(var1, var3)) {
                     return false;
                  }
               } else if (!var3.field_75733_a.func_75252_g()) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private boolean func_75777_a(EntityAITasks.EntityAITaskEntry var1, EntityAITasks.EntityAITaskEntry var2) {
      return (var1.field_75733_a.func_75247_h() & var2.field_75733_a.func_75247_h()) == 0;
   }

   public boolean func_188528_b(int var1) {
      return (this.field_188529_g & var1) > 0;
   }

   public void func_188526_c(int var1) {
      this.field_188529_g |= var1;
   }

   public void func_188525_d(int var1) {
      this.field_188529_g &= ~var1;
   }

   public void func_188527_a(int var1, boolean var2) {
      if (var2) {
         this.func_188525_d(var1);
      } else {
         this.func_188526_c(var1);
      }

   }

   class EntityAITaskEntry {
      public final EntityAIBase field_75733_a;
      public final int field_75731_b;
      public boolean field_188524_c;

      public EntityAITaskEntry(int var2, EntityAIBase var3) {
         super();
         this.field_75731_b = var2;
         this.field_75733_a = var3;
      }

      public boolean equals(@Nullable Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 != null && this.getClass() == var1.getClass() ? this.field_75733_a.equals(((EntityAITasks.EntityAITaskEntry)var1).field_75733_a) : false;
         }
      }

      public int hashCode() {
         return this.field_75733_a.hashCode();
      }
   }
}
