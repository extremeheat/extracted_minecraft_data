package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class LighthingBoltPredicate {
   public static final LighthingBoltPredicate ANY;
   private static final String BLOCKS_SET_ON_FIRE_KEY = "blocks_set_on_fire";
   private static final String ENTITY_STRUCK_KEY = "entity_struck";
   private final MinMaxBounds.Ints blocksSetOnFire;
   private final EntityPredicate entityStruck;

   private LighthingBoltPredicate(MinMaxBounds.Ints var1, EntityPredicate var2) {
      super();
      this.blocksSetOnFire = var1;
      this.entityStruck = var2;
   }

   public static LighthingBoltPredicate blockSetOnFire(MinMaxBounds.Ints var0) {
      return new LighthingBoltPredicate(var0, EntityPredicate.ANY);
   }

   public static LighthingBoltPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "lightning");
         return new LighthingBoltPredicate(MinMaxBounds.Ints.fromJson(var1.get("blocks_set_on_fire")), EntityPredicate.fromJson(var1.get("entity_struck")));
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("blocks_set_on_fire", this.blocksSetOnFire.serializeToJson());
         var1.add("entity_struck", this.entityStruck.serializeToJson());
         return var1;
      }
   }

   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (this == ANY) {
         return true;
      } else if (!(var1 instanceof LightningBolt)) {
         return false;
      } else {
         LightningBolt var4 = (LightningBolt)var1;
         return this.blocksSetOnFire.matches(var4.getBlocksSetOnFire()) && (this.entityStruck == EntityPredicate.ANY || var4.getHitEntities().anyMatch((var3x) -> {
            return this.entityStruck.matches(var2, var3, var3x);
         }));
      }
   }

   static {
      ANY = new LighthingBoltPredicate(MinMaxBounds.Ints.ANY, EntityPredicate.ANY);
   }
}
