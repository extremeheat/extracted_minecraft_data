package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<ChanneledLightningTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ChanneledLightningTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public ChanneledLightningTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      EntityPredicate.Composite[] var4 = EntityPredicate.Composite.fromJsonArray(var1, "victims", var3);
      return new ChanneledLightningTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Collection<? extends Entity> var2) {
      List var3 = (List)var2.stream().map((var1x) -> {
         return EntityPredicate.createContext(var1, var1x);
      }).collect(Collectors.toList());
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var3);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite[] victims;

      public TriggerInstance(EntityPredicate.Composite var1, EntityPredicate.Composite[] var2) {
         super(ChanneledLightningTrigger.ID, var1);
         this.victims = var2;
      }

      public static ChanneledLightningTrigger.TriggerInstance channeledLightning(EntityPredicate... var0) {
         return new ChanneledLightningTrigger.TriggerInstance(EntityPredicate.Composite.ANY, (EntityPredicate.Composite[])Stream.of(var0).map(EntityPredicate.Composite::wrap).toArray((var0x) -> {
            return new EntityPredicate.Composite[var0x];
         }));
      }

      public boolean matches(Collection<? extends LootContext> var1) {
         EntityPredicate.Composite[] var2 = this.victims;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EntityPredicate.Composite var5 = var2[var4];
            boolean var6 = false;
            Iterator var7 = var1.iterator();

            while(var7.hasNext()) {
               LootContext var8 = (LootContext)var7.next();
               if (var5.matches(var8)) {
                  var6 = true;
                  break;
               }
            }

            if (!var6) {
               return false;
            }
         }

         return true;
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("victims", EntityPredicate.Composite.toJson(this.victims, var1));
         return var2;
      }
   }
}
