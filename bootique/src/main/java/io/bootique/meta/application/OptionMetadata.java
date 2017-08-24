package io.bootique.meta.application;

import io.bootique.meta.MetadataNode;

/**
 * A descriptor of a command-line option.
 *
 * @since 0.20
 */
public class OptionMetadata implements MetadataNode {

    private String name;
    private String description;
    private String shortName;
    private OptionValueCardinality valueCardinality;
    private String valueName;

    protected OptionMetadata(Builder<?> builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.shortName = builder.shortName;
        this.valueCardinality = builder.valueCardinality;
        this.valueName = builder.valueName;
    }

    public static Builder builder(String name) {
        return new Builder().name(name);
    }

    public static Builder builder(String name, String description) {
        return new Builder().name(name)
                .description(description);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @return option short name.
     * @since 0.21
     */
    public String getShortName() {
        return (shortName != null) ? shortName : name.substring(0, 1);
    }

    public OptionValueCardinality getValueCardinality() {
        return valueCardinality;
    }

    public String getValueName() {
        return valueName;
    }

    public static class Builder<T extends Builder<T>> {

        private String name;
        private String description;
        private String shortName;
        private OptionValueCardinality valueCardinality;
        private String valueName;

        public Builder() {
            this.valueCardinality = OptionValueCardinality.NONE;
        }

        public T name(String name) {
            this.name = validateName(name);
            return (T) this;
        }

        public T shortName(String shortName) {
            this.shortName = validateShortName(shortName);
            return (T) this;
        }

        public T shortName(char shortName) {
            this.shortName = String.valueOf(shortName);
            return (T) this;
        }

        public T description(String description) {
            this.description = description;
            return (T) this;
        }

        public T valueRequired() {
            return valueRequired("");
        }

        public T valueRequired(String valueName) {
            this.valueCardinality = OptionValueCardinality.REQUIRED;
            this.valueName = valueName;
            return (T) this;
        }

        public T valueOptional() {
            return (T) valueOptional("");
        }

        public T valueOptional(String valueName) {
            this.valueCardinality = OptionValueCardinality.OPTIONAL;
            this.valueName = valueName;
            return (T) this;
        }

        public OptionMetadata build() {
            validateName(this.name);

            return new OptionMetadata(this);
        }

        private String validateName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Null 'name'");
            }

            if (name.length() == 0) {
                throw new IllegalArgumentException("Empty 'name'");
            }

            return name;
        }

        private String validateShortName(String shortName) {
            if (shortName == null) {
                throw new IllegalArgumentException("Null 'shortName'");
            }

            if (shortName.length() != 1) {
                throw new IllegalArgumentException("'shortName' must be exactly one char long: " + shortName);
            }

            return shortName;
        }
    }

}
