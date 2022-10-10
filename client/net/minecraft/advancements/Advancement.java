package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
   private final Advancement field_192076_a;
   private final DisplayInfo field_192077_b;
   private final AdvancementRewards field_192078_c;
   private final ResourceLocation field_192079_d;
   private final Map<String, Criterion> field_192080_e;
   private final String[][] field_192081_f;
   private final Set<Advancement> field_192082_g = Sets.newLinkedHashSet();
   private final ITextComponent field_193125_h;

   public Advancement(ResourceLocation var1, @Nullable Advancement var2, @Nullable DisplayInfo var3, AdvancementRewards var4, Map<String, Criterion> var5, String[][] var6) {
      super();
      this.field_192079_d = var1;
      this.field_192077_b = var3;
      this.field_192080_e = ImmutableMap.copyOf(var5);
      this.field_192076_a = var2;
      this.field_192078_c = var4;
      this.field_192081_f = var6;
      if (var2 != null) {
         var2.func_192071_a(this);
      }

      if (var3 == null) {
         this.field_193125_h = new TextComponentString(var1.toString());
      } else {
         ITextComponent var7 = var3.func_192297_a();
         TextFormatting var8 = var3.func_192291_d().func_193229_c();
         ITextComponent var9 = var7.func_212638_h().func_211708_a(var8).func_150258_a("\n").func_150257_a(var3.func_193222_b());
         ITextComponent var10 = var7.func_212638_h().func_211710_a((var1x) -> {
            var1x.func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var9));
         });
         this.field_193125_h = (new TextComponentString("[")).func_150257_a(var10).func_150258_a("]").func_211708_a(var8);
      }

   }

   public Advancement.Builder func_192075_a() {
      return new Advancement.Builder(this.field_192076_a == null ? null : this.field_192076_a.func_192067_g(), this.field_192077_b, this.field_192078_c, this.field_192080_e, this.field_192081_f);
   }

   @Nullable
   public Advancement func_192070_b() {
      return this.field_192076_a;
   }

   @Nullable
   public DisplayInfo func_192068_c() {
      return this.field_192077_b;
   }

   public AdvancementRewards func_192072_d() {
      return this.field_192078_c;
   }

   public String toString() {
      return "SimpleAdvancement{id=" + this.func_192067_g() + ", parent=" + (this.field_192076_a == null ? "null" : this.field_192076_a.func_192067_g()) + ", display=" + this.field_192077_b + ", rewards=" + this.field_192078_c + ", criteria=" + this.field_192080_e + ", requirements=" + Arrays.deepToString(this.field_192081_f) + '}';
   }

   public Iterable<Advancement> func_192069_e() {
      return this.field_192082_g;
   }

   public Map<String, Criterion> func_192073_f() {
      return this.field_192080_e;
   }

   public int func_193124_g() {
      return this.field_192081_f.length;
   }

   public void func_192071_a(Advancement var1) {
      this.field_192082_g.add(var1);
   }

   public ResourceLocation func_192067_g() {
      return this.field_192079_d;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Advancement)) {
         return false;
      } else {
         Advancement var2 = (Advancement)var1;
         return this.field_192079_d.equals(var2.field_192079_d);
      }
   }

   public int hashCode() {
      return this.field_192079_d.hashCode();
   }

   public String[][] func_192074_h() {
      return this.field_192081_f;
   }

   public ITextComponent func_193123_j() {
      return this.field_193125_h;
   }

   public static class Builder {
      private ResourceLocation field_192061_a;
      private Advancement field_192062_b;
      private DisplayInfo field_192063_c;
      private AdvancementRewards field_192064_d;
      private Map<String, Criterion> field_192065_e;
      private String[][] field_192066_f;
      private RequirementsStrategy field_199751_g;

      private Builder(@Nullable ResourceLocation var1, @Nullable DisplayInfo var2, AdvancementRewards var3, Map<String, Criterion> var4, String[][] var5) {
         super();
         this.field_192064_d = AdvancementRewards.field_192114_a;
         this.field_192065_e = Maps.newLinkedHashMap();
         this.field_199751_g = RequirementsStrategy.AND;
         this.field_192061_a = var1;
         this.field_192063_c = var2;
         this.field_192064_d = var3;
         this.field_192065_e = var4;
         this.field_192066_f = var5;
      }

      private Builder() {
         super();
         this.field_192064_d = AdvancementRewards.field_192114_a;
         this.field_192065_e = Maps.newLinkedHashMap();
         this.field_199751_g = RequirementsStrategy.AND;
      }

      public static Advancement.Builder func_200278_a() {
         return new Advancement.Builder();
      }

      public Advancement.Builder func_203905_a(Advancement var1) {
         this.field_192062_b = var1;
         return this;
      }

      public Advancement.Builder func_200272_a(ResourceLocation var1) {
         this.field_192061_a = var1;
         return this;
      }

      public Advancement.Builder func_203902_a(IItemProvider var1, ITextComponent var2, ITextComponent var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8) {
         return this.func_203903_a(new DisplayInfo(new ItemStack(var1.func_199767_j()), var2, var3, var4, var5, var6, var7, var8));
      }

      public Advancement.Builder func_203903_a(DisplayInfo var1) {
         this.field_192063_c = var1;
         return this;
      }

      public Advancement.Builder func_200271_a(AdvancementRewards.Builder var1) {
         return this.func_200274_a(var1.func_200281_a());
      }

      public Advancement.Builder func_200274_a(AdvancementRewards var1) {
         this.field_192064_d = var1;
         return this;
      }

      public Advancement.Builder func_200275_a(String var1, ICriterionInstance var2) {
         return this.func_200276_a(var1, new Criterion(var2));
      }

      public Advancement.Builder func_200276_a(String var1, Criterion var2) {
         if (this.field_192065_e.containsKey(var1)) {
            throw new IllegalArgumentException("Duplicate criterion " + var1);
         } else {
            this.field_192065_e.put(var1, var2);
            return this;
         }
      }

      public Advancement.Builder func_200270_a(RequirementsStrategy var1) {
         this.field_199751_g = var1;
         return this;
      }

      public boolean func_192058_a(Function<ResourceLocation, Advancement> var1) {
         if (this.field_192061_a == null) {
            return true;
         } else {
            if (this.field_192062_b == null) {
               this.field_192062_b = (Advancement)var1.apply(this.field_192061_a);
            }

            return this.field_192062_b != null;
         }
      }

      public Advancement func_192056_a(ResourceLocation var1) {
         if (!this.func_192058_a((var0) -> {
            return null;
         })) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
         } else {
            if (this.field_192066_f == null) {
               this.field_192066_f = this.field_199751_g.createRequirements(this.field_192065_e.keySet());
            }

            return new Advancement(var1, this.field_192062_b, this.field_192063_c, this.field_192064_d, this.field_192065_e, this.field_192066_f);
         }
      }

      public Advancement func_203904_a(Consumer<Advancement> var1, String var2) {
         Advancement var3 = this.func_192056_a(new ResourceLocation(var2));
         var1.accept(var3);
         return var3;
      }

      public JsonObject func_200273_b() {
         if (this.field_192066_f == null) {
            this.field_192066_f = this.field_199751_g.createRequirements(this.field_192065_e.keySet());
         }

         JsonObject var1 = new JsonObject();
         if (this.field_192062_b != null) {
            var1.addProperty("parent", this.field_192062_b.func_192067_g().toString());
         } else if (this.field_192061_a != null) {
            var1.addProperty("parent", this.field_192061_a.toString());
         }

         if (this.field_192063_c != null) {
            var1.add("display", this.field_192063_c.func_200290_k());
         }

         var1.add("rewards", this.field_192064_d.func_200286_b());
         JsonObject var2 = new JsonObject();
         Iterator var3 = this.field_192065_e.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var2.add((String)var4.getKey(), ((Criterion)var4.getValue()).func_200287_b());
         }

         var1.add("criteria", var2);
         JsonArray var13 = new JsonArray();
         String[][] var14 = this.field_192066_f;
         int var5 = var14.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String[] var7 = var14[var6];
            JsonArray var8 = new JsonArray();
            String[] var9 = var7;
            int var10 = var7.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               String var12 = var9[var11];
               var8.add(var12);
            }

            var13.add(var8);
         }

         var1.add("requirements", var13);
         return var1;
      }

      public void func_192057_a(PacketBuffer var1) {
         if (this.field_192061_a == null) {
            var1.writeBoolean(false);
         } else {
            var1.writeBoolean(true);
            var1.func_192572_a(this.field_192061_a);
         }

         if (this.field_192063_c == null) {
            var1.writeBoolean(false);
         } else {
            var1.writeBoolean(true);
            this.field_192063_c.func_192290_a(var1);
         }

         Criterion.func_192141_a(this.field_192065_e, var1);
         var1.func_150787_b(this.field_192066_f.length);
         String[][] var2 = this.field_192066_f;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String[] var5 = var2[var4];
            var1.func_150787_b(var5.length);
            String[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               var1.func_180714_a(var9);
            }
         }

      }

      public String toString() {
         return "Task Advancement{parentId=" + this.field_192061_a + ", display=" + this.field_192063_c + ", rewards=" + this.field_192064_d + ", criteria=" + this.field_192065_e + ", requirements=" + Arrays.deepToString(this.field_192066_f) + '}';
      }

      public static Advancement.Builder func_192059_a(JsonObject var0, JsonDeserializationContext var1) {
         ResourceLocation var2 = var0.has("parent") ? new ResourceLocation(JsonUtils.func_151200_h(var0, "parent")) : null;
         DisplayInfo var3 = var0.has("display") ? DisplayInfo.func_192294_a(JsonUtils.func_152754_s(var0, "display"), var1) : null;
         AdvancementRewards var4 = (AdvancementRewards)JsonUtils.func_188177_a(var0, "rewards", AdvancementRewards.field_192114_a, var1, AdvancementRewards.class);
         Map var5 = Criterion.func_192144_b(JsonUtils.func_152754_s(var0, "criteria"), var1);
         if (var5.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
         } else {
            JsonArray var6 = JsonUtils.func_151213_a(var0, "requirements", new JsonArray());
            String[][] var7 = new String[var6.size()][];

            int var8;
            int var10;
            for(var8 = 0; var8 < var6.size(); ++var8) {
               JsonArray var9 = JsonUtils.func_151207_m(var6.get(var8), "requirements[" + var8 + "]");
               var7[var8] = new String[var9.size()];

               for(var10 = 0; var10 < var9.size(); ++var10) {
                  var7[var8][var10] = JsonUtils.func_151206_a(var9.get(var10), "requirements[" + var8 + "][" + var10 + "]");
               }
            }

            if (var7.length == 0) {
               var7 = new String[var5.size()][];
               var8 = 0;

               String var21;
               for(Iterator var16 = var5.keySet().iterator(); var16.hasNext(); var7[var8++] = new String[]{var21}) {
                  var21 = (String)var16.next();
               }
            }

            String[][] var17 = var7;
            int var18 = var7.length;

            int var13;
            for(var10 = 0; var10 < var18; ++var10) {
               String[] var11 = var17[var10];
               if (var11.length == 0 && var5.isEmpty()) {
                  throw new JsonSyntaxException("Requirement entry cannot be empty");
               }

               String[] var12 = var11;
               var13 = var11.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  String var15 = var12[var14];
                  if (!var5.containsKey(var15)) {
                     throw new JsonSyntaxException("Unknown required criterion '" + var15 + "'");
                  }
               }
            }

            Iterator var19 = var5.keySet().iterator();

            String var20;
            boolean var23;
            do {
               if (!var19.hasNext()) {
                  return new Advancement.Builder(var2, var3, var4, var5, var7);
               }

               var20 = (String)var19.next();
               var23 = false;
               String[][] var22 = var7;
               int var24 = var7.length;

               for(var13 = 0; var13 < var24; ++var13) {
                  String[] var25 = var22[var13];
                  if (ArrayUtils.contains(var25, var20)) {
                     var23 = true;
                     break;
                  }
               }
            } while(var23);

            throw new JsonSyntaxException("Criterion '" + var20 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
         }
      }

      public static Advancement.Builder func_192060_b(PacketBuffer var0) {
         ResourceLocation var1 = var0.readBoolean() ? var0.func_192575_l() : null;
         DisplayInfo var2 = var0.readBoolean() ? DisplayInfo.func_192295_b(var0) : null;
         Map var3 = Criterion.func_192142_c(var0);
         String[][] var4 = new String[var0.func_150792_a()][];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = new String[var0.func_150792_a()];

            for(int var6 = 0; var6 < var4[var5].length; ++var6) {
               var4[var5][var6] = var0.func_150789_c(32767);
            }
         }

         return new Advancement.Builder(var1, var2, AdvancementRewards.field_192114_a, var3, var4);
      }

      public Map<String, Criterion> func_200277_c() {
         return this.field_192065_e;
      }

      // $FF: synthetic method
      Builder(ResourceLocation var1, DisplayInfo var2, AdvancementRewards var3, Map var4, String[][] var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }
}
