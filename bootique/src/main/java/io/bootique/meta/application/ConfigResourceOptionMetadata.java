package io.bootique.meta.application;

import io.bootique.resource.ResourceFactory;

/**
 * @since 0.24
 */
public class ConfigResourceOptionMetadata extends OptionMetadata {
    private ResourceFactory configResource;

    protected ConfigResourceOptionMetadata(Builder builder) {
        super(builder);
        this.configResource = builder.configResource;
    }

    public static Builder builder(String name) {
        return new Builder().name(name);
    }

    public static Builder builder(String name, String description) {
        return new Builder().name(name)
                .description(description);
    }

    /**
     * Returns an optional resource associated with this option.
     *
     * @return an optional resource associated with this option.
     */
    public ResourceFactory getConfigResource() {
        return configResource;
    }

    public static class Builder extends OptionMetadata.Builder<Builder> {
        private ResourceFactory configResource;

        public Builder() {

        }

        /**
         * Sets the config resource associated with this option.
         *
         * @param configResourceId a resource path compatible with {@link io.bootique.resource.ResourceFactory} denoting
         *                         a configuration source. E.g. "a/b/my.yml", or "classpath:com/foo/another.yml".
         * @return this builder instance
         */
        public Builder configResource(String configResourceId) {
            this.configResource = new ResourceFactory(configResourceId);
            return this;
        }

        @Override
        public ConfigResourceOptionMetadata build() {
            return new ConfigResourceOptionMetadata(this);
        }

    }
}
