package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum GenLayerRareBiome implements IC1Transformer {
   INSTANCE;

   private static final int field_202717_b = IRegistry.field_212624_m.func_148757_b(Biomes.field_76772_c);
   private static final int field_202718_c = IRegistry.field_212624_m.func_148757_b(Biomes.field_185441_Q);

   private GenLayerRareBiome() {
   }

   public int func_202716_a(IContext var1, int var2) {
      return var1.func_202696_a(57) == 0 && var2 == field_202717_b ? field_202718_c : var2;
   }
}
