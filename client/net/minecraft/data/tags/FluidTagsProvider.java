package net.minecraft.data.tags;

import java.nio.file.Path;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidTagsProvider extends TagsProvider<Fluid> {
   public FluidTagsProvider(DataGenerator var1) {
      super(var1, Registry.FLUID);
   }

   protected void addTags() {
      this.tag(FluidTags.WATER).add((Object[])(Fluids.WATER, Fluids.FLOWING_WATER));
      this.tag(FluidTags.LAVA).add((Object[])(Fluids.LAVA, Fluids.FLOWING_LAVA));
   }

   protected Path getPath(ResourceLocation var1) {
      Path var10000 = this.generator.getOutputFolder();
      String var10001 = var1.getNamespace();
      return var10000.resolve("data/" + var10001 + "/tags/fluids/" + var1.getPath() + ".json");
   }

   public String getName() {
      return "Fluid Tags";
   }
}
