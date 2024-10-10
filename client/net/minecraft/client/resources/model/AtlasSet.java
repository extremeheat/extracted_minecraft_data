package net.minecraft.client.resources.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AtlasSet implements AutoCloseable {
   private final Map<ResourceLocation, AtlasSet.AtlasEntry> atlases;

   public AtlasSet(Map<ResourceLocation, ResourceLocation> var1, TextureManager var2) {
      super();
      this.atlases = var1.entrySet().stream().collect(Collectors.toMap(Entry::getKey, var1x -> {
         TextureAtlas var2x = new TextureAtlas((ResourceLocation)var1x.getKey());
         var2.register((ResourceLocation)var1x.getKey(), var2x);
         return new AtlasSet.AtlasEntry(var2x, (ResourceLocation)var1x.getValue());
      }));
   }

   public TextureAtlas getAtlas(ResourceLocation var1) {
      return this.atlases.get(var1).atlas();
   }

   @Override
   public void close() {
      this.atlases.values().forEach(AtlasSet.AtlasEntry::close);
      this.atlases.clear();
   }

   public Map<ResourceLocation, CompletableFuture<AtlasSet.StitchResult>> scheduleLoad(ResourceManager var1, int var2, Executor var3) {
      return this.atlases
         .entrySet()
         .stream()
         .collect(
            Collectors.toMap(
               Entry::getKey,
               var3x -> {
                  AtlasSet.AtlasEntry var4 = var3x.getValue();
                  return SpriteLoader.create(var4.atlas)
                     .loadAndStitch(var1, var4.atlasInfoLocation, var2, var3)
                     .thenApply(var1xx -> new AtlasSet.StitchResult(var4.atlas, var1xx));
               }
            )
         );
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public static class StitchResult {
      private final TextureAtlas atlas;
      private final SpriteLoader.Preparations preparations;

      public StitchResult(TextureAtlas var1, SpriteLoader.Preparations var2) {
         super();
         this.atlas = var1;
         this.preparations = var2;
      }

      @Nullable
      public TextureAtlasSprite getSprite(ResourceLocation var1) {
         return this.preparations.regions().get(var1);
      }

      public TextureAtlasSprite missing() {
         return this.preparations.missing();
      }

      public CompletableFuture<Void> readyForUpload() {
         return this.preparations.readyForUpload();
      }

      public void upload() {
         this.atlas.upload(this.preparations);
      }
   }
}
