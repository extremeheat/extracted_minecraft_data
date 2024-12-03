package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class SpriteSourceList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter ATLAS_INFO_CONVERTER = new FileToIdConverter("atlases", ".json");
   private final List<SpriteSource> sources;

   private SpriteSourceList(List<SpriteSource> var1) {
      super();
      this.sources = var1;
   }

   public List<Function<SpriteResourceLoader, SpriteContents>> list(ResourceManager var1) {
      final HashMap var2 = new HashMap();
      SpriteSource.Output var3 = new SpriteSource.Output() {
         public void add(ResourceLocation var1, SpriteSource.SpriteSupplier var2x) {
            SpriteSource.SpriteSupplier var3 = (SpriteSource.SpriteSupplier)var2.put(var1, var2x);
            if (var3 != null) {
               var3.discard();
            }

         }

         public void removeAll(Predicate<ResourceLocation> var1) {
            Iterator var2x = var2.entrySet().iterator();

            while(var2x.hasNext()) {
               Map.Entry var3 = (Map.Entry)var2x.next();
               if (var1.test((ResourceLocation)var3.getKey())) {
                  ((SpriteSource.SpriteSupplier)var3.getValue()).discard();
                  var2x.remove();
               }
            }

         }
      };
      this.sources.forEach((var2x) -> var2x.run(var1, var3));
      ImmutableList.Builder var4 = ImmutableList.builder();
      var4.add((Function)(var0) -> MissingTextureAtlasSprite.create());
      var4.addAll(var2.values());
      return var4.build();
   }

   public static SpriteSourceList load(ResourceManager var0, ResourceLocation var1) {
      ResourceLocation var2 = ATLAS_INFO_CONVERTER.idToFile(var1);
      ArrayList var3 = new ArrayList();

      for(Resource var5 : var0.getResourceStack(var2)) {
         try {
            BufferedReader var6 = var5.openAsReader();

            try {
               Dynamic var7 = new Dynamic(JsonOps.INSTANCE, JsonParser.parseReader(var6));
               var3.addAll((Collection)SpriteSources.FILE_CODEC.parse(var7).getOrThrow());
            } catch (Throwable var10) {
               if (var6 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (var6 != null) {
               var6.close();
            }
         } catch (Exception var11) {
            LOGGER.error("Failed to parse atlas definition {} in pack {}", new Object[]{var2, var5.sourcePackId(), var11});
         }
      }

      return new SpriteSourceList(var3);
   }
}
