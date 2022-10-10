package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
   private static final Logger field_192753_a = LogManager.getLogger();
   private static final Gson field_192754_b = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).setPrettyPrinting().create();
   private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> field_192755_c = new TypeToken<Map<ResourceLocation, AdvancementProgress>>() {
   };
   private final MinecraftServer field_192756_d;
   private final File field_192757_e;
   private final Map<Advancement, AdvancementProgress> field_192758_f = Maps.newLinkedHashMap();
   private final Set<Advancement> field_192759_g = Sets.newLinkedHashSet();
   private final Set<Advancement> field_192760_h = Sets.newLinkedHashSet();
   private final Set<Advancement> field_192761_i = Sets.newLinkedHashSet();
   private EntityPlayerMP field_192762_j;
   @Nullable
   private Advancement field_194221_k;
   private boolean field_192763_k = true;

   public PlayerAdvancements(MinecraftServer var1, File var2, EntityPlayerMP var3) {
      super();
      this.field_192756_d = var1;
      this.field_192757_e = var2;
      this.field_192762_j = var3;
      this.func_192740_f();
   }

   public void func_192739_a(EntityPlayerMP var1) {
      this.field_192762_j = var1;
   }

   public void func_192745_a() {
      Iterator var1 = CriteriaTriggers.func_192120_a().iterator();

      while(var1.hasNext()) {
         ICriterionTrigger var2 = (ICriterionTrigger)var1.next();
         var2.func_192167_a(this);
      }

   }

   public void func_193766_b() {
      this.func_192745_a();
      this.field_192758_f.clear();
      this.field_192759_g.clear();
      this.field_192760_h.clear();
      this.field_192761_i.clear();
      this.field_192763_k = true;
      this.field_194221_k = null;
      this.func_192740_f();
   }

   private void func_192751_c() {
      Iterator var1 = this.field_192756_d.func_191949_aK().func_195438_b().iterator();

      while(var1.hasNext()) {
         Advancement var2 = (Advancement)var1.next();
         this.func_193764_b(var2);
      }

   }

   private void func_192752_d() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.field_192758_f.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((AdvancementProgress)var3.getValue()).func_192105_a()) {
            var1.add(var3.getKey());
            this.field_192761_i.add(var3.getKey());
         }
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         Advancement var4 = (Advancement)var2.next();
         this.func_192742_b(var4);
      }

   }

   private void func_192748_e() {
      Iterator var1 = this.field_192756_d.func_191949_aK().func_195438_b().iterator();

      while(var1.hasNext()) {
         Advancement var2 = (Advancement)var1.next();
         if (var2.func_192073_f().isEmpty()) {
            this.func_192750_a(var2, "");
            var2.func_192072_d().func_192113_a(this.field_192762_j);
         }
      }

   }

   private void func_192740_f() {
      if (this.field_192757_e.isFile()) {
         try {
            JsonReader var1 = new JsonReader(new StringReader(Files.toString(this.field_192757_e, StandardCharsets.UTF_8)));
            Throwable var2 = null;

            try {
               var1.setLenient(false);
               Dynamic var3 = new Dynamic(JsonOps.INSTANCE, Streams.parse(var1));
               if (!var3.get("DataVersion").flatMap(Dynamic::getNumberValue).isPresent()) {
                  var3 = var3.set("DataVersion", var3.createInt(1343));
               }

               var3 = this.field_192756_d.func_195563_aC().update(DataFixTypes.ADVANCEMENTS, var3, var3.getInt("DataVersion"), 1631);
               var3 = var3.remove("DataVersion");
               Map var4 = (Map)field_192754_b.getAdapter(field_192755_c).fromJsonTree((JsonElement)var3.getValue());
               if (var4 == null) {
                  throw new JsonParseException("Found null for advancements");
               }

               Stream var5 = var4.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));
               Iterator var6 = ((List)var5.collect(Collectors.toList())).iterator();

               while(var6.hasNext()) {
                  Entry var7 = (Entry)var6.next();
                  Advancement var8 = this.field_192756_d.func_191949_aK().func_192778_a((ResourceLocation)var7.getKey());
                  if (var8 == null) {
                     field_192753_a.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", var7.getKey(), this.field_192757_e);
                  } else {
                     this.func_192743_a(var8, (AdvancementProgress)var7.getValue());
                  }
               }
            } catch (Throwable var18) {
               var2 = var18;
               throw var18;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var17) {
                        var2.addSuppressed(var17);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (JsonParseException var20) {
            field_192753_a.error("Couldn't parse player advancements in {}", this.field_192757_e, var20);
         } catch (IOException var21) {
            field_192753_a.error("Couldn't access player advancements in {}", this.field_192757_e, var21);
         }
      }

      this.func_192748_e();
      this.func_192752_d();
      this.func_192751_c();
   }

   public void func_192749_b() {
      HashMap var1 = Maps.newHashMap();
      Iterator var2 = this.field_192758_f.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         AdvancementProgress var4 = (AdvancementProgress)var3.getValue();
         if (var4.func_192108_b()) {
            var1.put(((Advancement)var3.getKey()).func_192067_g(), var4);
         }
      }

      if (this.field_192757_e.getParentFile() != null) {
         this.field_192757_e.getParentFile().mkdirs();
      }

      try {
         Files.write(field_192754_b.toJson(var1), this.field_192757_e, StandardCharsets.UTF_8);
      } catch (IOException var5) {
         field_192753_a.error("Couldn't save player advancements to {}", this.field_192757_e, var5);
      }

   }

   public boolean func_192750_a(Advancement var1, String var2) {
      boolean var3 = false;
      AdvancementProgress var4 = this.func_192747_a(var1);
      boolean var5 = var4.func_192105_a();
      if (var4.func_192109_a(var2)) {
         this.func_193765_c(var1);
         this.field_192761_i.add(var1);
         var3 = true;
         if (!var5 && var4.func_192105_a()) {
            var1.func_192072_d().func_192113_a(this.field_192762_j);
            if (var1.func_192068_c() != null && var1.func_192068_c().func_193220_i() && this.field_192762_j.field_70170_p.func_82736_K().func_82766_b("announceAdvancements")) {
               this.field_192756_d.func_184103_al().func_148539_a(new TextComponentTranslation("chat.type.advancement." + var1.func_192068_c().func_192291_d().func_192307_a(), new Object[]{this.field_192762_j.func_145748_c_(), var1.func_193123_j()}));
            }
         }
      }

      if (var4.func_192105_a()) {
         this.func_192742_b(var1);
      }

      return var3;
   }

   public boolean func_192744_b(Advancement var1, String var2) {
      boolean var3 = false;
      AdvancementProgress var4 = this.func_192747_a(var1);
      if (var4.func_192101_b(var2)) {
         this.func_193764_b(var1);
         this.field_192761_i.add(var1);
         var3 = true;
      }

      if (!var4.func_192108_b()) {
         this.func_192742_b(var1);
      }

      return var3;
   }

   private void func_193764_b(Advancement var1) {
      AdvancementProgress var2 = this.func_192747_a(var1);
      if (!var2.func_192105_a()) {
         Iterator var3 = var1.func_192073_f().entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            CriterionProgress var5 = var2.func_192106_c((String)var4.getKey());
            if (var5 != null && !var5.func_192151_a()) {
               ICriterionInstance var6 = ((Criterion)var4.getValue()).func_192143_a();
               if (var6 != null) {
                  ICriterionTrigger var7 = CriteriaTriggers.func_192119_a(var6.func_192244_a());
                  if (var7 != null) {
                     var7.func_192165_a(this, new ICriterionTrigger.Listener(var6, var1, (String)var4.getKey()));
                  }
               }
            }
         }

      }
   }

   private void func_193765_c(Advancement var1) {
      AdvancementProgress var2 = this.func_192747_a(var1);
      Iterator var3 = var1.func_192073_f().entrySet().iterator();

      while(true) {
         Entry var4;
         CriterionProgress var5;
         do {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (Entry)var3.next();
               var5 = var2.func_192106_c((String)var4.getKey());
            } while(var5 == null);
         } while(!var5.func_192151_a() && !var2.func_192105_a());

         ICriterionInstance var6 = ((Criterion)var4.getValue()).func_192143_a();
         if (var6 != null) {
            ICriterionTrigger var7 = CriteriaTriggers.func_192119_a(var6.func_192244_a());
            if (var7 != null) {
               var7.func_192164_b(this, new ICriterionTrigger.Listener(var6, var1, (String)var4.getKey()));
            }
         }
      }
   }

   public void func_192741_b(EntityPlayerMP var1) {
      if (this.field_192763_k || !this.field_192760_h.isEmpty() || !this.field_192761_i.isEmpty()) {
         HashMap var2 = Maps.newHashMap();
         LinkedHashSet var3 = Sets.newLinkedHashSet();
         LinkedHashSet var4 = Sets.newLinkedHashSet();
         Iterator var5 = this.field_192761_i.iterator();

         Advancement var6;
         while(var5.hasNext()) {
            var6 = (Advancement)var5.next();
            if (this.field_192759_g.contains(var6)) {
               var2.put(var6.func_192067_g(), this.field_192758_f.get(var6));
            }
         }

         var5 = this.field_192760_h.iterator();

         while(var5.hasNext()) {
            var6 = (Advancement)var5.next();
            if (this.field_192759_g.contains(var6)) {
               var3.add(var6);
            } else {
               var4.add(var6.func_192067_g());
            }
         }

         if (this.field_192763_k || !var2.isEmpty() || !var3.isEmpty() || !var4.isEmpty()) {
            var1.field_71135_a.func_147359_a(new SPacketAdvancementInfo(this.field_192763_k, var3, var4, var2));
            this.field_192760_h.clear();
            this.field_192761_i.clear();
         }
      }

      this.field_192763_k = false;
   }

   public void func_194220_a(@Nullable Advancement var1) {
      Advancement var2 = this.field_194221_k;
      if (var1 != null && var1.func_192070_b() == null && var1.func_192068_c() != null) {
         this.field_194221_k = var1;
      } else {
         this.field_194221_k = null;
      }

      if (var2 != this.field_194221_k) {
         this.field_192762_j.field_71135_a.func_147359_a(new SPacketSelectAdvancementsTab(this.field_194221_k == null ? null : this.field_194221_k.func_192067_g()));
      }

   }

   public AdvancementProgress func_192747_a(Advancement var1) {
      AdvancementProgress var2 = (AdvancementProgress)this.field_192758_f.get(var1);
      if (var2 == null) {
         var2 = new AdvancementProgress();
         this.func_192743_a(var1, var2);
      }

      return var2;
   }

   private void func_192743_a(Advancement var1, AdvancementProgress var2) {
      var2.func_192099_a(var1.func_192073_f(), var1.func_192074_h());
      this.field_192758_f.put(var1, var2);
   }

   private void func_192742_b(Advancement var1) {
      boolean var2 = this.func_192738_c(var1);
      boolean var3 = this.field_192759_g.contains(var1);
      if (var2 && !var3) {
         this.field_192759_g.add(var1);
         this.field_192760_h.add(var1);
         if (this.field_192758_f.containsKey(var1)) {
            this.field_192761_i.add(var1);
         }
      } else if (!var2 && var3) {
         this.field_192759_g.remove(var1);
         this.field_192760_h.add(var1);
      }

      if (var2 != var3 && var1.func_192070_b() != null) {
         this.func_192742_b(var1.func_192070_b());
      }

      Iterator var4 = var1.func_192069_e().iterator();

      while(var4.hasNext()) {
         Advancement var5 = (Advancement)var4.next();
         this.func_192742_b(var5);
      }

   }

   private boolean func_192738_c(Advancement var1) {
      for(int var2 = 0; var1 != null && var2 <= 2; ++var2) {
         if (var2 == 0 && this.func_192746_d(var1)) {
            return true;
         }

         if (var1.func_192068_c() == null) {
            return false;
         }

         AdvancementProgress var3 = this.func_192747_a(var1);
         if (var3.func_192105_a()) {
            return true;
         }

         if (var1.func_192068_c().func_193224_j()) {
            return false;
         }

         var1 = var1.func_192070_b();
      }

      return false;
   }

   private boolean func_192746_d(Advancement var1) {
      AdvancementProgress var2 = this.func_192747_a(var1);
      if (var2.func_192105_a()) {
         return true;
      } else {
         Iterator var3 = var1.func_192069_e().iterator();

         Advancement var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (Advancement)var3.next();
         } while(!this.func_192746_d(var4));

         return true;
      }
   }
}
