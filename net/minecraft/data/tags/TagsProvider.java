package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final Registry registry;
   protected final Map builders = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator var1, Registry var2) {
      this.generator = var1;
      this.registry = var2;
   }

   protected abstract void addTags();

   public void run(HashCache var1) {
      this.builders.clear();
      this.addTags();
      TagCollection var2 = new TagCollection((var0) -> {
         return Optional.empty();
      }, "", false, "generated");
      Map var3 = (Map)this.builders.entrySet().stream().collect(Collectors.toMap((var0) -> {
         return ((Tag)var0.getKey()).getId();
      }, Entry::getValue));
      var2.load(var3);
      var2.getAllTags().forEach((var2x, var3x) -> {
         Registry var10001 = this.registry;
         var10001.getClass();
         JsonObject var4 = var3x.serializeToJson(var10001::getKey);
         Path var5 = this.getPath(var2x);

         try {
            String var6 = GSON.toJson(var4);
            String var7 = SHA1.hashUnencodedChars(var6).toString();
            if (!Objects.equals(var1.getHash(var5), var7) || !Files.exists(var5, new LinkOption[0])) {
               Files.createDirectories(var5.getParent());
               BufferedWriter var8 = Files.newBufferedWriter(var5);
               Throwable var9 = null;

               try {
                  var8.write(var6);
               } catch (Throwable var19) {
                  var9 = var19;
                  throw var19;
               } finally {
                  if (var8 != null) {
                     if (var9 != null) {
                        try {
                           var8.close();
                        } catch (Throwable var18) {
                           var9.addSuppressed(var18);
                        }
                     } else {
                        var8.close();
                     }
                  }

               }
            }

            var1.putNew(var5, var7);
         } catch (IOException var21) {
            LOGGER.error("Couldn't save tags to {}", var5, var21);
         }

      });
      this.useTags(var2);
   }

   protected abstract void useTags(TagCollection var1);

   protected abstract Path getPath(ResourceLocation var1);

   protected Tag.Builder tag(Tag var1) {
      return (Tag.Builder)this.builders.computeIfAbsent(var1, (var0) -> {
         return Tag.Builder.tag();
      });
   }
}
