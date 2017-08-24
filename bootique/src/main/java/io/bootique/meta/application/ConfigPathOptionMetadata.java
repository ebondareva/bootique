package io.bootique.meta.application;


import java.util.Objects;

/**
 * @since 0.24
 */
public class ConfigPathOptionMetadata extends OptionMetadata {

    private String configPath;
    private String defaultValue;

    protected ConfigPathOptionMetadata(Builder builder) {
        super(builder);
        this.configPath = builder.configPath;
        this.defaultValue = builder.defaultValue;
    }

    public static Builder builder(String name) {
        return new Builder().name(name);
    }

    public static Builder builder(String name, String description) {
        return new Builder().name(name)
                .description(description);
    }

    /**
     * Returns an optional configuration path associated with this option.
     *
     * @return null or a dot-separated "path" that navigates configuration tree to the property associated with this
     * option. E.g. "jdbc.myds.password".
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * Returns the default value for this option. I.e. the value that will be used if the option is provided on
     * command line without an explicit value.
     *
     * @return the default value for this option.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public static class Builder extends OptionMetadata.Builder<Builder> {

        private String configPath;
        private String defaultValue;

        public Builder() {

        }

        /**
         * Sets the configuration property path that should be associated to this option value.
         *
         * @param configPath a dot-separated "path" that navigates configuration tree to the desired property. E.g.
         *                   "jdbc.myds.password".
         * @return this builder instance
         */
        public Builder configPath(String configPath) {
            this.configPath = Objects.requireNonNull(configPath);
            return this;
        }

        /**
         * Sets the default value for this option.
         *
         * @param defaultValue a default value for the option.
         * @return this builder instance
         */
        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public ConfigPathOptionMetadata build() {
            Objects.requireNonNull(this.configPath);

            return new ConfigPathOptionMetadata(this);
        }
    }
}
