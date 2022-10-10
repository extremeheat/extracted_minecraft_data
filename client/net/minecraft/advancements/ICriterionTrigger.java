package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public interface ICriterionTrigger<T extends ICriterionInstance> {
   ResourceLocation func_192163_a();

   void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<T> var2);

   void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<T> var2);

   void func_192167_a(PlayerAdvancements var1);

   T func_192166_a(JsonObject var1, JsonDeserializationContext var2);

   public static class Listener<T extends ICriterionInstance> {
      private final T field_192160_a;
      private final Advancement field_192161_b;
      private final String field_192162_c;

      public Listener(T var1, Advancement var2, String var3) {
         super();
         this.field_192160_a = var1;
         this.field_192161_b = var2;
         this.field_192162_c = var3;
      }

      public T func_192158_a() {
         return this.field_192160_a;
      }

      public void func_192159_a(PlayerAdvancements var1) {
         var1.func_192750_a(this.field_192161_b, this.field_192162_c);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ICriterionTrigger.Listener var2 = (ICriterionTrigger.Listener)var1;
            if (!this.field_192160_a.equals(var2.field_192160_a)) {
               return false;
            } else {
               return !this.field_192161_b.equals(var2.field_192161_b) ? false : this.field_192162_c.equals(var2.field_192162_c);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.field_192160_a.hashCode();
         var1 = 31 * var1 + this.field_192161_b.hashCode();
         var1 = 31 * var1 + this.field_192162_c.hashCode();
         return var1;
      }
   }
}
