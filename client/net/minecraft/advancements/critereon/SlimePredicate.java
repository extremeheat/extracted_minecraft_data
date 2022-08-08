package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;

public class SlimePredicate implements EntitySubPredicate {
   private final MinMaxBounds.Ints size;

   private SlimePredicate(MinMaxBounds.Ints var1) {
      super();
      this.size = var1;
   }

   public static SlimePredicate sized(MinMaxBounds.Ints var0) {
      return new SlimePredicate(var0);
   }

   public static SlimePredicate fromJson(JsonObject var0) {
      MinMaxBounds.Ints var1 = MinMaxBounds.Ints.fromJson(var0.get("size"));
      return new SlimePredicate(var1);
   }

   public JsonObject serializeCustomData() {
      JsonObject var1 = new JsonObject();
      var1.add("size", this.size.serializeToJson());
      return var1;
   }

   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (var1 instanceof Slime var4) {
         return this.size.matches(var4.getSize());
      } else {
         return false;
      }
   }

   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.SLIME;
   }
}
