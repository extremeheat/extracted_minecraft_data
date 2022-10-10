package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.util.ResourceLocation;

public class ImpossibleTrigger implements ICriterionTrigger<ImpossibleTrigger.Instance> {
   private static final ResourceLocation field_192205_a = new ResourceLocation("impossible");

   public ImpossibleTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192205_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<ImpossibleTrigger.Instance> var2) {
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<ImpossibleTrigger.Instance> var2) {
   }

   public void func_192167_a(PlayerAdvancements var1) {
   }

   public ImpossibleTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return new ImpossibleTrigger.Instance();
   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   public static class Instance extends AbstractCriterionInstance {
      public Instance() {
         super(ImpossibleTrigger.field_192205_a);
      }
   }
}
