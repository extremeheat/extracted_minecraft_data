package io.netty.handler.codec.dns;

import io.netty.util.internal.ObjectUtil;

public class DefaultDnsResponse extends AbstractDnsMessage implements DnsResponse {
   private boolean authoritativeAnswer;
   private boolean truncated;
   private boolean recursionAvailable;
   private DnsResponseCode code;

   public DefaultDnsResponse(int var1) {
      this(var1, DnsOpCode.QUERY, DnsResponseCode.NOERROR);
   }

   public DefaultDnsResponse(int var1, DnsOpCode var2) {
      this(var1, var2, DnsResponseCode.NOERROR);
   }

   public DefaultDnsResponse(int var1, DnsOpCode var2, DnsResponseCode var3) {
      super(var1, var2);
      this.setCode(var3);
   }

   public boolean isAuthoritativeAnswer() {
      return this.authoritativeAnswer;
   }

   public DnsResponse setAuthoritativeAnswer(boolean var1) {
      this.authoritativeAnswer = var1;
      return this;
   }

   public boolean isTruncated() {
      return this.truncated;
   }

   public DnsResponse setTruncated(boolean var1) {
      this.truncated = var1;
      return this;
   }

   public boolean isRecursionAvailable() {
      return this.recursionAvailable;
   }

   public DnsResponse setRecursionAvailable(boolean var1) {
      this.recursionAvailable = var1;
      return this;
   }

   public DnsResponseCode code() {
      return this.code;
   }

   public DnsResponse setCode(DnsResponseCode var1) {
      this.code = (DnsResponseCode)ObjectUtil.checkNotNull(var1, "code");
      return this;
   }

   public DnsResponse setId(int var1) {
      return (DnsResponse)super.setId(var1);
   }

   public DnsResponse setOpCode(DnsOpCode var1) {
      return (DnsResponse)super.setOpCode(var1);
   }

   public DnsResponse setRecursionDesired(boolean var1) {
      return (DnsResponse)super.setRecursionDesired(var1);
   }

   public DnsResponse setZ(int var1) {
      return (DnsResponse)super.setZ(var1);
   }

   public DnsResponse setRecord(DnsSection var1, DnsRecord var2) {
      return (DnsResponse)super.setRecord(var1, var2);
   }

   public DnsResponse addRecord(DnsSection var1, DnsRecord var2) {
      return (DnsResponse)super.addRecord(var1, var2);
   }

   public DnsResponse addRecord(DnsSection var1, int var2, DnsRecord var3) {
      return (DnsResponse)super.addRecord(var1, var2, var3);
   }

   public DnsResponse clear(DnsSection var1) {
      return (DnsResponse)super.clear(var1);
   }

   public DnsResponse clear() {
      return (DnsResponse)super.clear();
   }

   public DnsResponse touch() {
      return (DnsResponse)super.touch();
   }

   public DnsResponse touch(Object var1) {
      return (DnsResponse)super.touch(var1);
   }

   public DnsResponse retain() {
      return (DnsResponse)super.retain();
   }

   public DnsResponse retain(int var1) {
      return (DnsResponse)super.retain(var1);
   }

   public String toString() {
      return DnsMessageUtil.appendResponse(new StringBuilder(128), this).toString();
   }
}
