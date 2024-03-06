package xyz.oribuin.eternalkoth;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.eternalkoth.manager.CommandManager;
import xyz.oribuin.eternalkoth.manager.ConfigurationManager;
import xyz.oribuin.eternalkoth.manager.KothManager;
import xyz.oribuin.eternalkoth.manager.LocaleManager;

import java.util.List;

public class EternalKothPlugin extends RosePlugin {

    private static EternalKothPlugin instance;

    public static EternalKothPlugin get() {
        return instance;
    }

    public EternalKothPlugin() {
        super(-1, -1,
                ConfigurationManager.class,
                null,
                LocaleManager.class,
                CommandManager.class
        );

        instance = this;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(KothManager.class);
    }

}
