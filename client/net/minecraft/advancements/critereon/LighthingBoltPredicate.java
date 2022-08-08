package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class LighthingBoltPredicate implements EntitySubPredicate {
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

   public static LighthingBoltPredicate fromJson(JsonObject var0) {
      return new LighthingBoltPredicate(MinMaxBounds.Ints.fromJson(var0.get("blocks_set_on_fire")), EntityPredicate.fromJson(var0.get("entity_struck")));
   }

   public JsonObject serializeCustomData() {
      JsonObject var1 = new JsonObject();
      var1.add("blocks_set_on_fire", this.blocksSetOnFire.serializeToJson());
      var1.add("entity_struck", this.entityStruck.serializeToJson());
      return var1;
   }

   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.LIGHTNING;
   }

   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (!(var1 instanceof LightningBolt var4)) {
         return false;
      } else {
         return this.blocksSetOnFire.matches(var4.getBlocksSetOnFire()) && (this.entityStruck == EntityPredicate.ANY || var4.getHitEntities().anyMatch((var3x) -> {
            return this.entityStruck.matches(var2, var3, var3x);
         }));
      }
   }
}
