package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.model.geom.ModelPart;

public class Model {
   public final List<ModelPart> cubes = Lists.newArrayList();
   public int texWidth = 64;
   public int texHeight = 32;

   public Model() {
      super();
   }

   public ModelPart getRandomModelPart(Random var1) {
      return (ModelPart)this.cubes.get(var1.nextInt(this.cubes.size()));
   }
}
