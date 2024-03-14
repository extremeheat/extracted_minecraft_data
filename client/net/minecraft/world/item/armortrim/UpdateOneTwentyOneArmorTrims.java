package net.minecraft.world.item.armortrim;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;

public class UpdateOneTwentyOneArmorTrims {
   public UpdateOneTwentyOneArmorTrims() {
      super();
   }

   public static void bootstrap(BootstrapContext<TrimPattern> var0) {
      TrimPatterns.register(var0, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.FLOW);
      TrimPatterns.register(var0, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.BOLT);
   }
}
