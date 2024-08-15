package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.level.block.state.BlockState;

public class MultiPart implements UnbakedBlockStateModel {
   private final List<MultiPart.InstantiatedSelector> selectors;

   MultiPart(List<MultiPart.InstantiatedSelector> var1) {
      super();
      this.selectors = var1;
   }

   @Override
   public Object visualEqualityGroup(BlockState var1) {
      IntArrayList var2 = new IntArrayList();

      for (int var3 = 0; var3 < this.selectors.size(); var3++) {
         if (this.selectors.get(var3).predicate.test(var1)) {
            var2.add(var3);
         }
      }

      record 1Key(MultiPart model, IntList selectors) {
         _Key/* $VF was: 1Key*/(IntList selectors) {
            super();
            this.selectors = selectors;
         }
      }

      return new 1Key(var2);
   }

   @Override
   public void resolveDependencies(UnbakedModel.Resolver var1, UnbakedModel.ResolutionContext var2) {
      this.selectors.forEach(var2x -> var2x.variant.resolveDependencies(var1, var2));
   }

   @Nullable
   @Override
   public BakedModel bake(ModelBaker var1, Function<Material, TextureAtlasSprite> var2, ModelState var3) {
      MultiPartBakedModel.Builder var4 = new MultiPartBakedModel.Builder();

      for (MultiPart.InstantiatedSelector var6 : this.selectors) {
         BakedModel var7 = var6.variant.bake(var1, var2, var3);
         if (var7 != null) {
            var4.add(var6.predicate, var7);
         }
      }

      return var4.build();
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

   public static class Deserializer implements JsonDeserializer<MultiPart.Definition> {
      public Deserializer() {
         super();
      }

      public MultiPart.Definition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return new MultiPart.Definition(this.getSelectors(var3, var1.getAsJsonArray()));
      }

      private List<Selector> getSelectors(JsonDeserializationContext var1, JsonArray var2) {
         ArrayList var3 = Lists.newArrayList();

         for (JsonElement var5 : var2) {
            var3.add((Selector)var1.deserialize(var5, Selector.class));
         }

         return var3;
      }
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
}
