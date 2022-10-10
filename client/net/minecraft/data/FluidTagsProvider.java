package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class FluidTagsProvider extends TagsProvider<Fluid> {
   public FluidTagsProvider(DataGenerator var1) {
      super(var1, IRegistry.field_212619_h);
   }

   protected void func_200432_c() {
      this.func_200426_a(FluidTags.field_206959_a).func_200573_a(Fluids.field_204546_a, Fluids.field_207212_b);
      this.func_200426_a(FluidTags.field_206960_b).func_200573_a(Fluids.field_204547_b, Fluids.field_207213_d);
   }

   protected Path func_200431_a(ResourceLocation var1) {
      return this.field_200433_a.func_200391_b().resolve("data/" + var1.func_110624_b() + "/tags/fluids/" + var1.func_110623_a() + ".json");
   }

   public String func_200397_b() {
      return "Fluid Tags";
   }

   protected void func_200429_a(TagCollection<Fluid> var1) {
      FluidTags.func_206953_a(var1);
   }
}
