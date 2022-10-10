package net.minecraft.advancements;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementList {
   private static final Logger field_192091_a = LogManager.getLogger();
   private final Map<ResourceLocation, Advancement> field_192092_b = Maps.newHashMap();
   private final Set<Advancement> field_192093_c = Sets.newLinkedHashSet();
   private final Set<Advancement> field_192094_d = Sets.newLinkedHashSet();
   private AdvancementList.Listener field_192095_e;

   public AdvancementList() {
      super();
   }

   private void func_192090_a(Advancement var1) {
      Iterator var2 = var1.func_192069_e().iterator();

      while(var2.hasNext()) {
         Advancement var3 = (Advancement)var2.next();
         this.func_192090_a(var3);
      }

      field_192091_a.info("Forgot about advancement {}", var1.func_192067_g());
      this.field_192092_b.remove(var1.func_192067_g());
      if (var1.func_192070_b() == null) {
         this.field_192093_c.remove(var1);
         if (this.field_192095_e != null) {
            this.field_192095_e.func_191928_b(var1);
         }
      } else {
         this.field_192094_d.remove(var1);
         if (this.field_192095_e != null) {
            this.field_192095_e.func_191929_d(var1);
         }
      }

   }

   public void func_192085_a(Set<ResourceLocation> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ResourceLocation var3 = (ResourceLocation)var2.next();
         Advancement var4 = (Advancement)this.field_192092_b.get(var3);
         if (var4 == null) {
            field_192091_a.warn("Told to remove advancement {} but I don't know what that is", var3);
         } else {
            this.func_192090_a(var4);
         }
      }

   }

   public void func_192083_a(Map<ResourceLocation, Advancement.Builder> var1) {
      Function var2 = Functions.forMap(this.field_192092_b, (Object)null);

      label42:
      while(!var1.isEmpty()) {
         boolean var3 = false;
         Iterator var4 = var1.entrySet().iterator();

         Entry var5;
         while(var4.hasNext()) {
            var5 = (Entry)var4.next();
            ResourceLocation var6 = (ResourceLocation)var5.getKey();
            Advancement.Builder var7 = (Advancement.Builder)var5.getValue();
            if (var7.func_192058_a(var2)) {
               Advancement var8 = var7.func_192056_a(var6);
               this.field_192092_b.put(var6, var8);
               var3 = true;
               var4.remove();
               if (var8.func_192070_b() == null) {
                  this.field_192093_c.add(var8);
                  if (this.field_192095_e != null) {
                     this.field_192095_e.func_191931_a(var8);
                  }
               } else {
                  this.field_192094_d.add(var8);
                  if (this.field_192095_e != null) {
                     this.field_192095_e.func_191932_c(var8);
                  }
               }
            }
         }

         if (!var3) {
            var4 = var1.entrySet().iterator();

            while(true) {
               if (!var4.hasNext()) {
                  break label42;
               }

               var5 = (Entry)var4.next();
               field_192091_a.error("Couldn't load advancement {}: {}", var5.getKey(), var5.getValue());
            }
         }
      }

      field_192091_a.info("Loaded {} advancements", this.field_192092_b.size());
   }

   public void func_192087_a() {
      this.field_192092_b.clear();
      this.field_192093_c.clear();
      this.field_192094_d.clear();
      if (this.field_192095_e != null) {
         this.field_192095_e.func_191930_a();
      }

   }

   public Iterable<Advancement> func_192088_b() {
      return this.field_192093_c;
   }

   public Collection<Advancement> func_195651_c() {
      return this.field_192092_b.values();
   }

   @Nullable
   public Advancement func_192084_a(ResourceLocation var1) {
      return (Advancement)this.field_192092_b.get(var1);
   }

   public void func_192086_a(@Nullable AdvancementList.Listener var1) {
      this.field_192095_e = var1;
      if (var1 != null) {
         Iterator var2 = this.field_192093_c.iterator();

         Advancement var3;
         while(var2.hasNext()) {
            var3 = (Advancement)var2.next();
            var1.func_191931_a(var3);
         }

         var2 = this.field_192094_d.iterator();

         while(var2.hasNext()) {
            var3 = (Advancement)var2.next();
            var1.func_191932_c(var3);
         }
      }

   }

   public interface Listener {
      void func_191931_a(Advancement var1);

      void func_191928_b(Advancement var1);

      void func_191932_c(Advancement var1);

      void func_191929_d(Advancement var1);

      void func_191930_a();
   }
}
