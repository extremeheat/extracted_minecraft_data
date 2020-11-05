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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final Registry<T> registry;
   private final Map<ResourceLocation, Tag.Builder> builders = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator var1, Registry<T> var2) {
      super();
      this.generator = var1;
      this.registry = var2;
   }

   protected abstract void addTags();

   public void run(HashCache var1) {
      this.builders.clear();
      this.addTags();
      SetTag var2 = SetTag.empty();
      Function var3 = (var2x) -> {
         return this.builders.containsKey(var2x) ? var2 : null;
      };
      Function var4 = (var1x) -> {
         return this.registry.getOptional(var1x).orElse((Object)null);
      };
      this.builders.forEach((var4x, var5) -> {
         List var6 = (List)var5.getUnresolvedEntries(var3, var4).collect(Collectors.toList());
         if (!var6.isEmpty()) {
            throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", var4x, var6.stream().map(Objects::toString).collect(Collectors.joining(","))));
         } else {
            JsonObject var7 = var5.serializeToJson();
            Path var8 = this.getPath(var4x);

            try {
               String var9 = GSON.toJson(var7);
               String var10 = SHA1.hashUnencodedChars(var9).toString();
               if (!Objects.equals(var1.getHash(var8), var10) || !Files.exists(var8, new LinkOption[0])) {
                  Files.createDirectories(var8.getParent());
                  BufferedWriter var11 = Files.newBufferedWriter(var8);
                  Throwable var12 = null;

                  try {
                     var11.write(var9);
                  } catch (Throwable var22) {
                     var12 = var22;
                     throw var22;
                  } finally {
                     if (var11 != null) {
                        if (var12 != null) {
                           try {
                              var11.close();
                           } catch (Throwable var21) {
                              var12.addSuppressed(var21);
                           }
                        } else {
                           var11.close();
                        }
                     }

                  }
               }

               var1.putNew(var8, var10);
            } catch (IOException var24) {
               LOGGER.error("Couldn't save tags to {}", var8, var24);
            }

         }
      });
   }

   protected abstract Path getPath(ResourceLocation var1);

   protected TagsProvider.TagAppender<T> tag(Tag.Named<T> var1) {
      Tag.Builder var2 = this.getOrCreateRawBuilder(var1);
      return new TagsProvider.TagAppender(var2, this.registry, "vanilla");
   }

   protected Tag.Builder getOrCreateRawBuilder(Tag.Named<T> var1) {
      return (Tag.Builder)this.builders.computeIfAbsent(var1.getName(), (var0) -> {
         return new Tag.Builder();
      });
   }

   public static class TagAppender<T> {
      private final Tag.Builder builder;
      private final Registry<T> registry;
      private final String source;

      private TagAppender(Tag.Builder var1, Registry<T> var2, String var3) {
         super();
         this.builder = var1;
         this.registry = var2;
         this.source = var3;
      }

      public TagsProvider.TagAppender<T> add(T var1) {
         this.builder.addElement(this.registry.getKey(var1), this.source);
         return this;
      }

      public TagsProvider.TagAppender<T> addTag(Tag.Named<T> var1) {
         this.builder.addTag(var1.getName(), this.source);
         return this;
      }

      @SafeVarargs
      public final TagsProvider.TagAppender<T> add(T... var1) {
         Stream var10000 = Stream.of(var1);
         Registry var10001 = this.registry;
         var10001.getClass();
         var10000.map(var10001::getKey).forEach((var1x) -> {
            this.builder.addElement(var1x, this.source);
         });
         return this;
      }

      // $FF: synthetic method
      TagAppender(Tag.Builder var1, Registry var2, String var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
