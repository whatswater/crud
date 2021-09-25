package com.whatswater.asyncmodule;

import java.util.Comparator;

public class Record {
    public static class ConsumerKey implements Comparator<Record> {
        @Override
        public int compare(Record o1, Record o2) {
            int v2 = o1.getRequireName().compareTo(o2.getRequireName());
            if(v2 != 0) {
                return v2;
            }

            if(o1.getConsumer() == o2.getConsumer()) {
                return 0;
            }
            if(o1.getConsumer() == null) {
                return -1;
            }
            if(o2.getConsumer() == null) {
                return 1;
            }
            return o1.getConsumer().compareTo(o2.getConsumer());
        }
    }

    public static class ProviderKey implements Comparator<Record> {
        @Override
        public int compare(Record o1, Record o2) {
            int v2 = o1.getProvider().compareTo(o2.getProvider());
            if(v2 != 0) {
                return v2;
            }
            return o1.getRequireName().compareTo(o2.getRequireName());
        }
    }

    public static final Comparator<Record> CONSUMER_KEY = new ConsumerKey();
    public static final Comparator<Record> PROVIDER_KEY = new ProviderKey();

    private final ModuleInfo provider;
    private final ModuleInfo consumer;
    private final String requireName;

    public Record(ModuleInfo provider, ModuleInfo consumer, String requireName) {
        this.consumer = consumer;
        this.requireName = requireName;
        this.provider = provider;
    }

    public ModuleInfo getProvider() {
        return provider;
    }

    public ModuleInfo getConsumer() {
        return consumer;
    }

    public String getRequireName() {
        return requireName;
    }

    public static Record createConsumerKey(ModuleInfo consumer, String requireName) {
        return new Record(null, consumer, requireName);
    }

    public static Record createProviderKey(ModuleInfo provider, String requireName) {
        return new Record(provider, null, requireName);
    }

    public static Record createKey(ModuleInfo provider, ModuleInfo consumer, String requireName) {
        return new Record(provider, consumer, requireName);
    }
}
