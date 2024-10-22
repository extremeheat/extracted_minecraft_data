package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BakedOverrides {
   public static final BakedOverrides EMPTY = new BakedOverrides();
   public static final float NO_OVERRIDE = -1.0F / 0.0F;
   private final BakedOverrides.BakedOverride[] overrides;
   private final ResourceLocation[] properties;

   private BakedOverrides() {
      super();
      this.overrides = new BakedOverrides.BakedOverride[0];
      this.properties = new ResourceLocation[0];
   }

   public BakedOverrides(ModelBaker var1, List<ItemOverride> var2) {
      super();
      this.properties = var2.stream()
         .flatMap(var0 -> var0.predicates().stream())
         .map(ItemOverride.Predicate::property)
         .distinct()
         .toArray(ResourceLocation[]::new);
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();

      for (int var4 = 0; var4 < this.properties.length; var4++) {
         var3.put(this.properties[var4], var4);
      }

      ArrayList var9 = Lists.newArrayList();

      for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
         ItemOverride var6 = (ItemOverride)var2.get(var5);
         BakedModel var7 = var1.bake(var6.model(), BlockModelRotation.X0_Y0);
         BakedOverrides.PropertyMatcher[] var8 = var6.predicates().stream().map(var1x -> {
            int var2x = var3.getInt(var1x.property());
            return new BakedOverrides.PropertyMatcher(var2x, var1x.value());
         }).toArray(BakedOverrides.PropertyMatcher[]::new);
         var9.add(new BakedOverrides.BakedOverride(var8, var7));
      }

      this.overrides = var9.toArray(new BakedOverrides.BakedOverride[0]);
   }

   @Nullable
   public BakedModel findOverride(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      int var5 = this.properties.length;
      if (var5 != 0) {
         float[] var6 = new float[var5];

         for (int var7 = 0; var7 < var5; var7++) {
            ResourceLocation var8 = this.properties[var7];
            ItemPropertyFunction var9 = ItemProperties.getProperty(var1, var8);
            if (var9 != null) {
               var6[var7] = var9.call(var1, var2, var3, var4);
            } else {
               var6[var7] = -1.0F / 0.0F;
            }
         }

         for (BakedOverrides.BakedOverride var10 : this.overrides) {
            if (var10.test(var6)) {
               return var10.model;
            }
         }
      }

      return null;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
