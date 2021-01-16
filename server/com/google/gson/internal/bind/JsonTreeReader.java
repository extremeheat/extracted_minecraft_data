package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map.Entry;

public final class JsonTreeReader extends JsonReader {
   private static final Reader UNREADABLE_READER = new Reader() {
      public int read(char[] var1, int var2, int var3) throws IOException {
         throw new AssertionError();
      }

      public void close() throws IOException {
         throw new AssertionError();
      }
   };
   private static final Object SENTINEL_CLOSED = new Object();
   private Object[] stack = new Object[32];
   private int stackSize = 0;
   private String[] pathNames = new String[32];
   private int[] pathIndices = new int[32];

   public JsonTreeReader(JsonElement var1) {
      super(UNREADABLE_READER);
      this.push(var1);
   }

   public void beginArray() throws IOException {
      this.expect(JsonToken.BEGIN_ARRAY);
      JsonArray var1 = (JsonArray)this.peekStack();
      this.push(var1.iterator());
      this.pathIndices[this.stackSize - 1] = 0;
   }

   public void endArray() throws IOException {
      this.expect(JsonToken.END_ARRAY);
      this.popStack();
      this.popStack();
      if (this.stackSize > 0) {
         int var10002 = this.pathIndices[this.stackSize - 1]++;
      }

   }

   public void beginObject() throws IOException {
      this.expect(JsonToken.BEGIN_OBJECT);
      JsonObject var1 = (JsonObject)this.peekStack();
      this.push(var1.entrySet().iterator());
   }

   public void endObject() throws IOException {
      this.expect(JsonToken.END_OBJECT);
      this.popStack();
      this.popStack();
      if (this.stackSize > 0) {
         int var10002 = this.pathIndices[this.stackSize - 1]++;
      }

   }

   public boolean hasNext() throws IOException {
      JsonToken var1 = this.peek();
      return var1 != JsonToken.END_OBJECT && var1 != JsonToken.END_ARRAY;
   }

   public JsonToken peek() throws IOException {
      if (this.stackSize == 0) {
         return JsonToken.END_DOCUMENT;
      } else {
         Object var1 = this.peekStack();
         if (var1 instanceof Iterator) {
            boolean var4 = this.stack[this.stackSize - 2] instanceof JsonObject;
            Iterator var3 = (Iterator)var1;
            if (var3.hasNext()) {
               if (var4) {
                  return JsonToken.NAME;
               } else {
                  this.push(var3.next());
                  return this.peek();
               }
            } else {
               return var4 ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
            }
         } else if (var1 instanceof JsonObject) {
            return JsonToken.BEGIN_OBJECT;
         } else if (var1 instanceof JsonArray) {
            return JsonToken.BEGIN_ARRAY;
         } else if (var1 instanceof JsonPrimitive) {
            JsonPrimitive var2 = (JsonPrimitive)var1;
            if (var2.isString()) {
               return JsonToken.STRING;
            } else if (var2.isBoolean()) {
               return JsonToken.BOOLEAN;
            } else if (var2.isNumber()) {
               return JsonToken.NUMBER;
            } else {
               throw new AssertionError();
            }
         } else if (var1 instanceof JsonNull) {
            return JsonToken.NULL;
         } else if (var1 == SENTINEL_CLOSED) {
            throw new IllegalStateException("JsonReader is closed");
         } else {
            throw new AssertionError();
         }
      }
   }

   private Object peekStack() {
      return this.stack[this.stackSize - 1];
   }

   private Object popStack() {
      Object var1 = this.stack[--this.stackSize];
      this.stack[this.stackSize] = null;
      return var1;
   }

   private void expect(JsonToken var1) throws IOException {
      if (this.peek() != var1) {
         throw new IllegalStateException("Expected " + var1 + " but was " + this.peek() + this.locationString());
      }
   }

   public String nextName() throws IOException {
      this.expect(JsonToken.NAME);
      Iterator var1 = (Iterator)this.peekStack();
      Entry var2 = (Entry)var1.next();
      String var3 = (String)var2.getKey();
      this.pathNames[this.stackSize - 1] = var3;
      this.push(var2.getValue());
      return var3;
   }

   public String nextString() throws IOException {
      JsonToken var1 = this.peek();
      if (var1 != JsonToken.STRING && var1 != JsonToken.NUMBER) {
         throw new IllegalStateException("Expected " + JsonToken.STRING + " but was " + var1 + this.locationString());
      } else {
         String var2 = ((JsonPrimitive)this.popStack()).getAsString();
         if (this.stackSize > 0) {
            int var10002 = this.pathIndices[this.stackSize - 1]++;
         }

         return var2;
      }
   }

   public boolean nextBoolean() throws IOException {
      this.expect(JsonToken.BOOLEAN);
      boolean var1 = ((JsonPrimitive)this.popStack()).getAsBoolean();
      if (this.stackSize > 0) {
         int var10002 = this.pathIndices[this.stackSize - 1]++;
      }

      return var1;
   }

   public void nextNull() throws IOException {
      this.expect(JsonToken.NULL);
      this.popStack();
      if (this.stackSize > 0) {
         int var10002 = this.pathIndices[this.stackSize - 1]++;
      }

   }

   public double nextDouble() throws IOException {
      JsonToken var1 = this.peek();
      if (var1 != JsonToken.NUMBER && var1 != JsonToken.STRING) {
         throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + var1 + this.locationString());
      } else {
         double var2 = ((JsonPrimitive)this.peekStack()).getAsDouble();
         if (this.isLenient() || !Double.isNaN(var2) && !Double.isInfinite(var2)) {
            this.popStack();
            if (this.stackSize > 0) {
               int var10002 = this.pathIndices[this.stackSize - 1]++;
            }

            return var2;
         } else {
            throw new NumberFormatException("JSON forbids NaN and infinities: " + var2);
         }
      }
   }

   public long nextLong() throws IOException {
      JsonToken var1 = this.peek();
      if (var1 != JsonToken.NUMBER && var1 != JsonToken.STRING) {
         throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + var1 + this.locationString());
      } else {
         long var2 = ((JsonPrimitive)this.peekStack()).getAsLong();
         this.popStack();
         if (this.stackSize > 0) {
            int var10002 = this.pathIndices[this.stackSize - 1]++;
         }

         return var2;
      }
   }

   public int nextInt() throws IOException {
      JsonToken var1 = this.peek();
      if (var1 != JsonToken.NUMBER && var1 != JsonToken.STRING) {
         throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + var1 + this.locationString());
      } else {
         int var2 = ((JsonPrimitive)this.peekStack()).getAsInt();
         this.popStack();
         if (this.stackSize > 0) {
            int var10002 = this.pathIndices[this.stackSize - 1]++;
         }

         return var2;
      }
   }

   public void close() throws IOException {
      this.stack = new Object[]{SENTINEL_CLOSED};
      this.stackSize = 1;
   }

   public void skipValue() throws IOException {
      if (this.peek() == JsonToken.NAME) {
         this.nextName();
         this.pathNames[this.stackSize - 2] = "null";
      } else {
         this.popStack();
         this.pathNames[this.stackSize - 1] = "null";
      }

      int var10002 = this.pathIndices[this.stackSize - 1]++;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   public void promoteNameToValue() throws IOException {
      this.expect(JsonToken.NAME);
      Iterator var1 = (Iterator)this.peekStack();
      Entry var2 = (Entry)var1.next();
      this.push(var2.getValue());
      this.push(new JsonPrimitive((String)var2.getKey()));
   }

   private void push(Object var1) {
      if (this.stackSize == this.stack.length) {
         Object[] var2 = new Object[this.stackSize * 2];
         int[] var3 = new int[this.stackSize * 2];
         String[] var4 = new String[this.stackSize * 2];
         System.arraycopy(this.stack, 0, var2, 0, this.stackSize);
         System.arraycopy(this.pathIndices, 0, var3, 0, this.stackSize);
         System.arraycopy(this.pathNames, 0, var4, 0, this.stackSize);
         this.stack = var2;
         this.pathIndices = var3;
         this.pathNames = var4;
      }

      this.stack[this.stackSize++] = var1;
   }

   public String getPath() {
      StringBuilder var1 = (new StringBuilder()).append('$');

      for(int var2 = 0; var2 < this.stackSize; ++var2) {
         if (this.stack[var2] instanceof JsonArray) {
            ++var2;
            if (this.stack[var2] instanceof Iterator) {
               var1.append('[').append(this.pathIndices[var2]).append(']');
            }
         } else if (this.stack[var2] instanceof JsonObject) {
            ++var2;
            if (this.stack[var2] instanceof Iterator) {
               var1.append('.');
               if (this.pathNames[var2] != null) {
                  var1.append(this.pathNames[var2]);
               }
            }
         }
      }

      return var1.toString();
   }

   private String locationString() {
      return " at path " + this.getPath();
   }
}
