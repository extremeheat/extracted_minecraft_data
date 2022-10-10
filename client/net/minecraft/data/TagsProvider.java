package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider {
   private static final Logger field_200436_d = LogManager.getLogger();
   private static final Gson field_200437_e = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator field_200433_a;
   protected final IRegistry<T> field_200435_c;
   protected final Map<Tag<T>, Tag.Builder<T>> field_200434_b = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator var1, IRegistry<T> var2) {
      super();
      this.field_200433_a = var1;
      this.field_200435_c = var2;
   }

   protected abstract void func_200432_c();

   public void func_200398_a(DirectoryCache var1) throws IOException {
      this.field_200434_b.clear();
      this.func_200432_c();
      TagCollection var2 = new TagCollection((var0) -> {
         return false;
      }, (var0) -> {
         return null;
      }, "", false, "generated");
      Iterator var3 = this.field_200434_b.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         ResourceLocation var5 = ((Tag)var4.getKey()).func_199886_b();
         Tag.Builder var10000 = (Tag.Builder)var4.getValue();
         var2.getClass();
         if (!var10000.func_200160_a(var2::func_199910_a)) {
            throw new UnsupportedOperationException("Unsupported referencing of tags!");
         }

         Tag var6 = ((Tag.Builder)var4.getValue()).func_200051_a(var5);
         IRegistry var10001 = this.field_200435_c;
         var10001.getClass();
         JsonObject var7 = var6.func_200571_a(var10001::func_177774_c);
         Path var8 = this.func_200431_a(var5);
         var2.func_199912_a(var6);
         this.func_200429_a(var2);

         try {
            String var9 = field_200437_e.toJson(var7);
            String var10 = field_208307_a.hashUnencodedChars(var9).toString();
            if (!Objects.equals(var1.func_208323_a(var8), var10) || !Files.exists(var8, new LinkOption[0])) {
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

            var1.func_208316_a(var8, var10);
         } catch (IOException var24) {
            field_200436_d.error("Couldn't save tags to {}", var8, var24);
         }
      }

   }

   protected abstract void func_200429_a(TagCollection<T> var1);

   protected abstract Path func_200431_a(ResourceLocation var1);

   protected Tag.Builder<T> func_200426_a(Tag<T> var1) {
      return (Tag.Builder)this.field_200434_b.computeIfAbsent(var1, (var0) -> {
         return Tag.Builder.func_200047_a();
      });
   }
}
