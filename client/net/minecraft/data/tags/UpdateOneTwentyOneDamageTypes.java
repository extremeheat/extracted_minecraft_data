package net.minecraft.data.tags;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

public class UpdateOneTwentyOneDamageTypes {
   public UpdateOneTwentyOneDamageTypes() {
      super();
   }

   public static void bootstrap(BootstrapContext<DamageType> var0) {
      var0.register(DamageTypes.WIND_CHARGE, new DamageType("mob", 0.1F));
   }
}
