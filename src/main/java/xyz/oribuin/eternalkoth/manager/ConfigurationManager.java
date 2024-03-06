package xyz.oribuin.eternalkoth.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import xyz.oribuin.eternalkoth.EternalKothPlugin;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        BAR_LENGTH("bar-length", 30, "The length of the progress bar in the %eternalkoth_bar% placeholder"),
        BAR_CHAR("bar-char", "|", "The character used in the progress bar in the %eternalkoth_bar% placeholder"),
        REMOVE_INVISIBLE("remove-invisible", true, "If true, Players who are invisible inside the zone will have their invisibility removed."),
        AUTO_START("auto-start", true, "If true, The plugin will automatically create a new KOTH Arena after a period of time"),
        AUTO_START_DELAY("auto-start-delay", "1h", "The delay before the plugin will automatically start a new KOTH Arena"),
        ;

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return EternalKothPlugin.get().getManager(ConfigurationManager.class).getConfig();
        }
    }

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[]{};
    }
}
